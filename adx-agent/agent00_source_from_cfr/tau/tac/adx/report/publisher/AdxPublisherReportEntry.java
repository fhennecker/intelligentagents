/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.report.publisher;

import edu.umich.eecs.tac.props.AbstractTransportableEntry;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.props.PublisherCatalogEntry;

public class AdxPublisherReportEntry
extends AbstractTransportableEntry<PublisherCatalogEntry> {
    private static final long serialVersionUID = -6459026589028276792L;
    private static final String AD_TYPE_ORIENTATION_ENTRY_TRANSPORT_NAME = "AdTypeOrientation";
    private static final String PUBLISHER_CATALOG_NAME_ENTRY_TRANSPORT_NAME = "PublisherCatalogEntry";
    private static final String POPULARITY_TRANSPORT_NAME = "PopularityEntry";
    private static String RESERVE_PRICE_BASELINE_KEY = "RESERVE_PRICE_BASELINE_KEY";
    private double reservePriceBaseline;
    private int popularity;
    private Map<AdType, Integer> adTypeOrientation = new HashMap<AdType, Integer>();

    public AdxPublisherReportEntry(PublisherCatalogEntry key) {
        this.adTypeOrientation.put(AdType.text, 0);
        this.adTypeOrientation.put(AdType.video, 0);
        this.setKey(key);
    }

    public AdxPublisherReportEntry() {
        this.adTypeOrientation.put(AdType.text, 0);
        this.adTypeOrientation.put(AdType.video, 0);
    }

    public String getPublisherName() {
        return ((PublisherCatalogEntry)this.getKey()).getPublisherName();
    }

    public int getPopularity() {
        return this.popularity;
    }

    public Map<AdType, Integer> getAdTypeOrientation() {
        return this.adTypeOrientation;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public void setAdTypeOrientation(Map<AdType, Integer> adTypeOrientation) {
        this.adTypeOrientation = adTypeOrientation;
    }

    public double getReservePriceBaseline() {
        return this.reservePriceBaseline;
    }

    public void setReservePriceBaseline(double reservePriceBaseline) {
        this.reservePriceBaseline = reservePriceBaseline;
    }

    @Override
    protected final void readEntry(TransportReader reader) throws ParseException {
        this.adTypeOrientation.clear();
        String attribute = reader.getAttribute("PublisherCatalogEntry");
        this.reservePriceBaseline = reader.getAttributeAsDouble(RESERVE_PRICE_BASELINE_KEY);
        this.setKey(new PublisherCatalogEntry(attribute));
        this.popularity = reader.getAttributeAsInt("PopularityEntry");
        while (reader.nextNode("AdTypeOrientation", false)) {
            this.readAdTypeEntry(reader);
        }
    }

    @Override
    protected final void writeEntry(TransportWriter writer) {
        writer.attr("PublisherCatalogEntry", ((PublisherCatalogEntry)this.getKey()).getPublisherName());
        writer.attr("PopularityEntry", this.popularity);
        writer.attr(RESERVE_PRICE_BASELINE_KEY, this.reservePriceBaseline);
        for (Map.Entry<AdType, Integer> entry : this.adTypeOrientation.entrySet()) {
            this.writeAdTypeEntry(writer, entry.getValue(), entry.getKey());
        }
    }

    protected final void writeAdTypeEntry(TransportWriter writer, Integer orientation, AdType adType) {
        writer.node("AdTypeOrientation");
        writer.attr("Oreintation", orientation);
        writer.attr("AdType", adType.toString());
        writer.endNode("AdTypeOrientation");
    }

    protected final void readAdTypeEntry(TransportReader reader) throws ParseException {
        reader.enterNode();
        int orientation = reader.getAttributeAsInt("Oreintation", 0);
        AdType adType = AdType.valueOf(reader.getAttribute("AdType"));
        this.adTypeOrientation.put(adType, orientation);
        reader.exitNode();
    }

    @Override
    protected String keyNodeName() {
        return "AdTypeOrientation";
    }

    public void addQuery(AdxQuery query) {
        ++this.popularity;
        this.adTypeOrientation.put(query.getAdType(), this.adTypeOrientation.get((Object)query.getAdType()) + 1);
    }

    public final String toString() {
        return String.format("(publisher: %s popularity: %d video: %d text: %d)", this.getPublisherName(), this.getPopularity(), this.getAdTypeOrientation().get((Object)AdType.video), this.getAdTypeOrientation().get((Object)AdType.text));
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.adTypeOrientation == null ? 0 : this.adTypeOrientation.hashCode());
        result = 31 * result + this.popularity;
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
        AdxPublisherReportEntry other = (AdxPublisherReportEntry)obj;
        if (this.adTypeOrientation == null ? other.adTypeOrientation != null : !this.adTypeOrientation.equals(other.adTypeOrientation)) {
            return false;
        }
        if (this.popularity != other.popularity) {
            return false;
        }
        return true;
    }
}

