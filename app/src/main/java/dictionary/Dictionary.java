package dictionary;

import android.util.Log;


import java.io.BufferedReader;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Dictionary {

   // static Dictionary  dictionary;
    private ConcurrentSkipListMap<String, CopyOnWriteArrayList<String>> sinhalaWordList=new ConcurrentSkipListMap<String, CopyOnWriteArrayList<String>>();
   // private String file;

    // crate the dictionary object
//    public ConcurrentSkipListMap<String, CopyOnWriteArrayList<String>> getDictionary(){
//       try {
//
//           importFile();
//         //  Log.d("message","status :"+"getDictionaryOk");
//       }catch(Exception e){
//           Log.d("message","exception :"+e);
//       }
//
//        return sinhalaWordList;
//    }
    public void buildDictionary(BufferedReader reader){
        try {
            this.importFile(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    // insert new word list
    public void insert(String k) {
       // Log.d("message","status :"+"insertOk");
        for (int i = 0; i < k.length(); i++) {
            String key = k.substring(0,i+1);
           // Log.d("word",": " +k );
            if(key.equals(" ")){
                continue;
            }
          // Log.d("key : "+ key,"word : "+k);

           // System.out.println(key + " " + w);
            CopyOnWriteArrayList<String> set = getSinhalaWordList().get(key);
            if (set == null) {
                set = new CopyOnWriteArrayList<String>();
                getSinhalaWordList().put(key, set);
            }
            if (! set.contains(k)) {
                set.add(k);
            }
        }
    }
    // importing the file
    private void importFile(BufferedReader reader) throws Exception {
        String line;
        try {
           // Log.d("state","masseage :"+"importFileOk  "+ reader.readLine());
//            //Log.d("state","masseage :"+"BeforeWhileimportFileOk");
            //String line =reader.readLine();
           // System.out.println(line);
            while ( (line = reader.readLine() ) != null) {
                String[] split = line.split(" ");
               // Log.d("state","masseage :"+"importFileOk  "+ Arrays.toString(split));
               // Log.d("state","masseage :"+"importFileOk  "+ Arrays.toString(split)+" "+split.length);
               // Log.d("wordlist","array"+split[0]);
                for (int i = 0; i < split.length; i++) {
                  //Log.d("state","masseage :"+ split[i] );
                    insert(split[i].trim());

                }

            }
            reader.close();

        } catch (Exception e) {
            //System.out.println(e);
        }

        }


    public ConcurrentSkipListMap<String, CopyOnWriteArrayList<String>> getSinhalaWordList() {
        return sinhalaWordList;
    }

    public void setSinhalaWordList(ConcurrentSkipListMap<String, CopyOnWriteArrayList<String>> sinhalaWordList) {
        this.sinhalaWordList = sinhalaWordList;
    }
}
