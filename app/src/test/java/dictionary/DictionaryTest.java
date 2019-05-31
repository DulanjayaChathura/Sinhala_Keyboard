package dictionary;

import android.content.Context;
import android.util.Log;

import com.example.myapplication.SinhalaKeyboard;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DictionaryTest {

    Dictionary dictionary=new Dictionary();


    @Test
    public void calculateDistance() {
        String word1="ගැලපීම";
        String word2="ගැලවීම";
        int expexted=1;
        int output;
        output=dictionary.calculateDistance(word1,word2);
        assertEquals(expexted,output);

    }

    @Test
    public void isWordCorrect(){

        // ArrayList<String> split=new ArrayList<>();
        String word="ගැල";

        boolean expected;
        expected=dictionary.isWordCorrect(word);
        assertTrue(expected);
    }




}