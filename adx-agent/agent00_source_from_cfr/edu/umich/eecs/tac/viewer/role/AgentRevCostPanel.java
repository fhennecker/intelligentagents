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
package edu.umich.eecs.tac.viewer.role;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.SalesReport;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.ViewerChartFactory;
import edu.umich.eecs.tac.viewer.ViewerUtils;
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
import se.sics.isl.transport.Transportable;
import se.sics.tasim.viewer.TickListener;

public class AgentRevCostPanel
extends JPanel {
    private int agent;
    private String advertiser;
    private XYSeriesCollection seriescollection;
    private XYSeries revSeries;
    private XYSeries costSeries;
    private int currentDay;
    private Set<Query> queries;
    private boolean showBorder;

    public AgentRevCostPanel(int agent, String advertiser, TACAASimulationPanel simulationPanel, boolean showBorder) {
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

    private void handleRetailCatalog(RetailCatalog retailCatalog) {
        this.queries.clear();
        ViewerUtils.buildQuerySpace(this.queries, retailCatalog);
    }

    protected void tick(long serverTime) {
    }

    protected void simulationTick(long serverTime, int simulationDate) {
        this.currentDay = simulationDate;
    }

    private double getDayCost(QueryReport report) {
        double result = 0.0;
        for (Query query : this.queries) {
            result += report.getCost(query);
        }
        return result;
    }

    private double getDayRevenue(SalesReport report) {
        double result = 0.0;
        for (Query query : this.queries) {
            result += report.getRevenue(query);
        }
        return result;
    }

    private class DataUpdateListener
    extends ViewAdaptor {
        final /* synthetic */ AgentRevCostPanel this$0;

        private DataUpdateListener(AgentRevCostPanel agentRevCostPanel) {
            this.this$0 = agentRevCostPanel;
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

        @Override
        public void dataUpdated(int type, final Transportable value) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    Class valueType = value.getClass();
                    if (valueType == RetailCatalog.class) {
                        DataUpdateListener.this.this$0.handleRetailCatalog((RetailCatalog)value);
                    }
                }
            });
        }

        /* synthetic */ DataUpdateListener(AgentRevCostPanel agentRevCostPanel, DataUpdateListener dataUpdateListener) {
            DataUpdateListener dataUpdateListener2;
            dataUpdateListener2(agentRevCostPanel);
        }

    }

    protected class DayListener
    implements TickListener {
        protected DayListener() {
        }

        @Override
        public void tick(long serverTime) {
            AgentRevCostPanel.this.tick(serverTime);
        }

        @Override
        public void simulationTick(long serverTime, int simulationDate) {
            AgentRevCostPanel.this.simulationTick(serverTime, simulationDate);
        }
    }

}

