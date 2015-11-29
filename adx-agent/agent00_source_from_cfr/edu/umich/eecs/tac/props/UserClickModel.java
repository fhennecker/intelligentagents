/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.AbstractTransportable;
import edu.umich.eecs.tac.props.Query;
import java.text.ParseException;
import java.util.LinkedList;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

public class UserClickModel
extends AbstractTransportable {
    private double[][] advertiserEffects;
    private double[] continuationProbabilities;
    private Query[] queries;
    private String[] advertisers;

    public UserClickModel() {
        this(new Query[0], new String[0]);
    }

    public UserClickModel(Query[] queries, String[] advertisers) {
        if (queries == null) {
            throw new NullPointerException("queries cannot be null");
        }
        if (advertisers == null) {
            throw new NullPointerException("advertisers cannot be null");
        }
        this.queries = queries;
        this.advertisers = advertisers;
        this.advertiserEffects = new double[queries.length][advertisers.length];
        this.continuationProbabilities = new double[queries.length];
    }

    public final int advertiserCount() {
        return this.advertisers.length;
    }

    public final String advertiser(int index) {
        return this.advertisers[index];
    }

    public final int advertiserIndex(String advertiser) {
        int index = 0;
        while (index < this.advertisers.length) {
            if (this.advertisers[index].equals(advertiser)) {
                return index;
            }
            ++index;
        }
        return -1;
    }

    public final int queryCount() {
        return this.queries.length;
    }

    public final Query query(int index) {
        return this.queries[index];
    }

    public final int queryIndex(Query query) {
        int index = 0;
        while (index < this.queries.length) {
            if (this.queries[index].equals(query)) {
                return index;
            }
            ++index;
        }
        return -1;
    }

    public final double getContinuationProbability(int queryIndex) {
        return this.continuationProbabilities[queryIndex];
    }

    public final void setContinuationProbability(int queryIndex, double probability) {
        this.lockCheck();
        this.continuationProbabilities[queryIndex] = probability;
    }

    public final double getAdvertiserEffect(int queryIndex, int advertiserIndex) {
        return this.advertiserEffects[queryIndex][advertiserIndex];
    }

    public final void setAdvertiserEffect(int queryIndex, int advertiserIndex, double effect) {
        this.lockCheck();
        this.advertiserEffects[queryIndex][advertiserIndex] = effect;
    }

    @Override
    protected final void readWithLock(TransportReader reader) throws ParseException {
        LinkedList<Query> queryList = new LinkedList<Query>();
        String queryName = Query.class.getSimpleName();
        while (reader.nextNode(queryName, false)) {
            queryList.add((Query)reader.readTransportable());
        }
        this.queries = queryList.toArray(new Query[0]);
        LinkedList<String> advertiserList = new LinkedList<String>();
        while (reader.nextNode("advertiser", false)) {
            advertiserList.add(reader.getAttribute("name"));
        }
        this.advertisers = advertiserList.toArray(new String[0]);
        this.advertiserEffects = new double[this.queries.length][this.advertisers.length];
        this.continuationProbabilities = new double[this.queries.length];
        while (reader.nextNode("continuationProbability", false)) {
            int index = reader.getAttributeAsInt("index");
            double probability = reader.getAttributeAsDouble("probability");
            this.setContinuationProbability(index, probability);
        }
        while (reader.nextNode("advertiserEffect", false)) {
            int queryIndex = reader.getAttributeAsInt("queryIndex");
            int advertiserIndex = reader.getAttributeAsInt("advertiserIndex");
            double effect = reader.getAttributeAsDouble("effect");
            this.setAdvertiserEffect(queryIndex, advertiserIndex, effect);
        }
    }

    @Override
    protected final void writeWithLock(TransportWriter writer) {
        Query[] arrquery = this.queries;
        int n = arrquery.length;
        int n2 = 0;
        while (n2 < n) {
            Query query = arrquery[n2];
            writer.write(query);
            ++n2;
        }
        arrquery = this.advertisers;
        n = arrquery.length;
        n2 = 0;
        while (n2 < n) {
            Query advertiser = arrquery[n2];
            writer.node("advertiser").attr("name", (String)((Object)advertiser)).endNode("advertiser");
            ++n2;
        }
        int queryIndex = 0;
        while (queryIndex < this.queries.length) {
            writer.node("continuationProbability").attr("index", queryIndex).attr("probability", this.continuationProbabilities[queryIndex]).endNode("continuationProbability");
            ++queryIndex;
        }
        queryIndex = 0;
        while (queryIndex < this.queries.length) {
            int advertiserIndex = 0;
            while (advertiserIndex < this.advertisers.length) {
                writer.node("advertiserEffect").attr("queryIndex", queryIndex).attr("advertiserIndex", advertiserIndex).attr("effect", this.advertiserEffects[queryIndex][advertiserIndex]).endNode("advertiserEffect");
                ++advertiserIndex;
            }
            ++queryIndex;
        }
    }
}

