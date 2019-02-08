package com.example.myapplication;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodSubtype;


public class EDMTkeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener{

    private KeyboardView inputView;
    private Keyboard sinhalaKeyboard;//keyboard1 when caps off

    private Keyboard sinhalaKeyboardShift; //keyboard1 when caps on

   // public static String activeKeyboard;
    //private Keyboard curKeyboard;



    @Override
    public void onInitializeInterface(){
        sinhalaKeyboard=new Keyboard(this,R.xml.keyboard1);
        sinhalaKeyboardShift=new Keyboard(this,R.xml.keyboard2);
        //making new keyboard1
    }


    @Override
    public View onCreateInputView() {
        onInitializeInterface();
        inputView=(KeyboardView)getLayoutInflater().inflate(R.layout.keyboardlayout,null);
        //instantiate a layout XML file into its corresponding view object
        inputView.setKeyboard(sinhalaKeyboard);

        inputView.setOnKeyboardActionListener(this);

     return inputView;
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
        //playClick(primaryCode);//if want to add sounds
        //switch case statement
        switch(primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1, 0);
                break;//we delete only one character at a time
            case Keyboard.KEYCODE_SHIFT:
                inputView.invalidateAllKeys();//Requests a redraw of the entire keyboard1.
                handleShift();
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                //KeyEvent(int action, int code(relevant code))
                break;
            default:
                char code = (char) primaryCode;
                //if (Character.isLetter(code) && isCaps)
                 // code = Character.toUpperCase(code);//set letter to upper case
                ic.commitText(String.valueOf(code), 1);
        }
    }
    private void handleShift(){


        Keyboard currentKeyboard=inputView.getKeyboard();
        if (currentKeyboard==sinhalaKeyboard){
            sinhalaKeyboard.setShifted(true);
            inputView.setKeyboard(sinhalaKeyboardShift);
        }else{
            sinhalaKeyboardShift.setShifted(true);
            inputView.setKeyboard(sinhalaKeyboard);
        }
    }

    /**
     * Switch to language when it is changed from Choose Input Method.

    @Override
    public void onCurrentInputMethodSubtypeChanged(InputMethodSubtype subtype) {
       // inputView.setSubtypeOnSpaceKey(subtype);
        String s = subtype.getLocale();
        switch (s) {
            case "Sinhala":
                activeKeyboard = "Sinhala";
                curKeyboard = sinhalaKeyboard;
                break;
            case "SinhalaShift":
                activeKeyboard = "SinhalaShift";
                curKeyboard = sinhalaKeyboardShift;
                break;

        }

        // Apply the selected keyboard to the input view.
        inputView.setKeyboard(curKeyboard);
    }


*/


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
