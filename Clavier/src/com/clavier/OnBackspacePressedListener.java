package com.clavier;

public interface OnBackspacePressedListener {
	/**
	 * Invoked when the backspace button is pressed.
	 *
	 * @param isLongClick
	 *            if the button is long pressed
	 */
	void backspacePressed(boolean isLongClick);

}
