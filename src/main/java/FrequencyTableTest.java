import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FrequencyTableTest {

    public static void main(String[] args) throws FileNotFoundException {
        //Convert JSON to String to StringArray
        Gson gson = new Gson();
        FileReader reader = new FileReader("/Users/survive/Desktop/EEATO/24Spring/CSC365/FebProjectFiles/yelp_dataset/test.json");
        JsonObject categories = gson.fromJson(reader, JsonObject.class);
        String[] sortedCategories = String.valueOf(categories.get("categories")).split(", ");
        for (String i : sortedCategories) {
            System.out.print(i + ", ");
        }
        System.out.println("\n");

        // Convert String to Frequency
        HashMap<String, Integer> map = new HashMap<>();
        for (int i = 0; i < sortedCategories.length; i++) {
            // If the map key exists, add 1 to its frequency
            if (map.containsKey(sortedCategories[i])) {
                map.put(sortedCategories[i], map.get(sortedCategories[i]) + 1);
            } else {
                // If the map key doesn't exist, set 1 to its frequency and add it to the map
                map.put(sortedCategories[i], 1);
            }
        }
        for (Map.Entry entry: map.entrySet()) {
            System.out.println("Element: " + entry.getKey() + "\n| Frequency: " + entry.getValue());
        }
        System.out.println();

        /*
         TFIDF(t,d,D) = tf(t,d) * idf(t,D
         tf(t,d) = n/N
         n is the number of times term t appears in the document d.
         N is the total number of terms in the document d.

         idf(t,D) = log (N/( n))
         N is the number of documents in the data set.
         n is the number of documents that contain the term t among the data set.
         */

        // Finding tf(t,d)
        HashMap<String, Double> termFrequency = new HashMap<>();
        int size = sortedCategories.length;
        for (int x = 0; x < sortedCategories.length; x++) {
            //If Map has the string value, get it's value and divide it by total N and
            if (map.containsKey(sortedCategories[x])) {
                double freq = map.get(sortedCategories[x])/(Double.valueOf(size));
                termFrequency.put(sortedCategories[x], freq);
            }
        }
        for (Map.Entry entry: termFrequency.entrySet()) {
            System.out.println("2: " + entry.getKey() + "\n| 2Freq: " + entry.getValue());
        }


        //Find idf(t,D)
        



        //Test for later
//        FileReader testReader = new FileReader("/Users/survive/Desktop/EEATO/24Spring/CSC365/FebProjectFiles/yelp_dataset/yelp_academic_dataset_review.json");
//        JsonObject builder = new GsonBuilder().create().fromJson(testReader, JsonObject.class);
//        JsonReader testReader2 = new JsonReader(testReader).setLenient(true);
//        JsonReader jsonReader = Json.createReader(new StringReader("[]"));
//        JsonObject reviews = builder.fromJson(testReader, JsonObject.class);
//        System.out.println(builder);
//        InputStream is = /* whatever */
//                InputStreamReader reee = new InputStreamReader(is, "UTF-8");
//        Gson gson = new GsonBuilder().create();
//        JsonStreamParser p = new JsonStreamParser(r);




    }
}
