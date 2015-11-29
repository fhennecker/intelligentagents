/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.ListMultimap
 *  com.google.common.eventbus.EventBus
 *  com.google.common.eventbus.Subscribe
 */
package tau.tac.adx.agents;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.util.Collection;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.isl.transport.Transportable;
import se.sics.isl.util.ConfigManager;
import se.sics.tasim.aw.Message;
import tau.tac.adx.AdxManager;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.auction.AdxAuctionResult;
import tau.tac.adx.auction.data.AuctionState;
import tau.tac.adx.demand.Campaign;
import tau.tac.adx.demand.CampaignImpl;
import tau.tac.adx.demand.CampaignStats;
import tau.tac.adx.demand.QualityManager;
import tau.tac.adx.demand.QualityManagerImpl;
import tau.tac.adx.demand.UserClassificationService;
import tau.tac.adx.demand.UserClassificationServiceAdNetData;
import tau.tac.adx.demand.UserClassificationServiceImpl;
import tau.tac.adx.devices.Device;
import tau.tac.adx.messages.AuctionMessage;
import tau.tac.adx.messages.CampaignLimitReached;
import tau.tac.adx.messages.CampaignNotification;
import tau.tac.adx.messages.UserClassificationServiceNotification;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.report.adn.MarketSegment;
import tau.tac.adx.report.demand.AdNetBidMessage;
import tau.tac.adx.report.demand.AdNetworkDailyNotification;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;
import tau.tac.adx.report.demand.CampaignReport;
import tau.tac.adx.report.demand.InitialCampaignMessage;
import tau.tac.adx.report.demand.campaign.auction.CampaignAuctionReport;
import tau.tac.adx.sim.Builtin;
import tau.tac.adx.sim.TACAdxSimulation;
import tau.tac.adx.users.AdxUser;

public class DemandAgent
extends Builtin {
    private int day;
    private static final int TOTAL_POPULATION_DEFAULT = 10000;
    private static int total_population;
    private static final double CMP_VC_DEFAULT = 2.0;
    private static double cmp_vc;
    private static final double CMP_MC_DEFAULT = 1.5;
    private static double cmp_mc;
    private static final double CMP_VC_OFFSET_DEFAULT = 2.0;
    private static double cmp_vc_offset;
    private static final double CMP_MC_OFFSET_DEFAULT = 1.5;
    private static double cmp_mc_offset;
    private static Random random;
    private static int[] CMP_LENGTHS_DEFAULT;
    private static int CMP_LENGTHS_COUNT_DEFAULT;
    private static int[] cmp_lengths;
    private static int cmp_lengths_count;
    private static Double[] CMP_REACHLEVELS_DEFAULT;
    private static int CMP_REACHLEVELS_COUNT_DEFAULT;
    private static Double[] cmp_reachlevels;
    private static int cmp_reachlevels_count;
    private Logger log;
    private QualityManager qualityManager;
    private ListMultimap<String, Campaign> adNetCampaigns;
    private Campaign pendingCampaign;
    private UserClassificationService ucs;
    private boolean bidsAllowed = false;

    static {
        CMP_LENGTHS_DEFAULT = new int[]{3, 5, 10};
        CMP_LENGTHS_COUNT_DEFAULT = 3;
        CMP_REACHLEVELS_DEFAULT = new Double[]{0.2, 0.5, 0.8};
        CMP_REACHLEVELS_COUNT_DEFAULT = 3;
    }

    public DemandAgent() {
        super("demand");
    }

    public void preNextTimeUnit(int date) {
        this.bidsAllowed = false;
        this.day = date;
        if (this.day == 0) {
            this.zeroDayInitialization();
        } else {
            this.auctionTomorrowsCampaign(this.day);
            this.ucs.auction(this.day, true);
            for (Campaign campaign : this.adNetCampaigns.values()) {
                campaign.preNextTimeUnit(date);
            }
            this.reportAuctionResutls(this.day);
        }
        this.getSimulation().getEventBus().post((Object)new UserClassificationServiceNotification(this.ucs));
        this.createAndPublishTomorrowsPendingCampaign();
        this.bidsAllowed = true;
    }

    private void createAndPublishTomorrowsPendingCampaign() {
        int cmplength = cmp_lengths[random.nextInt(cmp_lengths_count)];
        int lastCmpDay = this.day + 1 + cmplength;
        if (lastCmpDay < 60) {
            Set<MarketSegment> target = MarketSegment.randomMarketSegment();
            int reach = (int)(cmp_reachlevels[random.nextInt(cmp_reachlevels_count)] * (double)MarketSegment.marketSegmentSize(target).intValue() * (double)cmplength);
            this.pendingCampaign = new CampaignImpl(this.qualityManager, reach, this.day + 2, lastCmpDay, target, cmp_vc + cmp_vc_offset * random.nextDouble(), cmp_mc + cmp_mc_offset * random.nextDouble());
            this.pendingCampaign.registerToEventBus();
            this.log.log(Level.INFO, "Day " + this.day + " :" + "Notifying new campaign opportunity: " + this.pendingCampaign.logToString());
            this.getSimulation().sendCampaignOpportunity(new CampaignOpportunityMessage(this.pendingCampaign, this.day));
        } else {
            this.log.log(Level.INFO, "Day " + this.day + " :" + " A campaign was not published today because its randomized length was too long");
            this.pendingCampaign = null;
        }
    }

    private void reportAuctionResutls(int date) {
        this.log.log(Level.FINE, "Day " + this.day + " :" + "Quality Ratings... " + this.qualityManager.logToString());
        this.log.log(Level.FINE, "Day " + this.day + " :" + "Reporting campaign auction results... " + (this.pendingCampaign != null ? this.pendingCampaign.logToString() : "No pending campaign"));
        this.log.log(Level.FINE, "Day " + this.day + " :" + "Reporting UCS auction results... " + this.ucs.logToString());
        String[] arrstring = this.getAdxAdvertiserAddresses();
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String advertiser = arrstring[n2];
            if (date >= 2) {
                CampaignReport report = new CampaignReport();
                for (Campaign campaign : this.adNetCampaigns.values()) {
                    if (!campaign.isAllocated() || campaign.getDayStart() >= date || !advertiser.equals(campaign.getAdvertiser()) || !campaign.shouldReport()) continue;
                    report.addStatsEntry(campaign.getId(), campaign.getTotals());
                }
                this.getSimulation().sendCampaignReport(advertiser, report);
            }
            AdNetworkDailyNotification adNetworkNotification = new AdNetworkDailyNotification(this.ucs.getTomorrowsAdNetData(advertiser), this.pendingCampaign, this.qualityManager.getQualityScore(advertiser));
            if (this.pendingCampaign != null && !advertiser.equals(this.pendingCampaign.getAdvertiser())) {
                adNetworkNotification.zeroCost();
            }
            this.getSimulation().sendDemandDailyNotification(advertiser, adNetworkNotification);
            ++n2;
        }
    }

    private void auctionTomorrowsCampaign(int date) {
        if (this.pendingCampaign != null) {
            this.log.log(Level.INFO, "Day " + this.day + " : Auction pending campaign: " + this.pendingCampaign.logToString());
            CampaignAuctionReport campaignAuctionReport = this.pendingCampaign.auction();
            if (campaignAuctionReport != null) {
                this.getSimulation().sendCampaignAuctionReport(campaignAuctionReport);
            }
            if (this.pendingCampaign.isAllocated()) {
                this.adNetCampaigns.put((Object)this.pendingCampaign.getAdvertiser(), (Object)this.pendingCampaign);
                this.getSimulation().getEventBus().post((Object)new CampaignNotification(this.pendingCampaign));
            } else {
                this.log.log(Level.INFO, "Day " + this.day + " : Campaign auction: Not allocated");
            }
        } else {
            this.log.log(Level.INFO, "Day " + this.day + " : No pending campaign to auction");
        }
    }

    @Override
    protected void setup() {
        int numOfCompetitors = this.getSimulation().getNumberOfAdvertisers();
        random = new Random();
        total_population = this.getSimulation().getConfig().getPropertyAsInt("adxusers.population_size", 10000);
        cmp_vc = this.getSimulation().getConfig().getPropertyAsDouble("campaigns.video_coef", 2.0);
        cmp_mc = this.getSimulation().getConfig().getPropertyAsDouble("campaigns.mobile_coef", 1.5);
        cmp_vc_offset = this.getSimulation().getConfig().getPropertyAsDouble("campaigns.video_coef_offset", 2.0);
        cmp_mc_offset = this.getSimulation().getConfig().getPropertyAsDouble("campaigns.mobile_coef_offset", 1.5);
        String[] cmp_lengths_str = this.getSimulation().getConfig().getPropertyAsArray("campaigns.lengths");
        if (cmp_lengths_str == null) {
            cmp_lengths_count = CMP_LENGTHS_COUNT_DEFAULT;
            cmp_lengths = CMP_LENGTHS_DEFAULT;
        } else {
            cmp_lengths_count = cmp_lengths_str.length;
            cmp_lengths = new int[cmp_lengths_count];
            int i = 0;
            while (i < cmp_lengths_count) {
                DemandAgent.cmp_lengths[i] = Integer.parseInt(cmp_lengths_str[i]);
                ++i;
            }
        }
        String[] cmp_reachlevels_str = this.getSimulation().getConfig().getPropertyAsArray("campaigns.reachlevels");
        if (cmp_reachlevels_str == null) {
            cmp_reachlevels_count = CMP_REACHLEVELS_COUNT_DEFAULT;
            cmp_reachlevels = CMP_REACHLEVELS_DEFAULT;
        } else {
            cmp_reachlevels_count = cmp_reachlevels_str.length;
            cmp_reachlevels = new Double[cmp_reachlevels_count];
            int i = 0;
            while (i < cmp_reachlevels_count) {
                DemandAgent.cmp_reachlevels[i] = Double.parseDouble(cmp_reachlevels_str[i]);
                ++i;
            }
        }
        this.log = Logger.getLogger(DemandAgent.class.getName());
        this.log.info("setting up...");
        this.getSimulation().getEventBus().register((Object)this);
        this.adNetCampaigns = ArrayListMultimap.create();
        this.qualityManager = new QualityManagerImpl();
        this.ucs = new UserClassificationServiceImpl();
        AdxManager.getInstance().setUserClassificationService(this.ucs);
        this.log.fine("Finished setup");
    }

    private void zeroDayInitialization() {
        String[] arrstring = this.getAdxAdvertiserAddresses();
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String advertiser = arrstring[n2];
            int cmplength = cmp_lengths[1];
            Set<MarketSegment> target = MarketSegment.randomMarketSegment2();
            int reach = (int)(cmp_reachlevels[1] * (double)MarketSegment.marketSegmentSize(target).intValue() * (double)cmplength);
            this.qualityManager.addAdvertiser(advertiser);
            this.qualityManager.updateQualityScore(advertiser, 1.0);
            CampaignImpl campaign = new CampaignImpl(this.qualityManager, reach, 1, cmplength, target, cmp_vc + cmp_vc_offset * random.nextDouble(), cmp_mc + cmp_mc_offset * random.nextDouble());
            campaign.allocateToAdvertiser(advertiser);
            this.log.log(Level.FINE, "Allocating initial campaign : " + campaign.logToString());
            campaign.registerToEventBus();
            this.adNetCampaigns.put((Object)advertiser, (Object)campaign);
            this.getSimulation().sendInitialCampaign(advertiser, new InitialCampaignMessage(campaign, this.getAddress(), AdxManager.getInstance().getAdxAgentAddress()));
            this.getSimulation().getEventBus().post((Object)new CampaignNotification(campaign));
            this.ucs.updateAdvertiserBid(advertiser, 0.0, 0);
            this.getSimulation().getEventBus().post((Object)new UserClassificationServiceNotification(this.ucs));
            ++n2;
        }
    }

    @Override
    protected void stopped() {
    }

    @Override
    protected void shutdown() {
    }

    @Override
    protected void messageReceived(Message message) {
        String sender = message.getSender();
        Transportable content = message.getContent();
        if (content instanceof AdNetBidMessage) {
            AdNetBidMessage cbm = (AdNetBidMessage)content;
            this.log.log(Level.FINE, "Day " + this.day + " :" + "Got AdNetBidMessage from " + sender + " :" + " UCS Bid: " + cbm.getUcsBid() + " Cmp ID: " + cbm.getCampaignId() + " Cmp Bid: " + cbm.getCampaignBudget());
            if (cbm.getUcsBid() < 0.0) {
                this.log.log(Level.WARNING, "Day " + this.day + " :" + "UCS bid was negative and will be ignored");
                return;
            }
            if (!this.bidsAllowed) {
                this.log.log(Level.SEVERE, "Day " + this.day + " :" + "AdNetBid out of sync, while not allowed - ignoring");
                return;
            }
            if (this.pendingCampaign != null && this.pendingCampaign.getId() == cbm.getCampaignId()) {
                this.pendingCampaign.addAdvertiserBid(sender, cbm.getCampaignBudget());
            }
            this.ucs.updateAdvertiserBid(sender, cbm.getUcsBid(), this.day);
        }
    }

    @Subscribe
    public synchronized void impressed(AuctionMessage message) {
        Campaign cmpn = message.getAuctionResult().getCampaign();
        AuctionState auctionState = message.getAuctionResult().getAuctionState();
        if (auctionState == AuctionState.AUCTION_COPMLETED) {
            cmpn.impress(message.getUser(), message.getQuery().getAdType(), message.getQuery().getDevice(), message.getAuctionResult().getWinningPrice());
            if (cmpn.shouldWarnLimits()) {
                this.getSimulation().getEventBus().post((Object)new CampaignLimitReached(cmpn.getId(), cmpn.getAdvertiser()));
                this.log.log(Level.WARNING, "Day " + this.day + " :Campaign limit expired Impressed while over limit: " + cmpn.getId() + ", daily limit was: " + cmpn.getImpressionLimit() + ", " + cmpn.getBudgetlimit() + " values are: " + cmpn.getTodayStats().getTargetedImps() + ", " + cmpn.getTodayStats().getCost() + ", total limit was: " + cmpn.getTotalImpressionLimit() + ", " + cmpn.getTotalBudgetlimit() + " total values are: " + cmpn.getTotals().getTargetedImps() + cmpn.getTodayStats().getTargetedImps() + ", " + cmpn.getTotals().getCost() + cmpn.getTodayStats().getCost());
            }
        }
    }

    @Override
    public void nextTimeUnit(int timeUnit) {
    }
}

