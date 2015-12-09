/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.AbstractTransportableEntry;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryEntry;

public abstract class AbstractQueryEntry
extends AbstractTransportableEntry<Query>
implements QueryEntry {
    @Override
    public final Query getQuery() {
        return (Query)this.getKey();
    }

    public final void setQuery(Query query) {
        this.setKey(query);
    }

    @Override
    protected final String keyNodeName() {
        return Query.class.getSimpleName();
    }
}

