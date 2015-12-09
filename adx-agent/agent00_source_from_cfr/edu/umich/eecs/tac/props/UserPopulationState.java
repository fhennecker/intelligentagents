/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.AbstractKeyedEntryList;
import edu.umich.eecs.tac.props.AbstractTransportableEntry;
import edu.umich.eecs.tac.props.KeyedEntry;
import edu.umich.eecs.tac.props.Product;
import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

public class UserPopulationState
extends AbstractKeyedEntryList<Product, UserPopulationEntry> {
    private static final long serialVersionUID = 2656209779279027478L;

    @Override
    protected UserPopulationEntry createEntry(Product key) {
        return new UserPopulationEntry(key);
    }

    @Override
    protected Class entryClass() {
        return UserPopulationEntry.class;
    }

    public final int[] getDistribution(Product product) {
        int index = this.indexForEntry(product);
        return index < 0 ? null : (Object)this.getDistribution(index);
    }

    public final int[] getDistribution(int index) {
        return ((UserPopulationEntry)this.getEntry(index)).getDistribution();
    }

    public void setDistribution(Product product, int[] distribution) {
        this.lockCheck();
        int index = this.indexForEntry(product);
        if (index < 0) {
            index = this.addProduct(product);
        }
        this.setDistribution(index, distribution);
    }

    public void setDistribution(int index, int[] distribution) {
        this.lockCheck();
        ((UserPopulationEntry)this.getEntry(index)).setDistribution(distribution);
    }

    public final int addProduct(Product product) throws IllegalStateException {
        return this.addKey(product);
    }

    public static class UserPopulationEntry
    extends AbstractTransportableEntry<Product> {
        private static final long serialVersionUID = -4560192080485265951L;
        private int[] distribution;

        public UserPopulationEntry(Product key) {
            this.setProduct(key);
        }

        public UserPopulationEntry() {
        }

        public int[] getDistribution() {
            return this.distribution;
        }

        public void setDistribution(int[] distribution) {
            this.distribution = distribution;
        }

        public final Product getProduct() {
            return (Product)this.getKey();
        }

        protected final void setProduct(Product product) {
            this.setKey(product);
        }

        @Override
        protected String keyNodeName() {
            return Product.class.getSimpleName();
        }

        @Override
        protected void readEntry(TransportReader reader) throws ParseException {
            this.distribution = reader.getAttributeAsIntArray("distribution");
        }

        @Override
        protected void writeEntry(TransportWriter writer) {
            writer.attr("distribution", this.distribution);
        }
    }

}

