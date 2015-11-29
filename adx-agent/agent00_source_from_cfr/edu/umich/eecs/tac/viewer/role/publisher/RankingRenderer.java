/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.role.publisher;

import edu.umich.eecs.tac.viewer.role.publisher.RankingPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

class RankingRenderer
extends DefaultTableCellRenderer {
    private Color bkgndColor;
    private Color fgndColor;
    private Font cellFont;

    public RankingRenderer(Color bkgnd, Color foregnd) {
        this.bkgndColor = bkgnd;
        this.fgndColor = foregnd;
        this.cellFont = new Font("serif", 1, 12);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        this.fgndColor = ((RankingPanel.MyTableModel)table.getModel()).getRowFgndColor(row);
        this.bkgndColor = ((RankingPanel.MyTableModel)table.getModel()).getRowBkgndColor(row);
        if (value.getClass().equals(Double.class)) {
            value = RankingRenderer.round((Double)value, 3);
        }
        if (value.getClass() == Boolean.class) {
            boolean targeted = (Boolean)value;
            JCheckBox checkBox = new JCheckBox();
            if (targeted) {
                checkBox.setSelected(true);
            }
            checkBox.setForeground(this.fgndColor);
            checkBox.setBackground(this.bkgndColor);
            checkBox.setHorizontalAlignment(0);
            return checkBox;
        }
        JLabel cell = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        cell.setForeground(this.fgndColor);
        cell.setBackground(this.bkgndColor);
        cell.setFont(this.cellFont);
        cell.setHorizontalAlignment(0);
        return cell;
    }

    public static double round(double Rval, int Rpl) {
        double p = Math.pow(10.0, Rpl);
        double tmp = Math.round(Rval *= p);
        return tmp / p;
    }
}

