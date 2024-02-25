import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class InformationRetrieval {
    static HashMap<String, Business> mapOfBusiness = new HashMap<>();
    public static void main(String[] args) {
        // Take in Business ID and return Business
        HT frequencyTable = new HT();

        Gson gson = new Gson();
        BufferedReader buffRead;
        JsonObject[] businessData = new JsonObject[4];
        JsonObject[] businessReview = new JsonObject[4];

//        System.out.println(System.getProperty("user.dir"));
        // 1. Get business name/id and [number of documents (see step 5).]
        int documentSize = 0;
        try {
            buffRead = new BufferedReader(new FileReader("src/main/java/jsonBusinessTest.json"));
            while (documentSize < businessData.length) {
                String line = buffRead.readLine();
                businessData[documentSize] = gson.fromJson(line, JsonObject.class);
                documentSize++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < businessData.length; i++) {
            String name = String.join(" ", String.valueOf(businessData[i].get("name")).split("\\P{Alnum}+")).substring(1);
            //Note: Might need to implement the substring in a better way
            String id =  String.valueOf(businessData[i].get("business_id")).substring(1,23);
            mapOfBusiness.put(id, new Business(name, id));
            System.out.println(id + " " + name);
        }

        // 2. Get business id/reviews
        documentSize = 0;
        try {
            buffRead = new BufferedReader(new FileReader("src/main/java/jsonReviewTest.json"));
            while (documentSize < businessData.length) {
                String line = buffRead.readLine();
                businessReview[documentSize] = gson.fromJson(line, JsonObject.class);
                documentSize++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < businessReview.length; i++) {
            String id = String.valueOf(businessReview[i].get("business_id")).substring(1,23);
            String review = String.join(" ", String.valueOf(businessReview[i].get("text")).split("\\P{Alnum}+")).substring(1);
            mapOfBusiness.get(id).setReview(review);
        }

        //3b. Total Document Frequency Table
        // This is how many documents contains the same word
        for (Business business : mapOfBusiness.values()) {
            Set<String> uniqueWords = new HashSet<>(List.of(business.getReview().split("\\P{Alnum}+")));
            for (String word : uniqueWords) {
                if (frequencyTable.contains(word)) {
                    frequencyTable.setCount(word, frequencyTable.getCount(word) + 1);
                } else {
                    frequencyTable.add(word, 1);
                }
            }
        }
//        frequencyTable.printAll();


//        4. Term Frequency (Version 1)
//        for (Business business : mapOfBusiness.values()) {
//            HT termFrequencyInDoc = new HT();
//            for (String word : business.getReview().split("\\s+")) {
//                if (termFrequencyInDoc.contains(word)) {
//                    termFrequencyInDoc.setCount(word, frequencyTable.getCount(word)+1);
//                } else {
//                    termFrequencyInDoc.add(word, 1);
//                }
//            }
//            business.setTermFrequency(termFrequencyInDoc);
//        }
//        //Find term frequency ( term appeared / # of terms )
//        for (Business business : mapOfBusiness.values()) {
//        }


        // 4. Term Frequency Version 2
        for (Business business : mapOfBusiness.values()) {
            HashMap<String, Integer> termFrequency = new HashMap<>();
            HashMap<String, Double> termFrequencyTF = new HashMap<>();
            for (String word : business.getReview().split("\\P{Alnum}+")) {
                if (termFrequency.containsKey(word)) {
                    termFrequency.put(word, termFrequency.get(word) + 1);
                } else {
                    termFrequency.put(word, 1);
                }
            }

            for (String word : termFrequency.keySet()) {
                int term = termFrequency.get(word);
                int size = termFrequency.size();
                termFrequencyTF.put(word, (double) term/size);
                business.setTermFrequency(termFrequencyTF);
            }
        }

        //5. Inverse-Document Frequency
        // Log of (total Number of Documents [documentSize : see step 1] / documents with the term [frequencyTable.getCount(x)])
        // Side note: I want to convert HashMap to HT, but I need to add a double constructor to HT
//        HT idfValues = new HT();
        HashMap<String, Double> idfValues = new HashMap<>();
        for (Object x : frequencyTable) {
            String businessName = (String) x;
            int count = frequencyTable.getCount(x);
            double idf = Math.log10( documentSize / (double) count);
            idfValues.put(businessName,idf);
        }

        //6. TF-IDF : tf * idf
        for (Business business : mapOfBusiness.values()) {
            for (String word : business.getTermFrequency().keySet()) {
                double idf = idfValues.get(word);
                double tf = business.getTermFrequency().get(word);
                double tfIDF = tf * idf;
                business.addToTfIDF(word, tfIDF);
            }
        }
        // Debug: Print Map
        for (Business business : mapOfBusiness.values()) {
            System.out.println(business.getTfIDF());
        }

    }

    void cosineSimilarity(Business userInput) {
        // Cosine Similarity = (vector a * vector b) / sqrt(vectorA^2) sqrt(vectorB^2)
        Business champion, runnerUp;
        for (Business business : mapOfBusiness.values()) {
            double vectorA;
            double vectorB;

        }
    }








    // Debug Code: Print out each key and count in a HT:
    // None of this will work if you uncomment it, but I'm storing it for later use.
    void debug() {
//        for (Object x : frequencyTable) {
//            String name = (String) x;
//            System.out.println(x);
//        }
//        for (Iterator<Integer> it = frequencyTable.countIterator(); it.hasNext(); ) {
//            Integer x = it.next();
//            System.out.println(x);
//        }
    }


}
