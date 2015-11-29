/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 */
package tau.tac.adx.auction;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import tau.tac.adx.auction.AdxAuctionResult;
import tau.tac.adx.auction.AuctionManager;
import tau.tac.adx.auction.data.AuctionData;
import tau.tac.adx.auction.data.AuctionOrder;
import tau.tac.adx.auction.data.AuctionPriceType;
import tau.tac.adx.auction.data.AuctionState;
import tau.tac.adx.bids.BidInfo;
import tau.tac.adx.bids.BidProduct;
import tau.tac.adx.bids.Bidder;
import tau.tac.adx.demand.Campaign;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.report.adn.MarketSegment;

public class SimpleAuctionManager
implements AuctionManager {
    private static /* synthetic */ int[] $SWITCH_TABLE$tau$tac$adx$auction$data$AuctionOrder;
    private static /* synthetic */ int[] $SWITCH_TABLE$tau$tac$adx$auction$data$AuctionPriceType;

    @Override
    public AdxAuctionResult runAuction(AuctionData auctionData, AdxQuery query) {
        BidInfo winningBid = this.initializeByAuctionOrder(auctionData.getAuctionOrder());
        BidInfo secondBid = this.initializeByAuctionOrder(auctionData.getAuctionOrder());
        List<BidInfo> bidInfos = auctionData.getBidInfos();
        Collections.shuffle(bidInfos);
        for (BidInfo bidInfo : bidInfos) {
            if (this.betterBid(bidInfo, winningBid, auctionData.getAuctionOrder())) {
                secondBid = winningBid;
                winningBid = bidInfo;
                continue;
            }
            if (!this.betterBid(bidInfo, secondBid, auctionData.getAuctionOrder())) continue;
            secondBid = bidInfo;
        }
        BidInfo adjustedWinningBid = (BidInfo)winningBid.clone();
        Sets.SetView marketSegments = Sets.intersection(query.getMarketSegments(), winningBid.getMarketSegments());
        adjustedWinningBid.setMarketSegments((Set<MarketSegment>)marketSegments);
        return this.calculateAuctionResult(adjustedWinningBid, secondBid, auctionData);
    }

    protected boolean betterBid(BidInfo newBid, BidInfo oldBid, AuctionOrder auctionOrder) {
        return this.betterThan(newBid.getBid(), oldBid.getBid(), auctionOrder);
    }

    protected boolean betterThan(double first, double second, AuctionOrder auctionOrder) {
        switch (SimpleAuctionManager.$SWITCH_TABLE$tau$tac$adx$auction$data$AuctionOrder()[auctionOrder.ordinal()]) {
            case 1: {
                if (first > second) {
                    return true;
                }
                return false;
            }
            case 2: {
                if (first < second) {
                    return true;
                }
                return false;
            }
        }
        throw this.switchCaseException((Object)auctionOrder);
    }

    protected AdxAuctionResult calculateAuctionResult(BidInfo winningBid, BidInfo secondBid, AuctionData auctionData) {
        if (winningBid.equals(this.initializeByAuctionOrder(auctionData.getAuctionOrder()))) {
            return new AdxAuctionResult(AuctionState.NO_BIDS, null, Double.NaN, auctionData.getBidInfos());
        }
        if (!this.passedReservePrice(winningBid, auctionData)) {
            return new AdxAuctionResult(AuctionState.LOW_BIDS, null, null, auctionData.getBidInfos());
        }
        switch (SimpleAuctionManager.$SWITCH_TABLE$tau$tac$adx$auction$data$AuctionPriceType()[auctionData.getAuctionPriceType().ordinal()]) {
            case 1: {
                return new AdxAuctionResult(AuctionState.AUCTION_COPMLETED, winningBid, winningBid.getBid(), auctionData.getBidInfos());
            }
            case 2: {
                double winningPrice = !this.passedReservePrice(secondBid, auctionData) ? auctionData.getReservePrice().doubleValue() : secondBid.getBid();
                return new AdxAuctionResult(AuctionState.AUCTION_COPMLETED, winningBid, winningPrice, auctionData.getBidInfos());
            }
        }
        throw this.switchCaseException(auctionData);
    }

    protected BidInfo initializeByAuctionOrder(AuctionOrder auctionOrder) {
        switch (SimpleAuctionManager.$SWITCH_TABLE$tau$tac$adx$auction$data$AuctionOrder()[auctionOrder.ordinal()]) {
            case 1: {
                return new BidInfo(Double.MIN_VALUE, null, null, new HashSet<MarketSegment>(), null);
            }
            case 2: {
                return new BidInfo(Double.MAX_VALUE, null, null, new HashSet<MarketSegment>(), null);
            }
        }
        throw this.switchCaseException((Object)auctionOrder);
    }

    protected boolean passedReservePrice(BidInfo bid, AuctionData auctionData) {
        if (auctionData.getReservePrice().equals(Double.NaN)) {
            return true;
        }
        return this.betterThan(bid.getBid() / 1000.0, auctionData.getReservePrice(), auctionData.getAuctionOrder());
    }

    protected UnsupportedOperationException switchCaseException(Object object) {
        return new UnsupportedOperationException(String.valueOf(object.getClass().getName()) + " given type is not supporterd: " + object);
    }

    static /* synthetic */ int[] $SWITCH_TABLE$tau$tac$adx$auction$data$AuctionOrder() {
        int[] arrn;
        int[] arrn2 = $SWITCH_TABLE$tau$tac$adx$auction$data$AuctionOrder;
        if (arrn2 != null) {
            return arrn2;
        }
        arrn = new int[AuctionOrder.values().length];
        try {
            arrn[AuctionOrder.HIGHEST_WINS.ordinal()] = 1;
        }
        catch (NoSuchFieldError v1) {}
        try {
            arrn[AuctionOrder.LOWEST_WINS.ordinal()] = 2;
        }
        catch (NoSuchFieldError v2) {}
        $SWITCH_TABLE$tau$tac$adx$auction$data$AuctionOrder = arrn;
        return $SWITCH_TABLE$tau$tac$adx$auction$data$AuctionOrder;
    }

    static /* synthetic */ int[] $SWITCH_TABLE$tau$tac$adx$auction$data$AuctionPriceType() {
        int[] arrn;
        int[] arrn2 = $SWITCH_TABLE$tau$tac$adx$auction$data$AuctionPriceType;
        if (arrn2 != null) {
            return arrn2;
        }
        arrn = new int[AuctionPriceType.values().length];
        try {
            arrn[AuctionPriceType.GENERALIZED_FIRST_PRICE.ordinal()] = 1;
        }
        catch (NoSuchFieldError v1) {}
        try {
            arrn[AuctionPriceType.GENERALIZED_SECOND_PRICE.ordinal()] = 2;
        }
        catch (NoSuchFieldError v2) {}
        $SWITCH_TABLE$tau$tac$adx$auction$data$AuctionPriceType = arrn;
        return $SWITCH_TABLE$tau$tac$adx$auction$data$AuctionPriceType;
    }
}

