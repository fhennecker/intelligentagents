/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.jfree.chart.ChartPanel
 *  org.jfree.chart.JFreeChart
 *  org.jfree.chart.plot.DialShape
 *  org.jfree.chart.plot.MeterInterval
 *  org.jfree.chart.plot.MeterPlot
 *  org.jfree.chart.plot.Plot
 *  org.jfree.data.Range
 *  org.jfree.data.general.DefaultValueDataset
 *  org.jfree.data.general.ValueDataset
 */
package edu.umich.eecs.tac.logviewer.gui.advertiser;

import edu.umich.eecs.tac.logviewer.gui.PositiveBoundedRangeModel;
import edu.umich.eecs.tac.logviewer.info.Advertiser;
import edu.umich.eecs.tac.props.Query;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Paint;
import java.awt.Stroke;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DialShape;
import org.jfree.chart.plot.MeterInterval;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.data.Range;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.general.ValueDataset;

public class QueryRatioPanel {
    JPanel mainPane;
    private DefaultValueDataset ctrValue;
    private DefaultValueDataset convValue;
    Query query;
    Advertiser advertiser;
    PositiveBoundedRangeModel dayModel;

    public QueryRatioPanel(Query query, Advertiser advertiser, PositiveBoundedRangeModel dayModel) {
        this.query = query;
        this.advertiser = advertiser;
        this.dayModel = dayModel;
        if (dayModel != null) {
            dayModel.addChangeListener(new ChangeListener(){

                @Override
                public void stateChanged(ChangeEvent ce) {
                    QueryRatioPanel.this.updateMePlz();
                }
            });
        }
        this.mainPane = new JPanel();
        this.mainPane.setLayout(new GridLayout(2, 1));
        this.mainPane.setBorder(BorderFactory.createTitledBorder(query.toString()));
        ChartPanel CTRChart = new ChartPanel(this.createCTRChart());
        ChartPanel CONVChart = new ChartPanel(this.createConvChart());
        this.mainPane.add((Component)CTRChart);
        this.mainPane.add((Component)CONVChart);
    }

    private JFreeChart createCTRChart() {
        this.ctrValue = new DefaultValueDataset(0.0);
        return this.createChart("CTR", (ValueDataset)this.ctrValue);
    }

    private JFreeChart createConvChart() {
        this.convValue = new DefaultValueDataset(0.0);
        return this.createChart("Conv Rate", (ValueDataset)this.convValue);
    }

    private JFreeChart createChart(String s, ValueDataset dataset) {
        MeterPlot meterplot = new MeterPlot(dataset);
        meterplot.setDialShape(DialShape.CHORD);
        meterplot.setRange(new Range(0.0, 100.0));
        meterplot.addInterval(new MeterInterval("", new Range(0.0, 100.0), (Paint)Color.lightGray, (Stroke)new BasicStroke(2.0f), (Paint)new Color(0, 255, 0, 64)));
        meterplot.setNeedlePaint((Paint)Color.darkGray);
        meterplot.setDialBackgroundPaint((Paint)Color.white);
        meterplot.setDialOutlinePaint((Paint)Color.gray);
        meterplot.setMeterAngle(260);
        meterplot.setTickLabelsVisible(true);
        meterplot.setTickLabelFont(new Font("Dialog", 1, 10));
        meterplot.setTickLabelPaint((Paint)Color.darkGray);
        meterplot.setTickSize(5.0);
        meterplot.setTickPaint((Paint)Color.lightGray);
        meterplot.setValuePaint((Paint)Color.black);
        meterplot.setValueFont(new Font("Dialog", 1, 14));
        meterplot.setUnits("%");
        return new JFreeChart(s, JFreeChart.DEFAULT_TITLE_FONT, (Plot)meterplot, false);
    }

    protected void updateCTR(int impressions, int clicks) {
        if (impressions > 0) {
            this.ctrValue.setValue((Number)(100.0 * (double)clicks / (double)impressions));
        } else {
            this.ctrValue.setValue((Number)0.0);
        }
    }

    protected void updateConvRate(int conversions, int clicks) {
        if (clicks > 0) {
            this.convValue.setValue((Number)(100.0 * (double)conversions / (double)clicks));
        } else {
            this.convValue.setValue((Number)0.0);
        }
    }

    private void updateMePlz() {
    }

    public Component getMainPane() {
        return this.mainPane;
    }

}

