import java.util.HashMap;

public class Business {
    private String name;
    private String review;
    private String id;
    private HT termCount;
    private HashMap<String, Double> termFrequency;
    private HashMap<String, Double> inverseDocumentFrequency;

    public Business(String name, String id) {
        this.name = name;
        this.id = id;
        termCount = new HT();
    }

    public void setTermCount(HT termFrequency) {
        this.termCount.copyHT(termFrequency);
    }

    public HT getTermCount() {
        return termCount;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setTermFrequency(HashMap<String, Double> frequencyForWords) {
        this.termFrequency = frequencyForWords;
    }
    public HashMap<String, Double> getTermFrequency() {
        return termFrequency;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getReview() {
        return review;
    }


}
