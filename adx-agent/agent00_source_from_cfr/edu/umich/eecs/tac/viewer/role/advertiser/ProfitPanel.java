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

public class ProfitPanel
extends SimulationTabPanel {
    private int currentDay;
    private String advertiser;
    private int agent;
    private XYSeries series;
    private Color legendColor;

    public ProfitPanel(TACAASimulationPanel simulationPanel, int agent, String advertiser, Color legendColor) {
        super(simulationPanel);
        this.agent = agent;
        this.advertiser = advertiser;
        this.currentDay = 0;
        this.legendColor = legendColor;
        simulationPanel.addTickListener(new DayListener());
        simulationPanel.addViewListener(new BankStatusListener());
        this.initialize();
    }

    protected void initialize() {
        this.setLayout(new GridLayout(1, 1));
        this.setBorder(BorderFactory.createTitledBorder("Advertiser Profit"));
        this.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        XYSeriesCollection seriescollection = new XYSeriesCollection();
        this.series = new XYSeries((Comparable)((Object)this.advertiser));
        seriescollection.addSeries(this.series);
        JFreeChart chart = ViewerChartFactory.createDaySeriesChartWithColor(null, (XYDataset)seriescollection, this.legendColor);
        ChartPanel chartpanel = new ChartPanel(chart, false);
        chartpanel.setMouseZoomable(true, false);
        this.add((Component)chartpanel);
    }

    protected void tick(long serverTime) {
    }

    protected void simulationTick(long serverTime, int simulationDate) {
        this.currentDay = simulationDate;
    }

    protected class BankStatusListener
    extends ViewAdaptor {
        protected BankStatusListener() {
        }

        @Override
        public void dataUpdated(final int agent, final int type, final double value) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    if (type == 100 && agent == BankStatusListener.this.ProfitPanel.this.agent) {
                        BankStatusListener.this.ProfitPanel.this.series.addOrUpdate((double)BankStatusListener.this.ProfitPanel.this.currentDay, value);
                    }
                }
            });
        }

    }

    protected class DayListener
    implements TickListener {
        protected DayListener() {
        }

        @Override
        public void tick(long serverTime) {
            ProfitPanel.this.tick(serverTime);
        }

        @Override
        public void simulationTick(long serverTime, int simulationDate) {
            ProfitPanel.this.simulationTick(serverTime, simulationDate);
        }
    }

}

