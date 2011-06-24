package uk.co.deftelf.writefocus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Stack;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {
    
    private static final String MEASURE_TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce massa nunc. mmmmmm";
    
    private WfEditText text;
    private TextView helpHints;
    private Toast statsToast;
    private Handler helpHintHide;
    private Toast notFound;
    private Object findHighlightSpan;
    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        text = (WfEditText) findViewById(R.id.text);
        helpHints = (TextView) findViewById(R.id.helphints);
        text.init(this);
        
        notFound = Toast.makeText(this, R.string.not_found, Toast.LENGTH_SHORT);
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
        text.setText(testText);
        text.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
        
        try {
            Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/DOSEGA.ttf");
            text.setTypeface(tf);
        } catch (RuntimeException ex) {
        }
        
        text.setScroller(new Scroller(this, null, true));
        text.getLayoutParams().width = (int)Math.ceil(text.getPaint().measureText(MEASURE_TEXT));
        statsToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
        text.suppressUndo = false;
    }
    
    
    public int search(String search) {
        if (search.length() == 0)
            return -1;
        CharSequence contents = text.getText();
        int cursorAt = 0;//text.getSelectionEnd();
        boolean found = false;
        while (!found && cursorAt < contents.length()) {
            int i = 0;
            for (; cursorAt < contents.length() && i < search.length(); cursorAt++) {
                char c = Character.toLowerCase(search.charAt(i));
                if (cursorAt < contents.length() &&
                        c == Character.toLowerCase(contents.charAt(cursorAt))) {
                    i++;
                    found = true;
                } else {
                    found = false;
                    cursorAt++;
                    break;
                }
            }
            if (found)
                return cursorAt - search.length();
        }
        return -1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            
            public boolean onQueryTextSubmit(String arg0) {
                if (findHighlightSpan != null)
                    text.getText().removeSpan(findHighlightSpan);
                text.requestFocus();
                return false;
            }
            
            public boolean onQueryTextChange(String arg0) {
                if (findHighlightSpan != null)
                    text.getText().removeSpan(findHighlightSpan);
                
                if (arg0.length() == 0)
                    return false;
                
                int loc = search(arg0);
                Log.d("search", loc + "");
                if (loc == -1)
                    notFound.show();
                else {
                    findHighlightSpan = new BackgroundColorSpan(Color.RED);
                    text.getText().setSpan(findHighlightSpan, loc, loc + arg0.length(), 0);
                    text.setSelection(loc);
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    
    public void showStats() {
        statsToast.setText("Lines: " + text.getLineCount() + "\nWords: " + text.getWordCount() + "\nCharacters: " + text.getText().length());
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
                    
                    
                    public void onAnimationStart(Animator arg0) {}
                    
                    public void onAnimationRepeat(Animator arg0) {}
                    
                    public void onAnimationEnd(Animator arg0) {
                        helpHints.setVisibility(View.GONE);
                    }
                    
                    public void onAnimationCancel(Animator arg0) {}
                });
                anim.start();
                return true;
            }
        });
        helpHintHide.sendEmptyMessageDelayed(0, 5000);
    }
    
    
}