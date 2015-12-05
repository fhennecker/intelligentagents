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


public class World {
    public int popData[][][]= new int[][][]{{{1836,1980},{1795,2401}},{{517,256},{808,407}}}; //income,age,gender
    public HashMap<String, Double[]> publisherStats = new HashMap<String, Double[]>();
    public double totalPopularity;
    public int day;
    public PublisherCatalog publisherCatalog;
    public String[] publisherNames;

    /*
     * we maintain a list of queries - each characterized by the web site (the
     * publisher), the device type, the ad type, and the user market segment
     */
    public AdxQuery[] queries;

    public World() {

    }

    /**
     * Process the reported set of publishers
     * 
     * @param publisherCatalog
     */
    public void handlePublisherCatalog(PublisherCatalog publisherCatalog) {
        this.publisherCatalog = publisherCatalog;
        generateAdxQuerySpace();
        getPublishersNames();

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
    public void generateAdxQuerySpace() {
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
    public void getPublishersNames() {
        if (null == publisherNames && publisherCatalog != null) {
            ArrayList<String> names = new ArrayList<String>();
            for (PublisherCatalogEntry pce : publisherCatalog) {
                names.add(pce.getPublisherName());
            }

            publisherNames = new String[names.size()];
            names.toArray(publisherNames);
        }
    }

    public void initPublishers(){ //age,income, gender
        String publishers[]={ "yahoo", "cnn", "nyt", "hfn",
                "msn", "fox", "amazon", "ebay", "wallmart", "target", "bestbuy",
                "sears", "webmd", "ehow", "ask", "tripadvisor", "cnet", "weather" };
        Double stats[][]={  {46.0   ,   80.0    ,   49.6    ,   16.0},
                            {43.0   ,   75.0    ,   48.6    ,   2.2},
                            {41.0   ,   73.0    ,   47.6    ,   3.1},
                            {43.0   ,   74.0    ,   46.6    ,   8.1},
                            {43.0   ,   76.0    ,   47.6    ,   18.2},
                            {41.0   ,   72.0    ,   48.6    ,   3.1},
                            {41.0   ,   77.0    ,   47.6    ,   12.8},
                            {41.0   ,   77.0    ,   48.6    ,   8.5},
                            {39.0   ,   75.0    ,   45.6    ,   3.8},
                            {44.0   ,   72.0    ,   45.6    ,   2.0},
                            {41.0   ,   72.5    ,   47.6    ,   1.6},
                            {38.0   ,   70.0    ,   46.6    ,   1.6},
                            {40.0   ,   72.5    ,   45.6    ,   2.5},
                            {41.0   ,   77.0    ,   47.6    ,   2.5},
                            {39.0   ,   78.0    ,   48.6    ,   5.0},
                            {42.0   ,   72.5    ,   46.6    ,   1.6},
                            {43.0   ,   74.5    ,   50.6    ,   1.7},
                            {41.0   ,   72.0    ,   47.6    ,   5.8}};
        for (int i=0; i<publishers.length; i++){
            Double temp[]={stats[i][0],stats[i][1],stats[i][2],stats[i][3]};
            publisherStats.put(publishers[i], temp);
        }
        
    }


    public Set<String> expandSeg(Set<MarketSegment> mktSeg){
        Set<String> seg= new HashSet<String>();
        seg.add("FYL");
        seg.add("FYH");
        seg.add("FOL");
        seg.add("FOH");
        seg.add("MYL");
        seg.add("MYH");
        seg.add("MOL");
        seg.add("MOH");
        Boolean[] a1=splitSegment(mktSeg);  
        Boolean age=a1[0];
        Boolean gender=a1[1];
        Boolean income=a1[2];
        if(age!=null){
            if(age){ //age-old... remove all young
                seg.remove("FYL");
                seg.remove("MYL");
                seg.remove("FYH");
                seg.remove("MYH");
            }
            else if(!age){ //age-young... remove all old
                seg.remove("FOL");
                seg.remove("MOL");
                seg.remove("FOH");
                seg.remove("MOH");
            
            }
        }
        if(gender!=null){
            if(gender){ //gender-female... remove all male
                if(seg.contains("FYL")){seg.remove("FYL");}
                if(seg.contains("FOL")){seg.remove("FOL");}
                if(seg.contains("FYH")){seg.remove("FYH");}
                if(seg.contains("FOH")){seg.remove("FOH");}
            }
            else if(!gender){ //gender-male... remove all female
                if(seg.contains("MYL")){seg.remove("MYL");}
                if(seg.contains("MOL")){seg.remove("MOL");}
                if(seg.contains("MYH")){seg.remove("MYH");}
                if(seg.contains("MOH")){seg.remove("MOH");}
            
            }
        }
        if(income!=null){
            if(income){ //income-high... remove all low
                if(seg.contains("MYL")){seg.remove("MYL");}
                if(seg.contains("MOL")){seg.remove("MOL");}
                if(seg.contains("FYL")){seg.remove("FYL");}
                if(seg.contains("FOL")){seg.remove("FOL");}
            }
            else if(!income){ //income-low... remove all high
                
                if(seg.contains("MYH")){seg.remove("MYH");}
                if(seg.contains("MOH")){seg.remove("MOH");}
                if(seg.contains("FYH")){seg.remove("FYH");}
                if(seg.contains("FOH")){seg.remove("FOH");}
            }
            
        }
        return seg;
    }

    public int getPopulationForSegment(Boolean age, Boolean gender, Boolean income){
        int pop=0;
        if (age!=null){
            if (gender!=null){
                if (income!=null){  //income,age,gender
                    pop=    popData[income?1:0][age?1:0][gender?1:0];
                    return pop;
                }
                else{               //age,gender
                    pop=    popData[0][age?1:0][gender?1:0]+
                            popData[1][age?1:0][gender?1:0];
                    return pop;
                }
            }
            else{
                if (income!=null){  //income,age
                    pop=    popData[income?1:0][age?1:0][0]+
                            popData[income?1:0][age?1:0][1];
                    return pop;
                }
                else{               //age
                    pop=    popData[0][age?1:0][0]+
                            popData[0][age?1:0][1]+
                            popData[1][age?1:0][0]+
                            popData[1][age?1:0][1];
                    return pop;
                }
            }
        }
        else{
            if (gender!=null){
                if (income!=null){  //income,gender
                    pop=    popData[income?1:0][0][gender?1:0]+
                            popData[income?1:0][1][gender?1:0];
                    return pop;
                }
                else{               //gender
                    pop=    popData[0][age?1:0][0]+
                            popData[0][age?1:0][1]+
                            popData[1][age?1:0][0]+
                            popData[1][age?1:0][1];
                    return pop;
                }
            }
            else{
                if (income!=null){  //age
                    pop=    popData[0][age?1:0][0]+
                            popData[0][age?1:0][1]+
                            popData[1][age?1:0][0]+
                            popData[1][age?1:0][1];
                    return pop;
                }
                else{               //
                    pop=    10000;
                    return pop;
                }
            }
        }
    }

    public Boolean[] splitSegment(Set<MarketSegment> seg){
        System.out.println(seg.toArray().length);
        Boolean retSeg[]= new Boolean[3];
        for (int i=0; i<seg.toArray().length; i++){
            String element=seg.toArray()[i].toString();
            if(element.equals("FEMALE")){
                retSeg[1]=true;
            }
            else if(element.equals("MALE")){
                retSeg[1]=false;
            }
            if(element.equals("YOUNG")){
                retSeg[0]=false;
            }
            else if(element.equals("OLD")){
                retSeg[0]=true;
            }
            if(element.equals("LOW_INCOME")){
                retSeg[2]=false;
            }
            else if(element.equals("HIGH_INCOME")){
                retSeg[2]=true;
            }
        }
        return retSeg;
    }

    public int getTargetedPopulation(CampaignData camp){
        Boolean mktSeg[]= splitSegment(camp.targetSegment);
        return (getPopulationForSegment(mktSeg[0],mktSeg[1],mktSeg[2]));
    }
}