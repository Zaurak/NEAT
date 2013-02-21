package ocr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.tesseract.android.TessBaseAPI;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

public class Ocr {
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/NEAT/";
	
	public static final String lang = "eng";
	
	private static final String TAG = "FingerDraw.java";
	
	
	private TessBaseAPI baseApi;
	private String recognizedText;
	private OcrResult ocrResult;
	
	public Ocr(Context context) {
		init(context);
	}
	
	public void init(Context context) {
		String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };
		
		/*****  Check for NEAT directory *****/
		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
					return;
				} else {
					Log.v(TAG, "Created directory " + path + " on sdcard");
				}
			}
		}
		/***** Check for traineddata file *****/
		if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
			try {
				AssetManager assetManager = context.getAssets();
				InputStream in = assetManager.open("tessdata/eng.traineddata");
				OutputStream out = new FileOutputStream(DATA_PATH
						+ "tessdata/eng.traineddata");

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				
				out.close();
				
				Log.v(TAG, "Copied " + lang + " traineddata");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
			}
		}
		
		baseApi = new TessBaseAPI();
		baseApi.setDebug(true);
		baseApi.init(DATA_PATH, lang);
		ocrResult = new OcrResult();
	}
	
	public String recognize(Bitmap mBitmap) {
		baseApi.setImage(ReadFile.readBitmap(mBitmap));
		recognizedText = baseApi.getUTF8Text();
		
	    ocrResult.setWordConfidences(baseApi.wordConfidences());
	    ocrResult.setMeanConfidence( baseApi.meanConfidence());
	    ocrResult.setRegionBoundingBoxes(baseApi.getRegions().getBoxRects());
	    ocrResult.setTextlineBoundingBoxes(baseApi.getTextlines().getBoxRects());
	    ocrResult.setWordBoundingBoxes(baseApi.getWords().getBoxRects());
	    ocrResult.setStripBoundingBoxes(baseApi.getStrips().getBoxRects());
	    ocrResult.setCharacterBoundingBoxes(baseApi.getCharacters().getBoxRects());
	    ocrResult.setBitmap(mBitmap);
	    ocrResult.setText(recognizedText);
		
		baseApi.clear();
		mBitmap = ocrResult.getBitmap();
		
		return recognizedText;
	}
}
