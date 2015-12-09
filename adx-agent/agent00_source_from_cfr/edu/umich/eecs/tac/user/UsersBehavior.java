/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.Ranking;
import edu.umich.eecs.tac.sim.Auctioneer;
import edu.umich.eecs.tac.user.DistributionBroadcaster;
import edu.umich.eecs.tac.user.UserEventListener;
import se.sics.tasim.aw.Message;

public interface UsersBehavior
extends DistributionBroadcaster {
    public void nextTimeUnit(int var1);

    public void setup();

    public void stopped();

    public void shutdown();

    public void messageReceived(Message var1);

    public Ranking getRanking(Query var1, Auctioneer var2);

    public boolean addUserEventListener(UserEventListener var1);

    public boolean containsUserEventListener(UserEventListener var1);

    public boolean removeUserEventListener(UserEventListener var1);
}

