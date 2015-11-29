/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.sim;

import edu.umich.eecs.tac.props.QueryReport;

public interface QueryReportSender {
    public void sendQueryReport(String var1, QueryReport var2);

    public void broadcastImpressions(String var1, int var2);

    public void broadcastClicks(String var1, int var2);
}

