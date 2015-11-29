/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.AbstractBorder;

public class WindowBorder
extends AbstractBorder {
    private int borderSize = 3;
    private int titleHeight = 8;

    public int getTitleHeight() {
        return this.titleHeight + this.borderSize;
    }

    public int getBorderSize() {
        return this.borderSize;
    }

    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(this.titleHeight + this.borderSize, this.borderSize, this.borderSize, this.borderSize);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.top = this.titleHeight + this.borderSize;
        insets.left = this.borderSize;
        insets.bottom = this.borderSize;
        insets.right = this.borderSize;
        return insets;
    }

    public boolean isInTitle(Component c, int x, int y) {
        if (y <= 12) {
            return true;
        }
        return false;
    }

    public boolean isInCloseButton(Component c, int x, int y) {
        int cWidth = c.getWidth();
        int cHeight = c.getHeight();
        if (cWidth - 12 <= x && cWidth - 2 >= x && 2 <= y && 12 >= y) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Color oldColor = g.getColor();
        g.setColor(Color.white);
        g.drawRect(x, y, width - 1, height - 1);
        g.setColor(c.getBackground());
        g.drawRect(x + 2, y + 2, width - 5, height - 5);
        g.setColor(Color.black);
        g.drawRect(x + 1, y + 1, width - 3, height - 3);
        g.setColor(c.getBackground());
        g.fillRect(x += 2, y += 2, (width -= 4) - 6, 8);
        g.setColor(Color.lightGray);
        g.fillRect(x + width - 9, y, 9, 8);
        g.setColor(Color.black);
        g.drawLine(x, y + 2, x + width - 11, y + 2);
        g.drawLine(x, y + 5, x + width - 11, y + 5);
        g.drawLine(x, y + 8, x + width - 11, y + 8);
        g.drawRect(x + width - 10, y - 1, 10, 9);
        g.drawLine(x + width - 8, y + 1, x + width - 2, y + 7);
        g.drawLine(x + width - 8, y + 7, x + width - 2, y + 1);
        g.setColor(Color.white);
        g.drawLine(x + width - 8, y, x + width - 2, y + 6);
        g.drawLine(x + width - 8, y + 6, x + width - 2, y);
        g.setColor(oldColor);
    }
}

