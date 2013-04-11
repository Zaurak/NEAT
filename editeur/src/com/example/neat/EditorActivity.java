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
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EditorActivity extends Activity {
	
	 final String EXTRA_FILENAME = "filename";
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editor);
		// Show the Up button in the action bar.
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		//registerForContextMenu(findViewById(R.id.editText1));
		final EditText editText1 = (EditText)findViewById(R.id.editText1);
		final EditText input = new EditText(this);
		
		Intent intent = getIntent();
        if (intent != null) {
    	    editText1.setText(lireFichier(intent.getStringExtra(EXTRA_FILENAME)));
        }
		
		Button bold = (Button)findViewById(R.id.button4);
		bold.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TextBold(editText1);
			}
		});
		
		Button italic = (Button)findViewById(R.id.button5);
		italic.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TextItalic(editText1);
			}
		});
		
		Button underline = (Button)findViewById(R.id.button6);
		underline.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TextUnderline(editText1);
			}
		});
		
		Button delete = (Button)findViewById(R.id.button7);
		delete.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TextChangeDelete(editText1);
			}
		});
	}

	/*@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                               ContextMenuInfo menuInfo) {
	 super.onCreateContextMenu(menu, v, menuInfo);
	 MenuInflater inflater = getMenuInflater();
	 inflater.inflate(R.menu.activity_editor, menu);
	}*/
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.actionbar_menu, menu);
		return true;
	}
	
	/*@Override
	public boolean onContextItemSelected(MenuItem item) {
		final EditText editText1 = (EditText)findViewById(R.id.editText1);
	 switch (item.getItemId()) {
	  case R.id.underline:
	   		TextUnderline(editText1);
	   return true;
	  case R.id.italic:
		    TextItalic(editText1);
	  return true;
	  case R.id.underline:
		   	TextUnderline(editText1);
		  return true;
	  default:
	    return super.onContextItemSelected(item);
	 }
	}*/

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		final EditText editText1 = (EditText)findViewById(R.id.editText1);
		final EditText input = new EditText(this);
				
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.show:
			// Activity open
			Intent intent = new Intent(EditorActivity.this, FileChooserActivity.class);
					
			// Only make image files visible
			ArrayList<String> extensions = new ArrayList<String>();
			extensions.add(".neat");
			intent.putExtra(FileChooserActivity.EXTRA_ACCEPTED_FILE_EXTENSIONS, extensions);
					
			// Start the activity
			startActivity(intent);
			return true;
		case R.id.save:
			AlertDialog builder = new AlertDialog.Builder(EditorActivity.this)
		  	.setTitle("Save File")
		  	.setView(input)
		  	.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
		  		@Override
		  		public void onClick(DialogInterface dialog, int which) {
		  			// TODO Auto-generated method stub
		  			ecrireFichier(input.getText().toString() + ".tex", editText1.getText().toString());
		  		}
		  	})
		  	.setNegativeButton("Go back", new DialogInterface.OnClickListener(){
		  		@Override
		  		public void onClick(DialogInterface dialog, int which) {
		  			// TODO Auto-generated method stub
		  		}
		  	}).show();
			return true;
		case R.id.bold:
			TextBold(editText1);
			return true;
		case R.id.italic:
			TextItalic(editText1);
			return true;
		case R.id.underline:
			TextUnderline(editText1);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public String lireFichier(String nomFichier) {
        String monText="";
        File sdLien = Environment.getExternalStorageDirectory(); 
        File monFichier = new File(sdLien + "/" +nomFichier); 
        if (!monFichier.exists()) {
               throw new RuntimeException("Fichier in�xistant dur la carte sd");
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
        } /*finally {
               if (reader != null) {
                       try {
                               reader.close();
                       } catch (IOException e) {
                               e.printStackTrace();
                       }
               }
        }*/
        return monText;
    }
	
	public void ecrireFichier(String nomFichier,String monText) {
        
        File sdLien = Environment.getExternalStorageDirectory(); 
        File monFichier = new File(sdLien , nomFichier);
        
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
	
	public void TextBold(EditText editText1){
		int startselection = editText1.getSelectionStart();
		int endselection = editText1.getSelectionEnd();
		
		Editable edit = editText1.getText();
		String selectedText = editText1.getText().subSequence(startselection, endselection).toString();
		//edit.replace(startselection, endselection, Html.fromHtml("<i>"+selectedText+"</i>"));
		if(startselection==endselection){
			edit.insert(startselection, "<b></b>");
		} else if(selectedText.startsWith("<") && !selectedText.endsWith(">"))
		{
			edit.insert(startselection + 3, "<b>");
			/*if(selectedText.endsWith(">"))
			{
				edit.insert(endselection - 4, "</b>");
			}else */edit.insert(endselection + 3, "</b>");
		} else if(selectedText.endsWith(">") && !selectedText.startsWith("<"))
		{
			edit.insert(startselection, "<b>");
			/*if(selectedText.startsWith("<"))
			{
				edit.insert(endselection - 4, "</b>");
			}else */edit.insert(endselection - 1, "</b>");
		}else if(selectedText.startsWith("<") && selectedText.endsWith(">"))
		{
			edit.insert(startselection, "<b>");
			edit.insert(endselection + 3, "</b>");
		}
		else
		{
			edit.insert(startselection, "<b>");
			edit.insert(endselection + 3, "</b>");
		}
	}
	
	
	public void TextItalic(EditText editText1){
		int startselection = editText1.getSelectionStart();
		int endselection = editText1.getSelectionEnd();
		
		Editable edit = editText1.getText();
		String selectedText = editText1.getText().subSequence(startselection, endselection).toString();
		//edit.replace(startselection, endselection, Html.fromHtml("<i>"+selectedText+"</i>"));
		if(startselection==endselection){
			edit.insert(startselection, "<i></i>");
		}else if(selectedText.startsWith("<") && !selectedText.endsWith(">"))
		{
			edit.insert(startselection + 3, "<i>");
			/*if(selectedText.endsWith(">"))
			{
				edit.insert(endselection - 4, "</i>");
			}else */edit.insert(endselection + 3, "</i>");
		} else if(selectedText.endsWith(">") && !selectedText.startsWith("<"))
		{
			edit.insert(startselection, "<i>");
			/*if(selectedText.startsWith("<"))
			{
				edit.insert(endselection - 4, "</b>");
			}else */edit.insert(endselection - 1, "</i>");
		}else if(selectedText.startsWith("<") && selectedText.endsWith(">"))
		{
			edit.insert(startselection, "<i>");
			edit.insert(endselection + 3, "</i>");
		}else
		{
			edit.insert(startselection, "<i>");
			edit.insert(endselection + 3, "</i>");
		}
	}
	
	public void TextUnderline(EditText editText1){
		int startselection = editText1.getSelectionStart();
		int endselection = editText1.getSelectionEnd();
		
		Editable edit = editText1.getText();
		String selectedText = editText1.getText().subSequence(startselection, endselection).toString();
		//edit.replace(startselection, endselection, Html.fromHtml("<u>"+selectedText+"</u>"));
		if(startselection==endselection){
			edit.insert(startselection, "<u></u>");
		} else if(selectedText.startsWith("<") && !selectedText.endsWith(">"))
		{
			edit.insert(startselection + 3, "<u>");
			/*if(selectedText.endsWith(">"))
			{
				edit.insert(endselection - 4, "</b>");
			}else */edit.insert(endselection + 3, "</u>");
		} else if(selectedText.endsWith(">") && !selectedText.startsWith("<"))
		{
			edit.insert(startselection, "<u>");
			/*if(selectedText.startsWith("<"))
			{
				edit.insert(endselection - 4, "</b>");
			}else */edit.insert(endselection - 1, "</u>");
		}else if(selectedText.startsWith("<") && selectedText.endsWith(">"))
		{
			edit.insert(startselection, "<u>");
			edit.insert(endselection + 3, "</u>");
		}else{
			edit.insert(startselection, "<u>");
			edit.insert(endselection + 3, "</u>");
		}
	}
	
	public void TextChangeDelete(EditText editText1){
		int startselection = editText1.getSelectionStart();
		int endselection = editText1.getSelectionEnd();
		
		Editable edit = editText1.getText();
		String selectedText = editText1.getText().subSequence(startselection, endselection).toString();
		if(!(startselection == endselection))
		{
			if(selectedText.startsWith("<") && !selectedText.endsWith(">"))
			{
					edit.replace(startselection + 3, endselection, "");
					editText1.setText(edit);
			} else if(selectedText.endsWith(">") && !selectedText.startsWith("<")){
					edit.replace(startselection, endselection - 4, "");
					editText1.setText(edit);
			} else {
				edit.replace(startselection, endselection, "");
				editText1.setText(edit);
			}
		}
	}

}
