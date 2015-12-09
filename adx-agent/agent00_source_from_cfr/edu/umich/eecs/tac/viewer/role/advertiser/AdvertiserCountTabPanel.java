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
import se.sics.tasim.viewer.TickListener;

public class AdvertiserCountTabPanel
extends SimulationTabPanel {
    Map<Integer, String> agents = new HashMap<Integer, String>();
    private int currentDay;
    private XYSeriesCollection impressions;
    private XYSeriesCollection clicks;
    private XYSeriesCollection conversions;
    private final Map<String, XYSeries> impressionsMap = new HashMap<String, XYSeries>();
    private final Map<String, XYSeries> clicksMap = new HashMap<String, XYSeries>();
    private final Map<String, XYSeries> conversionsMap = new HashMap<String, XYSeries>();

    public AdvertiserCountTabPanel(TACAASimulationPanel simulationPanel) {
        super(simulationPanel);
        simulationPanel.addViewListener(new DataUpdateListener(this, null));
        simulationPanel.addTickListener(new DayListener());
        this.initialize();
    }

    private void initialize() {
        this.setLayout(new GridLayout(3, 1));
        this.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        this.add((Component)new ChartPanel(this.createImpressionsChart()));
        this.add((Component)new ChartPanel(this.createClicksChart()));
        this.add((Component)new ChartPanel(this.createConversionsChart()));
        this.setBorder(BorderFactory.createTitledBorder("Counts"));
    }

    private JFreeChart createConversionsChart() {
        this.conversions = new XYSeriesCollection();
        return ViewerChartFactory.createDaySeriesChartWithColors("Convs", (XYDataset)this.conversions, true);
    }

    private JFreeChart createClicksChart() {
        this.clicks = new XYSeriesCollection();
        return ViewerChartFactory.createDaySeriesChartWithColors("Clicks", (XYDataset)this.clicks, false);
    }

    private JFreeChart createImpressionsChart() {
        this.impressions = new XYSeriesCollection();
        return ViewerChartFactory.createDaySeriesChartWithColors("Imprs", (XYDataset)this.impressions, false);
    }

    protected void addImpressions(String advertiser, int impressions) {
        this.impressionsMap.get(advertiser).addOrUpdate((double)this.currentDay, (double)impressions);
    }

    protected void addClicks(String advertiser, int clicks) {
        this.clicksMap.get(advertiser).addOrUpdate((double)this.currentDay, (double)clicks);
    }

    protected void addConversions(String advertiser, int conversions) {
        this.conversionsMap.get(advertiser).addOrUpdate((double)this.currentDay, (double)conversions);
    }

    protected void tick(long serverTime) {
    }

    protected void simulationTick(long serverTime, int simulationDate) {
        this.currentDay = simulationDate;
    }

    private void handleParticipant(int agent, int role, String name, int participantID) {
        if (!this.agents.containsKey(agent) && role == 1) {
            this.agents.put(agent, name);
            XYSeries impressionsSeries = new XYSeries((Comparable)((Object)name));
            XYSeries clicksSeries = new XYSeries((Comparable)((Object)name));
            XYSeries conversionsSeries = new XYSeries((Comparable)((Object)name));
            this.impressionsMap.put(name, impressionsSeries);
            this.impressions.addSeries(impressionsSeries);
            this.clicksMap.put(name, clicksSeries);
            this.clicks.addSeries(clicksSeries);
            this.conversionsMap.put(name, conversionsSeries);
            this.conversions.addSeries(conversionsSeries);
        }
    }

    private class DataUpdateListener
    extends ViewAdaptor {
        final /* synthetic */ AdvertiserCountTabPanel this$0;

        private DataUpdateListener(AdvertiserCountTabPanel advertiserCountTabPanel) {
            this.this$0 = advertiserCountTabPanel;
        }

        @Override
        public void dataUpdated(final int agent, final int type, final int value) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    String agentAddress = DataUpdateListener.access$1((DataUpdateListener)DataUpdateListener.this).agents.get(agent);
                    if (agentAddress != null) {
                        switch (type) {
                            case 301: {
                                DataUpdateListener.this.this$0.addImpressions(agentAddress, value);
                                break;
                            }
                            case 302: {
                                DataUpdateListener.this.this$0.addClicks(agentAddress, value);
                                break;
                            }
                            case 303: {
                                DataUpdateListener.this.this$0.addConversions(agentAddress, value);
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

        /* synthetic */ DataUpdateListener(AdvertiserCountTabPanel advertiserCountTabPanel, DataUpdateListener dataUpdateListener) {
            DataUpdateListener dataUpdateListener2;
            dataUpdateListener2(advertiserCountTabPanel);
        }

    }

    protected class DayListener
    implements TickListener {
        protected DayListener() {
        }

        @Override
        public void tick(long serverTime) {
            AdvertiserCountTabPanel.this.tick(serverTime);
        }

        @Override
        public void simulationTick(long serverTime, int simulationDate) {
            AdvertiserCountTabPanel.this.simulationTick(serverTime, simulationDate);
        }
    }

}

