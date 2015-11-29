/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.is.common;

import java.io.File;
import se.sics.tasim.is.common.Competition;
import se.sics.tasim.is.common.CompetitionParticipant;

public abstract class ScoreGenerator {
    private String serverName;
    private String competitionPath;

    public final void init(String serverName, String competitionPath) {
        if (this.serverName != null) {
            throw new IllegalStateException("already initialized");
        }
        this.serverName = serverName;
        this.competitionPath = competitionPath;
    }

    protected String getServerName() {
        return this.serverName;
    }

    protected String getScoreFileName() {
        return String.valueOf(this.competitionPath) + File.separatorChar + "index.html";
    }

    protected CompetitionParticipant[] getCombinedParticipants(Competition competition) {
        CompetitionParticipant[] parts = competition.getParticipants();
        if (!competition.hasParentCompetition() || parts == null) {
            return parts;
        }
        CompetitionParticipant[] participants = new CompetitionParticipant[parts.length];
        int i = 0;
        int n = parts.length;
        while (i < n) {
            participants[i] = new CompetitionParticipant(parts[i]);
            ++i;
        }
        Competition parentCompetition = competition.getParentCompetition();
        while (parentCompetition != null) {
            int i2 = 0;
            int n2 = participants.length;
            while (i2 < n2) {
                CompetitionParticipant cpart = parentCompetition.getParticipantByID(participants[i2].getID());
                if (cpart != null) {
                    participants[i2].addScore(cpart);
                }
                ++i2;
            }
            parentCompetition = parentCompetition.getParentCompetition();
        }
        return participants;
    }

    public abstract boolean createScoreTable(Competition var1, int var2);
}

