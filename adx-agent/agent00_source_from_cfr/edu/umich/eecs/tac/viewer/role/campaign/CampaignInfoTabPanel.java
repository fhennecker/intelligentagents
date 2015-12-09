/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.role.campaign;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import edu.umich.eecs.tac.viewer.role.campaign.CampaignAuctionReportPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import tau.tac.adx.agents.CampaignData;
import tau.tac.adx.report.demand.campaign.auction.CampaignAuctionReport;

public class CampaignInfoTabPanel
extends SimulationTabPanel {
    private final TACAASimulationPanel simulationPanel;
    private JTabbedPane tabbedPane;
    private int day;
    private CampaignData pendingCampaign;
    private CampaignAuctionReport campaignAuctionReport;

    public CampaignInfoTabPanel(CampaignAuctionReport campaignAuctionReport, TACAASimulationPanel simulationPanel) {
        super(simulationPanel);
        this.campaignAuctionReport = campaignAuctionReport;
        this.simulationPanel = simulationPanel;
        this.initialize();
    }

    private void initialize() {
        this.setLayout(new GridLayout(1, 1));
        this.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = 1;
        CampaignAuctionReportPanel auctionReportTabPanel = new CampaignAuctionReportPanel(this.campaignAuctionReport, this.simulationPanel);
        panel.add((Component)auctionReportTabPanel, c);
        this.add(panel);
    }
}

