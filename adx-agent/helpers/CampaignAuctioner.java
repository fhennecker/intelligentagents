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

import helpers.World;
import helpers.CampaignData;

public class CampaignAuctioner {

    private World w;
    private AgentData d;
    private Logger log;

    public CampaignAuctioner(World world, AgentData data, Logger log){
        w = world;
        d = data;
        log = log;
    }


    double cmpFactor = 1.2;
    long cmpBidMillis;
    public long bidValue(CampaignOpportunityMessage com){
        int population=w.getTargetedPopulation(d.pendingCampaign);
        System.out.println(population);
        long cmpimps = com.getReachImps();
        long cmpBidMillis= (long)((cmpimps*0.1)*(2-(population/10000))*d.dailyNotification.getQualityScore());
        return cmpBidMillis;
    }
    

    public boolean shouldBid(){
        boolean shouldBid=true;
        for (int i=(int) d.pendingCampaign.dayStart; i<=(int)d.pendingCampaign.dayEnd; i++){
            for (int j=0; j<d.campTrack.get(i).size(); j++){
                CampaignData camp= d.campaigns.get(d.campTrack.get(i).get(j));
                /*if (d.pendingCampaign.targetSegment.containsAll(camp.targetSegment)){ //camp superset of pendingCamp
                    overlap++;
                }
                else if (camp.targetSegment.containsAll(d.pendingCampaign.targetSegment)){ //pendingCamp superset of camp
                    overlap=overlap+0.5;
                }
                else if ()
                if(overlap>=2){
                    return false;
                }*/
                Set<String> newCamp= w.expandSeg(d.pendingCampaign.targetSegment);
                Set<String> oldCamp= w.expandSeg(camp.targetSegment);
                newCamp.retainAll(oldCamp);
                if(newCamp.size()>2)
                    return false;
            }
        }
        
        return shouldBid;
    }
    
    
}