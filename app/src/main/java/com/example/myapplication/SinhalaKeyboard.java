package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.text.method.MetaKeyKeyListener;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CursorAnchorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dictionary.Dictionary;


public class SinhalaKeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener{

    private KeyboardView inputView;
    private Keyboard sinhalaKeyboard;//keyboard1 when caps off
    private Keyboard symbolKeyboard;// symbol keyboard
    private Keyboard symbolShiftKeyboard; //symbol shift keyboard
    private Keyboard sinhalaKeyboardShift; //keyboard1 when caps on
    private String  type="language";// to keep a track of type whether it is symbol or language
    private boolean isCap=false;
    private Dictionary dictionary;
    private int lengthBeforeCursor=0;
    private int lengthAfterCursor=0;
    private int position;
    private Keyboard currentKeyboard;

    /**keyboard  **/

    private InputMethodManager mInputMethodManager;
    private Candidate_view mCandidateView;
    private CompletionInfo[] mCompletions;
    //  private WordProcessor wordProcessor= new WordProcessor();
    private StringBuilder mComposing = new StringBuilder();
    private boolean mPredictionOn=true;
    private boolean mCompletionOn;
    private boolean mSound;
    private int mLastDisplayWidth;
    private boolean mCapsLock;
    private long mLastShiftTime;
    private long mMetaState;
    private String mWordSeparators;
    private ArrayList<String> list ;
    private String typedWord="";
    private boolean isWordValid=true;
    private boolean isSuggetionListEmplty;
    private ArrayList<String> suggestedList;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    private Button button;
    private boolean isPressedUndo;
    private boolean isChosenWord=false;
    private boolean isScreenTouch;
    private int redundantLetter;
    private int[] redundancyList=new int[]{3540,3536,3530,3538,3535,3539,3571,3537,3542};
    private static ExtractedText text;
    private String wrongWord;

//    private PopUp popUp;


    /**keyboard  **/

    public void onInitialize() {
        sinhalaKeyboard = new Keyboard(this, R.xml.keyboard1);
        sinhalaKeyboardShift = new Keyboard(this, R.xml.keyboard2);
        symbolKeyboard = new Keyboard(this, R.xml.simbol);
        symbolShiftKeyboard = new Keyboard(this, R.xml.simbolshift);
        list = new ArrayList<String>();
        dictionary = new Dictionary();
        suggestedList = new ArrayList<String>();




    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public View onCreateInputView() {
        onInitialize();
        inputView=(KeyboardView)getLayoutInflater().inflate(R.layout.keyboardlayout,null);
        //instantiate a layout XML file into its corresponding view object
        inputView.setKeyboard(sinhalaKeyboard);
        inputView.setOnKeyboardActionListener(this);




        return inputView;
    }


    @Override
    public void onRelease(int primaryCode){
        //Called when the user releases a key.
    }


    /**
     * Called by the framework when your view for showing candidates needs to
     * be generated, like {@link #onCreateInputView}.
     **/
    /** keyboard test**/

    @Override
    public View onCreateCandidatesView() {
        mCandidateView = new Candidate_view(this);
        mCandidateView.setService(this);
        setCandidatesView(mCandidateView);
        setCandidatesViewShown(true);
        return mCandidateView;
    }


    /**
     * This tells us about completions that the editor has determined based
     * on the current text in it.  We want to use this in fullscreen mode
     * to show the completions ourselves, since the editor can not be seen
     * in that situation.
     */
    @Override
    public void onDisplayCompletions(CompletionInfo[] completions) {
        if (mCompletionOn) {
            mCompletions = completions;
            if (completions == null) {
                setSuggestions(null, false, false);
                return;
            }

            List<String> stringList = new ArrayList<>();
            for (CompletionInfo ci : completions) {
                if (ci != null) stringList.add(ci.getText().toString());
            }
            setSuggestions(stringList, true, true);
        }

    }



    public void setSuggestions(List<String> suggestions, boolean completions,
                               boolean typedWordValid) {
        if (suggestions != null && suggestions.size() > 0) {

        } else if (isExtractViewShown()) {
            setCandidatesViewShown(true);
        }
        if (mCandidateView!= null) {

            mCandidateView.setSuggestions(suggestions, completions, typedWordValid);
        }
    }
    void removeCurrentWord(){
        getCurrentInputConnection().deleteSurroundingText(lengthBeforeCursor, lengthAfterCursor);


    }

    // Tap on suggestion to commit
    public void pickSuggestionManually(int index) {
        getCurrentDetails();
        removeCurrentWord();

        if (typedWord.length() > 0) {
            String selectedWord = list.get(index);
            typedWord=selectedWord;
            getCurrentInputConnection().commitText(typedWord , 1);
            isChosenWord=true;

        }
    }



    public void onPress(int primaryCode) {
        inputView.setPreviewEnabled(true);

        // Disable preview key on Shift, Delete, Space, Language, Symbol and Emoticon.
        if (primaryCode == -1 || primaryCode == -5 || primaryCode == -2 || primaryCode == -10000
                || primaryCode == -101 || primaryCode == 32) {
            inputView.setPreviewEnabled(false);
        }
    }
    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private void keyDownUp(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    /**
     * Helper to send a character to the editor as raw key events.
     */
    private void sendKey(int keyCode) {
        switch (keyCode) {
            case '\n':
                keyDownUp(KeyEvent.KEYCODE_ENTER);
                break;
            default:
                if (keyCode >= '0' && keyCode <= '9') {
                    keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0);
                } else {
                    getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);
                }
                break;
        }
    }

    /**
     * Helper function to commit any text being composed in to the editor.
     */


    private String getWordSeparators() {
        mWordSeparators="!1234567890 @#$%&*-=()!\"':;/?«~±×÷•°`´{}©£€^®¥_+[]¡<>¢|\\¿»";
        return mWordSeparators;
    }

    public boolean isWordSeparator(int code) {
        String separators = getWordSeparators();
        return separators.contains(String.valueOf((char)code));
    }

    /**
     * Deal with the editor reporting movement of its cursor.
     */
    @Override
    public void onUpdateSelection(int oldSelStart, int oldSelEnd,
                                  int newSelStart, int newSelEnd,
                                  int candidatesStart, int candidatesEnd) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
                candidatesStart, candidatesEnd);

        // If the current selection in the text view changes, we should
        // clear whatever candidate text we have.
        if (mComposing.length() > 0 && (newSelStart != candidatesEnd
                || newSelEnd != candidatesEnd)) {
            mComposing.setLength(0);


            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                ic.finishComposingText();
            }

        }
    //    System.out.println("onUpdateSelection ");
     //   mComposing.length();
        getCurrentDetails();
        updateCandidates();
    }
    public void getCurrentDetails(){
        String leftPart="";
        String rigthPart="" ;
//        int countOfSpace;
//        CharSequence part;
        String[] array;
        String leftLastWord="";
        String rightFirstWord="";
        StringBuilder currentWord = new StringBuilder("");
        int beforeLength = 0;
        try {
        text = getCurrentInputConnection().getExtractedText(new ExtractedTextRequest(), 0);
        }
        catch(NullPointerException e){System.out.println(e);return ;}
        if(text==null){return;}
        position = text.selectionStart;
        CharSequence enteredText = text.text.toString();
 //       System.out.println("position "+position);
 //       System.out.println("entered text "+enteredText.length());
        rigthPart=((String) enteredText).substring(position,enteredText.length());
        if(position!=0){
            leftPart = ((String) enteredText).substring(0, position).replaceAll("\\n", " ");
            array = leftPart.split(" ");
            if(!(array.length==0)){ leftLastWord=array[array.length-1];}
        }
        if(!(enteredText.length()==position)){
            array =rigthPart.replaceAll("\\n", " ").split(" ");
            if(!(array.length==0)){ rightFirstWord=array[0];}
//            System.out.println("position "+position);
//            System.out.println("entered text "+enteredText.length());
//            System.out.println("rigthPart length"+rigthPart.length());
          //  rightFirstWord=rigthPart.replaceAll("\\n", " ").split(" ")[0];
        }
        beforeLength=position;
        if(!leftPart.endsWith(" ") && !rigthPart.startsWith(" ")){
//            System.out.println("part 1");
//            System.out.println("left part "+rightFirstWord);
            beforeLength-=leftLastWord.length();
            currentWord=new StringBuilder(leftLastWord.concat(rightFirstWord));

        }
        else if(leftPart.endsWith(" ") && !rigthPart.startsWith(" ")){
//            System.out.println("part 2");
            currentWord=new StringBuilder(rightFirstWord);

        }
        lengthBeforeCursor=position-beforeLength;
        lengthAfterCursor=beforeLength+currentWord.length()-position;
        if(!leftPart.endsWith(" ") && rigthPart.startsWith(" ")){
            currentWord=new StringBuilder(leftLastWord);
            lengthAfterCursor=position-beforeLength;
            lengthBeforeCursor=beforeLength+currentWord.length()-position;
        }
        typedWord=currentWord.toString();
//        System.out.println("current word "+currentWord.toString());
//        System.out.println("beforeLength "+beforeLength);
//        System.out.println("rightFirstWord "+rightFirstWord);
//        System.out.println("leftFirstWord "+leftLastWord);
//        System.out.println("curren word length "+currentWord.length());
//        System.out.println("lenght Berofecursor "+lengthBeforeCursor);
//        System.out.println("lenght afterfecursor "+lengthAfterCursor);


    }
    @Override
    public void onWindowShown(){
  //      System.out.println("on Shown");
        currentKeyboard=inputView.getKeyboard();
        if(currentKeyboard!=sinhalaKeyboard){
            inputView.setKeyboard(sinhalaKeyboard);
         //   System.out.println(" current keyboard "+currentKeyboard.toString());
            currentKeyboard.setShifted(false);

        }
        getCurrentDetails();
        updateCandidates();



    }
    @Override
    public void onWindowHidden(){
  //      text.text="";
          typedWord="";
//        updateCandidates();

     //   System.out.println("on hidden");

    }
    private boolean reduceRedundancy(int primaryCode){
        if(redundantLetter==primaryCode){
            for(int i : redundancyList){
                if(i==primaryCode){
                    return true;
                }
            }
        }
        redundantLetter=primaryCode;
        return false;
    }

    private void handleCharacter(int primaryCode, int[] keyCodes) {
        if(reduceRedundancy(primaryCode)){return;}
        mComposing.append((char) primaryCode);
        getCurrentInputConnection().commitText(mComposing, 1);
        if (isWordSeparator(primaryCode)) {
            typedWord="";
            getCurrentInputConnection().finishComposingText();
            mComposing.setLength(0);
        }
        mCandidateView.clear();
        updateCandidates();
        getCurrentDetails();
    }




    /**
     * Update the list of available candidates from the current composing
     * text.  This will need to be filled in by however you are determining
     * candidates.
     */
    public void updateCandidates() {
        wrongWord="";
        if(!(list==null)) {// other wise we cannot clear the Arraylist
            list.clear();// clear the entire word list
        }

        if (!mCompletionOn) {
            InputStream inputStream;
            try{
                if(!typedWord.equals("")) {
                }
                if (typedWord.length() > 0 && !typedWord.equals("")) {
                    inputStream=getAssets().open(typedWord.substring(0,1)+".txt");

                    suggestedList.clear();

                    suggestedList=dictionary.getWordList(typedWord,inputStream,this);//error

                    if(!suggestedList.isEmpty()){
                        if(!dictionary.isWordCorrect(typedWord) && typedWord.length()>1 ){
                            wrongWord='"'+typedWord+'"';
                            list.add(wrongWord);
                        }else
                        { list.add(typedWord);
                        }if(suggestedList.contains(typedWord)) {
                            suggestedList.remove(typedWord);

                        }
                        list.addAll(suggestedList);

                        isSuggetionListEmplty=false;

                    }else if(typedWord.length()>1){
                        list.add('"'+typedWord+'"');
                        isSuggetionListEmplty=true;

                    }else{
                        list.add(typedWord);
                    }

                    // System.out.println(this.list.toString());

                    setSuggestions(list, true, true);

                } else {
                    setSuggestions(null, false, false);
                }

            }
            catch(NullPointerException e){
                //    System.out.println(e);
                Log.d("update candidate",e.getMessage());
            }catch(IOException e){
                typedWord="";
                updateCandidates();
                Log.d("update candidate",e.getMessage());
            }


        }
    }
    public void writeNewWord(String word){
        dictionary.writeNewWord(word);

    }
    private void handleBackspace() {
        if(mComposing.length()>0) {
            mComposing.deleteCharAt(mComposing.length()-1);
            typedWord = mComposing.toString();
            getCurrentInputConnection().setComposingText(mComposing, 1);
        }else{
            getCurrentInputConnection().deleteSurroundingText(1,0);// delete surrounding text
        }
        if(position==0){return;}
        getCurrentDetails();
        updateCandidates();
        redundantLetter=0;
    }
    /**
     * This is called when the user is done editing a field.  We can use
     * this to reset our state.
     */

    @Override public void onFinishInput() {
        //  System.out.println("this is working");
        super.onFinishInput();

        // Clear current composing text and candidates.
        mComposing.setLength(0);
        updateCandidates();

    }
    /** keyboard test**/
    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic=getCurrentInputConnection();
        switch(primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                handleBackspace();
                break;//we delete only one character at a time
            case Keyboard.KEYCODE_SHIFT:
                inputView.invalidateAllKeys();//Requests a redraw of the entire keyboard1.
                handleShift();
                break;
            case Keyboard.KEYCODE_MODE_CHANGE:
                inputView.invalidateAllKeys();
                switchTo();
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                //KeyEvent(int action, int code(relevant code))
                break;
            default:
                handleCharacter(primaryCode, keyCodes);
        }
    }
    private void handleShift(){// handle the shift key of the keybaord


        currentKeyboard=inputView.getKeyboard();
        if(type.equals("language")){
            if (currentKeyboard==sinhalaKeyboard){

                sinhalaKeyboard.setShifted(true);
                sinhalaKeyboardShift.setShifted(false);
                inputView.setKeyboard(sinhalaKeyboardShift);
                isCap=true;
            }else{
                sinhalaKeyboardShift.setShifted(true);
                sinhalaKeyboard.setShifted(false);
                inputView.setKeyboard(sinhalaKeyboard);
                isCap=false;
            }
        }

        else if(type.equals("symbol")){
            if (currentKeyboard==symbolKeyboard){
                symbolKeyboard.setShifted(true);
                symbolShiftKeyboard.setShifted(false);
                inputView.setKeyboard(symbolShiftKeyboard);
                isCap=true;
            }else{
                symbolShiftKeyboard.setShifted(true);
                symbolKeyboard.setShifted(false);
                inputView.setKeyboard(symbolKeyboard);
                isCap=false;


            }
        }
    }
    /**
     * this method change the type of the keyboard **/


    private void switchTo(){
        if(type.equals("language")) {
            type="symbol";
            if (!isCap) {
                inputView.setKeyboard(symbolKeyboard); }
            else{
                inputView.setKeyboard(symbolShiftKeyboard);
            }
        }else{
            type="language";
            if (!isCap) {
                inputView.setKeyboard(sinhalaKeyboard); }
            else{
                inputView.setKeyboard(sinhalaKeyboardShift);
            }
        }
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

    public ArrayList<String> getList() {
        return list;
    }

    public String getTypedWord() {
        return typedWord;
    }

    public String getWrongWord() {
        return wrongWord;
    }
}