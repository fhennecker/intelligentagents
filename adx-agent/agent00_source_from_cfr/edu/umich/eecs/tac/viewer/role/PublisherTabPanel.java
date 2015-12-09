/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.role;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import edu.umich.eecs.tac.viewer.role.publisher.RankingTabPanel;
import edu.umich.eecs.tac.viewer.role.publisher.SeriesTabPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.LayoutManager;
import javax.swing.JTabbedPane;

public class PublisherTabPanel
extends SimulationTabPanel {
    public PublisherTabPanel(TACAASimulationPanel simulationPanel) {
        super(simulationPanel);
        this.initialize();
    }

    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        JTabbedPane tabbedPane = new JTabbedPane(1);
        this.add((Component)tabbedPane, "Center");
        SeriesTabPanel seriesTabPanel = new SeriesTabPanel(this.getSimulationPanel());
        tabbedPane.addTab("Bid Series", seriesTabPanel);
        RankingTabPanel rankingTabPanel = new RankingTabPanel(this.getSimulationPanel());
        tabbedPane.addTab("Auction Rankings", rankingTabPanel);
    }
}

