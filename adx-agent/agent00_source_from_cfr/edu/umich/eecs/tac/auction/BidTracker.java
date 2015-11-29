/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.props.BidBundle;
import edu.umich.eecs.tac.props.Query;
import java.util.Set;
import tau.tac.adx.props.AdLink;

public interface BidTracker {
    public void addAdvertiser(String var1);

    public void initializeQuerySpace(Set<Query> var1);

    public double getDailySpendLimit(String var1);

    public double getBid(String var1, Query var2);

    public double getDailySpendLimit(String var1, Query var2);

    public AdLink getAdLink(String var1, Query var2);

    public void updateBids(String var1, BidBundle var2);

    public int size();
}

