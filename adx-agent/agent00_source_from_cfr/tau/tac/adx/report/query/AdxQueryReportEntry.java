/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.report.query;

import edu.umich.eecs.tac.props.AbstractQueryEntry;
import edu.umich.eecs.tac.props.Ad;
import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

public class AdxQueryReportEntry
extends AbstractQueryEntry {
    private int impressions;
    private double cost;
    private Ad ad;

    public final int getImpressions() {
        return this.impressions;
    }

    public final void setImpressions(int impressions) {
        this.impressions = impressions;
    }

    final void addImpressions(int impressionCount) {
        this.impressions += impressionCount;
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

    public final Ad getAd() {
        return this.ad;
    }

    public final void setAd(Ad ad) {
        this.ad = ad;
    }

    public final Ad getAd(String advertiser) {
        return this.ad;
    }

    @Override
    protected final void readEntry(TransportReader reader) throws ParseException {
        this.impressions = reader.getAttributeAsInt("regularImpressions", 0);
        this.cost = reader.getAttributeAsDouble("cost", 0.0);
        if (reader.nextNode(Ad.class.getSimpleName(), false)) {
            this.ad = (Ad)reader.readTransportable();
        }
    }

    @Override
    protected final void writeEntry(TransportWriter writer) {
        writer.attr("regularImpressions", this.impressions);
        writer.attr("cost", this.cost);
        if (this.ad != null) {
            writer.write(this.ad);
        }
    }

    public final String toString() {
        return String.format("(%s impressions: %d cost: %s)", this.impressions, this.cost);
    }
}

