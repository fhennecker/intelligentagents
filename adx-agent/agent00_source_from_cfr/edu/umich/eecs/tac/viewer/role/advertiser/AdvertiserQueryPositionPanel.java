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

public class AdvertiserQueryPositionPanel
extends JPanel {
    private int agent;
    private String advertiser;
    private Query query;
    private int currentDay;
    private XYSeries position;
    private Color legendColor;

    public AdvertiserQueryPositionPanel(int agent, String advertiser, Query query, TACAASimulationPanel simulationPanel, Color legendColor) {
        this.agent = agent;
        this.advertiser = advertiser;
        this.query = query;
        this.legendColor = legendColor;
        this.initialize();
        this.currentDay = 0;
        simulationPanel.addViewListener(new DataUpdateListener(this, null));
        simulationPanel.addTickListener(new DayListener());
    }

    private void initialize() {
        this.setLayout(new GridLayout(1, 1));
        this.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        this.add((Component)this.createChartPanel(this.createPositionChart()));
        this.setBorder(BorderFactory.createTitledBorder("Position"));
    }

    private ChartPanel createChartPanel(JFreeChart jFreeChart) {
        return new ChartPanel(jFreeChart);
    }

    private JFreeChart createPositionChart() {
        this.position = new XYSeries((Comparable)((Object)"Position"));
        return ViewerChartFactory.createDaySeriesChartWithColor(null, (XYDataset)new XYSeriesCollection(this.position), this.legendColor);
    }

    public int getAgent() {
        return this.agent;
    }

    public String getAdvertiser() {
        return this.advertiser;
    }

    protected void setPosition(double position) {
        this.position.addOrUpdate((double)this.currentDay, position);
    }

    protected void tick(long serverTime) {
    }

    protected void simulationTick(long serverTime, int simulationDate) {
        this.currentDay = simulationDate;
    }

    private class DataUpdateListener
    extends ViewAdaptor {
        final /* synthetic */ AdvertiserQueryPositionPanel this$0;

        private DataUpdateListener(AdvertiserQueryPositionPanel advertiserQueryPositionPanel) {
            this.this$0 = advertiserQueryPositionPanel;
        }

        @Override
        public void dataUpdated(final int agent, final int type, final Transportable value) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    if (agent == DataUpdateListener.this.this$0.agent) {
                        switch (type) {
                            case 304: {
                                DataUpdateListener.this.handleQueryReport((QueryReport)value);
                            }
                        }
                    }
                }
            });
        }

        private void handleQueryReport(QueryReport queryReport) {
            this.this$0.setPosition(queryReport.getPosition(this.this$0.query));
        }

        /* synthetic */ DataUpdateListener(AdvertiserQueryPositionPanel advertiserQueryPositionPanel, DataUpdateListener dataUpdateListener) {
            DataUpdateListener dataUpdateListener2;
            dataUpdateListener2(advertiserQueryPositionPanel);
        }

    }

    protected class DayListener
    implements TickListener {
        protected DayListener() {
        }

        @Override
        public void tick(long serverTime) {
            AdvertiserQueryPositionPanel.this.tick(serverTime);
        }

        @Override
        public void simulationTick(long serverTime, int simulationDate) {
            AdvertiserQueryPositionPanel.this.simulationTick(serverTime, simulationDate);
        }
    }

}

