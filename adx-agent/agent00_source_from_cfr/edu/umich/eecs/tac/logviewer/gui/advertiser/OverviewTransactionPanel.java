/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.logviewer.gui.advertiser;

import edu.umich.eecs.tac.logviewer.gui.PositiveBoundedRangeModel;
import edu.umich.eecs.tac.logviewer.gui.UpdatablePanel;
import edu.umich.eecs.tac.logviewer.info.Advertiser;
import edu.umich.eecs.tac.logviewer.info.GameInfo;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.SalesReport;
import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.text.DecimalFormat;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class OverviewTransactionPanel
extends UpdatablePanel {
    JLabel costLabel;
    JLabel revenueLabel;
    JLabel profitLabel;
    public static final String COST_STRING = "Total Cost: ";
    public static final String REVENUE_STRING = "Total Revenue: ";
    public static final String PROFIT_STRING = "Total Profit: ";
    public static final DecimalFormat dFormat = new DecimalFormat("$#0.00");
    Query[] querySpace;
    Advertiser advertiser;

    public OverviewTransactionPanel(Advertiser advertiser, PositiveBoundedRangeModel dm, GameInfo gameInfo) {
        super(dm);
        this.advertiser = advertiser;
        this.querySpace = gameInfo.getQuerySpace().toArray(new Query[0]);
        this.mainPane.setLayout(new BoxLayout(this.mainPane, 1));
        this.mainPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Transactions"));
        this.costLabel = new JLabel();
        this.revenueLabel = new JLabel();
        this.profitLabel = new JLabel();
        this.mainPane.add(this.costLabel);
        this.mainPane.add(this.revenueLabel);
        this.mainPane.add(this.profitLabel);
        this.updateMePlz();
    }

    private void setDefaultText() {
        this.costLabel.setText("Total Cost: $0.00");
        this.revenueLabel.setText("Total Revenue: $0.00");
        this.profitLabel.setText("Total Profit: $0.00");
    }

    @Override
    protected void updateMePlz() {
        int current = this.dayModel.getCurrent();
        QueryReport q_report = this.advertiser.getQueryReport(current + 1);
        SalesReport s_report = this.advertiser.getSalesReport(current + 1);
        if (q_report == null || s_report == null) {
            this.setDefaultText();
        } else {
            double cost = 0.0;
            double revenue = 0.0;
            int i = 0;
            while (i < this.querySpace.length) {
                cost += q_report.getCost(this.querySpace[i]);
                revenue += s_report.getRevenue(this.querySpace[i]);
                ++i;
            }
            double profit = revenue - cost;
            this.costLabel.setText("Total Cost: " + dFormat.format(cost));
            this.revenueLabel.setText("Total Revenue: " + dFormat.format(revenue));
            this.profitLabel.setText("Total Profit: " + dFormat.format(profit));
        }
    }
}

