package uk.co.deftelf.writefocus;


import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayDeque;

import com.tokaracamara.android.verticalslidevar.VerticalSeekBar;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class WfEditText extends EditText {
    
    private static final int STACK_MAX_SIZE = 20;
    
    private Main parent;
    
    private ClipboardManager clipBoard;
    
    boolean suppressUndo;
    boolean hasChanged = false;
    private SoftReference<ArrayDeque<Undo>> undoHistory;

    private Object findHighlightSpan;
//    private boolean scrollAuto = false;
    private ImageView scrollThumb;
    
    private class Undo {
        int start;
        int after;
        CharSequence oldText;
        public Undo(int start, int after, CharSequence oldText) {
            super();
            this.start = start;
            this.after = after;
            this.oldText = oldText;
        }
    }
    

    public WfEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public WfEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WfEditText(Context context) {
        super(context);
    }
    
    
    public void init(Main parent) {
        this.parent = parent;

        scrollThumb = (ImageView) findViewById(R.id.scrollThumb);
        clipBoard = (ClipboardManager) parent.getSystemService(Activity.CLIPBOARD_SERVICE);
        suppressUndo = true;
        addTextChangedListener(new TextWatcher() {
            
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (undoHistory == null)
                    suppressUndo = true;

                hasChanged = true;
                
                if (!suppressUndo) {
                    CharSequence old = s.subSequence(start, start + count);
                    
                    ArrayDeque<Undo> undoHistoryActual = undoHistory.get();
                    if (undoHistoryActual == null) {
                        undoHistoryActual = new ArrayDeque<Undo>();
                        undoHistory = new SoftReference<ArrayDeque<Undo>>(undoHistoryActual);
                        Toast.makeText(getContext(), R.string.undo_history_cleared, Toast.LENGTH_LONG).show();
                    }
                    undoHistoryActual.push(new Undo(start, start + after, old));
                    if (undoHistoryActual.size() > STACK_MAX_SIZE)
                        undoHistoryActual.removeLast();
                    
                }
            }
            
            public void afterTextChanged(Editable s) {
                
//                if (getLineCount() > (getHeight() / getLineHeight())) {
//                    scrollThumb.setVisibility(VISIBLE);
//                    int maxLines = getLineCount() - (getHeight() / getLineHeight());
//                    int curLine = getScrollY() / getLineHeight();
//                    ((LinearLayout.LayoutParams)scrollThumb.getLayoutParams()).topMargin = (int) ((((float)curLine) / maxLines) * getHeight());
//                } else {
//                    scrollThumb.setVisibility(INVISIBLE);
//                }
            }
        });
        
        
        undoHistory = new SoftReference<ArrayDeque<Undo>>(new ArrayDeque<Undo>());
    }

    public void undo() {
        ArrayDeque<Undo> undoHistoryActual = undoHistory.get();
        if (undoHistoryActual == null) {
            undoHistoryActual = new ArrayDeque<Undo>();
            undoHistory = new SoftReference<ArrayDeque<Undo>>(undoHistoryActual);
            Toast.makeText(getContext(), R.string.undo_history_cleared, Toast.LENGTH_LONG).show();
        }
        if (undoHistoryActual.isEmpty())
            return;
        suppressUndo = true;
        Undo undo = undoHistoryActual.pop();
        getText().replace(undo.start, undo.after, undo.oldText);
        suppressUndo = false;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("key", event.getAction() + " " + event.getKeyCode() + " " + event.getCharacters());
            
        if (keyCode == KeyEvent.KEYCODE_CTRL_RIGHT ||
                keyCode == KeyEvent.KEYCODE_CTRL_LEFT)
            parent.showHelpHint();
        
        if (event.hasModifiers(KeyEvent.META_CTRL_ON)) {
            if (keyCode == KeyEvent.KEYCODE_L) {
                parent.showStats();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_C) {
                copy();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_V) {
                paste();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_X) {
                cut();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_Z) {
                undo();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_F) {
                parent.searchView.setIconified(false);
                parent.searchView.requestFocus();
                return true;
            }
        }
        
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            if (findHighlightSpan != null)
                getText().removeSpan(findHighlightSpan);
        }
    }
    
    public void findHighlight(int from, int to) {
        if (findHighlightSpan != null)
            getText().removeSpan(findHighlightSpan);
        findHighlightSpan = new BackgroundColorSpan(Color.RED);
        getText().setSpan(findHighlightSpan, from, to, 0);
    }
    
    
    public void copy() {
        CharSequence textContent = getText().subSequence(Math.min(getSelectionStart(), getSelectionEnd()), Math.max(getSelectionStart(), getSelectionEnd()));
        clipBoard.setPrimaryClip(ClipData.newPlainText("test", textContent));
    }
    
    public void cut() {
        copy();
        getText().replace(Math.min(getSelectionStart(), getSelectionEnd()), Math.max(getSelectionStart(), getSelectionEnd()), "");
    }
    
    public void paste() {
        ClipData data = clipBoard.getPrimaryClip();
        if (data != null) {
            CharSequence text = data.getItemAt(0).getText();
            getText().replace(getSelectionEnd(), getSelectionEnd(), text, 0, text.length());
        }
    }
    

    public int search(CharSequence search) {
        if (search.length() == 0)
            return -1;
        CharSequence contents = getText();
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

    public int getWordCount() {
        int wc = 0;
        CharSequence ed = getText();
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
    
    
    
    
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        
        parent.updateScroll(t);
    }
    

    
}
