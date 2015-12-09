/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.agents;

import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.BankStatus;
import edu.umich.eecs.tac.props.Product;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
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

public class SampleAdNetwork
extends Agent {
    private final Logger log = Logger.getLogger(SampleAdNetwork.class.getName());
    private StartInfo startInfo;
    private final Queue<CampaignReport> campaignReports = new LinkedList<CampaignReport>();
    private PublisherCatalog publisherCatalog;
    private InitialCampaignMessage initialCampaignMessage;
    private AdNetworkDailyNotification adNetworkDailyNotification;
    private String demandAgentAddress;
    private String adxAgentAddress;
    private AdxQuery[] queries;
    private CampaignData pendingCampaign;
    private Map<Integer, CampaignData> myCampaigns;
    private AdxBidBundle bidBundle;
    double ucsBid;
    double ucsTargetLevel;
    private int day;
    private String[] publisherNames;
    private CampaignData currCampaign;

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
        this.generateAdxQuerySpace();
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
        this.currCampaign = campaignData;
        this.genCampaignQueries(this.currCampaign);
        System.out.println("Day " + this.day + ": Allocated campaign - " + campaignData);
        this.myCampaigns.put(this.initialCampaignMessage.getId(), campaignData);
    }

    private void handleICampaignOpportunityMessage(CampaignOpportunityMessage com) {
        this.day = com.getDay();
        this.pendingCampaign = new CampaignData(this, com);
        System.out.println("Day " + this.day + ": Campaign opportunity - " + this.pendingCampaign);
        Random random = new Random();
        long cmpimps = com.getReachImps();
        long cmpBidMillis = random.nextInt((int)cmpimps);
        System.out.println("Day " + this.day + ": Campaign total budget bid (millis): " + cmpBidMillis);
        if (this.adNetworkDailyNotification != null) {
            double ucsLevel = this.adNetworkDailyNotification.getServiceLevel();
            this.ucsBid = 0.1 + random.nextDouble() / 10.0;
            System.out.println("Day " + this.day + ": ucs level reported: " + ucsLevel);
        } else {
            System.out.println("Day " + this.day + ": Initial ucs bid is " + this.ucsBid);
        }
        AdNetBidMessage bids = new AdNetBidMessage(this.ucsBid, this.pendingCampaign.id, cmpBidMillis);
        this.sendMessage(this.demandAgentAddress, bids);
    }

    private void handleAdNetworkDailyNotification(AdNetworkDailyNotification notificationMessage) {
        this.adNetworkDailyNotification = notificationMessage;
        System.out.println("Day " + this.day + ": Daily notification for campaign " + this.adNetworkDailyNotification.getCampaignId());
        String campaignAllocatedTo = " allocated to " + notificationMessage.getWinner();
        if (this.pendingCampaign.id == this.adNetworkDailyNotification.getCampaignId() && notificationMessage.getCostMillis() != 0) {
            this.pendingCampaign.setBudget((double)notificationMessage.getCostMillis() / 1000.0);
            this.currCampaign = this.pendingCampaign;
            this.genCampaignQueries(this.currCampaign);
            this.myCampaigns.put(this.pendingCampaign.id, this.pendingCampaign);
            campaignAllocatedTo = " WON at cost (Millis)" + notificationMessage.getCostMillis();
        }
        System.out.println("Day " + this.day + ": " + campaignAllocatedTo + ". UCS Level set to " + notificationMessage.getServiceLevel() + " at price " + notificationMessage.getPrice() + " Quality Score is: " + notificationMessage.getQualityScore());
    }

    private void handleSimulationStatus(SimulationStatus simulationStatus) {
        System.out.println("Day " + this.day + " : Simulation Status Received");
        this.sendBidAndAds();
        System.out.println("Day " + this.day + " ended. Starting next day");
        ++this.day;
    }

    protected void sendBidAndAds() {
        this.bidBundle = new AdxBidBundle();
        int dayBiddingFor = this.day + 1;
        Random random = new Random();
        double rbid = 10.0 * random.nextDouble();
        if ((long)dayBiddingFor >= this.currCampaign.dayStart && (long)dayBiddingFor <= this.currCampaign.dayEnd && this.currCampaign.impsTogo() > 0) {
            int entCount = 0;
            AdxQuery[] arradxQuery = this.currCampaign.campaignQueries;
            int n = arradxQuery.length;
            int n2 = 0;
            while (n2 < n) {
                AdxQuery query = arradxQuery[n2];
                if (this.currCampaign.impsTogo() - entCount > 0) {
                    entCount = query.getDevice() == Device.pc ? (query.getAdType() == AdType.text ? ++entCount : (int)((double)entCount + this.currCampaign.videoCoef)) : (query.getAdType() == AdType.text ? (int)((double)entCount + this.currCampaign.mobileCoef) : (int)((double)entCount + (this.currCampaign.videoCoef + this.currCampaign.mobileCoef)));
                    this.bidBundle.addQuery(query, rbid, new Ad(null), this.currCampaign.id, 1);
                }
                ++n2;
            }
            double impressionLimit = this.currCampaign.impsTogo();
            double budgetLimit = this.currCampaign.budget;
            this.bidBundle.setCampaignDailyLimit(this.currCampaign.id, (int)impressionLimit, budgetLimit);
            System.out.println("Day " + this.day + ": Updated " + entCount + " Bid Bundle entries for Campaign id " + this.currCampaign.id);
        }
        if (this.bidBundle != null) {
            System.out.println("Day " + this.day + ": Sending BidBundle");
            this.sendMessage(this.adxAgentAddress, this.bidBundle);
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
        Random random = new Random();
        this.day = 0;
        this.bidBundle = new AdxBidBundle();
        this.ucsBid = 0.1 + random.nextDouble() / 10.0;
        this.myCampaigns = new HashMap<Integer, CampaignData>();
        this.log.fine("AdNet " + this.getName() + " simulationSetup");
    }

    @Override
    protected void simulationFinished() {
        this.campaignReports.clear();
        this.bidBundle = null;
    }

    private void generateAdxQuerySpace() {
        if (this.publisherCatalog != null && this.queries == null) {
            HashSet<AdxQuery> querySet = new HashSet<AdxQuery>();
            for (PublisherCatalogEntry publisherCatalogEntry : this.publisherCatalog) {
                String publishersName = publisherCatalogEntry.getPublisherName();
                MarketSegment[] arrmarketSegment = MarketSegment.values();
                int n = arrmarketSegment.length;
                int n2 = 0;
                while (n2 < n) {
                    MarketSegment userSegment = arrmarketSegment[n2];
                    HashSet<MarketSegment> singleMarketSegment = new HashSet<MarketSegment>();
                    singleMarketSegment.add(userSegment);
                    querySet.add(new AdxQuery(publishersName, singleMarketSegment, Device.mobile, AdType.text));
                    querySet.add(new AdxQuery(publishersName, singleMarketSegment, Device.pc, AdType.text));
                    querySet.add(new AdxQuery(publishersName, singleMarketSegment, Device.mobile, AdType.video));
                    querySet.add(new AdxQuery(publishersName, singleMarketSegment, Device.pc, AdType.video));
                    ++n2;
                }
                querySet.add(new AdxQuery(publishersName, new HashSet<MarketSegment>(), Device.mobile, AdType.video));
                querySet.add(new AdxQuery(publishersName, new HashSet<MarketSegment>(), Device.mobile, AdType.text));
                querySet.add(new AdxQuery(publishersName, new HashSet<MarketSegment>(), Device.pc, AdType.video));
                querySet.add(new AdxQuery(publishersName, new HashSet<MarketSegment>(), Device.pc, AdType.text));
            }
            this.queries = new AdxQuery[querySet.size()];
            querySet.toArray(this.queries);
        }
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
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!" + Arrays.toString(campaignData.campaignQueries) + "!!!!!!!!!!!!!!!!");
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
        final /* synthetic */ SampleAdNetwork this$0;

        public CampaignData(SampleAdNetwork sampleAdNetwork, InitialCampaignMessage icm) {
            this.this$0 = sampleAdNetwork;
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

        public CampaignData(SampleAdNetwork sampleAdNetwork, CampaignOpportunityMessage com) {
            this.this$0 = sampleAdNetwork;
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
            return "Campaign ID " + this.id + ": " + "day " + this.dayStart + " to " + this.dayEnd + " " + this.targetSegment + ", reach: " + this.reachImps + " coefs: (v=" + this.videoCoef + ", m=" + this.mobileCoef + ")";
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

}

