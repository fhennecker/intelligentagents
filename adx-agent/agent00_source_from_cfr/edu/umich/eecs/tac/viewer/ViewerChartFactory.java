/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.jfree.chart.ChartColor
 *  org.jfree.chart.ChartFactory
 *  org.jfree.chart.JFreeChart
 *  org.jfree.chart.block.BlockBorder
 *  org.jfree.chart.block.BlockFrame
 *  org.jfree.chart.plot.Plot
 *  org.jfree.chart.plot.PlotOrientation
 *  org.jfree.chart.plot.XYPlot
 *  org.jfree.chart.renderer.xy.XYDifferenceRenderer
 *  org.jfree.chart.renderer.xy.XYItemRenderer
 *  org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
 *  org.jfree.chart.title.LegendTitle
 *  org.jfree.data.xy.XYDataset
 *  org.jfree.ui.RectangleInsets
 */
package edu.umich.eecs.tac.viewer;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.block.BlockFrame;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

public class ViewerChartFactory {
    private ViewerChartFactory() {
    }

    public static JFreeChart createChart(XYDataset xydataset, String title, String xLabel, String yLabel, Color legendColor) {
        JFreeChart jfreechart = ChartFactory.createXYLineChart((String)title, (String)xLabel, (String)yLabel, (XYDataset)xydataset, (PlotOrientation)PlotOrientation.VERTICAL, (boolean)false, (boolean)false, (boolean)false);
        jfreechart.setBackgroundPaint((Paint)TACAAViewerConstants.CHART_BACKGROUND);
        ViewerChartFactory.formatPlotWithColor((XYPlot)jfreechart.getPlot(), legendColor);
        return jfreechart;
    }

    public static JFreeChart createCapacityChart(XYDataset xydataset, String title, Color legendColor) {
        return ViewerChartFactory.createChart(xydataset, title, "Day", "% Capacity Used", legendColor);
    }

    public static JFreeChart createCapacityChart(XYDataset xydataset, Color legendColor) {
        return ViewerChartFactory.createCapacityChart(xydataset, null, legendColor);
    }

    public static JFreeChart createDaySeriesChartWithColor(String s, XYDataset xydataset, Color legendColor) {
        JFreeChart jfreechart = ChartFactory.createXYLineChart((String)s, (String)"Day", (String)"", (XYDataset)xydataset, (PlotOrientation)PlotOrientation.VERTICAL, (boolean)false, (boolean)false, (boolean)false);
        jfreechart.setBackgroundPaint((Paint)TACAAViewerConstants.CHART_BACKGROUND);
        ViewerChartFactory.formatPlotWithColor((XYPlot)jfreechart.getPlot(), legendColor);
        return jfreechart;
    }

    public static JFreeChart createDaySeriesChartWithColors(String s, XYDataset xydataset, boolean legend) {
        return ViewerChartFactory.createDaySeriesChartWithColors(s, "", xydataset, legend);
    }

    public static JFreeChart createDifferenceChart(XYDataset xydataset) {
        return ViewerChartFactory.createDifferenceChart(null, xydataset);
    }

    public static JFreeChart createDifferenceChart(String title, XYDataset xydataset) {
        JFreeChart jfreechart = ChartFactory.createXYLineChart((String)title, (String)"Day", (String)"$", (XYDataset)xydataset, (PlotOrientation)PlotOrientation.VERTICAL, (boolean)false, (boolean)false, (boolean)false);
        jfreechart.setBackgroundPaint((Paint)TACAAViewerConstants.CHART_BACKGROUND);
        XYPlot xyplot = (XYPlot)jfreechart.getPlot();
        ViewerChartFactory.formatPlot(xyplot);
        XYDifferenceRenderer renderer = new XYDifferenceRenderer((Paint)Color.green, (Paint)Color.red, false);
        renderer.setSeriesPaint(0, (Paint)ChartColor.DARK_GREEN);
        renderer.setSeriesPaint(1, (Paint)ChartColor.DARK_RED);
        renderer.setBaseStroke((Stroke)new BasicStroke(4.0f, 0, 2));
        xyplot.setRenderer((XYItemRenderer)renderer);
        return jfreechart;
    }

    public static JFreeChart createDifferenceChart(String title, XYDataset xydataset, String bottomTitle, String sideTitle) {
        JFreeChart jfreechart = ChartFactory.createXYLineChart((String)title, (String)bottomTitle, (String)sideTitle, (XYDataset)xydataset, (PlotOrientation)PlotOrientation.VERTICAL, (boolean)false, (boolean)false, (boolean)false);
        jfreechart.setBackgroundPaint((Paint)TACAAViewerConstants.CHART_BACKGROUND);
        XYPlot xyplot = (XYPlot)jfreechart.getPlot();
        ViewerChartFactory.formatPlot(xyplot);
        XYDifferenceRenderer renderer = new XYDifferenceRenderer((Paint)Color.green, (Paint)Color.red, false);
        renderer.setSeriesPaint(0, (Paint)ChartColor.DARK_GREEN);
        renderer.setSeriesPaint(1, (Paint)ChartColor.DARK_RED);
        renderer.setBaseStroke((Stroke)new BasicStroke(4.0f, 0, 2));
        xyplot.setRenderer((XYItemRenderer)renderer);
        return jfreechart;
    }

    public static JFreeChart createAuctionChart(Query query, XYDataset xydataset) {
        JFreeChart jfreechart = ChartFactory.createXYLineChart((String)String.format("Auction for (%s,%s)", query.getManufacturer(), query.getComponent()), (String)"Day", (String)"Bid [$]", (XYDataset)xydataset, (PlotOrientation)PlotOrientation.VERTICAL, (boolean)false, (boolean)true, (boolean)false);
        jfreechart.setBackgroundPaint((Paint)TACAAViewerConstants.CHART_BACKGROUND);
        XYPlot xyplot = (XYPlot)jfreechart.getPlot();
        ViewerChartFactory.formatPlot(xyplot);
        ViewerChartFactory.formatRendererWithColors(xyplot);
        return jfreechart;
    }

    public static JFreeChart createDaySeriesChartWithColors(String s, String yAxisLabel, XYDataset xydataset, boolean legend) {
        JFreeChart jfreechart = ChartFactory.createXYLineChart((String)s, (String)"Day", (String)yAxisLabel, (XYDataset)xydataset, (PlotOrientation)PlotOrientation.VERTICAL, (boolean)true, (boolean)false, (boolean)false);
        jfreechart.setBackgroundPaint((Paint)TACAAViewerConstants.CHART_BACKGROUND);
        XYPlot xyplot = (XYPlot)jfreechart.getPlot();
        ViewerChartFactory.formatPlotWithColors(xyplot);
        if (legend) {
            LegendTitle legendTitle = jfreechart.getLegend();
            legendTitle.setBackgroundPaint((Paint)TACAAViewerConstants.CHART_BACKGROUND);
            legendTitle.setFrame((BlockFrame)BlockBorder.NONE);
        }
        return jfreechart;
    }

    private static void formatPlot(XYPlot xyplot) {
        xyplot.setBackgroundPaint((Paint)TACAAViewerConstants.CHART_BACKGROUND);
        xyplot.setDomainGridlinePaint((Paint)Color.GRAY);
        xyplot.setRangeGridlinePaint((Paint)Color.GRAY);
        xyplot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        xyplot.setOutlineVisible(false);
    }

    private static void formatPlotWithColor(XYPlot xyplot, Color legendColor) {
        ViewerChartFactory.formatPlot(xyplot);
        ViewerChartFactory.formatRendererWithColor(xyplot, legendColor);
    }

    private static void formatPlotWithColors(XYPlot xyplot) {
        ViewerChartFactory.formatPlot(xyplot);
        ViewerChartFactory.formatRendererWithColors(xyplot);
    }

    private static void formatRendererWithColor(XYPlot xyplot, Color legendColor) {
        XYItemRenderer xyitemrenderer = xyplot.getRenderer();
        xyitemrenderer.setBaseStroke((Stroke)new BasicStroke(4.0f, 0, 2));
        if (xyitemrenderer instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)xyitemrenderer;
            xylineandshaperenderer.setBaseShapesVisible(false);
            xylineandshaperenderer.setSeriesPaint(0, (Paint)legendColor);
        }
    }

    private static void formatRendererWithColors(XYPlot xyplot) {
        XYItemRenderer xyitemrenderer = xyplot.getRenderer();
        xyitemrenderer.setBaseStroke((Stroke)new BasicStroke(4.0f, 0, 2));
        if (xyitemrenderer instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)xyitemrenderer;
            xylineandshaperenderer.setBaseShapesVisible(false);
            int i = 0;
            while (i < TACAAViewerConstants.LEGEND_COLORS.length) {
                xylineandshaperenderer.setSeriesPaint(i, (Paint)TACAAViewerConstants.LEGEND_COLORS[i]);
                ++i;
            }
        }
    }
}

