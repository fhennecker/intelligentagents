/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.report.query;

import edu.umich.eecs.tac.props.QueryReport;

public interface AdxQueryReportSender {
    public void sendQueryReport(String var1, QueryReport var2);

    public void broadcastImpressions(String var1, int var2);
}

