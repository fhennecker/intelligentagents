/*
 * Decompiled with CFR 0_110.
 */
package adx.stats;

import tau.tac.adx.agents.CampaignData;
import tau.tac.adx.report.adn.MarketSegment;

public class CampaignSegmentTracker {
    private CampaignData campaign;
    private MarketSegment segment;
    private double avgDailyBudget;
    private String agent;
    private int daysLeft;

    public CampaignSegmentTracker(CampaignData campaign, MarketSegment segment, double budget, String agent) {
        this.segment = segment;
        this.daysLeft = (int)(campaign.getDayEnd() + 1 - campaign.getDayStart());
        this.avgDailyBudget = budget / (double)this.daysLeft / (double)campaign.getReachImps().longValue();
        this.agent = agent;
        this.campaign = campaign;
    }

    public int advanceDay() {
        return --this.daysLeft;
    }

    public CampaignData getCampaign() {
        return this.campaign;
    }

    public void setCampaign(CampaignData campaign) {
        this.campaign = campaign;
    }

    public MarketSegment getSegment() {
        return this.segment;
    }

    public void setSegment(MarketSegment segment) {
        this.segment = segment;
    }

    public double getAvgDailyBudget() {
        return this.avgDailyBudget;
    }

    public void setAvgDailyBudget(double avgDailyBudget) {
        this.avgDailyBudget = avgDailyBudget;
    }

    public String getAgent() {
        return this.agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public int getDaysLeft() {
        return this.daysLeft;
    }

    public void setDaysLeft(int daysLeft) {
        this.daysLeft = daysLeft;
    }
}

