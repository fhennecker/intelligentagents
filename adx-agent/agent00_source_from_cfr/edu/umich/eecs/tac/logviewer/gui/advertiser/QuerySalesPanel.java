/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.logviewer.gui.advertiser;

import edu.umich.eecs.tac.logviewer.gui.PositiveBoundedRangeModel;
import edu.umich.eecs.tac.logviewer.info.Advertiser;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.SalesReport;
import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class QuerySalesPanel {
    JPanel mainPane;
    JLabel costLabel;
    JLabel revenueLabel;
    JLabel profitLabel;
    public static final String COST_STRING = "Cost: ";
    public static final String REVENUE_STRING = "Revenue: ";
    public static final String PROFIT_STRING = "Profit: ";
    public static final DecimalFormat dFormat = new DecimalFormat("$#0.00");
    Query query;
    Advertiser advertiser;
    PositiveBoundedRangeModel dayModel;

    public QuerySalesPanel(Query query, Advertiser advertiser, PositiveBoundedRangeModel dm) {
        this.query = query;
        this.advertiser = advertiser;
        this.dayModel = dm;
        if (this.dayModel != null) {
            this.dayModel.addChangeListener(new ChangeListener(){

                @Override
                public void stateChanged(ChangeEvent ce) {
                    QuerySalesPanel.this.updateMePlz();
                }
            });
        }
        this.mainPane = new JPanel();
        this.mainPane.setLayout(new BoxLayout(this.mainPane, 1));
        this.mainPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), query.toString()));
        this.costLabel = new JLabel();
        this.revenueLabel = new JLabel();
        this.profitLabel = new JLabel();
        this.mainPane.add(this.costLabel);
        this.mainPane.add(this.revenueLabel);
        this.mainPane.add(this.profitLabel);
        this.updateMePlz();
    }

    private void updateMePlz() {
        int current = this.dayModel.getCurrent();
        QueryReport q_report = this.advertiser.getQueryReport(current + 1);
        SalesReport s_report = this.advertiser.getSalesReport(current + 1);
        if (q_report == null || s_report == null) {
            this.setDefaultText();
        } else {
            double cost = q_report.getCost(this.query);
            double revenue = s_report.getRevenue(this.query);
            double profit = revenue - cost;
            this.costLabel.setText("Cost: " + dFormat.format(cost));
            this.revenueLabel.setText("Revenue: " + dFormat.format(revenue));
            this.profitLabel.setText("Profit: " + dFormat.format(profit));
        }
    }

    private void setDefaultText() {
        this.costLabel.setText("Cost: $0.00");
        this.revenueLabel.setText("Revenue: $0.00");
        this.profitLabel.setText("Profit: $0.00");
    }

    public Component getMainPane() {
        return this.mainPane;
    }

}

