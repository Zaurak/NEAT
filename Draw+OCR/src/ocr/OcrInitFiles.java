package ocr;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.xeustechnologies.jtar.TarEntry;
import org.xeustechnologies.jtar.TarInputStream;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

public class OcrInitFiles extends AsyncTask<String, String, Boolean> {
	private static final String TAG = "OcrInitFiles.java";

	private TessBaseAPI baseApi;
	private ProgressDialog dialog;
	private ProgressDialog indeterminateDialog;

	public OcrInitFiles(TessBaseAPI baseApi, ProgressDialog dialog, ProgressDialog indeterminateDialog) {
		this.baseApi = baseApi;
		this.dialog = dialog;
		this.indeterminateDialog = indeterminateDialog;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog.setTitle("Please wait");
		dialog.setMessage("Checking for data installation...");
		dialog.setIndeterminate(false);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setCancelable(false);
		dialog.show();
	}

	@Override
	protected Boolean doInBackground(String... params) {
		boolean installSuccess;
		String[] paths = new String[] { Ocr.DATA_PATH,
				Ocr.DATA_PATH + "tessdata/" };

		/***** Check for NEAT and NEAT/tessdata directories *****/
		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.v(TAG, "ERROR: Creation of directory " + path
							+ " on sdcard failed");
					return false;
				} else {
					Log.v(TAG, "Created directory " + path + " on sdcard");
				}
			}
		}
		/***** Check for traineddata file *****/
		installSuccess = checkTrainedData();

		dialog.dismiss();

		if (baseApi.init(Ocr.DATA_PATH, Ocr.lang)) {
			return installSuccess;
		}
		return false;
	}

	private boolean checkTrainedData() {
		boolean installSuccess = false;
		String destinationFilenameBase = "tesseract-ocr-3.02." + Ocr.lang
				+ ".tar";
		File downloadFile = new File(Ocr.DATA_PATH + "tessdata/" + Ocr.lang
				+ ".traineddata");
		if (!downloadFile.exists()) {
			Log.d(TAG, "Downloading " + destinationFilenameBase);
			try {
				installSuccess = downloadFile(destinationFilenameBase,
						new File(Ocr.DATA_PATH + "tessdata/"
								+ destinationFilenameBase));
				if (!installSuccess) {
					Log.e(TAG, "Download failed");
					return false;
				}
			} catch (IOException e) {
				Log.e(TAG,
						"IOException received in doInBackground. Is a network connection available?");
				return false;
			}

			// If we have a tar file at this point because we downloaded v3.02+
			// data, untar it
			String extension = destinationFilenameBase
					.substring(destinationFilenameBase.lastIndexOf('.'));
			if (extension.equals(".tar")) {
				try {
					File tessdataDir = new File(Ocr.DATA_PATH + "tessdata");
					untar(new File(Ocr.DATA_PATH + "tessdata" + File.separator
							+ destinationFilenameBase), tessdataDir);
					installSuccess = true;
				} catch (IOException e) {
					Log.e(TAG, "Untar failed");
					return false;
				}
			}
		}
		return installSuccess;
	}

	/**
	 * Download a file from the site specified by DOWNLOAD_BASE, and gunzip to
	 * the given destination.
	 * 
	 * @param sourceFilenameBase
	 *            Name of file to download, minus the required ".gz" extension
	 * @param destinationFile
	 *            Name of file to save the unzipped data to, including path
	 * @return True if download and unzip are successful
	 * @throws IOException
	 */
	private boolean downloadFile(String sourceFilenameBase, File destinationFile)
			throws IOException {
		try {
			return downloadGzippedFileHttp(new URL(Ocr.DOWNLOAD_BASE
					+ sourceFilenameBase + ".gz"), destinationFile);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Bad URL string.");
		}
	}

	/**
	 * Download a gzipped file using an HttpURLConnection, and gunzip it to the
	 * given destination.
	 * 
	 * @param url
	 *            URL to download from
	 * @param destinationFile
	 *            File to save the download as, including path
	 * @return True if response received, destinationFile opened, and unzip
	 *         successful
	 * @throws IOException
	 */
	private boolean downloadGzippedFileHttp(URL url, File destinationFile)
			throws IOException {
		// Send an HTTP GET request for the file
		Log.d(TAG, "Sending GET request to " + url + "...");
		publishProgress("Downloading data for " + Ocr.lang + "...", "0");
		HttpURLConnection urlConnection = null;
		urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setAllowUserInteraction(false);
		urlConnection.setInstanceFollowRedirects(true);
		urlConnection.setRequestMethod("GET");
		urlConnection.connect();
		if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			Log.e(TAG, "Did not get HTTP_OK response.");
			Log.e(TAG, "Response code: " + urlConnection.getResponseCode());
			Log.e(TAG, "Response message: "
					+ urlConnection.getResponseMessage().toString());
			return false;
		}
		int fileSize = urlConnection.getContentLength();
		InputStream inputStream = urlConnection.getInputStream();
		File tempFile = new File(destinationFile.toString() + ".gz.download");

		// Stream the file contents to a local file temporarily
		Log.d(TAG, "Streaming download to " + destinationFile.toString()
				+ ".gz.download...");
		final int BUFFER = 8192;
		FileOutputStream fileOutputStream = null;
		Integer percentComplete;
		int percentCompleteLast = 0;
		try {
			fileOutputStream = new FileOutputStream(tempFile);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "Exception received when opening FileOutputStream.", e);
		}
		int downloaded = 0;
		byte[] buffer = new byte[BUFFER];
		int bufferLength = 0;
		while ((bufferLength = inputStream.read(buffer, 0, BUFFER)) > 0) {
			fileOutputStream.write(buffer, 0, bufferLength);
			downloaded += bufferLength;
			percentComplete = (int) ((downloaded / (float) fileSize) * 100);
			if (percentComplete > percentCompleteLast) {
				publishProgress("Downloading data for " + Ocr.lang + "...",
						percentComplete.toString());
				percentCompleteLast = percentComplete;
			}
		}
		fileOutputStream.close();
		if (urlConnection != null) {
			urlConnection.disconnect();
		}
		// Uncompress the downloaded temporary file into place, and remove the
		// temporary file
		try {
			Log.d(TAG, "Unzipping..." + tempFile.toString());
			gunzip(tempFile,
					new File(tempFile.toString().replace(".gz.download", "")));
			return true;
		} catch (FileNotFoundException e) {
			Log.e(TAG, "File not available for unzipping.");
		} catch (IOException e) {
			Log.e(TAG, "Problem unzipping file.");
		}
		return false;
	}

	/**
	 * Unzips the given Gzipped file to the given destination, and deletes the
	 * gzipped file.
	 * 
	 * @param zippedFile
	 *            The gzipped file to be uncompressed
	 * @param outFilePath
	 *            File to unzip to, including path
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void gunzip(File zippedFile, File outFilePath)
			throws FileNotFoundException, IOException {
		int uncompressedFileSize = getGzipSizeUncompressed(zippedFile);
		Integer percentComplete;
		int percentCompleteLast = 0;
		int unzippedBytes = 0;
		final Integer progressMin = 0;
		int progressMax = 100 - progressMin;
		publishProgress("Uncompressing data for " + Ocr.lang + "...",
				progressMin.toString());

		// If the file is a tar file, just show progress to 50%
		String extension = zippedFile.toString().substring(
				zippedFile.toString().length() - 16);
		if (extension.equals(".tar.gz.download")) {
			progressMax = 50;
		}
		GZIPInputStream gzipInputStream = new GZIPInputStream(
				new BufferedInputStream(new FileInputStream(zippedFile)));
		OutputStream outputStream = new FileOutputStream(outFilePath);
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
				outputStream);

		final int BUFFER = 8192;
		byte[] data = new byte[BUFFER];
		int len;
		while ((len = gzipInputStream.read(data, 0, BUFFER)) > 0) {
			bufferedOutputStream.write(data, 0, len);
			unzippedBytes += len;
			percentComplete = (int) ((unzippedBytes / (float) uncompressedFileSize) * progressMax)
					+ progressMin;

			if (percentComplete > percentCompleteLast) {
				publishProgress("Uncompressing data for " + Ocr.lang + "...",
						percentComplete.toString());
				percentCompleteLast = percentComplete;
			}
		}
		gzipInputStream.close();
		bufferedOutputStream.flush();
		bufferedOutputStream.close();

		if (zippedFile.exists()) {
			zippedFile.delete();
		}
	}

	/**
	 * Returns the uncompressed size for a Gzipped file.
	 * 
	 * @param file
	 *            Gzipped file to get the size for
	 * @return Size when uncompressed, in bytes
	 * @throws IOException
	 */
	private int getGzipSizeUncompressed(File zipFile) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(zipFile, "r");
		raf.seek(raf.length() - 4);
		int b4 = raf.read();
		int b3 = raf.read();
		int b2 = raf.read();
		int b1 = raf.read();
		raf.close();
		return (b1 << 24) | (b2 << 16) + (b3 << 8) + b4;
	}

	/**
	 * Untar the contents of a tar file into the given directory, ignoring the
	 * relative pathname in the tar file, and delete the tar file.
	 * 
	 * Uses jtar: http://code.google.com/p/jtar/
	 * 
	 * @param tarFile
	 *            The tar file to be untarred
	 * @param destinationDir
	 *            The directory to untar into
	 * @throws IOException
	 */
	private void untar(File tarFile, File destinationDir) throws IOException {
		final int uncompressedSize = getTarSizeUncompressed(tarFile);
		Integer percentComplete;
		int percentCompleteLast = 0;
		int unzippedBytes = 0;
		final Integer progressMin = 50;
		final int progressMax = 100 - progressMin;
		publishProgress("Uncompressing data for " + Ocr.lang + "...",
				progressMin.toString());

		// Extract all the files
		TarInputStream tarInputStream = new TarInputStream(
				new BufferedInputStream(new FileInputStream(tarFile)));
		TarEntry entry;
		while ((entry = tarInputStream.getNextEntry()) != null) {
			int len;
			final int BUFFER = 8192;
			byte data[] = new byte[BUFFER];
			String pathName = entry.getName();
			String fileName = pathName.substring(pathName.lastIndexOf('/'),
					pathName.length());
			OutputStream outputStream = new FileOutputStream(destinationDir
					+ fileName);
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
					outputStream);

			Log.d(TAG, "Writing " + fileName.substring(1, fileName.length())
					+ "...");
			while ((len = tarInputStream.read(data, 0, BUFFER)) != -1) {
				bufferedOutputStream.write(data, 0, len);
				unzippedBytes += len;
				percentComplete = (int) ((unzippedBytes / (float) uncompressedSize) * progressMax)
						+ progressMin;
				if (percentComplete > percentCompleteLast) {
					publishProgress("Uncompressing data for " + Ocr.lang
							+ "...", percentComplete.toString());
					percentCompleteLast = percentComplete;
				}
			}
			bufferedOutputStream.flush();
			bufferedOutputStream.close();
		}
		tarInputStream.close();

		if (tarFile.exists()) {
			tarFile.delete();
		}
	}

	/**
	 * Return the uncompressed size for a Tar file.
	 * 
	 * @param tarFile
	 *            The Tarred file
	 * @return Size when uncompressed, in bytes
	 * @throws IOException
	 */
	private int getTarSizeUncompressed(File tarFile) throws IOException {
		int size = 0;
		TarInputStream tis = new TarInputStream(new BufferedInputStream(
				new FileInputStream(tarFile)));
		TarEntry entry;
		while ((entry = tis.getNextEntry()) != null) {
			if (!entry.isDirectory()) {
				size += entry.getSize();
			}
		}
		tis.close();
		return size;
	}

	/**
	 * Update the dialog box with the latest incremental progress.
	 * 
	 * @param message
	 *            [0] Text to be displayed
	 * @param message
	 *            [1] Numeric value for the progress
	 */
	@Override
	protected void onProgressUpdate(String... message) {
		super.onProgressUpdate(message);
		int percentComplete = 0;

		percentComplete = Integer.parseInt(message[1]);
		dialog.setMessage(message[0]);
		dialog.setProgress(percentComplete);
		dialog.show();
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		indeterminateDialog.dismiss();
	}
}
