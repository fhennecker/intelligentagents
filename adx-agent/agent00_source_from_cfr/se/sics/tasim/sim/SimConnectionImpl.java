/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.sim;

import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.isl.util.ConfigManager;
import se.sics.tasim.is.AgentLookup;
import se.sics.tasim.is.CompetitionSchedule;
import se.sics.tasim.is.SimConnection;
import se.sics.tasim.is.SimulationInfo;
import se.sics.tasim.sim.Admin;

public class SimConnectionImpl
extends SimConnection {
    private static final Logger log = Logger.getLogger(SimConnectionImpl.class.getName());
    private final Admin admin;
    private final AgentLookup lookup;
    private boolean isInitialized = false;

    public SimConnectionImpl(Admin admin, AgentLookup lookup) {
        this.admin = admin;
        this.lookup = lookup;
    }

    @Override
    public void init(ConfigManager config) {
    }

    @Override
    public void close() {
        this.isInitialized = false;
    }

    @Override
    public void dataUpdated(int type, int value) {
        this.admin.dataUpdated(type, value);
    }

    @Override
    public void setUser(String agentName, String password, int agentID) {
        this.lookup.setUser(agentName, password, agentID);
    }

    @Override
    public void setServerTime(long time) {
        this.admin.setServerTime(time);
        if (!this.isInitialized) {
            this.isInitialized = true;
            this.admin.sendStateToInfoSystem();
        }
    }

    @Override
    public void simulationInfo(SimulationInfo info) {
        this.admin.addSimulation(info);
    }

    @Override
    public void resultsGenerated(int simulationID) {
        this.admin.resultsGenerated(simulationID);
    }

    @Override
    public void addChatMessage(long time, String serverName, String userName, String message) {
        this.admin.addChatMessage(time, serverName, userName, message);
    }

    @Override
    public void scheduleCompetition(CompetitionSchedule schedule) {
        this.admin.scheduleCompetition(schedule, true);
    }

    @Override
    public void lockNextSimulations(int simulationCount) {
        this.admin.lockNextSimulations(simulationCount);
    }

    @Override
    public void addTimeReservation(long startTime, int lengthInMillis) {
        this.admin.addTimeReservation(startTime, lengthInMillis, true);
    }

    @Override
    public void createSimulation(String type, String params) {
        this.admin.createSimulation(type, params, true);
    }

    @Override
    public void removeSimulation(int simulationUniqID) {
        this.admin.removeSimulation(simulationUniqID);
    }

    @Override
    public void joinSimulation(int simulationUniqID, int agentID, String simRole) {
        try {
            this.admin.joinSimulation(simulationUniqID, agentID, simRole);
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "could not join agent " + agentID + " to simulation with id " + simulationUniqID, e);
        }
    }

    @Override
    public void joinSimulation(int simulationUniqID, int agentID, int simRole) {
        try {
            this.admin.joinSimulation(simulationUniqID, agentID, simRole);
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "could not join agent " + agentID + " to simulation with id " + simulationUniqID, e);
        }
    }
}

