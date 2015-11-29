/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.auction;

import java.util.Collection;
import java.util.Set;
import tau.tac.adx.Adx;
import tau.tac.adx.auction.data.AuctionResult;
import tau.tac.adx.auction.data.AuctionState;
import tau.tac.adx.bids.BidInfo;
import tau.tac.adx.demand.Campaign;
import tau.tac.adx.report.adn.MarketSegment;

public class AdxAuctionResult
implements AuctionResult<Adx> {
    private final AuctionState auctionState;
    private final BidInfo winningBidInfo;
    private final Double winningPrice;
    private final Collection<BidInfo> bidInfos;

    public AdxAuctionResult(AuctionState auctionState, BidInfo winningBidInfo, Double winningPrice, Collection<BidInfo> bidInfos) {
        this.auctionState = auctionState;
        this.winningBidInfo = winningBidInfo;
        this.winningPrice = winningPrice;
        this.bidInfos = bidInfos;
    }

    public Set<MarketSegment> getMarketSegments() {
        return this.winningBidInfo.getMarketSegments();
    }

    public AuctionState getAuctionState() {
        return this.auctionState;
    }

    public BidInfo getWinningBidInfo() {
        return this.winningBidInfo;
    }

    public Double getWinningPrice() {
        return this.winningPrice;
    }

    public Campaign getCampaign() {
        if (this.winningBidInfo == null) {
            return null;
        }
        return this.winningBidInfo.getCampaign();
    }

    public Collection<BidInfo> getBidInfos() {
        return this.bidInfos;
    }

    public String toString() {
        return "AdxAuctionResult [auctionState=" + (Object)((Object)this.auctionState) + ", winningBidInfo=" + this.winningBidInfo + ", winningPrice=" + this.winningPrice + ", bidInfos=" + this.bidInfos + "]";
    }
}

