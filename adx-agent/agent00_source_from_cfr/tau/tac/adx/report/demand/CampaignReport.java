/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.report.demand;

import edu.umich.eecs.tac.props.AbstractKeyedEntryList;
import edu.umich.eecs.tac.props.KeyedEntry;
import java.util.List;
import tau.tac.adx.demand.CampaignStats;
import tau.tac.adx.report.demand.CampaignReportEntry;
import tau.tac.adx.report.demand.CampaignReportKey;

public class CampaignReport
extends AbstractKeyedEntryList<CampaignReportKey, CampaignReportEntry> {
    private static final long serialVersionUID = -383908225939942652L;

    @Override
    protected CampaignReportEntry createEntry(CampaignReportKey key) {
        return new CampaignReportEntry(key);
    }

    @Override
    protected final Class<CampaignReportEntry> entryClass() {
        return CampaignReportEntry.class;
    }

    public String toMyString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (CampaignReportEntry entry : this.getEntries()) {
            stringBuilder.append(entry.getKey()).append(" : ").append(entry).append("\n");
        }
        return "CampaignReport: " + stringBuilder;
    }

    public CampaignReportEntry addReportEntry(CampaignReportKey campaignReportKey) {
        this.lockCheck();
        int index = this.addKey(campaignReportKey);
        return (CampaignReportEntry)this.getEntry(index);
    }

    public CampaignReportEntry getCampaignReportEntry(CampaignReportKey campaignReportKey) {
        return (CampaignReportEntry)this.getEntry(campaignReportKey);
    }

    private CampaignReportKey getKey(Integer id) {
        return new CampaignReportKey(id);
    }

    public void addStatsEntry(Integer campaignId, CampaignStats campaignStats) {
        CampaignReportKey key = this.getKey(campaignId);
        CampaignReportEntry reportEntry = this.getCampaignReportEntry(key);
        if (reportEntry == null) {
            reportEntry = this.addReportEntry(key);
            reportEntry.setCampaignStats(campaignStats);
        }
    }
}

