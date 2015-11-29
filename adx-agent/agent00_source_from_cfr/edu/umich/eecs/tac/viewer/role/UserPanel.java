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

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.ViewerChartFactory;
import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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

public class UserPanel
extends SimulationTabPanel {
    private XYSeries nsTimeSeries;
    private XYSeries isTimeSeries;
    private XYSeries f0TimeSeries;
    private XYSeries f1TimeSeries;
    private XYSeries f2TimeSeries;
    private XYSeries tTimeSeries;
    private XYSeriesCollection seriescollection;
    private int currentDay;

    public UserPanel(TACAASimulationPanel simulationPanel) {
        super(simulationPanel);
        this.setBorder(BorderFactory.createTitledBorder("User State Distribution"));
        this.currentDay = 0;
        this.initializeView();
        simulationPanel.addTickListener(new DayListener());
        simulationPanel.addViewListener(new UserSearchStateListener(this, null));
    }

    protected void initializeView() {
        this.createDataset();
        this.setLayout(new BorderLayout());
        this.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        JFreeChart jfreechart = ViewerChartFactory.createDaySeriesChartWithColors(null, "Users per state", (XYDataset)this.seriescollection, true);
        ChartPanel chartpanel = new ChartPanel(jfreechart, false);
        chartpanel.setMouseZoomable(true, false);
        this.add((Component)chartpanel, "Center");
    }

    protected void nextTimeUnit(long serverTime, int timeUnit) {
        this.currentDay = timeUnit;
    }

    private void createDataset() {
        this.nsTimeSeries = new XYSeries((Comparable)((Object)"NS"));
        this.isTimeSeries = new XYSeries((Comparable)((Object)"IS"));
        this.f0TimeSeries = new XYSeries((Comparable)((Object)"F0"));
        this.f1TimeSeries = new XYSeries((Comparable)((Object)"F1"));
        this.f2TimeSeries = new XYSeries((Comparable)((Object)"F2"));
        this.tTimeSeries = new XYSeries((Comparable)((Object)"T"));
        this.seriescollection = new XYSeriesCollection();
        this.seriescollection.addSeries(this.isTimeSeries);
        this.seriescollection.addSeries(this.f0TimeSeries);
        this.seriescollection.addSeries(this.f1TimeSeries);
        this.seriescollection.addSeries(this.f2TimeSeries);
        this.seriescollection.addSeries(this.tTimeSeries);
    }

    protected class DayListener
    implements TickListener {
        protected DayListener() {
        }

        @Override
        public void tick(long serverTime) {
        }

        @Override
        public void simulationTick(long serverTime, int simulationDate) {
            UserPanel.this.nextTimeUnit(serverTime, simulationDate);
        }
    }

    private class UserSearchStateListener
    extends ViewAdaptor {
        final /* synthetic */ UserPanel this$0;

        private UserSearchStateListener(UserPanel userPanel) {
            this.this$0 = userPanel;
        }

        @Override
        public void dataUpdated(int agent, final int type, final int value) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    switch (type) {
                        case 200: {
                            UserSearchStateListener.this.this$0.nsTimeSeries.addOrUpdate((double)UserSearchStateListener.this.this$0.currentDay, (double)value);
                            break;
                        }
                        case 201: {
                            UserSearchStateListener.this.this$0.isTimeSeries.addOrUpdate((double)UserSearchStateListener.this.this$0.currentDay, (double)value);
                            break;
                        }
                        case 202: {
                            UserSearchStateListener.this.this$0.f0TimeSeries.addOrUpdate((double)UserSearchStateListener.this.this$0.currentDay, (double)value);
                            break;
                        }
                        case 203: {
                            UserSearchStateListener.this.this$0.f1TimeSeries.addOrUpdate((double)UserSearchStateListener.this.this$0.currentDay, (double)value);
                            break;
                        }
                        case 204: {
                            UserSearchStateListener.this.this$0.f2TimeSeries.addOrUpdate((double)UserSearchStateListener.this.this$0.currentDay, (double)value);
                            break;
                        }
                        case 205: {
                            UserSearchStateListener.this.this$0.tTimeSeries.addOrUpdate((double)UserSearchStateListener.this.this$0.currentDay, (double)value);
                            break;
                        }
                    }
                }
            });
        }

        /* synthetic */ UserSearchStateListener(UserPanel userPanel, UserSearchStateListener userSearchStateListener) {
            UserSearchStateListener userSearchStateListener2;
            userSearchStateListener2(userPanel);
        }

    }

}

