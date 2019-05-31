package com.example.myapplication;


import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class SinhalaKeyboardTest {
    Context context=InstrumentationRegistry.getTargetContext();
    SinhalaKeyboard keyboard=new SinhalaKeyboard();

    @Test
    public void useContext(){
        assertEquals("com.example.myapplication",context.getPackageName());

    }

    @Test
    public void testHandleBackSpace(){

        keyboard.handleBackspace();
    }
    @Test
    public void testHandleShift(){
        keyboard.handleShift();
    }

}