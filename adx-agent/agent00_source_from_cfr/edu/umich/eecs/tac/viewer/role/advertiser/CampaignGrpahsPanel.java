/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.jfree.chart.ChartPanel
 *  org.jfree.chart.JFreeChart
 *  org.jfree.data.xy.XYDataItem
 *  org.jfree.data.xy.XYDataset
 *  org.jfree.data.xy.XYSeries
 *  org.jfree.data.xy.XYSeriesCollection
 */
package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.ViewerChartFactory;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.viewer.TickListener;
import tau.tac.adx.demand.CampaignStats;
import tau.tac.adx.report.demand.AdNetworkDailyNotification;
import tau.tac.adx.report.demand.CampaignReport;
import tau.tac.adx.report.demand.CampaignReportEntry;
import tau.tac.adx.report.demand.CampaignReportKey;

public class CampaignGrpahsPanel
extends JPanel {
    private static final double ERR_A = 4.08577;
    private static final double ERR_B = 3.08577;
    private final int agent;
    private final String advertiser;
    private final Set<AdNetworkDailyNotification> campaigns;
    private final boolean advertiserBorder;
    private final Map<Integer, XYSeries> campaignSeries;
    private int counter;
    private int campaignId = 0;
    private int currentDay;
    private final CampaignReportKey key;
    private XYSeries reachSeries;
    private final long expectedImpressionReach;
    private double err;
    private XYSeries maxSeries;

    public CampaignGrpahsPanel(int agent, String advertiser, TACAASimulationPanel simulationPanel, boolean advertiserBorder, int campaignId, long expectedImpressionReach) {
        this.agent = agent;
        this.advertiser = advertiser;
        this.advertiserBorder = advertiserBorder;
        this.campaignSeries = new HashMap<Integer, XYSeries>();
        this.campaignId = campaignId;
        this.key = new CampaignReportKey(campaignId);
        this.expectedImpressionReach = expectedImpressionReach;
        this.initialize();
        simulationPanel.addViewListener(new DataUpdateListener(this, null));
        this.campaigns = new HashSet<AdNetworkDailyNotification>();
        simulationPanel.addTickListener(new DayListener());
    }

    private void initialize() {
        this.setLayout(new GridLayout(1, 1));
        this.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
    }

    private void createGraph(XYSeries reachSeries) {
        XYSeriesCollection seriescollection = new XYSeriesCollection(reachSeries);
        this.maxSeries = new XYSeries((Comparable)((Object)"Total"));
        int i = 1;
        while (i <= 1000) {
            double err = this.calcEffectiveReachRatio(this.expectedImpressionReach * (long)i / 1000, this.expectedImpressionReach);
            this.maxSeries.add((double)(this.expectedImpressionReach * (long)i / 1000), err);
            ++i;
        }
        seriescollection.addSeries(this.maxSeries);
        JFreeChart chart = ViewerChartFactory.createDifferenceChart(this.advertiserBorder ? null : this.advertiser, (XYDataset)seriescollection, "Reach Count", "Reach percentage");
        ChartPanel chartpanel = new ChartPanel(chart, false);
        chartpanel.setMouseZoomable(true, false);
        this.add((Component)chartpanel);
        this.repaint();
    }

    public int getAgent() {
        return this.agent;
    }

    public String getAdvertiser() {
        return this.advertiser;
    }

    protected void updateCampaigns(CampaignReport campaignReport) {
        double targetedImps;
        CampaignStats campaignStats;
        CampaignReportEntry campaignReportEntry = (CampaignReportEntry)campaignReport.getEntry(this.key);
        if (campaignReportEntry == null) {
            return;
        }
        if (!this.campaignSeries.containsKey(this.campaignId)) {
            String string = "Campaign " + this.campaignId;
            this.reachSeries = new XYSeries((Comparable)((Object)string));
            this.campaignSeries.put(this.campaignId, this.reachSeries);
            this.createGraph(this.reachSeries);
        }
        if ((targetedImps = (campaignStats = campaignReportEntry.getCampaignStats()).getTargetedImps()) != 0.0) {
            int i = 1;
            while (i <= 100) {
                double index = targetedImps * (double)i / 100.0;
                double err = this.calcEffectiveReachRatio(index, this.expectedImpressionReach);
                this.reachSeries.add(index, err);
                ++i;
            }
            i = 1;
            while (i <= 1000) {
                long index = this.expectedImpressionReach * (long)i / 1000;
                if ((double)index < targetedImps) {
                    this.maxSeries.remove((Number)index);
                    this.maxSeries.add((double)index, 0.0);
                }
                ++i;
            }
            this.repaint();
        }
    }

    private double calcEffectiveReachRatio(double currentReach, double expectedReach) {
        return 0.48950381445847413 * (Math.atan(4.08577 * currentReach / expectedReach - 3.08577) - Math.atan(-3.08577));
    }

    protected void simulationTick(long serverTime, int simulationDate) {
        this.currentDay = simulationDate;
    }

    private class DataUpdateListener
    extends ViewAdaptor {
        final /* synthetic */ CampaignGrpahsPanel this$0;

        private DataUpdateListener(CampaignGrpahsPanel campaignGrpahsPanel) {
            this.this$0 = campaignGrpahsPanel;
        }

        @Override
        public void dataUpdated(final int agentId, final int type, final Transportable value) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    if (agentId == DataUpdateListener.this.this$0.agent) {
                        switch (type) {
                            case 405: {
                                DataUpdateListener.this.this$0.updateCampaigns((CampaignReport)value);
                            }
                        }
                    }
                }
            });
        }

        /* synthetic */ DataUpdateListener(CampaignGrpahsPanel campaignGrpahsPanel, DataUpdateListener dataUpdateListener) {
            DataUpdateListener dataUpdateListener2;
            dataUpdateListener2(campaignGrpahsPanel);
        }

    }

    protected class DayListener
    implements TickListener {
        protected DayListener() {
        }

        @Override
        public void simulationTick(long serverTime, int simulationDate) {
            CampaignGrpahsPanel.this.simulationTick(serverTime, simulationDate);
        }

        @Override
        public void tick(long serverTime) {
        }
    }

}

