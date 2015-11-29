/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.props.Auction;
import edu.umich.eecs.tac.props.PublisherInfo;
import edu.umich.eecs.tac.props.Query;
import se.sics.tasim.aw.Message;

public interface PublisherBehavior {
    public void nextTimeUnit(int var1);

    public void setup();

    public void stopped();

    public void shutdown();

    public void messageReceived(Message var1);

    public void sendQueryReportsToAll();

    public Auction runAuction(Query var1);

    public void applyBidUpdates();

    public PublisherInfo getPublisherInfo();

    public void setPublisherInfo(PublisherInfo var1);
}

