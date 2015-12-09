/*
 * Decompiled with CFR 0_110.
 */
package adx.stats;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import tau.tac.adx.agents.Agent00;
import tau.tac.adx.report.adn.MarketSegment;

public class SegmentQuery {
    private static Logger log = Logger.getLogger(Agent00.class.getName());

    public static MarketSegment getGender(Set<MarketSegment> ms) {
        if (ms.contains((Object)MarketSegment.MALE)) {
            return MarketSegment.MALE;
        }
        if (ms.contains((Object)MarketSegment.FEMALE)) {
            return MarketSegment.FEMALE;
        }
        return null;
    }

    public static MarketSegment getAge(Set<MarketSegment> ms) {
        if (ms.contains((Object)MarketSegment.YOUNG)) {
            return MarketSegment.YOUNG;
        }
        if (ms.contains((Object)MarketSegment.OLD)) {
            return MarketSegment.OLD;
        }
        return null;
    }

    public static MarketSegment getIncome(Set<MarketSegment> ms) {
        if (ms.contains((Object)MarketSegment.LOW_INCOME)) {
            return MarketSegment.LOW_INCOME;
        }
        if (ms.contains((Object)MarketSegment.HIGH_INCOME)) {
            return MarketSegment.HIGH_INCOME;
        }
        return null;
    }

    public static Set<MarketSegment> calculateOverlappingSegments(Set<MarketSegment> runningCmp, Set<MarketSegment> pendingCmp) {
        HashSet<MarketSegment> overlappingSegments = new HashSet<MarketSegment>();
        log.info("Running cmp segment: " + runningCmp.toString());
        log.info("Pending cmp segment: " + pendingCmp.toString());
        SegmentQuery.updateOverlappingSegments(SegmentQuery.getGender(runningCmp), SegmentQuery.getGender(pendingCmp), overlappingSegments);
        SegmentQuery.updateOverlappingSegments(SegmentQuery.getAge(runningCmp), SegmentQuery.getAge(pendingCmp), overlappingSegments);
        SegmentQuery.updateOverlappingSegments(SegmentQuery.getIncome(runningCmp), SegmentQuery.getIncome(pendingCmp), overlappingSegments);
        return overlappingSegments;
    }

    public static void updateOverlappingSegments(MarketSegment s1, MarketSegment s2, Set<MarketSegment> overlappingSegments) {
        if (s1 != null) {
            if (s2 != null) {
                if (s1.equals((Object)s2)) {
                    overlappingSegments.add(s1);
                }
                return;
            }
            overlappingSegments.add(s1);
            return;
        }
        if (s2 != null) {
            overlappingSegments.add(s2);
        }
    }
}

