/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.auction.tracker;

import java.util.Set;
import tau.tac.adx.bids.BidInfo;
import tau.tac.adx.props.AdxBidBundle;
import tau.tac.adx.props.AdxQuery;

public interface AdxBidTracker {
    public void addAdvertiser(String var1);

    public void initializeQuerySpace(Set<AdxQuery> var1);

    public double getDailySpendLimit(String var1);

    public BidInfo getBidInfo(String var1, AdxQuery var2);

    public void updateBids(String var1, AdxBidBundle var2);

    public int size();
}

