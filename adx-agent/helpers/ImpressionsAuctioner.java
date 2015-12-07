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
import helpers.Regressor;

public class ImpressionsAuctioner {

    private World w;
    private AgentData d;
    private Logger log;

    public ImpressionsAuctioner(World world, AgentData data, Logger log){
        w = world;
        d = data;
        log = log;
    }

    public void generateBidBundle(){
        d.bidBundle = new AdxBidBundle();
        

        int dayBiddingFor = w.day + 1;

        double totalPrice=0.0;
        double totalFactor=0.0;
        for (int i=0; i<d.campTrack.get(dayBiddingFor).size(); i++){
            d.currCampaign= d.campaigns.get(d.campTrack.get(dayBiddingFor).get(i));
            if ((dayBiddingFor >= d.currCampaign.dayStart)
                    && (dayBiddingFor <= d.currCampaign.dayEnd)
                    && (d.currCampaign.impsTogo() > 0)) {

                int entCount = 0;
                for (AdxQuery query : w.queries) {
                        
                        d.bidBundle.addQuery(query, 10000, new Ad(null),
                                d.currCampaign.id, 1);
                        //int weight=(int)Math.ceil(d.currCampaign.budget/d.currCampaign.reachImps*Math.pow(d.currCampaign.impsTogo()/(d.currCampaign.dayEnd-dayBiddingFor+1),2.0));
                        //d.bidBundle.getEntry(query).setWeight(weight);
                    
                }

                double impressionLimit = d.currCampaign.impsTogo();
                double budgetLimit = d.currCampaign.budget;
                System.out.print("############");
                System.out.print(budgetLimit);
                System.out.println("############");
                d.bidBundle.setCampaignDailyLimit(d.currCampaign.id,
                        (int) impressionLimit, budgetLimit);

                System.out.println("Day " + w.day + ": Updated " + entCount
                        + " Bid Bundle entries for Campaign id " + d.currCampaign.id);
            }
        }
        d.avgPrice=totalPrice/totalFactor;
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

    public double estimateImpCost(int days){
        /* Regress on price per impression on given number of days before today*/
        days = Math.min(days, w.day-1);
        double[][] training = new double[days][2];
        int i = 0;
        System.out.print("Mean price per imps : ");
        for (int day=w.day-days-1; day<w.day-1; day++) {
            training[i][0] = day;
            training[i][1] = d.meanPricePerImp(day);
            i+=1;
            System.out.print(d.meanPricePerImp(day));
            System.out.print(" ");
        }
        Regressor r = new Regressor(training);
        System.out.print(", Prediction : ");
        double[] toPredict = new double[1];
        toPredict[0] = w.day;
        System.out.println(r.predict(toPredict));
        
        return r.predict(toPredict);
    }

}