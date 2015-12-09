/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.EventBus
 *  com.google.common.eventbus.Subscribe
 */
package tau.tac.adx.demand;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import edu.umich.eecs.tac.auction.AuctionUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import tau.tac.adx.AdxManager;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.demand.Accumulator;
import tau.tac.adx.demand.AccumulatorImpl;
import tau.tac.adx.demand.Campaign;
import tau.tac.adx.demand.CampaignStats;
import tau.tac.adx.demand.QualityManager;
import tau.tac.adx.devices.Device;
import tau.tac.adx.messages.CampaignLimitSet;
import tau.tac.adx.report.adn.MarketSegment;
import tau.tac.adx.report.demand.campaign.auction.CampaignAuctionReport;
import tau.tac.adx.report.demand.campaign.auction.CampaignAuctionReportEntry;
import tau.tac.adx.report.demand.campaign.auction.CampaignAuctionReportKey;
import tau.tac.adx.sim.TACAdxSimulation;
import tau.tac.adx.users.AdxUser;

public class CampaignImpl
implements Campaign,
Accumulator<CampaignStats> {
    private final Logger log = Logger.getLogger(CampaignImpl.class.getName());
    private static final double ERRA = 4.08577;
    private static final double ERRB = -3.08577;
    private static final Long DEFAULT_BUDGET_FACTOR = 1;
    private static final Double DEFAULT_RANDOM_ALLOC_PR = new Double(0.3);
    private static Random random = new Random();
    protected QualityManager qualityManager;
    int id;
    Long reachImps;
    int dayStart;
    int dayEnd;
    Set<MarketSegment> targetSegments;
    double videoCoef;
    double mobileCoef;
    private Double randomAllocPr;
    private static final double RESERVE_MAX_BUDGET_FACTOR = 1.0;
    private static final double RESERVE_MIN_BUDGET_FACTOR = 0.1;
    private final Map<String, Long> advertisersBids;
    long budgetMillis;
    String advertiser;
    protected int day;
    private CampaignStats todays;
    private CampaignStats totals;
    private Double budgetlimit;
    private int impressionLimit;
    private Double totalBudgetlimit;
    private int totalImpressionLimit;
    private int overLimitsWarnings = 0;
    private Double tomorrowsBudgetLimit;
    private int tomorrowsImpressionLimit;
    private final SortedMap<Integer, CampaignStats> dayStats;

    public Logger getLog() {
        return this.log;
    }

    public static double getErra() {
        return 4.08577;
    }

    public static double getErrb() {
        return -3.08577;
    }

    public static Long getDefaultBudgetFactor() {
        return DEFAULT_BUDGET_FACTOR;
    }

    public QualityManager getQualityManager() {
        return this.qualityManager;
    }

    public static double getReserveBudgetFactor() {
        return 1.0;
    }

    public Map<String, Long> getAdvertisersBids() {
        return this.advertisersBids;
    }

    public int getDay() {
        return this.day;
    }

    public CampaignStats getTodays() {
        return this.todays;
    }

    @Override
    public Double getBudgetlimit() {
        return this.budgetlimit;
    }

    @Override
    public Double getTotalBudgetlimit() {
        return this.totalBudgetlimit;
    }

    @Override
    public int getImpressionLimit() {
        return this.impressionLimit;
    }

    @Override
    public int getTotalImpressionLimit() {
        return this.totalImpressionLimit;
    }

    public Double getTomorrowsBudgetLimit() {
        return this.tomorrowsBudgetLimit;
    }

    public int getTomorrowsImpressionLimit() {
        return this.tomorrowsImpressionLimit;
    }

    public SortedMap<Integer, CampaignStats> getDayStats() {
        return this.dayStats;
    }

    public CampaignImpl(QualityManager qualityManager, int reachImps, int dayStart, int dayEnd, Set<MarketSegment> targetSegments, double videoCoef, double mobileCoef) {
        if (qualityManager == null) {
            throw new NullPointerException("qualityManager cannot be null");
        }
        this.randomAllocPr = DEFAULT_RANDOM_ALLOC_PR;
        this.id = this.hashCode();
        this.dayStats = new TreeMap<Integer, CampaignStats>();
        this.advertisersBids = new HashMap<String, Long>();
        this.budgetMillis = 0;
        this.advertiser = null;
        this.budgetlimit = Double.POSITIVE_INFINITY;
        this.totalBudgetlimit = Double.POSITIVE_INFINITY;
        this.tomorrowsBudgetLimit = Double.POSITIVE_INFINITY;
        this.impressionLimit = Integer.MAX_VALUE;
        this.totalImpressionLimit = Integer.MAX_VALUE;
        this.tomorrowsImpressionLimit = Integer.MAX_VALUE;
        this.day = dayStart;
        this.qualityManager = qualityManager;
        this.reachImps = reachImps;
        this.dayStart = dayStart;
        this.dayEnd = dayEnd;
        this.targetSegments = targetSegments;
        this.videoCoef = videoCoef;
        this.mobileCoef = mobileCoef;
        this.todays = new CampaignStats(0.0, 0.0, 0.0);
        this.totals = new CampaignStats(0.0, 0.0, 0.0);
    }

    @Override
    public void setRandomAllocPr(Double rap) {
        this.randomAllocPr = rap;
    }

    @Override
    public void registerToEventBus() {
        AdxManager.getInstance().getSimulation().getEventBus().register((Object)this);
    }

    @Override
    public long getBudgetMillis() {
        return this.budgetMillis;
    }

    @Override
    public Long getReachImps() {
        return this.reachImps;
    }

    @Override
    public int getDayStart() {
        return this.dayStart;
    }

    @Override
    public int getDayEnd() {
        return this.dayEnd;
    }

    public void setTomorowsLimit(CampaignLimitSet message) {
        this.tomorrowsBudgetLimit = message.getBudgetLimit();
        this.tomorrowsImpressionLimit = message.getImpressionLimit();
        this.log.log(Level.FINER, "Campaign " + this.id + " Tomorrows limits: " + this.tomorrowsBudgetLimit + ", " + this.tomorrowsImpressionLimit);
    }

    @Override
    public boolean isOverTodaysLimit() {
        if (this.budgetlimit >= this.todays.cost && (double)this.impressionLimit >= this.todays.tartgetedImps) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isOverTotalLimits() {
        if (this.totalBudgetlimit >= this.totals.cost + this.todays.cost && (double)this.totalImpressionLimit >= this.totals.tartgetedImps + this.todays.tartgetedImps) {
            return false;
        }
        return true;
    }

    @Override
    public Set<MarketSegment> getTargetSegment() {
        return this.targetSegments;
    }

    @Override
    public double getVideoCoef() {
        return this.videoCoef;
    }

    @Override
    public double getMobileCoef() {
        return this.mobileCoef;
    }

    @Override
    public void impress(AdxUser adxUser, AdType adType, Device device, double costPerMille) {
        if (this.isAllocated() && !this.isOverTodaysLimit() || !this.isOverTotalLimits()) {
            this.todays.cost += costPerMille / 1000.0;
            if (this.todays.cost > this.budgetlimit) {
                boolean bl = false;
            }
            double imps = (device == Device.mobile ? this.mobileCoef : 1.0) * (adType == AdType.video ? this.videoCoef : 1.0);
            Set<MarketSegment> actualSegments = MarketSegment.extractSegment(adxUser);
            if (actualSegments.containsAll(this.targetSegments)) {
                this.todays.tartgetedImps += imps;
            } else {
                this.todays.otherImps += imps;
            }
        }
    }

    double effectiveReachRatio(double imps) {
        double ratio = imps / (double)this.reachImps.longValue();
        return 0.48950381445847413 * (Math.atan(4.08577 * ratio + -3.08577) - Math.atan(-3.08577));
    }

    @Override
    public void preNextTimeUnit(int timeUnit) {
        if (timeUnit >= this.dayStart) {
            this.dayStats.put(this.day, this.todays);
            this.totals = this.totals.add(this.todays);
            this.todays = new CampaignStats(0.0, 0.0, 0.0);
            this.day = timeUnit;
            this.budgetlimit = this.tomorrowsBudgetLimit;
            this.tomorrowsBudgetLimit = Double.POSITIVE_INFINITY;
            this.impressionLimit = this.tomorrowsImpressionLimit;
            this.tomorrowsImpressionLimit = Integer.MAX_VALUE;
        }
        if (this.day == this.dayEnd + 1) {
            double effectiveReachRatio = this.effectiveReachRatio(this.totals.tartgetedImps);
            this.qualityManager.updateQualityScore(this.advertiser, effectiveReachRatio);
            AdxManager.getInstance().getSimulation().broadcastAdNetworkRevenue(this.advertiser, effectiveReachRatio * ((double)this.budgetMillis / 1000.0));
            this.log.log(Level.INFO, "Campaign " + this.id + " ended for advertiser " + this.advertiser + ". Stats " + this.totals + " Reach " + this.reachImps + " ERR " + effectiveReachRatio + " Budget " + (double)this.budgetMillis / 1000.0 + " Revenue " + effectiveReachRatio * ((double)this.budgetMillis / 1000.0));
        }
    }

    @Override
    public String getAdvertiser() {
        return this.advertiser;
    }

    @Override
    public void addAdvertiserBid(String advertiser, Long budgetBidMillis) {
        if ((double)budgetBidMillis.longValue() >= 0.1 * (double)this.reachImps.longValue() / this.qualityManager.getQualityScore(advertiser) && (double)budgetBidMillis.longValue() <= 1.0 * (double)this.reachImps.longValue() * this.qualityManager.getQualityScore(advertiser)) {
            this.advertisersBids.put(advertiser, budgetBidMillis);
        }
    }

    @Override
    public Map<String, Long> getBiddingAdvertisers() {
        return this.advertisersBids;
    }

    @Override
    public void allocateToAdvertiser(String advertiser) {
        this.budgetMillis = this.reachImps * DEFAULT_BUDGET_FACTOR;
        this.advertiser = advertiser;
    }

    @Override
    public CampaignAuctionReport auction() {
        CampaignAuctionReport auctionReport = null;
        int advCount = this.advertisersBids.size();
        this.advertiser = "";
        if (advCount > 0) {
            String[] advNames = new String[advCount];
            double[] qualityScores = new double[advCount];
            long[] bids = new long[advCount];
            double[] scores = new double[advCount];
            int[] indices = new int[advCount];
            int i = 0;
            ArrayList<String> advNamesList = new ArrayList<String>(this.advertisersBids.keySet());
            Collections.shuffle(advNamesList);
            for (String advName : advNamesList) {
                advNames[i] = new String(advName);
                bids[i] = this.advertisersBids.get(advName);
                qualityScores[i] = this.qualityManager.getQualityScore(advName);
                scores[i] = qualityScores[i] / (double)bids[i];
                indices[i] = i++;
            }
            AuctionUtils.hardSort(scores, indices);
            if (random.nextDouble() < this.randomAllocPr) {
                int ri = random.nextInt(advCount);
                this.advertiser = advNames[ri];
                this.budgetMillis = bids[ri];
            } else {
                double reserveScore = 1.0 / (1.0 * (double)this.reachImps.longValue());
                if (scores[indices[0]] >= reserveScore) {
                    this.advertiser = advNames[indices[0]];
                    double bsecond = advCount == 1 ? reserveScore : (scores[indices[1]] > reserveScore ? scores[indices[1]] : reserveScore);
                    this.budgetMillis = (long)(qualityScores[indices[0]] / bsecond);
                }
            }
            auctionReport = this.generateAuctionReport(advNames, bids, qualityScores, indices, this.advertiser);
        }
        return auctionReport;
    }

    private CampaignAuctionReport generateAuctionReport(String[] advNames, long[] bids, double[] qualityScores, int[] indices, String winner) {
        CampaignAuctionReport campaignAuctionReport = new CampaignAuctionReport(this.id);
        int i = 0;
        while (i < advNames.length) {
            CampaignAuctionReportKey campaignReportKey = new CampaignAuctionReportKey(advNames[indices[i]]);
            CampaignAuctionReportEntry addReportEntry = campaignAuctionReport.addReportEntry(campaignReportKey);
            addReportEntry.setActualBid(bids[indices[i]]);
            addReportEntry.setEffctiveBid((double)bids[indices[i]] * qualityScores[indices[i]]);
            ++i;
        }
        campaignAuctionReport.setWinner(winner);
        return campaignAuctionReport;
    }

    @Override
    public int getRemainingDays() {
        if (this.day > this.dayEnd) {
            return 0;
        }
        int cday = this.day <= this.dayStart ? this.dayStart : this.day;
        return this.dayEnd - cday + 1;
    }

    @Override
    public boolean isActive() {
        if (this.isAllocated() && this.day <= this.dayEnd && this.day >= this.dayStart) {
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldReport() {
        if (this.isAllocated() && this.day <= this.dayEnd + 1 && this.day >= this.dayStart) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isAllocated() {
        if (this.budgetMillis != 0 && this.advertiser != null) {
            return true;
        }
        return false;
    }

    @Override
    public CampaignStats getStats(int timeUnitFrom, int timeUnitTo) {
        CampaignStats current = timeUnitTo >= this.day ? this.todays : null;
        SortedMap<Integer, CampaignStats> daysRangeStats = this.dayStats.subMap(timeUnitFrom, timeUnitTo + 1);
        return AccumulatorImpl.accumulate(this, new ArrayList<CampaignStats>(daysRangeStats.values()), new CampaignStats(0.0, 0.0, 0.0)).add(current);
    }

    @Subscribe
    public void limitSet(CampaignLimitSet message) {
        if (message.getCampaignId() == this.id && message.getAdNetwork().equals(this.advertiser)) {
            if (message.getIsTotal()) {
                this.totalBudgetlimit = message.getBudgetLimit();
                this.totalImpressionLimit = message.getImpressionLimit();
            } else {
                this.setTomorowsLimit(message);
            }
        }
    }

    @Override
    public CampaignStats getTodayStats() {
        return this.todays;
    }

    @Override
    public CampaignStats accumulate(CampaignStats interim, CampaignStats next) {
        return interim.add(next);
    }

    @Override
    public int getId() {
        return this.id;
    }

    public String toString() {
        return "CampaignImpl [id=" + this.id + ", reachImps=" + this.reachImps + ", dayStart=" + this.dayStart + ", dayEnd=" + this.dayEnd + ", targetSegment=" + this.targetSegments + ", videoCoef=" + this.videoCoef + ", mobileCoef=" + this.mobileCoef + ", advertisersBids=" + this.advertisersBids + ", budgetMillis=" + this.budgetMillis + ", advertiser=" + this.advertiser + ", day=" + this.day + ", todays=" + this.todays + ", totals=" + this.totals + ", dayStats=" + this.dayStats + "]";
    }

    @Override
    public String logToString() {
        return "CampaignImpl [id=" + this.id + ", reachImps=" + this.reachImps + ", dayStart=" + this.dayStart + ", dayEnd=" + this.dayEnd + ", targetSegment=" + this.targetSegments + ", videoCoef=" + this.videoCoef + ", mobileCoef=" + this.mobileCoef + ", budgetMillis=" + this.budgetMillis + ", advertiser=" + this.advertiser + "]";
    }

    @Override
    public void nextTimeUnit(int timeUnit) {
    }

    @Override
    public CampaignStats getTotals() {
        if (this.totals.getTargetedImps() > 1300.0) {
            boolean bl = false;
        }
        return this.totals;
    }

    @Override
    public boolean shouldWarnLimits() {
        if (this.isOverTodaysLimit() || this.isOverTotalLimits()) {
            if (this.overLimitsWarnings % 10 == 0) {
                return true;
            }
            ++this.overLimitsWarnings;
        }
        return false;
    }
}

