/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.logviewer.gui;

import edu.umich.eecs.tac.logviewer.info.GameInfo;
import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class ParamsPanel {
    private JPanel mainPane = new JPanel();
    JLabel simulationID;
    JLabel secondsPerDay;
    JLabel numberOfDays;
    JLabel squash;
    JLabel server;
    JLabel storageCostLabel;
    JLabel suppNomCap;
    JLabel suppMaxRFQs;
    JLabel suppDiscountFactor;

    public ParamsPanel(GameInfo gameInfo) {
        this.mainPane.setLayout(new BoxLayout(this.mainPane, 1));
        this.mainPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Simulation Parameters "));
        this.simulationID = new JLabel("Simulation: " + gameInfo.getSimulationID() + " (" + gameInfo.getSimulationType() + ')');
        this.server = new JLabel("Server: " + gameInfo.getServer());
        this.secondsPerDay = new JLabel("Seconds per day: " + gameInfo.getSecondsPerDay());
        this.numberOfDays = new JLabel("Number of days: " + gameInfo.getNumberOfDays());
        DecimalFormat squashFormat = new DecimalFormat("#.###");
        this.squash = new JLabel("Squashing Parameter: " + squashFormat.format(gameInfo.getSquashingParameter()));
        this.mainPane.add(this.server);
        this.mainPane.add(this.simulationID);
        this.mainPane.add(this.secondsPerDay);
        this.mainPane.add(this.numberOfDays);
        this.mainPane.add(this.squash);
    }

    public JPanel getMainPane() {
        return this.mainPane;
    }
}

