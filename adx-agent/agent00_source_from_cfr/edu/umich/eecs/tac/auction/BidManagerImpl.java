/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.auction.BidManager;
import edu.umich.eecs.tac.auction.BidTracker;
import edu.umich.eecs.tac.auction.SpendTracker;
import edu.umich.eecs.tac.props.BidBundle;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.UserClickModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Message;
import tau.tac.adx.props.AdLink;

public class BidManagerImpl
implements BidManager {
    private Logger log = Logger.getLogger(BidManagerImpl.class.getName());
    private Set<String> advertisers;
    private Set<String> advertisersView;
    private List<Message> bidBundleList;
    private UserClickModel userClickModel;
    private BidTracker bidTracker;
    private SpendTracker spendTracker;

    public BidManagerImpl(UserClickModel userClickModel, BidTracker bidTracker, SpendTracker spendTracker) {
        if (userClickModel == null) {
            throw new NullPointerException("user click model cannot be null");
        }
        this.userClickModel = userClickModel;
        if (bidTracker == null) {
            throw new NullPointerException("bid tracker cannot be null");
        }
        this.bidTracker = bidTracker;
        if (spendTracker == null) {
            throw new NullPointerException("spend tracker cannot be null");
        }
        this.spendTracker = spendTracker;
        this.advertisers = new HashSet<String>();
        this.advertisersView = Collections.unmodifiableSet(this.advertisers);
        this.bidBundleList = new ArrayList<Message>();
    }

    @Override
    public double getBid(String advertiser, Query query) {
        double bid = this.bidTracker.getBid(advertiser, query);
        if (this.isOverspent(bid, advertiser, query)) {
            return 0.0;
        }
        return bid;
    }

    @Override
    public double getQualityScore(String advertiser, Query query) {
        int advertiserIndex = this.userClickModel.advertiserIndex(advertiser);
        int queryIndex = this.userClickModel.queryIndex(query);
        if (advertiserIndex < 0 || queryIndex < 0) {
            return 1.0;
        }
        return this.userClickModel.getAdvertiserEffect(queryIndex, advertiserIndex);
    }

    @Override
    public AdLink getAdLink(String advertiser, Query query) {
        return this.bidTracker.getAdLink(advertiser, query);
    }

    @Override
    public void updateBids(String advertiser, BidBundle bundle) {
        Message m = new Message(advertiser, advertiser, bundle);
        this.bidBundleList.add(m);
    }

    @Override
    public Set<String> advertisers() {
        return this.advertisersView;
    }

    @Override
    public void nextTimeUnit(int timeUnit) {
    }

    @Override
    public void applyBidUpdates() {
        for (Message m : this.bidBundleList) {
            this.bidTracker.updateBids(m.getSender(), (BidBundle)m.getContent());
        }
        this.bidBundleList.clear();
    }

    @Override
    public void addAdvertiser(String advertiser) {
        this.advertisers.add(advertiser);
        this.bidTracker.addAdvertiser(advertiser);
        this.spendTracker.addAdvertiser(advertiser);
    }

    private boolean isOverspent(double bid, String advertiser, Query query) {
        if (bid < this.bidTracker.getDailySpendLimit(advertiser, query) - this.spendTracker.getDailyCost(advertiser, query) && bid < this.bidTracker.getDailySpendLimit(advertiser) - this.spendTracker.getDailyCost(advertiser)) {
            return false;
        }
        return true;
    }
}

