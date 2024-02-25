import java.util.HashMap;

public class Business {
    private String name;
    private String review;
    private String id;
    private HT termFrequency;
    private HashMap<String, Double> frequencyForWords;

    public Business(String name, String id) {
        this.name = name;
        this.id = id;
        termFrequency = new HT();
    }

    public void setTermFrequency(HT termFrequency) {
        this.termFrequency.copyHT(termFrequency);
    }

    public HT getTermFrequency() {
        return termFrequency;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setFrequencyForWords(HashMap<String, Double> frequencyForWords) {
        this.frequencyForWords = frequencyForWords;
    }
    public HashMap<String, Double> getFrequencyForWords() {
        return frequencyForWords;
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
