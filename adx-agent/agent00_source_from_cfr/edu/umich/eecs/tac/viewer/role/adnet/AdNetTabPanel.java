/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.role.adnet;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.auction.ResultsPageModel;
import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import edu.umich.eecs.tac.viewer.role.adnet.AdNetInfoTabPanel;
import edu.umich.eecs.tac.viewer.role.adx.AdNetOverviewPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTabbedPane;

public class AdNetTabPanel
extends SimulationTabPanel {
    private JTabbedPane tabbedPane;
    private final Map<String, AdNetInfoTabPanel> advertiserInfoPanels = new HashMap<String, AdNetInfoTabPanel>();
    private final Map<Query, ResultsPageModel> resultsPageModels = new HashMap<Query, ResultsPageModel>();
    private int participantNum = 0;
    private final TACAASimulationPanel simulationPanel;

    public AdNetTabPanel(TACAASimulationPanel simulationPanel) {
        super(simulationPanel);
        this.simulationPanel = simulationPanel;
        simulationPanel.addViewListener(new ParticipantListener(this, null));
        this.initialize();
    }

    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        this.tabbedPane = new JTabbedPane(1);
        this.tabbedPane.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        this.add((Component)this.tabbedPane, "Center");
        AdNetOverviewPanel overviewPanel = new AdNetOverviewPanel(this.getSimulationPanel());
        this.tabbedPane.addTab("Overview", overviewPanel);
    }

    static /* synthetic */ void access$5(AdNetTabPanel adNetTabPanel, int n) {
        adNetTabPanel.participantNum = n;
    }

    private class ParticipantListener
    extends ViewAdaptor {
        final /* synthetic */ AdNetTabPanel this$0;

        private ParticipantListener(AdNetTabPanel adNetTabPanel) {
            this.this$0 = adNetTabPanel;
        }

        @Override
        public void participant(int agent, int role, String name, int participantID) {
            if (!this.this$0.advertiserInfoPanels.containsKey(name) && role == 5) {
                AdNetInfoTabPanel infoPanel = new AdNetInfoTabPanel(agent, name, this.this$0.resultsPageModels, this.this$0.simulationPanel, TACAAViewerConstants.LEGEND_COLORS[this.this$0.participantNum]);
                this.this$0.advertiserInfoPanels.put(name, infoPanel);
                this.this$0.tabbedPane.addTab(name, infoPanel);
                AdNetTabPanel adNetTabPanel = this.this$0;
                AdNetTabPanel.access$5(adNetTabPanel, adNetTabPanel.participantNum + 1);
            }
        }

        /* synthetic */ ParticipantListener(AdNetTabPanel adNetTabPanel, ParticipantListener participantListener) {
            ParticipantListener participantListener2;
            participantListener2(adNetTabPanel);
        }
    }

}

