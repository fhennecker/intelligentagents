/*
 * Decompiled with CFR 0_110.
 */
package adx.stats;

import adx.stats.SegmentQuery;
import adx.stats.Users;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import tau.tac.adx.agents.Agent00;
import tau.tac.adx.agents.CampaignData;
import tau.tac.adx.demand.CampaignStats;
import tau.tac.adx.report.adn.MarketSegment;

public class CampaignStrategy {
    private final Logger log = Logger.getLogger(Agent00.class.getName());
    private int day;
    private Map<Integer, CampaignData> myCampaigns;
    private CampaignData pendingCampaign;
    private double factor;
    private double qualityRating;
    private static final double Rcampaignmin = 1.0E-4;

    public CampaignStrategy(int day, Map<Integer, CampaignData> myCampaigns, CampaignData pendingCampaign, double factor, double qualityRating) {
        this.day = day;
        this.myCampaigns = myCampaigns;
        this.pendingCampaign = pendingCampaign;
        this.factor = factor;
        this.qualityRating = qualityRating;
    }

    public Object[] makeBidOffer() {
        long cmpBid = -1;
        boolean shouldWeBid = this.isCampaignReachable(this.pendingCampaign);
        long pendingCampaignDuration = this.pendingCampaign.getDayEnd() - this.pendingCampaign.getDayStart() + 1;
        for (CampaignData runningCmp : this.myCampaigns.values()) {
            Set<MarketSegment> overlappingSegments;
            Set<MarketSegment> incomingCmpSegment;
            Set<MarketSegment> runningCmpSegment;
            double runningCmpImpsToGo = (double)runningCmp.getReachImps().longValue() - runningCmp.getStats().getTargetedImps();
            if (runningCmp.getDayStart() <= 1 || runningCmp.getDayEnd() < (long)(this.day + 2) || runningCmpImpsToGo <= 0.0 || (overlappingSegments = SegmentQuery.calculateOverlappingSegments(runningCmpSegment = runningCmp.getTargetSegment(), incomingCmpSegment = this.pendingCampaign.getTargetSegment())).size() <= 0) continue;
            this.log.info("Found overlapping segments: " + overlappingSegments.toString());
            long overlappingSegmentPopulationSize = Users.getInstance().countSegments(overlappingSegments);
            double possibleImpressions = Users.getInstance().countImpressions(overlappingSegments, this.pendingCampaign.getMobileCoef(), this.pendingCampaign.getVideoCoef());
            double chance = (double)overlappingSegmentPopulationSize / possibleImpressions;
            long runningCmpLeftDays = runningCmp.getDayEnd() - (long)this.day;
            double incomingCmpImpressionsFirstFewDays = (double)this.pendingCampaign.getReachImps().longValue() / (double)pendingCampaignDuration * (double)(runningCmpLeftDays > pendingCampaignDuration ? pendingCampaignDuration : runningCmpLeftDays);
            this.log.info("    overlappingSegmentSize = " + overlappingSegmentPopulationSize);
            this.log.info("    possibleImpressionsForIncomingCmp = " + possibleImpressions);
            this.log.info("    overlappingSegmentSize / possibleImpressionsForIncomingCmp = " + chance);
            this.log.info("    runningCmpLeftDays = " + runningCmpLeftDays);
            this.log.info("    runningCmpImpsTogo = " + runningCmpImpsToGo);
            this.log.info("    incomingCmpImpressionsFirstFewDays = " + incomingCmpImpressionsFirstFewDays);
            boolean bl = shouldWeBid = shouldWeBid && incomingCmpImpressionsFirstFewDays < runningCmpImpsToGo;
            if (!shouldWeBid) break;
        }
        if (shouldWeBid) {
            cmpBid = (long)((double)this.pendingCampaign.getReachImps().longValue() * this.factor / this.qualityRating);
            Object[] fixedOffer = this.fixOffer(cmpBid);
            cmpBid = (Long)fixedOffer[0];
            this.factor = (Double)fixedOffer[1];
        } else {
            cmpBid = (long)(0.99 * (double)this.pendingCampaign.getReachImps().longValue() * this.qualityRating);
            this.log.info("AVOID BIDDING!");
        }
        return new Object[]{cmpBid, this.factor};
    }

    private Object[] fixOffer(long cmpBid) {
        int i = 0;
        while (i < 100) {
            double cmpBidUnits = (double)cmpBid / 1000.0;
            if (cmpBidUnits * this.qualityRating > (double)this.pendingCampaign.getReachImps().longValue() * 1.0E-4) break;
            this.factor += (this.factor - 0.1) / 8.0;
            cmpBid = (long)((double)this.pendingCampaign.getReachImps().longValue() * this.factor / this.qualityRating);
            this.log.info("OUT OF RANGE, increased factor to: " + this.factor);
            ++i;
        }
        return new Object[]{cmpBid, this.factor};
    }

    private boolean isCampaignReachable(CampaignData cmp) {
        long duration = cmp.getDayEnd() - cmp.getDayStart() + 1;
        double avgImps = cmp.getReachImps() / duration;
        long segmentSize = Users.getInstance().countSegments(cmp.getTargetSegment());
        double possibleReach = Users.getInstance().countImpressions(cmp.getTargetSegment(), cmp.getMobileCoef(), cmp.getVideoCoef());
        this.log.info("    isCmapginReachable:");
        this.log.info("        avgImps = " + avgImps);
        this.log.info("        segmentSize = " + segmentSize);
        this.log.info("        possibleReach: " + possibleReach);
        this.log.info("        reachImps=" + cmp.getReachImps());
        this.log.info("        possible > 1.2 * avgImps? " + (possibleReach > 1.2 * avgImps));
        if (possibleReach > 1.2 * avgImps) {
            return true;
        }
        return false;
    }
}

