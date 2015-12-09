/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.props.BidBundle;
import edu.umich.eecs.tac.props.Query;
import java.util.Set;
import se.sics.tasim.aw.TimeListener;
import tau.tac.adx.props.AdLink;

public interface BidManager
extends TimeListener {
    public void addAdvertiser(String var1);

    public double getBid(String var1, Query var2);

    public double getQualityScore(String var1, Query var2);

    public AdLink getAdLink(String var1, Query var2);

    public void updateBids(String var1, BidBundle var2);

    public void applyBidUpdates();

    public Set<String> advertisers();
}

