package uk.co.deftelf.writefocus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {
    
    private static final String MEASURE_TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce massa nunc. mmmmmm";
    
    WfEditText text;
    TextView helpHints;
    Toast statsToast;
    Handler helpHintHide;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        text = (WfEditText) findViewById(R.id.text);
        helpHints = (TextView) findViewById(R.id.helphints);
        text.parent = this;
        
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
        //text.setText(testText);
        text.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
        
        try {
            Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/DOSEGA.ttf");
            text.setTypeface(tf);
        } catch (RuntimeException ex) {
        }
        
        text.getLayoutParams().width = (int)Math.ceil(text.getPaint().measureText(MEASURE_TEXT));
        statsToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
        
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
            } else if (!word && 
                    !Character.isWhitespace(ch)) {
                word = true;
                wc++;
            }
        }
        return wc;
    }
    
    public int lineCount() {
        int lc = 1;
        Editable ed = text.getText();
        for (int i=0; i < ed.length(); i++) {
            char ch = ed.charAt(i);
            if (ch == '\n')
                lc++;
        }
        return lc;
    }
    
    public void showStats() {
        statsToast.setText("Lines: " + lineCount() + "\nWords: " + wordCount() + "\nCharacters: " + text.getText().length());
        statsToast.show();
    }

    public void showHelpHint() {
        if (helpHintHide != null)
            helpHintHide.removeMessages(0);
        
        if (helpHints.getVisibility() == View.GONE) {
            helpHints.setAlpha(0f);
            helpHints.setVisibility(View.VISIBLE);
            final ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
            anim.setInterpolator(new AccelerateInterpolator());
            anim.setDuration(300);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                
                public void onAnimationUpdate(ValueAnimator arg0) {
                    helpHints.setAlpha((Float) anim.getAnimatedValue());
                }
            });
            anim.start();
        }
        
        helpHintHide = new Handler(new Handler.Callback() {
            
            public boolean handleMessage(Message arg0) {
                final ValueAnimator anim = ValueAnimator.ofFloat(1f, 0f);
                anim.setDuration(300);
                anim.setInterpolator(new AccelerateInterpolator());
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    
                    public void onAnimationUpdate(ValueAnimator arg0) {
                        helpHints.setAlpha((Float) anim.getAnimatedValue());
                    }
                });
                anim.addListener(new Animator.AnimatorListener() {
                    
                    
                    public void onAnimationStart(Animator arg0) {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    public void onAnimationRepeat(Animator arg0) {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    public void onAnimationEnd(Animator arg0) {
                        helpHints.setVisibility(View.GONE);
                    }
                    
                    public void onAnimationCancel(Animator arg0) {
                        // TODO Auto-generated method stub
                        
                    }
                });
                anim.start();
                return true;
            }
        });
        helpHintHide.sendEmptyMessageDelayed(0, 5000);
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