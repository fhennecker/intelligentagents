/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.is;

import com.botbox.util.ArrayUtils;

public class CompetitionSchedule {
    private int id = -1;
    private int parentID = 0;
    private String name;
    private long startTime;
    private String simulationType;
    private String simulationParams;
    private int timeBetweenSimulations;
    private long[] reservationStartTime;
    private int[] reservationLength;
    private int reservationCount;
    private int simulationsBeforeReservation = 0;
    private int simulationsReservationLength;
    private boolean isSimulationsClosed;
    private int[][] simulationParticipants;
    private int[][] simulationRoles;
    private int simulationCount;
    private int[] participants;
    private float startWeight;
    private int flags;
    private String scoreClassName;

    public CompetitionSchedule(String name) {
        this.setName(name);
    }

    public int getID() {
        return this.id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getParentCompetitionID() {
        return this.parentID;
    }

    public void setParentCompetitionID(int parentID) {
        this.parentID = parentID;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        if (name == null || (name = name.trim()).length() < 2) {
            throw new IllegalArgumentException("name must be at least 2 characters");
        }
        this.name = name;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getSimulationType() {
        return this.simulationType;
    }

    public void setSimulationType(String simulationType) {
        this.simulationType = simulationType;
    }

    public String getSimulationParams() {
        return this.simulationParams;
    }

    public void setSimulationParams(String simulationParams) {
        this.simulationParams = simulationParams;
    }

    public int getTimeBetweenSimulations() {
        return this.timeBetweenSimulations;
    }

    public void setTimeBetweenSimulations(int timeBetweenSimulations) {
        this.timeBetweenSimulations = timeBetweenSimulations;
    }

    public float getStartWeight() {
        return this.startWeight;
    }

    public void setStartWeight(float startWeight) {
        this.startWeight = startWeight;
    }

    public int getFlags() {
        return this.flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public void validate() {
        if (this.startTime <= 0) {
            throw new IllegalStateException("no start time");
        }
        if (this.name == null) {
            throw new IllegalStateException("no name specified");
        }
        if (this.id < 0) {
            throw new IllegalStateException("no id specified");
        }
        if (this.simulationCount == 0) {
            throw new IllegalStateException("no simulations specified");
        }
        if (this.participants == null || this.participants.length == 0) {
            throw new IllegalStateException("no participants specified");
        }
    }

    public String getScoreClassName() {
        return this.scoreClassName;
    }

    public void setScoreClassName(String scoreClassName) {
        this.scoreClassName = scoreClassName;
    }

    public void addTimeReservation(long startTime, int lengthInMillis) {
        if (this.reservationStartTime == null) {
            this.reservationStartTime = new long[10];
            this.reservationLength = new int[10];
        } else if (this.reservationStartTime.length == this.reservationCount) {
            this.reservationStartTime = ArrayUtils.setSize(this.reservationStartTime, this.reservationCount + 10);
            this.reservationLength = ArrayUtils.setSize(this.reservationLength, this.reservationCount + 10);
        }
        this.reservationStartTime[this.reservationCount] = startTime;
        this.reservationLength[this.reservationCount++] = lengthInMillis;
    }

    public int getReservationCount() {
        return this.reservationCount;
    }

    public long getReservationStartTime(int index) {
        if (index < 0 || index >= this.reservationCount) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.reservationCount);
        }
        return this.reservationStartTime[index];
    }

    public int getReservationLength(int index) {
        if (index < 0 || index >= this.reservationCount) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.reservationCount);
        }
        return this.reservationLength[index];
    }

    public int getSimulationsBeforeReservation() {
        return this.simulationsBeforeReservation;
    }

    public int getSimulationsReservationLength() {
        return this.simulationsReservationLength;
    }

    public void setReservationBetweenSimulations(int simulationsBeforeReservation, int reservationLength) {
        this.simulationsBeforeReservation = simulationsBeforeReservation;
        this.simulationsReservationLength = reservationLength;
    }

    public void setParticipants(int[] participants) {
        this.participants = participants;
    }

    public int[] getParticipants() {
        return this.participants;
    }

    public boolean isSimulationsClosed() {
        return this.isSimulationsClosed;
    }

    public void setSimulationsClosed(boolean isSimulationsClosed) {
        this.isSimulationsClosed = isSimulationsClosed;
    }

    public void addSimulation(int[] participants) {
        this.addSimulation(participants, null);
    }

    public void addSimulation(int[] participants, int[] roles) {
        if (participants != null && roles != null && participants.length != roles.length) {
            throw new IllegalArgumentException("participants and roles must be of equal size");
        }
        if (this.simulationParticipants == null) {
            this.simulationParticipants = new int[10][];
            this.simulationRoles = new int[10][];
        } else if (this.simulationParticipants.length == this.simulationCount) {
            this.simulationParticipants = (int[][])ArrayUtils.setSize((Object[])this.simulationParticipants, this.simulationCount + 100);
            this.simulationRoles = (int[][])ArrayUtils.setSize((Object[])this.simulationRoles, this.simulationCount + 100);
        }
        this.simulationParticipants[this.simulationCount] = participants;
        this.simulationRoles[this.simulationCount++] = roles;
    }

    public int getSimulationCount() {
        return this.simulationCount;
    }

    public int[] getParticipants(int index) {
        if (index < 0 || index >= this.simulationCount) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.simulationCount);
        }
        return this.simulationParticipants[index];
    }

    public int[] getRoles(int index) {
        if (index < 0 || index >= this.simulationCount) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.simulationCount);
        }
        return this.simulationRoles[index];
    }
}

