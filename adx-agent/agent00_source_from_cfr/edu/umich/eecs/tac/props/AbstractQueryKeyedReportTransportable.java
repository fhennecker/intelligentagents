/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.AbstractKeyedEntryList;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryEntry;

public abstract class AbstractQueryKeyedReportTransportable<T extends QueryEntry>
extends AbstractKeyedEntryList<Query, T> {
    public final int addQuery(Query query) {
        return this.addKey(query);
    }

    public final boolean containsQuery(Query query) {
        return this.containsKey(query);
    }

    public final Query getQuery(int index) {
        return (Query)this.getKey(index);
    }
}

