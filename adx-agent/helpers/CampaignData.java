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


public class CampaignData {
    /* campaign attributes as set by server */
    public Long reachImps;
    public long dayStart;
    public long dayEnd;
    public Set<MarketSegment> targetSegment;
    public double videoCoef;
    public double mobileCoef;
    public int id;
    public AdxQuery[] campaignQueries;//array of queries relvent for the campaign.

    /* campaign info as reported */
    public CampaignStats stats;
    public double budget;

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

    public int impsTogo() {
        return (int) Math.max(0, reachImps - stats.getTargetedImps());
    }

    public void setStats(CampaignStats s) {
        stats.setValues(s);
    }

    public AdxQuery[] getCampaignQueries() {
        return campaignQueries;
    }

    public void setCampaignQueries(AdxQuery[] campaignQueries) {
        this.campaignQueries = campaignQueries;
    }
    
}