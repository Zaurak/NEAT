package com.example.neat;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	private final static String EXTRA_FILENAME = "filename";
	private final static String defaultFileName = Environment
			.getExternalStorageDirectory().getPath() + "/Test.tex";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final String input = "temp pour test";

		final EditText edit = new EditText(this);

		Button newdoc = (Button) findViewById(R.id.button3);
		newdoc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentnew = new Intent(MainActivity.this,
						EditorActivity.class);
				intentnew.putExtra(EXTRA_FILENAME, defaultFileName);
				startActivity(intentnew);
			}
		});

		Button opendoc = (Button) findViewById(R.id.button2);
		opendoc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Activity open
				Intent intent = new Intent(MainActivity.this,
						FileChooserActivity.class);

				// Only make image files visible
				ArrayList<String> extensions = new ArrayList<String>();
				extensions.add(".tex");
				intent.putExtra(
						FileChooserActivity.EXTRA_ACCEPTED_FILE_EXTENSIONS,
						extensions);

				// Start the activity
				startActivity(intent);
			}
		});

		Button tuto = (Button) findViewById(R.id.button1);
		tuto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Afficher l'adresse ip du serveur
				AlertDialog builder = new AlertDialog.Builder(MainActivity.this)
						.setTitle("Tutorial")
						.setMessage(input)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
									}
								}).show();
			}
		});

		Button settings = (Button) findViewById(R.id.button4);
		tuto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				AlertDialog builder = new AlertDialog.Builder(MainActivity.this)
						.setTitle("Select IP")
						.setView(edit)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										RemoteCompiler.setIp(edit.getText()
												.toString());
									}
								})
						.setNegativeButton("Go back",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
									}
								}).show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
