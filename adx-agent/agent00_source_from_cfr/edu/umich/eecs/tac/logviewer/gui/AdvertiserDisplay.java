/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.logviewer.gui;

import edu.umich.eecs.tac.logviewer.gui.AdvertiserPanel;
import edu.umich.eecs.tac.logviewer.gui.PositiveBoundedRangeModel;
import edu.umich.eecs.tac.logviewer.info.Advertiser;
import edu.umich.eecs.tac.logviewer.info.GameInfo;
import edu.umich.eecs.tac.logviewer.monitor.ParserMonitor;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class AdvertiserDisplay {
    JPanel mainPane = new JPanel();
    AdvertiserPanel[] ap;

    public AdvertiserDisplay(GameInfo gameInfo, PositiveBoundedRangeModel dayModel, ParserMonitor[] monitors) {
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gblConstraints = new GridBagConstraints();
        this.mainPane.setLayout(gbl);
        this.mainPane.setBorder(BorderFactory.createTitledBorder(" Game situation "));
        this.ap = new AdvertiserPanel[gameInfo.getAdvertiserCount()];
        gblConstraints.gridx = 0;
        gblConstraints.weighty = 0.0;
        gblConstraints.anchor = 10;
        int i = 0;
        int n = gameInfo.getAdvertiserCount();
        while (i < n) {
            this.ap[i] = new AdvertiserPanel(gameInfo, gameInfo.getAdvertiser(i), dayModel, monitors);
            gblConstraints.gridy = i + 1;
            gbl.setConstraints(this.ap[i].getMainPane(), gblConstraints);
            this.mainPane.add(this.ap[i].getMainPane(), gblConstraints);
            ++i;
        }
    }

    public JPanel getMainPane() {
        return this.mainPane;
    }
}

