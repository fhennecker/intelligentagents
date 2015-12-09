/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.sim;

import edu.umich.eecs.tac.props.SalesReport;

public interface SalesReportSender {
    public void sendSalesReport(String var1, SalesReport var2);

    public void broadcastConversions(String var1, int var2);
}

