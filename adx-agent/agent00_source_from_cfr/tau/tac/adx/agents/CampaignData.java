/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.agents;

import java.util.Set;
import tau.tac.adx.demand.CampaignStats;
import tau.tac.adx.report.adn.MarketSegment;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;
import tau.tac.adx.report.demand.InitialCampaignMessage;

public class CampaignData {
    Long reachImps;
    long dayStart;
    long dayEnd;
    Set<MarketSegment> targetSegment;
    double videoCoef;
    double mobileCoef;
    int id;
    CampaignStats stats;
    double budget;

    public CampaignData(InitialCampaignMessage icm) {
        this.reachImps = icm.getReachImps();
        this.dayStart = icm.getDayStart();
        this.dayEnd = icm.getDayEnd();
        this.targetSegment = icm.getTargetSegment();
        this.videoCoef = icm.getVideoCoef();
        this.mobileCoef = icm.getMobileCoef();
        this.id = icm.getId();
        this.stats = new CampaignStats(0.0, 0.0, 0.0);
        this.budget = 0.0;
    }

    public void setBudget(double d) {
        this.budget = d;
    }

    public CampaignData(CampaignOpportunityMessage com) {
        this.dayStart = com.getDayStart();
        this.dayEnd = com.getDayEnd();
        this.id = com.getId();
        this.reachImps = com.getReachImps();
        this.targetSegment = com.getTargetSegment();
        this.mobileCoef = com.getMobileCoef();
        this.videoCoef = com.getVideoCoef();
        this.stats = new CampaignStats(0.0, 0.0, 0.0);
        this.budget = 0.0;
    }

    public String toString() {
        return "Campaign ID " + this.id + ": " + "day " + this.dayStart + " to " + this.dayEnd + " " + MarketSegment.names(this.targetSegment) + ", reach: " + this.reachImps + " coefs: (v=" + this.videoCoef + ", m=" + this.mobileCoef + ")";
    }

    int impsTogo() {
        return (int)Math.max(0.0, (double)this.reachImps.longValue() - this.stats.getTargetedImps());
    }

    void setStats(CampaignStats s) {
        this.stats.setValues(s);
    }

    public Long getReachImps() {
        return this.reachImps;
    }

    public void setReachImps(Long reachImps) {
        this.reachImps = reachImps;
    }

    public long getDayStart() {
        return this.dayStart;
    }

    public void setDayStart(long dayStart) {
        this.dayStart = dayStart;
    }

    public long getDayEnd() {
        return this.dayEnd;
    }

    public void setDayEnd(long dayEnd) {
        this.dayEnd = dayEnd;
    }

    public Set<MarketSegment> getTargetSegment() {
        return this.targetSegment;
    }

    public void setTargetSegment(Set<MarketSegment> targetSegment) {
        this.targetSegment = targetSegment;
    }

    public double getVideoCoef() {
        return this.videoCoef;
    }

    public void setVideoCoef(double videoCoef) {
        this.videoCoef = videoCoef;
    }

    public double getMobileCoef() {
        return this.mobileCoef;
    }

    public void setMobileCoef(double mobileCoef) {
        this.mobileCoef = mobileCoef;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CampaignStats getStats() {
        return this.stats;
    }

    public double getBudget() {
        return this.budget;
    }
}

