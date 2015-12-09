/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.logviewer.gui;

import edu.umich.eecs.tac.logviewer.gui.AdvertiserDisplay;
import edu.umich.eecs.tac.logviewer.gui.AuctionResultsDisplay;
import edu.umich.eecs.tac.logviewer.gui.DayChanger;
import edu.umich.eecs.tac.logviewer.gui.GlobalAccountPanel;
import edu.umich.eecs.tac.logviewer.gui.ParamsPanel;
import edu.umich.eecs.tac.logviewer.gui.PopulationPanel;
import edu.umich.eecs.tac.logviewer.gui.PositiveBoundedRangeModel;
import edu.umich.eecs.tac.logviewer.info.GameInfo;
import edu.umich.eecs.tac.logviewer.monitor.ParserMonitor;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainWindow
extends JFrame {
    ParamsPanel paramsPane;
    GlobalAccountPanel accountPane;
    DayChanger dayChanger;
    AdvertiserDisplay advertiserDisplay;
    PopulationPanel populationPanel;
    AuctionResultsDisplay auctionResultsDisplay;

    public MainWindow(GameInfo gameInfo, PositiveBoundedRangeModel dayModel, ParserMonitor[] monitors) {
        super("TAC AA Visualizer - main window");
        this.setDefaultCloseOperation(3);
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gblConstraints = new GridBagConstraints();
        Container pane = this.getContentPane();
        pane.setLayout(gbl);
        this.dayChanger = new DayChanger(dayModel);
        this.accountPane = new GlobalAccountPanel(gameInfo, dayModel);
        this.paramsPane = new ParamsPanel(gameInfo);
        this.advertiserDisplay = new AdvertiserDisplay(gameInfo, dayModel, monitors);
        this.populationPanel = new PopulationPanel(gameInfo);
        this.auctionResultsDisplay = new AuctionResultsDisplay(gameInfo, dayModel);
        gblConstraints.fill = 2;
        gblConstraints.anchor = 18;
        gblConstraints.weighty = 0.0;
        gblConstraints.gridx = 0;
        gblConstraints.gridy = 0;
        gbl.setConstraints(this.paramsPane.getMainPane(), gblConstraints);
        pane.add(this.paramsPane.getMainPane());
        gblConstraints.gridx = 0;
        gblConstraints.gridy = 1;
        gbl.setConstraints(this.accountPane.getMainPane(), gblConstraints);
        pane.add(this.accountPane.getMainPane());
        gblConstraints.gridx = 0;
        gblConstraints.gridy = 2;
        gbl.setConstraints(this.dayChanger.getMainPane(), gblConstraints);
        pane.add(this.dayChanger.getMainPane());
        gblConstraints.gridx = 0;
        gblConstraints.gridy = 3;
        gbl.setConstraints(this.populationPanel.getMainPane(), gblConstraints);
        pane.add(this.populationPanel.getMainPane());
        gblConstraints.fill = 2;
        gblConstraints.gridx = 1;
        gblConstraints.gridy = 0;
        gblConstraints.gridheight = 5;
        gbl.setConstraints(this.advertiserDisplay.getMainPane(), gblConstraints);
        pane.add(this.advertiserDisplay.getMainPane());
        gblConstraints.gridx = 2;
        gblConstraints.gridy = 0;
        gblConstraints.weightx = 1.0;
        gblConstraints.weighty = 1.0;
        gbl.setConstraints(this.auctionResultsDisplay.getMainPane(), gblConstraints);
        pane.add(this.auctionResultsDisplay.getMainPane());
        this.pack();
        this.setLocationRelativeTo(null);
    }
}

