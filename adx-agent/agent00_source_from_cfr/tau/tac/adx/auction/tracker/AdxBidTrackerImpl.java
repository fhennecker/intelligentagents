/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Collections2
 *  com.google.common.eventbus.EventBus
 *  com.google.common.eventbus.Subscribe
 *  com.google.inject.Inject
 */
package tau.tac.adx.auction.tracker;

import com.botbox.util.ArrayUtils;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.util.sampling.WheelSampler;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import tau.tac.adx.AdxManager;
import tau.tac.adx.auction.tracker.AdxBidTracker;
import tau.tac.adx.bids.BidInfo;
import tau.tac.adx.bids.BidProduct;
import tau.tac.adx.bids.Bidder;
import tau.tac.adx.demand.Campaign;
import tau.tac.adx.messages.CampaignLimitReached;
import tau.tac.adx.messages.CampaignLimitSet;
import tau.tac.adx.props.AdxBidBundle;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.report.adn.MarketSegment;
import tau.tac.adx.sim.TACAdxSimulation;

public class AdxBidTrackerImpl
implements AdxBidTracker {
    private static final double DEFAULT_SPEND_LIMIT = Double.POSITIVE_INFINITY;
    private static final double DEFAULT_BID = 0.0;
    private static final Ad DEFAULT_AD = new Ad();
    private final Logger logger = Logger.getLogger(AdxBidTrackerImpl.class.getName());
    private String[] advertisers;
    private int advertisersCount;
    private AdxQueryBid[] queryBid;
    private AdxQuery[] querySpace;

    @Inject
    public AdxBidTrackerImpl() {
        this(0);
    }

    public AdxBidTrackerImpl(int advertisersCount) {
        this.advertisersCount = advertisersCount;
        this.advertisers = new String[advertisersCount];
        this.queryBid = new AdxQueryBid[advertisersCount];
    }

    @Override
    public void initializeQuerySpace(Set<AdxQuery> space) {
        if (this.querySpace == null) {
            this.querySpace = space.toArray(new AdxQuery[0]);
        } else {
            this.logger.warning("Attempt to re-initialize query space");
        }
    }

    private synchronized int doAddAdvertiser(String advertiser) {
        if (this.advertisersCount == this.advertisers.length) {
            int newSize = this.advertisersCount + 8;
            this.advertisers = (String[])ArrayUtils.setSize(this.advertisers, newSize);
            this.queryBid = (AdxQueryBid[])ArrayUtils.setSize(this.queryBid, newSize);
        }
        this.advertisers[this.advertisersCount] = advertiser;
        return this.advertisersCount++;
    }

    @Override
    public void addAdvertiser(String advertiser) {
        int index = ArrayUtils.indexOf(this.advertisers, 0, this.advertisersCount, advertiser);
        if (index < 0) {
            this.doAddAdvertiser(advertiser);
        }
    }

    @Override
    public double getDailySpendLimit(String advertiser) {
        int index = ArrayUtils.indexOf(this.advertisers, 0, this.advertisersCount, advertiser);
        if (index < 0) {
            return Double.POSITIVE_INFINITY;
        }
        if (this.queryBid[index] == null) {
            this.queryBid[index] = new AdxQueryBid(advertiser, 0);
        }
        return this.queryBid[index].getCampaignSpendLimit();
    }

    @Override
    public BidInfo getBidInfo(String advertiser, AdxQuery query) {
        int index = ArrayUtils.indexOf(this.advertisers, 0, this.advertisersCount, advertiser);
        if (index < 0) {
            return null;
        }
        if (this.queryBid[index] == null) {
            this.queryBid[index] = new AdxQueryBid(advertiser, 0);
        }
        return this.queryBid[index].generateBid(query);
    }

    @Override
    public void updateBids(String advertiser, AdxBidBundle bundle) {
        int index = ArrayUtils.indexOf(this.advertisers, 0, this.advertisersCount, advertiser);
        if (index < 0) {
            index = this.doAddAdvertiser(advertiser);
        }
        if (this.queryBid[index] == null) {
            this.queryBid[index] = new AdxQueryBid(advertiser, 0);
        }
        if (!Double.isNaN(bundle.getCampaignDailySpendLimit())) {
            this.queryBid[index].setCampaignSpendLimit(bundle.getCampaignDailySpendLimit());
        }
        this.queryBid[index].clearQueries();
        for (AdxQuery query : bundle) {
            AdxBidBundle.BidEntry entry = (AdxBidBundle.BidEntry)bundle.getEntry(query);
            if (entry == null) continue;
            this.queryBid[index].doAddQuery(entry);
        }
    }

    @Override
    public int size() {
        return this.advertisersCount;
    }

    private static class AdxQueryBid {
        private final String advertiser;
        private final double[] spendLimits;
        private final Set<AdxBidBundle.BidEntry> querySet = new HashSet<AdxBidBundle.BidEntry>();
        private final Map<AdxQuery, WheelSampler<AdxBidBundle.BidEntry>> queryMap = new HashMap<AdxQuery, WheelSampler<AdxBidBundle.BidEntry>>();
        private final int queryCount;
        private double campaignSpendLimit;
        private final Bidder bidder;
        private final Set<Integer> excludedCampaigns = new HashSet<Integer>();

        public AdxQueryBid(final String advertiser, int queryCount) {
            this.advertiser = advertiser;
            this.spendLimits = new double[queryCount];
            this.queryCount = queryCount;
            this.campaignSpendLimit = Double.POSITIVE_INFINITY;
            this.bidder = new Bidder(){

                @Override
                public String getName() {
                    return advertiser;
                }
            };
            TACAdxSimulation.eventBus.register((Object)this);
        }

        @Subscribe
        public void limitReached(CampaignLimitReached message) {
            if (message.getAdNetwork().equals(this.advertiser)) {
                this.excludedCampaigns.add(message.getCampaignId());
                this.queryMap.clear();
            }
        }

        public void clearQueries() {
            this.querySet.clear();
            this.queryMap.clear();
            this.excludedCampaigns.clear();
        }

        public BidInfo generateBid(AdxQuery query) {
            AdxBidBundle.BidEntry sample;
            WheelSampler sampler = this.queryMap.get(query);
            if (sampler == null) {
                sampler = new WheelSampler();
                this.queryMap.put(query, sampler);
                Collection filteredQueries = Collections2.filter(this.querySet, (Predicate)new BidPredicate(query, this.excludedCampaigns));
                for (AdxBidBundle.BidEntry bidEntry : filteredQueries) {
                    sampler.addState(bidEntry.getWeight(), bidEntry);
                }
            }
            if ((sample = sampler.getSample()) == null) {
                return null;
            }
            BidInfo bidInfo = new BidInfo(sample.getBid(), this.bidder, (BidProduct)sample.getAd(), sample.getMarketSegments(), AdxManager.getInstance().getCampaign(sample.getCampaignId()));
            return bidInfo;
        }

        private synchronized void doAddQuery(AdxBidBundle.BidEntry entry) {
            if (((AdxQuery)entry.getKey()).getPublisher().startsWith("CMP_SET_DAILY_LIMIT")) {
                AdxManager.getInstance().getSimulation().getEventBus().post((Object)new CampaignLimitSet(false, entry.getCampaignId(), this.advertiser, entry.getWeight(), entry.getDailyLimit()));
            } else if (((AdxQuery)entry.getKey()).getPublisher().startsWith("CMP_SET_TOTAL_LIMIT")) {
                AdxManager.getInstance().getSimulation().getEventBus().post((Object)new CampaignLimitSet(true, entry.getCampaignId(), this.advertiser, entry.getWeight(), entry.getDailyLimit()));
            } else {
                this.querySet.add(entry);
            }
        }

        public double getCampaignSpendLimit() {
            return this.campaignSpendLimit;
        }

        protected void setCampaignSpendLimit(double campaignSpendLimit) {
            this.campaignSpendLimit = campaignSpendLimit;
        }

        private class BidPredicate
        implements Predicate<AdxBidBundle.BidEntry> {
            private final AdxQuery adxQuery;
            private final Set<Integer> excludedCampaigns;

            public BidPredicate(AdxQuery adxQuery, Set<Integer> excludedCampaigns) {
                this.adxQuery = adxQuery;
                this.excludedCampaigns = excludedCampaigns;
            }

            public boolean apply(AdxBidBundle.BidEntry input) {
                if (this.adxQuery.getMarketSegments().containsAll(input.getMarketSegments()) && !this.excludedCampaigns.contains(input.getCampaignId())) {
                    return true;
                }
                return false;
            }
        }

    }

}

