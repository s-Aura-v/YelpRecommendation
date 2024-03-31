package com.example.yelprecommendation.Classes;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

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

    public Business(String id, String review) {
        this.id = id;
        this.review = review;
        termCount = new HT();
        tfIDF = new HashMap<>();
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
                "review='" + this.review + '\'' +
                ", id='" + this.id + '\'' +
                ", name='" + this.name + '\'' +
                ", similarity=" + this.similarityValue + '\'' +
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
            // TODO: Ask DL about this way to set class to itself...
            setBusiness(business);
            return business;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private void setBusiness(Business business) {
        this.name = business.name;
        this.id = business.id;
        this.review = business.review;
        this.termCount = business.termCount;
        this.tfIDF = business.tfIDF;
        this.termFrequency = business.termFrequency;
    }
}
