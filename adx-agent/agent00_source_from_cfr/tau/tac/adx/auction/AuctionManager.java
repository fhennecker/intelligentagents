/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.auction;

import tau.tac.adx.auction.AdxAuctionResult;
import tau.tac.adx.auction.data.AuctionData;
import tau.tac.adx.props.AdxQuery;

public interface AuctionManager {
    public AdxAuctionResult runAuction(AuctionData var1, AdxQuery var2);
}

