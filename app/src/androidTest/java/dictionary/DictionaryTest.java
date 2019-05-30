package dictionary;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class DictionaryTest {
    Dictionary dictionary1=new Dictionary();
    ArrayList<String> output;

//    @Before
//    public void setUp() throws Exception {
//    }
//
//    @After
//    public void tearDown() throws Exception {
//    }
    @Test
    public void calculateWordTest(){
        String word = "ග";
        String var="ගැලපීම ";
        output=dictionary1.calculateWord(word);


         System.out.println(output.toString());



    }
}