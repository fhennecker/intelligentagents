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
import helpers.AgentData;

public class UCSAuctioner {

    private World w;
    private AgentData d;
    private Logger log;

    public UCSAuctioner(World world, AgentData data, Logger log){
        w = world;
        d = data;
        log = log;
    }

   /* public double bidValue(){
        int noImpUCS=0;
        for(int jj=0;jj<d.campTrack.get(w.day+1).size();jj++){
            noImpUCS+=d.campaigns.get(d.campTrack.get(w.day+1).get(jj)).impsTogo();
        }
        if(noImpUCS==0)
            return 0.0;
        else{
            //return 0.2;
            double ro=0.75*d.dailyReach(w.day);
            double gucs=0.75;
            if (d.dailyNotification.getServiceLevel()>0.9){
                return (d.lastUCS/(1+gucs));
            }
            else if ((d.dailyNotification.getServiceLevel()<0.81)&&((ro/d.lastUCS)>=(20/3*(1+gucs)/d.avgPrice))){
                return (d.lastUCS*(1+gucs));
            }
            else{
                return d.lastUCS;
            }
        }*/
    public double bidValue(){
        double ucsLevel = d.dailyNotification.getServiceLevel();
       // double ucsBid;
        int noImpUCS=0;
        for(int jj=0;jj<d.campTrack.get(w.day+1).size();jj++){
            noImpUCS+=d.campaigns.get(d.campTrack.get(w.day+1).get(jj)).impsTogo();
        }
        if(noImpUCS==0){
            return 0.0;
        }
        
        if(ucsLevel > 0.9)
        {
           
            d.ucsBid = d.ucsBid/1.1;
            if(d.ucsBid == 0){
                d.ucsBid = 0.1;
            }
        }
        else if(ucsLevel < 0.8){
            d.ucsBid = d.ucsBid * 1.1;
            if(d.ucsBid == 0){
                d.ucsBid = 0.1;
            }
        }
        else {
            d.ucsBid = d.ucsBid;
            if(d.ucsBid == 0){
                d.ucsBid = 0.1;
            }
        }
        /*try{
            bufferedWriter.write("ucs bidding " + d.ucsBid + " ucs level " + ucsLevel);
            bufferedWriter.newLine();
            bufferedWriter.close();
        }catch (IOException e) {
            this.log.log(Level.SEVERE,"Exception thrown while trying to parse message." + e);
            return;
        }*/
        
        d.lastUCS=d.ucsBid;
        System.out.println("Day " + w.day + ": ucs level reported: " + ucsLevel);
        return d.ucsBid;
    
    }

      /*  ucsLevel = adNetworkDailyNotification.getServiceLevel();
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
        }
        
        lastUCS=ucsBid;
        System.out.println("Day " + day + ": ucs level reported: " + ucsLevel);
        } else {
            System.out.println("Day " + day + ": Initial ucs bid is " + ucsBid);
        }*/

}