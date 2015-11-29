/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.demand;

import java.util.Map;
import java.util.Set;
import se.sics.tasim.aw.TimeListener;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.demand.CampaignStats;
import tau.tac.adx.devices.Device;
import tau.tac.adx.report.adn.MarketSegment;
import tau.tac.adx.report.demand.campaign.auction.CampaignAuctionReport;
import tau.tac.adx.users.AdxUser;

public interface Campaign
extends TimeListener {
    public void addAdvertiserBid(String var1, Long var2);

    public Map<String, Long> getBiddingAdvertisers();

    public void allocateToAdvertiser(String var1);

    public CampaignAuctionReport auction();

    public String getAdvertiser();

    public long getBudgetMillis();

    public boolean isOverTodaysLimit();

    public void impress(AdxUser var1, AdType var2, Device var3, double var4);

    public int getRemainingDays();

    public boolean isActive();

    public boolean shouldReport();

    public boolean isAllocated();

    public CampaignStats getStats(int var1, int var2);

    public CampaignStats getTodayStats();

    public Long getReachImps();

    public int getDayStart();

    public int getDayEnd();

    public Set<MarketSegment> getTargetSegment();

    public double getVideoCoef();

    public double getMobileCoef();

    public int getId();

    public void preNextTimeUnit(int var1);

    public CampaignStats getTotals();

    public int getImpressionLimit();

    public void registerToEventBus();

    public Double getBudgetlimit();

    public String logToString();

    public Double getTotalBudgetlimit();

    public int getTotalImpressionLimit();

    public boolean isOverTotalLimits();

    public void setRandomAllocPr(Double var1);

    public boolean shouldWarnLimits();
}

