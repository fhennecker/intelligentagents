/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import edu.umich.eecs.tac.viewer.role.advertiser.AdvertiserRateMetricsPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;

public class AdvertiserMainTabPanel
extends SimulationTabPanel {
    private final TACAASimulationPanel simulationPanel;
    private final int agent;
    private final String name;
    private final Color legendColor;

    public AdvertiserMainTabPanel(TACAASimulationPanel simulationPanel, int agent, String advertiser, Color legendColor) {
        super(simulationPanel);
        this.simulationPanel = simulationPanel;
        this.agent = agent;
        this.name = advertiser;
        this.legendColor = legendColor;
        this.initialize();
    }

    private void initialize() {
        this.setLayout(new GridBagLayout());
        AdvertiserRateMetricsPanel ratesMetricsPanel = new AdvertiserRateMetricsPanel(this.agent, this.name, this.simulationPanel, false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = 1;
        this.add((Component)ratesMetricsPanel, c);
    }
}

