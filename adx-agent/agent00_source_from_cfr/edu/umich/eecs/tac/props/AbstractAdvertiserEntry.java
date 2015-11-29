/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.AbstractStringEntry;
import edu.umich.eecs.tac.props.AdvertiserEntry;

public abstract class AbstractAdvertiserEntry
extends AbstractStringEntry
implements AdvertiserEntry {
    @Override
    public final String getAdvertiser() {
        return (String)this.getKey();
    }

    public final void setAdvertiser(String advertiser) {
        this.setKey(advertiser);
    }
}

