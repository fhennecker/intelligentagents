/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AdxUtils<T> {
    private static final int DECIMAL_MULTIPLIER = 10;

    public static double cutDouble(double d, int digitsAfterPoint) {
        return d * Math.pow(10.0, digitsAfterPoint) / Math.pow(10.0, digitsAfterPoint);
    }

    public static int sum(Collection<Integer> collection) {
        int sum = 0;
        for (Integer value : collection) {
            sum += value.intValue();
        }
        return sum;
    }

    public static double sumDoubles(Collection<Double> collection) {
        double sum = 0.0;
        for (Double value : collection) {
            sum += value.doubleValue();
        }
        return sum;
    }

    public static boolean withinEpsilon(double first, double second, double epsilon) {
        if (Math.abs(first - second) < epsilon) {
            return true;
        }
        return false;
    }

    public Map<T, Integer> initEmptyMap(Iterable<T> iterable) {
        HashMap<T, Integer> map = new HashMap<T, Integer>();
        for (T object : iterable) {
            map.put(object, 0);
        }
        return map;
    }

    public Map<T, Integer> initEmptyMap(T[] values) {
        HashMap<T, Integer> map = new HashMap<T, Integer>();
        T[] arrT = values;
        int n = arrT.length;
        int n2 = 0;
        while (n2 < n) {
            T object = arrT[n2];
            map.put(object, 0);
            ++n2;
        }
        return map;
    }
}

