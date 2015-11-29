/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.auction;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.auction.AdRenderer;
import edu.umich.eecs.tac.viewer.auction.ResultsPageModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.border.Border;

public class AverageRankingPanel
extends JPanel {
    private final ResultsPageModel model;

    public AverageRankingPanel(ResultsPageModel model) {
        this.model = model;
        this.initialize();
    }

    private Query getQuery() {
        return this.model.getQuery();
    }

    private void initialize() {
        this.setBorder(BorderFactory.createTitledBorder(String.format("(%s,%s) average results", this.getQuery().getManufacturer(), this.getQuery().getComponent())));
        this.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        JList resultsList = new JList(this.model);
        resultsList.setCellRenderer(new AdRenderer(this.getQuery()));
        resultsList.setSelectionMode(0);
        this.setLayout(new GridLayout(1, 1));
        this.add(new JScrollPane(resultsList));
    }
}

