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
    
    private CampaignData  cd;

    public CampaignAuctioner(World world, AgentData data, Logger log){
        w = world;
        d = data;
        log = log;
        
    }

    double Gre = 1.2;
	double cmpFactor = 0.2;
	long cmpBidMillis;
	int counter = 0;
	
    //double cmpFactor = 1.2;
    //long cmpBidMillis;
    public long bidValue(CampaignOpportunityMessage com){
        
        Gre = 1.3-cd.count/60.0;
        
        //int population=w.getTargetedPopulation(d.pendingCampaign);
        //System.out.println(population);
        long cmpimps = com.getReachImps();
        //long cmpBidMillis= (long)((cmpimps*0.1)*(2-(population/10000))*d.dailyNotification.getQualityScore());
 if(w.day  == 1){
                        	cmpBidMillis = (long)(Math.ceil(cmpimps*0.1));
                        } else{
		if( d.currCampaign.id == d.dailyNotification.getCampaignId() && d.dailyNotification.getCostMillis() == d.currCampaign.budget){
			cmpFactor = cmpFactor;
		}else if(d.currCampaign.id == d.dailyNotification.getCampaignId() && d.dailyNotification.getCostMillis() != d.currCampaign.budget){
			cmpFactor = cmpFactor*Gre;
			counter++;
                     
		}else {
			cmpFactor = cmpFactor/Gre;
		}
        if(d.dailyNotification.getQualityScore() < 0.9){
			 cmpBidMillis = (long)(Math.ceil(cmpimps*0.1));
			//System.out.println("Day " + day + ": Campaign total budget bid (millis): " + cmpBidMillis);
		}else{
			if(cmpFactor >1){
				cmpFactor = 1;
			} else if(cmpFactor < 0.1){
				cmpFactor = 0.1;
			}
		            cmpBidMillis = (long)(Math.ceil(cmpimps*cmpFactor));
		            if(cmpBidMillis > cmpimps){
		            	cmpBidMillis = cmpimps;
		            } else if(cmpBidMillis < (long)(0.1*cmpimps)){
		            	cmpBidMillis = (long)(Math.ceil(0.1*cmpimps));
		            }
			//System.out.println("Day " + day + ": Campaign total budget bid (millis): " + cmpBidMillis);
		}  
	}
		System.out.println("Day " + w.day + ": Campaign total budget bid (millis): " + cmpBidMillis + "factor" + cmpFactor + "Greed" +Gre);

//***************************************************************************Add the counter to the file
	   File camLog= new File("camLog.txt");
       FileWriter fileWriter;// = new FileWriter(ucsLog,true);
       BufferedWriter bufferedWriter; // new BufferedWriter(fileWriter);
       //  fileWriter = new FileWriter(camLog);
        //bufferedWriter = new BufferedWriter(fileWriter);
                try{ 
        	            fileWriter = new FileWriter(camLog);
                        bufferedWriter = new BufferedWriter(fileWriter);
                   // }catch (IOException e) {
    	                 //this.log.log(Level.SEVERE,"Exception thrown while trying to parse message." + e);
    	                 //return;
                      //  }
                //try{
	        	   bufferedWriter.write( ""+ counter);
	               bufferedWriter.newLine();
	               bufferedWriter.close();
	               }catch (IOException e) {
	        	  // return;
	               }  
	        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&" +counter);
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
