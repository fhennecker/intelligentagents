/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.is;

import se.sics.isl.util.ConfigManager;
import se.sics.tasim.is.CompetitionSchedule;
import se.sics.tasim.is.InfoConnection;
import se.sics.tasim.is.SimulationInfo;

public abstract class SimConnection {
    public static final int STATUS = 1;
    public static final int UNIQUE_SIM_ID = 2;
    public static final int SIM_ID = 3;
    public static final int STATUS_READY = 1;
    private InfoConnection info;

    public void setInfoConnection(InfoConnection info) {
        if (this.info != null) {
            throw new IllegalStateException("Connection already set");
        }
        this.info = info;
    }

    protected InfoConnection getInfoConnection() {
        return this.info;
    }

    public abstract void init(ConfigManager var1);

    public abstract void close();

    public abstract void dataUpdated(int var1, int var2);

    public abstract void setUser(String var1, String var2, int var3);

    public abstract void setServerTime(long var1);

    public abstract void simulationInfo(SimulationInfo var1);

    public abstract void resultsGenerated(int var1);

    public abstract void addChatMessage(long var1, String var3, String var4, String var5);

    public abstract void scheduleCompetition(CompetitionSchedule var1);

    public abstract void lockNextSimulations(int var1);

    public abstract void addTimeReservation(long var1, int var3);

    public abstract void createSimulation(String var1, String var2);

    public abstract void removeSimulation(int var1);

    public abstract void joinSimulation(int var1, int var2, String var3);

    public abstract void joinSimulation(int var1, int var2, int var3);
}

