/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer;

import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import java.util.logging.Logger;
import javax.swing.JComponent;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.viewer.SimulationViewer;
import se.sics.tasim.viewer.ViewerPanel;
import tau.tac.adx.sim.TACAdxConstants;

public class TACAAViewer
extends SimulationViewer {
    private static final Logger log = Logger.getLogger(TACAAViewer.class.getName());
    private TACAASimulationPanel simulationPanel;
    private RetailCatalog catalog;

    @Override
    public void init(ViewerPanel panel) {
        this.simulationPanel = new TACAASimulationPanel(panel);
    }

    @Override
    public JComponent getComponent() {
        return this.simulationPanel;
    }

    @Override
    public void setServerTime(long serverTime) {
    }

    @Override
    public void simulationStarted(int realSimID, String type, long startTime, long endTime, String timeUnitName, int timeUnitCount) {
        this.simulationPanel.simulationStarted(startTime, endTime, timeUnitCount);
    }

    @Override
    public void simulationStopped(int realSimID) {
        this.simulationPanel.simulationStopped();
    }

    @Override
    public void nextSimulation(int publicSimID, long startTime) {
    }

    @Override
    public void intCache(int agent, int type, int[] cache) {
    }

    @Override
    public void participant(int agent, int role, String name, int participantID) {
        this.simulationPanel.participant(agent, role, name, participantID);
    }

    @Override
    public void nextTimeUnit(int timeUnit) {
        this.simulationPanel.nextTimeUnit(timeUnit);
    }

    @Override
    public void dataUpdated(int agent, int type, int value) {
        this.simulationPanel.dataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int agent, int type, long value) {
        this.simulationPanel.dataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int agent, int type, float value) {
        this.simulationPanel.dataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int agent, int type, double value) {
        this.simulationPanel.dataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int agent, int type, String value) {
        this.simulationPanel.dataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int agent, int type, Transportable value) {
        this.simulationPanel.dataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int type, Transportable value) {
        Class valueType = value.getClass();
        if (valueType == RetailCatalog.class) {
            this.catalog = (RetailCatalog)value;
        }
        this.simulationPanel.dataUpdated(type, value);
    }

    @Override
    public void interaction(int fromAgent, int toAgent, int type) {
    }

    @Override
    public void interactionWithRole(int fromAgent, int role, int type) {
    }

    public String getRoleName(int role) {
        return role >= 0 && role < TACAdxConstants.ROLE_NAME.length ? TACAdxConstants.ROLE_NAME[role] : Integer.toString(role);
    }

    public String getAgentName(int agentIndex) {
        return this.simulationPanel.getAgentName(agentIndex);
    }

    public RetailCatalog getCatalog() {
        return this.catalog;
    }
}

