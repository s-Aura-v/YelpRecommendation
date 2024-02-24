import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class InformationRetrieval {
    public static void main(String[] args) {
        // Take in Business ID and return Business
        HashMap<String, Business> mapOfBusiness = new HashMap<>();

        Gson gson = new Gson();
        BufferedReader buffRead;
        JsonObject[] businessData = new JsonObject[4];
        JsonObject[] businessReview = new JsonObject[4];

        System.out.println(System.getProperty("user.dir"));
        // 1. Get business name/id
        int index = 0;
        try {
            buffRead = new BufferedReader(new FileReader("src/main/java/jsonBusinessTest.json"));
            while (index < businessData.length) {
                String line = buffRead.readLine();
                businessData[index] = gson.fromJson(line, JsonObject.class);
                index++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < businessData.length; i++) {
            String name = String.join(" ", String.valueOf(businessData[i].get("name")).split("\\P{Alnum}+")).substring(1);
            //Note: Might need to implement the substring in a better way
            String id =  String.valueOf(businessData[i].get("business_id")).substring(1,23);
            mapOfBusiness.put(id, new Business(name, id));
            System.out.println(id + " " + name);
        }

        // 2. Get business id/reviews
        index = 0;
        try {
            buffRead = new BufferedReader(new FileReader("src/main/java/jsonReviewTest.json"));
            while (index < businessData.length) {
                String line = buffRead.readLine();
                businessReview[index] = gson.fromJson(line, JsonObject.class);
                index++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < businessReview.length; i++) {
            String id = String.valueOf(businessReview[i].get("business_id")).substring(1,23);
            String review = String.join(" ", String.valueOf(businessReview[i].get("text")).split("\\P{Alnum}+")).substring(1);
            mapOfBusiness.get(id).setReview(review);
        }

//        System.out.println(hashtable.get("Business@924"));
//        System.out.println(hashtable);


        // 2. Get Business review and add it to the correct class


    }



}
