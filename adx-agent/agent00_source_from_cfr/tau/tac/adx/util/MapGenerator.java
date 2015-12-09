/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MapGenerator<T> {
    Random random = new Random();

    public Map<T, Integer> randomizeWeightMap(T[] objects, int maxWeight) {
        HashMap<T, Integer> weights = new HashMap<T, Integer>();
        T[] arrT = objects;
        int n = arrT.length;
        int n2 = 0;
        while (n2 < n) {
            T object = arrT[n2];
            weights.put(object, this.random.nextInt(maxWeight));
            ++n2;
        }
        return weights;
    }

    public Map<T, Double> randomizeProbabilityMap(T[] objects) {
        HashMap<T, Double> weights = new HashMap<T, Double>();
        T[] arrT = objects;
        int n = arrT.length;
        int n2 = 0;
        while (n2 < n) {
            T object = arrT[n2];
            weights.put(object, this.random.nextDouble());
            ++n2;
        }
        return weights;
    }
}

