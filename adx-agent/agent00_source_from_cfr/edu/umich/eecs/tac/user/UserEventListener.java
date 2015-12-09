/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.Query;

public interface UserEventListener {
    public void queryIssued(Query var1);

    public void viewed(Query var1, Ad var2, int var3, String var4, boolean var5);

    public void clicked(Query var1, Ad var2, int var3, double var4, String var6);

    public void converted(Query var1, Ad var2, int var3, double var4, String var6);
}

