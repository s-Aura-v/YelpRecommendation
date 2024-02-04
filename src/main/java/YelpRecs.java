import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class YelpRecs {

    public static void main(String args[]) throws FileNotFoundException {
//        GUI one = new GUI(500,400);
//        one.setUpGUI();

        JsonParser parser = new JsonParser();
        FileReader reader = new FileReader("/Users/survive/Desktop/EEATO/24Spring/CSC365/FebProjectFiles/yelp_dataset/test.json");

        Object obj = parser.parse(reader);
        System.out.println(obj);

        JsonObject jsonObj = (JsonObject) obj;

        String name = String.valueOf(jsonObj.get("firstName"));
        System.out.println(name);

//    // Serialization
//        Gson gson = new Gson();
//        gson.toJson(1);            // ==> 1
//        gson.toJson("abcd");       // ==> "abcd"
//        gson.toJson(new Long(10)); // ==> 10
//        int[] values = { 1 };
//        gson.toJson(values);       // ==> [1]
//
//    // Deserialization
//        int i = gson.fromJson("1", int.class);
//        Integer intObj = gson.fromJson("1", Integer.class);
//        Long longObj = gson.fromJson("1", Long.class);
//        Boolean boolObj = gson.fromJson("false", Boolean.class);
//        String str = gson.fromJson("\"abc\"", String.class);
//        String[] strArray = gson.fromJson("[\"abc\"]", String[].class);
    }
}
