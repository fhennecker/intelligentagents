/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.report.publisher;

import edu.umich.eecs.tac.props.AbstractKeyedEntryList;
import edu.umich.eecs.tac.props.KeyedEntry;
import java.util.Map;
import tau.tac.adx.AdxManager;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.props.PublisherCatalogEntry;
import tau.tac.adx.publishers.AdxPublisher;
import tau.tac.adx.publishers.reserve.UserAdTypeReservePriceManager;
import tau.tac.adx.report.publisher.AdxPublisherReportEntry;

public class AdxPublisherReport
extends AbstractKeyedEntryList<PublisherCatalogEntry, AdxPublisherReportEntry> {
    private static final long serialVersionUID = -7957495904471250085L;

    @Override
    protected final Class<AdxPublisherReportEntry> entryClass() {
        return AdxPublisherReportEntry.class;
    }

    @Override
    protected AdxPublisherReportEntry createEntry(PublisherCatalogEntry key) {
        AdxPublisherReportEntry entry = new AdxPublisherReportEntry(key);
        return entry;
    }

    public void addPublisherReportEntry(PublisherCatalogEntry publisher, AdxPublisherReportEntry publisherReportEntry) {
        this.lockCheck();
        int index = this.addKey(publisher);
        AdxPublisherReportEntry entry = (AdxPublisherReportEntry)this.getEntry(index);
        entry.setPopularity(publisherReportEntry.getPopularity());
        entry.setAdTypeOrientation(publisherReportEntry.getAdTypeOrientation());
        entry.setReservePriceBaseline(publisherReportEntry.getReservePriceBaseline());
    }

    public AdxPublisherReportEntry getPublisherReportEntry(PublisherCatalogEntry publisher) {
        return (AdxPublisherReportEntry)this.getEntry(publisher);
    }

    public AdxPublisherReportEntry getPublisherReportEntry(String publisher) {
        PublisherCatalogEntry publisherEntry = this.getPublisherCatalogEntry(publisher);
        return (AdxPublisherReportEntry)this.getEntry(publisherEntry);
    }

    private PublisherCatalogEntry getPublisherCatalogEntry(String publisher) {
        return new PublisherCatalogEntry(AdxManager.getInstance().getPublisher(publisher));
    }

    public void addQuery(AdxQuery query) {
        PublisherCatalogEntry publisherCatalogEntry = this.getPublisherCatalogEntry(query.getPublisher());
        AdxPublisherReportEntry publisherReportEntry = this.getPublisherReportEntry(publisherCatalogEntry);
        if (publisherReportEntry == null) {
            publisherReportEntry = new AdxPublisherReportEntry(publisherCatalogEntry);
            publisherReportEntry.setReservePriceBaseline(AdxManager.getInstance().getPublisher(query.getPublisher()).getReservePriceManager().getDailyBaselineAverage(query));
            this.addPublisherReportEntry(publisherCatalogEntry, publisherReportEntry);
        }
        publisherReportEntry.addQuery(query);
    }
}

