/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.SalesReport;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.viewer.ViewListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import se.sics.isl.transport.Transportable;

public class AdvertiserQueryInfoPanel
extends JPanel {
    private final int agent;
    private final String advertiser;
    private final Query query;
    private int impressions;
    private int clicks;
    private int conversions;
    private double revenue;
    private double cost;
    private JLabel ctrLabel;
    private JLabel convRateLabel;
    private JLabel cpcLabel;
    private JLabel cpmLabel;
    private JLabel vpcLabel;
    private JLabel roiLabel;

    public AdvertiserQueryInfoPanel(int agent, String advertiser, Query query, TACAASimulationPanel simulationPanel) {
        this.agent = agent;
        this.advertiser = advertiser;
        this.query = query;
        this.initialize();
        simulationPanel.addViewListener(new DataUpdateListener(this, null));
    }

    private void initialize() {
        this.setLayout(new GridLayout(3, 4));
        this.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        this.ctrLabel = new JLabel("---");
        this.add(new JLabel("CTR:"));
        this.add(this.ctrLabel);
        this.cpcLabel = new JLabel("---");
        this.add(new JLabel("CPC:"));
        this.add(this.cpcLabel);
        this.convRateLabel = new JLabel("---");
        this.add(new JLabel("Conv. Rate:"));
        this.add(this.convRateLabel);
        this.vpcLabel = new JLabel("---");
        this.add(new JLabel("VPC:"));
        this.add(this.vpcLabel);
        this.cpmLabel = new JLabel("---");
        this.add(new JLabel("CPM:"));
        this.add(this.cpmLabel);
        this.roiLabel = new JLabel("---");
        this.add(new JLabel("ROI:"));
        this.add(this.roiLabel);
        this.setBorder(BorderFactory.createTitledBorder("Rate Metrics"));
    }

    public int getAgent() {
        return this.agent;
    }

    public String getAdvertiser() {
        return this.advertiser;
    }

    protected void addRevenue(double revenue) {
        this.revenue += revenue;
        this.updateCTR();
        this.updateVPC();
        this.updateROI();
    }

    protected void addCost(double cost) {
        this.cost += cost;
        this.updateCPC();
        this.updateVPC();
        this.updateCPM();
        this.updateROI();
    }

    protected void addImpressions(int impressions) {
        this.impressions += impressions;
        this.updateCTR();
        this.updateCPM();
    }

    protected void addClicks(int clicks) {
        this.clicks += clicks;
        this.updateCTR();
        this.updateConvRate();
        this.updateCPC();
        this.updateVPC();
    }

    protected void addConversions(int conversions) {
        this.conversions += conversions;
        this.updateConvRate();
    }

    protected void updateCTR() {
        if (this.impressions > 0) {
            this.ctrLabel.setText(String.format("%.2f%%", 100.0 * (double)this.clicks / (double)this.impressions));
        } else {
            this.ctrLabel.setText("---");
        }
    }

    protected void updateConvRate() {
        if (this.clicks > 0) {
            this.convRateLabel.setText(String.format("%.2f%%", 100.0 * (double)this.conversions / (double)this.clicks));
        } else {
            this.convRateLabel.setText("---");
        }
    }

    protected void updateCPC() {
        if (this.clicks > 0) {
            this.cpcLabel.setText(String.format("%.2f", this.cost / (double)this.clicks));
        } else {
            this.cpcLabel.setText("---");
        }
    }

    protected void updateCPM() {
        if (this.impressions > 0) {
            this.cpmLabel.setText(String.format("%.2f", this.cost / ((double)this.impressions / 1000.0)));
        } else {
            this.cpmLabel.setText("---");
        }
    }

    protected void updateROI() {
        if (this.cost > 0.0) {
            this.roiLabel.setText(String.format("%.2f%%", 100.0 * (this.revenue - this.cost) / this.cost));
        } else {
            this.roiLabel.setText("---");
        }
    }

    protected void updateVPC() {
        if (this.clicks > 0) {
            this.vpcLabel.setText(String.format("%.2f", (this.revenue - this.cost) / (double)this.clicks));
        } else {
            this.vpcLabel.setText("---");
        }
    }

    private class DataUpdateListener
    extends ViewAdaptor {
        final /* synthetic */ AdvertiserQueryInfoPanel this$0;

        private DataUpdateListener(AdvertiserQueryInfoPanel advertiserQueryInfoPanel) {
            this.this$0 = advertiserQueryInfoPanel;
        }

        @Override
        public void dataUpdated(final int agent, final int type, final Transportable value) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    if (agent == DataUpdateListener.this.this$0.agent) {
                        switch (type) {
                            case 305: {
                                DataUpdateListener.this.handleSalesReport((SalesReport)value);
                                break;
                            }
                            case 304: {
                                DataUpdateListener.this.handleQueryReport((QueryReport)value);
                            }
                        }
                    }
                }
            });
        }

        private void handleQueryReport(QueryReport queryReport) {
            this.this$0.addImpressions(queryReport.getImpressions(this.this$0.query));
            this.this$0.addClicks(queryReport.getClicks(this.this$0.query));
            this.this$0.addCost(queryReport.getCost(this.this$0.query));
        }

        private void handleSalesReport(SalesReport salesReport) {
            this.this$0.addConversions(salesReport.getConversions(this.this$0.query));
            this.this$0.addRevenue(salesReport.getRevenue(this.this$0.query));
        }

        /* synthetic */ DataUpdateListener(AdvertiserQueryInfoPanel advertiserQueryInfoPanel, DataUpdateListener dataUpdateListener) {
            DataUpdateListener dataUpdateListener2;
            dataUpdateListener2(advertiserQueryInfoPanel);
        }

    }

}

