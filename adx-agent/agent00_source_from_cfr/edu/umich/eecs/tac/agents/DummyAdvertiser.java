/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.agents;

import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.AdvertiserInfo;
import edu.umich.eecs.tac.props.BidBundle;
import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.SalesReport;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Agent;
import se.sics.tasim.aw.Message;
import se.sics.tasim.props.SimulationStatus;

public class DummyAdvertiser
extends Agent {
    private Logger log = Logger.global;
    private RetailCatalog retailCatalog;
    private String publisherAddress;
    private BidBundle bidBundle;
    private Query[] queries;
    private double[] impressions;
    private double[] clicks;
    private double[] conversions;
    private double[] values;

    @Override
    protected void messageReceived(Message message) {
        try {
            Transportable content = message.getContent();
            if (content instanceof QueryReport) {
                this.handleQueryReport((QueryReport)content);
            } else if (content instanceof SalesReport) {
                this.handleSalesReport((SalesReport)content);
            } else if (content instanceof SimulationStatus) {
                this.handleSimulationStatus((SimulationStatus)content);
            } else if (content instanceof RetailCatalog) {
                this.handleRetailCatalog((RetailCatalog)content);
            } else if (content instanceof AdvertiserInfo) {
                this.handleAdvertiserInfo((AdvertiserInfo)content);
            }
        }
        catch (NullPointerException e) {
            this.log.log(Level.SEVERE, "Null Message received.");
            return;
        }
    }

    private void handleSimulationStatus(SimulationStatus simulationStatus) {
        this.sendBidAndAds();
    }

    private void handleRetailCatalog(RetailCatalog retailCatalog) {
        this.retailCatalog = retailCatalog;
        this.generateQuerySpace();
    }

    private void handleAdvertiserInfo(AdvertiserInfo advertiserInfo) {
        this.publisherAddress = advertiserInfo.getPublisherId();
    }

    private void handleQueryReport(QueryReport queryReport) {
        int i = 0;
        while (i < this.queries.length) {
            Query query = this.queries[i];
            int index = queryReport.indexForEntry(query);
            if (index >= 0) {
                double[] arrd = this.impressions;
                int n = i;
                arrd[n] = arrd[n] + (double)queryReport.getImpressions(index);
                double[] arrd2 = this.clicks;
                int n2 = i;
                arrd2[n2] = arrd2[n2] + (double)queryReport.getClicks(index);
            }
            ++i;
        }
    }

    private void handleSalesReport(SalesReport salesReport) {
        int i = 0;
        while (i < this.queries.length) {
            Query query = this.queries[i];
            int index = salesReport.indexForEntry(query);
            if (index >= 0) {
                double[] arrd = this.conversions;
                int n = i;
                arrd[n] = arrd[n] + (double)salesReport.getConversions(index);
                double[] arrd2 = this.values;
                int n2 = i;
                arrd2[n2] = arrd2[n2] + salesReport.getRevenue(index);
            }
            ++i;
        }
    }

    @Override
    protected void simulationSetup() {
        this.bidBundle = new BidBundle();
        this.log = Logger.getLogger(String.valueOf(DummyAdvertiser.class.getName()) + '.' + this.getName());
        this.log.fine("dummy " + this.getName() + " simulationSetup");
    }

    @Override
    protected void simulationFinished() {
        this.bidBundle = null;
    }

    protected void sendBidAndAds() {
        this.bidBundle = new BidBundle();
        Ad ad = new Ad(null);
        int i = 0;
        while (i < this.queries.length) {
            this.bidBundle.addQuery(this.queries[i], this.values[i] / this.clicks[i], ad);
            ++i;
        }
        if (this.bidBundle != null && this.publisherAddress != null) {
            this.sendMessage(this.publisherAddress, this.bidBundle);
        }
    }

    private void generateQuerySpace() {
        if (this.retailCatalog != null && this.queries == null) {
            HashSet<Query> queryList = new HashSet<Query>();
            for (Product product : this.retailCatalog) {
                Query f0 = new Query();
                Query f1_manufacturer = new Query(product.getManufacturer(), null);
                Query f1_component = new Query(null, product.getComponent());
                Query f2 = new Query(product.getManufacturer(), product.getComponent());
                queryList.add(f0);
                queryList.add(f1_manufacturer);
                queryList.add(f1_component);
                queryList.add(f2);
            }
            this.queries = queryList.toArray(new Query[0]);
            this.impressions = new double[this.queries.length];
            this.clicks = new double[this.queries.length];
            this.conversions = new double[this.queries.length];
            this.values = new double[this.queries.length];
            int i = 0;
            while (i < this.queries.length) {
                this.impressions[i] = 100.0;
                this.clicks[i] = 9.0;
                this.conversions[i] = 1.0;
                this.values[i] = this.retailCatalog.getSalesProfit(0);
                ++i;
            }
        }
    }
}

