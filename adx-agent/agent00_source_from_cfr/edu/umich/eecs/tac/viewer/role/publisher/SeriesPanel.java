/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.jfree.chart.ChartPanel
 *  org.jfree.chart.JFreeChart
 *  org.jfree.chart.plot.Plot
 *  org.jfree.chart.plot.XYPlot
 *  org.jfree.chart.renderer.xy.XYItemRenderer
 *  org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
 *  org.jfree.data.xy.XYDataItem
 *  org.jfree.data.xy.XYDataset
 *  org.jfree.data.xy.XYSeries
 *  org.jfree.data.xy.XYSeriesCollection
 */
package edu.umich.eecs.tac.viewer.role.publisher;

import edu.umich.eecs.tac.props.BidBundle;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.ViewerChartFactory;
import edu.umich.eecs.tac.viewer.role.publisher.SeriesTabPanel;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.viewer.TickListener;

public class SeriesPanel
extends JComponent {
    private Query query;
    private int currentDay;
    private Map<String, XYSeries> bidSeries;
    private SeriesTabPanel seriesTabPanel;
    private JFreeChart chart;

    public SeriesPanel(Query query, SeriesTabPanel seriesTabPanel) {
        this.query = query;
        this.bidSeries = new HashMap<String, XYSeries>();
        this.seriesTabPanel = seriesTabPanel;
        this.currentDay = 0;
        this.initialize();
        seriesTabPanel.getSimulationPanel().addViewListener(new BidBundleListener(this, null));
        seriesTabPanel.getSimulationPanel().addTickListener(new DayListener());
    }

    protected void initialize() {
        this.setLayout(new GridLayout(1, 1));
        XYSeriesCollection seriescollection = new XYSeriesCollection();
        int count = this.seriesTabPanel.getAgentCount();
        int index = 0;
        while (index < count) {
            if (this.seriesTabPanel.getRole(index) == 1) {
                XYSeries series = new XYSeries((Comparable)((Object)this.seriesTabPanel.getAgentName(index)));
                this.bidSeries.put(this.seriesTabPanel.getAgentName(index), series);
                seriescollection.addSeries(series);
            }
            ++index;
        }
        this.chart = ViewerChartFactory.createAuctionChart(this.getQuery(), (XYDataset)seriescollection);
        ChartPanel chartpanel = new ChartPanel(this.chart, false);
        chartpanel.setMouseZoomable(true, false);
        this.add((Component)chartpanel);
    }

    public XYLineAndShapeRenderer getRenderer() {
        return (XYLineAndShapeRenderer)((XYPlot)this.chart.getPlot()).getRenderer();
    }

    public Query getQuery() {
        return this.query;
    }

    protected void tick(long serverTime) {
    }

    protected void simulationTick(long serverTime, int simulationDate) {
        this.currentDay = simulationDate;
    }

    private class BidBundleListener
    extends ViewAdaptor {
        final /* synthetic */ SeriesPanel this$0;

        private BidBundleListener(SeriesPanel seriesPanel) {
            this.this$0 = seriesPanel;
        }

        @Override
        public void dataUpdated(final int agent, final int type, final Transportable value) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    if (type == 300 && value.getClass().equals(BidBundle.class)) {
                        XYSeries timeSeries;
                        String name;
                        double bid;
                        BidBundle bundle;
                        int index = BidBundleListener.this.this$0.seriesTabPanel.indexOfAgent(agent);
                        String string = name = index < 0 ? null : BidBundleListener.this.this$0.seriesTabPanel.getAgentName(index);
                        if (name != null && (timeSeries = (XYSeries)BidBundleListener.this.this$0.bidSeries.get(name)) != null && !Double.isNaN(bid = (bundle = (BidBundle)value).getBid(BidBundleListener.this.this$0.query))) {
                            timeSeries.addOrUpdate((double)(BidBundleListener.this.this$0.currentDay - 1), bid);
                        }
                    }
                }
            });
        }

        /* synthetic */ BidBundleListener(SeriesPanel seriesPanel, BidBundleListener bidBundleListener) {
            BidBundleListener bidBundleListener2;
            bidBundleListener2(seriesPanel);
        }

    }

    protected class DayListener
    implements TickListener {
        protected DayListener() {
        }

        @Override
        public void tick(long serverTime) {
            SeriesPanel.this.tick(serverTime);
        }

        @Override
        public void simulationTick(long serverTime, int simulationDate) {
            SeriesPanel.this.simulationTick(serverTime, simulationDate);
        }
    }

}

