/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.logviewer.gui.advertiser;

import edu.umich.eecs.tac.logviewer.gui.PositiveBoundedRangeModel;
import edu.umich.eecs.tac.logviewer.info.Advertiser;
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

public class QueryUserInteractionPanel {
    JPanel mainPane;
    JLabel impLabel;
    JLabel clicksLabel;
    JLabel convLabel;
    JLabel ctrLabel;
    JLabel convRateLabel;
    public static final String IMPRESSIONS_STRING = "Impressions: ";
    public static final String CLICKS_STRING = "Clicks: ";
    public static final String CONVERSIONS_STRING = "Conversions: ";
    public static final String CTR_STRING = "CTR: ";
    public static final String CONV_RATE_STRING = "Conv. Rate: ";
    public static final DecimalFormat dFormat = new DecimalFormat("##.##%");
    Query query;
    Advertiser advertiser;
    PositiveBoundedRangeModel dayModel;

    public QueryUserInteractionPanel(Query query, Advertiser advertiser, PositiveBoundedRangeModel dm) {
        this.query = query;
        this.advertiser = advertiser;
        this.dayModel = dm;
        if (this.dayModel != null) {
            this.dayModel.addChangeListener(new ChangeListener(){

                @Override
                public void stateChanged(ChangeEvent ce) {
                    QueryUserInteractionPanel.this.updateMePlz();
                }
            });
        }
        this.mainPane = new JPanel();
        this.mainPane.setLayout(new BoxLayout(this.mainPane, 1));
        this.mainPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), query.toString()));
        this.impLabel = new JLabel();
        this.clicksLabel = new JLabel();
        this.convLabel = new JLabel();
        this.ctrLabel = new JLabel();
        this.convRateLabel = new JLabel();
        this.mainPane.add(this.impLabel);
        this.mainPane.add(this.clicksLabel);
        this.mainPane.add(this.convLabel);
        this.mainPane.add(this.ctrLabel);
        this.mainPane.add(this.convRateLabel);
        this.updateMePlz();
    }

    private void updateMePlz() {
        int current = this.dayModel.getCurrent();
        QueryReport q_report = this.advertiser.getQueryReport(current + 1);
        SalesReport s_report = this.advertiser.getSalesReport(current + 1);
        if (q_report == null || s_report == null) {
            this.setDefaultText();
        } else {
            int impressions = q_report.getImpressions(this.query);
            int clicks = q_report.getClicks(this.query);
            int conversions = s_report.getConversions(this.query);
            this.impLabel.setText("Impressions: " + q_report.getImpressions(this.query));
            this.clicksLabel.setText("Clicks: " + q_report.getClicks(this.query));
            this.convLabel.setText("Conversions: " + s_report.getConversions(this.query));
            this.ctrLabel.setText("CTR: " + dFormat.format(this.calcCTR(impressions, clicks)));
            this.convRateLabel.setText("Conv. Rate: " + dFormat.format(this.calcConvRate(conversions, clicks)));
        }
    }

    private void setDefaultText() {
        this.impLabel.setText("Impressions: 0");
        this.clicksLabel.setText("Clicks: 0");
        this.convLabel.setText("Conversions: 0");
        this.ctrLabel.setText("CTR: 0.0%");
        this.convRateLabel.setText("Conv. Rate: 0.0%");
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

    public Component getMainPane() {
        return this.mainPane;
    }

}

