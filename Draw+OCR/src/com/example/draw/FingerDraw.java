package com.example.draw;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class FingerDraw extends Activity {
	
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/NEAT/";
	
	public static final String lang = "eng";
	
	private static final String TAG = "FingerDraw.java";
	
	private Paint mPaint;
	private MyView view;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
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

    	LinearLayout layout = new LinearLayout(this);
        layout.setId(R.layout.activity_finger_draw);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,1);
        layout.setLayoutParams(layoutParams);
        layout.setOrientation(LinearLayout.VERTICAL);
        
        Button mButton = new Button(this);
        mButton.setText("Launch OCR");
        mButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,2));
        
        final TextView mRecognizedText = new TextView(this);
        mRecognizedText.setText("Recognized Text");
        mRecognizedText.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,3));
        
        view = new MyView(this);
        view.setLayoutParams(layoutParams);
        layout.addView(view);
        layout.addView(mButton);
        layout.addView(mRecognizedText);
        
        /*Toast toast = Toast.makeText(getApplicationContext(), view.lor, 2);
        toast.show();*/

        setContentView(layout);
    	//setContentView(R.layout.activity_finger_draw);
        
        //The Paint class holds the style and color information about how to draw geometries, text and bitmaps.
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFF4858C9); //Couleur du trait
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(4); //Taille d'un point en pixels
        
        mButton.setOnClickListener(new OnClickListener() {           

        	  public void onClick(View v) 
        	  {
        		  	TessBaseAPI baseApi = new TessBaseAPI();
					baseApi.setDebug(true);
					baseApi.init(DATA_PATH, lang);
					baseApi.setImage(view.mBitmap);
					
					String recognizedText = baseApi.getUTF8Text();
					
					baseApi.end();
					
					mRecognizedText.setText(recognizedText);
        	  }    
        	});
    }
     
   
public class MyView extends View {
        
        private Bitmap  mBitmap;
        private Canvas  mCanvas;
        private Path    mPath;
        private Paint   mBitmapPaint;
        public String lor;
        
        //Taille de l'écran
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        
        public MyView(Context c) {
            super(c);
            
            mBitmap = Bitmap.createBitmap(width, height/2, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
        }
        
        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(0xFFAAAAAA);
            
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            
            canvas.drawPath(mPath, mPaint);
        }
        
        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;
        
        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }
        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            
            if (x-mX>0) System.out.println("RIGHT");
            else if (x-mX<0) System.out.println("LEFT");
            if (y-mY>0) System.out.println("BOTTOM");
            else if (y-mY<0) System.out.println("TOP");
            
            /*if (x-mX>0 && y-mY>0) lor="RIGHT-BOTTOM";
            if (x-mX>0 && y-mY<0) lor="RIGHT-TOP";
            if (x-mX<0 && y-mY>0) lor="LEFT-BOTTOM";
            if (x-mX<0 && y-mY<0) lor="LEFT-TOP";*/
            
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;
            }
        }
        private void touch_up() {
        	mCanvas.drawPoint(mX,  mY,  mPaint); //Pour pouvoir faire de simples points
        	
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }
        
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: //le doigt touche l'écran
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE: //le doigt bouge sur l'écran
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP: //le doigt quitte l'écran
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(width , height/2);
        }
    }
}