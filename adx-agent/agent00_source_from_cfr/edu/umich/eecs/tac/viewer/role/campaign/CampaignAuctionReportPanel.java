/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.role.campaign;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import tau.tac.adx.report.demand.campaign.auction.CampaignAuctionReport;
import tau.tac.adx.report.demand.campaign.auction.CampaignAuctionReportEntry;
import tau.tac.adx.report.demand.campaign.auction.CampaignAuctionReportKey;

public class CampaignAuctionReportPanel
extends JPanel {
    private static final long serialVersionUID = 1;
    private final AdvertiserMetricsModel model;
    private int winnerRow;

    public CampaignAuctionReportPanel(CampaignAuctionReport campaignAuctionReport, TACAASimulationPanel simulationPanel) {
        this.model = new AdvertiserMetricsModel(campaignAuctionReport, simulationPanel, null);
        this.winnerRow = 0;
        int i = 0;
        while (i < campaignAuctionReport.size()) {
            CampaignAuctionReportEntry entry = (CampaignAuctionReportEntry)campaignAuctionReport.getEntry(i);
            if (((CampaignAuctionReportKey)entry.getKey()).getAdnetName().equals(campaignAuctionReport.getWinner())) break;
            ++this.winnerRow;
            ++i;
        }
        this.initialize();
    }

    private void initialize() {
        this.setLayout(new GridLayout(1, 1));
        this.setBorder(BorderFactory.createTitledBorder("Advertiser Information"));
        this.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        MetricsNumberRenderer renderer = new MetricsNumberRenderer();
        JTable table = new JTable(this.model);
        int i = 1;
        while (i < 4) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
            ++i;
        }
        JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane);
    }

    private static class AdvertiserMetricsModel
    extends AbstractTableModel {
        private static final long serialVersionUID = 1;
        private static final String[] COLUMN_NAMES = new String[]{"Ad Network", "Effective bid", "Actual Bid", "Quality Rating"};
        private CampaignAuctionReport campaignAuctionReport;

        private AdvertiserMetricsModel(CampaignAuctionReport campaignAuctionReport, TACAASimulationPanel simulationPanel) {
            this.campaignAuctionReport = campaignAuctionReport;
        }

        @Override
        public int getRowCount() {
            return this.campaignAuctionReport.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMN_NAMES.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            CampaignAuctionReportEntry entry = (CampaignAuctionReportEntry)this.campaignAuctionReport.getEntry(rowIndex);
            if (columnIndex == 0) {
                return ((CampaignAuctionReportKey)entry.getKey()).getAdnetName();
            }
            if (columnIndex == 1) {
                return entry.getEffctiveBid();
            }
            if (columnIndex == 2) {
                return entry.getActualBid();
            }
            if (columnIndex == 3) {
                return entry.getEffctiveBid() / entry.getActualBid();
            }
            return null;
        }

        @Override
        public String getColumnName(int column) {
            return COLUMN_NAMES[column];
        }

        /* synthetic */ AdvertiserMetricsModel(CampaignAuctionReport campaignAuctionReport, TACAASimulationPanel tACAASimulationPanel, AdvertiserMetricsModel advertiserMetricsModel) {
            AdvertiserMetricsModel advertiserMetricsModel2;
            advertiserMetricsModel2(campaignAuctionReport, tACAASimulationPanel);
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
            } else if (row == CampaignAuctionReportPanel.this.winnerRow) {
                if (row == 0) {
                    this.setBackground(Color.orange);
                } else {
                    this.setBackground(Color.magenta);
                }
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

