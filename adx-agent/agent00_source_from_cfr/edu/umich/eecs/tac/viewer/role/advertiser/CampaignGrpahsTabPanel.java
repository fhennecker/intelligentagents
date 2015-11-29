/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import edu.umich.eecs.tac.viewer.role.advertiser.CampaignGrpahsPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import tau.tac.adx.report.demand.CampaignReport;

public class CampaignGrpahsTabPanel
extends SimulationTabPanel {
    private final TACAASimulationPanel simulationPanel;
    private final int agent;
    private final String name;
    private final Color legendColor;
    private final int campaignId;
    private CampaignGrpahsPanel campaignGrpahsPanel;
    private final long expectedImpressionReach;

    public CampaignGrpahsTabPanel(TACAASimulationPanel simulationPanel, int agent, String advertiser, Color legendColor, int campaignId, long expectedImpressionReach) {
        super(simulationPanel);
        this.simulationPanel = simulationPanel;
        this.agent = agent;
        this.name = advertiser;
        this.legendColor = legendColor;
        this.campaignId = campaignId;
        this.expectedImpressionReach = expectedImpressionReach;
        this.initialize();
    }

    private void initialize() {
        this.setLayout(new GridBagLayout());
        this.campaignGrpahsPanel = new CampaignGrpahsPanel(this.agent, this.name, this.simulationPanel, false, this.campaignId, this.expectedImpressionReach);
        GridBagConstraints c2 = new GridBagConstraints();
        c2.gridx = 0;
        c2.gridy = 0;
        c2.weightx = 2.0;
        c2.weighty = 2.0;
        c2.fill = 1;
        this.add((Component)this.campaignGrpahsPanel, c2);
    }

    public void update(CampaignReport campaignReport) {
        this.campaignGrpahsPanel.updateCampaigns(campaignReport);
    }
}

