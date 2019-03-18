package com.example.myapplication;

import android.content.Intent;
import android.os.IBinder;
//import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.util.concurrent.TimeoutException;
/**
import static org.junit.Assert.*;
@RunWith(AndroidJUnit4.class)
public class SinhalaKeyboardTest {


    /**
     * testing the keyboard service



    @Rule
    public final ServiceTestRule serviceRule = new ServiceTestRule();


    @Test
    public void testWithBoundService() throws TimeoutException {
        // Create the service Intent.
        Intent serviceIntent =
                new Intent(ApplicationProvider.getApplicationContext(),
                        LocalService.class);

        // Data can be passed to the service via the Intent.
        serviceIntent.putExtra(LocalService.SEED_KEY, 42L);

        // Bind the service and grab a reference to the binder.
        IBinder binder = serviceRule.bindService(serviceIntent);

        // Get the reference to the service, or you can call
        // public methods on the binder directly.
        LocalService service =
                ((LocalService.LocalBinder) binder).getService();

        // Verify that the service is working correctly.
        assertThat(service.getRandomInt()).isAssignableTo(Integer.class);
    }

}


  x




    private String sinhalaKeyboard="sinhalaKeyboard";
    private String sinhalaShiftKeyboard="sinhalaShiftKeyboard";
    private String symbolKeyboard="symbolKeyboard";
    private String symbolShiftKeyboard="symbolShiftKeyboard";

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void onKey() {
        //to check the changing of the keyboard when pressing on shift



    }
**/