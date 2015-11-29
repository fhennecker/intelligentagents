/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.util;

import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import tau.tac.adx.util.AdxUtils;

public class EnumGenerator<T> {
    private final Map<T, Integer> weights;
    private final int size;
    private final Random random = new Random();

    public EnumGenerator(Map<T, Integer> weights) {
        this.weights = weights;
        this.size = AdxUtils.sum(weights.values());
    }

    public T randomType() {
        int randomNum = this.random.nextInt(this.size);
        int currentWeightSumm = 0;
        for (Map.Entry<T, Integer> currentValue : this.weights.entrySet()) {
            if (randomNum >= currentWeightSumm && randomNum < currentWeightSumm + currentValue.getValue()) {
                return currentValue.getKey();
            }
            currentWeightSumm += currentValue.getValue().intValue();
        }
        return null;
    }
}

