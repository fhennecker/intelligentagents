

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
import java.io.File;
import java.io.FileWriter;
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

import helpers.World;


/**
 * 
 * @author Mariano Schain
 * Test plug-in
 * 
 */
public class AdNetwork extends Agent {

	private World w;
	private ArrayList<ArrayList<Integer>> campTrack= new ArrayList<ArrayList<Integer>>();
	//private HashMap<Set<MarketSegment>, Integer> mktSegs = new HashMap<Set<MarketSegment>, Integer>();
	private double lastUCS=0.2;
	private double avgPrice;
	//private ArrayList<CampaignData> prevDay= new ArrayList<CampaignData>();
	//private HashMap 
	
	//{46.0,80.0,49.6,26.0,16.0}; //
	
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
	private AdNetworkDailyNotification adNetworkDailyNotification;

	/*
	 * The addresses of server entities to which the agent should send the daily
	 * bids data
	 */
	private String demandAgentAddress;
	private String adxAgentAddress;

	

	/**
	 * Information regarding the latest campaign opportunity announced
	 */
	private CampaignData pendingCampaign;

	/**
	 * We maintain a collection (mapped by the campaign id) of the campaigns won
	 * by our agent.
	 */
	private Map<Integer, CampaignData> myCampaigns;

	/*
	 * the bidBundle to be sent daily to the AdX
	 */
	private AdxBidBundle bidBundle;

	/*
	 * The current bid level for the user classification service
	 */
	double ucsBid;

	/*
	 * The targeted service level for the user classification service
	 */
	double ucsTargetLevel;


	private CampaignData currCampaign;

	File ucsLog;
	public AdNetwork() {
		w = new World();
		w.initPublishers();
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
	private void handleInitialCampaignMessage(
			InitialCampaignMessage campaignMessage) {
		System.out.println(campaignMessage.toString());

		w.day = 0;

		initialCampaignMessage = campaignMessage;
		demandAgentAddress = campaignMessage.getDemandAgentAddress();
		adxAgentAddress = campaignMessage.getAdxAgentAddress();

		
		
		CampaignData campaignData = new CampaignData(initialCampaignMessage);
		campaignData.setBudget(initialCampaignMessage.getBudgetMillis()/1000.0);
		currCampaign = campaignData;
		initTotalPopularity(currCampaign);
		genCampaignQueries(currCampaign);

		

		/*
		 * The initial campaign is already allocated to our agent so we add it
		 * to our allocated-campaigns list.
		 */
		System.out.println("Day " + w.day + ": Allocated campaign - " + campaignData);
		myCampaigns.put(initialCampaignMessage.getId(), campaignData);
		for (int i=0; i<60;i++){
			campTrack.add(new ArrayList<Integer>());
		}
		for (int i= (int)currCampaign.dayStart; i<= (int)currCampaign.dayEnd; i++){
			campTrack.get(i).add(currCampaign.id);
		}
	}

	/**
	 * On day n ( > 0) a campaign opportunity is announced to the competing
	 * agents. The campaign starts on day n + 2 or later and the agents may send
	 * (on day n) related bids (attempting to win the campaign). The allocation
	 * (the winner) is announced to the competing agents during day n + 1.
	 */
	double cmpFactor = 1.2;
	long cmpBidMillis;
	private void handleICampaignOpportunityMessage(
			CampaignOpportunityMessage com) {

		w.day = com.getDay();

		pendingCampaign = new CampaignData(com);
		System.out.println("Day " + w.day + ": Campaign opportunity - " + pendingCampaign);
		int population=getPop(pendingCampaign);
		System.out.println(population);
		/*
		 * The campaign requires com.getReachImps() impressions. The competing
		 * Ad Networks bid for the total campaign Budget (that is, the ad
		 * network that offers the lowest budget gets the campaign allocated).
		 * The advertiser is willing to pay the AdNetwork at most 1$ CPM,
		 * therefore the total number of impressions may be treated as a reserve
		 * (upper bound) price for the auction.
		 */
		
		boolean shouldBid=shouldBid(pendingCampaign);
		//-------------Leela Campaign Bid--------------
		long cmpimps = com.getReachImps();
		long cmpBidMillis= (long)((cmpimps*0.1)*(2-(population/10000))*adNetworkDailyNotification.getQualityScore());
		System.out.println("Day " + w.day + ": Campaign total budget bid (millis): " + cmpBidMillis);
		//-------------Leela Campaign Bid--------------
		//-------------Anne Campaign Bid---------------
		/*Random random = new Random();
		double Gre = 1.2;

                        
		long cmpimps = com.getReachImps();

		if( pendingCampaign.id == adNetworkDailyNotification.getCampaignId() && adNetworkDailyNotification.getCostMillis() == pendingCampaign.budget){
			cmpFactor = cmpFactor;
		}else if( pendingCampaign.id == adNetworkDailyNotification.getCampaignId() && adNetworkDailyNotification.getCostMillis() != pendingCampaign.budget){
			cmpFactor = cmpFactor/Gre;
		}else {
			cmpFactor = cmpFactor*Gre;
		}

		//long cmpBidMillis = (long)((cmpimps*0.1)*(2-(population/10000)));
		//long cmpBidMillis = (long)(avgPrice*cmpimps*cmpFactor);
		double ucsLevel = adNetworkDailyNotification.getServiceLevel();

		if(ucsLevel <= 0.9){
			 cmpBidMillis = (long)(cmpimps*0.1);
			System.out.println("Day " + w.day + ": Campaign total budget bid (millis): " + cmpBidMillis);
		}else{
		            cmpBidMillis = (long)(cmpimps*cmpFactor);
			System.out.println("Day " + w.day + ": Campaign total budget bid (millis): " + cmpBidMillis);
		}  */
		//-------------Anne Campaign Bid---------------
		
		File ucsLog= new File("ucsLog.txt");
        FileWriter fileWriter;// = new FileWriter(ucsLog,true);
        BufferedWriter bufferedWriter; // = new BufferedWriter(fileWriter);
        try{ 
        	fileWriter = new FileWriter(ucsLog,true);
       
        	bufferedWriter = new BufferedWriter(fileWriter);
        }catch (IOException e) {
    	   this.log.log(Level.SEVERE,"Exception thrown while trying to parse message." + e);
    	   return;
        }
        
		/*
		 * Adjust bid s.t. target level is achieved. Note: The bid for the
		 * user classification service is piggybacked
		 */

		if (adNetworkDailyNotification != null) {
			/*
			double ucsLevel = adNetworkDailyNotification.getServiceLevel();
			//ucsBid = 0.1 + random.nextDouble()/10.0;		                                                     //this is the place we add our own ucs bid method
			//ucsBid = 0.2;
			if(ucsLevel > 0.9)
			{
				ucsBid = ucsBid/1.1;
			}
			else if(ucsLevel < 0.8){
				ucsBid = ucsBid * 1.1;
			}
			else {
				ucsBid = ucsBid;
			}
	        try{
	        	bufferedWriter.write("ucs bidding " + ucsBid + " ucs level " + ucsLevel);
	            bufferedWriter.newLine();
	            bufferedWriter.close();
	        }catch (IOException e) {
	        	this.log.log(Level.SEVERE,"Exception thrown while trying to parse message." + e);
				return;
	        }*/
			
			double ucsLevel = adNetworkDailyNotification.getServiceLevel();
			ucsBid=getUCSBid(ucsLevel,lastUCS);
			lastUCS=ucsBid;
			//ucsBid=0.2;
			//ucsBid = 0.1 + random.nextDouble()/10.0;			
			System.out.println("Day " + w.day + ": ucs level reported: " + ucsLevel);
		} else {
			System.out.println("Day " + w.day + ": Initial ucs bid is " + ucsBid);
		}
		/* Note: Campaign bid is in millis */
		AdNetBidMessage bids;
		/*if(shouldBid){
			bids = new AdNetBidMessage(ucsBid, pendingCampaign.id, cmpBidMillis);
		}
		else{
			bids = new AdNetBidMessage(0.1, pendingCampaign.id, cmpimps);
			System.out.println("DID NOT BID! OVERLAP!!");
		}*/
		//cmpBidMillis=(long)(cmpimps*0.1);
		bids = new AdNetBidMessage(ucsBid, pendingCampaign.id, cmpBidMillis);
		sendMessage(demandAgentAddress, bids);
	}
	
	private boolean shouldBid(CampaignData pendingCampaign){
		boolean shouldBid=true;
		for (int i=(int) pendingCampaign.dayStart; i<=(int)pendingCampaign.dayEnd; i++){
			for (int j=0; j<campTrack.get(i).size(); j++){
				CampaignData camp= myCampaigns.get(campTrack.get(i).get(j));
				/*if (pendingCampaign.targetSegment.containsAll(camp.targetSegment)){ //camp superset of pendingCamp
					overlap++;
				}
				else if (camp.targetSegment.containsAll(pendingCampaign.targetSegment)){ //pendingCamp superset of camp
					overlap=overlap+0.5;
				}
				else if ()
				if(overlap>=2){
					return false;
				}*/
				Set<String> newCamp= w.expandSeg(pendingCampaign.targetSegment);
				Set<String> oldCamp= w.expandSeg(camp.targetSegment);
				newCamp.retainAll(oldCamp);
				if(newCamp.size()>2)
					return false;
			}
		}
		
		return shouldBid;
	}
	
	
	
	private double getUCSBid(double ucsLevel, double ucsBid){
		double ro=0.75*dailyReach(w.day);
		double gucs=0.75;
		if (ucsLevel>0.9){
			return (ucsBid/(1+gucs));
		}
		else if ((ucsLevel<0.81)&&((ro/ucsBid)>=(20/3*(1+gucs)/avgPrice))){
			return (ucsBid*(1+gucs));
		}
		else{
			return ucsBid;
		}
	}
	
	private double dailyReach(int day){
		//myCampaigns.get(arg0)
		double dailyReach=0.0;
		for (int i=0; i<campTrack.get(day).size(); i++){
			currCampaign=myCampaigns.get(campTrack.get(day).get(i));
			dailyReach=dailyReach+currCampaign.reachImps/(currCampaign.dayEnd-currCampaign.dayStart+1);
		}
		return dailyReach;
	}

	
	
	private int getPop(CampaignData camp){
		Boolean mktSeg[]= w.splitSegment(camp.targetSegment);
		return (w.getPop(mktSeg[0],mktSeg[1],mktSeg[2]));
	}
	
	private double impFactor(CampaignData camp, String publisher){
		double bidFactor=1.0;
		Boolean mktSeg[]= w.splitSegment(camp.targetSegment);
		if(mktSeg[0]) //age
			bidFactor=bidFactor*w.publisherStats.get(publisher)[0]; //age
		else
			bidFactor=bidFactor*(100-w.publisherStats.get(publisher)[0]);
		if(mktSeg[1])//gender
			bidFactor=bidFactor*w.publisherStats.get(publisher)[2];//gender
		else
			bidFactor=bidFactor*(100-w.publisherStats.get(publisher)[2]);
		if(mktSeg[2])
			bidFactor=bidFactor*w.publisherStats.get(publisher)[1];
		else
			bidFactor=bidFactor*(100-w.publisherStats.get(publisher)[1]);
		return bidFactor;
		
	}
	
	

	/**
	 * On day n ( > 0), the result of the UserClassificationService and Campaign
	 * auctions (for which the competing agents sent bids during day n -1) are
	 * reported. The reported Campaign starts in day n+1 or later and the user
	 * classification service level is applicable starting from day n+1.
	 */
	private void handleAdNetworkDailyNotification(
			AdNetworkDailyNotification notificationMessage) {

		adNetworkDailyNotification = notificationMessage;

		System.out.println("Day " + w.day + ": Daily notification for campaign "
				+ adNetworkDailyNotification.getCampaignId());

		String campaignAllocatedTo = " allocated to "
				+ notificationMessage.getWinner();

		if ((pendingCampaign.id == adNetworkDailyNotification.getCampaignId())
				&& (notificationMessage.getCostMillis() != 0)) {

			/* add campaign to list of won campaigns */
			pendingCampaign.setBudget(notificationMessage.getCostMillis()/1000.0);
			currCampaign = pendingCampaign;
			genCampaignQueries(currCampaign);
			myCampaigns.put(pendingCampaign.id, pendingCampaign);
			for (int i=(int)pendingCampaign.dayStart; i<= (int)pendingCampaign.dayEnd; i++){
				campTrack.get(i).add(pendingCampaign.id);
			}
			
			campaignAllocatedTo = " WON at cost (Millis)"
					+ notificationMessage.getCostMillis();
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
		
		bidBundle = new AdxBidBundle();
		
		/*
		 * 
		 */

		int dayBiddingFor = w.day + 1;

		/* A fixed random bid, for all queries of the campaign */
		/*
		 * Note: bidding per 1000 imps (CPM) - no more than average budget
		 * revenue per imp
		 */
		

		/*
		 * add bid entries w.r.t. each active campaign with remaining contracted
		 * impressions.
		 * 
		 * for now, a single entry per active campaign is added for queries of
		 * matching target segment.
		 */
		//prevDay= new ArrayList<CampaignData>();
		double totalPrice=0.0;
		double totalFactor=0.0;
		for (int i=0; i<campTrack.get(dayBiddingFor).size(); i++){
			currCampaign= myCampaigns.get(campTrack.get(dayBiddingFor).get(i));
			if ((dayBiddingFor >= currCampaign.dayStart)
					&& (dayBiddingFor <= currCampaign.dayEnd)
					&& (currCampaign.impsTogo() > 0)) {

				int entCount = 0;
				for (AdxQuery query : currCampaign.campaignQueries) {
					if (currCampaign.impsTogo() - entCount > 0) {
						/*
						 * among matching entries with the same campaign id, the AdX
						 * randomly chooses an entry according to the designated
						 * weight. by setting a constant weight 1, we create a
						 * uniform probability over active campaigns(irrelevant because we are bidding only on one campaign)
						 */
						double factor= w.publisherStats.get(query.getPublisher())[3]/w.totalPopularity;
						//double rbid = 1000.0*(0.5+factor);
						//double rbid= 10000.0; //EXTREME!!! or 20000.0
						//double rbid = publisherStats.values().//.*2*(0.5+factor); //What's this? :/
						
						double devFactor=1.0;
						double adTypeFactor = 1.0;
						if (query.getAdType() == AdType.text) {
							adTypeFactor=1;
						}
						else if (query.getAdType() == AdType.video) {
							adTypeFactor=currCampaign.videoCoef;
						}
						if (query.getDevice() == Device.pc) {
							adTypeFactor=1;
						}
						else if (query.getDevice() == Device.mobile) {
							adTypeFactor=currCampaign.videoCoef;
						}
						//= currCampaign.videoCoef;
						double rbid = 500.0+800.0*factor+100.0*devFactor+100.0*adTypeFactor;
						totalPrice=totalPrice+factor*rbid;
						totalFactor=totalFactor+factor;
						if (query.getDevice() == Device.pc) {
							if (query.getAdType() == AdType.text) {
								entCount++;
							} else {
								entCount += currCampaign.videoCoef;
							}
						} else {
							if (query.getAdType() == AdType.text) {
								entCount+=currCampaign.mobileCoef;
							} else {
								entCount += currCampaign.videoCoef + currCampaign.mobileCoef;
							}

						}
						bidBundle.addQuery(query, rbid, new Ad(null),
								currCampaign.id, 1);
						//bidBundle.
					}
				}

				double impressionLimit = currCampaign.impsTogo();
				double budgetLimit = currCampaign.budget;
				System.out.print("############");
				System.out.print(budgetLimit);
				System.out.println("############");
				bidBundle.setCampaignDailyLimit(currCampaign.id,
						(int) impressionLimit, budgetLimit);

				System.out.println("Day " + w.day + ": Updated " + entCount
						+ " Bid Bundle entries for Campaign id " + currCampaign.id);
			}
		}
		
		if (bidBundle != null) {
			System.out.println("Day " + w.day + ": Sending BidBundle");
			sendMessage(adxAgentAddress, bidBundle);
		}
		avgPrice=totalPrice/totalFactor;
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
			myCampaigns.get(cmpId).setStats(cstats);

			System.out.println("Day " + w.day + ": Updating campaign " + cmpId + " stats: "
					+ cstats.getTargetedImps() + " tgtImps "
					+ cstats.getOtherImps() + " nonTgtImps. Cost of imps is "
					+ cstats.getCost());
		}
	}

	/**
	 * Users and Publishers statistics: popularity and ad type orientation
	 */
	private void handleAdxPublisherReport(AdxPublisherReport adxPublisherReport) {
		System.out.print("~~~~~~~~~~");
		System.out.print(myCampaigns.size());
		System.out.print("~~~~~~~~~~");
		System.out.print(campTrack.get(w.day).size());
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
		bidBundle = new AdxBidBundle();

		/* initial bid between 0.1 and 0.2 */
		ucsBid = 0.2;

		myCampaigns = new HashMap<Integer, CampaignData>();
		log.fine("AdNet " + getName() + " simulationSetup");
	}

	@Override
	protected void simulationFinished() {
		campaignReports.clear();
		bidBundle = null;
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
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!"+Arrays.toString(campaignData.campaignQueries)+"!!!!!!!!!!!!!!!!");
	}

	public void initTotalPopularity(CampaignData campaignData){
		double totalPop=0.0;
		for (String PublisherName : w.publisherNames) {
			totalPop=totalPop+w.publisherStats.get(PublisherName)[3];
		}
		w.totalPopularity = totalPop;
	}

	

	private class CampaignData {
		/* campaign attributes as set by server */
		Long reachImps;
		long dayStart;
		long dayEnd;
		Set<MarketSegment> targetSegment;
		double videoCoef;
		double mobileCoef;
		int id;
		private AdxQuery[] campaignQueries;//array of queries relvent for the campaign.

		/* campaign info as reported */
		CampaignStats stats;
		double budget;

		public CampaignData(InitialCampaignMessage icm) {
			reachImps = icm.getReachImps();
			dayStart = icm.getDayStart();
			dayEnd = icm.getDayEnd();
			targetSegment = icm.getTargetSegment();
			videoCoef = icm.getVideoCoef();
			mobileCoef = icm.getMobileCoef();
			id = icm.getId();

			stats = new CampaignStats(0, 0, 0);
			budget = 0.0;
		}

		public void setBudget(double d) {
			budget = d;
		}

		public CampaignData(CampaignOpportunityMessage com) {
			dayStart = com.getDayStart();
			dayEnd = com.getDayEnd();
			id = com.getId();
			reachImps = com.getReachImps();
			targetSegment = com.getTargetSegment();
			mobileCoef = com.getMobileCoef();
			videoCoef = com.getVideoCoef();
			stats = new CampaignStats(0, 0, 0);
			budget = 0.0;
		}

		@Override
		public String toString() {
			return "Campaign ID " + id + ": " + "day " + dayStart + " to "
					+ dayEnd + " " + targetSegment + ", reach: " + reachImps
					+ " coefs: (v=" + videoCoef + ", m=" + mobileCoef + ")";
		}

		int impsTogo() {
			return (int) Math.max(0, reachImps - stats.getTargetedImps());
		}

		void setStats(CampaignStats s) {
			stats.setValues(s);
		}

		public AdxQuery[] getCampaignQueries() {
			return campaignQueries;
		}

		public void setCampaignQueries(AdxQuery[] campaignQueries) {
			this.campaignQueries = campaignQueries;
		}
		
	}

}