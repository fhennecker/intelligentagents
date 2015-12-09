/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.role.publisher;

import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.role.publisher.SeriesTabPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class LegendPanel
extends JPanel {
    private SeriesTabPanel seriesTabPanel;
    private Color[] legendColors;

    public LegendPanel(SeriesTabPanel seriesTabPanel, Color[] legendColors) {
        super(new GridLayout(1, 0));
        this.seriesTabPanel = seriesTabPanel;
        this.legendColors = legendColors;
        this.initialize();
    }

    private void initialize() {
        int count = this.seriesTabPanel.getAgentCount();
        JTable table = new JTable(1, 2 * (this.seriesTabPanel.getAgentCount() - 2));
        table.setAutoResizeMode(0);
        table.setBorder(BorderFactory.createEmptyBorder());
        int i = 0;
        while (i < table.getColumnCount()) {
            table.getColumnModel().getColumn(i).setCellRenderer(new LegendColorRenderer(this.legendColors[i / 2]));
            table.getColumnModel().getColumn(i).setPreferredWidth(1);
            i += 2;
        }
        int advertiser = 0;
        int index = 0;
        while (index < count) {
            if (this.seriesTabPanel.getRole(index) == 1) {
                table.getColumnModel().getColumn(advertiser * 2 + 1).setCellRenderer(new LegendTextRenderer(this.seriesTabPanel.getAgentName(index)));
                ++advertiser;
            }
            ++index;
        }
        table.setGridColor(TACAAViewerConstants.CHART_BACKGROUND);
        this.add(table);
    }

    private class LegendColorRenderer
    extends DefaultTableCellRenderer {
        Color bkgndColor;

        public LegendColorRenderer(Color bkgndColor) {
            this.bkgndColor = bkgndColor;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel cell = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            cell.setBackground(this.bkgndColor);
            return cell;
        }
    }

    private class LegendTextRenderer
    extends DefaultTableCellRenderer {
        String agent;

        public LegendTextRenderer(String agent) {
            this.agent = agent;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel cell = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            cell.setText(this.agent);
            return cell;
        }
    }

}

