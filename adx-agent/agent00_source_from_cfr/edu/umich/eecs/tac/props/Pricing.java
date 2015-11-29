/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.AbstractTransportable;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;
import tau.tac.adx.props.AdLink;

public class Pricing
extends AbstractTransportable {
    private static final String PRICE_ENTRY_TRANSPORT_NAME = "PriceEntry";
    private final Map<AdLink, Double> prices = new HashMap<AdLink, Double>();

    public final void setPrice(AdLink ad, double price) throws NullPointerException {
        this.lockCheck();
        if (ad == null) {
            throw new NullPointerException("ad cannot be null");
        }
        this.prices.put(ad, price);
    }

    public final double getPrice(AdLink ad) {
        Double price = this.prices.get(ad);
        if (price == null) {
            return Double.NaN;
        }
        return price;
    }

    public final Set<AdLink> adLinks() {
        return this.prices.keySet();
    }

    @Override
    protected final void readWithLock(TransportReader reader) throws ParseException {
        this.prices.clear();
        while (reader.nextNode("PriceEntry", false)) {
            this.readPriceEntry(reader);
        }
    }

    @Override
    protected final void writeWithLock(TransportWriter writer) {
        for (Map.Entry<AdLink, Double> entry : this.prices.entrySet()) {
            this.writePriceEntry(writer, entry.getValue(), entry.getKey());
        }
    }

    protected final void writePriceEntry(TransportWriter writer, Double price, AdLink adLink) {
        writer.node("PriceEntry");
        writer.attr("price", price);
        writer.write(adLink);
        writer.endNode("PriceEntry");
    }

    protected final void readPriceEntry(TransportReader reader) throws ParseException {
        reader.enterNode();
        double price = reader.getAttributeAsDouble("price", Double.NaN);
        reader.nextNode(AdLink.class.getSimpleName(), true);
        AdLink adLink = (AdLink)reader.readTransportable();
        this.setPrice(adLink, price);
        reader.exitNode();
    }
}

