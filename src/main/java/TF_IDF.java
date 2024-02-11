import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class TF_IDF {

    public static void main(String[] args) {
        // Created JsonObject, objects, that holds the input data.
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        // ArrayList of a HashMap, listOfKeywords, that holds the keywords.
        // IDF = n/N
        // N = sumOfDoc
        // n = Hashset's value (.get(keyword))
        ArrayList<HashMap<String, Double>> termFrequency = new ArrayList<>();
        for (int x = 0; x < objects.length; x++) {
            if (objects[x] != null) {
                termFrequency.add(frequencyTable(String.valueOf(objects[x].get("categories")).split("\\P{Alnum}+")));
            }
        }
        ArrayList<HashMap<String, Double>> listOfKeywords = termFrequency;
//        for (HashMap<String, Double> map : listOfKeywords) {
//            System.out.println("Map: " + map);
//        }
        // N = sumOfDOc
        int[] sumOfDoc = new int[termFrequency.size()];
        for (int i = 0; i < termFrequency.size(); i++) {
            int sum = 0;
            for (Object item : termFrequency.get(i).keySet()) {
                sum += (double) (termFrequency.get(i).get(item));
            }
            sumOfDoc[i] = sum;
        }
        // TF - Complete (Term Frequency contains IDF)
        for (int i = 0; i < termFrequency.size(); i++) {
            for (String item : termFrequency.get(i).keySet()) {
                double doubleValues = termFrequency.get(i).get(item);
                double termFreqCalc = (doubleValues / sumOfDoc[i]);
                termFrequency.get(i).replace(item, termFreqCalc);
            }
        }
//        for (HashMap l : termFrequency) {
//            System.out.println(l);
//        }


        // IDF = idf(t,D) = log (N/( n))
        // N is the number of documents in the data set. = (size of ArrayList)
        // n is the number of documents that contain the term t among the data set.
        // It will appear once because its a hash map
        HashMap<String, Integer> idfMap = new HashMap<>();
        for (HashMap<String, Double> map : listOfKeywords) {
            for (String item : map.keySet()) {
                if (idfMap.containsKey(item)) {
                    idfMap.put(item, idfMap.get(item) + 1);
                } else {
                    idfMap.put(item, 1);
                }
            }
        }




    }

    public static HashMap<String, Double> frequencyTable(String[] sortedCategories) {
        HashMap<String, Double> map = new HashMap<>();
        for (int i = 0; i < sortedCategories.length; i++) {
            if (map.containsKey(sortedCategories[i])) {
                map.put(sortedCategories[i], map.get(sortedCategories[i]) + 1);
            } else {
                map.put(sortedCategories[i], 1.0);
            }
        }
        return map;
    }
}

