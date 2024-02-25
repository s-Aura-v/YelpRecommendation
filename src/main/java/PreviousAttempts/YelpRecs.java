//package PreviousAttempts;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//
//import javax.swing.*;
//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//
//public class YelpRecs {
//
//    public static void main(String args[]) {
//        //Setup
//        GUI recommendation = new GUI(500,400);
//        recommendation.setUpGUI();
//        recommendation.storeString();
//        recommendation.setCloseButton();
//
//        Gson gson = new Gson();
//        BufferedReader buffRead;
//        JsonObject[] document = new JsonObject[10001];
//        int index = 0;
//        try {
//            buffRead = new BufferedReader(new FileReader("/Users/survive/Desktop/EEATO/24Spring/CSC365/FebProjectFiles/yelp_dataset/test.json"));
//            while (index < document.length) {
//                String line = buffRead.readLine();
//                document[index] = gson.fromJson(line, JsonObject.class);
//                index++;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        TF_IDF_Class frequency = new TF_IDF_Class(document);
//
//
//        String userInput = JOptionPane.showInputDialog("Please enter ...");
//
//        JsonElement element = gson.fromJson (userInput, JsonElement.class);
//        JsonObject jsonObj = element.getAsJsonObject();
//        System.out.println("printpls" + jsonObj);
//
////        TF_IDF_Class requestMap = new TF_IDF_Class(request);
////        System.out.println(frequency.createTFIDFMap());
//
//
//
//    }
//}
