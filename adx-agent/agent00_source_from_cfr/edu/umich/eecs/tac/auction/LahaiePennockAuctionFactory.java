/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.auction.AuctionFactory;
import edu.umich.eecs.tac.auction.AuctionUtils;
import edu.umich.eecs.tac.auction.BidManager;
import edu.umich.eecs.tac.props.Auction;
import edu.umich.eecs.tac.props.Pricing;
import edu.umich.eecs.tac.props.PublisherInfo;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryType;
import edu.umich.eecs.tac.props.Ranking;
import edu.umich.eecs.tac.props.ReserveInfo;
import edu.umich.eecs.tac.props.SlotInfo;
import edu.umich.eecs.tac.util.config.ConfigProxy;
import java.util.Set;
import java.util.logging.Logger;
import tau.tac.adx.props.AdLink;

public class LahaiePennockAuctionFactory
implements AuctionFactory {
    private BidManager bidManager;
    private PublisherInfo publisherInfo;
    private SlotInfo slotInfo;
    private ReserveInfo reserveInfo;
    private Logger log = Logger.getLogger(LahaiePennockAuctionFactory.class.getName());

    @Override
    public Auction runAuction(Query query) {
        String[] advertisers = this.bidManager.advertisers().toArray(new String[0]);
        double[] qualityScores = new double[advertisers.length];
        double[] bids = new double[advertisers.length];
        double[] scores = new double[advertisers.length];
        double[] weight = new double[advertisers.length];
        boolean[] promoted = new boolean[advertisers.length];
        AdLink[] ads = new AdLink[advertisers.length];
        int[] indices = new int[advertisers.length];
        double[] cpc = new double[advertisers.length];
        int i = 0;
        while (i < advertisers.length) {
            bids[i] = this.bidManager.getBid(advertisers[i], query);
            qualityScores[i] = this.bidManager.getQualityScore(advertisers[i], query);
            ads[i] = this.bidManager.getAdLink(advertisers[i], query);
            weight[i] = Math.pow(qualityScores[i], this.publisherInfo.getSquashingParameter());
            scores[i] = weight[i] * bids[i];
            indices[i] = i++;
        }
        AuctionUtils.hardSort(scores, indices);
        AuctionUtils.generalizedSecondPrice(indices, weight, bids, cpc, promoted, this.slotInfo.getPromotedSlots(), this.reserveInfo.getPromotedReserve(query.getType()), this.slotInfo.getRegularSlots(), this.reserveInfo.getRegularReserve(query.getType()));
        Ranking ranking = new Ranking();
        Pricing pricing = new Pricing();
        int i2 = 0;
        while (i2 < indices.length && i2 < this.slotInfo.getRegularSlots()) {
            if (ads[indices[i2]] != null && !Double.isNaN(cpc[indices[i2]])) {
                AdLink ad = ads[indices[i2]];
                double price = cpc[indices[i2]];
                pricing.setPrice(ad, price);
                ranking.add(ad, promoted[indices[i2]]);
            }
            ++i2;
        }
        ranking.lock();
        pricing.lock();
        Auction auction = new Auction();
        auction.setQuery(query);
        auction.setPricing(pricing);
        auction.setRanking(ranking);
        auction.lock();
        return auction;
    }

    @Override
    public void configure(ConfigProxy configProxy) {
    }

    @Override
    public BidManager getBidManager() {
        return this.bidManager;
    }

    @Override
    public void setBidManager(BidManager bidManager) {
        this.bidManager = bidManager;
    }

    @Override
    public PublisherInfo getPublisherInfo() {
        return this.publisherInfo;
    }

    @Override
    public void setPublisherInfo(PublisherInfo publisherInfo) {
        this.publisherInfo = publisherInfo;
    }

    @Override
    public SlotInfo getSlotInfo() {
        return this.slotInfo;
    }

    @Override
    public void setSlotInfo(SlotInfo slotInfo) {
        this.slotInfo = slotInfo;
    }

    @Override
    public ReserveInfo getReserveInfo() {
        return this.reserveInfo;
    }

    @Override
    public void setReserveInfo(ReserveInfo reserveInfo) {
        this.reserveInfo = reserveInfo;
    }
}

