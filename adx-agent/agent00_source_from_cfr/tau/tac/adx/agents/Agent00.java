/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.agents;

import adx.logging.AdNetworkReportFormatter;
import adx.logging.AdNetworkReportSorted;
import adx.logging.BidBundleFormatter;
import adx.query.IQuerySelectAgg;
import adx.query.IQuerySelectAggStar;
import adx.query.IQuerySelectStar;
import adx.query.Query;
import adx.stats.CampaignSegmentTracker;
import adx.stats.CampaignStrategy;
import adx.stats.LoadBalancer;
import adx.stats.Neuro;
import adx.stats.Users;
import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.BankStatus;
import edu.umich.eecs.tac.props.Product;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Agent;
import se.sics.tasim.aw.Message;
import se.sics.tasim.props.SimulationStatus;
import se.sics.tasim.props.StartInfo;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.agents.CampaignData;
import tau.tac.adx.demand.CampaignStats;
import tau.tac.adx.devices.Device;
import tau.tac.adx.props.AdxBidBundle;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.props.PublisherCatalog;
import tau.tac.adx.props.PublisherCatalogEntry;
import tau.tac.adx.report.adn.AdNetworkKey;
import tau.tac.adx.report.adn.AdNetworkReport;
import tau.tac.adx.report.adn.AdNetworkReportEntry;
import tau.tac.adx.report.adn.MarketSegment;
import tau.tac.adx.report.demand.AdNetBidMessage;
import tau.tac.adx.report.demand.AdNetworkDailyNotification;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;
import tau.tac.adx.report.demand.CampaignReport;
import tau.tac.adx.report.demand.CampaignReportEntry;
import tau.tac.adx.report.demand.CampaignReportKey;
import tau.tac.adx.report.demand.InitialCampaignMessage;
import tau.tac.adx.report.publisher.AdxPublisherReport;
import tau.tac.adx.report.publisher.AdxPublisherReportEntry;
import tau.tac.adx.users.AdxUser;
import tau.tac.adx.users.properties.Age;
import tau.tac.adx.users.properties.Gender;
import tau.tac.adx.users.properties.Income;

public class Agent00
extends Agent {
    private final Logger log = Logger.getLogger(Agent00.class.getName());
    private AdNetworkDailyNotification adNetworkDailyNotification;
    private String demandAgentAddress;
    private String adxAgentAddress;
    private CampaignData pendingCampaign;
    private Map<Integer, CampaignData> myCampaigns = new TreeMap<Integer, CampaignData>();
    double ucsBid;
    double ucsBidMin;
    double ucsBidMax;
    double ucsLevel;
    private int day;
    private boolean wonLastCampagin;
    private double factor;
    private double qualityRating;
    private Map<Integer, Mutable<Double>> campaignEstTargetImps = new TreeMap<Integer, Mutable<Double>>();

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
            } else {
                this.log.info("UNKNOWN Message Received: " + content);
            }
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            this.log.log(Level.SEVERE, "Exception thrown while trying to parse message." + e);
            return;
        }
    }

    private void handleBankStatus(BankStatus content) {
        this.log.info("Day " + this.day + ":" + content.toString());
    }

    protected void handleStartInfo(StartInfo startInfo) {
    }

    private void handlePublisherCatalog(PublisherCatalog publisherCatalog) {
        HashSet<String> publishers = new HashSet<String>();
        for (PublisherCatalogEntry entry : publisherCatalog) {
            publishers.add(entry.getPublisherName());
        }
        Users.getInstance().initialize(publishers);
        LoadBalancer.getInstance().initialize();
        Neuro.getInstance().initialize(publishers);
    }

    private void handleInitialCampaignMessage(InitialCampaignMessage campaignMessage) {
        this.log.info(campaignMessage.toString());
        this.day = 0;
        this.demandAgentAddress = campaignMessage.getDemandAgentAddress();
        this.adxAgentAddress = campaignMessage.getAdxAgentAddress();
        CampaignData campaignData = new CampaignData(campaignMessage);
        campaignData.setBudget((double)campaignMessage.getReachImps().longValue() / 1000.0 * this.factor);
        this.log.info("Day " + this.day + ": Allocated campaign - " + campaignData);
        this.myCampaigns.put(campaignMessage.getId(), campaignData);
        this.campaignEstTargetImps.put(campaignMessage.getId(), new Mutable<Double>(0.0));
        LoadBalancer.getInstance().put(campaignData, (long)(campaignData.getBudget() * 1000.0), "self");
    }

    private void handleICampaignOpportunityMessage(CampaignOpportunityMessage com) {
        this.day = com.getDay();
        this.pendingCampaign = new CampaignData(com);
        this.log.info("Day " + this.day + ": Campaign opportunity - " + this.pendingCampaign);
        this.log.info("previous factor=" + this.factor);
        this.factor = this.wonLastCampagin ? (this.factor += (this.factor - 0.1) / 8.0) : (this.factor -= (this.factor - 0.1) / 4.0);
        this.log.info("current factor=" + this.factor);
        CampaignStrategy cs = new CampaignStrategy(this.day, this.myCampaigns, this.pendingCampaign, this.factor, this.qualityRating);
        Object[] offer = cs.makeBidOffer();
        long cmpBid = (Long)offer[0];
        this.factor = (Double)offer[1];
        double cmpBidUnits = (double)cmpBid / 1000.0;
        this.log.info("Day " + this.day + ": Campaign total budget bid: " + cmpBidUnits);
        if (this.adNetworkDailyNotification != null) {
            double prevUcsLevel = this.ucsLevel;
            double prevUcsBid = this.ucsBid;
            this.ucsLevel = this.adNetworkDailyNotification.getServiceLevel();
            if (this.ucsLevel >= 0.8) {
                this.ucsBidMax -= (this.ucsBidMax - this.ucsBid) / 4.0;
                this.ucsBid -= (this.ucsBid - this.ucsBidMin) / 2.0;
            } else {
                this.ucsBidMin += (this.ucsBid - this.ucsBidMin) / 4.0;
                this.ucsBid += (this.ucsBidMax - this.ucsBid) / 2.0;
                if (prevUcsLevel == this.ucsLevel && this.ucsLevel < 0.7) {
                    this.ucsBidMax = Math.min(2.0, this.ucsBidMax * 1.1);
                }
            }
            this.ucsBid = 0.1;
            this.log.info("Day " + this.day + ": Adjusting ucs bid: was " + prevUcsBid + " level reported: " + this.ucsLevel + " target: " + 1.0 + " adjusted: " + this.ucsBid);
        } else {
            this.log.info("Day " + this.day + ": Initial ucs bid is " + this.ucsBid);
        }
        AdNetBidMessage bids = new AdNetBidMessage(this.ucsBid, this.pendingCampaign.id, cmpBid);
        this.sendMessage(this.demandAgentAddress, bids);
    }

    private void handleAdNetworkDailyNotification(AdNetworkDailyNotification notificationMessage) {
        this.adNetworkDailyNotification = notificationMessage;
        this.log.info("Day " + this.day + ": Daily notification for campaign " + this.adNetworkDailyNotification.getCampaignId());
        String campaignAllocatedTo = " allocated to " + notificationMessage.getWinner();
        String winner = notificationMessage.getWinner();
        this.wonLastCampagin = false;
        long reasonableBudget = (long)((double)this.pendingCampaign.getReachImps().longValue() * this.factor * Math.max(this.qualityRating, 1.0) * 1.1);
        if (this.pendingCampaign.id == this.adNetworkDailyNotification.getCampaignId() && notificationMessage.getCostMillis() != 0) {
            if ((double)(notificationMessage.getCostMillis() / this.pendingCampaign.getReachImps()) > 0.5) {
                reasonableBudget = (long)((double)reasonableBudget * 1.5);
            }
            this.pendingCampaign.setBudget((double)notificationMessage.getCostMillis() / 1000.0);
            this.myCampaigns.put(this.pendingCampaign.id, this.pendingCampaign);
            this.campaignEstTargetImps.put(this.pendingCampaign.id, new Mutable<Double>(0.0));
            campaignAllocatedTo = " WON at cost " + notificationMessage.getCostMillis();
            winner = "self";
            this.wonLastCampagin = true;
        }
        LoadBalancer.getInstance().put(this.pendingCampaign, reasonableBudget, winner);
        this.log.info("Day " + this.day + ": " + campaignAllocatedTo + ". UCS Level set to " + notificationMessage.getServiceLevel() + " at price " + notificationMessage.getPrice() + " Quality Score is: " + notificationMessage.getQualityScore());
        this.qualityRating = notificationMessage.getQualityScore();
    }

    private void handleSimulationStatus(SimulationStatus simulationStatus) {
        this.log.info("Day " + this.day + " : Simulation Status Received");
        this.sendBidAndAds();
        this.log.info("Day " + this.day + " ended. Starting next day");
        ++this.day;
        LoadBalancer.getInstance().advanceDay();
    }

    protected void sendBidAndAds() {
        AdxBidBundle bidBundle = new AdxBidBundle();
        BidBundleFormatter formatter = new BidBundleFormatter();
        for (CampaignData campaign : this.myCampaigns.values()) {
            int dayBiddingFor = this.day + 1;
            double impsToGo = (double)campaign.reachImps.longValue() * 1.25 - (Double)this.campaignEstTargetImps.get((Object)Integer.valueOf((int)campaign.id)).value;
            double reachRatio = (Double)this.campaignEstTargetImps.get((Object)Integer.valueOf((int)campaign.id)).value / (double)campaign.reachImps.longValue();
            if ((long)dayBiddingFor <= campaign.dayEnd + 1) {
                this.log.info("[ campaign " + campaign.id + " ] reachRatio: " + reachRatio);
            }
            if ((long)dayBiddingFor < campaign.dayStart || (long)dayBiddingFor > campaign.dayEnd) continue;
            double rbid = 1000.0 * (Double)((Object[])Query.select(LoadBalancer.getInstance().get(campaign.targetSegment)).property("avgDailyBudget").max().exec().iterator().next())[0];
            rbid /= this.factor;
            if (impsToGo <= 0.0) {
                rbid = 0.0;
            }
            Iterable<Object[]> users = Users.getInstance().generatePriorities(campaign.targetSegment);
            Object[] ratios = (Object[])Query.select(users).index(6).sum().index(6).count().exec().iterator().next();
            double ratio = (double)((Integer)ratios[1]).intValue() / (Double)ratios[0];
            for (Object[] user : users) {
                AdxQuery query = new AdxQuery((String)user[3], new AdxUser((Age)((Object)user[0]), (Gender)((Object)user[1]), (Income)((Object)user[2]), 0.0, 0), (Device)((Object)user[4]), (AdType)((Object)user[5]));
                double rbid_adjusted = rbid * Math.pow((Double)user[6] * ratio * ((Device)((Object)user[4]) == Device.mobile ? campaign.getMobileCoef() : 1.0) * ((AdType)((Object)user[5]) == AdType.video ? campaign.getVideoCoef() : 1.0), 0.125);
                bidBundle.addQuery(query, rbid_adjusted *= Neuro.getInstance().test((Age)((Object)user[0]), (Gender)((Object)user[1]), (Income)((Object)user[2]), (Device)((Object)user[4]), (AdType)((Object)user[5]), (String)user[3]), new Ad(null), campaign.id, 1);
                if (rbid == 0.0) continue;
                formatter.add(campaign.id, query, rbid_adjusted);
            }
            double impressionLimit = Math.max(0.0, impsToGo);
            this.log.info("[ campaign " + campaign.id + " ] impressionLimit: " + impressionLimit / (double)campaign.reachImps.longValue());
            double budgetLimit = impressionLimit == 0.0 ? 0.0 : (1.25 - reachRatio) * rbid / 1000.0 * 1.2;
            bidBundle.setCampaignDailyLimit(campaign.id, (int)impressionLimit, budgetLimit);
        }
        for (String entry : formatter) {
            this.log.info(entry);
        }
        if (bidBundle != null) {
            this.log.info("Day " + this.day + ": Sending BidBundle");
            this.sendMessage(this.adxAgentAddress, bidBundle);
        }
    }

    private void handleCampaignReport(CampaignReport campaignReport) {
        for (CampaignReportKey campaignKey : campaignReport.keys()) {
            int cmpId = campaignKey.getCampaignId();
            CampaignStats cstats = campaignReport.getCampaignReportEntry(campaignKey).getCampaignStats();
            this.myCampaigns.get(cmpId).setStats(cstats);
            this.log.info("Day " + this.day + ": Updating campaign " + cmpId + " stats: " + cstats.getTargetedImps() + " tgtImps " + cstats.getOtherImps() + " nonTgtImps. Cost of imps is " + cstats.getCost());
            this.campaignEstTargetImps.get((Object)Integer.valueOf((int)cmpId)).value = cstats.getTargetedImps();
        }
    }

    private void handleAdxPublisherReport(AdxPublisherReport adxPublisherReport) {
        LinkedList<Object[]> preferences = new LinkedList<Object[]>();
        this.log.info("Publishers Report: ");
        for (PublisherCatalogEntry publisherKey : adxPublisherReport.keys()) {
            AdxPublisherReportEntry entry = (AdxPublisherReportEntry)adxPublisherReport.getEntry(publisherKey);
            this.log.info(entry.toString());
            preferences.add(new Object[]{entry.getPublisherName(), (double)entry.getAdTypeOrientation().get((Object)AdType.video), (double)entry.getPopularity() + 1.0});
        }
        Users.getInstance().updateAdTypePreferences(preferences);
    }

    private void handleAdNetworkReport(AdNetworkReport adnetReport) {
        this.log.info("Day " + this.day + " : AdNetworkReport");
        HashMap<Integer, Boolean> finished = new HashMap<Integer, Boolean>();
        AdNetworkReportSorted entries = new AdNetworkReportSorted(adnetReport);
        AdNetworkReportFormatter formatter = new AdNetworkReportFormatter();
        for (Object entry2 : entries) {
            int campaignId = ((AdNetworkKey)entry2.getKey()).getCampaignId();
            if (!this.myCampaigns.containsKey(campaignId)) continue;
            formatter.add((AdNetworkReportEntry)entry2);
            AdType adType = ((AdNetworkKey)entry2.getKey()).getAdType();
            Device device = ((AdNetworkKey)entry2.getKey()).getDevice();
            CampaignData campaign = this.myCampaigns.get(campaignId);
            Mutable<Double> d = this.campaignEstTargetImps.get(campaignId);
            d.value = (Double)d.value + (double)entry2.getWinCount() * (adType == AdType.video ? campaign.getVideoCoef() : 1.0) * (device == Device.mobile ? campaign.getMobileCoef() : 1.0);
            finished.put(campaignId, false);
        }
        for (Object entry2 : formatter) {
            this.log.info((String)entry2);
        }
        for (Integer campaign : finished.keySet()) {
            finished.put(campaign, (double)this.myCampaigns.get((Object)campaign).reachImps.longValue() - (Double)this.campaignEstTargetImps.get((Object)campaign).value <= 0.0);
        }
        Neuro.getInstance().update(entries, finished);
    }

    @Override
    protected void simulationSetup() {
        this.adNetworkDailyNotification = null;
        this.myCampaigns.clear();
        this.ucsBid = 0.1;
        this.ucsBidMin = 0.1;
        this.ucsBidMax = 0.2;
        this.ucsLevel = 1.0;
        this.day = 0;
        this.wonLastCampagin = true;
        this.factor = 0.1003125;
        this.qualityRating = 1.0;
        this.campaignEstTargetImps.clear();
        this.log.fine("AdNet " + this.getName() + " simulationSetup");
    }

    @Override
    protected void simulationFinished() {
        this.log.fine("Simulation Ended.");
    }

    private static class Mutable<T> {
        public T value;

        Mutable(T defaultValue) {
            this.value = defaultValue;
        }
    }

}

