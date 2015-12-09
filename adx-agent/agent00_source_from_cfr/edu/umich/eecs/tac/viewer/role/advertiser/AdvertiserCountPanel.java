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
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import se.sics.tasim.viewer.TickListener;

public class AdvertiserCountPanel
extends JPanel {
    private int agent;
    private String advertiser;
    private int currentDay;
    private XYSeries impressions;
    private XYSeries clicks;
    private XYSeries conversions;
    private boolean advertiserBorder;
    private Color legendColor;

    public AdvertiserCountPanel(int agent, String advertiser, TACAASimulationPanel simulationPanel, boolean advertiserBorder, Color legendColor) {
        this.agent = agent;
        this.advertiser = advertiser;
        this.advertiserBorder = advertiserBorder;
        this.legendColor = legendColor;
        this.initialize();
        this.currentDay = 0;
        simulationPanel.addViewListener(new DataUpdateListener(this, null));
        simulationPanel.addTickListener(new DayListener());
    }

    private void initialize() {
        this.setLayout(new GridLayout(3, 1));
        this.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        this.add((Component)new ChartPanel(this.createImpressionsChart()));
        this.add((Component)new ChartPanel(this.createClicksChart()));
        this.add((Component)new ChartPanel(this.createConversionsChart()));
        if (this.advertiserBorder) {
            this.setBorder(BorderFactory.createTitledBorder(this.advertiser));
        } else {
            this.setBorder(BorderFactory.createTitledBorder("Impressions, Clicks and Conversions"));
        }
    }

    private JFreeChart createConversionsChart() {
        this.conversions = new XYSeries((Comparable)((Object)"Convs"));
        return ViewerChartFactory.createDaySeriesChartWithColor("Convs", (XYDataset)new XYSeriesCollection(this.conversions), this.legendColor);
    }

    private JFreeChart createClicksChart() {
        this.clicks = new XYSeries((Comparable)((Object)"Clicks"));
        return ViewerChartFactory.createDaySeriesChartWithColor("Clicks", (XYDataset)new XYSeriesCollection(this.clicks), this.legendColor);
    }

    private JFreeChart createImpressionsChart() {
        this.impressions = new XYSeries((Comparable)((Object)"Imprs"));
        return ViewerChartFactory.createDaySeriesChartWithColor("Imprs", (XYDataset)new XYSeriesCollection(this.impressions), this.legendColor);
    }

    public int getAgent() {
        return this.agent;
    }

    public String getAdvertiser() {
        return this.advertiser;
    }

    protected void addImpressions(int impressions) {
        this.impressions.addOrUpdate((double)this.currentDay, (double)impressions);
    }

    protected void addClicks(int clicks) {
        this.clicks.addOrUpdate((double)this.currentDay, (double)clicks);
    }

    protected void addConversions(int conversions) {
        this.conversions.addOrUpdate((double)this.currentDay, (double)conversions);
    }

    protected void tick(long serverTime) {
    }

    protected void simulationTick(long serverTime, int simulationDate) {
        this.currentDay = simulationDate;
    }

    private class DataUpdateListener
    extends ViewAdaptor {
        final /* synthetic */ AdvertiserCountPanel this$0;

        private DataUpdateListener(AdvertiserCountPanel advertiserCountPanel) {
            this.this$0 = advertiserCountPanel;
        }

        @Override
        public void dataUpdated(final int agent, final int type, final int value) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    if (agent == DataUpdateListener.this.this$0.agent) {
                        switch (type) {
                            case 301: {
                                DataUpdateListener.this.this$0.addImpressions(value);
                                break;
                            }
                            case 302: {
                                DataUpdateListener.this.this$0.addClicks(value);
                                break;
                            }
                            case 303: {
                                DataUpdateListener.this.this$0.addConversions(value);
                            }
                        }
                    }
                }
            });
        }

        /* synthetic */ DataUpdateListener(AdvertiserCountPanel advertiserCountPanel, DataUpdateListener dataUpdateListener) {
            DataUpdateListener dataUpdateListener2;
            dataUpdateListener2(advertiserCountPanel);
        }

    }

    protected class DayListener
    implements TickListener {
        protected DayListener() {
        }

        @Override
        public void tick(long serverTime) {
            AdvertiserCountPanel.this.tick(serverTime);
        }

        @Override
        public void simulationTick(long serverTime, int simulationDate) {
            AdvertiserCountPanel.this.simulationTick(serverTime, simulationDate);
        }
    }

}

