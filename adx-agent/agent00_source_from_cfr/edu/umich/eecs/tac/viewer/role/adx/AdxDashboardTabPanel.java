/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.role.adx;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import edu.umich.eecs.tac.viewer.role.adx.AdNetProfitsPanel;
import edu.umich.eecs.tac.viewer.role.adx.AdxRevCostPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;

public class AdxDashboardTabPanel
extends SimulationTabPanel {
    public AdxDashboardTabPanel(TACAASimulationPanel simulationPanel) {
        super(simulationPanel);
        this.setLayout(new GridBagLayout());
        this.setBackground(TACAAViewerConstants.LEGEND_COLORS[0]);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = 1;
        this.add((Component)new AdNetProfitsPanel(simulationPanel), c);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        this.add((Component)new AdxRevCostPanel(simulationPanel), c);
    }
}

