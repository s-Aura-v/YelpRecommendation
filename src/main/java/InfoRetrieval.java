import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.util.*;

public class InfoRetrieval {
    static HashMap<String, Business> mapOfBusiness = new HashMap<>();
    static HashMap<String, String> businessNames = new HashMap<>();
    static StringHT idToFileName = new StringHT();
    static HT frequencyTable = new HT();
    static int MAX_LENGTH = 10000;

    public static List<Map.Entry<String, Double>> tfIDF(String inputtedID) throws IOException {
        // Take in Business ID and return Business
        Gson gson = new Gson();
        BufferedReader buffRead;
        JsonObject[] businessReview = new JsonObject[10000];
        // 1. Get business name/id and [number of documents (see step 5).]
        int documentSize = 0;
        try {
            buffRead = new BufferedReader(new FileReader("../yelp_dataset/yelp_academic_dataset_business.json"));
            String line;
            while ((line = buffRead.readLine()) != null) {
                JsonObject business = gson.fromJson(line, JsonObject.class);
                String name = String.join(" ", String.valueOf(business.get("name")).split("[^a-zA-Z0-9'&]+")).substring(1);
                String id =  String.valueOf(business.get("business_id")).substring(1,23);
                businessNames.put(id, name);
                documentSize++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println(businessNames.size());
        // 2. Get business id/reviews
        documentSize = 0;
        try {
            buffRead = new BufferedReader(new FileReader("../yelp_dataset/yelp_academic_dataset_review.json"));
            while (documentSize < businessReview.length) {
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
            mapOfBusiness.put(id, new Business(id, review));
        }
        //3b. Total Document Frequency Table
        // This is how many documents contains the same word
        for (Business business : mapOfBusiness.values()) {
            Set<String> uniqueWords = new HashSet<>(List.of(business.getReview().split("[^a-zA-Z0-9'&]+")));
            for (String word : uniqueWords) {
                if (frequencyTable.contains(word)) {
                    frequencyTable.setCount(word, frequencyTable.getCount(word) + 1);
                } else {
                    frequencyTable.add(word, 1);
                }
            }
        }
//        frequencyTable.printAll();
        // 4. Term Frequency Version 2
        for (Business business : mapOfBusiness.values()) {
            HashMap<String, Integer> termFrequency = new HashMap<>();
            HashMap<String, Double> termFrequencyTF = new HashMap<>();
            for (String word : business.getReview().split("[^a-zA-Z0-9'&]+")) {
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
        HashMap<String, Double> idfValues = new HashMap<>();
        for (Object x : frequencyTable) {
            String businessName = (String) x;
            int count = frequencyTable.getCount(x);
            double idf = Math.log10( documentSize / (double) count);
            idfValues.put(businessName,idf);
        }
//        System.out.println(idfValues);
        //6. TF-IDF : tf * idf
        for (Business business : mapOfBusiness.values()) {
            for (String word : business.getTermFrequency().keySet()) {
                double idf = idfValues.get(word);
                double tf = business.getTermFrequency().get(word);
                double tfIDF = tf * idf;
                business.addToTfIDF(word, tfIDF);
            }
        }
        //7. Add business name
        for (String id : mapOfBusiness.keySet()) {
            if (businessNames.containsKey(id)) {
                mapOfBusiness.get(id).setName(businessNames.get(id));
            }
        }
//         Use ID
        BTree btree = new BTree();
        for (Business business : mapOfBusiness.values()) {
            business.serializeBusiness(inputtedID);
            btree.put(business.getId(), business.getName());
        }
//        System.out.println(btree);
        System.out.println("size:    " + btree.size());
        System.out.println("height:  " + btree.height());

        //        for (Business business : mapOfBusiness.values()) {
//            System.out.println(business.getId());
//        }
        return cosineSimilarity(inputtedID);
    }

     static List<Map.Entry<String, Double>> cosineSimilarity(String businessID) throws IOException {
        // Cosine Similarity = (vector a * vector b) / (sqrt(vectorA^2) sqrt(vectorB^2))
        Business userInput = mapOfBusiness.get(businessID);
        HashMap<String, Double> similarityScores = new HashMap<>();
        HashMap<String, Double> output = new HashMap<>();
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
            // Add an insignificant value to prevent NaN
            double cosineSimilarity = dotProduct / ((userInputMagnitude * businessMagnitude) + .000001);
            similarityScores.put(business.getId(), cosineSimilarity);
        }
        // Sort the similarity scores
         HashMap<String, Double> scoresWithName = new HashMap<>();
         for (String id : similarityScores.keySet()) {
             String name = businessNames.get(mapOfBusiness.get(id).getId());
             scoresWithName.put(name, similarityScores.get(id));
//             similarityScores.remove(id);
         }
        List<Map.Entry<String, Double>> sortedScores = new ArrayList<>(scoresWithName.entrySet());
        sortedScores.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
        // Get similar businesses
        int topN = 3;
//        System.out.println("Top " + topN + " similar businesses to " + userInput.getId() + ":");
//        for (int i = 0; i < Math.min(topN, sortedScores.size()); i++) {
//            Map.Entry<String, Double> entry = sortedScores.get(i);
//            String name = businessNames.get(mapOfBusiness.get(entry.getKey()).getId());
//            double similarityScore = entry.getValue();
//            output.put(name, similarityScore);
//            System.out.println(name + ": " + similarityScore);
//        }
        return sortedScores;
    }


}
