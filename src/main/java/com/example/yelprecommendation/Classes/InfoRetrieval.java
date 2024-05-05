package com.example.yelprecommendation.Classes;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.lang.Double.MAX_VALUE;

public class InfoRetrieval {
    static HashMap<String, Business> mapOfBusiness = new HashMap<>();
    static HashMap<String, String> businessNames = new HashMap<>();
    static HashMap<String, String> businessLocation = new HashMap<>();
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
                String id = String.valueOf(business.get("business_id")).substring(1, 23);
                String longitude = String.valueOf(business.get("longitude"));
                String latitude = String.valueOf(business.get("latitude"));
                String location = latitude + "," + longitude;
                businessLocation.put(id, location);
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

            String unfilteredReview = String.join(" ", String.valueOf(businessReview[i].get("text")).split("[^a-zA-Z0-9'&]+"));
            String review = "placeholder place";
            if (unfilteredReview.length() != 0) {
                review = unfilteredReview.substring(1);
            }
            mapOfBusiness.put(id, new Business(id, review));
        }

        for (String id : mapOfBusiness.keySet()) {
            if (businessNames.containsKey(id)) {
                mapOfBusiness.get(id).setName(businessNames.get(id));
                double latitude = Double.parseDouble(businessLocation.get(id).substring(0, businessLocation.get(id).indexOf(",")));
                double longitude = Double.parseDouble(businessLocation.get(id).substring(businessLocation.get(id).indexOf(",") + 1));
                mapOfBusiness.get(id).setLatitude(latitude);
                mapOfBusiness.get(id).setLongitude(longitude);
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
                termFrequencyTF.put(word, (double) term / size);
                business.setTermFrequency(termFrequencyTF);
            }
        }

        //5. Inverse-Document Frequency
        // Log of (total Number of Documents [documentSize : see step 1] / documents with the term [frequencyTable.getCount(x)])
        HashMap<String, Double> idfValues = new HashMap<>();
        for (Object x : frequencyTable) {
            String businessName = (String) x;
            int count = frequencyTable.getCount(x);
            double idf = Math.log10(documentSize / (double) count);
            idfValues.put(businessName, idf);
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

    public static BTree<String, String> getBusinessBTree() {
        BTree<String, String> btree = new BTree();
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
        geographicCluster(businessID);
        return mapOfBusiness;
    }

    public static List<Map.Entry<String, Double>> getSortedScores() {
        return sortedScores;
    }

    private static void removeDuplicates() {
        ArrayList<String> uniqueBusiness = new ArrayList<>();
        ArrayList<String> notUniqueBusiness = new ArrayList<>();
        for (Business business : mapOfBusiness.values()) {
            if (uniqueBusiness.contains(business.getName())) {
                notUniqueBusiness.add(business.getId());
            } else {
                uniqueBusiness.add(business.getName());
            }
        }
        for (int i = 0; i < notUniqueBusiness.size(); i++) {
            mapOfBusiness.remove(notUniqueBusiness.get(i));
        }
        System.out.println(mapOfBusiness.size());
    }

    private static void findKMeans() {
        int TOTAL_CLUSTERS = 5;
        int MAX_ARRAY_SIZE = 2000;
        int TOTAL_CLUSTER_ITERATIONS = 100;
        int clusterIndex = -1;

        double clusterZeroSum = 0;
        double clusterOneSum = 0;
        double clusterTwoSum = 0;
        double clusterThreeSum = 0;
        double clusterFourSum = 0;
        ArrayList<Double> clusterZero = new ArrayList<>();
        ArrayList<Double> clusterOne = new ArrayList<>();
        ArrayList<Double> clusterTwo = new ArrayList<>();
        ArrayList<Double> clusterThree = new ArrayList<>();
        ArrayList<Double> clusterFour = new ArrayList<>();
        ArrayList<String> clusterZeroIDs = new ArrayList<>();
        ArrayList<String> clusterOneIDs = new ArrayList<>();
        ArrayList<String> clusterTwoIDs = new ArrayList<>();
        ArrayList<String> clusterThreeIDs = new ArrayList<>();
        ArrayList<String> clusterFourIDs = new ArrayList<>();
        HashMap<Double, ArrayList<String>> clusteredBusiness = new HashMap<>();
        List<Double> allSimilitarityValues = new ArrayList<>();
        for (Business x : mapOfBusiness.values()) {
            allSimilitarityValues.add(x.getSimilarityValue());
        }

        for (int i = 0; i < TOTAL_CLUSTER_ITERATIONS; i++) {
            clusterZero.clear();
            clusterOne.clear();
            clusterTwo.clear();
            clusterThree.clear();
            clusterFour.clear();
            clusterZeroIDs.clear();
            clusterOneIDs.clear();
            clusterTwoIDs.clear();
            clusterThreeIDs.clear();
            clusterFourIDs.clear();
            clusterZeroSum = 0;
            clusterOneSum = 0;
            clusterTwoSum = 0;
            clusterThreeSum = 0;
            clusterFourSum = 0;

            // Set up the centers
            double[] distance = new double[TOTAL_CLUSTERS];
            String businessID = "placeholder";
            Random rand = new Random(System.currentTimeMillis());
            List<Double> clusterCentroids = new ArrayList<>();
            for (int j = 0; j < TOTAL_CLUSTERS; j++) {
                int index = rand.nextInt(allSimilitarityValues.size());
                double centroidValue = allSimilitarityValues.get(index);
                if (!clusterCentroids.contains(centroidValue)) {
                    clusterCentroids.add(allSimilitarityValues.get(index));
                } else {
                    j--;
                }
            }

            for (Business business : mapOfBusiness.values()) {
                double businessPosition = business.getSimilarityValue();
                double closestCluster = MAX_VALUE;
                clusterIndex = -1;
                for (int k = 0; k < clusterCentroids.size(); k++) {
                    distance[k] = Math.abs(clusterCentroids.get(k) - businessPosition);
                    if (closestCluster > distance[k]) {
                        if (k == 0 && (clusterZero.size() < MAX_ARRAY_SIZE)) {
                            closestCluster = distance[k];
                            clusterIndex = k;
                            businessID = business.getId();
                        } else if (k == 1 && (clusterOne.size() < MAX_ARRAY_SIZE)) {
                            closestCluster = distance[k];
                            clusterIndex = k;
                            businessID = business.getId();
                        } else if (k == 2 && (clusterTwo.size() < MAX_ARRAY_SIZE)) {
                            closestCluster = distance[k];
                            clusterIndex = k;
                            businessID = business.getId();
                        } else if (k == 3 && (clusterThree.size() < MAX_ARRAY_SIZE)) {
                            closestCluster = distance[k];
                            clusterIndex = k;
                            businessID = business.getId();
                        } else if (k == 4 && (clusterFour.size() < MAX_ARRAY_SIZE)) {
                            closestCluster = distance[k];
                            clusterIndex = k;
                            businessID = business.getId();
                        }
                    }
                }
                switch (clusterIndex) {
                    case 0 -> {
                        clusterZero.add(closestCluster);
                        clusterZeroIDs.add(businessID);
                    }
                    case 1 -> {
                        clusterOne.add(closestCluster);
                        clusterOneIDs.add(businessID);
                    }
                    case 2 -> {
                        clusterTwo.add(closestCluster);
                        clusterTwoIDs.add(businessID);
                    }
                    case 3 -> {
                        clusterThree.add(closestCluster);
                        clusterThreeIDs.add(businessID);
                    }
                    case 4 -> {
                        clusterFour.add(closestCluster);
                        clusterFourIDs.add(businessID);
                    }
                }
            }

//             Get the total size of the clusters: To be used to find the most accurate clusters
            for (int j = 0; j < MAX_ARRAY_SIZE; j++) {
                if (j < clusterZero.size()) {
                    clusterZeroSum += clusterZero.get(j);
                }
                if (j < clusterOne.size()) {
                    clusterOneSum += clusterOne.get(j);
                }
                if (j < clusterTwo.size()) {
                    clusterTwoSum += clusterTwo.get(j);
                }
                if (j < clusterThree.size()) {
                    clusterThreeSum += clusterThree.get(j);
                }
                if (j < clusterFour.size()) {
                    clusterFourSum += clusterFour.get(j);
                }
            }
            double totalSum = clusterZeroSum + clusterOneSum + clusterTwoSum + clusterThreeSum + clusterFourSum;
            /*
            Index 0-1999: Cluster 0
            Index: 2000-3999: Cluster 1
            Index: 4000-5999: Cluster 2
            Index: 6000-7999: Cluster 3
            Index: 8000-9999: Cluster 4
             */
            ArrayList<String> oneIterationBusinessIDs = new ArrayList<>();
            oneIterationBusinessIDs.addAll(clusterZeroIDs);
            oneIterationBusinessIDs.addAll(clusterOneIDs);
            oneIterationBusinessIDs.addAll(clusterTwoIDs);
            oneIterationBusinessIDs.addAll(clusterThreeIDs);
            oneIterationBusinessIDs.addAll(clusterFourIDs);
            // Store it
            clusteredBusiness.put(totalSum, oneIterationBusinessIDs);
        }

        //Sort and find the one
        // The smallest clusterSum are the five clusters that are the least apart.
        ArrayList<Double> sortedClusterSums = new ArrayList<>(clusteredBusiness.keySet());
        Collections.sort(sortedClusterSums);

        // Using the index from before, add them to the appropriate cluster
        double smallestVariability = sortedClusterSums.get(0);
        ArrayList<String> businessIDList = clusteredBusiness.get(smallestVariability);
        for (int i = 0; i < 10000; i++) {
            if (i < 1999) {
                mapOfBusiness.get(businessIDList.get(i)).setCluster(0);
            } else if (i < 3999) {
                mapOfBusiness.get(businessIDList.get(i)).setCluster(1);
            } else if (i < 5999) {
                mapOfBusiness.get(businessIDList.get(i)).setCluster(2);
            } else if (i < 7999) {
                mapOfBusiness.get(businessIDList.get(i)).setCluster(3);
            } else if (i < 9999) {
                if (i < businessIDList.size()) {
                    mapOfBusiness.get(businessIDList.get(i)).setCluster(4);
                }
            }
        }
    }

    public static void geographicCluster(String id) {
        // Control business is the name of the business that we'll try to find the four closest geographic location for
        HashMap<String, Double> closestBusiness = new HashMap<>();
        Business controlBusiness = mapOfBusiness.get(id);
        int cluster = mapOfBusiness.get(id).getCluster();
        double latDistance = 0;
        double longDistance = 0;
        for (Business experimentBusiness : mapOfBusiness.values()) {
            double controlLat = controlBusiness.getLatitude();
            double controlLong = controlBusiness.getLongitude();
            double experimentLat = experimentBusiness.getLatitude();
            double experimentLong = experimentBusiness.getLongitude();

            latDistance = (controlLat - experimentLat) * (Math.PI / 180);
            longDistance = (controlLong - experimentLong) * (Math.PI / 180);
            controlLat = (controlLat) * (Math.PI / 180);
            experimentLat = (experimentLat) * (Math.PI / 180);

            double a = Math.pow(Math.sin(latDistance / 2), 2) +
                    Math.pow(Math.sin(longDistance / 2), 2) *
                            Math.cos(controlLat) * Math.cos(experimentLat);
            double rad = 6371;
            double c = 2 * Math.asin(Math.sqrt(a));
            double distance = c * rad;
            closestBusiness.put(experimentBusiness.getId(), distance);
        }

        ArrayList<Double> sortedDistanceList = new ArrayList<>();
        sortedDistanceList.addAll(closestBusiness.values());
        Collections.sort(sortedDistanceList);

        for (int index = 0; index < sortedDistanceList.size(); index += 125) {
            for (String orderedBusinessID : mapOfBusiness.keySet()) {
                if (closestBusiness.get(orderedBusinessID) == sortedDistanceList.get(index)) {
                    if (!orderedBusinessID.equals(controlBusiness.getId())) {
                        // Creates Disjoint Sets (Used for Union Find)
                        mapOfBusiness.get(controlBusiness.getId()).setClosestBusiness(orderedBusinessID, sortedDistanceList.get(index));
                    }
                }
            }
            if (mapOfBusiness.get(controlBusiness.getId()).getClosestBusiness().size() > 3) {
                break;
            }
        }
    }

    public static void createPaths(String rootBusiness) {
        int SIZE = 5;
        int SOURCE = 0;
        Business destination = mapOfBusiness.get(rootBusiness);
        System.out.println(rootBusiness);

        List<List<Node>> adj = new ArrayList<List<Node>>();
        for (int i = 0; i < SIZE; i++) {
            List<Node> item = new ArrayList<Node>();
            adj.add(item);
        }

        int index = 1;
        for (Double x : destination.getClosestBusiness().values()) {
            adj.get(0).add(new Node(index, x.intValue()));
            index++;
        }

        Graph dpq = new Graph(SIZE);
        dpq.dijkstra(adj, SOURCE);

        mapOfBusiness.get(rootBusiness).setBusinessGraphNodes(adj);

        for (int i = 0; i < dpq.dist.length; i++) {
            System.out.println(SOURCE + " to " + i + " is "
                    + dpq.dist[i]);
        }
    }

    public static String findPath(String startingID, String destinationID) {
        //rZ4WHc8fcopYKCGi7ahwzA = Flying J Travel Center
        String output = "";
//        String startingID = "IX25aSHBIfYd9fbSKlP4qg";
//        String destinationID = "rZ4WHc8fcopYKCGi7ahwzA";

        List<List<Node>> startingGraphNodes = mapOfBusiness.get(startingID).getBusinessGraphNodes();
        List<List<Node>> destinationGraphNodes = mapOfBusiness.get(destinationID).getBusinessGraphNodes();
        Graph startingGraph = new Graph(5);
        Graph destinationGraph = new Graph(5);
        startingGraph.dijkstra(startingGraphNodes, 0);
        destinationGraph.dijkstra(destinationGraphNodes, 0);
        HashMap<String, Double> startingBusinessNeighbors = mapOfBusiness.get(startingID).getClosestBusiness();
        HashMap<String, Double> destinationBusinessNeighbors = mapOfBusiness.get(destinationID).getClosestBusiness();
        System.out.println(mapOfBusiness.get(startingID).getClosestBusiness());
        System.out.println(mapOfBusiness.get(destinationID).getClosestBusiness());

        int startingIndex = 1;
        int destinationIndex = 1;
        String connectorBusiness = "placeholder";
        boolean found = false;
        for (String x : startingBusinessNeighbors.keySet()) {

            for (String y : destinationBusinessNeighbors.keySet()) {
                if (x.equals(y)) {
                    found = true;
                    connectorBusiness = x;
                }
                if (!found) {
                    destinationIndex++;
                }
            }
            if (!found) {
                startingIndex++;
            }
        }
        destinationIndex = destinationIndex / startingIndex;
//        System.out.println(startingIndex + " and " + destinationIndex);

        if (!connectorBusiness.equals("placeholder")) {
            output = mapOfBusiness.get(startingID).getName() + " ==> \n" + mapOfBusiness.get(connectorBusiness).getName() + " ==>  \n" + mapOfBusiness.get(destinationID).getName();
        }
        return output;
    }

    //DEBUG:
    public static void businessCluster(String id) {
        geographicCluster(id);
        int cluster = mapOfBusiness.get(id).getCluster();
        for (Business x : mapOfBusiness.values()) {
            if (x.getCluster() == cluster) {
                geographicCluster(x.getId());
            }
        }
    }

    public static ArrayList<String> allBusinessUnionFind(String sourceID) {
        Business sourceBusiness = mapOfBusiness.get(sourceID);
        boolean valid = false;
        ArrayList<String> validBusinessNames = new ArrayList<>();

        for (Business business : mapOfBusiness.values()) {
            valid = false;
            for (String parent : sourceBusiness.getClosestBusinessSet().parent.keySet()) {
                try {
                    business.getClosestBusinessSet().find(parent);
                    valid = true;
                } catch (NullPointerException e) {
                    continue;
                }
            }
            if (valid) validBusinessNames.add(business.getName());
        }
        return validBusinessNames;
    }
}