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


public class
SinhalaKeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener{

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

    private Candidate_view mCandidateView;
    private StringBuilder mComposing = new StringBuilder();
    private String wordSeparators;
    private ArrayList<String> list ;
    private String typedWord="";
    private boolean isSuggetionListEmplty;
    private ArrayList<String> suggestedList;
    private CoordinatorLayout coordinatorLayout;
    private Button button;
    private boolean isPressedUndo;
    private boolean isChosenWord=false;
    private boolean isScreenTouch;
    private int redundantLetter;
    private int[] redundancyList=new int[]{3540,3536,3530,3538,3535,3539,3571,3537,3542};
    private static ExtractedText text;
    private String wrongWord="";



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



    @Override
    public View onCreateCandidatesView() { // to create candidate view
        mCandidateView = new Candidate_view(this);
        mCandidateView.setService(this);
        setCandidatesView(mCandidateView);
        setCandidatesViewShown(true);
        return mCandidateView;
    }

    public void setSuggestions(List<String> suggestions, boolean completions,
                               boolean typedWordValid) {
        if (mCandidateView!= null) {

            mCandidateView.setSuggestions(suggestions, completions, typedWordValid);
        }
    }
    public void removeCurrentWord(){ // remove current word from the text field
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


    @Override
    public void onPress(int primaryCode) {// over write method

        inputView.setPreviewEnabled(true);

        // Disable preview key on Shift, Delete, Space, Language, Symbol and Emoticon.
        if (primaryCode == -1 || primaryCode == -5 || primaryCode == -2 || primaryCode == -10000
                || primaryCode == -101 || primaryCode == 32) {
            inputView.setPreviewEnabled(false);
        }
    }


    private String getWordSeparators() {
        wordSeparators="!1234567890 @#$%&*-=()!\"':;/?«~±×÷•°`´{}©£€^®¥_+[]¡<>¢|\\¿».,";
        return wordSeparators;
    }

    public boolean isWordSeparator(int code) { // check whether a letter is a word separator
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
            //       System.out.println("on update selection "+mComposing.length());
            mComposing.setLength(0);// when cursor moves we should set


            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                ic.finishComposingText();
            }

        }

        getCurrentDetails();
        updateCandidates();
    }
    public void getCurrentDetails(){// this method used to get the current detail about the text
        String leftPart=""; // left part before the cursor
        String rigthPart="" ;// right part after the cursor
        String[] array;
        String leftLastWord="";
        String rightFirstWord="";
        StringBuilder currentWord = new StringBuilder("");// current word on which cursor is
        int beforeLength = 0;// distance to cursor position from the start of the text area
        try {
            text = getCurrentInputConnection().getExtractedText(new ExtractedTextRequest(), 0);//  extract current text area
        }
        catch(NullPointerException e){System.out.println(e);return ;}
        if(text==null){typedWord="";return;}
        position = text.selectionStart;// calculate the cursor position

        CharSequence enteredText = text.text.toString();
        rigthPart=((String) enteredText).substring(position,enteredText.length());
        if(position!=0){
            leftPart = ((String) enteredText).substring(0, position).replaceAll("\\n", " ");
            array = leftPart.split(" ");
            if(!(array.length==0)){ leftLastWord=array[array.length-1];}
        }
        if(!(enteredText.length()==position)){
            array =rigthPart.replaceAll("\\n", " ").split(" ");
            if(!(array.length==0)){ rightFirstWord=array[0];}
        }
        beforeLength=position;
        if(!leftPart.endsWith(" ") && !rigthPart.startsWith(" ")){
            beforeLength-=leftLastWord.length();
            currentWord=new StringBuilder(leftLastWord.concat(rightFirstWord));

        }
        else if(leftPart.endsWith(" ") && !rigthPart.startsWith(" ")){
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



    }
    @Override
    public void onWindowShown(){  // this method is called when keyboard appears after hiddenning
        currentKeyboard=inputView.getKeyboard();
        if(currentKeyboard!=sinhalaKeyboard){
            inputView.setKeyboard(sinhalaKeyboard);
            currentKeyboard.setShifted(false);

        }
        getCurrentDetails();
        updateCandidates();



    }
    @Override
    public void onWindowHidden(){ // this method is called when home button is pressed
        typedWord="";


    }
    public boolean reduceRedundancy(int primaryCode){
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

    private void handleCharacter(int primaryCode, int[] keyCodes) { // handle input given by user
        if(reduceRedundancy(primaryCode)){return;}// redeuce reducndancy
        mComposing.append((char) primaryCode);// create string builder
        getCurrentInputConnection().commitText(mComposing, 1);
        if (isWordSeparator(primaryCode)) {
            typedWord="";
            getCurrentInputConnection().finishComposingText();
            mComposing.setLength(0);// set composing text's length zero
        }
        mCandidateView.clear();
        updateCandidates();
        getCurrentDetails();
    }





    public ArrayList<String> getWordListFromArticle(String currentWord){
        try {
            InputStream inputStream;
            inputStream = getAssets().open(currentWord.substring(0, 1) + ".txt");
            suggestedList=dictionary.getWordList(currentWord,inputStream,this);
        }
        catch(NullPointerException e){

            Log.d("update candidate",e.getMessage());

        }catch(IOException e){

            typedWord="";
            updateCandidates();
            Log.d("update candidate",e.getMessage());

        }
        return  suggestedList;
    }
    public boolean updateCandidates() {
        String currentWord=typedWord;

        if(!(list==null)) {// other wise we cannot clear the Arraylist
            list.clear();// clear the entire word list

        }


        if(!currentWord.equals("")) {
        }
        if (currentWord.length() > 0 && !currentWord.equals("")) {


            suggestedList.clear();

            suggestedList=getWordListFromArticle(currentWord);

            if(!suggestedList.isEmpty()){
                if(!dictionary.isWordCorrect(currentWord) && currentWord.length()>1 ){
                    wrongWord='"'+currentWord+'"';
                    list.add(wrongWord);
                }else
                { list.add(currentWord);
                }if(suggestedList.contains(currentWord)) {
                    suggestedList.remove(currentWord);

                }
                list.addAll(suggestedList);

                isSuggetionListEmplty=false;

            }else if(currentWord.length()>1){
                list.add('"'+currentWord+'"');
                isSuggetionListEmplty=true;

            }else{
                list.add(currentWord);
            }



            setSuggestions(list, true, true);

        } else {
            setSuggestions(null, false, false);

        }




        return true;

    }
    public void writeNewWord(String word){ // write new word on dictionary
        dictionary.writeNewWord(word);

    }
    public void handleBackspace() {

        getCurrentInputConnection().deleteSurroundingText(1,0);// delete surrounding text

        if(position==0){return;}
        getCurrentDetails();
        updateCandidates();
        redundantLetter=0;// redundancy letter as zero
    }
    /**
     * This is called when the user is done editing a field.  We can use
     * this to reset our state.
     */

    @Override public void onFinishInput() {
        super.onFinishInput();

        // Clear current composing text and candidates.
        mComposing.setLength(0);
        updateCandidates();

    }
    /** keyboard test**/
    @Override
    public void onKey(int primaryCode, int[] keyCodes) { // when user press on a key this the method come into play
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
    public void handleShift(){// handle the shift key of the keybaord


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


    private void switchTo(){ // keyboard switch language to symbol and vise versa
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



    public String getTypedWord() {
        return typedWord;
    }

    public String getWrongWord() {
        return list.get(0);
    }

}