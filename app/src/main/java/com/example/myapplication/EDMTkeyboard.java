package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

public class EDMTkeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView kv;
    private Keyboard keyboard;
    private boolean isCaps=false;

    @Override
    public View onCreateInputView() {
        kv=(KeyboardView)getLayoutInflater().inflate(R.layout.keyboardlayout,null);
        //instantiate a layout XML file into its corresponding view object
        keyboard=new Keyboard(this,R.xml.keyboard);
        //making new keyboard
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);
        //connect to this class reference
        return kv;

    }

    @Override

    public void onPress(int primaryCode) {
    //called when the user pressed a key

    }

    @Override
    public void onRelease(int primaryCode){
    //Called when the user releases a key.
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
    //send a key press to listener
        InputConnection ic=getCurrentInputConnection();
        //Retrieve the currently active InputConnection that is bound to the input method, or null if there is none.
        playClick(primaryCode);//if want to add sounds
        //switch case statement
        switch(primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1, 0);
                break;//we delete only one character at a time
            case Keyboard.KEYCODE_SHIFT:
                isCaps = !isCaps;
                keyboard.setShifted(isCaps);
                kv.invalidateAllKeys();//Requests a redraw of the entire keyboard.
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                //KeyEvent(int action, int code(relevant code))
                break;
            default:
                char code = (char) primaryCode;
                if (Character.isLetter(code) && isCaps)
                    code = Character.toUpperCase(code);//set letter to upper case
                ic.commitText(String.valueOf(code), 1);
        }



    }




    private void playClick(int primaryCode) {
        //add sound
    }

    @Override
    public void onText(CharSequence text) {
    //sends a sequence of characters to the listener
    }

    @Override
    public void swipeLeft() {
    //Called when the user quickly moves the finger from right to left
    }

    @Override
    public void swipeRight() {
    //Called when the user quickly moves the finger from right to left
    }

    @Override
    public void swipeDown() {
    //Called when the user quickly moves the finger from up to down
    }

    @Override
    public void swipeUp() {
    //Called when the user quickly moves the finger from down to up.
    }
}
