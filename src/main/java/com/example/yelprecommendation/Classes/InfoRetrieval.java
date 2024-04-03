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
    static List<Map.Entry<String, Double>> sortedScores;
    static HT frequencyTable = new HT();
    static int documentSize = 0;

    public static void hashTableSetup() {
        Gson gson = new Gson();
        BufferedReader buffRead;

        //1. Get business name/id
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

        //2. Get business id/reviews
        JsonObject[] businessReview = new JsonObject[documentSize];
        int index = 0;
        try {
            buffRead = new BufferedReader(new FileReader("../yelp_dataset/yelp_academic_dataset_review.json"));
            while (index < documentSize - 1) {
                String line = buffRead.readLine();
                businessReview[index] = gson.fromJson(line, JsonObject.class);
                index++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < businessReview.length - 1; i++) {
            String id = String.valueOf(businessReview[i].get("business_id")).substring(1, 23);

            String unfilteredReview = String.join( " ", String.valueOf(businessReview[i].get("text")).split("[^a-zA-Z0-9'&]+"));
            String review = "placeholder place";
            if (unfilteredReview.length() != 0) {
                review = unfilteredReview.substring(1);
            }
            mapOfBusiness.put(id, new Business(id, review));
        }

        for (String id : mapOfBusiness.keySet()) {
            if (businessNames.containsKey(id)) {
                mapOfBusiness.get(id).setName(businessNames.get(id));
            }
        }

        removeDuplicates();
    }

    public static HashMap<String, Business> tfIDF(String inputtedID) throws IOException {
        //3. Load Serialized Data
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

        // 4. Term Frequency
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
//        for (String id : mapOfBusiness.keySet()) {
//            if (businessNames.containsKey(id)) {
//                mapOfBusiness.get(id).setName(businessNames.get(id));
//            }
//        }

        //8. Serialize
        for (Business business : mapOfBusiness.values()) {
            business.serializeBusiness(inputtedID);
        }
        System.out.println("Your file was serialized! :)");
//        System.out.println(btree);
//        System.out.println("height: "  + btree.height() + "\n size: " + btree.size() );
        return cosineSimilarity(inputtedID);
    }

    public static BTree<String ,String> getBusinessBTree() {
        BTree<String ,String> btree = new BTree();
        for (Business business : mapOfBusiness.values()) {
            btree.put(business.getName(), business.getId());
        }
        return btree;
    }

     private static HashMap<String, Business> cosineSimilarity(String businessID) throws IOException {
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
             mapOfBusiness.get(id).setSimilarityValue(similarityScores.get(id));
             String name = businessNames.get(mapOfBusiness.get(id).getId());
             scoresWithName.put(name, similarityScores.get(id));
         }
         sortedScores = new ArrayList<>(scoresWithName.entrySet());
         sortedScores.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
         findKMeans();
         return mapOfBusiness;
    }

    public static List<Map.Entry<String, Double>> getSortedScores() {
        return sortedScores;
    }


    private static void removeDuplicates() {
        HashMap<String, Business> uniqueBusinessess = new HashMap<>();
        System.out.println("business size: " + mapOfBusiness.size());
        for (Business business : mapOfBusiness.values()) {
            if (uniqueBusinessess.containsValue(business.getName())) {
                mapOfBusiness.remove(business.getId());
            } else {
                uniqueBusinessess.put(business.getId(), business);
            }
        }
        System.out.println("business size: " + mapOfBusiness.size());
    }

    private static void findKMeans() {
        //1. Set up variables
        int K_VALUE = 5;
        int MAX_ARRAY = 2000;
        int TOTAL_CLUSTER_REITERATIONS = 100;
        int clusterIndex = -1;

        HashMap<Double, HashMap<Integer, String>> collectionOfClusteredBusiness = new HashMap<>();
        ArrayList<Object[]> clusteredBusinessesIndex = new ArrayList<>();


        ArrayList<Double> clusterZero = new ArrayList<>();
        ArrayList<Double> clusterOne = new ArrayList<>();
        ArrayList<Double> clusterTwo = new ArrayList<>();
        ArrayList<Double> clusterThree = new ArrayList<>();
        ArrayList<Double> clusterFour = new ArrayList<>();

        double clusterZeroSum = 0;
        double clusterOneSum = 0;
        double clusterTwoSum = 0;
        double clusterThreeSum = 0;
        double clusterFourSum = 0;

        List<Double> allCosineSimilaries = new ArrayList<>();
        HashMap<Integer, String> hashMapContainingClusters = new HashMap<>();

        for (Business x : mapOfBusiness.values()) {
            allCosineSimilaries.add(x.getSimilarityValue());
        }

        for (int i = 0; i < TOTAL_CLUSTER_REITERATIONS; i++) {
            HashMap<String, Business> mapOfBusinessCopy = new HashMap<>();
            mapOfBusinessCopy = copyHashMap(mapOfBusinessCopy);

            for (Business x : mapOfBusiness.values()) {
                x.setInCluster(false);
            }

            clusterZero.clear();
            clusterOne.clear();
            clusterTwo.clear();
            clusterThree.clear();
            clusterFour.clear();

            clusterZeroSum = 0;
            clusterOneSum = 0;
            clusterTwoSum = 0;
            clusterThreeSum = 0;
            clusterFourSum = 0;

            double[] distance = new double[K_VALUE];
            String businessID = "placeholder";

            Random rand = new Random(System.currentTimeMillis());
            List<Double> clusterCentroids = new ArrayList<>();
            for (int k = 0; k < K_VALUE; k++) {
                int index = rand.nextInt(allCosineSimilaries.size());
                clusterCentroids.add(allCosineSimilaries.get(index));
                allCosineSimilaries.remove(index);
            }

            for (Business business : mapOfBusinessCopy.values()) {
                if (!business.isInCluster()) {
                    double businessPosition = business.getSimilarityValue();
                    double closestCluster = MAX_VALUE;
                    for (int j = 0; j < clusterCentroids.size(); j++) {
                        distance[j] = Math.abs(clusterCentroids.get(j) - businessPosition);
                        if (closestCluster > distance[j]) {
                            if (j == 0 && (clusterZero.size() < MAX_ARRAY)) {
                                closestCluster = distance[j];
                                clusterIndex = j;
                                businessID = business.getId();
                            } else if (j == 1 && (clusterOne.size() < MAX_ARRAY)) {
                                closestCluster = distance[j];
                                clusterIndex = j;
                                businessID = business.getId();
                            } else if (j == 2 && (clusterTwo.size() < MAX_ARRAY)) {
                                closestCluster = distance[j];
                                clusterIndex = j;
                                businessID = business.getId();
                            } else if (j == 3 && (clusterThree.size() < MAX_ARRAY)) {
                                closestCluster = distance[j];
                                clusterIndex = j;
                                businessID = business.getId();
                            } else if (j == 4 && (clusterFour.size() < MAX_ARRAY)) {
                                closestCluster = distance[j];
                                clusterIndex = j;
                                businessID = business.getId();
                            }
                        }
                    }
                    switch (clusterIndex) {
                        case 0 -> {
                            clusterZero.add(closestCluster);
                            hashMapContainingClusters.put(0, businessID);
                            break;
                        }
                        case 1 -> {
                            clusterOne.add(closestCluster);
                            hashMapContainingClusters.put(1, businessID);
                            break;
                        }
                        case 2 -> {
                            clusterTwo.add(closestCluster);
                            hashMapContainingClusters.put(2, businessID);
                            break;
                        }
                        case 3 -> {
                            clusterThree.add(closestCluster);
                            hashMapContainingClusters.put(3, businessID);
                            break;
                        }
                        case 4 -> {
                            clusterFour.add(closestCluster);
                            hashMapContainingClusters.put(4, businessID);
                            break;
                        }
                    }
                }
            }

            //Get the total cluster
            for (int j = 0; j < MAX_ARRAY; j++) {
                clusterZeroSum += clusterZero.get(j);
                clusterOneSum += clusterOne.get(j);
                clusterTwoSum += clusterTwo.get(j);
                clusterThreeSum += clusterThree.get(j);
                clusterFourSum += clusterFour.get(j);
            }

            //Store it
            collectionOfClusteredBusiness.put(clusterZeroSum, hashMapContainingClusters);
            collectionOfClusteredBusiness.put(clusterOneSum, hashMapContainingClusters);
            collectionOfClusteredBusiness.put(clusterTwoSum, hashMapContainingClusters);
            collectionOfClusteredBusiness.put(clusterThreeSum, hashMapContainingClusters);
            collectionOfClusteredBusiness.put(clusterFourSum, hashMapContainingClusters);
        }

        // System.out.println(collectionOfClusteredBusiness);
        // Use the collection to find the five smallest values
        ArrayList<Double> clusterSumsSorted = new ArrayList<>(collectionOfClusteredBusiness.keySet());
        Collections.sort(clusterSumsSorted);
        System.out.println(clusterSumsSorted);

        clusterZero.clear();
        clusterOne.clear();
        clusterTwo.clear();
        clusterThree.clear();
        clusterFour.clear();

        boolean clusterZeroFull = false;
        boolean clusterOneFull = false;
        boolean clusterTwoFull = false;
        boolean clusterThreeFull = false;
        boolean clusterFourFull = false;

//        System.out.println(collectionOfClusteredBusiness.get(clusterSumsSorted.get(0)));
//        Set<String> businessWithClusters = new HashSet<>();
//        businessWithClusters.addAll(collectionOfClusteredBusiness.get(clusterSumsSorted.get(0)).keySet());
//        System.out.println(businessWithClusters + "" + businessWithClusters.size());
//        int numOfClustersRemaining = 4;
//        for (int i = 0; i < clusterSumsSorted.size(); i++) {
//
//
//            HashMap<String, Business> clusterdBusiness = collectionOfClusteredBusiness.get(clusterSumsSorted.get(i));
//            for (Business x : clusterdBusiness.values()) {
//                if (!businessWithClusters.contains(x.getId())) {
//                    mapOfBusiness.get(x.getId()).setCluster(i);
//                    businessWithClusters.add(x.getId());
//                    clusterZeroFull = true;
//                }
//            }
//        }
//        System.out.println(clusterSumsSorted);



    }
    private static HashMap<String, Business> copyHashMap(HashMap<String, Business> temp) {
        for (Business x : mapOfBusiness.values()) {
            temp.put(x.getId(), x.getBusiness());
        }
        return temp;
    }
}


