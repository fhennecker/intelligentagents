/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.demand;

public interface QualityManager {
    public void addAdvertiser(String var1);

    public double updateQualityScore(String var1, Double var2);

    public double getQualityScore(String var1);

    public String logToString();
}

