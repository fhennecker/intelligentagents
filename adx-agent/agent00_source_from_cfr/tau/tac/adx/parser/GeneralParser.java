/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.parser;

import edu.umich.eecs.tac.Parser;
import edu.umich.eecs.tac.props.BankStatus;
import java.io.PrintStream;
import org.apache.commons.lang3.StringUtils;
import se.sics.isl.transport.Transportable;
import se.sics.isl.util.ConfigManager;
import se.sics.tasim.logtool.LogReader;
import se.sics.tasim.logtool.ParticipantInfo;
import se.sics.tasim.props.SimulationStatus;
import tau.tac.adx.demand.CampaignStats;
import tau.tac.adx.report.adn.AdNetworkKey;
import tau.tac.adx.report.adn.AdNetworkReport;
import tau.tac.adx.report.adn.AdNetworkReportEntry;
import tau.tac.adx.report.demand.AdNetworkDailyNotification;
import tau.tac.adx.report.demand.CampaignReport;
import tau.tac.adx.report.demand.CampaignReportEntry;
import tau.tac.adx.report.demand.CampaignReportKey;

public class GeneralParser
extends Parser {
    private int day = 0;
    private final String[] participantNames;
    private final boolean[] is_Advertiser;
    private final ParticipantInfo[] participants;
    private final ConfigManager configManager;
    private boolean ucs = false;
    private boolean rating = false;
    private boolean bank = false;
    private boolean campaign = false;
    private boolean adnet = false;
    private boolean all = false;

    public GeneralParser(LogReader reader, ConfigManager configManager) {
        super(reader);
        this.configManager = configManager;
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
        System.out.println("****General Log Prser***");
        this.ucs = configManager.getPropertyAsBoolean("ucs", false);
        this.rating = configManager.getPropertyAsBoolean("rating", false);
        this.bank = configManager.getPropertyAsBoolean("bank", false);
        this.campaign = configManager.getPropertyAsBoolean("campaign", false);
        this.adnet = configManager.getPropertyAsBoolean("adnet", false);
        this.all = configManager.getPropertyAsBoolean("all", false);
    }

    @Override
    protected void message(int sender, int receiver, Transportable content) {
        if (this.all) {
            System.out.println(String.valueOf(this.getBasicInfoString(receiver)) + StringUtils.rightPad(new StringBuilder(String.valueOf(content.getClass().getSimpleName())).append(": ").toString(), 30) + content);
        }
        if (!this.all && content instanceof AdNetworkDailyNotification) {
            AdNetworkDailyNotification dailyNotification;
            if (this.ucs) {
                dailyNotification = (AdNetworkDailyNotification)content;
                System.out.println(String.valueOf(this.getBasicInfoString(receiver)) + StringUtils.rightPad("UCS level: ", 20) + dailyNotification.getServiceLevel());
            }
            if (this.rating) {
                dailyNotification = (AdNetworkDailyNotification)content;
                System.out.println(String.valueOf(this.getBasicInfoString(receiver)) + StringUtils.rightPad("Quality rating: ", 20) + dailyNotification.getQualityScore());
            }
        } else if (!this.all && this.bank && content instanceof BankStatus) {
            BankStatus status = (BankStatus)content;
            System.out.println(String.valueOf(this.getBasicInfoString(receiver)) + StringUtils.rightPad("Bank balance: ", 20) + status.getAccountBalance());
        } else if (!this.all && this.campaign && content instanceof CampaignReport) {
            CampaignReport campaignReport = (CampaignReport)content;
            for (CampaignReportKey campaignReportKey : campaignReport) {
                CampaignReportEntry reportEntry = (CampaignReportEntry)campaignReport.getEntry(campaignReportKey);
                CampaignStats campaignStats = reportEntry.getCampaignStats();
                System.out.println(String.valueOf(this.getBasicInfoString(receiver)) + StringUtils.rightPad("Campaign report: ", 20) + StringUtils.rightPad(new StringBuilder("#").append(campaignReportKey.getCampaignId()).toString(), 5) + "\t Targeted Impressions: " + StringUtils.rightPad(new StringBuilder().append(campaignStats.getTargetedImps()).toString(), 5) + "\t Non Targeted Impressions: " + StringUtils.rightPad(new StringBuilder().append(campaignStats.getOtherImps()).toString(), 5) + "\t Cost: " + campaignStats.getCost());
            }
        } else if (!this.all && this.adnet && content instanceof AdNetworkReport) {
            AdNetworkReport adNetworkReport = (AdNetworkReport)content;
            for (AdNetworkKey adNetworkKey : adNetworkReport) {
                AdNetworkReportEntry reportEntry = (AdNetworkReportEntry)adNetworkReport.getEntry(adNetworkKey);
                System.out.println(String.valueOf(this.getBasicInfoString(receiver)) + StringUtils.rightPad("Ad Network report: ", 20) + adNetworkKey + "," + "Bid count: " + StringUtils.rightPad(new StringBuilder().append(reportEntry.getBidCount()).toString(), 5) + "\t Win count: " + StringUtils.rightPad(new StringBuilder().append(reportEntry.getWinCount()).toString(), 5) + "\t Cost: " + StringUtils.rightPad(new StringBuilder().append(reportEntry.getCost()).toString(), 5));
            }
        } else if (content instanceof SimulationStatus) {
            SimulationStatus ss = (SimulationStatus)content;
            this.day = ss.getCurrentDate();
        }
    }

    private String getBasicInfoString(int receiver) {
        return String.valueOf(StringUtils.rightPad(new StringBuilder().append(this.day).toString(), 5)) + "\t" + StringUtils.rightPad(this.participantNames[receiver], 20) + "\t";
    }
}

