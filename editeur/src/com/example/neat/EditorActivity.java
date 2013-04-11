package com.example.neat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditorActivity extends Activity {

	private final String EXTRA_FILENAME = "filename";

	/**
	 * Name of the file opened in the Activity
	 */
	private String OpenedFileName;

	/**
	 * Total path to the file opened in the activity
	 */
	private String OpenedFilePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editor);

		final EditText editText1 = (EditText) findViewById(R.id.editText1);

		Intent intent = getIntent();
		if (intent != null) {
			String filepath = intent.getStringExtra(EXTRA_FILENAME);

			if (filepath != null) {
				if (new File(filepath).isFile()) {
					editText1.setText(/* "\'begin{}"+ */lireFichier(intent
							.getStringExtra(EXTRA_FILENAME))/* +"\'end{}" */);
				} else {
					editText1.setText("\\begin{}\\end{}");
				}
			}
		}

		Button bold = (Button) findViewById(R.id.button4);
		bold.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TextBold(editText1);
			}
		});

		Button italic = (Button) findViewById(R.id.button5);
		italic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TextItalic(editText1);
			}
		});

		Button underline = (Button) findViewById(R.id.button6);
		underline.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TextUnderline(editText1);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.actionbar_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		final EditText editText1 = (EditText) findViewById(R.id.editText1);
		final EditText input = new EditText(this);

		switch (item.getItemId()) {
		case R.id.show:
			// Activity open
			Intent intent = new Intent(EditorActivity.this,
					FileChooserActivity.class);

			// Only make image files visible
			ArrayList<String> extensions = new ArrayList<String>();
			extensions.add(".tex");
			intent.putExtra(FileChooserActivity.EXTRA_ACCEPTED_FILE_EXTENSIONS,
					extensions);

			// Start the activity
			startActivity(intent);
			return true;
		case R.id.save:
			AlertDialog builder = new AlertDialog.Builder(EditorActivity.this)
					.setTitle("Save File")
					.setView(input)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									ecrireFichier(input.getText().toString()
											+ ".tex", editText1.getText()
											.toString());
								}
							})
					.setNegativeButton("Go back",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();
			return true;

		case R.id.compile:
			AlertDialog builder1 = new AlertDialog.Builder(EditorActivity.this)
		  	.setTitle("Tutorial")
		  	.setMessage(OpenedFilePath+" / "+ OpenedFileName +" / "+ RemoteCompiler.ip_adress)
		  	.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
		  		@Override
		  		public void onClick(DialogInterface dialog, int which) {
		  			// TODO Auto-generated method stub
		  		}
		  	})
		  	.show();
			//RemoteCompiler.compile(OpenedFilePath, OpenedFileName);
			//Toast.makeText(getBaseContext(), OpenedFilePath, 10);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public String lireFichier(String nomFichier) {
		String monText = "";
		File monFichier = new File(nomFichier);

		OpenedFileName = monFichier.getName().substring(0,
				monFichier.getName().length() - 4);
		OpenedFilePath = monFichier.getAbsolutePath();

		if (!monFichier.exists()) {
			throw new RuntimeException("Fichier inexistant dur la carte sd");
		}
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(monFichier));
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append("\n");
			}
			monText = builder.toString();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return monText;
	}

	public void ecrireFichier(String nomFichier, String monText) {

		File sdLien = Environment.getExternalStorageDirectory();
		File monFichier = new File(sdLien, nomFichier);

		BufferedWriter writer = null;
		FileWriter out = null;
		try {
			out = new FileWriter(monFichier, false);
			writer = new BufferedWriter(out);
			writer.write(monText);
			writer.flush();
			writer.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void TextBold(EditText editText1) {
		int startselection = editText1.getSelectionStart();
		int endselection = editText1.getSelectionEnd();

		Editable edit = editText1.getText();
		String selectedText = editText1.getText()
				.subSequence(startselection, endselection).toString();

		if (startselection == endselection) {
			edit.insert(startselection, "<b></b>");
		} else if (selectedText.startsWith("<") && !selectedText.endsWith(">")) {
			edit.insert(startselection + 3, "<b>");
			edit.insert(endselection + 3, "</b>");
		} else if (selectedText.endsWith(">") && !selectedText.startsWith("<")) {
			edit.insert(startselection, "<b>");
			edit.insert(endselection - 1, "</b>");
		} else if (selectedText.startsWith("<") && selectedText.endsWith(">")) {
			edit.insert(startselection, "<b>");
			edit.insert(endselection + 3, "</b>");
		} else {
			edit.insert(startselection, "<b>");
			edit.insert(endselection + 3, "</b>");
		}
	}

	public void TextItalic(EditText editText1) {
		int startselection = editText1.getSelectionStart();
		int endselection = editText1.getSelectionEnd();

		Editable edit = editText1.getText();
		String selectedText = editText1.getText()
				.subSequence(startselection, endselection).toString();
		if (startselection == endselection) {
			edit.insert(startselection, "<i></i>");
		} else if (selectedText.startsWith("<") && !selectedText.endsWith(">")) {
			edit.insert(startselection + 3, "<i>");
			edit.insert(endselection + 3, "</i>");
		} else if (selectedText.endsWith(">") && !selectedText.startsWith("<")) {
			edit.insert(startselection, "<i>");
			edit.insert(endselection - 1, "</i>");
		} else if (selectedText.startsWith("<") && selectedText.endsWith(">")) {
			edit.insert(startselection, "<i>");
			edit.insert(endselection + 3, "</i>");
		} else {
			edit.insert(startselection, "<i>");
			edit.insert(endselection + 3, "</i>");
		}
	}

	public void TextUnderline(EditText editText1) {
		int startselection = editText1.getSelectionStart();
		int endselection = editText1.getSelectionEnd();

		Editable edit = editText1.getText();
		String selectedText = editText1.getText()
				.subSequence(startselection, endselection).toString();
		if (startselection == endselection) {
			edit.insert(startselection, "<u></u>");
		} else if (selectedText.startsWith("<") && !selectedText.endsWith(">")) {
			edit.insert(startselection + 3, "<u>");
			edit.insert(endselection + 3, "</u>");
		} else if (selectedText.endsWith(">") && !selectedText.startsWith("<")) {
			edit.insert(startselection, "<u>");
			edit.insert(endselection - 1, "</u>");
		} else if (selectedText.startsWith("<") && selectedText.endsWith(">")) {
			edit.insert(startselection, "<u>");
			edit.insert(endselection + 3, "</u>");
		} else {
			edit.insert(startselection, "<u>");
			edit.insert(endselection + 3, "</u>");
		}
	}

	public void TextChangeDelete(EditText editText1) {
		int startselection = editText1.getSelectionStart();
		int endselection = editText1.getSelectionEnd();

		Editable edit = editText1.getText();
		String selectedText = editText1.getText()
				.subSequence(startselection, endselection).toString();
		if (!(startselection == endselection)) {
			if (selectedText.startsWith("<") && !selectedText.endsWith(">")) {
				edit.replace(startselection + 3, endselection, "");
				editText1.setText(edit);
			} else if (selectedText.endsWith(">")
					&& !selectedText.startsWith("<")) {
				edit.replace(startselection, endselection - 4, "");
				editText1.setText(edit);
			} else {
				edit.replace(startselection, endselection, "");
				editText1.setText(edit);
			}
		}
	}

}
