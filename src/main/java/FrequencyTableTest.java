import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FrequencyTableTest {

    public static void main(String[] args) throws FileNotFoundException {
//        //Map - Key Value Pairs
//        // key to value; string to int; what you recieve and what you want
//        String[] arr = {"Hello", "gppd", "nice", "fuck", "fuck", "fuck", "gppd"};
//        // HashMap< KeyType, DataType>
//        HashMap<String, Integer> map = new HashMap<>();
//        for (int i = 0; i < arr.length; i++) {
//            if (map.containsKey(arr[i])) {
//                map.put(arr[i], map.get(arr[i]) + 1);
//            } else {
//                map.put(arr[i], 1);
//            }
//        }
//        for (Map.Entry entry: map.entrySet()) {
//            System.out.println("Element | Frequency");
//            System.out.println(entry.getKey() + " " + entry.getValue());
//        }
//        //Random method tests
//        System.out.println(map.keySet() + " and " + map.values() + " and " + map.size());


        // Trying to use a file reader
        JsonParser parser = new JsonParser();
        FileReader reader = new FileReader("/Users/survive/Desktop/EEATO/24Spring/CSC365/FebProjectFiles/yelp_dataset/test.json");

        Object obj = parser.parse(reader);
        System.out.println("Input: " + obj);
        JsonObject jsonObj = (JsonObject) obj;
        String categories = String.valueOf(jsonObj.get("categories"));
        System.out.println("Output: " + categories);
        String[] sortedCategories = categories.split(", ");
//        for (String i : sortedCategories) {
//            System.out.println("this is string " + i);
//        }
        System.out.println(sortedCategories.length);
        for (int i = 0; i < sortedCategories.length; i++) {
            System.out.println("this is string " + sortedCategories[i] + "length " + sortedCategories.length);
        }




    }
}
