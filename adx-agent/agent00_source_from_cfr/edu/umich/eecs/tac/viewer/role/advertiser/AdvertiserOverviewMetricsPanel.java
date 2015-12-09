/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.props.AdvertiserInfo;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.SalesReport;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.viewer.ViewListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import se.sics.isl.transport.Transportable;

public class AdvertiserOverviewMetricsPanel
extends JPanel {
    private AdvertiserMetricsModel model;

    public AdvertiserOverviewMetricsPanel(TACAASimulationPanel simulationPanel) {
        this.model = new AdvertiserMetricsModel(simulationPanel, null);
        this.initialize();
    }

    private void initialize() {
        this.setLayout(new GridLayout(1, 1));
        this.setBorder(BorderFactory.createTitledBorder("Advertiser Information"));
        this.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        MetricsNumberRenderer renderer = new MetricsNumberRenderer();
        JTable table = new JTable(this.model);
        int i = 2;
        while (i < 6) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
            ++i;
        }
        JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane);
    }

    private static class AdvertiserMetricsItem {
        private int agent;
        private String advertiser;
        private int impressions;
        private int clicks;
        private int conversions;
        private double revenue;
        private double cost;
        private AdvertiserInfo advertiserInfo;
        private AdvertiserMetricsModel model;

        private AdvertiserMetricsItem(int agent, String advertiser, AdvertiserMetricsModel model, TACAASimulationPanel simulationPanel) {
            this.agent = agent;
            this.advertiser = advertiser;
            this.model = model;
            simulationPanel.addViewListener(new DataUpdateListener(this, null));
        }

        public int getAgent() {
            return this.agent;
        }

        public String getAdvertiser() {
            return this.advertiser;
        }

        public double getProfit() {
            return this.revenue - this.cost;
        }

        public double getCapacity() {
            return this.advertiserInfo != null ? (double)this.advertiserInfo.getDistributionCapacity() : Double.NaN;
        }

        public double getCTR() {
            return this.impressions > 0 ? (double)this.clicks / (double)this.impressions : Double.NaN;
        }

        public double getConvRate() {
            return this.clicks > 0 ? (double)this.conversions / (double)this.clicks : Double.NaN;
        }

        public double getCPC() {
            return this.cost / (double)this.clicks;
        }

        public double getCPM() {
            return 1000.0 * this.cost / (double)this.impressions;
        }

        public double getVPC() {
            return (this.revenue - this.cost) / (double)this.clicks;
        }

        public double getROI() {
            return (this.revenue - this.cost) / this.cost;
        }

        protected void addRevenue(double revenue) {
            this.revenue += revenue;
            this.model.fireUpdatedAgent(this.agent);
        }

        protected void addCost(double cost) {
            this.cost += cost;
            this.model.fireUpdatedAgent(this.agent);
        }

        protected void addImpressions(int impressions) {
            this.impressions += impressions;
            this.model.fireUpdatedAgent(this.agent);
        }

        protected void addClicks(int clicks) {
            this.clicks += clicks;
        }

        protected void addConversions(int conversions) {
            this.conversions += conversions;
            this.model.fireUpdatedAgent(this.agent);
        }

        public void setAdvertiserInfo(AdvertiserInfo advertiserInfo) {
            this.advertiserInfo = advertiserInfo;
            this.model.fireUpdatedAgent(this.agent);
        }

        public AdvertiserInfo getAdvertiserInfo() {
            return this.advertiserInfo;
        }

        /* synthetic */ AdvertiserMetricsItem(int n, String string, AdvertiserMetricsModel advertiserMetricsModel, TACAASimulationPanel tACAASimulationPanel, AdvertiserMetricsItem advertiserMetricsItem) {
            AdvertiserMetricsItem advertiserMetricsItem2;
            advertiserMetricsItem2(n, string, advertiserMetricsModel, tACAASimulationPanel);
        }
    }

    private static class AdvertiserMetricsModel
    extends AbstractTableModel {
        private static final String[] COLUMN_NAMES = new String[]{"Agent", "Capacity", "Profit", "CPC", "VPC", "ROI"};
        List<AdvertiserMetricsItem> data;
        Map<Integer, AdvertiserMetricsItem> agents;

        private AdvertiserMetricsModel(final TACAASimulationPanel simulationPanel) {
            this.data = new ArrayList<AdvertiserMetricsItem>();
            this.agents = new HashMap<Integer, AdvertiserMetricsItem>();
            simulationPanel.addViewListener(new ViewAdaptor(){

                @Override
                public void participant(int agent, int role, String name, int participantID) {
                    if (role == 1 && !AdvertiserMetricsModel.this.agents.containsKey(agent)) {
                        AdvertiserMetricsItem item = new AdvertiserMetricsItem(agent, name, AdvertiserMetricsModel.this, simulationPanel, null);
                        AdvertiserMetricsModel.this.agents.put(agent, item);
                        AdvertiserMetricsModel.this.data.add(item);
                        AdvertiserMetricsModel.this.fireTableDataChanged();
                    }
                }
            });
        }

        public void fireUpdatedAgent(int agent) {
            int i = 0;
            while (i < this.data.size()) {
                if (this.data.get(i).getAgent() == agent) {
                    this.fireTableRowsUpdated(i, i);
                }
                ++i;
            }
        }

        @Override
        public int getRowCount() {
            return this.data.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMN_NAMES.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return this.data.get(rowIndex).getAdvertiser();
            }
            if (columnIndex == 1) {
                return this.data.get(rowIndex).getCapacity();
            }
            if (columnIndex == 2) {
                return this.data.get(rowIndex).getProfit();
            }
            if (columnIndex == 3) {
                return this.data.get(rowIndex).getCPC();
            }
            if (columnIndex == 4) {
                return this.data.get(rowIndex).getVPC();
            }
            if (columnIndex == 5) {
                return this.data.get(rowIndex).getROI();
            }
            return null;
        }

        @Override
        public String getColumnName(int column) {
            return COLUMN_NAMES[column];
        }

        /* synthetic */ AdvertiserMetricsModel(TACAASimulationPanel tACAASimulationPanel, AdvertiserMetricsModel advertiserMetricsModel) {
            AdvertiserMetricsModel advertiserMetricsModel2;
            advertiserMetricsModel2(tACAASimulationPanel);
        }

    }

    private static class DataUpdateListener
    extends ViewAdaptor {
        private AdvertiserMetricsItem item;

        private DataUpdateListener(AdvertiserMetricsItem item) {
            this.item = item;
        }

        @Override
        public void dataUpdated(final int agent, final int type, final int value) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    if (agent == DataUpdateListener.this.item.getAgent()) {
                        switch (type) {
                            case 301: {
                                DataUpdateListener.this.item.addImpressions(value);
                                break;
                            }
                            case 302: {
                                DataUpdateListener.this.item.addClicks(value);
                                break;
                            }
                            case 303: {
                                DataUpdateListener.this.item.addConversions(value);
                            }
                        }
                    }
                }
            });
        }

        @Override
        public void dataUpdated(final int agent, final int type, final Transportable value) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    if (agent == DataUpdateListener.this.item.getAgent()) {
                        switch (type) {
                            case 305: {
                                DataUpdateListener.this.handleSalesReport((SalesReport)value);
                                break;
                            }
                            case 304: {
                                DataUpdateListener.this.handleQueryReport((QueryReport)value);
                                break;
                            }
                            case 307: {
                                DataUpdateListener.this.handleAdvertiserInfo((AdvertiserInfo)value);
                            }
                        }
                    }
                }
            });
        }

        private void handleAdvertiserInfo(AdvertiserInfo advertiserInfo) {
            this.item.setAdvertiserInfo(advertiserInfo);
        }

        private void handleQueryReport(QueryReport queryReport) {
            double cost = 0.0;
            int i = 0;
            while (i < queryReport.size()) {
                cost += queryReport.getCost(i);
                ++i;
            }
            this.item.addCost(cost);
        }

        private void handleSalesReport(SalesReport salesReport) {
            double revenue = 0.0;
            int i = 0;
            while (i < salesReport.size()) {
                revenue += salesReport.getRevenue(i);
                ++i;
            }
            this.item.addRevenue(revenue);
        }

        /* synthetic */ DataUpdateListener(AdvertiserMetricsItem advertiserMetricsItem, DataUpdateListener dataUpdateListener) {
            DataUpdateListener dataUpdateListener2;
            dataUpdateListener2(advertiserMetricsItem);
        }

    }

    public class MetricsNumberRenderer
    extends JLabel
    implements TableCellRenderer {
        public MetricsNumberRenderer() {
            this.setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object object, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                this.setBackground(table.getSelectionBackground());
                this.setForeground(table.getSelectionForeground());
            } else {
                this.setBackground(table.getBackground());
                this.setForeground(table.getForeground());
            }
            this.setHorizontalAlignment(4);
            this.setText(String.format("%.2f", object));
            return this;
        }
    }

}

