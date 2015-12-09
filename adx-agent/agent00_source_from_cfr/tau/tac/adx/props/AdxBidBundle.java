/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.props;

import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.KeyedEntry;
import edu.umich.eecs.tac.props.Product;
import java.text.ParseException;
import java.util.Set;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;
import tau.tac.adx.AdxManager;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.bids.BidInfo;
import tau.tac.adx.bids.BidProduct;
import tau.tac.adx.bids.Bidder;
import tau.tac.adx.demand.Campaign;
import tau.tac.adx.devices.Device;
import tau.tac.adx.props.AdxAbstractQueryEntry;
import tau.tac.adx.props.AdxAbstractQueryKeyedReportTransportable;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.report.adn.MarketSegment;

public class AdxBidBundle
extends AdxAbstractQueryKeyedReportTransportable<BidEntry> {
    public static final double PERSISTENT_SPEND_LIMIT = Double.NaN;
    public static final double PERSISTENT_BID = Double.NaN;
    public static final String CMP_DSL = "CMP_SET_DAILY_LIMIT";
    public static final String CMP_TSL = "CMP_SET_TOTAL_LIMIT";
    public static final Ad PERSISTENT_AD = null;
    private static final int PERSISTENT_WEIGHT = 1;
    public static final double NO_SPEND_LIMIT = Double.POSITIVE_INFINITY;
    public static final double NO_SHOW_BID = 0.0;
    private static final long serialVersionUID = 5057969669832603679L;
    private double campaignTotalSpendLimit;
    private double campaignDailySpendLimit = Double.NaN;
    private int advertiserId;

    @Override
    protected final BidEntry createEntry(AdxQuery key) {
        BidEntry entry = new BidEntry();
        entry.setQuery(key);
        return entry;
    }

    @Override
    protected final Class entryClass() {
        return BidEntry.class;
    }

    public final void addQuery(AdxQuery query, double bid, Ad ad, int campaignId, int weight) {
        this.addQuery(query, bid, ad, campaignId, weight, Double.NaN);
    }

    public final void addQuery(AdxQuery query, double bid, Ad ad, int campaignId, int weight, double dailyLimit) {
        int index = this.addQuery(query);
        BidEntry entry = (BidEntry)this.getEntry(index);
        entry.setQuery(query);
        entry.setBid(bid);
        entry.setAd(ad);
        entry.setDailyLimit(dailyLimit);
        entry.setCampaignId(campaignId);
        entry.setWeight(weight);
    }

    public final void setBid(AdxQuery query, double bid) {
        this.lockCheck();
        int index = this.indexForEntry(query);
        if (index < 0) {
            index = this.addQuery(query);
        }
        this.setBid(index, bid);
    }

    public final void setBid(int index, double bid) {
        this.lockCheck();
        ((BidEntry)this.getEntry(index)).setBid(bid);
    }

    public final void setAd(AdxQuery query, Ad ad) {
        this.lockCheck();
        int index = this.indexForEntry(query);
        if (index < 0) {
            index = this.addQuery(query);
        }
        this.setAd(index, ad);
    }

    public final void setAd(int index, Ad ad) {
        this.lockCheck();
        ((BidEntry)this.getEntry(index)).setAd(ad);
    }

    public final void setDailyLimit(AdxQuery query, double dailyLimit) {
        this.lockCheck();
        int index = this.indexForEntry(query);
        if (index < 0) {
            index = this.addQuery(query);
        }
        this.setDailyLimit(index, dailyLimit);
    }

    public final void setDailyLimit(int index, double dailyLimit) {
        this.lockCheck();
        ((BidEntry)this.getEntry(index)).setDailyLimit(dailyLimit);
    }

    public final void setBidAndAd(AdxQuery query, double bid, Ad ad) {
        this.lockCheck();
        int index = this.indexForEntry(query);
        if (index < 0) {
            index = this.addQuery(query);
        }
        this.setBidAndAd(index, bid, ad);
    }

    public final void setBidAndAd(int index, double bid, Ad ad) {
        this.lockCheck();
        BidEntry entry = (BidEntry)this.getEntry(index);
        entry.setBid(bid);
        entry.setAd(ad);
    }

    public final double getBid(AdxQuery query) {
        int index = this.indexForEntry(query);
        return index < 0 ? Double.NaN : this.getBid(index);
    }

    public final double getBid(int index) {
        return ((BidEntry)this.getEntry(index)).getBid();
    }

    public final double getWeight(AdxQuery query) {
        int index = this.indexForEntry(query);
        return index < 0 ? Double.NaN : this.getWeight(index);
    }

    public final double getWeight(int index) {
        return ((BidEntry)this.getEntry(index)).getWeight();
    }

    public final double getCampaignId(AdxQuery query) {
        int index = this.indexForEntry(query);
        return index < 0 ? Double.NaN : this.getCampaignId(index);
    }

    public final double getCampaignId(int index) {
        return ((BidEntry)this.getEntry(index)).getCampaignId();
    }

    public final Ad getAd(AdxQuery query) {
        int index = this.indexForEntry(query);
        return index < 0 ? PERSISTENT_AD : this.getAd(index);
    }

    public BidInfo getBidInfo(AdxQuery query) {
        int index = this.indexForEntry(query);
        if (index < 0) {
            return null;
        }
        BidEntry bidEntry = (BidEntry)this.getEntry(index);
        BidInfo bidInfo = new BidInfo(bidEntry.getBid(), AdxManager.getInstance().getBidder(this.advertiserId), (BidProduct)bidEntry.getAd(), bidEntry.getMarketSegments(), AdxManager.getInstance().getCampaign(bidEntry.getCampaignId()));
        return bidInfo;
    }

    public final Ad getAd(int index) {
        return ((BidEntry)this.getEntry(index)).getAd();
    }

    public final double getDailyLimit(AdxQuery query) {
        int index = this.indexForEntry(query);
        return index < 0 ? Double.NaN : this.getDailyLimit(index);
    }

    public final double getDailyLimit(int index) {
        return ((BidEntry)this.getEntry(index)).getDailyLimit();
    }

    public final double getCampaignDailySpendLimit() {
        return this.campaignDailySpendLimit;
    }

    public void setCampaignDailyLimit(int campaignId, int impstogo, double campaignDailySpendLimit) {
        this.addQuery(new AdxQuery("CMP_SET_DAILY_LIMIT" + campaignId, MarketSegment.FEMALE, Device.mobile, AdType.text), 0.0, new Ad(null), campaignId, impstogo, campaignDailySpendLimit);
    }

    public void setCampaignTotalLimit(int campaignId, int totalimps, double campaignTotalSpendLimit) {
        this.addQuery(new AdxQuery("CMP_SET_TOTAL_LIMIT" + campaignId, MarketSegment.FEMALE, Device.mobile, AdType.text), 0.0, new Ad(null), campaignId, totalimps, campaignTotalSpendLimit);
    }

    public final void setCampaignDailySpendLimit(double campaignDailySpendLimit) {
        this.lockCheck();
        this.campaignDailySpendLimit = campaignDailySpendLimit;
    }

    @Override
    protected final void readBeforeEntries(TransportReader reader) throws ParseException {
        this.campaignDailySpendLimit = reader.getAttributeAsDouble("campaignDailySpendLimit", Double.NaN);
        this.advertiserId = reader.getAttributeAsInt("advertiserId", -1);
    }

    @Override
    protected final void writeBeforeEntries(TransportWriter writer) {
        writer.attr("campaignDailySpendLimit", this.campaignDailySpendLimit);
        writer.attr("advertiserId", this.advertiserId);
    }

    @Override
    protected final void toStringBeforeEntries(StringBuilder builder) {
        builder.append(" limit: ").append(this.campaignDailySpendLimit);
    }

    public static class BidEntry
    extends AdxAbstractQueryEntry {
        private Ad ad;
        private double bid;
        private double dailyLimit;
        private int campaignId;
        private int weight;

        public BidEntry() {
            this.bid = Double.NaN;
            this.dailyLimit = Double.NaN;
            this.ad = AdxBidBundle.PERSISTENT_AD;
            this.weight = 1;
        }

        public BidEntry(Ad ad, double bid, double dailyLimit, int campaignId, int weight) {
            this.ad = ad;
            this.bid = bid;
            this.dailyLimit = dailyLimit;
            this.campaignId = campaignId;
            this.weight = weight;
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + (this.ad == null ? 0 : this.ad.hashCode());
            long temp = Double.doubleToLongBits(this.bid);
            result = 31 * result + (int)(temp ^ temp >>> 32);
            result = 31 * result + this.campaignId;
            temp = Double.doubleToLongBits(this.dailyLimit);
            result = 31 * result + (int)(temp ^ temp >>> 32);
            result = 31 * result + this.weight;
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
            BidEntry other = (BidEntry)obj;
            if (this.ad == null ? other.ad != null : !this.ad.equals(other.ad)) {
                return false;
            }
            if (Double.doubleToLongBits(this.bid) != Double.doubleToLongBits(other.bid)) {
                return false;
            }
            if (this.campaignId != other.campaignId) {
                return false;
            }
            if (Double.doubleToLongBits(this.dailyLimit) != Double.doubleToLongBits(other.dailyLimit)) {
                return false;
            }
            if (this.weight != other.weight) {
                return false;
            }
            return true;
        }

        public int getWeight() {
            return this.weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public final Ad getAd() {
            return this.ad;
        }

        public final void setAd(Ad ad) {
            this.ad = ad;
        }

        public final double getBid() {
            return this.bid;
        }

        public final void setBid(double bid) {
            this.bid = bid;
        }

        public int getCampaignId() {
            return this.campaignId;
        }

        public void setCampaignId(int campaignId) {
            this.campaignId = campaignId;
        }

        public Set<MarketSegment> getMarketSegments() {
            return ((AdxQuery)this.getKey()).getMarketSegments();
        }

        @Override
        protected final void readEntry(TransportReader reader) throws ParseException {
            this.bid = reader.getAttributeAsDouble("bid", Double.NaN);
            this.dailyLimit = reader.getAttributeAsDouble("dailyLimit", Double.NaN);
            this.campaignId = reader.getAttributeAsInt("campaignId", 0);
            this.weight = reader.getAttributeAsInt("weight", 1);
            if (reader.nextNode("Ad", false)) {
                this.ad = (Ad)reader.readTransportable();
            }
        }

        @Override
        protected final void writeEntry(TransportWriter writer) {
            writer.attr("bid", this.bid);
            writer.attr("dailyLimit", this.dailyLimit);
            writer.attr("campaignId", this.campaignId);
            writer.attr("weight", this.weight);
            if (this.ad != null) {
                writer.write(this.ad);
            }
        }

        public final double getDailyLimit() {
            return this.dailyLimit;
        }

        public final void setDailyLimit(double dailyLimit) {
            this.dailyLimit = dailyLimit;
        }

        public String toString() {
            return "BidEntry [ad=" + this.ad + ", bid=" + this.bid + ", dailyLimit=" + this.dailyLimit + " , weight=" + this.weight + "]";
        }
    }

}

