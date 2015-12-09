/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.users;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.Ranking;
import edu.umich.eecs.tac.sim.Auctioneer;
import se.sics.tasim.aw.Message;

public interface AdxUsersBehavior {
    public void nextTimeUnit(int var1);

    public void setup();

    public void stopped();

    public void shutdown();

    public void messageReceived(Message var1);

    public Ranking getRanking(Query var1, Auctioneer var2);

    public void sendReportsToAll();
}

