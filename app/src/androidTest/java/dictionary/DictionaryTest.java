package dictionary;

import android.content.Context;
        import android.inputmethodservice.KeyboardView;
        import android.support.test.InstrumentationRegistry;
        import android.support.test.runner.AndroidJUnit4;
        import android.util.Log;

        import org.junit.Before;
        import org.junit.Test;
        import org.junit.runner.RunWith;

        import java.io.BufferedReader;
        import java.io.FileInputStream;
        import java.io.FileNotFoundException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.lang.reflect.Array;
        import java.util.ArrayList;
        import org.junit.After;
        import org.junit.Before;

        import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DictionaryTest {
    Context context;
    FileInputStream fileInputStream;
    StringBuilder composing;
    BufferedReader bufferedReader;
    Dictionary dictionary;
    InputStream inputStream;
    String input="අකුණ";

    @Before
    public void setUp() throws Exception {
        context= InstrumentationRegistry.getTargetContext();
        composing=new StringBuilder();
        dictionary=new Dictionary();

        String fileName=input.substring(0,1);

        try{
             inputStream = context.openFileInput(fileName + ".txt");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader,20000);


        }catch (FileNotFoundException e){

        }
    }
    @Test
    public void useContext(){

        assertEquals("com.example.myapplication",context.getPackageName());

    }
    @Test
    public void testWordListGenerator(){
        String[] expected={"අකුණ"};
        ArrayList<String> output;
        output=dictionary.getWordList(input,inputStream,context);
        assertArrayEquals(expected,output.toArray());


    }


}