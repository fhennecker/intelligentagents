/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.role;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import javax.swing.JPanel;

public class SimulationTabPanel
extends JPanel {
    private TACAASimulationPanel simulationPanel;

    public SimulationTabPanel(TACAASimulationPanel simulationPanel) {
        this.simulationPanel = simulationPanel;
    }

    public TACAASimulationPanel getSimulationPanel() {
        return this.simulationPanel;
    }
}

