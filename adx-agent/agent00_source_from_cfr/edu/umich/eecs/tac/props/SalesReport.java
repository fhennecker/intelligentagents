/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.AbstractQueryEntry;
import edu.umich.eecs.tac.props.AbstractQueryKeyedReportTransportable;
import edu.umich.eecs.tac.props.KeyedEntry;
import edu.umich.eecs.tac.props.Query;
import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

public class SalesReport
extends AbstractQueryKeyedReportTransportable<SalesReportEntry> {
    private static final long serialVersionUID = 3473199640271355791L;

    @Override
    protected final SalesReportEntry createEntry(Query query) {
        SalesReportEntry entry = new SalesReportEntry();
        entry.setQuery(query);
        return entry;
    }

    @Override
    protected final Class entryClass() {
        return SalesReportEntry.class;
    }

    protected final void addQuery(Query query, int conversions, double revenue) {
        int index = this.addQuery(query);
        SalesReportEntry entry = (SalesReportEntry)this.getEntry(index);
        entry.setQuery(query);
        entry.setConversions(conversions);
        entry.setRevenue(revenue);
    }

    public final void addConversions(Query query, int conversions) {
        int index = this.indexForEntry(query);
        if (index < 0) {
            this.setConversions(query, conversions);
        } else {
            this.addConversions(index, conversions);
        }
    }

    public final void addConversions(int index, int conversions) {
        this.lockCheck();
        ((SalesReportEntry)this.getEntry(index)).addConversions(conversions);
    }

    public final void addRevenue(Query query, double revenue) {
        int index = this.indexForEntry(query);
        if (index < 0) {
            this.setRevenue(query, revenue);
        } else {
            this.addRevenue(index, revenue);
        }
    }

    public final void addRevenue(int index, double revenue) {
        this.lockCheck();
        ((SalesReportEntry)this.getEntry(index)).addRevenue(revenue);
    }

    public final void setConversions(Query query, int conversions) {
        this.lockCheck();
        int index = this.indexForEntry(query);
        if (index < 0) {
            this.addQuery(query, conversions, 0.0);
        } else {
            this.setConversions(index, conversions);
        }
    }

    public final void setConversions(int index, int conversions) {
        this.lockCheck();
        ((SalesReportEntry)this.getEntry(index)).setConversions(conversions);
    }

    public final void setRevenue(Query query, double revenue) {
        this.lockCheck();
        int index = this.indexForEntry(query);
        if (index < 0) {
            this.addQuery(query, 0, revenue);
        } else {
            this.setRevenue(index, revenue);
        }
    }

    public final void setRevenue(int index, double revenue) {
        this.lockCheck();
        ((SalesReportEntry)this.getEntry(index)).setRevenue(revenue);
    }

    public final void setConversionsAndRevenue(Query query, int conversions, double revenue) {
        this.lockCheck();
        int index = this.indexForEntry(query);
        if (index < 0) {
            this.addQuery(query, conversions, revenue);
        } else {
            this.setConversionsAndRevenue(index, conversions, revenue);
        }
    }

    public final void setConversionsAndRevenue(int index, int conversions, double revenue) {
        this.lockCheck();
        SalesReportEntry entry = (SalesReportEntry)this.getEntry(index);
        entry.setConversions(conversions);
        entry.setRevenue(revenue);
    }

    public final int getConversions(Query query) {
        int index = this.indexForEntry(query);
        return index < 0 ? 0 : this.getConversions(index);
    }

    public final int getConversions(int index) {
        return ((SalesReportEntry)this.getEntry(index)).getConversions();
    }

    public final double getRevenue(Query query) {
        int index = this.indexForEntry(query);
        return index < 0 ? 0.0 : this.getRevenue(index);
    }

    public final double getRevenue(int index) {
        return ((SalesReportEntry)this.getEntry(index)).getRevenue();
    }

    public static class SalesReportEntry
    extends AbstractQueryEntry {
        private static final long serialVersionUID = -3012145053844178964L;
        private int conversions;
        private double revenue;

        public final int getConversions() {
            return this.conversions;
        }

        final void setConversions(int conversions) {
            this.conversions = conversions;
        }

        final void addConversions(int conversions) {
            this.conversions += conversions;
        }

        public final double getRevenue() {
            return this.revenue;
        }

        final void setRevenue(double revenue) {
            this.revenue = revenue;
        }

        final void addRevenue(double revenue) {
            this.revenue += revenue;
        }

        @Override
        protected final void readEntry(TransportReader reader) throws ParseException {
            this.conversions = reader.getAttributeAsInt("conversions", 0);
            this.revenue = reader.getAttributeAsDouble("revenue", 0.0);
        }

        @Override
        protected final void writeEntry(TransportWriter writer) {
            writer.attr("conversions", this.conversions);
            writer.attr("revenue", this.revenue);
        }

        public final String toString() {
            return String.format("(%s conv: %d rev: %f)", this.getQuery(), this.conversions, this.revenue);
        }
    }

}

