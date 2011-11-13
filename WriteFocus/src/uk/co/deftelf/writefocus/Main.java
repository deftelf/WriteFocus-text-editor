package uk.co.deftelf.writefocus;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.AccelerateInterpolator;
import android.widget.Scroller;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {
    
    private static final String MEASURE_TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce massa nunc. mmmmmm";
    String FILENAME = "data.txt";

    private WfEditText text;
    private TextView helpHints;
    private Toast statsToast;
    private Handler helpHintHide;
    private Toast notFound;
    SearchView searchView;
    
    
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
            //InputStream fs = getAssets().open("pg2600.txt");
            InputStream fs = openFileInput(FILENAME);
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
    
    @Override
    protected void onPause() {
        super.onPause();
        if (text.hasChanged) {
            try {
                FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                for (int i=0; i < text.getText().length(); i++)
                    fos.write(text.getText().charAt(i));
                fos.close();
                text.hasChanged = false;
                Toast.makeText(this, R.string.text_saved, Toast.LENGTH_SHORT).show();
            } catch (IOException ex) {
                Toast.makeText(this, R.string.failed_to_save_text, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar, menu);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            
            public boolean onQueryTextSubmit(String arg0) {
                text.requestFocus();
                searchView.setIconified(true);
                return false;
            }
            
            public boolean onQueryTextChange(String arg0) {
                search(arg0);
                return false;
            }
        });
        searchView.setOnFocusChangeListener(new OnFocusChangeListener() {
            
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    search(searchView.getQuery());
                } else {
                    searchView.setIconified(true);
                }
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    
    public void search(CharSequence str) {
        if (str.length() == 0)
            return;
        
        int loc = text.search(str);
        Log.d("search", loc + "");
        if (loc == -1)
            notFound.show();
        else {
            text.findHighlight(loc, loc + str.length());
            text.setSelection(loc);
        }
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