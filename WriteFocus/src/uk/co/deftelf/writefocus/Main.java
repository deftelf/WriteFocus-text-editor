package uk.co.deftelf.writefocus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class Main extends Activity {
    
    EditText text;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        text = (EditText) findViewById(R.id.text);
        
        StringBuilder testText = new StringBuilder();
        try {
            InputStream fs = getAssets().open("pg2600.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fs));
            String line;
            while ((line = br.readLine()) != null)
                testText.append(line + "\n");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (int i=0; i < 10000; i++)
            testText.append(i + "\n");
        text.setText(testText);
        text.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
        
        try {
            Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/DOSEGA.ttf");
            text.setTypeface(tf);
        } catch (RuntimeException ex) {
        }
        
        
        
        Log.i("wc", wordCount() + "");
        Log.i("lc", lineCount() + "");
    }
    
    public int wordCount() {
        int wc = 0;
        Editable ed = text.getText();
        boolean word = false;
        for (int i=0; i < ed.length(); i++) {
            char ch = ed.charAt(i);
            if (word &&
                    Character.isWhitespace(ch)) {
                word = false;
                wc++;
            } else if (!word && 
                    !Character.isWhitespace(ch)) {
                word = true;
            }
        }
        return wc;
    }
    
    public int lineCount() {
        int lc = 0;
        Editable ed = text.getText();
        for (int i=0; i < ed.length(); i++) {
            char ch = ed.charAt(i);
            if (ch == '\n')
                lc++;
        }
        return lc;
    }
    

//    public int lineCount() {
//        int lc = 0;
//        Editable ed = text.getText();
//        boolean line = false;
//        for (int i=0; i < ed.length(); i++) {
//            char ch = ed.charAt(i);
//            if (line &&
//                    ch == '\n') {
//                line = false;
//                lc++;
//            } else if (!line && 
//                    ch != '\n') {
//                line = true;
//            }
//        }
//        return lc;
//    }
}