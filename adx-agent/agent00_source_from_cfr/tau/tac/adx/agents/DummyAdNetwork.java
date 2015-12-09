/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.agents;

import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.Product;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Agent;
import se.sics.tasim.aw.Message;
import se.sics.tasim.props.SimulationStatus;
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
import tau.tac.adx.report.publisher.AdxPublisherReport;

public class DummyAdNetwork
extends Agent {
    private final Logger log = Logger.getLogger(DummyAdNetwork.class.getName());
    private PublisherCatalog publisherCatalog;
    private String ServerAddress;
    private AdxBidBundle bidBundle;
    private AdxQuery[] queries;
    private Random randomGenerator;
    private InitialCampaignMessage initialCampaignMessage;
    private CampaignOpportunityMessage campaignOpportunityMessage;
    private CampaignReport campaignReport;
    private AdNetworkDailyNotification adNetworkDailyNotification;
    private CampaignData pendingCampaign;
    private Map<Integer, CampaignData> myCampaigns;
    private int day;

    @Override
    protected void messageReceived(Message message) {
        try {
            Transportable content = message.getContent();
            this.log.fine(message.getContent().getClass().toString());
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
            }
        }
        catch (NullPointerException e) {
            this.log.log(Level.SEVERE, "Excdeption thrown. ", e);
            return;
        }
    }

    private void handleAdxPublisherReport(AdxPublisherReport content) {
    }

    private void handleAdNetworkDailyNotification(AdNetworkDailyNotification adNetNotificationMessage) {
        this.adNetworkDailyNotification = adNetNotificationMessage;
        if (this.pendingCampaign.id == this.adNetworkDailyNotification.getCampaignId() && this.getName().equals(adNetNotificationMessage.getWinner())) {
            this.myCampaigns.put(this.pendingCampaign.id, this.pendingCampaign);
        }
    }

    private void handleCampaignReport(CampaignReport campaignReport) {
        this.campaignReport = campaignReport;
        for (CampaignReportKey campaignKey : campaignReport.keys()) {
            int cmpId = campaignKey.getCampaignId();
            CampaignStats campaignStats = campaignReport.getCampaignReportEntry(campaignKey).getCampaignStats();
        }
    }

    private void updateCampaignDataOpportunity(CampaignOpportunityMessage campaignOpportunityMessage, CampaignData campaignData) {
        campaignData.dayStart = campaignOpportunityMessage.getDayStart();
        campaignData.dayEnd = campaignOpportunityMessage.getDayEnd();
        campaignData.id = campaignOpportunityMessage.getId();
        campaignData.reachImps = campaignOpportunityMessage.getReachImps();
        campaignData.targetSegment = campaignOpportunityMessage.getTargetSegment();
        campaignData.mobileCoef = campaignOpportunityMessage.getMobileCoef();
        campaignData.videoCoef = campaignOpportunityMessage.getVideoCoef();
    }

    private void handleICampaignOpportunityMessage(CampaignOpportunityMessage com) {
        this.day = com.getDay();
        this.campaignOpportunityMessage = com;
        this.pendingCampaign = new CampaignData(this, null);
        this.updateCampaignDataOpportunity(com, this.pendingCampaign);
        long cmpBid = Math.abs(this.randomGenerator.nextLong()) % this.campaignOpportunityMessage.getReachImps();
        AdNetBidMessage bids = new AdNetBidMessage(this.randomGenerator.nextInt(10), this.pendingCampaign.id, cmpBid);
        this.sendMessage(this.ServerAddress, bids);
    }

    private void updateCampaignData(InitialCampaignMessage campaignMessage, CampaignData campaignData) {
        campaignData.reachImps = campaignMessage.getReachImps();
        campaignData.dayStart = campaignMessage.getDayStart();
        campaignData.dayEnd = campaignMessage.getDayEnd();
        campaignData.targetSegment = campaignMessage.getTargetSegment();
        campaignData.videoCoef = campaignMessage.getVideoCoef();
        campaignData.mobileCoef = campaignMessage.getMobileCoef();
        campaignData.id = campaignMessage.getId();
    }

    private void handleInitialCampaignMessage(InitialCampaignMessage campaignMessage) {
        this.day = 0;
        this.initialCampaignMessage = campaignMessage;
        this.ServerAddress = campaignMessage.getDemandAgentAddress();
        CampaignData campaignData = new CampaignData(this, null);
        this.updateCampaignData(this.initialCampaignMessage, campaignData);
        this.myCampaigns.put(this.initialCampaignMessage.getId(), campaignData);
    }

    private void handleSimulationStatus(SimulationStatus simulationStatus) {
        this.sendBidAndAds();
    }

    private void handlePublisherCatalog(PublisherCatalog publisherCatalog) {
        this.publisherCatalog = publisherCatalog;
        this.generateAdxQuerySpace();
    }

    private void handleAdNetworkReport(AdNetworkReport queryReport) {
    }

    @Override
    protected void simulationSetup() {
        this.day = 0;
        this.bidBundle = new AdxBidBundle();
        this.myCampaigns = new HashMap<Integer, CampaignData>();
        this.randomGenerator = new Random();
        this.log.fine("dummy " + this.getName() + " simulationSetup");
    }

    @Override
    protected void simulationFinished() {
        this.bidBundle = null;
    }

    protected void sendBidAndAds() {
        this.bidBundle = new AdxBidBundle();
        for (CampaignData campaign : this.myCampaigns.values()) {
            int dayBiddingFor = this.day + 1;
            if ((long)dayBiddingFor < campaign.dayStart || (long)dayBiddingFor > campaign.dayEnd) continue;
            Random rnd = new Random();
            int i = 0;
            while (i < this.queries.length) {
                Set<MarketSegment> segmentsList = this.queries[i].getMarketSegments();
                this.bidBundle.addQuery(this.queries[i], (double)(1 + rnd.nextLong() % 1000) / 1000.0, new Ad(null), campaign.id, 1);
                ++i;
            }
        }
        if (this.bidBundle != null) {
            this.sendToRole(3, this.bidBundle);
        }
    }

    private void generateAdxQuerySpace() {
        if (this.publisherCatalog != null && this.queries == null) {
            HashSet<AdxQuery> queryList = new HashSet<AdxQuery>();
            for (PublisherCatalogEntry publisherCatalogEntry : this.publisherCatalog) {
                MarketSegment[] arrmarketSegment = MarketSegment.values();
                int n = arrmarketSegment.length;
                int n2 = 0;
                while (n2 < n) {
                    MarketSegment userSegment = arrmarketSegment[n2];
                    HashSet<MarketSegment> marketSegments = new HashSet<MarketSegment>();
                    marketSegments.add(userSegment);
                    queryList.add(new AdxQuery(publisherCatalogEntry.getPublisherName(), marketSegments, Device.mobile, AdType.text));
                    queryList.add(new AdxQuery(publisherCatalogEntry.getPublisherName(), marketSegments, Device.pc, AdType.text));
                    queryList.add(new AdxQuery(publisherCatalogEntry.getPublisherName(), marketSegments, Device.mobile, AdType.video));
                    queryList.add(new AdxQuery(publisherCatalogEntry.getPublisherName(), marketSegments, Device.pc, AdType.video));
                    ++n2;
                }
            }
            this.queries = new AdxQuery[queryList.size()];
            queryList.toArray(this.queries);
        }
    }

    private class CampaignData {
        Long reachImps;
        long dayStart;
        long dayEnd;
        Set<MarketSegment> targetSegment;
        double videoCoef;
        double mobileCoef;
        int id;
        CampaignStats stats;
        final /* synthetic */ DummyAdNetwork this$0;

        private CampaignData(DummyAdNetwork dummyAdNetwork) {
            this.this$0 = dummyAdNetwork;
        }

        void setStats(CampaignStats s) {
            this.stats.setValues(s);
        }

        /* synthetic */ CampaignData(DummyAdNetwork dummyAdNetwork, CampaignData campaignData) {
            CampaignData campaignData2;
            campaignData2(dummyAdNetwork);
        }
    }

}

