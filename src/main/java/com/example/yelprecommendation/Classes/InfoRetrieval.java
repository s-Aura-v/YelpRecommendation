package com.example.yelprecommendation.Classes;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.lang.Double.MAX_VALUE;

public class InfoRetrieval {
    static HashMap<String, Business> mapOfBusiness = new HashMap<>();
    static HashMap<String, String> businessNames = new HashMap<>();
    static HT frequencyTable = new HT();
    static int MAX_LENGTH = 10000;

    public static List<Map.Entry<String, Double>> tfIDF(String inputtedID) throws IOException {
        // Take in Business ID and return Business
        Gson gson = new Gson();
        BufferedReader buffRead;
        JsonObject[] businessReview = new JsonObject[MAX_LENGTH];

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
        System.out.println(businessNames.size());
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
        System.out.println("docsize: " + documentSize);

        for (int i = 0; i < businessReview.length; i++) {
            String id = String.valueOf(businessReview[i].get("business_id")).substring(1,23);
            String review = String.join(" ", String.valueOf(businessReview[i].get("text")).split("[^a-zA-Z0-9'&]+")).substring(1);
            mapOfBusiness.put(id, new Business(id, review));
        }
        System.out.println(mapOfBusiness.size());

        //2.5) Load Serialized Data
        if (Files.exists(Path.of(System.getProperty("user.dir") + "/SerializedDocuments/" + inputtedID))) {
            for (Business business : mapOfBusiness.values()) {
                business.loadSerialization(inputtedID);
            }
            System.out.println("Your file was deserialized! :)");
            return cosineSimilarity(inputtedID);
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
        System.out.println("Your file was serialized! :)");


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
            business.setSimilarityValue(cosineSimilarity);
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
        findKMeans();
        return sortedScores;
    }


    public static void findKMeans() {
        int K_VALUE = 5;
        int sizeOfArray = 2000;
        HashMap<String, Business> mapOfBusinessCopy = mapOfBusiness;
        ArrayList<Double> clusterZero = new ArrayList<>();
        ArrayList<Double> clusterOne = new ArrayList<>();
        ArrayList<Double> clusterTwo = new ArrayList<>();
        ArrayList<Double> clusterThree = new ArrayList<>();
        ArrayList<Double> clusterFour = new ArrayList<>();

//        double[] clusterTwo = new double[sizeOfArray];
//        double[] clusterThree = new double[sizeOfArray];
//        double[] clusterFour = new double[sizeOfArray];
//        double[] clusterFive = new double[sizeOfArray];

        List<Double> allCosineSimilaries = new ArrayList<>();
        for (Business x : mapOfBusinessCopy.values()) {
            allCosineSimilaries.add(x.getSimilarityValue());
        }
        Random rand = new Random();
        List<Double> clusterCentroids = new ArrayList<>();
        for (int i = 0; i < K_VALUE; i++) {
            int index = rand.nextInt(allCosineSimilaries.size());
            clusterCentroids.add(allCosineSimilaries.get(index));
            allCosineSimilaries.remove(index);
        }
        double[] distance = new double[K_VALUE];
        double closestCluster = MAX_VALUE;
        int clusterIndex = -1;
        for (Business business: mapOfBusinessCopy.values()) {
            double businessPosition = business.getSimilarityValue();
            for (int i = 0; i < K_VALUE; i++) {
                distance[i] = Math.abs(businessPosition - clusterCentroids.get(i));
                if (closestCluster > distance[i]) {
                    if (i == 0 && clusterZero.size() < 1000) {
                        closestCluster = distance[i];
                        clusterIndex = i;
                    }
                    } else if (i == 1 && clusterOne.size() < 1000) {
                    closestCluster = distance[i];
                    clusterIndex = i;
                } else if (i == 2 && clusterTwo.size() < 1000) {
                    closestCluster = distance[i];
                    clusterIndex = i;
                } else if (i == 3 && clusterThree.size() < 1000) {
                    closestCluster = distance[i];
                    clusterIndex = i;
                } else if (i == 4 && clusterFour.size() < 1000) {
                    closestCluster = distance[i];
                    clusterIndex = i;
                }
            }
            switch (clusterIndex) {
                case 0 -> clusterZero.add(closestCluster);
                case 1 -> clusterOne.add(closestCluster);
                case 2 -> clusterTwo.add(closestCluster);
                case 3 -> clusterThree.add(closestCluster);
                case 4 -> clusterFour.add(closestCluster);
            }

//            switch (clusterIndex) {
//                case 0 -> {
//                    if(clusterZero.size() < 1000) clusterZero.add(closestCluster); }
//                case 1 -> {
//                    if (clusterOne.size() < 1000) clusterOne.add(closestCluster); }
//                case 2 -> {
//                    if (clusterTwo.size() < 1000) clusterTwo.add(closestCluster); }
//                case 3 -> {
//                    if (clusterThree.size() < 1000) clusterThree.add(closestCluster); }
//                case 4 -> {
//                    if (clusterFour.size() < 1000) clusterFour.add(closestCluster); }
//            }

        }
        System.out.println(clusterZero.size() + " " + clusterOne.size() + " " + clusterTwo.size() + " " + clusterThree.size() + " " + clusterFour.size());
    }


}
