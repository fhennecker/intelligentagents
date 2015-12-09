/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.role;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.role.AgentRevCostPanel;
import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

public class RevCostPanel
extends SimulationTabPanel {
    private Map<String, AgentRevCostPanel> agentPanels = new HashMap<String, AgentRevCostPanel>();

    public RevCostPanel(TACAASimulationPanel simulationPanel) {
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
        final /* synthetic */ RevCostPanel this$0;

        private ParticipantListener(RevCostPanel revCostPanel) {
            this.this$0 = revCostPanel;
        }

        @Override
        public void participant(int agent, int role, String name, int participantID) {
            if (!this.this$0.agentPanels.containsKey(name) && role == 1) {
                AgentRevCostPanel agentRevCostPanel = new AgentRevCostPanel(agent, name, this.this$0.getSimulationPanel(), false);
                this.this$0.agentPanels.put(name, agentRevCostPanel);
                this.this$0.add(agentRevCostPanel);
            }
        }

        /* synthetic */ ParticipantListener(RevCostPanel revCostPanel, ParticipantListener participantListener) {
            ParticipantListener participantListener2;
            participantListener2(revCostPanel);
        }
    }

}

