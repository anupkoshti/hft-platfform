package org.pm.strategyengine.utils;


import java.util.Deque;

public class SMAUtils {
    public static double calculateSMA(Deque<Double> prices) {
        if (prices == null || prices.isEmpty()) return 0.0;
        return prices.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
}

