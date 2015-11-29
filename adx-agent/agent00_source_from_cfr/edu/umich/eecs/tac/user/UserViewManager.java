/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.props.Auction;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.UserClickModel;
import edu.umich.eecs.tac.user.User;
import edu.umich.eecs.tac.user.UserEventListener;
import se.sics.tasim.aw.TimeListener;

public interface UserViewManager
extends TimeListener {
    public boolean processImpression(User var1, Query var2, Auction var3);

    public boolean addUserEventListener(UserEventListener var1);

    public boolean containsUserEventListener(UserEventListener var1);

    public boolean removeUserEventListener(UserEventListener var1);

    public UserClickModel getUserClickModel();

    public void setUserClickModel(UserClickModel var1);
}

