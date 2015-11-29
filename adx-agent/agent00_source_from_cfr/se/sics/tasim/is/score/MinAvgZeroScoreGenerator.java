/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.is.score;

import java.util.Comparator;
import se.sics.tasim.is.common.CompetitionParticipant;
import se.sics.tasim.is.common.DefaultScoreGenerator;

public class MinAvgZeroScoreGenerator
extends DefaultScoreGenerator {
    public MinAvgZeroScoreGenerator() {
        this.setShowingZeroGameAgents(true);
        this.setShowingAverageScoreWithoutZeroGames(true);
        this.setShowingWeightedAverageScoreWithoutZeroGames(true);
    }

    @Override
    protected Comparator getComparator(boolean isWeightUsed) {
        return isWeightUsed ? CompetitionParticipant.getMinAvgZeroWeightedComparator() : CompetitionParticipant.getMinAvgZeroComparator();
    }

    @Override
    protected void addPostInfo(StringBuffer page) {
        page.append("<br><em>Agents are ranked by the lowest of average score and average score without zero games.</em>");
    }
}

