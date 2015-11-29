/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.props;

import edu.umich.eecs.tac.props.AbstractTransportable;
import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.Product;
import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;
import tau.tac.adx.bids.BidProduct;

public class AdLink
extends AbstractTransportable
implements BidProduct {
    private String advertiser;
    private Ad ad;

    public AdLink() {
    }

    public AdLink(Product product, String advertiser) {
        this(new Ad(product), advertiser);
    }

    public AdLink(Ad ad, String advertiser) {
        this.ad = ad;
        this.advertiser = advertiser;
    }

    public final String getAdvertiser() {
        return this.advertiser;
    }

    public final Ad getAd() {
        return this.ad;
    }

    public final void setAd(Ad ad) {
        this.ad = ad;
    }

    public final void setAdvertiser(String advertiser) throws IllegalStateException {
        this.lockCheck();
        this.advertiser = advertiser;
    }

    @Override
    protected final void readWithLock(TransportReader reader) throws ParseException {
        this.advertiser = reader.getAttribute("advertiser", null);
        if (reader.nextNode(Ad.class.getSimpleName(), false)) {
            this.ad = (Ad)reader.readTransportable();
        }
    }

    @Override
    protected final void writeWithLock(TransportWriter writer) {
        if (this.advertiser != null) {
            writer.attr("advertiser", this.advertiser);
        }
        if (this.ad != null) {
            writer.write(this.ad);
        }
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AdLink adLink = (AdLink)o;
        if (this.ad != null ? !this.ad.equals(adLink.ad) : adLink.ad != null) {
            return false;
        }
        if (this.advertiser != null ? !this.advertiser.equals(adLink.advertiser) : adLink.advertiser != null) {
            return false;
        }
        return true;
    }

    public final int hashCode() {
        int result = this.advertiser != null ? this.advertiser.hashCode() : 0;
        result = 31 * result + (this.ad != null ? this.ad.hashCode() : 0);
        return result;
    }

    public final String toString() {
        return String.format("(AdLink advertiser:%s ad:%s)", this.getAdvertiser(), this.getAd());
    }
}

