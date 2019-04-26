package dictionary;


import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.util.Log;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;


public class Dictionary extends InputMethodService {
    private Context context;
    private ArrayList<String> split=new ArrayList<String>();
    private ArrayList<String> word1_character=new ArrayList<String>();
    private ArrayList<String> word2_character=new ArrayList<String>();
    private InputStream inputStream;
    private ArrayList<String> returnList = new ArrayList<String>();
//    private String privoisLength;
//    private String currentLenght;
//    private boolean cLenLargeThanpLen;
//    private Hashtable<String,Integer> calculatedWord= new Hashtable<String,Integer>();// add the calculated words to increase the performance of the project

    private void wordListGenerator(String word) {

        word1_character.clear();
        word2_character.clear();
        split.clear();
        String line;
        String fileName = word.substring(0, 1);
        writeOnFile(fileName);
        try {
            InputStream inputStream = context.openFileInput(fileName + ".txt");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            while ((line = bufferedReader.readLine()) != null) {
                String[] splitLine = line.split(" ");

                //Collections.addAll(split, splitLine);// adding two arrays
                for (String var : splitLine) {
                    if (!var.equals("")) {
                        //                 System.out.println("word generator : "+var);
                        split.add(var);
                    }

                }
            }
        } catch (FileNotFoundException e) {
            Log.d("wordListGenerator", e.getMessage());
        } catch (IOException e) {
            Log.d("wordListGenerator", e.getMessage());
        }


    }

    private void writeOnFile(String fileName) {
        try {

            if (inputStream != null) {
                String line;
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);

                for(String var: context.fileList()){
                   if((fileName+".txt").equals(var)){return;}
                }

           //     System.out.println(con);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName + ".txt", context.MODE_APPEND));
                while ((line = reader.readLine()) != null) {
                    String[] splitLine = line.split(" ");

                    //Collections.addAll(split, splitLine);// adding two arrays
                    for (String var : splitLine) {
                        if (!var.equals("")) {
                            //          System.out.println(var);
                            outputStreamWriter.write((var + " "));
                        }
                        outputStreamWriter.flush();
                    }
                    //  Log.d("writeOnFile","this is !!!!!!!!! ok");

                }
                outputStreamWriter.close();

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
        } catch (NullPointerException e) {
            System.out.println(e);
        }
        //    System.out.println("load dictionary is ok");
        catch (IOException e) {
            System.out.println(e);
        }


    }

    private int calculateDistance(String word1, String word2) {
        int distance = 0;//distance between words
        try {
            int counter = 0;

            distance = Math.abs(word1.length() - word2.length());
            if (distance >= 3) {
                return -1;
            }// check whether distance is grater than 3

            if (word1.length() <= word2.length()) {
                for (String var : word1.split("")) {
                    if (var.equals("")) {
                        continue;
                    }
                    if (!word2.substring(counter, counter + 1).equals(var)) {
                        distance += 1;//distance increase by one
                        if(distance>=3){return -1;}
                    }
                    counter++;


                }
            } else {
                for (String var : word2.split("")) {
                    if (var.equals("")) {
                        continue;
                    }
                    if (!word1.substring(counter, counter + 1).equals(var)) {
                        distance += 1;//distance increase by one
                        if(distance>=3){return -1;}
                    }
                    counter++;
                }
            }

        } catch (IndexOutOfBoundsException e) {
            System.out.println("calculated distance : " + e);
        }
         // check whether distance between two words are less than three
        return distance;

    }


    private ArrayList<String> calculateWord(String word) {
        int counter = 0;
        String nextWord = "";
        returnList.clear();// we clear the return list

        this.wordListGenerator(word);
        Collections.shuffle(split);// make shuffle so that to gain different suggestion list
        for (String var : split) {
            if (Math.min(returnList.size(), 4) == 4) { // check whether size is equal four
                break;
            }

            if (calculateDistance(word, var) != -1) {
                if (!returnList.contains(var)) {
                    returnList.add(var);

                }
            }
        }
    //    System.out.println("calculated word list "+returnList);
        return returnList;
    }

    public ArrayList<String> getWordList(String word, InputStream inputStream, Context context) {
        this.inputStream = inputStream;
        this.context = context;
        return calculateWord(word);
    }
    public boolean isWordCorrect(String word) {
     //   System.out.println("isWordCorrect "+split);
     //   System.out.println("isWordCorrect "+split.contains(word) + "word is "+word );
        if (split.contains(word)) {
            return true;
        } else {
            return false;
        }

    }

    public void writeNewWord(String word) {
        String fileName = word.substring(0, 1);
     //   System.out.println("writeNewWord: " + fileName + " : " + word);
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName + ".txt", context.MODE_APPEND));
            outputStreamWriter.write(word + " ");
            outputStreamWriter.flush();
            outputStreamWriter.close();
            // System.out.print("After writing");
        } catch (FileNotFoundException e) {
            Log.d("writeNewWord", e.getMessage());
        } catch (IOException e) {
            Log.d("writeNewWord", e.getMessage());
        }

    }
}