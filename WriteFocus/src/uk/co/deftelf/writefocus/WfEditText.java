package uk.co.deftelf.writefocus;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;

public class WfEditText extends EditText {
    
    Main parent;

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("key", event.getAction() + " " + event.getKeyCode() + " " + event.getCharacters());
        if (keyCode == KeyEvent.KEYCODE_L)
            parent.showStats();
        if (keyCode == KeyEvent.KEYCODE_CTRL_RIGHT)
            parent.showHelpHint();
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        Log.d("keyshort", event.getAction() + " " + event.getKeyCode() + " " + event.getCharacters());
        return super.onKeyShortcut(keyCode, event);
    }
    
}
