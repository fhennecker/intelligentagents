/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class VUMeter
extends JComponent {
    private Color ltGreen = new Color(11599792);
    private Color ltRed = new Color(16756912);
    private Color ltYellow = new Color(15790256);
    private Color ltGray = new Color(10526880);
    private boolean useAntiAliasing = true;
    private double value;
    private int width = 0;
    private int height = 0;

    public VUMeter() {
        this.setOpaque(true);
        this.setDoubleBuffered(true);
    }

    public void setValue(double value) {
        this.value = value;
        this.repaint();
    }

    public double getValue() {
        return this.value;
    }

    public boolean isAntiAliasing() {
        return this.useAntiAliasing;
    }

    public void setAntiAliasing(boolean useAntiAliasing) {
        this.useAntiAliasing = useAntiAliasing;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (this.isOpaque()) {
            g.setColor(this.getBackground());
            g.fillRect(0, 0, this.width, this.height);
        }
        if (this.useAntiAliasing && g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D)g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        this.width = this.getWidth() - 1;
        this.height = this.getHeight() - 1;
        int midx = this.width / 2;
        g.setColor(Color.white);
        g.fillArc(0, 0, this.width, this.height * 2 - 1, 0, 180);
        g.setColor(Color.black);
        g.drawArc(0, 0, this.width, this.height * 2 - 1, 0, 180);
        double factor = 0.800000011920929;
        g.setColor(this.ltGreen);
        this.fillArc(g, factor, 120, 50);
        g.setColor(this.ltYellow);
        this.fillArc(g, factor, 60, 60);
        g.setColor(this.ltRed);
        this.fillArc(g, factor, 10, 50);
        factor = 0.6000000238418579;
        g.setColor(Color.white);
        this.fillArc(g, factor, 0, 180);
        g.setColor(Color.black);
        double angle = 3.141592 - 3.141592 * this.value;
        g.drawLine(midx, this.height - 2, midx + (int)(Math.cos(angle) * (double)midx * 0.8), this.height - (int)(0.8 * (double)this.height * Math.sin(angle)) - 2);
        factor = 0.30000001192092896;
        g.setColor(Color.gray);
        this.fillArc(g, factor, 0, 180);
        g.setColor(Color.black);
        g.drawLine(0, this.height - 1, this.width, this.height - 1);
    }

    private void fillArc(Graphics g, double factor, int start, int length) {
        g.fillArc((int)(0.5 + (double)this.width * (1.0 - factor) / 2.0), (int)((double)this.height * (1.0 - factor)), (int)(0.5 + (double)this.width * factor), (int)((double)(this.height * 2) * factor), start, length);
    }

    public static void main(String[] args) {
        JFrame window = new JFrame("Test");
        VUMeter meter = new VUMeter();
        meter.setPreferredSize(new Dimension(60, 40));
        window.getContentPane().add(meter);
        window.pack();
        window.setVisible(true);
        double v = 0.0;
        double dv = 0.01;
        do {
            meter.setValue(v);
            if ((v += dv) <= 0.0 || v >= 1.0) {
                dv = - dv;
            }
            try {
                Thread.sleep(25);
            }
            catch (Exception var7_5) {
            }
        } while (true);
    }
}

