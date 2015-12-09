/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.publishers.reserve;

import edu.umich.eecs.tac.util.config.ConfigProxy;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import tau.tac.adx.util.AdxUtils;

public class ReservePriceManager {
    private static final int DIGITS_AFTER_DECIMAL_POINT = 2;
    public static final String INITIAL_DAILY_BASELINE_AVERAGE = "INITIAL_DAILY_BASELINE_AVERAGE";
    private double dailyBaselineAverage;
    private final double baselineRange;
    private final double updateCoefficient;
    private final TreeMap<Double, AtomicLong> profitMap = new TreeMap();
    private int dailyRequests;
    ConfigProxy config;

    public ReservePriceManager(ConfigProxy config, double baselineRange, double updateCoefficient) {
        this.config = config;
        this.dailyBaselineAverage = config.getPropertyAsDouble("INITIAL_DAILY_BASELINE_AVERAGE", 0.0);
        this.baselineRange = baselineRange;
        this.updateCoefficient = updateCoefficient;
        this.dailyRequests = 0;
    }

    public ReservePriceManager(double dailyBaselineAverage, double baselineRange, double updateCoefficient) {
        this.dailyBaselineAverage = dailyBaselineAverage;
        this.baselineRange = baselineRange;
        this.updateCoefficient = updateCoefficient;
        this.dailyRequests = 0;
    }

    public synchronized double generateReservePrice() {
        ++this.dailyRequests;
        return AdxUtils.cutDouble(Math.random() * this.baselineRange * 2.0 + this.dailyBaselineAverage - this.baselineRange, 2);
    }

    public synchronized void addImpressionForPrice(double reservePrice) {
        if (!this.profitMap.containsKey(reservePrice)) {
            this.profitMap.put(reservePrice, new AtomicLong());
        }
        this.profitMap.get(reservePrice).incrementAndGet();
    }

    public double updateDailyBaselineAverage() {
        if (this.dailyRequests != 0) {
            double highestProfitsPrice = this.profitMap.size() == 0 ? 0.0 : this.getMostProfitableReservePrice();
            this.profitMap.clear();
            this.dailyBaselineAverage = this.updateCoefficient * this.dailyBaselineAverage + (1.0 - this.updateCoefficient) * highestProfitsPrice;
        }
        this.dailyRequests = 0;
        return this.dailyBaselineAverage;
    }

    public double getMostProfitableReservePrice() {
        double bestReservePrice = 0.0;
        long bestReservePriceImpresssions = 0;
        for (Map.Entry<Double, AtomicLong> entry : this.profitMap.entrySet()) {
            if (entry.getKey() * (double)entry.getValue().get() <= bestReservePrice * (double)bestReservePriceImpresssions) continue;
            bestReservePrice = entry.getKey();
            bestReservePriceImpresssions = entry.getValue().get();
        }
        return bestReservePrice;
    }

    public double getDailyBaselineAverage() {
        return this.dailyBaselineAverage;
    }
}

