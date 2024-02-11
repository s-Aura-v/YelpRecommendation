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
        // TF - Complete
        ArrayList<HashMap<String, Double>> listOfKeywords = termFrequency;
        for (int i = 0; i < termFrequency.size(); i++) {
            for (String item : termFrequency.get(i).keySet()) {
                double doubleValues = termFrequency.get(i).get(item);
                double termFreqCalc = (doubleValues / sumOfDoc[i]);
                termFrequency.get(i).replace(item, termFreqCalc);
            }
        }
        for (HashMap l : termFrequency) {
            System.out.println(l);
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

