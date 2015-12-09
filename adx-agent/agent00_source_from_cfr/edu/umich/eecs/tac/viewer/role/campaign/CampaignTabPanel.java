/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.role.campaign;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.auction.ResultsPageModel;
import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import edu.umich.eecs.tac.viewer.role.campaign.CampaignInfoTabPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTabbedPane;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.viewer.TickListener;
import tau.tac.adx.report.demand.campaign.auction.CampaignAuctionReport;

public class CampaignTabPanel
extends SimulationTabPanel {
    private JTabbedPane tabbedPane;
    private int currentDay;
    private final Map<Integer, CampaignInfoTabPanel> campaignInfoPanels = new HashMap<Integer, CampaignInfoTabPanel>();
    private final Map<Query, ResultsPageModel> resultsPageModels = new HashMap<Query, ResultsPageModel>();
    private int participantNum = 0;
    private final TACAASimulationPanel simulationPanel;

    public CampaignTabPanel(TACAASimulationPanel simulationPanel) {
        super(simulationPanel);
        this.simulationPanel = simulationPanel;
        simulationPanel.addViewListener(new ParticipantListener(this, null));
        simulationPanel.addTickListener(new DayListener());
        this.initialize();
    }

    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        this.tabbedPane = new JTabbedPane(4);
        this.tabbedPane.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        this.add((Component)this.tabbedPane, "Center");
    }

    static /* synthetic */ void access$0(CampaignTabPanel campaignTabPanel, int n) {
        campaignTabPanel.currentDay = n;
    }

    static /* synthetic */ void access$6(CampaignTabPanel campaignTabPanel, int n) {
        campaignTabPanel.participantNum = n;
    }

    protected class DayListener
    implements TickListener {
        protected DayListener() {
        }

        @Override
        public void simulationTick(long serverTime, int simulationDate) {
            CampaignTabPanel.access$0(CampaignTabPanel.this, simulationDate);
        }

        @Override
        public void tick(long serverTime) {
        }
    }

    private class ParticipantListener
    extends ViewAdaptor {
        final /* synthetic */ CampaignTabPanel this$0;

        private ParticipantListener(CampaignTabPanel campaignTabPanel) {
            this.this$0 = campaignTabPanel;
        }

        @Override
        public void dataUpdated(int type, Transportable value) {
            if (value instanceof CampaignAuctionReport) {
                CampaignAuctionReport campaignAuctionReport = (CampaignAuctionReport)value;
                if (!this.this$0.campaignInfoPanels.containsKey(campaignAuctionReport.getCampaignID())) {
                    CampaignInfoTabPanel infoPanel = new CampaignInfoTabPanel(campaignAuctionReport, this.this$0.simulationPanel);
                    this.this$0.campaignInfoPanels.put(campaignAuctionReport.getCampaignID(), infoPanel);
                    this.this$0.tabbedPane.addTab("Day #" + this.this$0.currentDay + " - " + campaignAuctionReport.getCampaignID(), infoPanel);
                    CampaignTabPanel campaignTabPanel = this.this$0;
                    CampaignTabPanel.access$6(campaignTabPanel, campaignTabPanel.participantNum + 1);
                }
            }
        }

        /* synthetic */ ParticipantListener(CampaignTabPanel campaignTabPanel, ParticipantListener participantListener) {
            ParticipantListener participantListener2;
            participantListener2(campaignTabPanel);
        }
    }

}

