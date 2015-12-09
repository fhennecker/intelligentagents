/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.auction.tracker;

import tau.tac.adx.props.AdxQuery;

public interface AdxSpendTracker {
    public void addAdvertiser(String var1);

    public double getDailyCost(String var1);

    public double getDailyCost(String var1, AdxQuery var2);

    public void reset();

    public void addCost(String var1, AdxQuery var2, double var3);

    public int size();
}

