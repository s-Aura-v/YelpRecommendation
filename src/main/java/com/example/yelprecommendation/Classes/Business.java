package com.example.yelprecommendation.Classes;

import java.io.*;
import java.util.HashMap;
import java.util.List;

public class Business implements Serializable {
    @Serial
    private static final long serialVersionUID = 9999L;
    private String review;
    private String id;
    private String name;
    private HT termCount;
    private HashMap<String, Double> termFrequency;
    private HashMap<String, Double> tfIDF;
    private double similarityValue;
    private int cluster; // 0 - 4
    private double latitude;
    private double longitude;
    private HashMap<String, Double> closestBusiness;
    private DisjointUnionSets closestBusinessSet;
    private List<List<Node>> businessGraphNodes;

    public Business(String id, String review) {
        this.id = id;
        this.review = review;
        termCount = new HT();
        tfIDF = new HashMap<>();
        closestBusiness = new HashMap<>();
    }

    public List<List<Node>> getBusinessGraphNodes() {
        return businessGraphNodes;
    }

    public void setBusinessGraphNodes(List<List<Node>> businessGraphNodes) {
        this.businessGraphNodes = businessGraphNodes;
    }

    public HashMap<String, Double> getClosestBusiness() {
        return closestBusiness;
    }

    public void setClosestBusiness(String businessID, double distance) {
        closestBusiness.put(businessID, distance);
        String[] elements = closestBusiness.keySet().toArray(new String[0]);
        if (elements.length == 4) {
            closestBusinessSet = new DisjointUnionSets(elements);
        }
    }
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getCluster() {
        return cluster;
    }
    public void setCluster(int cluster) {
        this.cluster = cluster;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public double getSimilarityValue() {
        return similarityValue;
    }
    public void setSimilarityValue(double similarityValue) {
        this.similarityValue = similarityValue;
    }
    public void setTermCount(HT termFrequency) {
        this.termCount.copyHT(termFrequency);
    }
    public HT getTermCount() {
        return termCount;
    }
    public void setTermFrequency(HashMap<String, Double> frequencyForWords) {
        this.termFrequency = frequencyForWords;
    }
    public void addToTfIDF(String word, double tfIDFValue) {
        this.tfIDF.put(word, tfIDFValue);
    }
    public HashMap<String, Double> getTfIDF() {
        return tfIDF;
    }
    public HashMap<String, Double> getTermFrequency() {
        return termFrequency;
    }
    public String getId() {
        return id;
    }
    public String getReview() {
        return review;
    }

    @Override
    public String toString() {
        return "Business{" +
                "review='" + this.review + '\n' +
                ", id='" + this.id + '\n' +
                ", name='" + this.name + '\n' +
                ", similarity=" + this.similarityValue + '\n' +
                ", closestBusiness: " + closestBusiness + '\n' +
                '}';
    }
    public void serializeBusiness(String inputtedID) throws IOException {
        // Directory
        String newFolderDirectory = System.getProperty("user.dir") + "/SerializedDocuments/" + inputtedID;
//        if (Files.exists(Path.of(System.getProperty("user.dir") + "/SerializedDocuments/" + inputtedID))) {
////            loadSerialization(inputtedID);
//            return;
//        } else {
            File createDirectory = new File(newFolderDirectory);
            createDirectory.mkdir();
//        }

        FileOutputStream fileOut;
        try {
            //HrIbP2-jdRJAU92yqyDmyw
            // Redudant file directory because Java is weird with reading folders...
            fileOut = new FileOutputStream(System.getProperty("user.dir") + "/SerializedDocuments/" + inputtedID + "/" + this.id);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        ObjectOutput out;
        try {
            out = new ObjectOutputStream(fileOut);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        out.writeObject(this);
        out.close();
        fileOut.close();
    }

    public Business loadSerialization(String businessID) {
        //HrIbP2-jdRJAU92yqyDmyw
        Business business;
        String fileDirectory = System.getProperty("user.dir") + "/SerializedDocuments/" + businessID;
        try {
            FileInputStream fileIn = new FileInputStream(fileDirectory + "/" + this.id);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            business = (Business) in.readObject();
            in.close();
            fileIn.close();
            setBusiness(business);
            return business;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public void setBusiness(Business business) {
        this.name = business.name;
        this.id = business.id;
        this.review = business.review;
        this.termCount = business.termCount;
        this.tfIDF = business.tfIDF;
        this.termFrequency = business.termFrequency;
    }



    public DisjointUnionSets getClosestBusinessSet() {
        return closestBusinessSet;
    }

    public Business getBusiness() {
        return this;
    }


}
