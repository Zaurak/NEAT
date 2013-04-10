package ocr;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.tesseract.android.TessBaseAPI;

public class Ocr {
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/NEAT/";

	public static final String lang = "eng";

	private static final String TAG = "Ocr.java";

	static final String DOWNLOAD_BASE = "https://tesseract-ocr.googlecode.com/files/";

	private TessBaseAPI baseApi;
	private String recognizedText;
	private OcrResult ocrResult;
	private ProgressDialog indeterminateDialog;

	public Ocr(Context context) {
		init(context);
	}

	public void init(Context context) {
		
		// Display the name of the OCR engine we're initializing in the indeterminate progress dialog box
	    indeterminateDialog = new ProgressDialog(context);
	    Log.d(TAG, "indeterminateDialog created");
	    indeterminateDialog.setTitle("Please wait");
	    indeterminateDialog.setMessage("Initializing Tesseract OCR engine for " + lang + "...");
	    indeterminateDialog.setCancelable(false);
	    indeterminateDialog.show();
		baseApi = new TessBaseAPI();
		new OcrInitFiles(baseApi, new ProgressDialog(context), indeterminateDialog).execute("");

		baseApi.setDebug(true);
		ocrResult = new OcrResult();
	}

	public String recognize(Bitmap mBitmap) {
		baseApi.setImage(ReadFile.readBitmap(mBitmap));
		recognizedText = baseApi.getUTF8Text();

		ocrResult.setWordConfidences(baseApi.wordConfidences());
		ocrResult.setMeanConfidence(baseApi.meanConfidence());
		ocrResult.setRegionBoundingBoxes(baseApi.getRegions().getBoxRects());
		ocrResult.setTextlineBoundingBoxes(baseApi.getTextlines().getBoxRects());
		ocrResult.setWordBoundingBoxes(baseApi.getWords().getBoxRects());
		ocrResult.setStripBoundingBoxes(baseApi.getStrips().getBoxRects());
		ocrResult.setCharacterBoundingBoxes(baseApi.getCharacters()
				.getBoxRects());
		ocrResult.setBitmap(mBitmap);
		ocrResult.setText(recognizedText);

		baseApi.clear();
		mBitmap = ocrResult.getBitmap();

		return recognizedText;
	}
}
