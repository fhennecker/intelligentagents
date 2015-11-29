/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.auction;

import com.botbox.util.ArrayUtils;
import edu.umich.eecs.tac.auction.BidTracker;
import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.BidBundle;
import edu.umich.eecs.tac.props.Query;
import java.util.Set;
import java.util.logging.Logger;
import tau.tac.adx.props.AdLink;

public class BidTrackerImpl
implements BidTracker {
    private static final double DEFAULT_SPEND_LIMIT = Double.POSITIVE_INFINITY;
    private static final double DEFAULT_BID = 0.0;
    private static final Ad DEFAULT_AD = new Ad();
    private Logger logger = Logger.getLogger(BidTrackerImpl.class.getName());
    private String[] advertisers;
    private int advertisersCount;
    private QueryBid[] queryBid;
    private Query[] querySpace;

    public BidTrackerImpl() {
        this(0);
    }

    public BidTrackerImpl(int advertisersCount) {
        this.advertisersCount = advertisersCount;
        this.advertisers = new String[advertisersCount];
        this.queryBid = new QueryBid[advertisersCount];
    }

    @Override
    public void initializeQuerySpace(Set<Query> space) {
        if (this.querySpace == null) {
            this.querySpace = space.toArray(new Query[0]);
        } else {
            this.logger.warning("Attempt to re-initialize query space");
        }
    }

    private synchronized int doAddAdvertiser(String advertiser) {
        if (this.advertisersCount == this.advertisers.length) {
            int newSize = this.advertisersCount + 8;
            this.advertisers = (String[])ArrayUtils.setSize(this.advertisers, newSize);
            this.queryBid = (QueryBid[])ArrayUtils.setSize(this.queryBid, newSize);
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
            this.queryBid[index] = new QueryBid(advertiser, 0);
        }
        return this.queryBid[index].getCampaignSpendLimit();
    }

    @Override
    public double getDailySpendLimit(String advertiser, Query query) {
        int index = ArrayUtils.indexOf(this.advertisers, 0, this.advertisersCount, advertiser);
        if (index < 0) {
            return Double.POSITIVE_INFINITY;
        }
        if (this.queryBid[index] == null) {
            this.queryBid[index] = new QueryBid(advertiser, 0);
        }
        return this.queryBid[index].getSpendLimits(query);
    }

    @Override
    public double getBid(String advertiser, Query query) {
        int index = ArrayUtils.indexOf(this.advertisers, 0, this.advertisersCount, advertiser);
        if (index < 0) {
            return 0.0;
        }
        if (this.queryBid[index] == null) {
            this.queryBid[index] = new QueryBid(advertiser, 0);
        }
        return this.queryBid[index].getBid(query);
    }

    @Override
    public AdLink getAdLink(String advertiser, Query query) {
        int index = ArrayUtils.indexOf(this.advertisers, 0, this.advertisersCount, advertiser);
        if (index < 0) {
            return new AdLink(DEFAULT_AD, advertiser);
        }
        if (this.queryBid[index] == null) {
            this.queryBid[index] = new QueryBid(advertiser, 0);
        }
        return this.queryBid[index].getAdLink(query);
    }

    @Override
    public void updateBids(String advertiser, BidBundle bundle) {
        int index = ArrayUtils.indexOf(this.advertisers, 0, this.advertisersCount, advertiser);
        if (index < 0) {
            index = this.doAddAdvertiser(advertiser);
        }
        if (this.queryBid[index] == null) {
            this.queryBid[index] = new QueryBid(advertiser, 0);
        }
        if (!Double.isNaN(bundle.getCampaignDailySpendLimit())) {
            this.queryBid[index].setCampaignSpendLimit(bundle.getCampaignDailySpendLimit());
        }
        Query[] arrquery = this.querySpace;
        int n = arrquery.length;
        int n2 = 0;
        while (n2 < n) {
            Query query = arrquery[n2];
            Ad ad = bundle.getAd(query);
            double dailyLimit = bundle.getDailyLimit(query);
            double bid = bundle.getBid(query);
            if (ad != null) {
                this.queryBid[index].setAd(query, ad);
            }
            if (bid >= 0.0) {
                this.queryBid[index].setBid(query, bid);
            }
            if (dailyLimit >= 0.0) {
                this.queryBid[index].setSpendLimit(query, dailyLimit);
            }
            ++n2;
        }
    }

    @Override
    public int size() {
        return this.advertisersCount;
    }

    private static class QueryBid {
        private String advertiser;
        private Query[] queries;
        private AdLink[] adLinks;
        private double[] spendLimits;
        private double[] bids;
        private int queryCount;
        private double campaignSpendLimit;

        public QueryBid(String advertiser, int queryCount) {
            this.advertiser = advertiser;
            this.queries = new Query[queryCount];
            this.adLinks = new AdLink[queryCount];
            this.spendLimits = new double[queryCount];
            this.bids = new double[queryCount];
            this.queryCount = queryCount;
            this.campaignSpendLimit = Double.POSITIVE_INFINITY;
        }

        private synchronized int doAddQuery(Query query) {
            if (this.queryCount == this.queries.length) {
                int newSize = this.queryCount + 8;
                this.queries = (Query[])ArrayUtils.setSize(this.queries, newSize);
                this.adLinks = (AdLink[])ArrayUtils.setSize(this.adLinks, newSize);
                this.spendLimits = ArrayUtils.setSize(this.spendLimits, newSize);
                this.bids = ArrayUtils.setSize(this.bids, newSize);
                int i = this.queryCount;
                while (i < newSize) {
                    this.bids[i] = 0.0;
                    this.spendLimits[i] = Double.POSITIVE_INFINITY;
                    this.adLinks[i] = new AdLink(DEFAULT_AD, this.advertiser);
                    ++i;
                }
            }
            this.queries[this.queryCount] = query;
            return this.queryCount++;
        }

        protected void setBid(Query query, double bid) {
            int index = ArrayUtils.indexOf(this.queries, 0, this.queryCount, query);
            if (index < 0) {
                index = this.doAddQuery(query);
            }
            this.bids[index] = bid;
        }

        protected double getBid(Query query) {
            int index = ArrayUtils.indexOf(this.queries, 0, this.queryCount, query);
            if (index < 0) {
                return 0.0;
            }
            return this.bids[index];
        }

        protected void setSpendLimit(Query query, double spendLimit) {
            int index = ArrayUtils.indexOf(this.queries, 0, this.queryCount, query);
            if (index < 0) {
                index = this.doAddQuery(query);
            }
            this.spendLimits[index] = spendLimit;
        }

        protected double getSpendLimits(Query query) {
            int index = ArrayUtils.indexOf(this.queries, 0, this.queryCount, query);
            if (index < 0) {
                return Double.POSITIVE_INFINITY;
            }
            return this.spendLimits[index];
        }

        protected void setAd(Query query, Ad ad) {
            int index = ArrayUtils.indexOf(this.queries, 0, this.queryCount, query);
            if (index < 0) {
                index = this.doAddQuery(query);
            }
            this.adLinks[index] = new AdLink(ad, this.advertiser);
        }

        protected AdLink getAdLink(Query query) {
            int index = ArrayUtils.indexOf(this.queries, 0, this.queryCount, query);
            if (index < 0) {
                return new AdLink(DEFAULT_AD, this.advertiser);
            }
            return this.adLinks[index];
        }

        public double getCampaignSpendLimit() {
            return this.campaignSpendLimit;
        }

        protected void setCampaignSpendLimit(double campaignSpendLimit) {
            this.campaignSpendLimit = campaignSpendLimit;
        }
    }

}

