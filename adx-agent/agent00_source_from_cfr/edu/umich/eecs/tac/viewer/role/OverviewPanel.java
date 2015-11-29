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

import com.botbox.util.ArrayUtils;
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
import javax.swing.border.Border;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import se.sics.tasim.viewer.TickListener;

public class OverviewPanel
extends SimulationTabPanel {
    private XYSeriesCollection seriescollection;
    private int[] agents;
    private int[] roles;
    private int[] participants;
    private XYSeries[] series;
    private String[] names;
    private int agentCount;
    private int currentDay;

    public OverviewPanel(TACAASimulationPanel simulationPanel) {
        super(simulationPanel);
        this.setBorder(BorderFactory.createTitledBorder("Advertiser Profits"));
        this.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        this.agents = new int[0];
        this.roles = new int[0];
        this.participants = new int[0];
        this.series = new XYSeries[0];
        this.names = new String[0];
        this.currentDay = 0;
        this.initialize();
        this.getSimulationPanel().addTickListener(new DayListener());
        this.getSimulationPanel().addViewListener(new BankStatusListener());
    }

    protected void initialize() {
        this.setLayout(new GridLayout(1, 1));
        this.seriescollection = new XYSeriesCollection();
        JFreeChart chart = ViewerChartFactory.createDaySeriesChartWithColors(null, (XYDataset)this.seriescollection, true);
        ChartPanel chartpanel = new ChartPanel(chart, false);
        chartpanel.setMouseZoomable(true, false);
        this.add((Component)chartpanel);
    }

    protected void addAgent(int agent) {
        int index = ArrayUtils.indexOf(this.agents, 0, this.agentCount, agent);
        if (index < 0) {
            this.doAddAgent(agent);
        }
    }

    private int doAddAgent(int agent) {
        if (this.agentCount == this.participants.length) {
            int newSize = this.agentCount + 8;
            this.agents = ArrayUtils.setSize(this.agents, newSize);
            this.roles = ArrayUtils.setSize(this.roles, newSize);
            this.participants = ArrayUtils.setSize(this.participants, newSize);
            this.series = (XYSeries[])ArrayUtils.setSize(this.series, newSize);
            this.names = (String[])ArrayUtils.setSize(this.names, newSize);
        }
        this.agents[this.agentCount] = agent;
        return this.agentCount++;
    }

    private void setAgent(int agent, int role, String name, int participantID) {
        this.addAgent(agent);
        int index = ArrayUtils.indexOf(this.agents, 0, this.agentCount, agent);
        this.roles[index] = role;
        this.names[index] = name;
        this.participants[index] = participantID;
        if (this.series[index] == null && 1 == this.roles[index]) {
            this.series[index] = new XYSeries((Comparable)((Object)name));
            this.seriescollection.addSeries(this.series[index]);
        }
    }

    protected void participant(int agent, int role, String name, int participantID) {
        this.setAgent(agent, role, name, participantID);
    }

    protected void dataUpdated(int agent, int type, double value) {
        int index = ArrayUtils.indexOf(this.agents, 0, this.agentCount, agent);
        if (index < 0 || this.series[index] == null || type != 100) {
            return;
        }
        this.series[index].addOrUpdate((double)this.currentDay, value);
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
        public void dataUpdated(int agent, int type, double value) {
            OverviewPanel.this.dataUpdated(agent, type, value);
        }

        @Override
        public void participant(int agent, int role, String name, int participantID) {
            OverviewPanel.this.participant(agent, role, name, participantID);
        }
    }

    protected class DayListener
    implements TickListener {
        protected DayListener() {
        }

        @Override
        public void tick(long serverTime) {
            OverviewPanel.this.tick(serverTime);
        }

        @Override
        public void simulationTick(long serverTime, int simulationDate) {
            OverviewPanel.this.simulationTick(serverTime, simulationDate);
        }
    }

}

