/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.UserClickModel;
import edu.umich.eecs.tac.sim.Auctioneer;
import edu.umich.eecs.tac.user.UserEventListener;
import se.sics.tasim.aw.Message;
import se.sics.tasim.aw.TimeListener;

public interface UserManager
extends TimeListener {
    public void initialize(int var1);

    public void triggerBehavior(Auctioneer var1);

    public boolean addUserEventListener(UserEventListener var1);

    public boolean containsUserEventListener(UserEventListener var1);

    public boolean removeUserEventListener(UserEventListener var1);

    public int[] getStateDistribution();

    public int[] getStateDistribution(Product var1);

    public RetailCatalog getRetailCatalog();

    public UserClickModel getUserClickModel();

    public void setUserClickModel(UserClickModel var1);

    public void messageReceived(Message var1);
}

