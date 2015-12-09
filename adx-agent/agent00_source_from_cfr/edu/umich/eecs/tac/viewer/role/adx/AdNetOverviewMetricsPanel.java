/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.role.adx;

import edu.umich.eecs.tac.props.AdvertiserInfo;
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

public class AdNetOverviewMetricsPanel
extends JPanel {
    private static final long serialVersionUID = 1;
    private final AdvertiserMetricsModel model;

    public AdNetOverviewMetricsPanel(TACAASimulationPanel simulationPanel) {
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
        while (i < 4) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
            ++i;
        }
        JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane);
    }

    private static class AdvertiserMetricsItem {
        private final int agent;
        private final String advertiser;
        private int impressions;
        private double revenue;
        private double qualityRating;
        private AdvertiserInfo advertiserInfo;
        private final AdvertiserMetricsModel model;
        private double ucsCost;
        private double adxCost;

        public int getImpressions() {
            return this.impressions;
        }

        public double getQualityRating() {
            return this.qualityRating;
        }

        public double getRevenue() {
            return this.revenue;
        }

        public double getADXCost() {
            return this.adxCost;
        }

        public double getUCSCost() {
            return this.ucsCost;
        }

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
            return this.revenue - this.adxCost - this.ucsCost;
        }

        public double getCapacity() {
            return this.advertiserInfo != null ? (double)this.advertiserInfo.getDistributionCapacity() : Double.NaN;
        }

        protected void addRevenue(double revenue) {
            this.revenue += revenue;
            this.model.fireUpdatedAgent(this.agent);
        }

        protected void addADXCost(double cost) {
            this.adxCost += cost;
            this.model.fireUpdatedAgent(this.agent);
        }

        protected void addUCSCost(double cost) {
            this.ucsCost += cost;
            this.model.fireUpdatedAgent(this.agent);
        }

        protected void addImpressions(int impressions) {
            this.impressions += impressions;
            this.model.fireUpdatedAgent(this.agent);
        }

        protected void setQualityRating(double qualityRating) {
            this.qualityRating = qualityRating;
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
        private static final long serialVersionUID = 1;
        private static final String[] COLUMN_NAMES = new String[]{"Agent", "Quality Rating", "Profit", "revenue", "adx cost", "ucs cost", "impressions"};
        List<AdvertiserMetricsItem> data;
        Map<Integer, AdvertiserMetricsItem> agents;

        private AdvertiserMetricsModel(final TACAASimulationPanel simulationPanel) {
            this.data = new ArrayList<AdvertiserMetricsItem>();
            this.agents = new HashMap<Integer, AdvertiserMetricsItem>();
            simulationPanel.addViewListener(new ViewAdaptor(){

                @Override
                public void participant(int agent, int role, String name, int participantID) {
                    if (role == 5 && !AdvertiserMetricsModel.this.agents.containsKey(agent)) {
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
                return this.data.get(rowIndex).getQualityRating();
            }
            if (columnIndex == 2) {
                return this.data.get(rowIndex).getProfit();
            }
            if (columnIndex == 3) {
                return this.data.get(rowIndex).getRevenue();
            }
            if (columnIndex == 4) {
                return this.data.get(rowIndex).getADXCost();
            }
            if (columnIndex == 5) {
                return this.data.get(rowIndex).getUCSCost();
            }
            if (columnIndex == 6) {
                return this.data.get(rowIndex).getImpressions();
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
        private final AdvertiserMetricsItem item;

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
                            case 407: {
                                DataUpdateListener.this.item.addImpressions(value);
                            }
                        }
                    }
                }
            });
        }

        @Override
        public void dataUpdated(final int agent, final int type, final double value) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    if (agent == DataUpdateListener.this.item.getAgent()) {
                        switch (type) {
                            case 409: {
                                DataUpdateListener.this.item.addRevenue(value);
                                break;
                            }
                            case 412: {
                                DataUpdateListener.this.item.addADXCost(value);
                                break;
                            }
                            case 411: {
                                DataUpdateListener.this.item.addUCSCost(value);
                                break;
                            }
                            case 408: {
                                DataUpdateListener.this.item.setQualityRating(value);
                            }
                        }
                    }
                }
            });
        }

        /* synthetic */ DataUpdateListener(AdvertiserMetricsItem advertiserMetricsItem, DataUpdateListener dataUpdateListener) {
            DataUpdateListener dataUpdateListener2;
            dataUpdateListener2(advertiserMetricsItem);
        }

    }

    public class MetricsNumberRenderer
    extends JLabel
    implements TableCellRenderer {
        private static final long serialVersionUID = 1;

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

