package com.clavier;

import java.util.Queue;
import android.inputmethodservice.InputMethodService;
import android.view.View;
import android.view.inputmethod.EditorInfo;

public class Clavier extends InputMethodService {

	 private ClavierView mClavierView;

	  @Override
	  public View onCreateInputView() {
		final ClavierView clavierView = (ClavierView) getLayoutInflater().inflate(R.layout.clavier, null);
		
		clavierView.setOnCharacterEnteredListener(new OnCharacterEnteredListener() {
			@Override
			public void characterEntered(String character) {
				getCurrentInputConnection().commitText(character, 1); //Envoie le caractère reconnu à l'application
			}
		});

		//Appuie sur bouton "effacer"
		clavierView.setOnBackspacePressedListener(new OnBackspacePressedListener() {
			@Override
			public void backspacePressed(boolean isLongClick) {
				if (isLongClick) {
					deleteLastWord(); //Appui long, efface 20 caractères
				} else {
					getCurrentInputConnection().deleteSurroundingText(1, 0); //Appui court, efface juste le caractère
				}
			}
		});

		mClavierView = clavierView;
		return clavierView;
	}

	@Override
	public void onStartInput(EditorInfo attribute, boolean restarting) {
		if (mClavierView != null) {
			final Queue<Character> symbolsQueue = mClavierView.getSymbolsQueue();
			while (!symbolsQueue.isEmpty()) {
				final Character character = symbolsQueue.poll();
				getCurrentInputConnection().commitText(String.valueOf(character), 1);
			}
		}
	}

	/**
	 * Deletes one word before the cursor.
	 */
	private void deleteLastWord() {
		final int charactersToGet = 20;
		final String splitRegexp = " ";

		// delete trailing spaces
		while (getCurrentInputConnection().getTextBeforeCursor(1, 0).toString().equals(splitRegexp)) {
			getCurrentInputConnection().deleteSurroundingText(1, 0);
		}

		// delete last word letters
		final String[] words = getCurrentInputConnection().getTextBeforeCursor(charactersToGet, 0).toString()
				.split(splitRegexp);
		getCurrentInputConnection().deleteSurroundingText(words[words.length - 1].length(), 0);
	}
	  
	  

}
