/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.props;

import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;
import tau.tac.adx.publishers.AdxPublisher;

public class PublisherCatalogEntry
implements Transportable {
    private static final String PUBLISHER_NAME_KEY = "PUBLISHER_NAME_KEY";
    private String publisherName;

    public PublisherCatalogEntry(String publisherName) {
        this.publisherName = publisherName;
    }

    public PublisherCatalogEntry() {
    }

    public PublisherCatalogEntry(AdxPublisher adxPublisher) {
        this.publisherName = adxPublisher.getName();
    }

    public String getPublisherName() {
        return this.publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    @Override
    public String getTransportName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void read(TransportReader reader) throws ParseException {
        this.publisherName = reader.getAttribute("PUBLISHER_NAME_KEY", null);
    }

    @Override
    public void write(TransportWriter writer) {
        if (this.publisherName != null) {
            writer.attr("PUBLISHER_NAME_KEY", this.publisherName);
        }
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.publisherName == null ? 0 : this.publisherName.hashCode());
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
        PublisherCatalogEntry other = (PublisherCatalogEntry)obj;
        if (this.publisherName == null ? other.publisherName != null : !this.publisherName.equals(other.publisherName)) {
            return false;
        }
        return true;
    }
}

