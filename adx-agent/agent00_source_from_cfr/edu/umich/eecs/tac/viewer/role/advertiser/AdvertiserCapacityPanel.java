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

import edu.umich.eecs.tac.props.AdvertiserInfo;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.SalesReport;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.ViewerChartFactory;
import edu.umich.eecs.tac.viewer.ViewerUtils;
import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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

public class AdvertiserCapacityPanel
extends SimulationTabPanel {
    private int agent;
    private int currentDay;
    private XYSeries relativeCapacity;
    private int capacity;
    private int window;
    private Map<Integer, Integer> amountsSold;
    private Set<Query> queries;
    private Color legendColor;

    public AdvertiserCapacityPanel(int agent, String advertiser, TACAASimulationPanel simulationPanel, Color legendColor) {
        super(simulationPanel);
        this.agent = agent;
        this.legendColor = legendColor;
        this.currentDay = 0;
        simulationPanel.addViewListener(new DataUpdateListener(this, null));
        simulationPanel.addTickListener(new DayListener());
        this.initialize();
    }

    protected void initialize() {
        this.setLayout(new GridLayout(1, 1));
        this.setBorder(BorderFactory.createTitledBorder(" Capacity Used"));
        this.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        this.amountsSold = new HashMap<Integer, Integer>();
        this.queries = new HashSet<Query>();
        this.relativeCapacity = new XYSeries((Comparable)((Object)"Relative Capacity"));
        XYSeriesCollection seriescollection = new XYSeriesCollection();
        seriescollection.addSeries(this.relativeCapacity);
        JFreeChart chart = ViewerChartFactory.createCapacityChart((XYDataset)seriescollection, this.legendColor);
        ChartPanel chartpanel = new ChartPanel(chart, false);
        chartpanel.setMouseZoomable(true, false);
        this.add((Component)chartpanel);
    }

    protected void tick(long serverTime) {
    }

    protected void simulationTick(long serverTime, int simulationDate) {
        this.currentDay = simulationDate;
    }

    private int getAmountSold(SalesReport report) {
        int result = 0;
        for (Query query : this.queries) {
            result += report.getConversions(query);
        }
        return result;
    }

    private void updateChart() {
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                double soldInWindow = 0.0;
                int i = Math.max(0, AdvertiserCapacityPanel.this.currentDay - AdvertiserCapacityPanel.this.window);
                while (i < AdvertiserCapacityPanel.this.currentDay) {
                    if (AdvertiserCapacityPanel.this.amountsSold.get(i) != null && !Double.isNaN(((Integer)AdvertiserCapacityPanel.this.amountsSold.get(i)).intValue())) {
                        soldInWindow += (double)((Integer)AdvertiserCapacityPanel.this.amountsSold.get(i)).intValue();
                    }
                    ++i;
                }
                AdvertiserCapacityPanel.this.relativeCapacity.addOrUpdate((double)AdvertiserCapacityPanel.this.currentDay, soldInWindow / (double)AdvertiserCapacityPanel.this.capacity * 100.0);
            }
        });
    }

    private void handleRetailCatalog(RetailCatalog retailCatalog) {
        ViewerUtils.buildQuerySpace(this.queries, retailCatalog);
    }

    private void handleAdvertiserInfo(AdvertiserInfo advertiserInfo) {
        this.capacity = advertiserInfo.getDistributionCapacity();
        this.window = advertiserInfo.getDistributionWindow();
    }

    private void handleSalesReport(SalesReport salesReport) {
        int sold = this.getAmountSold(salesReport);
        this.amountsSold.put(this.currentDay - 1, sold);
        this.updateChart();
    }

    private class DataUpdateListener
    extends ViewAdaptor {
        final /* synthetic */ AdvertiserCapacityPanel this$0;

        private DataUpdateListener(AdvertiserCapacityPanel advertiserCapacityPanel) {
            this.this$0 = advertiserCapacityPanel;
        }

        @Override
        public void dataUpdated(int agent, int type, Transportable value) {
            if (this.this$0.agent == agent && type == 307 && value.getClass() == AdvertiserInfo.class) {
                this.this$0.handleAdvertiserInfo((AdvertiserInfo)value);
            }
            if (this.this$0.agent == agent && type == 305 && value.getClass() == SalesReport.class) {
                this.this$0.handleSalesReport((SalesReport)value);
            }
        }

        @Override
        public void dataUpdated(int type, Transportable value) {
            Class valueType = value.getClass();
            if (valueType == RetailCatalog.class) {
                this.this$0.handleRetailCatalog((RetailCatalog)value);
            }
        }

        /* synthetic */ DataUpdateListener(AdvertiserCapacityPanel advertiserCapacityPanel, DataUpdateListener dataUpdateListener) {
            DataUpdateListener dataUpdateListener2;
            dataUpdateListener2(advertiserCapacityPanel);
        }
    }

    protected class DayListener
    implements TickListener {
        protected DayListener() {
        }

        @Override
        public void tick(long serverTime) {
            AdvertiserCapacityPanel.this.tick(serverTime);
        }

        @Override
        public void simulationTick(long serverTime, int simulationDate) {
            AdvertiserCapacityPanel.this.simulationTick(serverTime, simulationDate);
        }
    }

}

