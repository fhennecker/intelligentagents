/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.agents;

import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.BankStatus;
import edu.umich.eecs.tac.props.Product;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Agent;
import se.sics.tasim.aw.Message;
import se.sics.tasim.props.SimulationStatus;
import se.sics.tasim.props.StartInfo;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.demand.CampaignStats;
import tau.tac.adx.devices.Device;
import tau.tac.adx.props.AdxBidBundle;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.props.PublisherCatalog;
import tau.tac.adx.props.PublisherCatalogEntry;
import tau.tac.adx.report.adn.AdNetworkReport;
import tau.tac.adx.report.adn.MarketSegment;
import tau.tac.adx.report.demand.AdNetBidMessage;
import tau.tac.adx.report.demand.AdNetworkDailyNotification;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;
import tau.tac.adx.report.demand.CampaignReport;
import tau.tac.adx.report.demand.CampaignReportEntry;
import tau.tac.adx.report.demand.CampaignReportKey;
import tau.tac.adx.report.demand.InitialCampaignMessage;
import tau.tac.adx.report.demand.campaign.auction.CampaignAuctionReport;
import tau.tac.adx.report.publisher.AdxPublisherReport;
import tau.tac.adx.report.publisher.AdxPublisherReportEntry;

public class GreedyLuckyAdNetwork
extends Agent {
    private final Logger log = Logger.getLogger(GreedyLuckyAdNetwork.class.getName());
    private StartInfo startInfo;
    private final Queue<CampaignReport> campaignReports = new LinkedList<CampaignReport>();
    private PublisherCatalog publisherCatalog;
    private InitialCampaignMessage initialCampaignMessage;
    private AdNetworkDailyNotification adNetworkDailyNotification;
    private String demandAgentAddress = null;
    private String adxAgentAddress = null;
    private CampaignData pendingCampaign;
    private Map<Integer, CampaignData> myCampaigns;
    private AdxBidBundle bidBundle = new AdxBidBundle();
    private int day;
    private String[] publisherNames;
    private Double cmpBidMillis;
    private double qualityScore;
    private UcsModel ucsModel;

    public GreedyLuckyAdNetwork() {
        this.ucsModel = new UcsModel();
        this.myCampaigns = new HashMap<Integer, CampaignData>();
        this.cmpBidMillis = new Double(0.0);
    }

    @Override
    protected void messageReceived(Message message) {
        try {
            Transportable content = message.getContent();
            if (content instanceof InitialCampaignMessage) {
                this.handleInitialCampaignMessage((InitialCampaignMessage)content);
            } else if (content instanceof CampaignOpportunityMessage) {
                this.handleICampaignOpportunityMessage((CampaignOpportunityMessage)content);
            } else if (content instanceof CampaignReport) {
                this.handleCampaignReport((CampaignReport)content);
            } else if (content instanceof AdNetworkDailyNotification) {
                this.handleAdNetworkDailyNotification((AdNetworkDailyNotification)content);
            } else if (content instanceof AdxPublisherReport) {
                this.handleAdxPublisherReport((AdxPublisherReport)content);
            } else if (content instanceof SimulationStatus) {
                this.handleSimulationStatus((SimulationStatus)content);
            } else if (content instanceof PublisherCatalog) {
                this.handlePublisherCatalog((PublisherCatalog)content);
            } else if (content instanceof AdNetworkReport) {
                this.handleAdNetworkReport((AdNetworkReport)content);
            } else if (content instanceof StartInfo) {
                this.handleStartInfo((StartInfo)content);
            } else if (content instanceof BankStatus) {
                this.handleBankStatus((BankStatus)content);
            } else if (content instanceof CampaignAuctionReport) {
                this.hadnleCampaignAuctionReport((CampaignAuctionReport)content);
            } else {
                System.out.println("UNKNOWN Message Received: " + content);
            }
        }
        catch (NullPointerException e) {
            this.log.log(Level.SEVERE, "Exception thrown while trying to parse message." + e);
            return;
        }
    }

    private void hadnleCampaignAuctionReport(CampaignAuctionReport content) {
    }

    private void handleBankStatus(BankStatus content) {
        System.out.println("Day " + this.day + " :" + content.toString());
    }

    protected void handleStartInfo(StartInfo startInfo) {
        this.startInfo = startInfo;
    }

    private void handlePublisherCatalog(PublisherCatalog publisherCatalog) {
        this.publisherCatalog = publisherCatalog;
        this.getPublishersNames();
    }

    private void handleInitialCampaignMessage(InitialCampaignMessage campaignMessage) {
        System.out.println(campaignMessage.toString());
        this.day = 0;
        this.initialCampaignMessage = campaignMessage;
        this.demandAgentAddress = campaignMessage.getDemandAgentAddress();
        this.adxAgentAddress = campaignMessage.getAdxAgentAddress();
        CampaignData campaignData = new CampaignData(this, this.initialCampaignMessage);
        campaignData.setBudget((double)this.initialCampaignMessage.getBudgetMillis() / 1000.0);
        this.genCampaignQueries(campaignData);
        System.out.println("Day " + this.day + ": Allocated campaign - " + campaignData);
        this.myCampaigns.put(this.initialCampaignMessage.getId(), campaignData);
    }

    private void handleICampaignOpportunityMessage(CampaignOpportunityMessage com) {
        this.day = com.getDay();
        this.pendingCampaign = new CampaignData(this, com);
        System.out.println("Day " + this.day + ": Campaign opportunity - " + this.pendingCampaign);
        long cmpimps = com.getReachImps();
        this.cmpBidMillis = new Double(cmpimps) * this.qualityScore - 1.0;
        System.out.println("Day " + this.day + ": Campaign total budget bid (millis): " + this.cmpBidMillis.longValue());
    }

    private void handleAdNetworkDailyNotification(AdNetworkDailyNotification notificationMessage) {
        this.adNetworkDailyNotification = notificationMessage;
        System.out.println("Day " + this.day + ": Daily notification for campaign " + this.adNetworkDailyNotification.getCampaignId());
        String campaignAllocatedTo = " allocated to " + notificationMessage.getWinner();
        if (this.pendingCampaign != null && this.pendingCampaign.id == this.adNetworkDailyNotification.getCampaignId() && notificationMessage.getCostMillis() != 0) {
            this.pendingCampaign.setBudget((double)notificationMessage.getCostMillis() / 1000.0);
            this.genCampaignQueries(this.pendingCampaign);
            this.myCampaigns.put(this.pendingCampaign.id, this.pendingCampaign);
            campaignAllocatedTo = " WON at cost (Millis)" + notificationMessage.getCostMillis();
        }
        this.qualityScore = notificationMessage.getQualityScore();
        this.ucsModel.ucsUpdate(notificationMessage.getServiceLevel(), notificationMessage.getPrice(), this.activeCampaigns());
        System.out.println("Day " + this.day + ": " + campaignAllocatedTo + ". UCS Level set to " + notificationMessage.getServiceLevel() + " at price " + notificationMessage.getPrice() + " Quality Score is: " + notificationMessage.getQualityScore());
    }

    private void handleSimulationStatus(SimulationStatus simulationStatus) {
        System.out.println("Day " + this.day + " : Simulation Status Received. Server reporting end of day " + simulationStatus.getCurrentDate() + " consuming " + simulationStatus.getConsumedMillis() + " milliseconds");
        int cm = simulationStatus.getConsumedMillis();
        if (cm > 4200) {
            System.out.println(" XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println(" XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println(" XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println(" XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println(" XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println(" XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println(" XXXXXXXXXXXXXXXXXXXXXXXXXXXXX             XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println(" XXXXXXXXXXXXXXXXXXXXXXXXXXXXX    " + cm + "    XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println(" XXXXXXXXXXXXXXXXXXXXXXXXXXXXX             XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println(" XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println(" XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println(" XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println(" XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println(" XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println(" XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            this.log.log(Level.SEVERE, " XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            this.log.log(Level.SEVERE, "Day " + this.day + " overconsumed: " + cm);
            this.log.log(Level.SEVERE, " XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        }
        System.out.println("Day " + this.day + ": Ucs bid is " + (this.ucsModel != null ? Double.valueOf(this.ucsModel.getBid()) : "...No Model"));
        AdNetBidMessage bids = new AdNetBidMessage(this.ucsModel != null ? this.ucsModel.getBid() : 0.0, this.pendingCampaign != null ? this.pendingCampaign.id : 0, this.cmpBidMillis != null ? this.cmpBidMillis.longValue() : 0);
        if (this.demandAgentAddress != null) {
            this.sendMessage(this.demandAgentAddress, bids);
        }
        this.sendBidAndAds();
        System.out.println("Day " + this.day + " ended. Starting next day");
        ++this.day;
    }

    private boolean activeCampaigns() {
        int dayBiddingFor = this.day + 1;
        for (CampaignData cmpgn : this.myCampaigns.values()) {
            if ((long)dayBiddingFor < cmpgn.dayStart || (long)dayBiddingFor > cmpgn.dayEnd || cmpgn.impsTogo() <= 0) continue;
            return true;
        }
        return false;
    }

    protected void sendBidAndAds() {
        this.bidBundle = new AdxBidBundle();
        int dayBiddingFor = this.day + 1;
        Random random = new Random();
        double rbid = 10.0 * random.nextDouble();
        for (CampaignData campaign : this.myCampaigns.values()) {
            if ((long)dayBiddingFor < campaign.dayStart || (long)dayBiddingFor > campaign.dayEnd || campaign.impsTogo() <= 0) continue;
            AdxQuery[] arradxQuery = campaign.campaignQueries;
            int n = arradxQuery.length;
            int n2 = 0;
            while (n2 < n) {
                AdxQuery query = arradxQuery[n2];
                this.bidBundle.addQuery(query, rbid, new Ad(null), campaign.id, 1);
                ++n2;
            }
            double impressionLimit = campaign.impsTogo();
            double budgetLimit = campaign.budget;
            this.bidBundle.setCampaignDailyLimit(campaign.id, (int)impressionLimit, budgetLimit);
            System.out.println("Day " + this.day + ": Updated Bid Bundle entries " + "for Campaign id " + campaign.id);
        }
        if (this.bidBundle != null) {
            System.out.println("Day " + this.day + ": Sending BidBundle");
            if (this.adxAgentAddress != null) {
                this.sendMessage(this.adxAgentAddress, this.bidBundle);
            }
        }
    }

    private void handleCampaignReport(CampaignReport campaignReport) {
        this.campaignReports.add(campaignReport);
        for (CampaignReportKey campaignKey : campaignReport.keys()) {
            int cmpId = campaignKey.getCampaignId();
            CampaignStats cstats = campaignReport.getCampaignReportEntry(campaignKey).getCampaignStats();
            this.myCampaigns.get(cmpId).setStats(cstats);
            System.out.println("Day " + this.day + ": Updating campaign " + cmpId + " stats: " + cstats.getTargetedImps() + " tgtImps " + cstats.getOtherImps() + " nonTgtImps. Cost of imps is " + cstats.getCost());
        }
    }

    private void handleAdxPublisherReport(AdxPublisherReport adxPublisherReport) {
        System.out.println("Publishers Report: ");
        for (PublisherCatalogEntry publisherKey : adxPublisherReport.keys()) {
            AdxPublisherReportEntry entry = (AdxPublisherReportEntry)adxPublisherReport.getEntry(publisherKey);
            System.out.println(entry.toString());
        }
    }

    private void handleAdNetworkReport(AdNetworkReport adnetReport) {
        System.out.println("Day " + this.day + " : AdNetworkReport");
    }

    @Override
    protected void simulationSetup() {
        this.day = 0;
        this.bidBundle = new AdxBidBundle();
        this.qualityScore = 1.0;
        this.ucsModel = new UcsModel();
        this.myCampaigns = new HashMap<Integer, CampaignData>();
        this.log.fine("AdNet " + this.getName() + " simulationSetup");
    }

    @Override
    protected void simulationFinished() {
        this.campaignReports.clear();
        this.bidBundle = null;
    }

    private void getPublishersNames() {
        if (this.publisherNames == null && this.publisherCatalog != null) {
            ArrayList<String> names = new ArrayList<String>();
            for (PublisherCatalogEntry pce : this.publisherCatalog) {
                names.add(pce.getPublisherName());
            }
            this.publisherNames = new String[names.size()];
            names.toArray(this.publisherNames);
        }
    }

    private void genCampaignQueries(CampaignData campaignData) {
        HashSet<AdxQuery> campaignQueriesSet = new HashSet<AdxQuery>();
        String[] arrstring = this.publisherNames;
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String PublisherName = arrstring[n2];
            campaignQueriesSet.add(new AdxQuery(PublisherName, campaignData.targetSegment, Device.mobile, AdType.text));
            campaignQueriesSet.add(new AdxQuery(PublisherName, campaignData.targetSegment, Device.mobile, AdType.video));
            campaignQueriesSet.add(new AdxQuery(PublisherName, campaignData.targetSegment, Device.pc, AdType.text));
            campaignQueriesSet.add(new AdxQuery(PublisherName, campaignData.targetSegment, Device.pc, AdType.video));
            ++n2;
        }
        CampaignData.access$1(campaignData, new AdxQuery[campaignQueriesSet.size()]);
        campaignQueriesSet.toArray(campaignData.campaignQueries);
    }

    private class CampaignData {
        Long reachImps;
        long dayStart;
        long dayEnd;
        Set<MarketSegment> targetSegment;
        double videoCoef;
        double mobileCoef;
        int id;
        private AdxQuery[] campaignQueries;
        CampaignStats stats;
        double budget;
        final /* synthetic */ GreedyLuckyAdNetwork this$0;

        public CampaignData(GreedyLuckyAdNetwork greedyLuckyAdNetwork, InitialCampaignMessage icm) {
            this.this$0 = greedyLuckyAdNetwork;
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

        public CampaignData(GreedyLuckyAdNetwork greedyLuckyAdNetwork, CampaignOpportunityMessage com) {
            this.this$0 = greedyLuckyAdNetwork;
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

        public AdxQuery[] getCampaignQueries() {
            return this.campaignQueries;
        }

        public void setCampaignQueries(AdxQuery[] campaignQueries) {
            this.campaignQueries = campaignQueries;
        }

        static /* synthetic */ void access$1(CampaignData campaignData, AdxQuery[] arradxQuery) {
            campaignData.campaignQueries = arradxQuery;
        }
    }

    private class UcsModel {
        private Random random;
        private double ucsBid;
        private double ucsBidPercentile;
        private double ucsLearningRate;
        private double ucsAlpha;
        private double ucsBeta;
        private double ucsLevel;
        private double ucsCost;
        private double ucsGamma;
        private double ucsDelta;

        public UcsModel() {
            this.ucsLearningRate = 0.3;
            this.random = new Random();
            this.ucsLevel = 1.0;
            this.ucsCost = 0.0;
            this.ucsBidPercentile = 0.8;
            this.ucsAlpha = -10.0;
            this.ucsBeta = 10.0;
            this.ucsGamma = 0.0;
            this.ucsDelta = 1.0;
            this.ucsBid = this.ucsBidByPercentile();
        }

        private double ucsFactor(double percentile) {
            return Math.log(1.0 / percentile - 1.0);
        }

        private double ucsBidByPercentile() {
            return (this.ucsFactor(this.ucsBidPercentile) - this.ucsAlpha) / this.ucsBeta;
        }

        public double getBid() {
            return this.ucsBid;
        }

        public double getCost(double level) {
            return this.ucsGamma + this.ucsDelta * level;
        }

        public void ucsUpdate(double level, double cost, boolean bidHigh) {
            double yk = level == 1.0 ? 1.0 : 0.0;
            this.ucsAlpha += this.ucsLearningRate * (yk - this.ucsBidPercentile);
            this.ucsBeta += this.ucsLearningRate * (yk - this.ucsBidPercentile) * this.ucsBid;
            this.ucsBidPercentile = bidHigh ? 0.9 : 0.9 * this.random.nextDouble();
            this.ucsBid = this.ucsBidByPercentile();
            this.ucsGamma += this.ucsLearningRate * (cost - (this.ucsGamma + this.ucsDelta * level));
            this.ucsDelta += this.ucsLearningRate * (cost - (this.ucsGamma + this.ucsDelta * level)) * level;
        }
    }

}

