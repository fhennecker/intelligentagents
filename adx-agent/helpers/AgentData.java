package helpers;

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

import helpers.CampaignData;

public class AgentData {

    public ArrayList<ArrayList<Integer>> campTrack= new ArrayList<ArrayList<Integer>>();
    public ArrayList<ArrayList<Integer>> otherCampTrack= new ArrayList<ArrayList<Integer>>();
    //public HashMap<Set<MarketSegment>, Integer> mktSegs = new HashMap<Set<MarketSegment>, Integer>();
    public double lastUCS=0.2;
    public double avgPrice;

    private double[] wonLosses = new double[60];

    /**
     * Information regarding the latest campaign opportunity announced
     */
    public CampaignData pendingCampaign;

    /**
     * We maintain a collection (mapped by the campaign id) of the campaigns won
     * by our agent.
     */
    public Map<Integer, CampaignData> campaigns;

    /*
     * the bidBundle to be sent daily to the AdX
     */
    public AdxBidBundle bidBundle;

    /*
     * The current bid level for the user classification service
     */
    public double ucsBid;

    /*
     * The targeted service level for the user classification service
     */
    public double ucsTargetLevel;


    public CampaignData currCampaign;
    public double yesterdayCampaignBid;

    public AdNetworkDailyNotification dailyNotification;

    /* For every day, keep a list of all running campaigns stats */
    public ArrayList<HashMap<Integer, CampaignStats>> impressionStats;

    public AgentData(){
        impressionStats = new ArrayList<HashMap<Integer, CampaignStats>>();
        for (Integer i=0; i<60; i++) {
            impressionStats.add(null);
        }
    }

    public double dailyReach(int day){
        //myCampaigns.get(arg0)
        double dailyReach=0.0;
        for (int i=0; i<campTrack.get(day).size(); i++){
            currCampaign=campaigns.get(campTrack.get(day).get(i));
            dailyReach=dailyReach+currCampaign.reachImps/(currCampaign.dayEnd-currCampaign.dayStart+1);
        }
        return dailyReach;
    }

    public void registerWin(int day){
        wonLosses[day] = 1;
    }

    public double wonLossRatio(int day, int days){
        double res = 0;
        int startDay = Math.max(day-days, 0);
        for (int i=startDay; i<day; i++){
            res += wonLosses[i];
        }
        return res/(day-startDay);
    }

    public double meanPricePerImp(int day){
        double imps = 0;
        double price = 0;
        if (impressionStats.get(day) != null) {
            for (int cmpId : impressionStats.get(day).keySet()) {
                double dayImps = impressionStats.get(day).get(cmpId).getTargetedImps();
                double dayPrice = impressionStats.get(day).get(cmpId).getCost();
                if (day-1 > 0 && impressionStats.get(day-1).containsKey(cmpId)){
                    dayImps -= impressionStats.get(day-1).get(cmpId).getTargetedImps();
                    dayPrice -= impressionStats.get(day-1).get(cmpId).getCost();
                }
                imps += dayImps;
                price += dayPrice;
            }
        }
        return price/imps;
    }
}