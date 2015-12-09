/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.role.advertiser.AdvertiserCountTabPanel;
import edu.umich.eecs.tac.viewer.role.advertiser.AdvertiserOverviewMetricsPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import javax.swing.JPanel;

public class AdvertiserOverviewPanel
extends JPanel {
    private AdvertiserOverviewMetricsPanel advertiserOverviewMetricsPanel;
    private AdvertiserCountTabPanel advertiserCountTabPanel;
    private TACAASimulationPanel simulationPanel;

    public AdvertiserOverviewPanel(TACAASimulationPanel simulationPanel) {
        this.simulationPanel = simulationPanel;
        this.initialize();
    }

    private void initialize() {
        this.setLayout(new GridBagLayout());
        this.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = 1;
        this.advertiserCountTabPanel = new AdvertiserCountTabPanel(this.simulationPanel);
        this.add((Component)this.advertiserCountTabPanel, c);
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 2.0;
        c.weighty = 1.0;
        c.fill = 1;
        this.advertiserOverviewMetricsPanel = new AdvertiserOverviewMetricsPanel(this.simulationPanel);
        this.add((Component)this.advertiserOverviewMetricsPanel, c);
    }
}
