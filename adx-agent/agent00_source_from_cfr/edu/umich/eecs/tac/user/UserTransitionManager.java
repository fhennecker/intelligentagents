/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.user.QueryState;
import edu.umich.eecs.tac.user.User;
import se.sics.tasim.aw.TimeListener;

public interface UserTransitionManager
extends TimeListener {
    public QueryState transition(User var1, boolean var2);
}

