/*
 * A lot of this code has been inspired by :
 * 		- https://github.com/Kaloer/Android-File-Picker-Activity
 * 		- https://github.com/GautamGupta/Simple-Android-OCR
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.ece.ppe.firstocr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/FirstOCRTest/";
	
	public static final String lang = "eng";
	
	private static final String TAG = "MainActivity.java";
	
	private static final int REQUEST_PICK_FILE = 1;
	
	private TextView mFilePathTextView;
	private Button mStartActivityButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
		String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

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

		if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
			try {
				AssetManager assetManager = getAssets();
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

    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Set the views
        mFilePathTextView = (TextView)findViewById(R.id.file_path_text_view);
        mStartActivityButton = (Button)findViewById(R.id.start_file_picker_button);
        
        mStartActivityButton.setOnClickListener(this);
    }

	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.start_file_picker_button:
			// Create a new Intent for the file picker activity
			Intent intent = new Intent(this, FilePickerActivity.class);
			
			// Only make image files visible
			ArrayList<String> extensions = new ArrayList<String>();
			extensions.add(".png");
			extensions.add(".jpg");
			extensions.add(".jpeg");
			extensions.add(".bmp");
			intent.putExtra(FilePickerActivity.EXTRA_ACCEPTED_FILE_EXTENSIONS, extensions);
			
			// Start the activity
			startActivityForResult(intent, REQUEST_PICK_FILE);
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			switch(requestCode) {
			case REQUEST_PICK_FILE:
				if(data.hasExtra(FilePickerActivity.EXTRA_FILE_PATH)) {
					// Get the file path
					File f = new File(data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH));
					
					Bitmap bitmap = BitmapFactory.decodeFile(f.getPath(), null);
					
					Log.v(TAG, "Before baseApi");
					
					TessBaseAPI baseApi = new TessBaseAPI();
					baseApi.setDebug(true);
					baseApi.init(DATA_PATH, lang);
					baseApi.setImage(bitmap);
					
					String recognizedText = baseApi.getUTF8Text();
					
					baseApi.end();
					
					Log.v(TAG, "OCRED TEXT: " + recognizedText);
					
					// Show the recognized text
					mFilePathTextView.setText(recognizedText);
				}
			}
		}
	}
}