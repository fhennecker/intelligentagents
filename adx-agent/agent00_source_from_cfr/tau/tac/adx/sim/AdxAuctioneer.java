/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.sim;

import tau.tac.adx.auction.AdxAuctionResult;
import tau.tac.adx.props.AdxQuery;

public interface AdxAuctioneer {
    public AdxAuctionResult runAuction(AdxQuery var1);

    public void applyBidUpdates();
}

