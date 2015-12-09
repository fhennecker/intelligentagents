/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.jfree.chart.ChartFactory
 *  org.jfree.chart.ChartPanel
 *  org.jfree.chart.JFreeChart
 *  org.jfree.chart.plot.Plot
 *  org.jfree.chart.plot.PlotOrientation
 *  org.jfree.chart.plot.XYPlot
 *  org.jfree.chart.renderer.xy.XYItemRenderer
 *  org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
 *  org.jfree.data.xy.XYDataItem
 *  org.jfree.data.xy.XYDataset
 *  org.jfree.data.xy.XYSeries
 *  org.jfree.data.xy.XYSeriesCollection
 *  org.jfree.ui.RectangleInsets
 */
package edu.umich.eecs.tac.logviewer.gui;

import edu.umich.eecs.tac.logviewer.gui.PopulationWindow;
import edu.umich.eecs.tac.logviewer.info.GameInfo;
import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.UserPopulationState;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

public class PopulationPanel {
    JPanel mainPane;
    PopulationWindow populationWindow;
    GameInfo gameInfo;
    private static final int numQueryStates = 6;
    private XYSeries nsTimeSeries;
    private XYSeries isTimeSeries;
    private XYSeries f0TimeSeries;
    private XYSeries f1TimeSeries;
    private XYSeries f2TimeSeries;
    private XYSeries tTimeSeries;
    private XYSeriesCollection seriescollection;

    public PopulationPanel(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
        this.mainPane = new JPanel();
        this.mainPane.setLayout(new BorderLayout());
        this.mainPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Total User Population "));
        this.mainPane.addMouseListener(new MouseInputAdapter(){

            @Override
            public void mouseClicked(MouseEvent me) {
                PopulationPanel.this.openPopulationWindow();
            }
        });
        this.createDataset();
        this.applyData();
        JFreeChart jfreechart = this.createChart((XYDataset)this.seriescollection);
        ChartPanel chartpanel = new ChartPanel(jfreechart, false);
        chartpanel.setMinimumSize(new Dimension(500, 270));
        chartpanel.setPreferredSize(new Dimension(500, 270));
        chartpanel.setMouseZoomable(true, false);
        this.mainPane.add((Component)chartpanel, "Center");
    }

    private void openPopulationWindow() {
        if (this.populationWindow == null) {
            this.populationWindow = new PopulationWindow(this.gameInfo);
            this.populationWindow.setLocationRelativeTo(this.mainPane);
            this.populationWindow.setVisible(true);
        } else if (this.populationWindow.isVisible()) {
            this.populationWindow.toFront();
        } else {
            this.populationWindow.setVisible(true);
        }
        int state = this.populationWindow.getExtendedState();
        if ((state & 1) != 0) {
            this.populationWindow.setExtendedState(state & -2);
        }
    }

    private void applyData() {
        UserPopulationState[] ups = this.gameInfo.getUserPopulationState();
        int currentDay = 0;
        int n = ups.length;
        while (currentDay < n) {
            int[] population = new int[6];
            for (Product product : this.gameInfo.getRetailCatalog()) {
                int[] productPop = ups[currentDay].getDistribution(product);
                int j = 0;
                while (j < population.length) {
                    int[] arrn = population;
                    int n2 = j;
                    arrn[n2] = arrn[n2] + productPop[j];
                    ++j;
                }
            }
            int k = 0;
            while (k < population.length) {
                switch (k) {
                    case 0: {
                        this.nsTimeSeries.addOrUpdate((double)currentDay, (double)population[k]);
                        break;
                    }
                    case 1: {
                        this.isTimeSeries.addOrUpdate((double)currentDay, (double)population[k]);
                        break;
                    }
                    case 2: {
                        this.f0TimeSeries.addOrUpdate((double)currentDay, (double)population[k]);
                        break;
                    }
                    case 3: {
                        this.f1TimeSeries.addOrUpdate((double)currentDay, (double)population[k]);
                        break;
                    }
                    case 4: {
                        this.f2TimeSeries.addOrUpdate((double)currentDay, (double)population[k]);
                        break;
                    }
                    case 5: {
                        this.tTimeSeries.addOrUpdate((double)currentDay, (double)population[k]);
                    }
                }
                ++k;
            }
            ++currentDay;
        }
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

    private JFreeChart createChart(XYDataset xydataset) {
        JFreeChart jfreechart = ChartFactory.createXYLineChart((String)"User state distribution", (String)"Day", (String)"Users per state", (XYDataset)xydataset, (PlotOrientation)PlotOrientation.VERTICAL, (boolean)true, (boolean)true, (boolean)false);
        jfreechart.setBackgroundPaint((Paint)Color.white);
        XYPlot xyplot = (XYPlot)jfreechart.getPlot();
        xyplot.setBackgroundPaint((Paint)Color.lightGray);
        xyplot.setDomainGridlinePaint((Paint)Color.white);
        xyplot.setRangeGridlinePaint((Paint)Color.white);
        xyplot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        xyplot.setDomainCrosshairVisible(true);
        xyplot.setRangeCrosshairVisible(true);
        XYItemRenderer xyitemrenderer = xyplot.getRenderer();
        xyitemrenderer.setBaseStroke((Stroke)new BasicStroke(3.0f, 0, 2));
        if (xyitemrenderer instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)xyitemrenderer;
            xylineandshaperenderer.setBaseShapesVisible(false);
        }
        return jfreechart;
    }

    public JPanel getMainPane() {
        return this.mainPane;
    }

}

