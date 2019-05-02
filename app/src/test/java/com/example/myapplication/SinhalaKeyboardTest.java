package com.example.myapplication;

import org.junit.Test;

import static org.junit.Assert.*;

public class SinhalaKeyboardTest {

    @Test
    public void isWordSeparator() {
       // String expected="true";
        boolean output;
        int value=50;
        SinhalaKeyboard keyboard=new SinhalaKeyboard();
        output=keyboard.isWordSeparator(value);
        assertTrue(output);
       // assertTrue(expected,output);

    }
}