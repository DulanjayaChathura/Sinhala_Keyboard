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

                for (String var : splitLine) {
                    split.add(var);



                }
            }
            inputStream.close();
            inputStreamReader.close();
            bufferedReader.close();

        } catch (FileNotFoundException e) {
            Log.d("wordListGenerator", e.getMessage());

        } catch (IOException e) {
            Log.d("wordListGenerator", e.getMessage());

        }

        return split;
    }

    private void writeOnFile(String fileName) {
        InputStreamReader inputStreamReader;
        BufferedReader reader;
        OutputStreamWriter outputStreamWriter;
        try {
            if (inputStream != null) {
                String line;
                inputStreamReader = new InputStreamReader(inputStream);
                reader = new BufferedReader(inputStreamReader,1000000);
                for(String var: context.fileList()){
                   if((fileName+".txt").equals(var)){return;}
                }

                outputStreamWriter= new OutputStreamWriter(context.openFileOutput(fileName + ".txt", context.MODE_APPEND));
                while ((line = reader.readLine()) != null) {
                    String[] splitLine = line.split(" ");

                    for (String var : splitLine) {
                        if (!var.equals("")) {
                            outputStreamWriter.write((var + " "));
                        }

                    }
                            outputStreamWriter.write('\n');
                            outputStreamWriter.flush();
                }
                outputStreamWriter.close();
                reader.close();
                inputStream.close();





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
        wordGenerationList=wordListGenerator(word);
        Collections.shuffle(wordGenerationList);// make shuffle so that to gain different suggestion list
        for (String var : wordGenerationList) {
            if (Math.min(returnList.size(), 6) == 6) { // check whether size is equal four
                break;
            }
            if ((var.length()>1) && var.contains(word)&& (calculateDistance(word, var)!= -1) ) {
                if (!returnList.contains(var)) {
                    returnList.add(var);

                }
            }
        }

        return returnList;
    }

    public ArrayList<String> getWordList(String word, InputStream inputStream, Context context) {
        this.inputStream = inputStream;
        this.context = context;
        return calculateWord(word);
    }
    public boolean isWordCorrect(String word) {
        if(split.contains(word)){
            return true;
        } else {
            return false;
        }

    }

    public void writeNewWord(String word) { //write new word on dictionary
        String fileName = word.substring(0, 1);
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName + ".txt", context.MODE_APPEND));
            outputStreamWriter.write(word + " ");
            outputStreamWriter.flush();
            outputStreamWriter.close();
        } catch (FileNotFoundException e) {
            Log.d("writeNewWord", e.getMessage());
        } catch (IOException e) {
            Log.d("writeNewWord", e.getMessage());
        }

    }

}