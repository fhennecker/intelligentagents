/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.AbstractQueryEntry;
import edu.umich.eecs.tac.props.AbstractQueryKeyedReportTransportable;
import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.KeyedEntry;
import edu.umich.eecs.tac.props.Query;
import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

public class BidBundle
extends AbstractQueryKeyedReportTransportable<BidEntry> {
    public static final double PERSISTENT_SPEND_LIMIT = Double.NaN;
    public static final double PERSISTENT_BID = Double.NaN;
    public static final Ad PERSISTENT_AD = null;
    public static final double NO_SPEND_LIMIT = Double.POSITIVE_INFINITY;
    public static final double NO_SHOW_BID = 0.0;
    private static final long serialVersionUID = 5057969669832603679L;
    private double campaignDailySpendLimit = Double.NaN;

    @Override
    protected final BidEntry createEntry(Query key) {
        BidEntry entry = new BidEntry();
        entry.setQuery(key);
        return entry;
    }

    @Override
    protected final Class entryClass() {
        return BidEntry.class;
    }

    public final void addQuery(Query query, double bid, Ad ad) {
        this.addQuery(query, bid, ad, Double.NaN);
    }

    public final void addQuery(Query query, double bid, Ad ad, double dailyLimit) {
        int index = this.addQuery(query);
        BidEntry entry = (BidEntry)this.getEntry(index);
        entry.setQuery(query);
        entry.setBid(bid);
        entry.setAd(ad);
        entry.setDailyLimit(dailyLimit);
    }

    public final void setBid(Query query, double bid) {
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

    public final void setAd(Query query, Ad ad) {
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

    public final void setDailyLimit(Query query, double dailyLimit) {
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

    public final void setBidAndAd(Query query, double bid, Ad ad) {
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

    public final double getBid(Query query) {
        int index = this.indexForEntry(query);
        return index < 0 ? Double.NaN : this.getBid(index);
    }

    public final double getBid(int index) {
        return ((BidEntry)this.getEntry(index)).getBid();
    }

    public final Ad getAd(Query query) {
        int index = this.indexForEntry(query);
        return index < 0 ? PERSISTENT_AD : this.getAd(index);
    }

    public final Ad getAd(int index) {
        return ((BidEntry)this.getEntry(index)).getAd();
    }

    public final double getDailyLimit(Query query) {
        int index = this.indexForEntry(query);
        return index < 0 ? Double.NaN : this.getDailyLimit(index);
    }

    public final double getDailyLimit(int index) {
        return ((BidEntry)this.getEntry(index)).getDailyLimit();
    }

    public final double getCampaignDailySpendLimit() {
        return this.campaignDailySpendLimit;
    }

    public final void setCampaignDailySpendLimit(double campaignDailySpendLimit) {
        this.lockCheck();
        this.campaignDailySpendLimit = campaignDailySpendLimit;
    }

    @Override
    protected final void readBeforeEntries(TransportReader reader) throws ParseException {
        this.campaignDailySpendLimit = reader.getAttributeAsDouble("campaignDailySpendLimit", Double.NaN);
    }

    @Override
    protected final void writeBeforeEntries(TransportWriter writer) {
        writer.attr("campaignDailySpendLimit", this.campaignDailySpendLimit);
    }

    @Override
    protected final void toStringBeforeEntries(StringBuilder builder) {
        builder.append(" limit: ").append(this.campaignDailySpendLimit);
    }

    public static class BidEntry
    extends AbstractQueryEntry {
        private Ad ad = BidBundle.PERSISTENT_AD;
        private double bid = Double.NaN;
        private double dailyLimit = Double.NaN;

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

        @Override
        protected final void readEntry(TransportReader reader) throws ParseException {
            this.bid = reader.getAttributeAsDouble("bid", Double.NaN);
            this.dailyLimit = reader.getAttributeAsDouble("dailyLimit", Double.NaN);
            if (reader.nextNode("Ad", false)) {
                this.ad = (Ad)reader.readTransportable();
            }
        }

        @Override
        protected final void writeEntry(TransportWriter writer) {
            writer.attr("bid", this.bid);
            writer.attr("dailyLimit", this.dailyLimit);
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

        public final String toString() {
            return String.format("(Bid query:%s ad:%s bid: %f limit: %f)", this.getQuery(), this.ad, this.bid, this.dailyLimit);
        }
    }

}

