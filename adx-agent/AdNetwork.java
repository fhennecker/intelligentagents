
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
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

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
import tau.tac.adx.report.demand.CampaignReportKey;
import tau.tac.adx.report.demand.InitialCampaignMessage;
import tau.tac.adx.report.demand.campaign.auction.CampaignAuctionReport;
import tau.tac.adx.report.publisher.AdxPublisherReport;
import tau.tac.adx.report.publisher.AdxPublisherReportEntry;
import tau.tac.adx.sim.config.AdxConfigurationParser;
import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.BankStatus;
import java.sql.*;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;


import helpers.World;
import helpers.AgentData;
import helpers.CampaignData;
import helpers.CampaignAuctioner;
import helpers.UCSAuctioner;
import helpers.ImpressionsAuctioner;
import helpers.EM;


/**
 * 
 * @author Mariano Schain
 * Test plug-in
 * 
 */
public class AdNetwork extends Agent {

	private World w;
	private AgentData d;
	private CampaignAuctioner ca;
	private UCSAuctioner ua;
	private ImpressionsAuctioner ia;
	private EM em;
	
	//private ArrayList<CampaignData> prevDay= new ArrayList<CampaignData>();

	
	private final Logger log = Logger
			.getLogger(AdNetwork.class.getName());

	/*
	 * Basic simulation information. An agent should receive the {@link
	 * StartInfo} at the beginning of the game or during recovery.
	 */
	//@SuppressWarnings("unused")
	private StartInfo startInfo;

	/**
	 * Messages received:
	 * 
	 * We keep all the {@link CampaignReport campaign reports} delivered to the
	 * agent. We also keep the initialization messages {@link PublisherCatalog}
	 * and {@link InitialCampaignMessage} and the most recent messages and
	 * reports {@link CampaignOpportunityMessage}, {@link CampaignReport}, and
	 * {@link AdNetworkDailyNotification}.
	 */
	private final Queue<CampaignReport> campaignReports;
	
	private InitialCampaignMessage initialCampaignMessage;
	

	/*
     * The addresses of server entities to which the agent should send the daily
     * bids data
     */
    private String demandAgentAddress;
    private String adxAgentAddress;

	

	

	File ucsLog;
	public AdNetwork() {
		w = new World();
		w.initPublishers();
		d = new AgentData();
		ca = new CampaignAuctioner(w, d, log);
		ua = new UCSAuctioner(w, d, log);
		ia = new ImpressionsAuctioner(w, d, log);
		em = new EM();
		campaignReports = new LinkedList<CampaignReport>();
	}

	@Override
	protected void messageReceived(Message message) {
		try {
			Transportable content = message.getContent();

			// log.fine(message.getContent().getClass().toString());

			if (content instanceof InitialCampaignMessage) {
				handleInitialCampaignMessage((InitialCampaignMessage) content);
			} else if (content instanceof CampaignOpportunityMessage) {
				handleICampaignOpportunityMessage((CampaignOpportunityMessage) content);
			} else if (content instanceof CampaignReport) {
				handleCampaignReport((CampaignReport) content);
			} else if (content instanceof AdNetworkDailyNotification) {
				handleAdNetworkDailyNotification((AdNetworkDailyNotification) content);
			} else if (content instanceof AdxPublisherReport) {
				handleAdxPublisherReport((AdxPublisherReport) content);
			} else if (content instanceof SimulationStatus) {
				handleSimulationStatus((SimulationStatus) content);
			} else if (content instanceof PublisherCatalog) {
				w.handlePublisherCatalog((PublisherCatalog) content);
			} else if (content instanceof AdNetworkReport) {
				handleAdNetworkReport((AdNetworkReport) content);
			} else if (content instanceof StartInfo) {
				handleStartInfo((StartInfo) content);
			} else if (content instanceof BankStatus) {
				handleBankStatus((BankStatus) content);
			} else if(content instanceof CampaignAuctionReport) {
				hadnleCampaignAuctionReport((CampaignAuctionReport) content);
			}
			else {
				System.out.println("UNKNOWN Message Received: " + content);
			}

		} catch (NullPointerException e) {
			this.log.log(Level.SEVERE,
					"Exception thrown while trying to parse message." + e);
			return;
		}
	}

	private void hadnleCampaignAuctionReport(CampaignAuctionReport content) {
		// ingoring
	}

	private void handleBankStatus(BankStatus content) {
		System.out.println("Day " + w.day + " :" + content.toString());
	}

	/**
	 * Processes the start information.
	 * 
	 * @param startInfo
	 *            the start information.
	 */
	protected void handleStartInfo(StartInfo startInfo) {
		this.startInfo = startInfo;
	}

	

	/**
	 * On day 0, a campaign (the "initial campaign") is allocated to each
	 * competing agent. The campaign starts on day 1. The address of the
	 * server's AdxAgent (to which bid bundles are sent) and DemandAgent (to
	 * which bids regarding campaign opportunities may be sent in subsequent
	 * days) are also reported in the initial campaign message
	 */
	public int count; 
	public void handleInitialCampaignMessage(
            InitialCampaignMessage campaignMessage) {
        System.out.println(campaignMessage.toString());
       /*************************************Open and read the camplog file*******************************************************/
        String str = null;
                        try{
                      //  int count = 0;
		FileReader file = new FileReader("camLog.txt");
                       BufferedReader reader = new BufferedReader(file);  

                       
                        str = reader.readLine();
                      
                         count = Integer.parseInt(str);
                        
                        }catch (IOException e) {
	        	
	        }
                       
                       System.out.println("********^^^^^^^^^^^^^^^^^^^^******************" +count);
       
       /*************************************end file*****************************************************************************/
        w.day = 0;

        initialCampaignMessage = campaignMessage;
        demandAgentAddress = campaignMessage.getDemandAgentAddress();
        adxAgentAddress = campaignMessage.getAdxAgentAddress();

        
        
        CampaignData campaignData = new CampaignData(initialCampaignMessage);
        campaignData.setBudget(initialCampaignMessage.getBudgetMillis()/1000.0);
        d.currCampaign = campaignData;
        initTotalPopularity(campaignData);
        genCampaignQueries(campaignData);

        

        /*
         * The initial campaign is already allocated to our agent so we add it
         * to our allocated-campaigns list.
         */
        System.out.println("Day " + w.day + ": Allocated campaign - " + campaignData);
        d.campaigns.put(initialCampaignMessage.getId(), campaignData);
        for (int i=0; i<60;i++){
            d.campTrack.add(new ArrayList<Integer>());
            d.otherCampTrack.add(new ArrayList<Integer>());
        }
        for (int i= (int)d.currCampaign.dayStart; i<= (int)d.currCampaign.dayEnd; i++){
            d.campTrack.get(i).add(d.currCampaign.id);
        }
    }

	
	
	private void handleICampaignOpportunityMessage(
            CampaignOpportunityMessage com) {

        w.day = com.getDay();

        d.pendingCampaign = new CampaignData(com);
        System.out.println("Day " + w.day + ": Campaign opportunity - " + d.pendingCampaign);
        
        
        //-------------Leela Campaign Bid--------------
        long cmpBidMillis = ca.bidValue(com);
        System.out.print("Original bid : ");
        System.out.println(cmpBidMillis);
        System.out.print("Won/Loss ratio on 5 days :");
        System.out.println(d.wonLossRatio(w.day, 5));
        if (w.day > 5) {
        	if (d.wonLossRatio(w.day, 5) < 0.5) {
        		cmpBidMillis += (long) em.getPessimisticBid(com.getReachImps());
        	}
        	else {
        		cmpBidMillis += (long) em.getOptimisticBid(com.getReachImps());
        	}
        	cmpBidMillis /= 2; // average between EM's bid and our bid
        	if (cmpBidMillis < com.getReachImps() / 10 + 1){
        		cmpBidMillis = (long) com.getReachImps() / 10 + 1;
        	}
        }
        // System.out.print("  - Pessimistic bid : ");
        // System.out.println(em.getPessimisticBid(com.getReachImps()));
        
        System.out.println("Day " + w.day + ": Campaign total budget bid (millis): " + cmpBidMillis);
       
        if (d.dailyNotification != null) {
           
            d.ucsBid=ua.bidValue();
            d.lastUCS=d.ucsBid;
            System.out.println("Day " + w.day + ": ucs level reported: " + d.dailyNotification.getServiceLevel());
        } else {
            System.out.println("Day " + w.day + ": Initial ucs bid is " + d.ucsBid);
        }
        /* Note: Campaign bid is in millis */
        AdNetBidMessage bids;
        /*if(shouldBid){
            bids = new AdNetBidMessage(ucsBid, d.pendingCampaign.id, cmpBidMillis);
        }
        else{
            bids = new AdNetBidMessage(0.1, d.pendingCampaign.id, cmpimps);
            System.out.println("DID NOT BID! OVERLAP!!");
        }*/
        //cmpBidMillis=(long)(cmpimps*0.1);
        bids = new AdNetBidMessage(d.ucsBid, d.pendingCampaign.id, cmpBidMillis);
        sendMessage(demandAgentAddress, bids);
        d.yesterdayCampaignBid = cmpBidMillis;
    }
	

	/**
	 * On day n ( > 0), the result of the UserClassificationService and Campaign
	 * auctions (for which the competing agents sent bids during day n -1) are
	 * reported. The reported Campaign starts in day n+1 or later and the user
	 * classification service level is applicable starting from day n+1.
	 */
	private void handleAdNetworkDailyNotification(
			AdNetworkDailyNotification notificationMessage) {

		d.dailyNotification = notificationMessage;

		System.out.println("Day " + w.day + ": Daily notification for campaign "
				+ d.dailyNotification.getCampaignId());

		String campaignAllocatedTo = " allocated to "
				+ notificationMessage.getWinner();

		if ((d.pendingCampaign.id == d.dailyNotification.getCampaignId())
				&& (notificationMessage.getCostMillis() != 0)) {

			d.registerWin(w.day);
			/* add campaign to list of won campaigns */
			d.pendingCampaign.setBudget(notificationMessage.getCostMillis()/1000.0);
			d.currCampaign = d.pendingCampaign;
			genCampaignQueries(d.currCampaign);
			d.campaigns.put(d.pendingCampaign.id, d.pendingCampaign);
			for (int i=(int)d.pendingCampaign.dayStart; i<= (int)d.pendingCampaign.dayEnd; i++){
				d.campTrack.get(i).add(d.pendingCampaign.id);
			}
			
			campaignAllocatedTo = " WON at cost (Millis)"
					+ notificationMessage.getCostMillis();
			em.wonBids.addValue((double)d.yesterdayCampaignBid/(double)d.pendingCampaign.reachImps);
		}
		else{
			if (w.day > 1){
				em.lostBids.addValue((double)d.yesterdayCampaignBid/(double)d.pendingCampaign.reachImps);
			}
			for (int i= (int)d.pendingCampaign.dayStart; i<= (int)d.pendingCampaign.dayEnd; i++){
				d.otherCampTrack.get(i).add(d.pendingCampaign.id);
			}
		}

		System.out.println("Day " + w.day + ": " + campaignAllocatedTo
				+ ". UCS Level set to " + notificationMessage.getServiceLevel()
				+ " at price " + notificationMessage.getPrice()
				+ " Quality Score is: " + notificationMessage.getQualityScore());
	}

	/**
	 * The SimulationStatus message received on day n indicates that the
	 * calculation time is up and the agent is requested to send its bid bundle
	 * to the AdX.
	 */
	private void handleSimulationStatus(SimulationStatus simulationStatus) {
		System.out.println("Day " + w.day + " : Simulation Status Received");
		sendBidAndAds();
		System.out.println("Day " + w.day + " ended. Starting next day");
		//campTrack.add(new ArrayList<Integer>());
		++w.day;
	}

	/**
	 * 
	 */
	protected void sendBidAndAds() {
		ia.generateBidBundle();
		if (d.bidBundle != null) {
            System.out.println("Day " + w.day + ": Sending BidBundle");
            sendMessage(adxAgentAddress, d.bidBundle);
        }
	}

	/**
	 * Campaigns performance w.r.t. each allocated campaign
	 */
	private void handleCampaignReport(CampaignReport campaignReport) {

		campaignReports.add(campaignReport);

		/*
		 * for each campaign, the accumulated statistics from day 1 up to day
		 * n-1 are reported
		 */
		for (CampaignReportKey campaignKey : campaignReport.keys()) {
			int cmpId = campaignKey.getCampaignId();
			CampaignStats cstats = campaignReport.getCampaignReportEntry(
					campaignKey).getCampaignStats();
			d.campaigns.get(cmpId).setStats(cstats);

			System.out.println("Day " + w.day + ": Updating campaign " + cmpId + " stats: "
					+ cstats.getTargetedImps() + " tgtImps "
					+ cstats.getOtherImps() + " nonTgtImps. Cost of imps is "
					+ cstats.getCost());

			if (d.impressionStats.get(w.day-1) == null){
				d.impressionStats.set(w.day-1, new HashMap<Integer, CampaignStats>());
			}
			d.impressionStats.get(w.day-1).put(new Integer(cmpId), cstats);
		}
		ia.estimateImpCost(5);
	}

	/**
	 * Users and Publishers statistics: popularity and ad type orientation
	 */
	private void handleAdxPublisherReport(AdxPublisherReport adxPublisherReport) {
		System.out.print("~~~~~~~~~~");
		System.out.print(d.campaigns.size());
		System.out.print("~~~~~~~~~~");
		System.out.print(d.campTrack.get(w.day).size());
		System.out.print("~~~~~~~~~~");
		System.out.print(w.day);
		System.out.println("~~~~~~~~~~");
		
		System.out.println("Publishers Report: ");
		for (PublisherCatalogEntry publisherKey : adxPublisherReport.keys()) {
			AdxPublisherReportEntry entry = adxPublisherReport
					.getEntry(publisherKey);
			System.out.println(entry.toString());
		}
	}

	/**
	 * 
	 * @param AdNetworkReport
	 */
	private void handleAdNetworkReport(AdNetworkReport adnetReport) {

		System.out.println("Day " + w.day + " : AdNetworkReport");
		/*
		 * for (AdNetworkKey adnetKey : adnetReport.keys()) {
		 * 
		 * double rnd = Math.random(); if (rnd > 0.95) { AdNetworkReportEntry
		 * entry = adnetReport .getAdNetworkReportEntry(adnetKey);
		 * System.out.println(adnetKey + " " + entry); } }
		 */
	}

	@Override
	protected void simulationSetup() {

		w.day = 0;
		d.bidBundle = new AdxBidBundle();

		/* initial bid between 0.1 and 0.2 */
		d.ucsBid = 0.2;

		d.campaigns = new HashMap<Integer, CampaignData>();
		log.fine("AdNet " + getName() + " simulationSetup");
	}

	@Override
	protected void simulationFinished() {
		campaignReports.clear();
		d.bidBundle = null;
	}

	
	/*
	 * genarates the campaign queries relevant for the specific campaign, and assign them as the campaigns campaignQueries field 
	 */
	private void genCampaignQueries(CampaignData campaignData) {
		Set<AdxQuery> campaignQueriesSet = new HashSet<AdxQuery>();
		for (String PublisherName : w.publisherNames) {
			campaignQueriesSet.add(new AdxQuery(PublisherName,
					campaignData.targetSegment, Device.mobile, AdType.text));
			campaignQueriesSet.add(new AdxQuery(PublisherName,
					campaignData.targetSegment, Device.mobile, AdType.video));
			campaignQueriesSet.add(new AdxQuery(PublisherName,
					campaignData.targetSegment, Device.pc, AdType.text));
			campaignQueriesSet.add(new AdxQuery(PublisherName,
					campaignData.targetSegment, Device.pc, AdType.video));
		}

		campaignData.campaignQueries = new AdxQuery[campaignQueriesSet.size()];
		campaignQueriesSet.toArray(campaignData.campaignQueries);
	}

	public void initTotalPopularity(CampaignData campaignData){
		double totalPop=0.0;
		for (String PublisherName : w.publisherNames) {
			totalPop=totalPop+w.publisherStats.get(PublisherName)[3];
		}
		w.totalPopularity = totalPop;
	}

}
