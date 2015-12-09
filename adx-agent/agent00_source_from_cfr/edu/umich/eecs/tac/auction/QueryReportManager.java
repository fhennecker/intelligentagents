/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.user.UserEventListener;

public interface QueryReportManager
extends UserEventListener {
    public void addAdvertiser(String var1);

    public void sendQueryReportToAll();

    public int size();
}

