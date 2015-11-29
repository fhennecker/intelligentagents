/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 */
package tau.tac.adx.auction.manager;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Message;
import tau.tac.adx.auction.manager.AdxBidManager;
import tau.tac.adx.auction.tracker.AdxBidTracker;
import tau.tac.adx.auction.tracker.AdxSpendTracker;
import tau.tac.adx.bids.BidInfo;
import tau.tac.adx.props.AdxBidBundle;
import tau.tac.adx.props.AdxQuery;

public class AdxBidManagerImpl
implements AdxBidManager {
    private final Logger log = Logger.getLogger(AdxBidManagerImpl.class.getName());
    private final Set<String> advertisers;
    private final Set<String> advertisersView;
    private final List<Message> bidBundleList;
    private final AdxBidTracker bidTracker;
    private final AdxSpendTracker spendTracker;

    @Inject
    public AdxBidManagerImpl(AdxBidTracker bidTracker, AdxSpendTracker spendTracker) {
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
    public BidInfo getBidInfo(String advertiser, AdxQuery query) {
        BidInfo bidInfo = this.bidTracker.getBidInfo(advertiser, query);
        if (bidInfo == null || this.isOverspent(bidInfo.getBid(), advertiser, query)) {
            return null;
        }
        return bidInfo;
    }

    @Override
    public void updateBids(String advertiser, AdxBidBundle bundle) {
        Message m = new Message(advertiser, advertiser, bundle);
        this.bidBundleList.add(m);
    }

    @Override
    public Set<String> advertisers() {
        return this.advertisers;
    }

    @Override
    public void nextTimeUnit(int timeUnit) {
    }

    @Override
    public void applyBidUpdates() {
        for (Message m : this.bidBundleList) {
            this.bidTracker.updateBids(m.getSender(), (AdxBidBundle)m.getContent());
        }
        this.bidBundleList.clear();
    }

    @Override
    public void addAdvertiser(String advertiser) {
        this.advertisers.add(advertiser);
        this.bidTracker.addAdvertiser(advertiser);
        this.spendTracker.addAdvertiser(advertiser);
    }

    private boolean isOverspent(double bid, String advertiser, AdxQuery query) {
        return false;
    }
}

