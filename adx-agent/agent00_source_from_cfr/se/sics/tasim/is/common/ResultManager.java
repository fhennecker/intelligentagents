/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.is.common;

import java.io.IOException;
import se.sics.tasim.is.common.SimServer;
import se.sics.tasim.logtool.LogReader;
import se.sics.tasim.logtool.ParticipantInfo;

public abstract class ResultManager {
    private SimServer simServer;
    private boolean addToTable;
    private String destinationPath;
    private LogReader logReader;
    private String gameLogName;

    final void init(SimServer simServer, boolean addToTable, String gameLogName) {
        if (simServer == null) {
            throw new NullPointerException();
        }
        this.simServer = simServer;
        this.gameLogName = gameLogName;
        this.addToTable = addToTable;
    }

    public final void generateResult(LogReader logReader, String destinationPath) throws IOException {
        this.destinationPath = destinationPath;
        this.logReader = logReader;
        this.generateResult();
    }

    protected String getDestinationPath() {
        return this.destinationPath;
    }

    protected LogReader getLogReader() {
        return this.logReader;
    }

    protected String getGameLogName() {
        return this.gameLogName;
    }

    protected void addSimulationToHistory(ParticipantInfo[] participants) {
        this.addSimulationToHistory(participants, null);
    }

    protected void addSimulationToHistory(ParticipantInfo[] participants, String[] participantColors) {
        if (this.addToTable && this.simServer != null) {
            this.simServer.addSimulationToHistory(this.logReader, participants, participantColors);
        }
    }

    protected void addSimulationResult(ParticipantInfo[] participants, long[] scores) {
        if (this.simServer != null) {
            this.simServer.addSimulationResult(this.logReader, participants, scores, !this.addToTable);
        }
    }

    protected void addSimulationResult(ParticipantInfo[] participants, double[] scores) {
        if (this.simServer != null) {
            this.simServer.addSimulationResult(this.logReader, participants, scores, !this.addToTable);
        }
    }

    protected abstract void generateResult() throws IOException;
}

