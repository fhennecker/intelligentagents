/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.logviewer.gui.advertiser;

import edu.umich.eecs.tac.logviewer.gui.PositiveBoundedRangeModel;
import edu.umich.eecs.tac.logviewer.gui.UpdatablePanel;
import edu.umich.eecs.tac.logviewer.info.Advertiser;
import edu.umich.eecs.tac.logviewer.info.GameInfo;
import edu.umich.eecs.tac.props.BidBundle;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.SalesReport;
import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.text.DecimalFormat;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class OverviewBidPanel
extends UpdatablePanel {
    JLabel bidLabel;
    JLabel reserveLabel;
    JLabel adLabel;
    JLabel cpcLabel;
    JLabel vpcLabel;
    JLabel posLabel;
    public static final String RESERVE_STRING = "Global Spend Limit: ";
    public static final String CPC_STRING = "Total Avg. CPC: ";
    public static final String VPC_STRING = "Total Avg. VPC: ";
    public static final String POS_STRING = "Avg. Placed Position: ";
    public static final String AD_NULL = "NULL";
    public static final DecimalFormat dFormat = new DecimalFormat("$#0.000");
    public static final DecimalFormat pFormat = new DecimalFormat("#0.###");
    Query[] querySpace;
    Advertiser advertiser;
    double[] reserve;
    double[] cpc;
    double[] vpc;
    double[] pos;

    public OverviewBidPanel(Advertiser advertiser, PositiveBoundedRangeModel dm, GameInfo gameInfo, int numDays) {
        super(dm);
        this.advertiser = advertiser;
        this.querySpace = gameInfo.getQuerySpace().toArray(new Query[0]);
        this.reserve = new double[numDays];
        this.cpc = new double[numDays];
        this.vpc = new double[numDays];
        this.pos = new double[numDays];
        this.mainPane.setLayout(new BoxLayout(this.mainPane, 1));
        this.mainPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Bid Metrics"));
        this.reserveLabel = new JLabel();
        this.cpcLabel = new JLabel();
        this.vpcLabel = new JLabel();
        this.posLabel = new JLabel();
        this.mainPane.add(this.reserveLabel);
        this.mainPane.add(this.cpcLabel);
        this.mainPane.add(this.vpcLabel);
        this.mainPane.add(this.posLabel);
        this.applyData();
        this.updateMePlz();
    }

    private void applyData() {
        this.reserve[0] = Double.NaN;
        this.cpc[0] = Double.NaN;
        this.vpc[0] = Double.NaN;
        this.pos[0] = Double.NaN;
        int i = 0;
        while (i < this.reserve.length - 1) {
            BidBundle current = this.advertiser.getBidBundle(i);
            QueryReport report = this.advertiser.getQueryReport(i + 2);
            SalesReport s_report = this.advertiser.getSalesReport(i + 2);
            this.reserve[i + 1] = current != null ? current.getCampaignDailySpendLimit() : Double.NaN;
            if (report != null) {
                double cost = 0.0;
                int clicks = 0;
                int count = 0;
                double position = 0.0;
                int j = 0;
                while (j < this.querySpace.length) {
                    cost += report.getCost(this.querySpace[j]);
                    clicks += report.getClicks(this.querySpace[j]);
                    double curPosition = report.getPosition(this.querySpace[j]);
                    if (!Double.isNaN(curPosition)) {
                        position += curPosition;
                        ++count;
                    }
                    ++j;
                }
                this.cpc[i + 1] = cost / (double)clicks;
                this.pos[i + 1] = position / (double)count;
            } else {
                this.cpc[i + 1] = Double.NaN;
                this.pos[i + 1] = Double.NaN;
            }
            if (s_report != null && report != null) {
                double revenue = 0.0;
                double cost = 0.0;
                int clicks = 0;
                int j = 0;
                while (j < this.querySpace.length) {
                    revenue += s_report.getRevenue(this.querySpace[j]);
                    cost += report.getCost(this.querySpace[j]);
                    clicks += report.getClicks(this.querySpace[j]);
                    ++j;
                }
                this.vpc[i + 1] = (revenue - cost) / (double)clicks;
            } else {
                this.vpc[i + 1] = Double.NaN;
            }
            if (i != 0 && (Double.isNaN(this.reserve[i + 1]) && Double.isNaN(Double.NaN) || this.reserve[i + 1] == Double.NaN)) {
                this.reserve[i + 1] = this.reserve[i];
            }
            ++i;
        }
    }

    @Override
    protected void updateMePlz() {
        String s = "" + this.dayModel.getCurrent();
        int day = this.dayModel.getCurrent();
        if (Double.isNaN(this.reserve[day])) {
            this.reserveLabel.setText("Global Spend Limit: " + this.reserve[day]);
        } else {
            this.reserveLabel.setText("Global Spend Limit: " + dFormat.format(this.reserve[day]));
        }
        if (Double.isNaN(this.cpc[day])) {
            this.cpcLabel.setText("Total Avg. CPC: " + this.cpc[day]);
        } else {
            this.cpcLabel.setText("Total Avg. CPC: " + dFormat.format(this.cpc[day]));
        }
        if (Double.isNaN(this.vpc[day])) {
            this.vpcLabel.setText("Total Avg. VPC: " + this.vpc[day]);
        } else {
            this.vpcLabel.setText("Total Avg. VPC: " + dFormat.format(this.vpc[day]));
        }
        if (Double.isNaN(this.pos[day])) {
            this.posLabel.setText("Avg. Placed Position: " + this.pos[day]);
        } else {
            this.posLabel.setText("Avg. Placed Position: " + pFormat.format(this.pos[day]));
        }
    }
}

