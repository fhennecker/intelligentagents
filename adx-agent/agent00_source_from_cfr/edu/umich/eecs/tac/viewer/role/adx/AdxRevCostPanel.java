/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.role.adx;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import edu.umich.eecs.tac.viewer.role.adx.AdNetRevCostPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

public class AdxRevCostPanel
extends SimulationTabPanel {
    private final Map<String, AdNetRevCostPanel> agentPanels = new HashMap<String, AdNetRevCostPanel>();

    public AdxRevCostPanel(TACAASimulationPanel simulationPanel) {
        super(simulationPanel);
        simulationPanel.addViewListener(new ParticipantListener(this, null));
        this.initialize();
    }

    private void initialize() {
        this.setLayout(new GridLayout(2, 4));
        this.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Daily Revenue and Cost", 2, 0));
    }

    private class ParticipantListener
    extends ViewAdaptor {
        final /* synthetic */ AdxRevCostPanel this$0;

        private ParticipantListener(AdxRevCostPanel adxRevCostPanel) {
            this.this$0 = adxRevCostPanel;
        }

        @Override
        public void participant(int agent, int role, String name, int participantID) {
            if (!this.this$0.agentPanels.containsKey(name) && role == 5) {
                AdNetRevCostPanel agentRevCostPanel = new AdNetRevCostPanel(agent, name, this.this$0.getSimulationPanel(), false);
                this.this$0.agentPanels.put(name, agentRevCostPanel);
                this.this$0.add(agentRevCostPanel);
            }
        }

        /* synthetic */ ParticipantListener(AdxRevCostPanel adxRevCostPanel, ParticipantListener participantListener) {
            ParticipantListener participantListener2;
            participantListener2(adxRevCostPanel);
        }
    }

}

