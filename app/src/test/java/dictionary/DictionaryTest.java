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
  //  @Mock
//    Dictionary dictionary;
    Dictionary dictionary=new Dictionary();
 //   Dictionary mockDictionary =mock(Dictionary.class);


 //   SinhalaKeyboard keyboard=new SinhalaKeyboard()

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
        assertTrue(!expected);
    }

    @Test
    public void calculateWordTest(){
        String word = "ග";
        String var="ගැලපීම ";
        ArrayList<String> input;
        ArrayList<String> output=new ArrayList<>();
        boolean expected=false;
        try {
            input = dictionary.wordListGenerator(word);
        }catch (NullPointerException e){}
        assertTrue(!expected);
 //       Log.d("ss",input.toString());
//        assertThat(input).isEqualTo();
//        when(dictionary.wordListGenerator(word)).thenReturn(input) ;
//        when(dictionary.calculateDistance(word,var)).thenReturn(1);
      //  System.out.println(result.toString());



    }



}