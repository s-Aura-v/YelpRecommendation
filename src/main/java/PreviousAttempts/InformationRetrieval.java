/*
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class InformationRetrieval {
    static HashMap<String, Business> mapOfBusiness = new HashMap<>();
    static HT frequencyTable = new HT();

    public static void main(String[] args) {
        // Setup
        HashMap<String, String> businessNames;
        // Take in Business ID and return Business
        Gson gson = new Gson();
        BufferedReader buffRead;
        JsonObject[] businessData = new JsonObject[150346];
        JsonObject[] businessReview = new JsonObject[150346];

//        System.out.println(System.getProperty("user.dir"));
        // 1. Get business name/id and [number of documents (see step 5).]
        int documentSize = 0;
        try {
            buffRead = new BufferedReader(new FileReader("../dataset/yelp_academic_dataset_business.json"));
            while (documentSize < businessData.length) {
                String line = buffRead.readLine();
                businessData[documentSize] = gson.fromJson(line, JsonObject.class);
                documentSize++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < businessData.length; i++) {
            String name = String.join(" ", String.valueOf(businessData[i].get("name")).split("[^a-zA-Z0-9'&]+")).substring(1);
            //Note: Might need to implement the substring in a better way
            String id =  String.valueOf(businessData[i].get("business_id")).substring(1,23);
                mapOfBusiness.put(id, new Business(name, id));
//            System.out.println(id + " " + name);
        }

        // 2. Get business id/reviews
        documentSize = 0;
        try {
            buffRead = new BufferedReader(new FileReader("../dataset/yelp_academic_dataset_review.json"));
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
            String review = String.join(" ", String.valueOf(businessReview[i].get("text")).split("[^a-zA-Z0-9'&]+")).substring(1);
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
        // Use ID
//        cosineSimilarity("Pns2l4eNsfO8kk83dixA6A");
    }

    static void cosineSimilarity(String businessID) {
        // Cosine Similarity = (vector a * vector b) / (sqrt(vectorA^2) sqrt(vectorB^2))
        Business userInput = mapOfBusiness.get(businessID);
        HashMap<String, Double> similarityScores = new HashMap<>();
        // Calculate the dot product
        for (Business business : mapOfBusiness.values()) {
            double dotProduct = 0.0;
            double userInputMagnitude = 0.0;
            double businessMagnitude = 0.0;
            for (String term : userInput.getTfIDF().keySet()) {
                double userInputTfIdf = userInput.getTfIDF().getOrDefault(term, 0.0);
                double businessTfIdf = business.getTfIDF().getOrDefault(term, 0.0);
                dotProduct += userInputTfIdf * businessTfIdf;
                userInputMagnitude += Math.pow(userInputTfIdf, 2);
                businessMagnitude += Math.pow(businessTfIdf, 2);
            }
            userInputMagnitude = Math.sqrt(userInputMagnitude);
            businessMagnitude = Math.sqrt(businessMagnitude);
            double cosineSimilarity = dotProduct / (userInputMagnitude * businessMagnitude);
            similarityScores.put(business.getId(), cosineSimilarity);
        }
        // Sort the similarity scores
        List<Map.Entry<String, Double>> sortedScores = new ArrayList<>(similarityScores.entrySet());
        sortedScores.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
        // Get top similar businesses
        int topN = 3; // Change this value to get more top businesses
        System.out.println("Top " + topN + " similar businesses to " + userInput.getName() + ":");
        for (int i = 0; i < Math.min(topN, sortedScores.size()); i++) {
            Map.Entry<String, Double> entry = sortedScores.get(i);
            String similarBusinessName = mapOfBusiness.get(entry.getKey()).getName();
            double similarityScore = entry.getValue();
            System.out.println(similarBusinessName + ": " + similarityScore);
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

 */
