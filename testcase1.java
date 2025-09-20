import org.json.JSONObject;
import java.util.*;

public class SecretSharing {
    public static void main(String[] args) {
        // ---- Sample JSON Input ----
        String jsonInput = "{ \"keys\": { \"n\": 4, \"k\": 3 }, " +
            "\"1\": { \"base\": \"10\", \"value\": \"4\" }, " +
            "\"2\": { \"base\": \"2\", \"value\": \"111\" }, " +
            "\"3\": { \"base\": \"10\", \"value\": \"12\" }, " +
            "\"6\": { \"base\": \"4\", \"value\": \"213\" } }";

        // ---- Parse JSON ----
        JSONObject obj = new JSONObject(jsonInput);
        JSONObject keys = obj.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");

        // ---- Extract (x, y) points ----
        List<int[]> points = new ArrayList<>();
        for (String key : obj.keySet()) {
            if (key.equals("keys")) continue;
            JSONObject root = obj.getJSONObject(key);
            int base = Integer.parseInt(root.getString("base"));
            String val = root.getString("value");
            int y = Integer.parseInt(val, base);  // convert from base -> decimal
            int x = Integer.parseInt(key);        // key itself is x
            points.add(new int[]{x, y});
        }

        // ---- Pick first k points ----
        List<int[]> subset = points.subList(0, k);

        // ---- Apply Lagrange interpolation at x=0 to get the secret ----
        double secret = lagrangeInterpolation(subset, 0);

        // ---- Print the result ----
        System.out.println("Secret = " + (int) Math.round(secret));
    }

    // ---- Lagrange interpolation ----
    public static double lagrangeInterpolation(List<int[]> pts, int atX) {
        double result = 0;
        int k = pts.size();

        for (int i = 0; i < k; i++) {
            int xi = pts.get(i)[0];
            int yi = pts.get(i)[1];
            double term = yi;

            for (int j = 0; j < k; j++) {
                if (i == j) continue;
                int xj = pts.get(j)[0];
                term *= (double) (atX - xj) / (xi - xj);
            }
            result += term;
        }
        return result;
    }
}
