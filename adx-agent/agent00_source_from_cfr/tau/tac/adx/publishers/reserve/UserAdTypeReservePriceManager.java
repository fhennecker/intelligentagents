/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.publishers.reserve;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.publishers.reserve.MultiReservePriceManager;
import tau.tac.adx.publishers.reserve.ReservePriceManager;
import tau.tac.adx.publishers.reserve.ReservePriceType;

public class UserAdTypeReservePriceManager
implements MultiReservePriceManager<AdxQuery> {
    private Map<ReservePriceType, ReservePriceManager> reservePriceManagers = new HashMap<ReservePriceType, ReservePriceManager>();
    private double dailyBaselineAverage;
    private final double baselineRange;
    private final double updateCoefficient;

    public UserAdTypeReservePriceManager(double dailyBaselineAverage, double baselineRange, double updateCoefficient) {
        this.dailyBaselineAverage = dailyBaselineAverage;
        this.baselineRange = baselineRange;
        this.updateCoefficient = updateCoefficient;
    }

    @Override
    public double generateReservePrice(AdxQuery adxQuery) {
        ReservePriceManager reservePriceManager = this.getReservePriceManager(adxQuery);
        return reservePriceManager.generateReservePrice();
    }

    @Override
    public void addImpressionForPrice(double reservePrice, AdxQuery adxQuery) {
        ReservePriceManager reservePriceManager = this.getReservePriceManager(adxQuery);
        reservePriceManager.addImpressionForPrice(reservePrice);
    }

    @Override
    public void updateDailyBaselineAverage() {
        for (ReservePriceManager reservePriceManager : this.reservePriceManagers.values()) {
            reservePriceManager.updateDailyBaselineAverage();
        }
    }

    @Override
    public double getDailyBaselineAverage(AdxQuery adxQuery) {
        ReservePriceManager reservePriceManager = this.getReservePriceManager(adxQuery);
        return reservePriceManager.getDailyBaselineAverage();
    }

    private synchronized ReservePriceManager getReservePriceManager(AdxQuery adxQuery) {
        ReservePriceManager reservePriceManager;
        ReservePriceType type = this.getType(adxQuery);
        if (!this.reservePriceManagers.containsKey(type)) {
            reservePriceManager = new ReservePriceManager(this.dailyBaselineAverage, this.baselineRange, this.updateCoefficient);
            this.reservePriceManagers.put(type, reservePriceManager);
        }
        reservePriceManager = this.reservePriceManagers.get(type);
        return reservePriceManager;
    }

    private ReservePriceType getType(AdxQuery adxQuery) {
        return new ReservePriceType(adxQuery);
    }
}

