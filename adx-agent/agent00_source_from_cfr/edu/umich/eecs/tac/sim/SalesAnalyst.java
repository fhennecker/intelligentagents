/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.sim;

import edu.umich.eecs.tac.sim.RecentConversionsTracker;
import edu.umich.eecs.tac.user.UserEventListener;

public interface SalesAnalyst
extends UserEventListener,
RecentConversionsTracker {
    public void addAccount(String var1);

    public void sendSalesReportToAll();
}

