import java.util.HashMap;
import java.util.Map;

public class FrequencyTableTest {

    public static void main(String[] args) {

        String[] arr = {"Hello", "gppd", "nice", "fuck", "fuck", "fuck", "gppd"};
        // HashMap< KeyType, DataType>
        HashMap<String, Integer> map = new HashMap<>();

        for (int i = 0; i <arr.length; i++) {
            if (map.containsKey(arr[i])) {
                map.put(arr[i], map.get(arr[i]) + 1);
            } else {
                map.put(arr[i], 1);
            }
        }

        for (Map.Entry entry: map.entrySet()) {
            System.out.println("Element | Frequency");
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }
}
