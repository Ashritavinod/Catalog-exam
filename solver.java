package com.assignment;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;

public class Solver {

    public static void main(String[] args) {
        solveForFile("testcase1.json");
        solveForFile("testcase2.json");
    }

    public static void solveForFile(String fileName) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(fileName)));
            JSONObject json = new JSONObject(content);

            int k = json.getJSONObject("keys").getInt("k");
            BigInteger[] x_values = new BigInteger[k];
            BigInteger[] y_values = new BigInteger[k];

            for (int i = 1; i <= k; i++) {
                String key = String.valueOf(i);
                JSONObject rootObject = json.getJSONObject(key);
                x_values[i - 1] = new BigInteger(key);
                int base = Integer.parseInt(rootObject.getString("base"));
                String value = rootObject.getString("value");
                y_values[i - 1] = new BigInteger(value, base);
            }

            BigDecimal secretC = lagrangeInterpolation(x_values, y_values);
            System.out.println("The secret for " + fileName + " is: " + secretC.toBigInteger());

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static BigDecimal lagrangeInterpolation(BigInteger[] x, BigInteger[] y) {
        BigDecimal sum = BigDecimal.ZERO;
        int k = x.length;
        int scale = 50;

        for (int i = 0; i < k; i++) {
            BigDecimal numerator = BigDecimal.ONE;
            BigDecimal denominator = BigDecimal.ONE;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    numerator = numerator.multiply(new BigDecimal(x[j]).negate());
                    denominator = denominator.multiply(new BigDecimal(x[i].subtract(x[j])));
                }
            }
            
            BigDecimal lagrangeBasis = numerator.divide(denominator, scale, RoundingMode.HALF_UP);
            BigDecimal term = new BigDecimal(y[i]).multiply(lagrangeBasis);
            sum = sum.add(term);
        }
        return sum;
    }
}
