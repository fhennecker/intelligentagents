/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.publishers.reserve;

public interface MultiReservePriceManager<T> {
    public double generateReservePrice(T var1);

    public void addImpressionForPrice(double var1, T var3);

    public void updateDailyBaselineAverage();

    public double getDailyBaselineAverage(T var1);
}

