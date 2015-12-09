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
package edu.umich.eecs.tac.viewer.role.adx;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.ViewerChartFactory;
import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.viewer.TickListener;
import tau.tac.adx.report.demand.AdNetworkDailyNotification;

public class AdNetCountTabPanel
extends SimulationTabPanel {
    Map<Integer, String> agents = new HashMap<Integer, String>();
    private int currentDay;
    private XYSeriesCollection targetedImpressions;
    private XYSeriesCollection serviceLevels;
    private XYSeriesCollection qualityRatings;
    private final Map<String, XYSeries> targetedImpressionsMap = new HashMap<String, XYSeries>();
    private final Map<String, XYSeries> serviceLevelMap = new HashMap<String, XYSeries>();
    private final Map<String, XYSeries> qualityRatingsMap = new HashMap<String, XYSeries>();

    public AdNetCountTabPanel(TACAASimulationPanel simulationPanel) {
        super(simulationPanel);
        simulationPanel.addViewListener(new DataUpdateListener(this, null));
        simulationPanel.addTickListener(new DayListener());
        this.initialize();
    }

    private void initialize() {
        this.setLayout(new GridLayout(3, 1));
        this.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        this.add((Component)new ChartPanel(this.createTargetedImpressionsChart()));
        this.add((Component)new ChartPanel(this.createServiceLevelChart()));
        this.add((Component)new ChartPanel(this.createQualityRatingChart()));
        this.setBorder(BorderFactory.createTitledBorder("Counts"));
    }

    private JFreeChart createQualityRatingChart() {
        this.qualityRatings = new XYSeriesCollection();
        return ViewerChartFactory.createDaySeriesChartWithColors("Quality Rating", (XYDataset)this.qualityRatings, true);
    }

    private JFreeChart createServiceLevelChart() {
        this.serviceLevels = new XYSeriesCollection();
        return ViewerChartFactory.createDaySeriesChartWithColors("Service Level", (XYDataset)this.serviceLevels, false);
    }

    private JFreeChart createTargetedImpressionsChart() {
        this.targetedImpressions = new XYSeriesCollection();
        return ViewerChartFactory.createDaySeriesChartWithColors("Impressions", (XYDataset)this.targetedImpressions, false);
    }

    protected void addTargetedImpressions(String advertiser, int impressions) {
        this.targetedImpressionsMap.get(advertiser).addOrUpdate((double)this.currentDay, (double)impressions);
    }

    protected void addServiceLevel(String advertiser, double serviceLevel) {
        this.serviceLevelMap.get(advertiser).addOrUpdate((double)this.currentDay, serviceLevel);
    }

    protected void addQualityRating(String advertiser, double qualityRating) {
        this.qualityRatingsMap.get(advertiser).addOrUpdate((double)this.currentDay, qualityRating);
    }

    protected void tick(long serverTime) {
    }

    protected void simulationTick(long serverTime, int simulationDate) {
        this.currentDay = simulationDate;
    }

    private void handleParticipant(int agent, int role, String name, int participantID) {
        if (!this.agents.containsKey(agent) && role == 5) {
            this.agents.put(agent, name);
            XYSeries targetedImpressionsSeries = new XYSeries((Comparable)((Object)name));
            this.targetedImpressionsMap.put(name, targetedImpressionsSeries);
            this.targetedImpressions.addSeries(targetedImpressionsSeries);
            XYSeries serviceLevelSeries = new XYSeries((Comparable)((Object)name));
            this.serviceLevelMap.put(name, serviceLevelSeries);
            this.serviceLevels.addSeries(serviceLevelSeries);
            XYSeries qualityRatingSeries = new XYSeries((Comparable)((Object)name));
            this.qualityRatingsMap.put(name, qualityRatingSeries);
            this.qualityRatings.addSeries(qualityRatingSeries);
        }
    }

    private class DataUpdateListener
    extends ViewAdaptor {
        final /* synthetic */ AdNetCountTabPanel this$0;

        private DataUpdateListener(AdNetCountTabPanel adNetCountTabPanel) {
            this.this$0 = adNetCountTabPanel;
        }

        @Override
        public void dataUpdated(final int agent, final int type, final int value) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    String agentAddress = DataUpdateListener.access$1((DataUpdateListener)DataUpdateListener.this).agents.get(agent);
                    if (agentAddress != null) {
                        switch (type) {
                            case 407: {
                                DataUpdateListener.this.this$0.addTargetedImpressions(agentAddress, value);
                            }
                        }
                    }
                }
            });
        }

        @Override
        public void dataUpdated(final int agent, final int type, final double value) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    String agentAddress = DataUpdateListener.access$1((DataUpdateListener)DataUpdateListener.this).agents.get(agent);
                    if (agentAddress != null) {
                        switch (type) {
                            case 408: {
                                DataUpdateListener.this.this$0.addQualityRating(agentAddress, value);
                            }
                        }
                    }
                }
            });
        }

        @Override
        public void dataUpdated(final int agent, final int type, final Transportable content) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    String agentAddress = DataUpdateListener.access$1((DataUpdateListener)DataUpdateListener.this).agents.get(agent);
                    if (agentAddress != null) {
                        switch (type) {
                            case 406: {
                                AdNetworkDailyNotification adNetworkDailyNotification = (AdNetworkDailyNotification)content;
                                DataUpdateListener.this.this$0.addServiceLevel(agentAddress, adNetworkDailyNotification.getServiceLevel());
                            }
                        }
                    }
                }
            });
        }

        @Override
        public void participant(final int agent, final int role, final String name, final int participantID) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    DataUpdateListener.this.this$0.handleParticipant(agent, role, name, participantID);
                }
            });
        }

        /* synthetic */ DataUpdateListener(AdNetCountTabPanel adNetCountTabPanel, DataUpdateListener dataUpdateListener) {
            DataUpdateListener dataUpdateListener2;
            dataUpdateListener2(adNetCountTabPanel);
        }

    }

    protected class DayListener
    implements TickListener {
        protected DayListener() {
        }

        @Override
        public void tick(long serverTime) {
            AdNetCountTabPanel.this.tick(serverTime);
        }

        @Override
        public void simulationTick(long serverTime, int simulationDate) {
            AdNetCountTabPanel.this.simulationTick(serverTime, simulationDate);
        }
    }

}

