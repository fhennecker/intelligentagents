/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.logviewer.monitor;

import edu.umich.eecs.tac.logviewer.gui.PositiveBoundedRangeModel;
import edu.umich.eecs.tac.logviewer.info.Actor;
import edu.umich.eecs.tac.logviewer.util.SimulationParser;
import javax.swing.JComponent;
import se.sics.isl.transport.Transportable;
import se.sics.isl.util.ConfigManager;
import se.sics.tasim.logtool.LogHandler;

public abstract class ParserMonitor {
    private SimulationParser simParser;
    private String name;
    private LogHandler logHandler;
    private PositiveBoundedRangeModel dayModel;

    public final void init(String name, LogHandler logHandler, SimulationParser simParser, PositiveBoundedRangeModel dayModel) {
        this.name = name;
        this.logHandler = logHandler;
        this.simParser = simParser;
        this.dayModel = dayModel;
    }

    public void parseStarted() {
    }

    public void parseStopped() {
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }

    protected LogHandler getLogHandler() {
        return this.logHandler;
    }

    protected ConfigManager getConfig() {
        return this.logHandler.getConfig();
    }

    protected SimulationParser getSimulationParser() {
        return this.simParser;
    }

    protected PositiveBoundedRangeModel getDayModel() {
        return this.dayModel;
    }

    public boolean hasSimulationView() {
        return false;
    }

    public JComponent getSimulationView() {
        return null;
    }

    public boolean hasAgentView(Actor actor) {
        return false;
    }

    public JComponent getAgentView(Actor actor) {
        return null;
    }

    protected void warn(String warning) {
        this.logHandler.warn(String.valueOf(this.name) + ": " + warning);
    }

    public void messageToRole(int sender, int role, Transportable content) {
    }

    public void message(int sender, int receiver, Transportable content) {
    }

    public void data(Transportable object) {
    }

    public void dataUpdated(int agent, int type, int value) {
    }

    public void dataUpdated(int agent, int type, long value) {
    }

    public void dataUpdated(int agent, int type, float value) {
    }

    public void dataUpdated(int agent, int type, String value) {
    }

    public void dataUpdated(int agent, int type, Transportable content) {
    }

    public void dataUpdated(int type, Transportable content) {
    }

    public void interest(int agent, long amount) {
    }

    public void transaction(int supplier, int customer, int orderID, long amount) {
    }

    public void penalty(int supplier, int customer, int orderID, int amount, boolean orderCancelled) {
    }

    public void nextDay(int date, long serverTime) {
    }

    public void unhandledNode(String nodeName) {
    }
}

