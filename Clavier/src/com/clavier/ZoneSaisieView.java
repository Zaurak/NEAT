package com.clavier;

import java.util.List;
import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.util.AttributeSet;

public class ZoneSaisieView extends GestureOverlayView implements OnGesturePerformedListener {
	private static final double SCORE_TRESHOLD = 3.0;
	private final GestureLibrary mGestureLibrary;
	private OnGestureRecognizedListener mOnGestureRecognizedListener;

	public ZoneSaisieView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mGestureLibrary = GestureLibraries.fromRawResource(context, R.raw.gestures); //Recherche le fichier gesture dans lequel se trouve les caractères reconnus grâce à l'application Gesture Builder
		mGestureLibrary.load();
		addOnGesturePerformedListener(this);
	}

	public void setOnGestureRecognizedListener(OnGestureRecognizedListener onGestureRecognizedListener) {
		mOnGestureRecognizedListener = onGestureRecognizedListener;
	}


	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		final List<Prediction> predictions = mGestureLibrary.recognize(gesture);
		Prediction bestPrediction = null;
		if (!predictions.isEmpty()) {
			bestPrediction = predictions.get(0);
		}
		if (mOnGestureRecognizedListener != null && bestPrediction != null) {
			if (bestPrediction.score > SCORE_TRESHOLD) {
				mOnGestureRecognizedListener.gestureRecognized(bestPrediction.name);
			} else {
				clear(false);
			}
		}
	}
}