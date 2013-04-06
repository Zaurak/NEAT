package com.clavier;

public interface OnGestureRecognizedListener {
	/**
	 * Invoked when a gesture is recognized.
	 *
	 * @param character
	 *            The character represented by the gesture.
	 */
	void gestureRecognized(String character);

}
