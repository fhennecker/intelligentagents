/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.viewer.ViewListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.viewer.TickListener;
import tau.tac.adx.agents.CampaignData;
import tau.tac.adx.demand.CampaignStats;
import tau.tac.adx.report.adn.MarketSegment;
import tau.tac.adx.report.demand.AdNetworkDailyNotification;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;
import tau.tac.adx.report.demand.CampaignReport;
import tau.tac.adx.report.demand.CampaignReportEntry;
import tau.tac.adx.report.demand.CampaignReportKey;
import tau.tac.adx.report.demand.InitialCampaignMessage;

public class AdvertiserRateMetricsPanel
extends JPanel {
    private final int agent;
    private final String advertiser;
    private final Set<AdNetworkDailyNotification> campaigns;
    private final boolean advertiserBorder;
    private JTextArea area;
    private int day;
    private CampaignData pendingCampaign;

    public AdvertiserRateMetricsPanel(int agent, String advertiser, TACAASimulationPanel simulationPanel, boolean advertiserBorder) {
        this.agent = agent;
        this.advertiser = advertiser;
        this.advertiserBorder = advertiserBorder;
        this.initialize();
        simulationPanel.addViewListener(new DataUpdateListener(this, null));
        this.campaigns = new HashSet<AdNetworkDailyNotification>();
        simulationPanel.addTickListener(new DayListener());
    }

    private void initialize() {
        this.setLayout(new GridLayout(1, 1));
        this.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        this.area = new JTextArea();
        this.add(new JScrollPane(this.area));
    }

    public int getAgent() {
        return this.agent;
    }

    public String getAdvertiser() {
        return this.advertiser;
    }

    protected void updateCampaigns(AdNetworkDailyNotification adNetworkDailyNotification) {
        if (this.pendingCampaign.getId() == adNetworkDailyNotification.getCampaignId() && adNetworkDailyNotification.getCostMillis() != 0) {
            String message = "Day " + adNetworkDailyNotification.getEffectiveDay() + ":\t" + "Range [" + this.pendingCampaign.getDayStart() + ", " + this.pendingCampaign.getDayEnd() + "]\t" + this.pendingCampaign.getTargetSegment() + "\twon at cost (Millis)" + adNetworkDailyNotification.getCostMillis();
            this.area.append(message);
            this.area.append("\r\n");
        }
    }

    protected void updateCampaigns(InitialCampaignMessage campaignMessage) {
        String message = "Day 0:\tRange [" + campaignMessage.getDayStart() + ", " + campaignMessage.getDayEnd() + "]\t" + campaignMessage.getTargetSegment() + "\trecieved";
        this.area.append(message);
        this.area.append("\r\n");
    }

    private void updateCampaigns(CampaignReport campaignReport) {
        for (CampaignReportKey campaignKey : campaignReport.keys()) {
            int cmpId = campaignKey.getCampaignId();
            CampaignStats cstats = campaignReport.getCampaignReportEntry(campaignKey).getCampaignStats();
            String message = "Day " + this.day + ": Updating campaign " + cmpId + " stats: " + cstats.getTargetedImps() + " tgtImps " + cstats.getOtherImps() + " nonTgtImps. Cost of imps is " + cstats.getCost();
            this.area.append(message);
            this.area.append("\r\n");
        }
    }

    protected void handleCampaignOpportunityMessage(CampaignOpportunityMessage com) {
        this.pendingCampaign = new CampaignData(com);
    }

    static /* synthetic */ void access$0(AdvertiserRateMetricsPanel advertiserRateMetricsPanel, int n) {
        advertiserRateMetricsPanel.day = n;
    }

    private class DataUpdateListener
    extends ViewAdaptor {
        final /* synthetic */ AdvertiserRateMetricsPanel this$0;

        private DataUpdateListener(AdvertiserRateMetricsPanel advertiserRateMetricsPanel) {
            this.this$0 = advertiserRateMetricsPanel;
        }

        @Override
        public void dataUpdated(final int agentId, final int type, final Transportable value) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    if (agentId == DataUpdateListener.this.this$0.agent) {
                        switch (type) {
                            case 406: {
                                DataUpdateListener.this.this$0.updateCampaigns((AdNetworkDailyNotification)value);
                                break;
                            }
                            case 403: {
                                DataUpdateListener.this.this$0.updateCampaigns((InitialCampaignMessage)value);
                            }
                        }
                    }
                }
            });
        }

        @Override
        public void dataUpdated(final int type, final Transportable value) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    switch (type) {
                        case 404: {
                            DataUpdateListener.this.this$0.handleCampaignOpportunityMessage((CampaignOpportunityMessage)value);
                        }
                    }
                }
            });
        }

        /* synthetic */ DataUpdateListener(AdvertiserRateMetricsPanel advertiserRateMetricsPanel, DataUpdateListener dataUpdateListener) {
            DataUpdateListener dataUpdateListener2;
            dataUpdateListener2(advertiserRateMetricsPanel);
        }

    }

    protected class DayListener
    implements TickListener {
        protected DayListener() {
        }

        @Override
        public void tick(long serverTime) {
        }

        @Override
        public void simulationTick(long serverTime, int simulationDate) {
            AdvertiserRateMetricsPanel.access$0(AdvertiserRateMetricsPanel.this, simulationDate);
        }
    }

}

