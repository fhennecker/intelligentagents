/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.is.score;

import java.util.Comparator;
import java.util.logging.Logger;
import se.sics.tasim.is.common.CompetitionParticipant;
import se.sics.tasim.is.common.ScoreMerger;

public class MinAvgZeroScoreMerger
extends ScoreMerger {
    private static final Logger log = Logger.getLogger(MinAvgZeroScoreMerger.class.getName());

    public MinAvgZeroScoreMerger() {
        this.setShowingAllAgents(true);
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
        page.append("<em>Agents are ranked by the lowest of average score and average score without zero games.</em><br>");
        super.addPostInfo(page);
    }
}

