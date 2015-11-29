/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.AbstractAdvertiserEntry;
import edu.umich.eecs.tac.props.AbstractAdvertiserKeyedReportTransportable;
import edu.umich.eecs.tac.props.AbstractQueryEntry;
import edu.umich.eecs.tac.props.AbstractQueryKeyedReportTransportable;
import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.KeyedEntry;
import edu.umich.eecs.tac.props.Query;
import java.text.ParseException;
import java.util.Collections;
import java.util.Set;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

public class QueryReport
extends AbstractQueryKeyedReportTransportable<QueryReportEntry> {
    private static final long serialVersionUID = -7957495904471250085L;

    @Override
    protected final QueryReportEntry createEntry(Query query) {
        QueryReportEntry entry = new QueryReportEntry();
        entry.setQuery(query);
        return entry;
    }

    @Override
    protected final Class entryClass() {
        return QueryReportEntry.class;
    }

    public final void addQuery(Query query, int regularImpressions, int promotedImpressions, int clicks, double cost, double positionSum) {
        this.lockCheck();
        int index = this.addQuery(query);
        QueryReportEntry entry = (QueryReportEntry)this.getEntry(index);
        entry.setImpressions(regularImpressions, promotedImpressions);
        entry.setClicks(clicks);
        entry.setCost(cost);
        entry.setPositionSum(positionSum);
    }

    public final void setPositionSum(Query query, double positionSum) {
        this.lockCheck();
        int index = this.indexForEntry(query);
        if (index < 0) {
            index = this.addQuery(query);
        }
        this.setPositionSum(index, positionSum);
    }

    public final void setPositionSum(int index, double positionSum) {
        this.lockCheck();
        ((QueryReportEntry)this.getEntry(index)).setPositionSum(positionSum);
    }

    public final void setCost(Query query, double cost) {
        this.lockCheck();
        int index = this.indexForEntry(query);
        if (index < 0) {
            index = this.addQuery(query);
        }
        this.setCost(index, cost);
    }

    public final void setCost(int index, double cost) {
        this.lockCheck();
        ((QueryReportEntry)this.getEntry(index)).setCost(cost);
    }

    public final void setImpressions(Query query, int regularImpressions, int promotedImpressions) {
        this.lockCheck();
        int index = this.indexForEntry(query);
        if (index < 0) {
            index = this.addQuery(query);
        }
        this.setImpressions(index, regularImpressions, promotedImpressions);
    }

    public final void setImpressions(Query query, int regularImpressions, int promotedImpressions, Ad ad, double positionSum) {
        this.lockCheck();
        int index = this.indexForEntry(query);
        if (index < 0) {
            index = this.addQuery(query);
        }
        this.setImpressions(index, regularImpressions, promotedImpressions, ad, positionSum);
    }

    public final void addImpressions(Query query, int regular, int promoted) {
        this.lockCheck();
        int index = this.indexForEntry(query);
        if (index < 0) {
            this.setImpressions(query, regular, promoted);
        } else {
            this.addImpressions(index, regular, promoted);
        }
    }

    public final void addImpressions(Query query, int regular, int promoted, Ad ad, double positionSum) {
        this.lockCheck();
        int index = this.indexForEntry(query);
        if (index < 0) {
            this.setImpressions(query, regular, promoted, ad, positionSum);
        } else {
            this.addImpressions(index, regular, promoted, ad, positionSum);
        }
    }

    public final void addImpressions(int index, int regular, int promoted) {
        this.lockCheck();
        ((QueryReportEntry)this.getEntry(index)).addImpressions(regular, promoted);
    }

    public final void addImpressions(int index, int regular, int promoted, Ad ad, double positionSum) {
        this.lockCheck();
        ((QueryReportEntry)this.getEntry(index)).addImpressions(regular, promoted);
        ((QueryReportEntry)this.getEntry(index)).setAd(ad);
        ((QueryReportEntry)this.getEntry(index)).addPosition(positionSum);
    }

    public final void setImpressions(int index, int regularImpressions, int promotedImpressions) {
        this.lockCheck();
        ((QueryReportEntry)this.getEntry(index)).setImpressions(regularImpressions, promotedImpressions);
    }

    public final void setImpressions(int index, int regular, int promoted, Ad ad, double positionSum) {
        this.lockCheck();
        ((QueryReportEntry)this.getEntry(index)).setImpressions(regular, promoted);
        ((QueryReportEntry)this.getEntry(index)).setPositionSum(positionSum);
        ((QueryReportEntry)this.getEntry(index)).setAd(ad);
    }

    public final void setClicks(Query query, int clicks) {
        this.lockCheck();
        int index = this.indexForEntry(query);
        if (index < 0) {
            index = this.addQuery(query);
        }
        this.setClicks(index, clicks);
    }

    public final void setClicks(Query query, int clicks, double cost) {
        this.lockCheck();
        int index = this.indexForEntry(query);
        if (index < 0) {
            index = this.addQuery(query);
        }
        this.setClicks(index, clicks, cost);
    }

    public final void setClicks(int index, int clicks) {
        this.lockCheck();
        ((QueryReportEntry)this.getEntry(index)).setClicks(clicks);
    }

    public final void setClicks(int index, int clicks, double cost) {
        this.lockCheck();
        ((QueryReportEntry)this.getEntry(index)).setClicks(clicks);
        ((QueryReportEntry)this.getEntry(index)).setCost(cost);
    }

    public final void addClicks(Query query, int clicks) {
        this.lockCheck();
        int index = this.indexForEntry(query);
        if (index < 0) {
            this.setClicks(query, clicks);
        } else {
            this.addClicks(index, clicks);
        }
    }

    public final void addClicks(Query query, int clicks, double cost) {
        this.lockCheck();
        int index = this.indexForEntry(query);
        if (index < 0) {
            this.setClicks(query, clicks, cost);
        } else {
            this.addClicks(index, clicks, cost);
        }
    }

    public final void addClicks(int index, int clicks) {
        this.lockCheck();
        ((QueryReportEntry)this.getEntry(index)).addClicks(clicks);
    }

    public final void addClicks(int index, int clicks, double cost) {
        this.lockCheck();
        ((QueryReportEntry)this.getEntry(index)).addClicks(clicks);
        ((QueryReportEntry)this.getEntry(index)).addCost(cost);
    }

    public final void addCost(Query query, double cost) {
        this.lockCheck();
        int index = this.indexForEntry(query);
        if (index < 0) {
            this.setCost(query, cost);
        } else {
            this.addCost(index, cost);
        }
    }

    public final void addCost(int index, double cost) {
        this.lockCheck();
        ((QueryReportEntry)this.getEntry(index)).addCost(cost);
    }

    public final double getPosition(Query query) {
        int index = this.indexForEntry(query);
        return index < 0 ? Double.NaN : this.getPosition(index);
    }

    public final double getPosition(int index) {
        return ((QueryReportEntry)this.getEntry(index)).getPosition();
    }

    public final double getCPC(Query query) {
        int index = this.indexForEntry(query);
        return index < 0 ? Double.NaN : this.getCPC(index);
    }

    public final double getCPC(int index) {
        return ((QueryReportEntry)this.getEntry(index)).getCPC();
    }

    public final int getImpressions(Query query) {
        int index = this.indexForEntry(query);
        return index < 0 ? 0 : this.getImpressions(index);
    }

    public final int getImpressions(int index) {
        return ((QueryReportEntry)this.getEntry(index)).getImpressions();
    }

    public final int getRegularImpressions(Query query) {
        int index = this.indexForEntry(query);
        return index < 0 ? 0 : this.getRegularImpressions(index);
    }

    public final int getRegularImpressions(int index) {
        return ((QueryReportEntry)this.getEntry(index)).getRegularImpressions();
    }

    public final int getPromotedImpressions(Query query) {
        int index = this.indexForEntry(query);
        return index < 0 ? 0 : this.getPromotedImpressions(index);
    }

    public final int getPromotedImpressions(int index) {
        return ((QueryReportEntry)this.getEntry(index)).getPromotedImpressions();
    }

    public final int getClicks(Query query) {
        int index = this.indexForEntry(query);
        return index < 0 ? 0 : this.getClicks(index);
    }

    public final int getClicks(int index) {
        return ((QueryReportEntry)this.getEntry(index)).getClicks();
    }

    public final double getCost(Query query) {
        int index = this.indexForEntry(query);
        return index < 0 ? 0.0 : this.getCost(index);
    }

    public final double getCost(int index) {
        return ((QueryReportEntry)this.getEntry(index)).getCost();
    }

    public final double getPosition(Query query, String advertiser) {
        int index = this.indexForEntry(query);
        return index < 0 ? Double.NaN : this.getPosition(index, advertiser);
    }

    public final double getPosition(int index, String advertiser) {
        return ((QueryReportEntry)this.getEntry(index)).getPosition(advertiser);
    }

    public final void setPosition(Query query, String advertiser, double position) {
        this.lockCheck();
        int index = this.indexForEntry(query);
        if (index < 0) {
            index = this.addQuery(query);
        }
        this.setPosition(index, advertiser, position);
    }

    public final void setPosition(int index, String advertiser, double position) {
        this.lockCheck();
        ((QueryReportEntry)this.getEntry(index)).setPosition(advertiser, position);
    }

    public final Ad getAd(Query query) {
        int index = this.indexForEntry(query);
        return index < 0 ? null : this.getAd(index);
    }

    public final Ad getAd(int index) {
        return ((QueryReportEntry)this.getEntry(index)).getAd();
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
        ((QueryReportEntry)this.getEntry(index)).setAd(ad);
    }

    public final Ad getAd(Query query, String advertiser) {
        int index = this.indexForEntry(query);
        return index < 0 ? null : this.getAd(index, advertiser);
    }

    public final Ad getAd(int index, String advertiser) {
        return ((QueryReportEntry)this.getEntry(index)).getAd(advertiser);
    }

    public final void setAd(Query query, String advertiser, Ad ad) {
        this.lockCheck();
        int index = this.indexForEntry(query);
        if (index < 0) {
            index = this.addQuery(query);
        }
        this.setAd(index, advertiser, ad);
    }

    public final void setAd(int index, String advertiser, Ad ad) {
        this.lockCheck();
        ((QueryReportEntry)this.getEntry(index)).setAd(advertiser, ad);
    }

    public final void setAdAndPosition(Query query, String advertiser, Ad ad, double position) {
        this.lockCheck();
        int index = this.indexForEntry(query);
        if (index < 0) {
            index = this.addQuery(query);
        }
        this.setAdAndPosition(index, advertiser, ad, position);
    }

    public final void setAdAndPosition(int index, String advertiser, Ad ad, double position) {
        this.lockCheck();
        ((QueryReportEntry)this.getEntry(index)).setAdAndPosition(advertiser, ad, position);
    }

    public final Set<String> advertisers(Query query) {
        int index = this.indexForEntry(query);
        return index < 0 ? Collections.EMPTY_SET : this.advertisers(index);
    }

    public final Set<String> advertisers(int index) {
        return ((QueryReportEntry)this.getEntry(index)).advertisers();
    }

    public static class DisplayReport
    extends AbstractAdvertiserKeyedReportTransportable<DisplayReportEntry> {
        @Override
        protected final DisplayReportEntry createEntry(String advertiser) {
            DisplayReportEntry entry = new DisplayReportEntry();
            entry.setAdvertiser(advertiser);
            return entry;
        }

        @Override
        protected final Class entryClass() {
            return DisplayReportEntry.class;
        }

        public final double getPosition(String advertiser) {
            int index = this.indexForEntry(advertiser);
            return index < 0 ? Double.NaN : this.getPosition(index);
        }

        public final double getPosition(int index) {
            return ((DisplayReportEntry)this.getEntry(index)).getPosition();
        }

        public final void setPosition(String advertiser, double position) {
            this.lockCheck();
            int index = this.indexForEntry(advertiser);
            if (index < 0) {
                index = this.addAdvertiser(advertiser);
            }
            this.setPosition(index, position);
        }

        public final void setPosition(int index, double position) {
            this.lockCheck();
            ((DisplayReportEntry)this.getEntry(index)).setPosition(position);
        }

        public final Ad getAd(String advertiser) {
            int index = this.indexForEntry(advertiser);
            return index < 0 ? null : this.getAd(index);
        }

        public final Ad getAd(int index) {
            return ((DisplayReportEntry)this.getEntry(index)).getAd();
        }

        public final void setAd(String advertiser, Ad ad) {
            this.lockCheck();
            int index = this.indexForEntry(advertiser);
            if (index < 0) {
                index = this.addAdvertiser(advertiser);
            }
            this.setAd(index, ad);
        }

        public final void setAd(int index, Ad ad) {
            this.lockCheck();
            ((DisplayReportEntry)this.getEntry(index)).setAd(ad);
        }

        public final void setAdAndPosition(String advertiser, Ad ad, double position) {
            this.lockCheck();
            int index = this.indexForEntry(advertiser);
            if (index < 0) {
                index = this.addAdvertiser(advertiser);
            }
            this.setAdAndPosition(index, ad, position);
        }

        public final void setAdAndPosition(int index, Ad ad, double position) {
            this.lockCheck();
            ((DisplayReportEntry)this.getEntry(index)).setAdAndPosition(ad, position);
        }
    }

    public static class DisplayReportEntry
    extends AbstractAdvertiserEntry {
        private Ad ad;
        private double position = Double.NaN;

        public final Ad getAd() {
            return this.ad;
        }

        public final void setAd(Ad ad) {
            this.ad = ad;
        }

        public final double getPosition() {
            return this.position;
        }

        public final void setPosition(double position) {
            this.position = position;
        }

        public final void setAdAndPosition(Ad ad, double position) {
            this.ad = ad;
            this.position = position;
        }

        @Override
        protected final void readEntry(TransportReader reader) throws ParseException {
            this.position = reader.getAttributeAsDouble("position", Double.NaN);
            if (reader.nextNode(Ad.class.getSimpleName(), false)) {
                this.ad = (Ad)reader.readTransportable();
            }
        }

        @Override
        protected final void writeEntry(TransportWriter writer) {
            writer.attr("position", this.position);
            if (this.ad != null) {
                writer.write(this.ad);
            }
        }
    }

    public static class QueryReportEntry
    extends AbstractQueryEntry {
        private int promotedImpressions;
        private int regularImpressions;
        private int clicks;
        private double cost = 0.0;
        private double positionSum = 0.0;
        private Ad ad;
        private DisplayReport displayReport = new DisplayReport();

        public final int getImpressions() {
            return this.promotedImpressions + this.regularImpressions;
        }

        public final int getPromotedImpressions() {
            return this.promotedImpressions;
        }

        public final void setPromotedImpressions(int promotedImpressions) {
            this.promotedImpressions = promotedImpressions;
        }

        public final int getRegularImpressions() {
            return this.regularImpressions;
        }

        public final void setRegularImpressions(int regularImpressions) {
            this.regularImpressions = regularImpressions;
        }

        final void setImpressions(int regularImpressions, int promotedImpressions) {
            this.regularImpressions = regularImpressions;
            this.promotedImpressions = promotedImpressions;
        }

        final void addImpressions(int regular, int promoted) {
            this.regularImpressions += regular;
            this.promotedImpressions += promoted;
        }

        public final int getClicks() {
            return this.clicks;
        }

        final void setClicks(int clicks) {
            this.clicks = clicks;
        }

        final void addClicks(int clicks) {
            this.clicks += clicks;
        }

        public final double getPosition() {
            return this.positionSum / (double)this.getImpressions();
        }

        final void addPosition(double position) {
            this.positionSum += position;
        }

        final void setPositionSum(double positionSum) {
            this.positionSum = positionSum;
        }

        public final double getCost() {
            return this.cost;
        }

        final void setCost(double cost) {
            this.cost = cost;
        }

        final void addCost(double cost) {
            this.cost += cost;
        }

        public final double getCPC() {
            return this.cost / (double)this.clicks;
        }

        public final Ad getAd() {
            return this.ad;
        }

        public final void setAd(Ad ad) {
            this.ad = ad;
        }

        public final double getPosition(String advertiser) {
            return this.displayReport.getPosition(advertiser);
        }

        public final void setPosition(String advertiser, double position) {
            this.displayReport.setPosition(advertiser, position);
        }

        public final Ad getAd(String advertiser) {
            return this.displayReport.getAd(advertiser);
        }

        public final void setAd(String advertiser, Ad ad) {
            this.displayReport.setAd(advertiser, ad);
        }

        public final void setAdAndPosition(String advertiser, Ad ad, double position) {
            this.displayReport.setAdAndPosition(advertiser, ad, position);
        }

        public final Set<String> advertisers() {
            return this.displayReport.keys();
        }

        @Override
        protected final void readEntry(TransportReader reader) throws ParseException {
            this.regularImpressions = reader.getAttributeAsInt("regularImpressions", 0);
            this.promotedImpressions = reader.getAttributeAsInt("promotedImpressions", 0);
            this.clicks = reader.getAttributeAsInt("clicks", 0);
            this.positionSum = reader.getAttributeAsDouble("positionSum", 0.0);
            this.cost = reader.getAttributeAsDouble("cost", 0.0);
            if (reader.nextNode(Ad.class.getSimpleName(), false)) {
                this.ad = (Ad)reader.readTransportable();
            }
            reader.nextNode(DisplayReport.class.getSimpleName(), true);
            this.displayReport = (DisplayReport)reader.readTransportable();
        }

        @Override
        protected final void writeEntry(TransportWriter writer) {
            writer.attr("regularImpressions", this.regularImpressions);
            writer.attr("promotedImpressions", this.promotedImpressions);
            writer.attr("clicks", this.clicks);
            writer.attr("positionSum", this.positionSum);
            writer.attr("cost", this.cost);
            if (this.ad != null) {
                writer.write(this.ad);
            }
            writer.write(this.displayReport);
        }

        public final String toString() {
            return String.format("(%s regular_impr: %d promoted_impr: %d clicks: %d pos: %f cpc: %f advertisers: %s)", this.getQuery(), this.regularImpressions, this.promotedImpressions, this.clicks, this.getPosition(), this.getCPC(), this.displayReport);
        }
    }

}

