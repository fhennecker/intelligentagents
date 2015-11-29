/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.parser;

import edu.umich.eecs.tac.Parser;
import java.io.PrintStream;
import org.apache.commons.lang3.StringUtils;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.logtool.LogReader;
import se.sics.tasim.logtool.ParticipantInfo;
import se.sics.tasim.props.SimulationStatus;
import tau.tac.adx.demand.CampaignStats;
import tau.tac.adx.report.demand.CampaignReport;
import tau.tac.adx.report.demand.CampaignReportEntry;
import tau.tac.adx.report.demand.CampaignReportKey;

public class CampaignReportParser
extends Parser {
    private int day = 0;
    private final String[] participantNames;
    private final boolean[] is_Advertiser;
    private final ParticipantInfo[] participants;

    public CampaignReportParser(LogReader reader) {
        super(reader);
        System.out.println("****AGENT INDEXES****");
        this.participants = reader.getParticipants();
        if (this.participants == null) {
            throw new IllegalStateException("no participants");
        }
        this.participantNames = new String[this.participants.length];
        this.is_Advertiser = new boolean[this.participants.length];
        int i = 0;
        int n = this.participants.length;
        while (i < n) {
            ParticipantInfo info = this.participants[i];
            int agent = info.getIndex();
            System.out.println(String.valueOf(info.getName()) + ": " + agent);
            this.participantNames[agent] = info.getName();
            this.is_Advertiser[agent] = info.getRole() == 1;
            ++i;
        }
        System.out.println("****Campaign data***");
        System.out.println(String.valueOf(StringUtils.rightPad("Day", 20)) + "\t" + StringUtils.rightPad("Agent", 20) + "\t" + StringUtils.rightPad("Campaign ID", 20) + "\t" + StringUtils.rightPad("Targeted", 20) + "\t" + StringUtils.rightPad("Non Taregted", 20) + "\tCost");
    }

    @Override
    protected void dataUpdated(int type, Transportable content) {
    }

    @Override
    protected void message(int sender, int receiver, Transportable content) {
        if (content instanceof CampaignReport) {
            CampaignReport campaignReport = (CampaignReport)content;
            for (CampaignReportKey campaignReportKey : campaignReport) {
                CampaignReportEntry reportEntry = (CampaignReportEntry)campaignReport.getEntry(campaignReportKey);
                CampaignStats campaignStats = reportEntry.getCampaignStats();
                System.out.println(String.valueOf(StringUtils.rightPad(new StringBuilder().append(this.day).toString(), 20)) + "\t" + StringUtils.rightPad(this.participantNames[receiver], 20) + "\t #" + StringUtils.rightPad(new StringBuilder().append(campaignReportKey.getCampaignId()).toString(), 20) + "\t" + StringUtils.rightPad(new StringBuilder().append(campaignStats.getTargetedImps()).toString(), 20) + "\t" + StringUtils.rightPad(new StringBuilder().append(campaignStats.getOtherImps()).toString(), 20) + "\t" + campaignStats.getCost());
            }
        } else if (content instanceof SimulationStatus) {
            SimulationStatus ss = (SimulationStatus)content;
            this.day = ss.getCurrentDate();
        }
    }
}

