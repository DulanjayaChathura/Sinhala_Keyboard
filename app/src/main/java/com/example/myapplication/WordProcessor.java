
/*
package com.example.myapplication;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

import dictionary.Dictionary;

public class WordProcessor{
    private Dictionary dictionary=new Dictionary();
   // private ConcurrentSkipListMap<String, CopyOnWriteArrayList<String>> sinhalaDictionary;

    // build dictionary
    public void buildDictionary(BufferedReader reader){

        dictionary.buildDictionary(reader);
        Log.d("message","status :"+"build is ok");
    }



    public ArrayList<String> getWordList(String word){
       // Log.d("message","status :"+"getWordListOk");
        //this.dictionary.setFile();
        //this.sinhalaDictionary= dictionary.getDictionary();
        CopyOnWriteArrayList<String> wordList=this.dictionary.getSinhalaWordList().get(word);
        // this part is ok!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
       // Log.d("wordList", "dic word "+ wordList);
        ArrayList<String> wordArrayList=new ArrayList<String>();
        if(wordList!=null) {
            for (int i = 0; i < Math.min(wordList.size(),6); i++) {
                wordArrayList.add(wordList.get(i));
            }
            return wordArrayList;
        }else{
          ///  System.out.println("this line is ok");
            return wordArrayList;
        }
    }






}
*/
