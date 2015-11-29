/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.props;

import edu.umich.eecs.tac.props.AbstractTransportableEntry;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.props.AdxQueryEntry;

public abstract class AdxAbstractQueryEntry
extends AbstractTransportableEntry<AdxQuery>
implements AdxQueryEntry {
    @Override
    public final AdxQuery getQuery() {
        return (AdxQuery)this.getKey();
    }

    public final void setQuery(AdxQuery query) {
        this.setKey(query);
    }

    @Override
    protected final String keyNodeName() {
        return AdxQuery.class.getSimpleName();
    }
}

