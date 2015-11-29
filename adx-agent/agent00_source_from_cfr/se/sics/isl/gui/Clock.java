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
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Clock
extends JPanel {
    private static final double PI = 3.141592;
    private long time = 0;
    private boolean showSeconds = true;
    private boolean useAntiAliasing = true;
    private int[] xpoints = new int[3];
    private int[] ypoints = new int[3];
    private Insets insets;

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        if (this.time != time) {
            this.time = time;
            this.repaint();
        }
    }

    public boolean isShowingSeconds() {
        return this.showSeconds;
    }

    public void setShowingSeconds(boolean secs) {
        this.showSeconds = secs;
    }

    public boolean isAntiAliasing() {
        return this.useAntiAliasing;
    }

    public void setAntiAliasing(boolean useAntiAliasing) {
        this.useAntiAliasing = useAntiAliasing;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Color originalColor = g.getColor();
        this.insets = this.getInsets(this.insets);
        int width = this.getWidth();
        int height = this.getHeight();
        if (this.isOpaque()) {
            g.setColor(this.getBackground());
            g.fillRect(0, 0, width, height);
        }
        width = width - this.insets.left - this.insets.right - 1;
        height = height - this.insets.top - this.insets.bottom - 1;
        if (width <= 0 || height <= 0) {
            g.setColor(originalColor);
            return;
        }
        if (this.useAntiAliasing && g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D)g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        int x = this.insets.left;
        int y = this.insets.top;
        int midx = width / 2;
        int midy = height / 2;
        g.setColor(Color.white);
        g.fillOval(x, y, width, height);
        g.setColor(Color.black);
        g.drawOval(x, y, width - 1, height - 1);
        double p = 0.5235986666666667;
        int i = 0;
        while (i < 12) {
            double s = i % 3 == 0 ? 0.9 : 0.95;
            double ip = (double)i * p;
            int dx = (int)((double)midx * Math.cos(ip));
            int dy = (int)((double)midy * Math.sin(ip));
            int xs = (int)((double)dx * s);
            int ys = (int)((double)dy * s);
            g.drawLine(x + midx + xs, y + midy + ys, x + midx + dx, y + midy + dy);
            ++i;
        }
        double sec = (double)this.time / 1000.0;
        double min = sec / 60.0;
        double hr = min / 60.0 % 12.0;
        if (this.showSeconds) {
            double angle = 6.283184 * (min % 60.0) - 1.570796;
            g.drawLine(x + midx, y + midy, x + midx + (int)((double)midx * Math.cos(angle)), y + midy + (int)((double)midy * Math.sin(angle)));
        }
        double angle = 6.283184 * (min / 60.0 % 60.0) - 1.570796;
        int dx = (int)((double)midx / 1.1 * Math.cos(angle));
        int dy = (int)((double)midy / 1.1 * Math.sin(angle));
        this.xpoints[0] = x + midx + dy / 30;
        this.ypoints[0] = y + midy - dx / 30;
        this.xpoints[1] = x + midx + dx;
        this.ypoints[1] = y + midy + dy;
        this.xpoints[2] = x + midx - dy / 30;
        this.ypoints[2] = y + midy + dx / 30;
        g.setColor(Color.gray);
        g.fillPolygon(this.xpoints, this.ypoints, 3);
        g.setColor(Color.black);
        g.drawPolygon(this.xpoints, this.ypoints, 3);
        angle = 6.283184 * (min / 720.0 % 12.0) - 1.570796;
        dx = (int)((double)midx / 1.5 * Math.cos(angle));
        dy = (int)((double)midy / 1.5 * Math.sin(angle));
        this.xpoints[0] = x + midx + dy / 20;
        this.ypoints[0] = y + midy - dx / 20;
        this.xpoints[1] = x + midx + dx;
        this.ypoints[1] = y + midy + dy;
        this.xpoints[2] = x + midx - dy / 20;
        this.ypoints[2] = y + midy + dx / 20;
        g.setColor(Color.gray);
        g.fillPolygon(this.xpoints, this.ypoints, 3);
        g.setColor(Color.black);
        g.drawPolygon(this.xpoints, this.ypoints, 3);
        g.setColor(originalColor);
    }

    public static void main(String[] args) {
        JFrame window = new JFrame("GMT Clock");
        JPanel panel = new JPanel();
        Clock clk = new Clock();
        Clock clk2 = new Clock();
        clk.setPreferredSize(new Dimension(50, 50));
        clk2.setPreferredSize(new Dimension(100, 100));
        panel.add(clk);
        window.setDefaultCloseOperation(3);
        window.getContentPane().add((Component)panel, "North");
        window.getContentPane().add((Component)clk2, "Center");
        window.pack();
        window.setVisible(true);
        clk.setShowingSeconds(false);
        clk2.setShowingSeconds(false);
        do {
            long time = System.currentTimeMillis() * 6171;
            clk.setTime(time);
            clk2.setTime(time);
            try {
                Thread.sleep(20);
            }
            catch (Exception var7_6) {
            }
        } while (true);
    }
}

