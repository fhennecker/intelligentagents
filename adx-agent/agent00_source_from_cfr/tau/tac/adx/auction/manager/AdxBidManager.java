/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.auction.manager;

import java.util.Set;
import se.sics.tasim.aw.TimeListener;
import tau.tac.adx.bids.BidInfo;
import tau.tac.adx.props.AdxBidBundle;
import tau.tac.adx.props.AdxQuery;

public interface AdxBidManager
extends TimeListener {
    public void addAdvertiser(String var1);

    public BidInfo getBidInfo(String var1, AdxQuery var2);

    public void updateBids(String var1, AdxBidBundle var2);

    public void applyBidUpdates();

    public Set<String> advertisers();
}

