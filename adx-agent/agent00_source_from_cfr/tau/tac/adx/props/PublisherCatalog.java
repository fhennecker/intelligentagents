/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.props;

import edu.umich.eecs.tac.props.AbstractTransportableEntryListBacking;
import java.util.Iterator;
import java.util.List;
import tau.tac.adx.AdxManager;
import tau.tac.adx.props.PublisherCatalogEntry;
import tau.tac.adx.publishers.AdxPublisher;

public class PublisherCatalog
extends AbstractTransportableEntryListBacking<PublisherCatalogEntry>
implements Iterable<PublisherCatalogEntry> {
    private static final long serialVersionUID = -5999861205883888430L;

    public void addPublisher(AdxPublisher publisher) {
        this.addEntry(new PublisherCatalogEntry(publisher.getName()));
        AdxManager.getInstance().addPublisher(publisher);
    }

    protected final PublisherCatalogEntry createEntry(AdxPublisher adxPublisher) {
        return new PublisherCatalogEntry(adxPublisher);
    }

    protected boolean locked() {
        return this.isLocked();
    }

    @Override
    public Iterator<PublisherCatalogEntry> iterator() {
        return this.getEntries().iterator();
    }

    @Override
    protected Class<PublisherCatalogEntry> entryClass() {
        return PublisherCatalogEntry.class;
    }

    public List<PublisherCatalogEntry> getPublishers() {
        return this.getEntries();
    }
}

