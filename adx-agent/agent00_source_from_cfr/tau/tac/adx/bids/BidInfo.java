/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.bids;

import java.util.HashSet;
import java.util.Set;
import tau.tac.adx.bids.BidProduct;
import tau.tac.adx.bids.Bidder;
import tau.tac.adx.demand.Campaign;
import tau.tac.adx.report.adn.MarketSegment;

public class BidInfo
implements Cloneable {
    private final double bid;
    private final Bidder bidder;
    private final BidProduct bidProduct;
    private Set<MarketSegment> marketSegments;
    private Campaign campaign;

    public BidInfo(double bidPrice, Bidder bidder, BidProduct bidProduct, Set<MarketSegment> marketSegments, Campaign campaign) {
        this.bid = bidPrice;
        this.bidder = bidder;
        this.bidProduct = bidProduct;
        this.marketSegments = marketSegments;
        this.campaign = campaign;
    }

    public BidInfo(double bidPrice, Bidder bidder, BidProduct bidProduct, MarketSegment marketSegment, Campaign campaign) {
        this.bid = bidPrice;
        this.bidder = bidder;
        this.bidProduct = bidProduct;
        this.marketSegments = new HashSet<MarketSegment>();
        this.marketSegments.add(marketSegment);
        this.campaign = campaign;
    }

    public double getBid() {
        return this.bid;
    }

    public Bidder getBidder() {
        return this.bidder;
    }

    public BidProduct getBidProduct() {
        return this.bidProduct;
    }

    public Set<MarketSegment> getMarketSegments() {
        return this.marketSegments;
    }

    public void setMarketSegments(Set<MarketSegment> marketSegments) {
        this.marketSegments = marketSegments;
    }

    public Campaign getCampaign() {
        return this.campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public Object clone() {
        return new BidInfo(this.bid, this.bidder, this.bidProduct, this.marketSegments, this.campaign);
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        long temp = Double.doubleToLongBits(this.bid);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        result = 31 * result + (this.bidProduct == null ? 0 : this.bidProduct.hashCode());
        result = 31 * result + (this.bidder == null ? 0 : this.bidder.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        BidInfo other = (BidInfo)obj;
        if (Double.doubleToLongBits(this.bid) != Double.doubleToLongBits(other.bid)) {
            return false;
        }
        if (this.bidProduct == null ? other.bidProduct != null : !this.bidProduct.equals(other.bidProduct)) {
            return false;
        }
        if (this.bidder == null ? other.bidder != null : !this.bidder.equals(other.bidder)) {
            return false;
        }
        return true;
    }
}

