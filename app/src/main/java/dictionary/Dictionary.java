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
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;


public class Dictionary extends InputMethodService {
    private Context context;
    public ArrayList<String> split=new ArrayList<String>();
    public ArrayList<String> wordGenerationList=new ArrayList<>();
    private ArrayList<String> word1_character=new ArrayList<String>();
    private ArrayList<String> word2_character=new ArrayList<String>();
    private InputStream inputStream;
    private ArrayList<String> returnList = new ArrayList<String>();
//    private String privoisLength;
//    private String currentLenght;
//    private boolean cLenLargeThanpLen;
//    private Hashtable<String,Integer> calculatedWord= new Hashtable<String,Integer>();// add the calculated words to increase the performance of the project

    public ArrayList<String> wordListGenerator(String word) {

        word1_character.clear();
        word2_character.clear();
        split.clear();
        String line;
        String fileName = word.substring(0, 1);
        writeOnFile(fileName);
        try {
            InputStream inputStream = context.openFileInput(fileName + ".txt");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader,20000);

            while ((line = bufferedReader.readLine()) != null) {
                String[] splitLine = line.split(" ");

                //Collections.addAll(split, splitLine);// adding two arrays
                for (String var : splitLine) {
                    split.add(var);

//                    if (!(var.equals("")|var.contains(" ") )){
//                        //                 System.out.println("word generator : "+var);
//                        split.add(var);
//                    }

                }
            }
            inputStream.close();
            inputStreamReader.close();
            bufferedReader.close();
         //   System.out.println("split "+split.get(0).length());
         //   System.out.println("split "+split.size());


        } catch (FileNotFoundException e) {
            Log.d("wordListGenerator", e.getMessage());

        } catch (IOException e) {
            Log.d("wordListGenerator", e.getMessage());

        }

        return split;
    }

    private void writeOnFile(String fileName) {
        //System.out.println("warning here");
        InputStreamReader inputStreamReader;
        BufferedReader reader;
        OutputStreamWriter outputStreamWriter;
        try {
//            if(split.contains(" ")){
//                System.out.println("there is space");
//            }

            if (inputStream != null) {
                String line;
                inputStreamReader = new InputStreamReader(inputStream);
                reader = new BufferedReader(inputStreamReader,1000000);
                for(String var: context.fileList()){
                   if((fileName+".txt").equals(var)){return;}
                }

           //     System.out.println(con);
                outputStreamWriter= new OutputStreamWriter(context.openFileOutput(fileName + ".txt", context.MODE_APPEND));
                while ((line = reader.readLine()) != null) {
                    String[] splitLine = line.split(" ");

                    //Collections.addAll(split, splitLine);// adding two arrays
                    for (String var : splitLine) {
                        if (!var.equals("")) {
                            //          System.out.println(var);
                            outputStreamWriter.write((var + " "));
                        }

                    }
                    //  Log.d("writeOnFile","this is !!!!!!!!! ok");
                            outputStreamWriter.write('\n');
                            outputStreamWriter.flush();
                }
                outputStreamWriter.close();
                reader.close();
                inputStream.close();


                //       System.out.println(split);
                //  System.out.println("before load");
                //          this.buildDictionary(reader);
                //              System.out.println("After build");




            }
        } catch (FileNotFoundException e) {
            Log.d("writing on file", e.getMessage());
        } catch (NullPointerException e) {
            Log.d("writing on file", e.getMessage());
        }
        //    System.out.println("load dictionary is ok");
        catch (IOException e) {
            Log.d("writing on file", e.getMessage());
        }


    }

    public int  calculateDistance(String word1, String word2) {
        int distance = 0;//distance between words
        try {
            int counter = 0;

            distance = Math.abs(word1.length() - word2.length());
            if (distance >3) {
                return -1;
            }// check whether distance is grater than 3

            if (word1.length() <= word2.length()) {
                for (String var : word1.split("")) {
                    if (var.equals("")) {
                        continue;
                    }
                    if (!word2.substring(counter, counter + 1).equals(var)) {
                        distance += 1;//distance increase by one
                        if(distance>3){return -1;}
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
                        if(distance>3){return -1;}
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


    public ArrayList<String> calculateWord(String word) {
        int counter = 0;
        String nextWord = "";
        returnList.clear();// we clear the return list
//        Instant start = Instant.now();
        wordGenerationList=wordListGenerator(word);
        Collections.shuffle(wordGenerationList);// make shuffle so that to gain different suggestion list
        for (String var : wordGenerationList) {
            if (Math.min(returnList.size(), 6) == 6) { // check whether size is equal four
                break;
            }
           // int distance=calculateDistance(word, var);
            if ((var.length()>1) && var.contains(word)&& (calculateDistance(word, var)!= -1) ) {
            //    System.out.println("word  " +word+" var "+var+" distance "+distance);
                if (!returnList.contains(var)) {
                    returnList.add(var);

                }
            }
        }
    //    System.out.println("calculated word list "+returnList);
   //     Instant end = Instant.now();
   //     Duration timeElapsed = Duration.between(start, end);
    //    System.out.println("article size , Time taken: "+ "("+ split.size()+","+timeElapsed +")");
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
        if(split.contains(word)){
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
    public ArrayList<String> returnWordList() {
        return new ArrayList<String>(){{add("ගමන");add("ගත");add("ගමරාළ");}};
    }
}