package uk.co.deftelf.writefocus;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;

public class WfEditText extends EditText {
    
    private Main parent;
    
    private ClipboardManager clipBoard;

    public WfEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public WfEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public WfEditText(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    
    public void init(Main parent) {
        this.parent = parent;
        clipBoard = (ClipboardManager) parent.getSystemService(Activity.CLIPBOARD_SERVICE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("key", event.getAction() + " " + event.getKeyCode() + " " + event.getCharacters());
        
        
            
        if (keyCode == KeyEvent.KEYCODE_CTRL_RIGHT)
            parent.showHelpHint();
        
        return super.onKeyDown(keyCode, event);
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
        }
        return super.onKeyShortcut(keyCode, event);
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
    
}
