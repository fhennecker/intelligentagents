/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.auction.data;

import java.util.List;
import tau.tac.adx.auction.data.AuctionOrder;
import tau.tac.adx.auction.data.AuctionPriceType;
import tau.tac.adx.bids.BidInfo;

public class AuctionData {
    AuctionOrder auctionOrder;
    AuctionPriceType auctionPriceType;
    List<BidInfo> bidInfos;
    Double reservePrice;

    public AuctionData(AuctionOrder auctionOrder, AuctionPriceType auctionPriceType, List<BidInfo> bidInfos, Double reservePrice) {
        this.auctionOrder = auctionOrder;
        this.auctionPriceType = auctionPriceType;
        this.bidInfos = bidInfos;
        this.reservePrice = reservePrice;
    }

    public AuctionOrder getAuctionOrder() {
        return this.auctionOrder;
    }

    public void setAuctionOrder(AuctionOrder auctionOrder) {
        this.auctionOrder = auctionOrder;
    }

    public AuctionPriceType getAuctionPriceType() {
        return this.auctionPriceType;
    }

    public void setAuctionPriceType(AuctionPriceType auctionPriceType) {
        this.auctionPriceType = auctionPriceType;
    }

    public List<BidInfo> getBidInfos() {
        return this.bidInfos;
    }

    public void setBidInfos(List<BidInfo> bidInfos) {
        this.bidInfos = bidInfos;
    }

    public Double getReservePrice() {
        return this.reservePrice;
    }

    public void setReservePrice(Double reservePrice) {
        this.reservePrice = reservePrice;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.auctionOrder == null ? 0 : this.auctionOrder.hashCode());
        result = 31 * result + (this.auctionPriceType == null ? 0 : this.auctionPriceType.hashCode());
        result = 31 * result + (this.reservePrice == null ? 0 : this.reservePrice.hashCode());
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
        AuctionData other = (AuctionData)obj;
        if (this.auctionOrder != other.auctionOrder) {
            return false;
        }
        if (this.auctionPriceType != other.auctionPriceType) {
            return false;
        }
        if (this.reservePrice == null ? other.reservePrice != null : !this.reservePrice.equals(other.reservePrice)) {
            return false;
        }
        return true;
    }
}

