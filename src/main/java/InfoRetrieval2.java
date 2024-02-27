import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class InfoRetrieval2 {
    static HashMap<String, Business2> mapOfBusiness = new HashMap<>();
    static HT frequencyTable = new HT();
    static HashMap<String, String> businessNames = new HashMap<>();


    public static void main(String[] args) {
        // Take in Business ID and return Business
        Gson gson = new Gson();
        BufferedReader buffRead;
        JsonObject[] businessReview = new JsonObject[10000];

//        System.out.println(System.getProperty("user.dir"));
        // 1. Get business name/id and [number of documents (see step 5).]
        int documentSize = 0;
        try {
            buffRead = new BufferedReader(new FileReader("../dataset/yelp_academic_dataset_business.json"));
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
//        System.out.println(businessNames);

        // 2. Get business id/reviews
        documentSize = 0;
        try {
            buffRead = new BufferedReader(new FileReader("../dataset/yelp_academic_dataset_review.json"));
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
            mapOfBusiness.put(id, new Business2(id, review));
        }

        //3b. Total Document Frequency Table
        // This is how many documents contains the same word
        for (Business2 business : mapOfBusiness.values()) {
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
        for (Business2 business : mapOfBusiness.values()) {
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
        // Side note: I want to convert HashMap to HT, but I need to add a double constructor to HT
//        HT idfValues = new HT();
        HashMap<String, Double> idfValues = new HashMap<>();
        for (Object x : frequencyTable) {
            String businessName = (String) x;
            int count = frequencyTable.getCount(x);
            double idf = Math.log10( documentSize / (double) count);
            idfValues.put(businessName,idf);
        }
//        System.out.println(idfValues);

        //6. TF-IDF : tf * idf
        for (Business2 business : mapOfBusiness.values()) {
            for (String word : business.getTermFrequency().keySet()) {
                double idf = idfValues.get(word);
                double tf = business.getTermFrequency().get(word);
                double tfIDF = tf * idf;
                business.addToTfIDF(word, tfIDF);
            }
        }

//        for (Business2 business : mapOfBusiness.values()) {
//            System.out.println(business.getId());
//        }

        // Use ID
        cosineSimilarity("HrIbP2-jdRJAU92yqyDmyw");
    }

    static void cosineSimilarity(String businessID) {
        // Cosine Similarity = (vector a * vector b) / (sqrt(vectorA^2) sqrt(vectorB^2))
        Business2 userInput = mapOfBusiness.get(businessID);
        HashMap<String, Double> similarityScores = new HashMap<>();
        // Calculate the dot product
        for (Business2 business : mapOfBusiness.values()) {
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
            // I added by .000001 to prevent values from being divided by 0 :(
            double cosineSimilarity = dotProduct / ((userInputMagnitude * businessMagnitude) + .000001);
            similarityScores.put(business.getId(), cosineSimilarity);
        }
        // Sort the similarity scores
        List<Map.Entry<String, Double>> sortedScores = new ArrayList<>(similarityScores.entrySet());
        sortedScores.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
        // Get top similar businesses
        int topN = 3; // Change this value to get more top businesses
        System.out.println("Top " + topN + " similar businesses to " + userInput.getId() + ":");
        for (int i = 0; i < Math.min(topN, sortedScores.size()); i++) {
            Map.Entry<String, Double> entry = sortedScores.get(i);
            String similarBusinessName = mapOfBusiness.get(entry.getKey()).getId();
            String name = businessNames.get(mapOfBusiness.get(entry.getKey()).getId());
            double similarityScore = entry.getValue();
            System.out.println(name + ": " + similarityScore);
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
