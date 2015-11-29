/*
 * Decompiled with CFR 0_110.
 */
package adx.logging;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import tau.tac.adx.props.PublisherCatalog;
import tau.tac.adx.props.PublisherCatalogEntry;

public class PublisherCatalogSorted
implements Iterable<PublisherCatalogEntry> {
    protected SortedSet<PublisherCatalogEntry> sortedSet;

    public PublisherCatalogSorted(PublisherCatalog catalog) {
        this.sortedSet = new TreeSet<PublisherCatalogEntry>(new Comparator<PublisherCatalogEntry>(){

            @Override
            public int compare(PublisherCatalogEntry o1, PublisherCatalogEntry o2) {
                return o1.getPublisherName().compareTo(o2.getPublisherName());
            }
        });
        for (PublisherCatalogEntry entry : catalog) {
            this.sortedSet.add(entry);
        }
    }

    @Override
    public Iterator<PublisherCatalogEntry> iterator() {
        return this.sortedSet.iterator();
    }

}

