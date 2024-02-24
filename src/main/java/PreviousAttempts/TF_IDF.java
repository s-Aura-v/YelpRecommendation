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
            buffRead = new BufferedReader(new FileReader("/Users/survive/Desktop/EEATO/24Spring/CSC365/FebProjectFiles/yelp_dataset/reviewTEST.json"));
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
                termFrequency.add(frequencyTable(String.valueOf(objects[x].get("text")).split("\\P{Alnum}+")));
            }
        }
//        ArrayList<HashMap<String, Double>> listOfKeywords = termFrequency;
//        for (HashMap<String, Double> map : listOfKeywords) {
//            for (String item : map.keySet()) {
//                System.out.println(item);
//            }
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
        HashMap<String, Integer> wordFrequencyInDoc = new HashMap<>();
        for (HashMap<String, Double> map : termFrequency) {
            for (String item : map.keySet()) {
                if (wordFrequencyInDoc.containsKey(item)) {
                    wordFrequencyInDoc.put(item, wordFrequencyInDoc.get(item) + 1);
                } else {
                    wordFrequencyInDoc.put(item, 1);
                }
            }
        }

        //Calculating IDF
        HashMap<String, Double> idfMap = new HashMap<>();
        for (HashMap<String, Double> map : termFrequency) {
            for (String item : map.keySet()) {
                double idfValue = Math.log(termFrequency.size() / (double) wordFrequencyInDoc.get(item));
                idfMap.put(item, idfValue);
//                System.out.println("word: " + item + "     idfMap: " + wordFrequencyInDoc.get(item) + "     idfValue: " + idfValue);
            }
        }
//        System.out.println(idfMap);

        //TF-IDF
        HashMap<String, Double> tfIDF = new HashMap<>();
        for (HashMap<String, Double> tfMap : termFrequency) {
            for (String term : tfMap.keySet()) {
                double tfidfValue = tfMap.get(term) * idfMap.get(term);
                tfIDF.put(term, tfidfValue);
            }
        }

        System.out.println(tfIDF);

        computeTFDIFFromString("fuck me man, this is troubling, fuck, fuck");
//        return tfIDF;
    }

    public static HashMap<String, Double> computeTFDIFFromString(String userInput) {
        // 1. Find term frequency for TFDIF String
        String[] wordsArray = userInput.split("\\P{Alnum}+");
        HashMap<String, Double> inputFrequency = frequencyTable(wordsArray);
//        System.out.println(inputFrequency);

        //2. Get size of sentence
        int sizeN = wordsArray.length;
        System.out.println("Size: " + sizeN);

        //3. Term Frequency: (word frequency)/(total numbers)
        for (String item : inputFrequency.keySet()) {
            double termFreq = (double) inputFrequency.get(item) / sizeN;
            inputFrequency.replace(item, termFreq);
        }

        //4. IDF = It's only 1 document so IDF is pointless
        return inputFrequency;
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

