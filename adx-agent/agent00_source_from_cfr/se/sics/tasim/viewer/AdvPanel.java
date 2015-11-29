/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.viewer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.JLabel;
import se.sics.tasim.viewer.ViewerPanel;

public class AdvPanel
extends JLabel {
    private ViewerPanel viewerPanel;

    public AdvPanel(ViewerPanel viewerPanel) {
        super("Viewing games on server " + viewerPanel.getServerName());
        this.viewerPanel = viewerPanel;
        this.setVerticalAlignment(0);
        this.setHorizontalAlignment(0);
        this.setVerticalTextPosition(3);
        this.setHorizontalTextPosition(0);
    }

    @Override
    public Insets getInsets() {
        return this.getInsets(null);
    }

    @Override
    public Insets getInsets(Insets insets) {
        if (insets == null) {
            return new Insets(0, 2, 0, 2);
        }
        insets.right = 2;
        insets.left = 2;
        insets.bottom = 0;
        insets.top = 0;
        return insets;
    }

    @Override
    protected void paintBorder(Graphics g) {
        Color originalColor = g.getColor();
        int width = this.getWidth() - 1;
        int height = this.getHeight() - 1;
        g.setColor(this.getForeground());
        g.drawLine(0, 0, 0, height);
        g.drawLine(width, 0, width, height);
        g.setColor(originalColor);
    }
}

