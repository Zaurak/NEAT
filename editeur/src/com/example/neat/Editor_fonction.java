package com.example.neat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;
import android.text.Editable;
import android.widget.EditText;

public class Editor_fonction {
	
	public String lireFichier(String nomFichier) {
        String monText="";
        File sdLien = Environment.getExternalStorageDirectory(); 
        File monFichier = new File(sdLien + "/" +nomFichier); 
        if (!monFichier.exists()) {
               throw new RuntimeException("Fichier inéxistant dur la carte sd");
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
	
	public void TextChangeDelete(EditText editText1){
		int startselection = editText1.getSelectionStart();
		int endselection = editText1.getSelectionEnd();
		
		Editable edit = editText1.getText();
		String selectedText = editText1.getText().subSequence(startselection, endselection).toString();
		if(!(startselection == endselection)){
			if(selectedText.startsWith("<")){
				if(selectedText.endsWith(">")){
					edit.replace(startselection, endselection, "");
					editText1.setText(edit);
				}else{
					edit.replace(startselection + 3, endselection, "");
					editText1.setText(edit);
				}
			}else if(selectedText.endsWith(">")){
				if(selectedText.startsWith("<")){
					edit.replace(startselection, endselection, "");
					editText1.setText(edit);
				}else{
					edit.replace(startselection, endselection - 4, "");
					editText1.setText(edit);
				}
			}else {
				edit.replace(startselection, endselection, "");
				editText1.setText(edit);
			}
		}
	}
	
	public void TextChangeInsert(EditText editText1){
		int startselection = editText1.getSelectionStart();
		int endselection = editText1.getSelectionEnd();
		
		Editable edit = editText1.getText();
		String textToInsert = "titi";
		editText1.getText().replace(Math.min(startselection, endselection), Math.max(startselection, endselection), textToInsert, 0 , textToInsert.length());
	}
	
	public void TextBold(EditText editText1){
		int startselection = editText1.getSelectionStart();
		int endselection = editText1.getSelectionEnd();
		
		Editable edit = editText1.getText();
		String selectedText = editText1.getText().subSequence(startselection, endselection).toString();
		//edit.replace(startselection, endselection, Html.fromHtml("<b>"+selectedText+"</b>"));
		if(startselection==endselection){
			edit.insert(startselection, "<b></b>");
		}else if(selectedText.startsWith("<")){
			if(selectedText.endsWith(">")){
				edit.insert(startselection + 3, "<b>");
				edit.insert(endselection - 4, "</b>");
			}else {
				edit.insert(startselection + 3, "<b>");
				edit.insert(endselection + 3, "</b>");
			}
		}else if(selectedText.endsWith(">")){
			if(selectedText.startsWith("<")){
				edit.insert(startselection + 3, "<b>");
				edit.insert(endselection - 4, "</b>");
			}else{
				edit.insert(startselection, "<b>");
				edit.insert(endselection, "</b>");
			}
		}else{			
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
		}else{
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
		}else{
			edit.insert(startselection, "<u>");
			edit.insert(endselection + 3, "</u>");
		}
	}

}
