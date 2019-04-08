package dictionary;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.inputmethodservice.InputMethodService;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Dictionary extends InputMethodService {
    private ArrayList<String> split ;
    private ArrayList<String> word1_character;
    private ArrayList<String> word2_character ;
    private InputStream inputStream;
    //  private Hashtable<String,Integer> calculatedWord= new Hashtable<String,Integer>();// add the calculated words to increase the performance of the project
    private void writeOnFile(String startingLetter) {
         split = new ArrayList<String>();
         word1_character = new ArrayList<String>();
         word2_character = new ArrayList<String>();
        try {
//            ArrayList<String> file= new ArrayList<String>();
//            file.add("article1rewrite.txt");
//            file.add("article2rewrite.txt");

            // for (int i = 0 ; i< file.size(); i++) {
//            System.out.println("before load");
//            AssetManager assetManager = getAssets();
//            System.out.println("after load");
//           InputStream is = assetManager.open( startingLetter+".txt");// error
//
//      //      System.out.println(startingLetter);
//            InputStreamReader inputStreamReader = new InputStreamReader(is);
//            BufferedReader reader = new BufferedReader(inputStreamReader);

           // System.out.println("before load");
           // AssetManager assetManager=context.getAssets();
          //  System.out.println(assetManager==null);
          //  InputStream inputStream = assetManager.open("ක.txt");
          //  System.out.println("after load");
            if (inputStream != null) {
                split.clear();
                String line;
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                while ((line = reader.readLine()) != null) {
                    String[] splitLine = line.split(" ");

                   //Collections.addAll(split, splitLine);// adding two arrays
                    for (String var :splitLine) {
                        if(!var.equals("")){
                            split.add(var);
                        }

                    }

            //            System.out.print(split+" "+split.contains(""));
                  //  System.out.println(var);
//                        if(!var.equals(" ")){
//                            split.add(var);
//                        }


                  //  split.addAll(splitLine);


                  // }
                  // System.out.println();


                }
         //       System.out.println(split);
                //  System.out.println("before load");
      //          this.buildDictionary(reader);
  //              System.out.println("After build");
                inputStream.close();
                inputStreamReader.close();
                reader.close();
        //        System.out.println(split);
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        }catch(NullPointerException e){
            System.out.println(e);
        }
        //    System.out.println("load dictionary is ok");
        catch (IOException e) {
            System.out.println(e);
    }
    }


//    public void buildDictionary(BufferedReader reader) {
// //       System.out.println("build is ok");
//        try {
//            this.importFile(reader);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    private void importFile(BufferedReader reader) throws Exception {
////        String line;
////        split.clear();
////        try {
////            // Log.d("state","masseage :"+"importFileOk  "+ reader.readLine());
//////            //Log.d("state","masseage :"+"BeforeWhileimportFileOk");
////            //String line =reader.readLine();
////            // System.out.println(line);
////            //System.out.println("split :"+split.toString());
////            while ((line = reader.readLine()) != null) {
////                String[] splitLine= line.split(" ").
////                System.out.println("splitline :"+() );
////                Collections.addAll(split,splitLine);// adding two arrays
//////                for (String var :splitLine) {
//////                  //  System.out.println(var);
//////                   split.add(var.trim());
//////
//////                  //  split.addAll(splitLine);
//////
//////                }
////
////                // Log.d("state","masseage :"+"importFileOk  "+ Arrays.toString(split));
////                // Log.d("state","masseage :"+"importFileOk  "+ Arrays.toString(split)+" "+split.length);
////                // Log.d("wordlist","array"+split[0]);
//////                for (int i = 0; i < split.length; i++) {
//////                    //Log.d("state","masseage :"+ split[i] );
//////                    insert(split[i].trim());
//////
//////                }//
////                //System.out.println(splitLine.length);
////
////            }
////
////         //  System.out.println(split.removeAll(new ArrayList<String>()));
////
////
////            reader.close();
//
////        } catch (Exception e) {
////            //System.out.println(e);
////        }
////
//   }

    private int calculateDistance(String word1, String word2) {
        int distance = 0;//distance between words
        try {
            int counter = 0;

            distance = Math.abs(word1.length() - word2.length());
            if (distance > 3) {
                return -1;
            }// check whether distance is grater than 3

            if (word1.length() <= word2.length()) {
                for (String var : word1.split("")) {
                    if(var.equals("")){continue;}
                    if (!word2.substring(counter, counter + 1).equals(var)) {
                        distance += 1;//distance increase by one
                    }
                    counter ++;

                }
            } else {
                for (String var : word2.split("")) {
                    if(var.equals("")){continue;}
                    if (!word1.substring(counter, counter + 1).equals(var)) {
                        distance += 1;//distance increase by one
                    }
                    counter ++;
                }
            }

        }catch (IndexOutOfBoundsException e){
            System.out.println("calculated distance : "+e);
        }
        if (distance <= 3) {
            return distance;
        } else {
            return -1;
        }
    }


    private ArrayList<String> calculateWord(String word) {
        int counter = 0;
        String nextWord = "";
        ArrayList<String> returnList = new ArrayList<String>();
        //      int sumOfASCIIValueOf_word=word.chars().sum();//calculate sum of the ASCII value

        //  System.out.println("calculated word is ok");

        this.writeOnFile(word.substring(0, 1));// build the dictionary
//        System.out.println("calculated word is ok");
//        System.out.println("split : "+split.toString());
//        System.out.println("calculated word is ok "+word.substring(0, 1));
        //     System.out.print(returnList.size());
        for (String var : split) {
            if(Math.min(returnList.size(),6)==6){
                break;
            }
   //         System.out.println(word+"  :  "+var);
            if (calculateDistance(word, var) != -1) {
                if(!returnList.contains(var)) {
                    returnList.add(var);

                }
            }
//            while ((split.size() > counter - 1) & (Math.min(returnList.size(), 5) <= 5)) {
//
//                nextWord = split.get(counter);
//                System.out.println("nextWord " + nextWord);
////            if(calculatedWord.contains(sumOfASCIIValueOf_word)){
////
////
////            }
////            int calculated_value = nextWord.chars().sum();//calculate sum of the ASCII value
////            if (calculateDistance(word, nextWord) != -1) {
////                returnList.add(nextWord);
////
////
////            }
//                // calculatedWord.put(nextWord,calculated_value);
//
//                counter += 1;
//
//            }

            // calculatedWord.clear();//clear the hashtable, this should be in at last
        }
        return returnList;
    }

    public ArrayList<String> getWordList(String word,InputStream inputStream) {
        this.inputStream=inputStream;
        return calculateWord(word);
    }
    public boolean isWordCorrect(String word){
        if(split.contains(word)){
            return true;
        }else{return false;}

    }
//    public void writeWord(String word){
//        try {
//
//            FileOutputStream fos = new FileOutputStream(word.substring(0, 1) + ".txt",true);
//            fos.write(word.getBytes());
//            fos.close();
//            System.out.println("writing is success");
//        }catch(FileNotFoundException e){e.printStackTrace();
//        }catch(IOException e){e.printStackTrace();
//        }catch(ArrayIndexOutOfBoundsException e){e.printStackTrace();}
//    }
}



//   // static Dictionary  dictionary;
//    private ConcurrentSkipListMap<String, CopyOnWriteArrayList<String>> sinhalaWordList=new ConcurrentSkipListMap<String, CopyOnWriteArrayList<String>>();
//   // private String file;
//
//    // crate the dictionary object
////    public ConcurrentSkipListMap<String, CopyOnWriteArrayList<String>> getDictionary(){
////       try {
////
////           importFile();
////         //  Log.d("message","status :"+"getDictionaryOk");
////       }catch(Exception e){
////           Log.d("message","exception :"+e);
////       }
////
////        return sinhalaWordList;
////    }
//
//
//

//
//
//    }
//    // insert new word list
//    public void insert(String k) {
//       // Log.d("message","status :"+"insertOk");
//        for (int i = 0; i < k.length(); i++) {
//            String key = k.substring(0,i+1);
//           // Log.d("word",": " +k );
//            if(key.equals(" ")){
//                continue;
//            }
//          // Log.d("key : "+ key,"word : "+k);
//
//           // System.out.println(key + " " + w);
//            CopyOnWriteArrayList<String> set = getSinhalaWordList().get(key);
//            if (set == null) {
//                set = new CopyOnWriteArrayList<String>();
//                getSinhalaWordList().put(key, set);
//            }
//            if (! set.contains(k)) {
//                set.add(k);
//            }
//        }
//    }
//    // importing the file

//
//    public ConcurrentSkipListMap<String, CopyOnWriteArrayList<String>> getSinhalaWordList() {
//        return sinhalaWordList;
//    }
//
//    public void setSinhalaWordList(ConcurrentSkipListMap<String, CopyOnWriteArrayList<String>> sinhalaWordList) {
//        this.sinhalaWordList = sinhalaWordList;
//    }

