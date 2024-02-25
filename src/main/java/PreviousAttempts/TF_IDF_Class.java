package PreviousAttempts;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

public class TF_IDF_Class {
    private final JsonObject[] document;
    private HashMap<String, Integer> frequencyTable;
    private HashMap<String, Double> tfdifMap;

    public TF_IDF_Class(JsonObject[] document) {
        this.document = document;
        this.tfdifMap = new HashMap<String, Double>();
    }

    public HashMap<String,Double> createTFIDFMap() {
        // ArrayList of a HashMap, listOfKeywords, that holds the keywords.
        // IDF = n/N
        // N = sumOfDoc
        // n = Hashset's value (.get(keyword))
        ArrayList<HashMap<String, Double>> termFrequency = new ArrayList<>();
        for (int x = 0; x < document.length; x++) {
            if (document[x] != null) {
                termFrequency.add(frequencyTable(String.valueOf(document[x].get("categories")).split("\\P{Alnum}+")));
            }
        }
//        this.frequencyTable = termFrequency;
//        ArrayList<HashMap<String, Double>> listOfKeywords = termFrequency;
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

        //TF-IDF
        for (HashMap<String, Double> tfMap : termFrequency) {
            for (String term : tfMap.keySet()) {
                double tfidfValue = tfMap.get(term) * idfMap.get(term);
                tfdifMap.put(term, tfidfValue);
            }
        }

        System.out.println(tfdifMap);
        return tfdifMap;
    }

    public void compareTFIDF(String userInput) {
        String champion = "";
        String challenger = "";
//        for (HashMap<String, Double> map : tfdifMap) {
//            for (String term : tfdifMap.keySet()) {
//
//
//
//            }
//        }
    }


        private static HashMap<String, Double> frequencyTable(String[] sortedCategories) {
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
