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

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.SalesReport;
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
import se.sics.isl.transport.Transportable;
import se.sics.tasim.viewer.TickListener;

public class AdvertiserQueryValuePanel
extends JPanel {
    private int agent;
    private Query query;
    private XYSeriesCollection seriescollection;
    private XYSeries revSeries;
    private XYSeries costSeries;
    private int currentDay;

    public AdvertiserQueryValuePanel(int agent, String advertiser, Query query, TACAASimulationPanel simulationPanel) {
        this.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        this.revSeries = new XYSeries((Comparable)((Object)"Revenue"));
        this.costSeries = new XYSeries((Comparable)((Object)"Cost"));
        this.seriescollection = new XYSeriesCollection();
        this.agent = agent;
        this.query = query;
        simulationPanel.addTickListener(new DayListener());
        simulationPanel.addViewListener(new DataUpdateListener(this, null));
        this.initialize();
    }

    private void initialize() {
        this.setLayout(new GridLayout(1, 1));
        this.seriescollection.addSeries(this.revSeries);
        this.seriescollection.addSeries(this.costSeries);
        JFreeChart chart = ViewerChartFactory.createDifferenceChart((XYDataset)this.seriescollection);
        ChartPanel chartpanel = new ChartPanel(chart, false);
        chartpanel.setMouseZoomable(true, false);
        this.add((Component)chartpanel);
        this.setBorder(BorderFactory.createTitledBorder("Revenue and Cost"));
    }

    protected void tick(long serverTime) {
    }

    protected void simulationTick(long serverTime, int simulationDate) {
        this.currentDay = simulationDate;
    }

    private double getDayCost(QueryReport report) {
        return report.getCost(this.query);
    }

    private double getDayRevenue(SalesReport report) {
        return report.getRevenue(this.query);
    }

    private class DataUpdateListener
    extends ViewAdaptor {
        final /* synthetic */ AdvertiserQueryValuePanel this$0;

        private DataUpdateListener(AdvertiserQueryValuePanel advertiserQueryValuePanel) {
            this.this$0 = advertiserQueryValuePanel;
        }

        @Override
        public void dataUpdated(final int agent, final int type, final Transportable value) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    if (type == 304 && value.getClass().equals(QueryReport.class) && agent == DataUpdateListener.this.this$0.agent) {
                        QueryReport queryReport = (QueryReport)value;
                        DataUpdateListener.this.this$0.costSeries.addOrUpdate((double)DataUpdateListener.this.this$0.currentDay, DataUpdateListener.this.this$0.getDayCost(queryReport));
                    }
                    if (type == 305 && value.getClass().equals(SalesReport.class) && agent == DataUpdateListener.this.this$0.agent) {
                        SalesReport salesReport = (SalesReport)value;
                        DataUpdateListener.this.this$0.revSeries.addOrUpdate((double)DataUpdateListener.this.this$0.currentDay, DataUpdateListener.this.this$0.getDayRevenue(salesReport));
                    }
                }
            });
        }

        /* synthetic */ DataUpdateListener(AdvertiserQueryValuePanel advertiserQueryValuePanel, DataUpdateListener dataUpdateListener) {
            DataUpdateListener dataUpdateListener2;
            dataUpdateListener2(advertiserQueryValuePanel);
        }

    }

    protected class DayListener
    implements TickListener {
        protected DayListener() {
        }

        @Override
        public void tick(long serverTime) {
            AdvertiserQueryValuePanel.this.tick(serverTime);
        }

        @Override
        public void simulationTick(long serverTime, int simulationDate) {
            AdvertiserQueryValuePanel.this.simulationTick(serverTime, simulationDate);
        }
    }

}

