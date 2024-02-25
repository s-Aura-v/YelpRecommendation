import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class InformationRetrieval {
    public static void main(String[] args) {
        // Take in Business ID and return Business
        HashMap<String, Business> mapOfBusiness = new HashMap<>();
        HT frequencyTable = new HT();

        Gson gson = new Gson();
        BufferedReader buffRead;
        JsonObject[] businessData = new JsonObject[4];
        JsonObject[] businessReview = new JsonObject[4];

//        System.out.println(System.getProperty("user.dir"));
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

        //3. Total Document Frequency Table
        // This is how many times the word appears throughout the entire document
        for (Business business : mapOfBusiness.values()) {
            for (String word : business.getReview().split("\\P{Alnum}+")) {
                if (frequencyTable.contains(word)) {
                    frequencyTable.setCount(word, frequencyTable.getCount(word)+1);
                } else {
                    frequencyTable.add(word, 1);
                }
            }
        }
        frequencyTable.printAll();

//        for (Object x : frequencyTable) {
//            String name = (String) x;
//            System.out.println(x);
//        }
//        for (Iterator<Integer> it = frequencyTable.countIterator(); it.hasNext(); ) {
//            Integer x = it.next();
//            System.out.println(x);
//        }

        
//        4. Term Frequency (Version 1)
//        for (Business business : mapOfBusiness.values()) {
//            HT termFrequencyInDoc = new HT();
//            for (String word : business.getReview().split("\\s+")) {
//                if (termFrequencyInDoc.contains(word)) {
//                    termFrequencyInDoc.setCount(word, frequencyTable.getCount(word)+1);
//                } else {
//                    termFrequencyInDoc.add(word, 1);
//                }
//            }
//            business.setTermFrequency(termFrequencyInDoc);
//        }
//        //Find term frequency ( term appeared / # of terms )
//        for (Business business : mapOfBusiness.values()) {
//        }


        // 4. Term Frequency Version 2
        for (Business business : mapOfBusiness.values()) {
            HashMap<String, Integer> termFrequency = new HashMap<>();
            HashMap<String, Double> termFrequencyTF = new HashMap<>();
            for (String word : business.getReview().split("\\P{Alnum}+")) {
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
        int size = frequencyTable.size;





    }




}
