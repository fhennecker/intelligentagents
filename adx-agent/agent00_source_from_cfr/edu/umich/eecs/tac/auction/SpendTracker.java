/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.props.Query;

public interface SpendTracker {
    public void addAdvertiser(String var1);

    public double getDailyCost(String var1);

    public double getDailyCost(String var1, Query var2);

    public void reset();

    public void addCost(String var1, Query var2, double var3);

    public int size();
}

