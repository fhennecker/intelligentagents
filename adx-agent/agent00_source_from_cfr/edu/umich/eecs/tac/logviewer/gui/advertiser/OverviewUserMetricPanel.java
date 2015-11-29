/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.logviewer.gui.advertiser;

import edu.umich.eecs.tac.logviewer.gui.PositiveBoundedRangeModel;
import edu.umich.eecs.tac.logviewer.gui.UpdatablePanel;
import edu.umich.eecs.tac.logviewer.info.Advertiser;
import edu.umich.eecs.tac.logviewer.info.GameInfo;
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

public class OverviewUserMetricPanel
extends UpdatablePanel {
    JLabel impLabel;
    JLabel clicksLabel;
    JLabel convLabel;
    JLabel ctrLabel;
    JLabel convRateLabel;
    JLabel capAvLabel;
    public static final String IMPRESSIONS_STRING = "Total Impressions: ";
    public static final String CLICKS_STRING = "Total Clicks: ";
    public static final String CONVERSIONS_STRING = "Total Conversions: ";
    public static final String CAPACITY_AVAIL_STRING = "Capacity Available:";
    public static final String CTR_STRING = "CTR: ";
    public static final String CONV_RATE_STRING = "Conv. Rate: ";
    public static final DecimalFormat dFormat = new DecimalFormat("###.##%");
    Query[] querySpace;
    Advertiser advertiser;
    int window;

    public OverviewUserMetricPanel(Advertiser advertiser, PositiveBoundedRangeModel dm, GameInfo gameInfo) {
        super(dm);
        this.advertiser = advertiser;
        this.querySpace = gameInfo.getQuerySpace().toArray(new Query[0]);
        this.window = advertiser.getDistributionWindow();
        this.mainPane.setLayout(new BoxLayout(this.mainPane, 1));
        this.mainPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "User Metrics"));
        this.impLabel = new JLabel();
        this.clicksLabel = new JLabel();
        this.convLabel = new JLabel();
        this.ctrLabel = new JLabel();
        this.convRateLabel = new JLabel();
        this.capAvLabel = new JLabel();
        this.mainPane.add(this.impLabel);
        this.mainPane.add(this.clicksLabel);
        this.mainPane.add(this.convLabel);
        this.mainPane.add(this.ctrLabel);
        this.mainPane.add(this.convRateLabel);
        this.updateMePlz();
    }

    @Override
    protected void updateMePlz() {
        int current = this.dayModel.getCurrent();
        QueryReport q_report = this.advertiser.getQueryReport(current + 1);
        SalesReport s_report = this.advertiser.getSalesReport(current + 1);
        if (q_report == null || s_report == null) {
            this.setDefaultText();
        } else {
            int impressions = 0;
            int clicks = 0;
            int conversions = 0;
            int i = 0;
            while (i < this.querySpace.length) {
                impressions += q_report.getImpressions(this.querySpace[i]);
                clicks += q_report.getClicks(this.querySpace[i]);
                conversions += s_report.getConversions(this.querySpace[i]);
                ++i;
            }
            this.impLabel.setText("Total Impressions: " + impressions);
            this.clicksLabel.setText("Total Clicks: " + clicks);
            this.convLabel.setText("Total Conversions: " + conversions);
            this.ctrLabel.setText("CTR: " + dFormat.format(this.calcCTR(impressions, clicks)));
            this.convRateLabel.setText("Conv. Rate: " + dFormat.format(this.calcConvRate(conversions, clicks)));
            int c = 0;
            int i2 = 0;
            while (i2 < this.window) {
                if (current + 1 - i2 >= 1) {
                    s_report = this.advertiser.getSalesReport(current + 1 - i2);
                    int j = 0;
                    while (j < this.querySpace.length) {
                        c = s_report.getConversions(this.querySpace[j]);
                        ++j;
                    }
                }
                ++i2;
            }
            this.capAvLabel.setText("Capacity Available:" + dFormat.format(this.calcCapacityAvail(c, this.advertiser.getDistributionCapacity())));
        }
    }

    private void setDefaultText() {
        this.impLabel.setText("Total Impressions: 0");
        this.clicksLabel.setText("Total Clicks: 0");
        this.convLabel.setText("Total Conversions: 0");
        this.ctrLabel.setText("CTR: 0.0%");
        this.convRateLabel.setText("Conv. Rate: 0.0%");
        this.capAvLabel.setText("Capacity Available:100.0%");
    }

    protected double calcCTR(int impressions, int clicks) {
        if (impressions > 0) {
            return (double)clicks / (double)impressions;
        }
        return 0.0;
    }

    protected double calcConvRate(int conversions, int clicks) {
        if (clicks > 0) {
            return (double)conversions / (double)clicks;
        }
        return 0.0;
    }

    protected double calcCapacityAvail(int conversions, int capacity) {
        return ((double)capacity - (double)conversions) / (double)capacity;
    }
}

