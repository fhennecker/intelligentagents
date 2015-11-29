/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.logviewer.gui;

import edu.umich.eecs.tac.logviewer.gui.AuctionResultsPanel;
import edu.umich.eecs.tac.logviewer.gui.PositiveBoundedRangeModel;
import edu.umich.eecs.tac.logviewer.info.GameInfo;
import edu.umich.eecs.tac.props.Query;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class AuctionResultsDisplay {
    JPanel mainPane = new JPanel();

    public AuctionResultsDisplay(GameInfo gameInfo, PositiveBoundedRangeModel dayModel) {
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gblConstraints = new GridBagConstraints();
        gblConstraints.fill = 1;
        this.mainPane.setLayout(gbl);
        this.mainPane.setBorder(BorderFactory.createTitledBorder("Auction Results by Query"));
        this.mainPane.setToolTipText("Auctions sorted by average position");
        Query[] querySpace = gameInfo.getQuerySpace().toArray(new Query[0]);
        gblConstraints.weightx = 1.0;
        gblConstraints.weighty = 1.0;
        gblConstraints.gridwidth = 1;
        int i = 0;
        while (i < 4) {
            int j = 0;
            while (j < 4) {
                gblConstraints.gridx = i;
                gblConstraints.gridy = j;
                AuctionResultsPanel current = new AuctionResultsPanel(querySpace[4 * i + j], gameInfo, dayModel);
                gbl.setConstraints(current.getMainPane(), gblConstraints);
                this.mainPane.add(current.getMainPane());
                ++j;
            }
            ++i;
        }
    }

    public JPanel getMainPane() {
        return this.mainPane;
    }
}

