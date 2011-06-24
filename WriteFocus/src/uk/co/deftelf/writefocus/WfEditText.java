package uk.co.deftelf.writefocus;


import java.util.ArrayDeque;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;

public class WfEditText extends EditText {
    
    private static final int STACK_MAX_SIZE = 20;
    
    private Main parent;
    
    private ClipboardManager clipBoard;
    
    boolean suppressUndo;
    private boolean hasChanged = false;
    private ArrayDeque<Undo> undoHistory = new ArrayDeque<Undo>();
    
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
        clipBoard = (ClipboardManager) parent.getSystemService(Activity.CLIPBOARD_SERVICE);
    }

    public void undo() {
        if (undoHistory.isEmpty())
            return;
        suppressUndo = true;
        Undo undo = undoHistory.pop();
        getText().replace(undo.start, undo.after, undo.oldText);
        suppressUndo = false;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("key", event.getAction() + " " + event.getKeyCode() + " " + event.getCharacters());
            
        if (keyCode == KeyEvent.KEYCODE_CTRL_RIGHT)
            parent.showHelpHint();
        
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            //TODO: remove highlights
        }
    }
    
    @Override
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        Log.d("keyshort", event.getAction() + " " + event.getKeyCode() + " " + event.getCharacters());
        if (keyCode == KeyEvent.KEYCODE_L)
            parent.showStats();
        else if (keyCode == KeyEvent.KEYCODE_C) {
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
        }
        
        return super.onKeyShortcut(keyCode, event);
    }
    
    @Override
    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        super.onTextChanged(text, start, before, after);
        
        if (undoHistory == null)
            suppressUndo = true;
        
        if (!suppressUndo) {
            hasChanged = true;
            CharSequence old = text.subSequence(start, before + start);
            undoHistory.push(new Undo(start, after + start, old));
            if (undoHistory.size() > STACK_MAX_SIZE)
                undoHistory.removeLast();
        }
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
    

    
}
