/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.EventBus
 *  com.google.inject.Inject
 */
package tau.tac.adx.sim;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import se.sics.tasim.aw.TimeListener;
import tau.tac.adx.AdxManager;
import tau.tac.adx.auction.AdxAuctionResult;
import tau.tac.adx.auction.AuctionManager;
import tau.tac.adx.auction.data.AuctionData;
import tau.tac.adx.auction.data.AuctionOrder;
import tau.tac.adx.auction.data.AuctionPriceType;
import tau.tac.adx.auction.data.AuctionState;
import tau.tac.adx.auction.manager.AdxBidManager;
import tau.tac.adx.bids.BidInfo;
import tau.tac.adx.demand.UserClassificationService;
import tau.tac.adx.demand.UserClassificationServiceAdNetData;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.publishers.AdxPublisher;
import tau.tac.adx.publishers.reserve.UserAdTypeReservePriceManager;
import tau.tac.adx.report.adn.MarketSegment;
import tau.tac.adx.sim.AdxAuctioneer;
import tau.tac.adx.sim.TACAdxSimulation;

public class SimpleAdxAuctioneer
implements AdxAuctioneer,
TimeListener {
    private final AuctionManager auctionManager;
    private final AdxBidManager bidManager;
    private final Random random;

    @Inject
    public SimpleAdxAuctioneer(AuctionManager auctionManager, AdxBidManager bidManager, EventBus eventBus) {
        this.auctionManager = auctionManager;
        this.bidManager = bidManager;
        this.random = new Random();
        eventBus.register((Object)this);
    }

    @Override
    public AdxAuctionResult runAuction(AdxQuery query) {
        UserAdTypeReservePriceManager reservePriceManager;
        Double reservePrice;
        List<BidInfo> bidInfos = this.generateBidInfos(query);
        AuctionData auctionData = new AuctionData(AuctionOrder.HIGHEST_WINS, AuctionPriceType.GENERALIZED_SECOND_PRICE, bidInfos, reservePrice = Double.valueOf((reservePriceManager = AdxManager.getInstance().getPublisher(query.getPublisher()).getReservePriceManager()).generateReservePrice(query)));
        AdxAuctionResult auctionResult = this.auctionManager.runAuction(auctionData, query);
        if (auctionResult.getAuctionState() == AuctionState.AUCTION_COPMLETED) {
            reservePriceManager.addImpressionForPrice((double)reservePrice, query);
        }
        return auctionResult;
    }

    private List<BidInfo> generateBidInfos(AdxQuery query) {
        String[] advertisers;
        LinkedList<BidInfo> bidInfos = new LinkedList<BidInfo>();
        String[] arrstring = advertisers = AdxManager.getInstance().getSimulation().getAdxAdvertiserAddresses();
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String advertiser = arrstring[n2];
            AdxQuery classifiedQuery = this.getClassifiedQuery(advertiser, query);
            BidInfo bidInfo = this.bidManager.getBidInfo(advertiser, classifiedQuery);
            if (bidInfo != null) {
                bidInfos.add(bidInfo);
            }
            ++n2;
        }
        return bidInfos;
    }

    private AdxQuery getClassifiedQuery(String advertiser, AdxQuery query) {
        UserClassificationService userClassificationService = AdxManager.getInstance().getUserClassificationService();
        UserClassificationServiceAdNetData adNetData = userClassificationService.getAdNetData(advertiser);
        if (adNetData.getServiceLevel() <= this.random.nextDouble()) {
            return query;
        }
        AdxQuery clone = query.clone();
        clone.setMarketSegments(new HashSet<MarketSegment>());
        return query;
    }

    @Override
    public void nextTimeUnit(int timeUnit) {
    }

    @Override
    public void applyBidUpdates() {
        this.bidManager.applyBidUpdates();
    }
}

