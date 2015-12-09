/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.logviewer.gui.advertiser;

import edu.umich.eecs.tac.logviewer.gui.DayChanger;
import edu.umich.eecs.tac.logviewer.gui.PositiveBoundedRangeModel;
import edu.umich.eecs.tac.logviewer.gui.PositiveRangeDiagram;
import edu.umich.eecs.tac.logviewer.gui.advertiser.OverviewBidPanel;
import edu.umich.eecs.tac.logviewer.gui.advertiser.OverviewTransactionPanel;
import edu.umich.eecs.tac.logviewer.gui.advertiser.OverviewUserMetricPanel;
import edu.umich.eecs.tac.logviewer.gui.advertiser.QueryBidPanel;
import edu.umich.eecs.tac.logviewer.gui.advertiser.QueryRatioPanel;
import edu.umich.eecs.tac.logviewer.gui.advertiser.QuerySalesPanel;
import edu.umich.eecs.tac.logviewer.gui.advertiser.QueryUserInteractionPanel;
import edu.umich.eecs.tac.logviewer.info.Actor;
import edu.umich.eecs.tac.logviewer.info.Advertiser;
import edu.umich.eecs.tac.logviewer.info.GameInfo;
import edu.umich.eecs.tac.logviewer.monitor.ParserMonitor;
import edu.umich.eecs.tac.props.Query;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;

public class AdvertiserWindow
extends JFrame {
    private static final int ROOT_NUM_QUERIES = 4;
    GameInfo gameInfo;
    DayChanger dayChanger;
    Advertiser advertiser;
    int advertiserIndex;
    PositiveBoundedRangeModel dayModel;
    Query[] querySpace;

    public AdvertiserWindow(GameInfo gameInfo, Advertiser advertiser, PositiveBoundedRangeModel dayModel, ParserMonitor[] monitors) {
        super(advertiser.getName());
        this.setDefaultCloseOperation(2);
        this.gameInfo = gameInfo;
        this.advertiser = advertiser;
        this.advertiserIndex = gameInfo.getAdvertiserIndex(advertiser);
        this.querySpace = gameInfo.getQuerySpace().toArray(new Query[0]);
        this.dayModel = dayModel;
        this.dayChanger = new DayChanger(dayModel);
        JTabbedPane tabPane = new JTabbedPane();
        tabPane.addTab("Overview", null, this.createOverviewPane(), "Advertiser overview information");
        tabPane.addTab("Bidding Metrics", null, this.createBiddingPane(), "Information about bidding");
        tabPane.addTab("User Metrics", null, this.createUserInteractionPane(), "Information about user interactions");
        tabPane.addTab("Transactions", null, this.createSalesPane(), "Information about sales");
        if (monitors != null) {
            int i = 0;
            int n = monitors.length;
            while (i < n) {
                if (monitors[i].hasAgentView(advertiser)) {
                    tabPane.addTab("Monitor " + monitors[i].getName(), null, monitors[i].getAgentView(advertiser), "Information from the monitor " + monitors[i].getName());
                }
                ++i;
            }
        }
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add((Component)tabPane, "Center");
        this.getContentPane().add((Component)this.dayChanger.getMainPane(), "South");
        this.pack();
    }

    protected JPanel createBankPane() {
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gblConstraints = new GridBagConstraints();
        gblConstraints.fill = 1;
        JPanel pane = new JPanel();
        pane.setLayout(gbl);
        PositiveRangeDiagram accountDiagram = new PositiveRangeDiagram(1, this.dayModel);
        accountDiagram.setPreferredSize(new Dimension(200, 150));
        accountDiagram.setData(0, this.advertiser.getAccountBalance(), 1);
        accountDiagram.addConstant(Color.black, 0);
        accountDiagram.setBorder(BorderFactory.createTitledBorder(""));
        accountDiagram.setTitle(0, "Account Balance: $", "");
        gblConstraints.weightx = 1.0;
        gblConstraints.weighty = 1.0;
        gblConstraints.gridwidth = 3;
        gblConstraints.gridx = 0;
        gblConstraints.gridy = 0;
        gbl.setConstraints(accountDiagram, gblConstraints);
        pane.add(accountDiagram);
        int[] diff = new int[this.dayModel.getLast() - 1];
        diff[0] = 0;
        int i = 1;
        int n = this.dayModel.getLast() - 1;
        while (i < n) {
            diff[i] = (this.advertiser.getAccountBalance(i + 1) - this.advertiser.getAccountBalance(i - 1)) / 2;
            ++i;
        }
        PositiveRangeDiagram diffDiagram = new PositiveRangeDiagram(1, this.dayModel);
        diffDiagram.setPreferredSize(new Dimension(200, 150));
        diffDiagram.setData(0, diff, 1);
        diffDiagram.addConstant(Color.black, 0);
        diffDiagram.setBorder(BorderFactory.createTitledBorder(""));
        diffDiagram.setTitle(0, "Account diff: $", "");
        gblConstraints.weightx = 1.0;
        gblConstraints.weighty = 1.0;
        gblConstraints.gridwidth = 1;
        gblConstraints.gridx = 0;
        gblConstraints.gridy = 1;
        gbl.setConstraints(diffDiagram, gblConstraints);
        pane.add(diffDiagram);
        return pane;
    }

    protected JPanel createOverviewPane() {
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gblConstraints = new GridBagConstraints();
        gblConstraints.fill = 1;
        JPanel pane = new JPanel();
        pane.setLayout(gbl);
        PositiveRangeDiagram accountDiagram = new PositiveRangeDiagram(1, this.dayModel);
        accountDiagram.setPreferredSize(new Dimension(200, 150));
        accountDiagram.setData(0, this.advertiser.getAccountBalance(), 1);
        accountDiagram.addConstant(Color.black, 0);
        accountDiagram.setBorder(BorderFactory.createTitledBorder(""));
        accountDiagram.setTitle(0, "Account Balance: $", "");
        gblConstraints.weightx = 1.0;
        gblConstraints.weighty = 1.0;
        gblConstraints.gridwidth = 3;
        gblConstraints.gridx = 0;
        gblConstraints.gridy = 0;
        gbl.setConstraints(accountDiagram, gblConstraints);
        pane.add(accountDiagram);
        OverviewBidPanel obp = new OverviewBidPanel(this.advertiser, this.dayModel, this.gameInfo, this.gameInfo.getNumberOfDays());
        gblConstraints.weightx = 1.0;
        gblConstraints.weighty = 1.0;
        gblConstraints.gridwidth = 1;
        gblConstraints.gridx = 1;
        gblConstraints.gridy = 1;
        gbl.setConstraints(obp.getMainPane(), gblConstraints);
        pane.add(obp.getMainPane());
        OverviewUserMetricPanel oump = new OverviewUserMetricPanel(this.advertiser, this.dayModel, this.gameInfo);
        gblConstraints.weightx = 1.0;
        gblConstraints.weighty = 1.0;
        gblConstraints.gridwidth = 1;
        gblConstraints.gridx = 2;
        gblConstraints.gridy = 1;
        gbl.setConstraints(oump.getMainPane(), gblConstraints);
        pane.add(oump.getMainPane());
        OverviewTransactionPanel otp = new OverviewTransactionPanel(this.advertiser, this.dayModel, this.gameInfo);
        gblConstraints.weightx = 1.0;
        gblConstraints.weighty = 1.0;
        gblConstraints.gridwidth = 1;
        gblConstraints.gridx = 0;
        gblConstraints.gridy = 1;
        gbl.setConstraints(otp.getMainPane(), gblConstraints);
        pane.add(otp.getMainPane());
        return pane;
    }

    protected JPanel createBiddingPane() {
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gblConstraints = new GridBagConstraints();
        gblConstraints.fill = 1;
        JPanel pane = new JPanel();
        pane.setLayout(gbl);
        gblConstraints.weightx = 1.0;
        gblConstraints.weighty = 1.0;
        gblConstraints.gridwidth = 1;
        int i = 0;
        while (i < 4) {
            int j = 0;
            while (j < 4) {
                gblConstraints.gridx = i;
                gblConstraints.gridy = j;
                QueryBidPanel current = new QueryBidPanel(this.querySpace[4 * i + j], this.advertiser, this.dayModel, this.gameInfo.getNumberOfDays(), this.querySpace);
                gbl.setConstraints(current.getMainPane(), gblConstraints);
                pane.add(current.getMainPane());
                ++j;
            }
            ++i;
        }
        return pane;
    }

    protected JPanel createSalesPane() {
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gblConstraints = new GridBagConstraints();
        gblConstraints.fill = 1;
        JPanel pane = new JPanel();
        pane.setLayout(gbl);
        gblConstraints.weightx = 1.0;
        gblConstraints.weighty = 1.0;
        gblConstraints.gridwidth = 1;
        int i = 0;
        while (i < 4) {
            int j = 0;
            while (j < 4) {
                gblConstraints.gridx = i;
                gblConstraints.gridy = j;
                QuerySalesPanel current = new QuerySalesPanel(this.querySpace[4 * i + j], this.advertiser, this.dayModel);
                gbl.setConstraints(current.getMainPane(), gblConstraints);
                pane.add(current.getMainPane());
                ++j;
            }
            ++i;
        }
        return pane;
    }

    protected JPanel createRatioPane() {
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gblConstraints = new GridBagConstraints();
        gblConstraints.fill = 1;
        JPanel pane = new JPanel();
        pane.setLayout(gbl);
        gblConstraints.weightx = 1.0;
        gblConstraints.weighty = 1.0;
        gblConstraints.gridwidth = 1;
        int i = 0;
        while (i < 4) {
            int j = 0;
            while (j < 4) {
                gblConstraints.gridx = i;
                gblConstraints.gridy = j;
                QueryRatioPanel current = new QueryRatioPanel(this.querySpace[4 * i + j], this.advertiser, this.dayModel);
                gbl.setConstraints(current.getMainPane(), gblConstraints);
                pane.add(current.getMainPane());
                ++j;
            }
            ++i;
        }
        return pane;
    }

    protected JPanel createUserInteractionPane() {
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gblConstraints = new GridBagConstraints();
        gblConstraints.fill = 1;
        JPanel pane = new JPanel();
        pane.setLayout(gbl);
        gblConstraints.weightx = 1.0;
        gblConstraints.weighty = 1.0;
        gblConstraints.gridwidth = 1;
        int i = 0;
        while (i < 4) {
            int j = 0;
            while (j < 4) {
                gblConstraints.gridx = i;
                gblConstraints.gridy = j;
                QueryUserInteractionPanel current = new QueryUserInteractionPanel(this.querySpace[4 * i + j], this.advertiser, this.dayModel);
                gbl.setConstraints(current.getMainPane(), gblConstraints);
                pane.add(current.getMainPane());
                ++j;
            }
            ++i;
        }
        return pane;
    }

    private PositiveRangeDiagram createDiagram(Dimension preferredSize, int[] data, String titlePrefix, String titlePostfix) {
        PositiveRangeDiagram diagram = new PositiveRangeDiagram(1, this.dayModel);
        diagram.setPreferredSize(preferredSize);
        diagram.setData(0, data, 1);
        diagram.addConstant(Color.black, 0);
        diagram.setBorder(BorderFactory.createTitledBorder(""));
        diagram.setTitle(0, titlePrefix, titlePostfix);
        return diagram;
    }
}

