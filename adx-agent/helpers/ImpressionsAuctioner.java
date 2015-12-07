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
                for (AdxQuery query : d.currCampaign.campaignQueries) {
                    if (d.currCampaign.impsTogo() - entCount > 0) {
                        /*
                         * among matching entries with the same campaign id, the AdX
                         * randomly chooses an entry according to the designated
                         * weight. by setting a constant weight 1, we create a
                         * uniform probability over active campaigns(irrelevant because we are bidding only on one campaign)
                         */
                        double factor= w.publisherStats.get(query.getPublisher())[3]/w.totalPopularity;
                        
                        double devFactor=1.0;
                        double adTypeFactor = 1.0;
                        if (query.getAdType() == AdType.text) {
                            adTypeFactor=1;
                        }
                        else if (query.getAdType() == AdType.video) {
                            adTypeFactor=d.currCampaign.videoCoef;
                        }
                        if (query.getDevice() == Device.pc) {
                            adTypeFactor=1;
                        }
                        else if (query.getDevice() == Device.mobile) {
                            adTypeFactor=d.currCampaign.videoCoef;
                        }
                        //double rbid = 500.0+200.0*factor+100.0*devFactor+100.0*adTypeFactor;
                        //double rbid= 1000.0*w.getSegmentPopularity(d.currCampaign)*d.currCampaign.budget/d.currCampaign.reachImps;
                        double budgetLeft=d.currCampaign.budget;
                        double rbid=0.0;
                        double factorImp=0.0;
                        double eta=(d.currCampaign.reachImps- d.currCampaign.impsTogo())/d.currCampaign.reachImps;
                        if(w.day<=1){
                            rbid=100.0;
                        }
                        else{
                            if(d.impressionStats.get(w.day-1).get(d.currCampaign.id) != null){
                                budgetLeft=d.currCampaign.budget-d.impressionStats.get(w.day-1).get(d.currCampaign.id).getCost();
                                System.out.print("-------------------------------");
                                System.out.print(d.impressionStats.get(w.day-1).get(d.currCampaign.id).getCost());
                            }
                            factorImp= decideImpPrice(d.currCampaign.impsTogo(),budgetLeft,eta);
                            rbid= 1000.0*w.getSegmentPopularity(d.currCampaign)*factorImp;//budgetLeft/d.currCampaign.impsTogo();
                            
                            if(((w.day+1)==(d.currCampaign.dayEnd))&&(eta<1.0)){
                                rbid = 47.0;
                            }
                            if((d.currCampaign.dayEnd - d.currCampaign.dayStart + 1) == 10){
                                rbid = 10.0;
                            }
                        }
                        
                       // rbid=rbid*0.8+0.1*factor+0.5*(adTypeFactor+devFactor);
                        System.out.print("[[[[[[[[");
                        System.out.print(rbid);
                        System.out.print("]]]]]]]]");
                        System.out.print(1000.0*w.getSegmentPopularity(d.currCampaign));
                        System.out.print("[[[[[[[[");
                        System.out.print(factorImp);
                        System.out.println("]]]]]]]]");
                        //System.out.print("[[[[[[[[");
                        //System.out.print(d.impressionStats);
                        //System.out.print("]]]]]]]]");
                        //System.out.print("[[[[[[[[");
                        //System.out.print(w.getSegmentPopularity(d.currCampaign));
                        //System.out.println("]]]]]]]]");
                        double doneImp= d.currCampaign.reachImps-d.currCampaign.impsTogo();
                        double impPerDay=d.currCampaign.reachImps/(d.currCampaign.dayEnd-d.currCampaign.dayStart+1);
                        //double goal= (w.day-currCampaign.dayStart)*impPerDay;
                        //double impFactor= 1+(goal-doneImp)/currCampaign.reachImps;
                        
                        /*if((doneImp*1.2<impPerDay*(w.day-d.currCampaign.dayStart+1))&&(dayBiddingFor>d.currCampaign.dayStart)){
                        	System.out.println("QUALITY SCORE LOW!!!");
                        	rbid=1000.0;
                        }*/
                        
                        totalPrice=totalPrice+factor*rbid;
                        totalFactor=totalFactor+factor;
                        if (query.getDevice() == Device.pc) {
                            if (query.getAdType() == AdType.text) {
                                entCount++;
                            } else {
                                entCount += d.currCampaign.videoCoef;
                            }
                        } else {
                            if (query.getAdType() == AdType.text) {
                                entCount+=d.currCampaign.mobileCoef;
                            } else {
                                entCount += d.currCampaign.videoCoef + d.currCampaign.mobileCoef;
                            }

                        }
                        d.bidBundle.addQuery(query, rbid, new Ad(null),
                                d.currCampaign.id, 1);
                        double weight=1;
                        //int weight=(int)Math.ceil(d.currCampaign.budget/d.currCampaign.reachImps)*Math.pow(d.currCampaign.impsTogo()/(d.currCampaign.dayEnd-dayBiddingFor+1),2.0);
                        //weight= (double)((d.currCampaign.budget/d.currCampaign.reachImps)*(Math.pow(((d.currCampaign.impsTogo()/d.currCampaign.reachImps)/(d.currCampaign.dayEnd-dayBiddingFor+1)), 2.0))));
                        weight= (double)((d.currCampaign.budget/d.currCampaign.reachImps)*(d.currCampaign.impsTogo()/d.currCampaign.reachImps));
                                        // (Math.pow(((d.currCampaign.impsTogo()/d.currCampaign.reachImps)/(d.currCampaign.dayEnd-dayBiddingFor+1)), 1.0)));
                        int weightInt = (int)(10000000.0*weight);
                        if(w.day == 0){
                            weightInt = 1;
                        }
                        System.out.println("======================================================weight" + weightInt);
                        d.bidBundle.getEntry(query).setWeight(weightInt);
                    }
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
    
   /* public double estimateImpCost(int days){
        // Regress on price per impression on given number of days before today
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
    }*/
    
    public double decideImpPrice(double reach, double budget, double eta){
        double budgetNew= budget*0.85;
        if((budget<0.2)&&(reach<200)){
            budgetNew= 1*budget;
        }
        if((((w.day+1)==(d.currCampaign.dayEnd-1))||((w.day+1)==(d.currCampaign.dayEnd)))&&(eta<1.0)){
            budgetNew=50.0*budget;
        }
        if(((w.day+1)==(d.currCampaign.dayEnd))&&(eta<1.0)){
            budgetNew=100.0;
            System.out.println("*****************************************************URGENT BUDGET!!!!!" + budgetNew/reach);
        }
        System.out.println("*****************************************************" + budgetNew/reach);
        return (budgetNew/reach);
    }
    
   /* public double decideImpPrice(double reach, double budget, double eta){
        double budgetNew= budget*0.87;
        if((budget<0.3)&&(reach<500)){
            budgetNew= 1*budget;
        }
        if((((w.day+1)==(d.currCampaign.dayEnd-1))||((w.day+1)==(d.currCampaign.dayEnd)))&&(eta<1.0)){
            budgetNew=3.0*budget;
        }
        if(((w.day+1)==(d.currCampaign.dayEnd))&&(eta<1.0)){
            budgetNew=5.0*budget;
             System.out.println("*****************************************************URGENT BUDGET!!!!!" + budgetNew/reach);
        }
        System.out.println("*****************************************************" + budgetNew/reach);
        return (budgetNew/reach);
    }*/

   /* private double impFactor(CampaignData camp, String publisher){
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
        
    }*/

}