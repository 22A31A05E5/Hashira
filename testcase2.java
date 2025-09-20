import org.json.JSONObject;
import java.math.BigInteger;
import java.util.*;

public class SecretSharing {
    public static void main(String[] args) {
        // ---- JSON Input for Test Case 2 ----
        String jsonInput = "{ \"keys\": { \"n\": 10, \"k\": 7 }, " +
            "\"1\": { \"base\": \"7\", \"value\": \"420020006424065463\" }, " +
            "\"2\": { \"base\": \"7\", \"value\": \"10511630252064643035\" }, " +
            "\"3\": { \"base\": \"2\", \"value\": \"101010101001100101011100000001000111010010111101100100010\" }, " +
            "\"4\": { \"base\": \"8\", \"value\": \"31261003022226126015\" }, " +
            "\"5\": { \"base\": \"7\", \"value\": \"2564201006101516132035\" }, " +
            "\"6\": { \"base\": \"15\", \"value\": \"a3c97ed550c69484\" }, " +
            "\"7\": { \"base\": \"13\", \"value\": \"134b08c8739552a734\" }, " +
            "\"8\": { \"base\": \"10\", \"value\": \"23600283241050447333\" }, " +
            "\"9\": { \"base\": \"9\", \"value\": \"375870320616068547135\" }, " +
            "\"10\": { \"base\": \"6\", \"value\": \"30140555423010311322515333\" } }";

        // ---- Parse JSON ----
        JSONObject obj = new JSONObject(jsonInput);
        JSONObject keys = obj.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");

        // ---- Extract (x, y) points ----
        List<Point> points = new ArrayList<>();
        for (String key : obj.keySet()) {
            if (key.equals("keys")) continue;
            JSONObject root = obj.getJSONObject(key);
            int base = Integer.parseInt(root.getString("base"));
            String val = root.getString("value");

            // Convert value from base → decimal (BigInteger for large numbers)
            BigInteger y = new BigInteger(val, base);
            int x = Integer.parseInt(key);

            points.add(new Point(x, y));
        }

        // ---- Sort points by x ----
        points.sort(Comparator.comparingInt(p -> p.x));

        // ---- Take first k points ----
        List<Point> subset = points.subList(0, k);

        // ---- Use Lagrange interpolation at x=0 ----
        BigInteger secret = lagrangeInterpolation(subset, BigInteger.ZERO);

        // ---- Print secret key ----
        System.out.println("Secret = " + secret);
    }

    // ---- Helper class ----
    static class Point {
        int x;
        BigInteger y;
        Point(int x, BigInteger y) { this.x = x; this.y = y; }
    }

    // ---- Lagrange interpolation ----
    public static BigInteger lagrangeInterpolation(List<Point> pts, BigInteger atX) {
        BigInteger result = BigInteger.ZERO;
        int k = pts.size();

        for (int i = 0; i < k; i++) {
            BigInteger term = pts.get(i).y;

            for (int j = 0; j < k; j++) {
                if (i == j) continue;

                BigInteger numerator = atX.subtract(BigInteger.valueOf(pts.get(j).x));
                BigInteger denominator = BigInteger.valueOf(pts.get(i).x - pts.get(j).x);

                // Multiply term with fraction (numerator/denominator)
                term = term.multiply(numerator).divide(denominator);
            }

            result = result.add(term);
        }
        return result;
    }
}
