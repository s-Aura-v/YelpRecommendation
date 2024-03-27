import java.io.*;
import java.util.HashMap;

public class Business implements java.io.Serializable {
    private String review;
    private String id;
    private String name;
    private final HT termCount;
    private HashMap<String, Double> termFrequency;
    private final HashMap<String, Double> tfIDF;
    public Business(String id, String review) {
        this.id = id;
        this.review = review;
        termCount = new HT();
        tfIDF = new HashMap<>();
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
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
    public void serializeBusiness(String inputtedID) throws IOException {
        FileOutputStream fileOut;
        try {
            //HrIbP2-jdRJAU92yqyDmyw
            fileOut = new FileOutputStream(System.getProperty("user.dir") + "/SerializedDocuments/" + inputtedID + "__" + this.id);
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
}
