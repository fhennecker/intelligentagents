/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.logviewer.gui;

import edu.umich.eecs.tac.logviewer.gui.PositiveBoundedRangeModel;
import edu.umich.eecs.tac.logviewer.info.Advertiser;
import edu.umich.eecs.tac.logviewer.info.GameInfo;
import edu.umich.eecs.tac.logviewer.util.VisualizerUtils;
import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class AuctionResultsPanel {
    public static final String NA = "";
    JPanel mainPane;
    JLabel[] positionLabels;
    int[] indexes;
    Advertiser[] advertisers;
    Query query;
    PositiveBoundedRangeModel dayModel;
    GameInfo gameInfo;

    public AuctionResultsPanel(Query query, GameInfo gameInfo, PositiveBoundedRangeModel dm) {
        this.query = query;
        this.gameInfo = gameInfo;
        this.dayModel = dm;
        this.advertisers = gameInfo.getAdvertisers();
        if (this.dayModel != null) {
            this.dayModel.addChangeListener(new ChangeListener(){

                @Override
                public void stateChanged(ChangeEvent ce) {
                    AuctionResultsPanel.this.updateMePlz();
                }
            });
        }
        this.mainPane = new JPanel();
        this.mainPane.setLayout(new BoxLayout(this.mainPane, 1));
        this.mainPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), VisualizerUtils.formatToString(query)));
        this.mainPane.setMinimumSize(new Dimension(105, 155));
        this.mainPane.setPreferredSize(new Dimension(105, 155));
        this.mainPane.setBackground(Color.WHITE);
        this.indexes = new int[this.advertisers.length];
        this.positionLabels = new JLabel[this.advertisers.length];
        int i = 0;
        while (i < this.indexes.length) {
            this.indexes[i] = i;
            this.positionLabels[this.indexes.length - i - 1] = new JLabel("");
            this.positionLabels[this.indexes.length - i - 1].setForeground(this.advertisers[i].getColor());
            this.mainPane.add(this.positionLabels[this.indexes.length - i - 1]);
            ++i;
        }
        this.updateMePlz();
    }

    private void updateMePlz() {
        int day = this.dayModel.getCurrent();
        double[] averagePosition = new double[this.advertisers.length];
        QueryReport report = this.advertisers[0].getQueryReport(day + 1);
        if (report == null) {
            this.noQueryReportDay();
        } else {
            int i = 0;
            while (i < this.indexes.length) {
                averagePosition[this.indexes[i]] = report.getPosition(this.query, this.advertisers[this.indexes[i]].getAddress());
                ++i;
            }
            VisualizerUtils.hardSort(averagePosition, this.indexes);
            i = 0;
            while (i < this.indexes.length) {
                Ad ad = report.getAd(this.query, this.advertisers[this.indexes[i]].getAddress());
                String adString = ad == null ? "" : VisualizerUtils.formatToString(ad);
                this.positionLabels[i].setText(adString);
                this.positionLabels[i].setForeground(this.advertisers[this.indexes[i]].getColor());
                ++i;
            }
        }
    }

    private void noQueryReportDay() {
        int i = 0;
        while (i < this.indexes.length) {
            this.indexes[i] = i;
            this.positionLabels[i].setText("");
            this.positionLabels[i].setForeground(this.advertisers[i].getColor());
            ++i;
        }
    }

    public Component getMainPane() {
        return this.mainPane;
    }

}

