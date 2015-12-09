/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.is.common;

import com.botbox.util.ArrayUtils;
import java.util.logging.Logger;
import se.sics.tasim.is.SimulationInfo;
import se.sics.tasim.is.common.CompetitionParticipant;

public class Competition {
    private static final Logger log = Logger.getLogger(Competition.class.getName());
    public static final int WEEKEND_LOW = 1;
    public static final int NO_WEIGHT = 2;
    public static final int LOWEST_SCORE_FOR_ZERO = 64;
    private int id;
    private String name;
    private int parentID;
    private Competition parentCompetition;
    private long startTime;
    private long endTime;
    private int startUniqueID = -1;
    private int startPublicID = -1;
    private int simulationCount;
    private int flags;
    private double startWeight = 1.0;
    private String scoreClassName = null;
    private int startDay = -1;
    private CompetitionParticipant[] participants;
    private int participantCount;
    private static boolean forceWeightFlag = false;
    private static double forcedWeight;

    public Competition(int id, String name) {
        if (name == null || (name = name.trim()).length() < 2) {
            throw new IllegalArgumentException("name must be at least 2 characters");
        }
        this.id = id;
        this.name = name;
    }

    public Competition(int id, String name, long startTime, long endTime, int startUniqueID, int simulationCount, double startWeight) {
        this(id, name);
        this.startTime = startTime;
        this.endTime = endTime;
        this.startUniqueID = startUniqueID;
        this.simulationCount = simulationCount;
        this.startWeight = startWeight;
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    void setName(String name) {
        if (name == null) {
            throw new NullPointerException();
        }
        this.name = name;
    }

    public int getStartUniqueID() {
        return this.startUniqueID;
    }

    public int getEndUniqueID() {
        return this.startUniqueID + this.simulationCount - 1;
    }

    public boolean isSimulationIncluded(int simulationUniqID) {
        if (this.startUniqueID <= simulationUniqID && this.startUniqueID + this.simulationCount - 1 >= simulationUniqID && this.startUniqueID >= 0) {
            return true;
        }
        return false;
    }

    public int getSimulationCount() {
        return this.simulationCount;
    }

    public boolean hasSimulationID() {
        if (this.startPublicID >= 0) {
            return true;
        }
        return false;
    }

    public int getStartSimulationID() {
        return this.startPublicID;
    }

    public void setStartSimulationID(int simulationID) {
        this.startPublicID = simulationID;
    }

    public int getEndSimulationID() {
        return this.startPublicID + this.simulationCount - 1;
    }

    public boolean containsSimulation(int simulationID) {
        if (this.getStartSimulationID() <= simulationID && this.getEndSimulationID() >= simulationID) {
            return true;
        }
        return false;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public void addSimulation(SimulationInfo info) {
        long endTime;
        int simID;
        long startTime;
        int id = info.getID();
        if (id < this.startUniqueID || this.startUniqueID < 0) {
            this.startUniqueID = id;
        }
        if (info.hasSimulationID() && ((simID = info.getSimulationID()) < this.startPublicID || this.startPublicID < 0)) {
            this.startPublicID = simID;
        }
        if ((startTime = info.getStartTime()) < this.startTime || this.startTime <= 0) {
            this.startTime = startTime;
        }
        if ((endTime = info.getEndTime()) > this.endTime) {
            this.endTime = endTime;
        }
        ++this.simulationCount;
    }

    public void addParticipant(CompetitionParticipant part) {
        if (this.participants == null) {
            this.participants = new CompetitionParticipant[6];
        } else if (this.participantCount == this.participants.length) {
            this.participants = (CompetitionParticipant[])ArrayUtils.setSize(this.participants, this.participantCount + 6);
        }
        this.participants[this.participantCount++] = part;
    }

    public int getParticipantCount() {
        return this.participantCount;
    }

    public CompetitionParticipant getParticipantByID(int userID) {
        int index = CompetitionParticipant.indexOf(this.participants, 0, this.participantCount, userID);
        return index >= 0 ? this.participants[index] : null;
    }

    public CompetitionParticipant getParticipant(int index) {
        if (index < 0 || index >= this.participantCount) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.participantCount);
        }
        return this.participants[index];
    }

    public CompetitionParticipant[] getParticipants() {
        if (this.participants != null && this.participantCount < this.participants.length) {
            this.participants = (CompetitionParticipant[])ArrayUtils.setSize(this.participants, this.participantCount);
        }
        return this.participants;
    }

    public int getFlags() {
        return this.flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public String getScoreClassName() {
        return this.scoreClassName;
    }

    public void setScoreClassName(String scoreClassName) {
        this.scoreClassName = scoreClassName;
    }

    public double getStartWeight() {
        return this.startWeight;
    }

    public void setStartWeight(double startWeight) {
        this.startWeight = startWeight;
    }

    public boolean isWeightUsed() {
        if ((this.flags & 2) == 0) {
            return true;
        }
        return false;
    }

    public static void setForcedWeight(double weight, boolean force) {
        forcedWeight = weight;
        forceWeightFlag = force;
    }

    public static boolean isWeightForced() {
        return forceWeightFlag;
    }

    public static double getForcedWeight() {
        return forcedWeight;
    }

    public double getWeight(int gameID) {
        if (forceWeightFlag) {
            return forcedWeight;
        }
        return this.startWeight;
    }

    public boolean hasParentCompetition() {
        if (this.parentID > 0) {
            return true;
        }
        return false;
    }

    public int getParentCompetitionID() {
        return this.parentID;
    }

    void setParentCompetitionID(int parentID) {
        this.parentID = parentID;
    }

    public boolean isParentCompetition(Competition competition) {
        if (competition == this) {
            return true;
        }
        if (this.parentCompetition != null) {
            return this.parentCompetition.isParentCompetition(competition);
        }
        return false;
    }

    public Competition getParentCompetition() {
        return this.parentCompetition;
    }

    void setParentCompetition(Competition competition) {
        this.parentCompetition = competition;
    }

    public static int indexOf(Competition[] competitions, int competitionID) {
        if (competitions != null) {
            int i = 0;
            int n = competitions.length;
            while (i < n) {
                if (competitions[i].id == competitionID) {
                    return i;
                }
                ++i;
            }
        }
        return -1;
    }

    public static int indexOf(Competition[] competitions, int start, int end, int competitionID) {
        int i = start;
        while (i < end) {
            if (competitions[i].id == competitionID) {
                return i;
            }
            ++i;
        }
        return -1;
    }
}

