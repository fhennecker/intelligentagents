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

/**
 * 
 * @author Mariano Schain
 * Test plug-in
 * 
 */
public class myAgent0 extends Agent {

	private ArrayList<ArrayList<Integer>> campTrack= new ArrayList<ArrayList<Integer>>();
	private int popData[][][]= new int[][][]{{{1836,1980},{1795,2401}},{{517,256},{808,407}}}; //income,age,gender
	private HashMap<String, Double[]> publisherStats = new HashMap<String, Double[]>();
	private HashMap<Set<MarketSegment>, Integer> mktSegs = new HashMap<Set<MarketSegment>, Integer>();
	private double totalPop;
	private double lastUCS=0.2;
	private double avgPrice;
	//private ArrayList<CampaignData> prevDay= new ArrayList<CampaignData>();
	//private HashMap 
	
	//{46.0,80.0,49.6,26.0,16.0}; //
	
	private final Logger log = Logger
			.getLogger(myAgent0.class.getName());

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
	private PublisherCatalog publisherCatalog;
	private InitialCampaignMessage initialCampaignMessage;
	private AdNetworkDailyNotification adNetworkDailyNotification;

	/*
	 * The addresses of server entities to which the agent should send the daily
	 * bids data
	 */
	private String demandAgentAddress;
	private String adxAgentAddress;

	/*
	 * we maintain a list of queries - each characterized by the web site (the
	 * publisher), the device type, the ad type, and the user market segment
	 */
	private AdxQuery[] queries;

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

	/*
	 * current day of simulation
	 */
	private int day;
	private String[] publisherNames;
	private CampaignData currCampaign;

	File ucsLog;
	public myAgent0() {
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
				handlePublisherCatalog((PublisherCatalog) content);
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
		System.out.println("Day " + day + " :" + content.toString());
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
	 * Process the reported set of publishers
	 * 
	 * @param publisherCatalog
	 */
	private void handlePublisherCatalog(PublisherCatalog publisherCatalog) {
		this.publisherCatalog = publisherCatalog;
		generateAdxQuerySpace();
		getPublishersNames();

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

		day = 0;

		initialCampaignMessage = campaignMessage;
		demandAgentAddress = campaignMessage.getDemandAgentAddress();
		adxAgentAddress = campaignMessage.getAdxAgentAddress();

		initPublishers();
		
		CampaignData campaignData = new CampaignData(initialCampaignMessage);
		campaignData.setBudget(initialCampaignMessage.getBudgetMillis()/1000.0);
		currCampaign = campaignData;
		totalPop= genCampaignQueries(currCampaign);
		

		/*
		 * The initial campaign is already allocated to our agent so we add it
		 * to our allocated-campaigns list.
		 */
		System.out.println("Day " + day + ": Allocated campaign - " + campaignData);
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

		day = com.getDay();

		pendingCampaign = new CampaignData(com);
		System.out.println("Day " + day + ": Campaign opportunity - " + pendingCampaign);
		
		//Boolean mktSeg[]= splitSegment(pendingCampaign.targetSegment);
		//System.out.println(getPop(mktSeg[0],mktSeg[1],mktSeg[2]));
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
        
		if(!mktSegs.containsKey(pendingCampaign.targetSegment)){
			System.out.println("Market Segment Not handled before.");
			mktSegs.put(pendingCampaign.targetSegment, 0);
		}
		
		Random random = new Random();
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
			System.out.println("Day " + day + ": Campaign total budget bid (millis): " + cmpBidMillis);
		}else{
		            cmpBidMillis = (long)(cmpimps*cmpFactor);
			System.out.println("Day " + day + ": Campaign total budget bid (millis): " + cmpBidMillis);
		}                                                                                                                                         //I need to add some thing here about the conditions that I don't want to bid: reach impressions are too high, and overlapping thing.







		
		//System.out.println("Day " + day + ": Campaign total budget bid (millis): " + cmpBidMillis);

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
			
			//double ucsLevel = adNetworkDailyNotification.getServiceLevel();
			ucsBid=getUCSBid(ucsLevel,lastUCS);
			lastUCS=ucsBid;
			//ucsBid = 0.1 + random.nextDouble()/10.0;			
			System.out.println("Day " + day + ": ucs level reported: " + ucsLevel);
		} else {
			System.out.println("Day " + day + ": Initial ucs bid is " + ucsBid);
		}
		//System.out.print("Market Segment ");
		//System.out.print(pendingCampaign.targetSegment);
		/* Note: Campaign bid is in millis */
		AdNetBidMessage bids = new AdNetBidMessage(ucsBid, pendingCampaign.id, cmpBidMillis);
		sendMessage(demandAgentAddress, bids);
	}
	
	private double getUCSBid(double ucsLevel, double ucsBid){
		//I/P: previous bid price, received UCS level
		//O/P: today's bid price
		double ro=0.75*dailyReach(day);
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

	private int getPop(Boolean age, Boolean gender, Boolean income){
		int pop=0;
		if (age!=null){
			if (gender!=null){
				if (income!=null){ 	//income,age,gender
					pop=	popData[income?1:0][age?1:0][gender?1:0];
					return pop;
				}
				else{				//age,gender
					pop= 	popData[0][age?1:0][gender?1:0]+
							popData[1][age?1:0][gender?1:0];
					return pop;
				}
			}
			else{
				if (income!=null){	//income,age
					pop= 	popData[income?1:0][age?1:0][0]+
							popData[income?1:0][age?1:0][1];
					return pop;
				}
				else{ 				//age
					pop= 	popData[0][age?1:0][0]+
							popData[0][age?1:0][1]+
							popData[1][age?1:0][0]+
							popData[1][age?1:0][1];
					return pop;
				}
			}
		}
		else{
			if (gender!=null){
				if (income!=null){	//income,gender
					pop= 	popData[income?1:0][0][gender?1:0]+
							popData[income?1:0][1][gender?1:0];
					return pop;
				}
				else{				//gender
					pop= 	popData[0][age?1:0][0]+
							popData[0][age?1:0][1]+
							popData[1][age?1:0][0]+
							popData[1][age?1:0][1];
					return pop;
				}
			}
			else{
				if (income!=null){	//age
					pop= 	popData[0][age?1:0][0]+
							popData[0][age?1:0][1]+
							popData[1][age?1:0][0]+
							popData[1][age?1:0][1];
					return pop;
				}
				else{				//
					pop= 	10000;
					return pop;
				}
			}
		}
	}

	private Boolean[] splitSegment(Set<MarketSegment> seg){
		System.out.println(seg.toArray().length);
		Boolean retSeg[]= new Boolean[3];
		for (int i=0; i<seg.toArray().length; i++){
			String element=seg.toArray()[i].toString();
			if(element.equals("FEMALE")){
				System.out.println("Segment Female");
				retSeg[1]=true;
			}
			else if(element.equals("MALE")){
				System.out.println("Segment Male");
				retSeg[1]=false;
			}
			if(element.equals("YOUNG")){
				System.out.println("Segment Young");
				retSeg[0]=false;
			}
			else if(element.equals("OLD")){
				System.out.println("Segment Old");
				retSeg[0]=true;
			}
			if(element.equals("LOW_INCOME")){
				System.out.println("Segment Low Income");
				retSeg[2]=false;
			}
			else if(element.equals("HIGH_INCOME")){
				System.out.println("Segment High Income");
				retSeg[2]=true;
			}
		}
		return retSeg;
	}
	
	private int getPop(CampaignData camp){
		Boolean mktSeg[]= splitSegment(camp.targetSegment);
		return (getPop(mktSeg[0],mktSeg[1],mktSeg[2]));
	}
	
	private double impFactor(CampaignData camp, String publisher){
		double bidFactor=1.0;
		Boolean mktSeg[]= splitSegment(camp.targetSegment);
		if(mktSeg[0]) //age
			bidFactor=bidFactor*publisherStats.get(publisher)[0]; //age
		else
			bidFactor=bidFactor*(100-publisherStats.get(publisher)[0]);
		if(mktSeg[1])//gender
			bidFactor=bidFactor*publisherStats.get(publisher)[2];//gender
		else
			bidFactor=bidFactor*(100-publisherStats.get(publisher)[2]);
		if(mktSeg[2])
			bidFactor=bidFactor*publisherStats.get(publisher)[1];
		else
			bidFactor=bidFactor*(100-publisherStats.get(publisher)[1]);
		return bidFactor;
		
	}
	
	private void initPublishers(){ //age,income, gender
		String publishers[]={ "yahoo", "cnn", "nyt", "hfn",
				"msn", "fox", "amazon", "ebay", "wallmart", "target", "bestbuy",
				"sears", "webmd", "ehow", "ask", "tripadvisor", "cnet", "weather" };
		Double stats[][]={	{46.0	,	80.0	,	49.6	,	16.0},
							{43.0	,	75.0	,	48.6	,	2.2},
							{41.0	,	73.0	,	47.6	,	3.1},
							{43.0	,	74.0	,	46.6	,	8.1},
							{43.0	,	76.0	,	47.6	,	18.2},
							{41.0	,	72.0	,	48.6	,	3.1},
							{41.0	,	77.0	,	47.6	,	12.8},
							{41.0	,	77.0	,	48.6	,	8.5},
							{39.0	,	75.0	,	45.6	,	3.8},
							{44.0	,	72.0	,	45.6	,	2.0},
							{41.0	,	72.5	,	47.6	,	1.6},
							{38.0	,	70.0	,	46.6	,	1.6},
							{40.0	,	72.5	,	45.6	,	2.5},
							{41.0	,	77.0	,	47.6	,	2.5},
							{39.0	,	78.0	,	48.6	,	5.0},
							{42.0	,	72.5	,	46.6	,	1.6},
							{43.0	,	74.5	,	50.6	,	1.7},
							{41.0	,	72.0	,	47.6	,	5.8}};
		for (int i=0; i<publishers.length; i++){
			Double temp[]={stats[i][0],stats[i][1],stats[i][2],stats[i][3]};
			publisherStats.put(publishers[i], temp);
		}
		
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

		System.out.println("Day " + day + ": Daily notification for campaign "
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

		System.out.println("Day " + day + ": " + campaignAllocatedTo
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
		System.out.println("Day " + day + " : Simulation Status Received");
		sendBidAndAds();
		System.out.println("Day " + day + " ended. Starting next day");
		//campTrack.add(new ArrayList<Integer>());
		++day;
	}

	/**
	 * 
	 */
	protected void sendBidAndAds() {
		
		bidBundle = new AdxBidBundle();
		
		/*
		 * 
		 */

		int dayBiddingFor = day + 1;

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
		//double avgPrice=0.0;
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
						double factor= publisherStats.get(query.getPublisher())[3]/totalPop;
						//double rbid = 10000.0*2*(0.5+factor);
						double rbid = 20000.0;
						totalPrice=totalPrice+factor*rbid;
						totalFactor=totalFactor+factor;
						System.out.print("########");
						System.out.print(factor);
						System.out.print("########");
						System.out.print(rbid);
						System.out.println("########");
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
					}
				}

				double impressionLimit = currCampaign.impsTogo();
				double budgetLimit = currCampaign.budget;
				bidBundle.setCampaignDailyLimit(currCampaign.id,
						(int) impressionLimit, budgetLimit);

				System.out.println("Day " + day + ": Updated " + entCount
						+ " Bid Bundle entries for Campaign id " + currCampaign.id);
			}
		}
		
		if (bidBundle != null) {
			System.out.println("Day " + day + ": Sending BidBundle");
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

			System.out.println("Day " + day + ": Updating campaign " + cmpId + " stats: "
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
		System.out.print(campTrack.get(day).size());
		System.out.print("~~~~~~~~~~");
		System.out.print(day);
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

		System.out.println("Day " + day + " : AdNetworkReport");
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

		day = 0;
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

	/**
	 * A user visit to a publisher's web-site results in an impression
	 * opportunity (a query) that is characterized by the the publisher, the
	 * market segment the user may belongs to, the device used (mobile or
	 * desktop) and the ad type (text or video).
	 * 
	 * An array of all possible queries is generated here, based on the
	 * publisher names reported at game initialization in the publishers catalog
	 * message
	 */
	private void generateAdxQuerySpace() {
		if (publisherCatalog != null && queries == null) {
			Set<AdxQuery> querySet = new HashSet<AdxQuery>();

			/*
			 * for each web site (publisher) we generate all possible variations
			 * of device type, ad type, and user market segment
			 */
			for (PublisherCatalogEntry publisherCatalogEntry : publisherCatalog) {
				String publishersName = publisherCatalogEntry
						.getPublisherName();
				for (MarketSegment userSegment : MarketSegment.values()) {
					Set<MarketSegment> singleMarketSegment = new HashSet<MarketSegment>();
					singleMarketSegment.add(userSegment);

					querySet.add(new AdxQuery(publishersName,
							singleMarketSegment, Device.mobile, AdType.text));

					querySet.add(new AdxQuery(publishersName,
							singleMarketSegment, Device.pc, AdType.text));

					querySet.add(new AdxQuery(publishersName,
							singleMarketSegment, Device.mobile, AdType.video));

					querySet.add(new AdxQuery(publishersName,
							singleMarketSegment, Device.pc, AdType.video));

				}

				/**
				 * An empty segments set is used to indicate the "UNKNOWN"
				 * segment such queries are matched when the UCS fails to
				 * recover the user's segments.
				 */
				querySet.add(new AdxQuery(publishersName,
						new HashSet<MarketSegment>(), Device.mobile,
						AdType.video));
				querySet.add(new AdxQuery(publishersName,
						new HashSet<MarketSegment>(), Device.mobile,
						AdType.text));
				querySet.add(new AdxQuery(publishersName,
						new HashSet<MarketSegment>(), Device.pc, AdType.video));
				querySet.add(new AdxQuery(publishersName,
						new HashSet<MarketSegment>(), Device.pc, AdType.text));
			}
			queries = new AdxQuery[querySet.size()];
			querySet.toArray(queries);
		}
	}
	
	/*genarates an array of the publishers names
	 * */
	private void getPublishersNames() {
		if (null == publisherNames && publisherCatalog != null) {
			ArrayList<String> names = new ArrayList<String>();
			for (PublisherCatalogEntry pce : publisherCatalog) {
				names.add(pce.getPublisherName());
			}

			publisherNames = new String[names.size()];
			names.toArray(publisherNames);
		}
	}
	/*
	 * genarates the campaign queries relevant for the specific campaign, and assign them as the campaigns campaignQueries field 
	 */
	private double genCampaignQueries(CampaignData campaignData) {
		Set<AdxQuery> campaignQueriesSet = new HashSet<AdxQuery>();
		double totalPop=0.0;
		for (String PublisherName : publisherNames) {
			totalPop=totalPop+publisherStats.get(PublisherName)[3];
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
		return totalPop;

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
