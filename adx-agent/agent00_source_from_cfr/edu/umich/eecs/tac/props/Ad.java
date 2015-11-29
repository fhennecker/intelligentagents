/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.AbstractTransportable;
import edu.umich.eecs.tac.props.Product;
import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;
import tau.tac.adx.bids.BidProduct;

public class Ad
extends AbstractTransportable
implements BidProduct {
    private Product product;

    public Ad() {
    }

    public Ad(Product product) {
        this.product = product;
    }

    public final boolean isGeneric() {
        if (this.product == null) {
            return true;
        }
        return false;
    }

    public final Product getProduct() {
        return this.product;
    }

    public final void setProduct(Product product) throws IllegalStateException {
        this.lockCheck();
        this.product = product;
    }

    @Override
    protected final void readWithLock(TransportReader reader) throws ParseException {
        if (reader.nextNode(Product.class.getSimpleName(), false)) {
            this.product = (Product)reader.readTransportable();
        }
    }

    @Override
    protected final void writeWithLock(TransportWriter writer) {
        if (this.product != null) {
            writer.write(this.product);
        }
    }

    public final String toString() {
        return String.format("(Ad generic:%s product:%s)", this.isGeneric(), this.getProduct());
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Ad ad = (Ad)o;
        return !(this.product != null ? !this.product.equals(ad.product) : ad.product != null);
    }

    public final int hashCode() {
        return this.product != null ? this.product.hashCode() : 0;
    }
}

