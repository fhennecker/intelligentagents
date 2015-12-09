/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.logviewer.gui.advertiser;

import edu.umich.eecs.tac.logviewer.gui.PositiveBoundedRangeModel;
import edu.umich.eecs.tac.logviewer.info.Advertiser;
import edu.umich.eecs.tac.logviewer.util.VisualizerUtils;
import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.BidBundle;
import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.SalesReport;
import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class QueryBidPanel {
    JPanel mainPane;
    JLabel bidLabel;
    JLabel reserveLabel;
    JLabel adLabel;
    JLabel cpcLabel;
    JLabel vpcLabel;
    JLabel posLabel;
    public static final String BID_STRING = "Bid: ";
    public static final String RESERVE_STRING = "Spend Limit: ";
    public static final String AD_STRING = "Ad: ";
    public static final String CPC_STRING = "Avg. CPC: ";
    public static final String VPC_STRING = "Avg. VPC: ";
    public static final String POS_STRING = "Avg. Position: ";
    public static final String AD_NULL = "NULL";
    public static final DecimalFormat dFormat = new DecimalFormat("$#0.000");
    public static final DecimalFormat pFormat = new DecimalFormat("#0.###");
    double[] bid;
    double[] reserve;
    Ad[] ad;
    double[] cpc;
    double[] vpc;
    double[] pos;
    Query[] querySpace;
    Query query;
    Advertiser advertiser;
    PositiveBoundedRangeModel dayModel;

    public QueryBidPanel(Query query, Advertiser advertiser, PositiveBoundedRangeModel dm, int numDays, Query[] querySpace) {
        this.query = query;
        this.advertiser = advertiser;
        this.dayModel = dm;
        this.bid = new double[numDays];
        this.reserve = new double[numDays];
        this.ad = new Ad[numDays];
        this.cpc = new double[numDays];
        this.vpc = new double[numDays];
        this.pos = new double[numDays];
        this.querySpace = querySpace;
        if (this.dayModel != null) {
            this.dayModel.addChangeListener(new ChangeListener(){

                @Override
                public void stateChanged(ChangeEvent ce) {
                    QueryBidPanel.this.updateMePlz();
                }
            });
        }
        this.applyData();
        this.mainPane = new JPanel();
        this.mainPane.setLayout(new BoxLayout(this.mainPane, 1));
        this.mainPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), query.toString()));
        this.bidLabel = new JLabel();
        this.reserveLabel = new JLabel();
        this.adLabel = new JLabel();
        this.cpcLabel = new JLabel();
        this.vpcLabel = new JLabel();
        this.posLabel = new JLabel();
        this.mainPane.add(this.bidLabel);
        this.mainPane.add(this.reserveLabel);
        this.mainPane.add(this.adLabel);
        this.mainPane.add(this.cpcLabel);
        this.mainPane.add(this.vpcLabel);
        this.mainPane.add(this.posLabel);
        this.updateMePlz();
    }

    private void applyData() {
        this.bid[0] = Double.NaN;
        this.reserve[0] = Double.NaN;
        this.ad[0] = null;
        this.cpc[0] = Double.NaN;
        this.vpc[0] = Double.NaN;
        this.pos[0] = Double.NaN;
        int i = 0;
        while (i < this.bid.length - 1) {
            BidBundle current = this.advertiser.getBidBundle(i);
            QueryReport report = this.advertiser.getQueryReport(i + 2);
            SalesReport s_report = this.advertiser.getSalesReport(i + 2);
            if (current != null) {
                this.bid[i + 1] = current.getBid(this.query);
                this.reserve[i + 1] = current.getDailyLimit(this.query);
                this.ad[i + 1] = current.getAd(this.query);
            } else {
                this.bid[i + 1] = Double.NaN;
                this.reserve[i + 1] = Double.NaN;
                this.ad[i + 1] = BidBundle.PERSISTENT_AD;
            }
            if (report != null) {
                this.cpc[i + 1] = report.getCPC(this.query);
                this.pos[i + 1] = report.getPosition(this.query);
            } else {
                this.cpc[i + 1] = Double.NaN;
                this.pos[i + 1] = Double.NaN;
            }
            this.vpc[i + 1] = s_report != null && report != null ? (s_report.getRevenue(this.query) - report.getCost(this.query)) / (double)report.getClicks(this.query) : Double.NaN;
            if (i != 0) {
                if (Double.isNaN(this.bid[i + 1]) && Double.isNaN(Double.NaN) || this.bid[i + 1] == Double.NaN || this.bid[i + 1] < 0.0) {
                    this.bid[i + 1] = this.bid[i];
                }
                if (Double.isNaN(this.reserve[i + 1]) && Double.isNaN(Double.NaN) || this.reserve[i + 1] == Double.NaN) {
                    this.reserve[i + 1] = this.reserve[i];
                }
                if (this.ad[i + 1] == BidBundle.PERSISTENT_AD) {
                    this.ad[i + 1] = this.ad[i];
                }
            }
            ++i;
        }
    }

    private void updateMePlz() {
        String s = "" + this.dayModel.getCurrent();
        int day = this.dayModel.getCurrent();
        if (Double.isNaN(this.bid[day])) {
            this.bidLabel.setText("Bid: " + this.bid[day]);
        } else {
            this.bidLabel.setText("Bid: " + dFormat.format(this.bid[day]));
        }
        if (Double.isNaN(this.reserve[day])) {
            this.reserveLabel.setText("Spend Limit: " + this.reserve[day]);
        } else {
            this.reserveLabel.setText("Spend Limit: " + dFormat.format(this.reserve[day]));
        }
        if (this.ad[day] != null) {
            this.adLabel.setText("Ad: " + VisualizerUtils.formatToString(this.ad[day]));
        } else {
            this.adLabel.setText("Ad: NULL");
        }
        if (Double.isNaN(this.cpc[day])) {
            this.cpcLabel.setText("Avg. CPC: " + this.cpc[day]);
        } else {
            this.cpcLabel.setText("Avg. CPC: " + dFormat.format(this.cpc[day]));
        }
        if (Double.isNaN(this.vpc[day])) {
            this.vpcLabel.setText("Avg. VPC: " + this.vpc[day]);
        } else {
            this.vpcLabel.setText("Avg. VPC: " + dFormat.format(this.vpc[day]));
        }
        if (Double.isNaN(this.pos[day])) {
            this.posLabel.setText("Avg. Position: " + this.pos[day]);
        } else {
            this.posLabel.setText("Avg. Position: " + pFormat.format(this.pos[day]));
        }
    }

    public Component getMainPane() {
        return this.mainPane;
    }

    private boolean validAd(Ad ad) {
        if (ad.isGeneric()) {
            return true;
        }
        String m1 = ad.getProduct().getManufacturer();
        String c1 = ad.getProduct().getComponent();
        int i = 0;
        while (i < this.querySpace.length) {
            Query q = this.querySpace[i];
            String m2 = q.getManufacturer();
            String c2 = q.getComponent();
            if (m1.equals(m2) && c1.equals(c2)) {
                return true;
            }
            ++i;
        }
        return false;
    }

}

