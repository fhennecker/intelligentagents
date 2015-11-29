/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.role;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.auction.ResultsPageModel;
import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import edu.umich.eecs.tac.viewer.role.advertiser.AdvertiserInfoTabPanel;
import edu.umich.eecs.tac.viewer.role.advertiser.AdvertiserOverviewPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTabbedPane;

public class AdvertiserTabPanel
extends SimulationTabPanel {
    private JTabbedPane tabbedPane;
    private final Map<String, AdvertiserInfoTabPanel> advertiserInfoPanels = new HashMap<String, AdvertiserInfoTabPanel>();
    private final Map<Query, ResultsPageModel> resultsPageModels = new HashMap<Query, ResultsPageModel>();
    private int participantNum = 0;
    private final TACAASimulationPanel simulationPanel;

    public AdvertiserTabPanel(TACAASimulationPanel simulationPanel) {
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
        AdvertiserOverviewPanel overviewPanel = new AdvertiserOverviewPanel(this.getSimulationPanel());
        this.tabbedPane.addTab("Overview", overviewPanel);
    }

    static /* synthetic */ void access$5(AdvertiserTabPanel advertiserTabPanel, int n) {
        advertiserTabPanel.participantNum = n;
    }

    private class ParticipantListener
    extends ViewAdaptor {
        final /* synthetic */ AdvertiserTabPanel this$0;

        private ParticipantListener(AdvertiserTabPanel advertiserTabPanel) {
            this.this$0 = advertiserTabPanel;
        }

        @Override
        public void participant(int agent, int role, String name, int participantID) {
            if (!this.this$0.advertiserInfoPanels.containsKey(name) && role == 5) {
                AdvertiserInfoTabPanel infoPanel = new AdvertiserInfoTabPanel(agent, name, this.this$0.resultsPageModels, this.this$0.simulationPanel, TACAAViewerConstants.LEGEND_COLORS[this.this$0.participantNum]);
                this.this$0.advertiserInfoPanels.put(name, infoPanel);
                this.this$0.tabbedPane.addTab(name, infoPanel);
                AdvertiserTabPanel advertiserTabPanel = this.this$0;
                AdvertiserTabPanel.access$5(advertiserTabPanel, advertiserTabPanel.participantNum + 1);
            }
        }

        /* synthetic */ ParticipantListener(AdvertiserTabPanel advertiserTabPanel, ParticipantListener participantListener) {
            ParticipantListener participantListener2;
            participantListener2(advertiserTabPanel);
        }
    }

}

