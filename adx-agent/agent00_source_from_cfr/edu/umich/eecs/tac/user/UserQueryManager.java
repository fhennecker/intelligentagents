/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.user.User;
import se.sics.tasim.aw.TimeListener;

public interface UserQueryManager
extends TimeListener {
    public Query generateQuery(User var1);
}

