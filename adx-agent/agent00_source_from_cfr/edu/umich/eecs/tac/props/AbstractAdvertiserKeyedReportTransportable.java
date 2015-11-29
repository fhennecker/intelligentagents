/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.AbstractKeyedEntryList;
import edu.umich.eecs.tac.props.AdvertiserEntry;

public abstract class AbstractAdvertiserKeyedReportTransportable<T extends AdvertiserEntry>
extends AbstractKeyedEntryList<String, T> {
    public final int addAdvertiser(String advertiser) throws NullPointerException {
        return this.addKey(advertiser);
    }

    public final boolean containsAdvertiser(String advertiser) {
        return this.containsKey(advertiser);
    }
}

