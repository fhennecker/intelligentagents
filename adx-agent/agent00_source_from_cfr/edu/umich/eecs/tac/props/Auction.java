/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.AbstractTransportable;
import edu.umich.eecs.tac.props.Pricing;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.Ranking;
import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

public class Auction
extends AbstractTransportable {
    private Ranking ranking;
    private Pricing pricing;
    private Query query;

    public final Ranking getRanking() {
        return this.ranking;
    }

    public final void setRanking(Ranking ranking) {
        this.lockCheck();
        this.ranking = ranking;
    }

    public final Pricing getPricing() {
        return this.pricing;
    }

    public final void setPricing(Pricing pricing) {
        this.lockCheck();
        this.pricing = pricing;
    }

    public final Query getQuery() {
        return this.query;
    }

    public final void setQuery(Query query) {
        this.lockCheck();
        this.query = query;
    }

    @Override
    protected final void readWithLock(TransportReader reader) throws ParseException {
        if (reader.nextNode(Ranking.class.getSimpleName(), false)) {
            this.ranking = (Ranking)reader.readTransportable();
        }
        if (reader.nextNode(Pricing.class.getSimpleName(), false)) {
            this.pricing = (Pricing)reader.readTransportable();
        }
        if (reader.nextNode(Query.class.getSimpleName(), false)) {
            this.query = (Query)reader.readTransportable();
        }
    }

    @Override
    protected final void writeWithLock(TransportWriter writer) {
        if (this.ranking != null) {
            writer.write(this.ranking);
        }
        if (this.pricing != null) {
            writer.write(this.pricing);
        }
        if (this.query != null) {
            writer.write(this.query);
        }
    }
}

