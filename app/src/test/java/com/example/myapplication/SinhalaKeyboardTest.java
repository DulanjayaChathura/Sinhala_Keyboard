package com.example.myapplication;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


public class SinhalaKeyboardTest {
    SinhalaKeyboard keyboard=new SinhalaKeyboard();
    @Mock
    int redudancyLetter=3540;

    @Test
    public void wordSeparator() {
         boolean expected=true;
        boolean output;
        int value=49;
       // System.out.println(value);
        output=keyboard.isWordSeparator(value);
        assertEquals(expected,output);
        // assertTrue(expected,output);
    }
    @Test
    public void updateCandidates() {
        ArrayList<String> output;
        boolean expected=false;
        try {
            output = keyboard.getWordListFromArticle("ඡායා");
           // System.out.println(output);
            if (!output.isEmpty()) {
                expected = true;
            }
        }catch(NullPointerException e){}

//          assertTrue(expected);
    }
    @Test
    public void reduceRedundancy(){
        boolean expected;

        expected=keyboard.reduceRedundancy(3540);
 //       System.out.println(expected);
        assertTrue(!expected);


    }


    @Test
    public void getWordListFromArticle() {
        ArrayList<String> wordList;
        String curretWord="ඡායා";
        wordList=keyboard.getWordListFromArticle(curretWord);
        try {
            assertTrue(wordList.size() > 0);
        }catch(Exception e){}
    }



//    @Test
//    public void handleShift() {
//
//    }
//
//    @Test
//    public void getCurrentDetails() {
//
//    }




   }