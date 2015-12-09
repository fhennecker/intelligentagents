/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.logviewer.gui;

import edu.umich.eecs.tac.logviewer.gui.PositiveBoundedRangeModel;
import edu.umich.eecs.tac.logviewer.gui.PositiveRangeDiagram;
import edu.umich.eecs.tac.logviewer.info.Advertiser;
import edu.umich.eecs.tac.logviewer.info.GameInfo;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class GlobalAccountPanel {
    JPanel mainPane = new JPanel();
    PositiveRangeDiagram accountDiagram;

    public GlobalAccountPanel(GameInfo simInfo, PositiveBoundedRangeModel dayModel) {
        this.mainPane.setLayout(new BorderLayout());
        this.mainPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Account Balance "));
        this.mainPane.setMinimumSize(new Dimension(280, 200));
        this.mainPane.setPreferredSize(new Dimension(280, 200));
        this.accountDiagram = new PositiveRangeDiagram(simInfo.getAdvertiserCount(), dayModel);
        this.accountDiagram.addConstant(Color.black, 0);
        int i = 0;
        int n = simInfo.getAdvertiserCount();
        while (i < n) {
            this.accountDiagram.setData(i, simInfo.getAdvertiser(i).getAccountBalance(), 1);
            this.accountDiagram.setDotColor(i, simInfo.getAdvertiser(i).getColor());
            ++i;
        }
        this.accountDiagram.setToolTipText("Account balance for all agents");
        this.mainPane.add((Component)this.accountDiagram, "Center");
    }

    public JPanel getMainPane() {
        return this.mainPane;
    }
}

