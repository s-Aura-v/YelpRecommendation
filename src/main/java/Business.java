import java.util.HashMap;

public class Business {
//    private String name;
    private String review;
    private String id;
    private HT termCount;
    private HashMap<String, Double> termFrequency;
    private HashMap<String, Double> tfIDF;

    public Business(String id, String review) {
//        this.name = name;
        this.id = id;
        this.review = review;
        termCount = new HT();
        tfIDF = new HashMap<>();
    }

    public void setTermCount(HT termFrequency) {
        this.termCount.copyHT(termFrequency);
    }

    public HT getTermCount() {
        return termCount;
    }


    public void setId(String id) {
        this.id = id;
    }
    public void setTermFrequency(HashMap<String, Double> frequencyForWords) {
        this.termFrequency = frequencyForWords;
    }
    public void setReview(String review) {
        this.review = review;
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


    //    public void setName(String name) {
//        this.name = name;
//    }

    //    public String getName() {
//        return name;
//    }

}
