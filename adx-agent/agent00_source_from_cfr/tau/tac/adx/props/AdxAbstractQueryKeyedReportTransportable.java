/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.props;

import edu.umich.eecs.tac.props.AbstractKeyedEntryList;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.props.AdxQueryEntry;

public abstract class AdxAbstractQueryKeyedReportTransportable<T extends AdxQueryEntry>
extends AbstractKeyedEntryList<AdxQuery, T> {
    public final int addQuery(AdxQuery query) {
        return this.addKey(query);
    }

    public final boolean containsQuery(AdxQuery query) {
        return this.containsKey(query);
    }

    public final AdxQuery getQuery(int index) {
        return (AdxQuery)this.getKey(index);
    }
}

