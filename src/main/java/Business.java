public class Business {
    private String name;
    private String review;
    private String id;

    public Business(String name, String id) {
        this.name = name;
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setId(String id) {
        this.id = id;
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
