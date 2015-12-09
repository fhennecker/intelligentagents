/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Font;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.border.Border;
import se.sics.isl.gui.MessageModel;

public class MessageRenderer
extends DefaultListCellRenderer {
    private MessageModel model;

    public MessageRenderer(MessageModel model) {
        this.model = model;
        this.setBorder(null);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        int flag = this.model.getFlagAt(index);
        this.setComponentOrientation(list.getComponentOrientation());
        this.setBackground(list.getBackground());
        this.setForeground(flag == 1 ? Color.red : list.getForeground());
        if (value instanceof Icon) {
            this.setIcon((Icon)value);
            this.setText("");
        } else {
            this.setIcon(null);
            this.setText(value == null ? "" : value.toString());
        }
        this.setEnabled(list.isEnabled());
        this.setFont(list.getFont());
        return this;
    }
}

