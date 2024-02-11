import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.lang.Number.*;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.*;

public class jsonMultLinesTest {
    public static void main(String[] args) throws IOException {
        Gson gson = new Gson();
        BufferedReader buffRead;
        JsonObject[] objects = new JsonObject[10001];
        int index = 0;
        try {
            buffRead = new BufferedReader(new FileReader("/Users/survive/Desktop/EEATO/24Spring/CSC365/FebProjectFiles/yelp_dataset/test.json"));
            while (index < objects.length) {
                String line = buffRead.readLine();
                objects[index] = gson.fromJson(line, JsonObject.class);
                index++;
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        ArrayList<HashMap<String,Double>> listOfKeywords = new ArrayList<>();
        for (int x = 0; x < objects.length; x++) {
            if (objects[x] != null) {
                frequencyTable(String.valueOf(objects[x].get("categories")).split("\\P{Alnum}+"));
                listOfKeywords.add(frequencyTable(String.valueOf(objects[x].get("categories")).split("\\P{Alnum}+")));
            }
        }
        /*
     TFIDF(t,d,D) = tf(t,d) * idf(t,D
     tf(t,d) = n/N
     n is the number of times term t appears in the document d.
     N is the total number of terms in the document d.

     idf(t,D) = log (N/( n))
     N is the number of documents in the data set.
     n is the number of documents that contain the term t among the data set.
     */

        //Finding TF-Dif
        ArrayList<HashMap<String,Double>> listOfKeywordsBackup = listOfKeywords;

        for (HashMap i : listOfKeywords) {
            for (Object item: i.keySet()) {
//                System.out.println("key : " + item);
//                System.out.println("value : " + (i.get(item)));
                Integer intValues = (Integer) i.get(item);
                double doubleValues = (double) intValues.doubleValue();
                double termFrequency = (doubleValues/i.keySet().size());
                i.replace(item, termFrequency);
//                System.out.println("keyset:" + item + "\n tF: " + i.get(item));
            }
        }

//        // Debug: Print out the HashSet
//        for (HashMap i : listOfKeywords) {
//            for (Object item: i.keySet()) {
//                System.out.println("Keyset: " + item + "\n Keyset TF: " + i.get(item));
//            }
//        }

        //idf-finding lowercase-n (Word frequency)
        int numOfDocuments = listOfKeywords.size();
        HashMap<String, Double> wordFrequency = new HashMap<>();

        for (HashMap i : listOfKeywords) {
            for (Object item: i.keySet()) {
                if (wordFrequency.containsKey(item)) {
                        wordFrequency.put(String.valueOf(item), wordFrequency.get(item) + 1);
                    } else {
                    wordFrequency.put(String.valueOf(item), 1.0);
                }
            }
        }

////         Debug: Print HashMap
//        for (String i : wordFrequency.keySet()) {
//            System.out.println(i);
//            System.out.println(wordFrequency.get(i));
//        }

//        n is the number of documents that contain the term t among the data set.

        for (String keyword : wordFrequency.keySet()) {
            double idf = (Math.log(numOfDocuments))/(wordFrequency.get(keyword));

//            System.out.println("word:" + keyword + " AND wordFrequency(n): " + wordFrequency.get(keyword) + "\n idf:" + idf);
        }
//        double idf = Math.log(numOfDocuments)/
    }


    public static HashMap frequencyTable(String[] sortedCategories) {
        HashMap<String, Integer> map = new HashMap<>();
        for (int i = 0; i < sortedCategories.length; i++) {
            // If the map key exists, add 1 to its frequency
            if (map.containsKey(sortedCategories[i])) {
                map.put(sortedCategories[i], map.get(sortedCategories[i]) + 1);
            } else {
                // If the map key doesn't exist, set 1 to its frequency and add it to the map
                map.put(sortedCategories[i], 1);
            }
        }
        return map;
    }
}
