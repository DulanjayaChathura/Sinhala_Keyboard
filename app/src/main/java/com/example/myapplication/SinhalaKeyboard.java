package com.example.myapplication;

import android.content.res.AssetManager;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.method.MetaKeyKeyListener;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.textservice.TextInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class SinhalaKeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener{

    private KeyboardView inputView;
    private Keyboard sinhalaKeyboard;//keyboard1 when caps off
    private Keyboard symbolKeyboard;// symbol keyboard
    private Keyboard symbolShiftKeyboard; //symbol shift keyboard
    private Keyboard sinhalaKeyboardShift; //keyboard1 when caps on
    private String  type="language";// to keep a track of type whether it is symbol or language
    private boolean isCap=false;



    /**keyboard  **/

    private InputMethodManager mInputMethodManager;
    private Candidate_view mCandidateView;
    private CompletionInfo[] mCompletions;
    private WordProcessor wordProcessor= new WordProcessor();
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





    /**keyboard  **/



   // public static String activeKeyboard;
    //private Keyboard curKeyboard;

    private void loadDictionary(String startingLetter){

        try {
//            ArrayList<String> file= new ArrayList<String>();
//            file.add("article1rewrite.txt");
//            file.add("article2rewrite.txt");

           // for (int i = 0 ; i< file.size(); i++) {
                AssetManager assetManager = getAssets();
                InputStream is = assetManager.open( startingLetter+".txt");
                InputStreamReader inputStreamReader = new InputStreamReader(is);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                wordProcessor.buildDictionary(reader);
                is.close();
                inputStreamReader.close();
                reader.close();
              //  Log.d("message", "status :" + "Initializing is ok");
          //  }
        } catch (IOException e) {
            System.out.println(e);
        }



    }


    public void onInitialize(){
        sinhalaKeyboard=new Keyboard(this,R.xml.keyboard1);
        sinhalaKeyboardShift=new Keyboard(this,R.xml.keyboard2);
        symbolKeyboard=new Keyboard(this,R.xml.simbol);
        symbolShiftKeyboard=new Keyboard(this,R.xml.simbolshift);
        list=new ArrayList<String>();
        //making new keyboard1


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

        return mCandidateView;
    }

    /**
     * Deal with the editor reporting movement of its cursor.
     */
//    @Override
//    public void onUpdateSelection(int oldSelStart, int oldSelEnd,
//                                  int newSelStart, int newSelEnd,
//                                  int candidatesStart, int candidatesEnd) {
//        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
//                candidatesStart, candidatesEnd);
//
//        // If the current selection in the text view changes, we should
//        // clear whatever candidate text we have.
//        if (mComposing.length() > 0 && (newSelStart != candidatesEnd
//                || newSelEnd != candidatesEnd)) {
//            mComposing.setLength(0);
//            updateCandidates();
//            InputConnection ic = getCurrentInputConnection();
//            if (ic != null) {
//                ic.finishComposingText();
//            }
//        }
//    }

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

    /**
     * This translates incoming hard key events in to edit operations on an
     * InputConnection.  It is only needed when using the
     * PROCESS_HARD_KEYS option.
     */
    private boolean translateKeyDown(int keyCode, KeyEvent event) {
        mMetaState = MetaKeyKeyListener.handleKeyDown(mMetaState,
                keyCode, event);
        int c = event.getUnicodeChar(MetaKeyKeyListener.getMetaState(mMetaState));
        mMetaState = MetaKeyKeyListener.adjustMetaAfterKeypress(mMetaState);
        InputConnection ic = getCurrentInputConnection();
        if (c == 0 || ic == null) {
            return false;
        }

        if ((c & KeyCharacterMap.COMBINING_ACCENT) != 0) {
            c = c & KeyCharacterMap.COMBINING_ACCENT_MASK;
        }

        if (mComposing.length() > 0) {
            char accent = mComposing.charAt(mComposing.length() - 1);
            int composed = KeyEvent.getDeadChar(accent, c);
            if (composed != 0) {
                c = composed;
                mComposing.setLength(mComposing.length() - 1);
            }
        }

        onKey(c, null);

        return true;
    }


    public void setSuggestions(List<String> suggestions, boolean completions,
                               boolean typedWordValid) {
        if (suggestions != null && suggestions.size() > 0) {
            setCandidatesViewShown(true);
        } else if (isExtractViewShown()) {
            setCandidatesViewShown(true);
        }
        if (mCandidateView != null) {

            mCandidateView.setSuggestions(suggestions, completions, typedWordValid);
        }
    }

    // Tap on suggestion to commit
    public void pickSuggestionManually(int index) {
//        if (mCompletionOn  && index >= 0 && index < mCompletions.length) {
//            CompletionInfo ci = mCompletions[index];
//            getCurrentInputConnection().commitCompletion(ci);
//            if (mCandidateView != null) {
//                mCandidateView.clear();
//            }
//        }
        System.out.println("mComposing length : "+mComposing.length());
        if (mComposing.length() > 0) {
            // If we were generating candidate suggestions for the current
            // text, we would commit one of them here. But for this sample,
            // we will just commit the current text.
            mComposing.setLength(index);
            mComposing = new StringBuilder(list.get(index));
            typedWord=mComposing.toString();// set the suggetion word list according to picked word
            System.out.println("This is the composing text : "+mComposing);
            getCurrentInputConnection().setComposingText(mComposing, 1);

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

//    private void handleCharacter(int primaryCode, int[] keyCodes) {
//        if (isInputViewShown()) {
//            if (mInputView.isShifted()) {
//                primaryCode = Character.toUpperCase(primaryCode);
//            }
//        }
//        if (mPredictionOn) {
//            mComposing.append((char) primaryCode);
//            getCurrentInputConnection().setComposingText(mComposing, 1);
//            updateShiftKeyState(getCurrentInputEditorInfo());
//            updateCandidates();
//        } else {
//            getCurrentInputConnection().commitText(
//                    String.valueOf((char) primaryCode), 1);
//        }
//    }

    private void handleCharacter(int primaryCode, int[] keyCodes) {
        mComposing.append((char) primaryCode);
        typedWord=mComposing.toString();
        if(!isWordValid){

        }
      // getCurrentInputConnection().commitText(String.valueOf((char)primaryCode),1);
        getCurrentInputConnection().setComposingText(mComposing, 1);
        if (isWordSeparator(primaryCode)) {
           typedWord="";
            getCurrentInputConnection().finishComposingText();
                mComposing.setLength(0);
                setCandidatesViewShown(false);


            //  updateShiftKeyState(getCurrentInputEditorInfo());
    //    } else {
        //    typedWord+=(String.valueOf((char)primaryCode));
            //System.out.println("typedWord :!!!!!!!!!" + typedWord);



        }
     //   System.out.println(mComposing.toString());

        mCandidateView.clear();
        updateCandidates();
    }


    /**
     * Update the list of available candidates from the current composing
     * text.  This will need to be filled in by however you are determining
     * candidates.
     */
    private void updateCandidates() {
//        if (!mCompletionOn) {
//            if (mComposing.length() > 0) {
//                ArrayList<String> list = new ArrayList<String>();
//                list.add(mComposing.toString());
//                Log.d("SoftKeyboard", "REQUESTING: " + mComposing.toString());
//                //mScs.getSentenceSuggestions(new TextInfo[] {new TextInfo(mComposing.toString())}, 5);
//                setSuggestions(list, true, true);
//            } else {
//                setSuggestions(null, false, false);
//            }
//        }
        if(!(list==null)) {// other wise we cannot clear the Arraylist
            list.clear();// clear the entire word list
        }
        if (!mCompletionOn) {
            String currentStartingLetter="";
            try{
                if(!typedWord.equals("")) {
                    if ( !currentStartingLetter.equals(typedWord.substring(0, 1)))
                        currentStartingLetter = typedWord.substring(0, 1);
                        loadDictionary(currentStartingLetter);
                }
                if (typedWord.length() > 0) {

                    ArrayList<String> suggestedList = new ArrayList<String>();
                  //  ArrayList<String> list1 =new ArrayList<String>();
                    list.add(typedWord);
                   //Log.d("SoftKeyboard", "REQUESTING: " + typedWord);
                    // Log.d("keyboard","candidate"+ typedWord);
                   // list2=wordProcessor.getWordList(typedWord);
                   // Log.d("list","containing"+list2);
                    suggestedList=wordProcessor.getWordList(typedWord);
                    if(!suggestedList.isEmpty()){
                       list.addAll(suggestedList);
                        isSuggetionListEmplty=false;
                    }else{
                        isSuggetionListEmplty=true;

                    }
                   // System.out.println(this.list.toString());

                    setSuggestions(list, true, true);
                } else {
                    setSuggestions(null, false, false);
                }

            }
            catch(NullPointerException e){
                System.out.println(e);
            }


        }
    }
    private void handleBackspace() {
/*        final int length = mComposing.length();
        if (length > 1) {
            mComposing.delete(length - 1, length);
            getCurrentInputConnection().setComposingText(mComposing, 1);f
            updateCandidates();
        } else if (length > 0) {
            mComposing.setLength(0);
            getCurrentInputConnection().commitText("", 0);
            updateCandidates();
        }
        else{

        }*/
        //setCandidatesViewShown(false);

        getCurrentInputConnection().deleteSurroundingText(1, 0);// delete surrounding text
        if (isSuggetionListEmplty | typedWord.length()==0) {
            mComposing.setLength(0);
        }
        if (typedWord.length() > 0) {
            typedWord = typedWord.substring(0, typedWord.length()-1);
            mComposing.setLength(typedWord.length());
            mComposing = new StringBuilder(typedWord);

//           if (!isSuggetionListEmplty) {
//                mComposing.setLength(0);// make empty
//                mComposing.append(typedWord);// add the word
//            }



            updateCandidates();

        }
        if (typedWord.length() == 0) {
            setCandidatesViewShown(false);
        }

      //  System.out.println("Typedword :"+typedWord);
      //  System.out.println("composing :"+mComposing.toString());
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

        // We only hide the candidates window when finishing input on
        // a particular editor, to avoid popping the underlying application
        // up and down if the user is entering text into the bottom of
        // its window.
        setCandidatesViewShown(false);


    }
    /** keyboard test**/
    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
    //send a key press to listener
        InputConnection ic=getCurrentInputConnection();
        //Retrieve the currently active InputConnection that is bound to the input method, or null if there is none.
        //playClick(primaryCode);//if want to add sounds
        //switch case statement
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
           /* if (isWordSeparator(primaryCode)){
                // Handle separator
                if (mComposing.length() > 0) {
                    commitTyped(getCurrentInputConnection());
                }
                sendKey(primaryCode);
                //updateShiftKeyState(getCurrentInputEditorInfo());
            }*/
                handleCharacter(primaryCode, keyCodes);
        }
    }
    private void handleShift(){// handle the shift key of the keybaord


        Keyboard currentKeyboard=inputView.getKeyboard();
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
               inputView.setKeyboard(symbolKeyboard);
           } else {
               inputView.setKeyboard(symbolShiftKeyboard);
           }
       }else{
           type="language";
           if (!isCap) {
               inputView.setKeyboard(sinhalaKeyboard);
           } else {
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

}
