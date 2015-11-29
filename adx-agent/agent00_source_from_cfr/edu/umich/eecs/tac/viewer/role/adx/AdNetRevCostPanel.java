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

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.ViewerChartFactory;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.HashSet;
import java.util.Set;
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

public class AdNetRevCostPanel
extends JPanel {
    private final int agent;
    private final String advertiser;
    private final XYSeriesCollection seriescollection;
    private final XYSeries revSeries;
    private final XYSeries costSeries;
    private double aggregatedRevenue;
    private double aggregatedCost;
    private int currentDay;
    private Set<Query> queries;
    private final boolean showBorder;

    public AdNetRevCostPanel(int agent, String advertiser, TACAASimulationPanel simulationPanel, boolean showBorder) {
        this.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        this.revSeries = new XYSeries((Comparable)((Object)"Revenue"));
        this.costSeries = new XYSeries((Comparable)((Object)"Cost"));
        this.seriescollection = new XYSeriesCollection();
        this.showBorder = showBorder;
        this.agent = agent;
        this.advertiser = advertiser;
        simulationPanel.addTickListener(new DayListener());
        simulationPanel.addViewListener(new DataUpdateListener(this, null));
        this.initialize();
    }

    private void initialize() {
        this.setLayout(new GridLayout(1, 1));
        if (this.showBorder) {
            this.setBorder(BorderFactory.createTitledBorder("Revenue and Cost"));
        }
        this.queries = new HashSet<Query>();
        this.seriescollection.addSeries(this.revSeries);
        this.seriescollection.addSeries(this.costSeries);
        JFreeChart chart = ViewerChartFactory.createDifferenceChart(this.showBorder ? null : this.advertiser, (XYDataset)this.seriescollection);
        ChartPanel chartpanel = new ChartPanel(chart, false);
        chartpanel.setMouseZoomable(true, false);
        this.add((Component)chartpanel);
    }

    protected void tick(long serverTime) {
    }

    protected void simulationTick(long serverTime, int simulationDate) {
        if (this.currentDay != simulationDate) {
            AdNetRevCostPanel adNetRevCostPanel = this;
            synchronized (adNetRevCostPanel) {
                this.currentDay = simulationDate;
            }
        }
    }

    static /* synthetic */ void access$2(AdNetRevCostPanel adNetRevCostPanel, double d) {
        adNetRevCostPanel.aggregatedCost = d;
    }

    static /* synthetic */ void access$6(AdNetRevCostPanel adNetRevCostPanel, double d) {
        adNetRevCostPanel.aggregatedRevenue = d;
    }

    private class DataUpdateListener
    extends ViewAdaptor {
        final /* synthetic */ AdNetRevCostPanel this$0;

        private DataUpdateListener(AdNetRevCostPanel adNetRevCostPanel) {
            this.this$0 = adNetRevCostPanel;
        }

        @Override
        public void dataUpdated(final int agent, final int type, final double value) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    if (DataUpdateListener.this.this$0.agent == agent && type == 410) {
                         var1_1 = this;
                        synchronized (var1_1) {
                            AdNetRevCostPanel adNetRevCostPanel = DataUpdateListener.this.this$0;
                            AdNetRevCostPanel.access$2(adNetRevCostPanel, adNetRevCostPanel.aggregatedCost + value);
                            DataUpdateListener.this.this$0.costSeries.addOrUpdate((double)DataUpdateListener.this.this$0.currentDay, DataUpdateListener.this.this$0.aggregatedCost);
                        }
                    }
                    if (DataUpdateListener.this.this$0.agent == agent && type == 409) {
                         var1_2 = this;
                        synchronized (var1_2) {
                            AdNetRevCostPanel adNetRevCostPanel = DataUpdateListener.this.this$0;
                            AdNetRevCostPanel.access$6(adNetRevCostPanel, adNetRevCostPanel.aggregatedRevenue + value);
                            DataUpdateListener.this.this$0.revSeries.addOrUpdate((double)DataUpdateListener.this.this$0.currentDay, DataUpdateListener.this.this$0.aggregatedRevenue);
                        }
                    }
                }
            });
        }

        /* synthetic */ DataUpdateListener(AdNetRevCostPanel adNetRevCostPanel, DataUpdateListener dataUpdateListener) {
            DataUpdateListener dataUpdateListener2;
            dataUpdateListener2(adNetRevCostPanel);
        }

    }

    protected class DayListener
    implements TickListener {
        protected DayListener() {
        }

        @Override
        public void tick(long serverTime) {
            AdNetRevCostPanel.this.tick(serverTime);
        }

        @Override
        public void simulationTick(long serverTime, int simulationDate) {
            AdNetRevCostPanel.this.simulationTick(serverTime, simulationDate);
        }
    }

}

