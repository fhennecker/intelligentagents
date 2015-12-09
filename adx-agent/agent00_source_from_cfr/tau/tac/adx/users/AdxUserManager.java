/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.users;

import se.sics.tasim.aw.Message;
import se.sics.tasim.aw.TimeListener;
import tau.tac.adx.props.PublisherCatalog;
import tau.tac.adx.sim.AdxAuctioneer;

public interface AdxUserManager
extends TimeListener {
    public void initialize(int var1);

    public void triggerBehavior(AdxAuctioneer var1);

    public PublisherCatalog getPublisherCatalog();

    public void messageReceived(Message var1);
}

