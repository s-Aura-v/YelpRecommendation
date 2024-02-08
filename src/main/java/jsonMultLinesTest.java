import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class jsonMultLinesTest {

    public static void main(String[] args) throws IOException {
        Gson gson = new Gson();
        BufferedReader buffRead;
        JsonObject[] objects = new JsonObject[10001];
        int i = 0;
        try {
            buffRead = new BufferedReader(new FileReader("/Users/survive/Desktop/EEATO/24Spring/CSC365/FebProjectFiles/yelp_dataset/yelp_academic_dataset_business.json"));
            while (i < 10000) {
                String line = buffRead.readLine();
                objects[i] = gson.fromJson(line, JsonObject.class);
                i++;
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        for (JsonObject z : objects) {
            System.out.println(z);
        }




//        FileReader reader = new FileReader("/Users/survive/Desktop/EEATO/24Spring/CSC365/FebProjectFiles/yelp_dataset/test.json");
//        JsonReader tester = new JsonReader(reader).setLenient(true);
//        System.out.println(reader);

    }
}
