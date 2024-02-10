import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.*;

public class jsonMultLinesTest {

    public static void main(String[] args) throws IOException {
        Gson gson = new Gson();
        BufferedReader buffRead;
        JsonObject[] objects = new JsonObject[10001];
        int index = 0;
        try {
            buffRead = new BufferedReader(new FileReader("/Users/survive/Desktop/EEATO/24Spring/CSC365/FebProjectFiles/yelp_dataset/yelp_academic_dataset_business.json"));
            while (index < objects.length) {
                String line = buffRead.readLine();
                objects[index] = gson.fromJson(line, JsonObject.class);
                index++;
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        ArrayList<HashMap<String,Integer>> listOfKeywords = new ArrayList<>();
        for (int x = 0; x < objects.length; x++) {
            if (objects[x] != null) {
                frequencyTable(String.valueOf(objects[x].get("categories")).split("\\P{Alnum}+"));
                listOfKeywords.add(frequencyTable(String.valueOf(objects[x].get("categories")).split("\\P{Alnum}+")));
            }
        }
        for (int z = 0; z < listOfKeywords.size(); z++) {
            System.out.println(listOfKeywords.get(z));
        }

    }

    public static HashMap frequencyTable(String[] sortedCategories) {
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
        return map;
    }
}
