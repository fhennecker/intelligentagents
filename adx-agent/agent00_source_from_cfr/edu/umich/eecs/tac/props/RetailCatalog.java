/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.AbstractKeyedEntryList;
import edu.umich.eecs.tac.props.AbstractTransportableEntry;
import edu.umich.eecs.tac.props.KeyedEntry;
import edu.umich.eecs.tac.props.Product;
import java.text.ParseException;
import java.util.Set;
import java.util.TreeSet;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

public class RetailCatalog
extends AbstractKeyedEntryList<Product, RetailCatalogEntry> {
    private static final long serialVersionUID = 6299454928374287377L;
    private Set<String> manufacturers = new TreeSet<String>();
    private Set<String> components = new TreeSet<String>();

    public final Set<String> getManufacturers() {
        return this.manufacturers;
    }

    public final Set<String> getComponents() {
        return this.components;
    }

    public final double getSalesProfit(Product product) {
        int index = this.indexForEntry(product);
        return index < 0 ? 0.0 : this.getSalesProfit(index);
    }

    public final double getSalesProfit(int index) {
        return ((RetailCatalogEntry)this.getEntry(index)).getSalesProfit();
    }

    public final void setSalesProfit(Product product, double salesProfit) throws IllegalStateException {
        this.lockCheck();
        int index = this.indexForEntry(product);
        if (index < 0) {
            index = this.addProduct(product);
        }
        this.setSalesProfit(index, salesProfit);
    }

    public final void setSalesProfit(int index, double salesProfit) throws IllegalStateException {
        this.lockCheck();
        ((RetailCatalogEntry)this.getEntry(index)).setSalesProfit(salesProfit);
    }

    public final int addProduct(Product product) throws IllegalStateException {
        return this.addKey(product);
    }

    @Override
    protected final void afterAddEntry(RetailCatalogEntry entry) throws IllegalStateException {
        this.manufacturers.add(entry.getProduct().getManufacturer());
        this.components.add(entry.getProduct().getComponent());
    }

    @Override
    protected final RetailCatalogEntry createEntry(Product key) {
        return new RetailCatalogEntry(key);
    }

    @Override
    protected final void beforeRemoveEntry(int index) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot remove retail catalog entry");
    }

    @Override
    protected final Class entryClass() {
        return RetailCatalogEntry.class;
    }

    public static class RetailCatalogEntry
    extends AbstractTransportableEntry<Product> {
        private static final long serialVersionUID = -1140097762238141476L;
        private double salesProfit;

        public RetailCatalogEntry() {
        }

        public RetailCatalogEntry(Product product) {
            this.setProduct(product);
        }

        public final Product getProduct() {
            return (Product)this.getKey();
        }

        protected final void setProduct(Product product) {
            this.setKey(product);
        }

        public final double getSalesProfit() {
            return this.salesProfit;
        }

        protected final void setSalesProfit(double salesProfit) {
            this.salesProfit = salesProfit;
        }

        @Override
        protected final void readEntry(TransportReader reader) throws ParseException {
            this.salesProfit = reader.getAttributeAsDouble("salesProfit", 0.0);
        }

        @Override
        protected final void writeEntry(TransportWriter writer) {
            writer.attr("salesProfit", this.salesProfit);
        }

        @Override
        protected final String keyNodeName() {
            return Product.class.getSimpleName();
        }
    }

}

