package com.clavier;

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class ClavierView extends RelativeLayout{
	private final Context mContext;
	private OnCharacterEnteredListener mOnCharacterEnteredListener;
	private OnBackspacePressedListener mOnBackspacePressedListener;
	private final Queue<Character> mSymbolsQueue = new LinkedList<Character>();


	public ClavierView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	@Override
	protected void onFinishInflate() {
		ZoneSaisieView drawingSpaceView = (ZoneSaisieView) findViewById(R.id.drawing_space);
		drawingSpaceView.setOnGestureRecognizedListener(new OnGestureRecognizedListener() {
			@Override
			public void gestureRecognized(String character) {
				enterCharacter(character);
			}
		});

		final Button backspaceButton = (Button) findViewById(R.id.backspace_btn);
		backspaceButton.setOnClickListener(mButtonClickListener);
		backspaceButton.setOnLongClickListener(mButtonLongClickListener);

		final Button spaceButton = (Button) findViewById(R.id.space_btn);
		spaceButton.setOnClickListener(mButtonClickListener);
		spaceButton.setOnLongClickListener(mButtonLongClickListener);
	}

	public void setOnCharacterEnteredListener(OnCharacterEnteredListener onCharacterEnteredListener) {
		mOnCharacterEnteredListener = onCharacterEnteredListener;
	}

	public void setOnBackspacePressedListener(OnBackspacePressedListener onBackspacePressedListener) {
		mOnBackspacePressedListener = onBackspacePressedListener;
	}

	public Queue<Character> getSymbolsQueue() {
		return mSymbolsQueue;
	}

	/**
	 * Listener handling pressing all buttons.
	 */
	private final OnClickListener mButtonClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.backspace_btn:
				mOnBackspacePressedListener.backspacePressed(false);
				break;
			case R.id.space_btn:
				mOnCharacterEnteredListener.characterEntered(" ");
				break;
			default:
				throw new IllegalArgumentException();
			}
		}
	};

	/**
	 * Listener handling long pressing all buttons.
	 */
	private final OnLongClickListener mButtonLongClickListener = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			switch (v.getId()) {
			case R.id.backspace_btn:
				mOnBackspacePressedListener.backspacePressed(true);
				return true;
			case R.id.space_btn:
				break;
			default:
				throw new IllegalArgumentException();
			}

			return false;
		}
	};


	/**
	 * Passes the given character to the input service.
	 *
	 * @param character
	 *            The character to enter
	 */
	private void enterCharacter(String character) {
			mOnCharacterEnteredListener.characterEntered(character);
		}
}
