/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class BarDiagram
extends JComponent {
    private static final double PI2 = 1.570796;
    private int[] data;
    private int maxData;
    private int minData;
    private double factor;
    private int xspace;
    private int xfill;
    private boolean rescale = false;
    private int sizeX;
    private int sizeY;
    private int lowerY;
    private Color barColor;
    private Color leftColor;
    private Color rightColor;
    private Color[] allColors;
    private boolean isShowingValue = false;
    private Color inValueColor = Color.white;
    private Color outValueColor = Color.black;
    private Insets insets;
    private String[] names;

    public BarDiagram() {
        this.setOpaque(true);
        this.setBackground(Color.white);
    }

    public void setToolTipVisible(boolean showToolTip) {
        this.setToolTipText(showToolTip ? "" : null);
    }

    public void setNames(String[] names) {
        this.names = names;
    }

    public void setData(int[] data) {
        this.maxData = Integer.MIN_VALUE;
        this.minData = Integer.MAX_VALUE;
        if (data != null) {
            int i = 0;
            int n = data.length;
            while (i < n) {
                if (this.maxData < data[i]) {
                    this.maxData = data[i];
                }
                if (this.minData > data[i]) {
                    this.minData = data[i];
                }
                ++i;
            }
        }
        if (this.minData > 0) {
            this.minData = 0;
        }
        if (this.maxData < this.minData) {
            this.maxData = this.minData;
        }
        this.data = data;
        this.rescale = true;
        this.setupColors();
        this.repaint();
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        int dataLen;
        if (this.xspace == 0) {
            return null;
        }
        String[] names = this.names;
        int[] data = this.data;
        int index = event.getX() / this.xspace;
        int nameLen = names == null ? 0 : names.length;
        int n = dataLen = data == null ? 0 : data.length;
        if (dataLen >= nameLen) {
            if (index >= dataLen) {
                index = dataLen - 1;
            }
        } else if (index >= nameLen) {
            index = nameLen - 1;
        }
        if (index < 0) {
            return null;
        }
        String name = index >= nameLen ? "Value =" : names[index];
        int value = index >= dataLen ? 0 : data[index];
        return String.valueOf(name) + ' ' + value;
    }

    public void setBarColor(Color color) {
        this.barColor = color;
        this.leftColor = null;
        this.rightColor = null;
        this.allColors = null;
    }

    public void setBarColors(Color leftColor, Color rightColor) {
        this.leftColor = leftColor;
        this.rightColor = rightColor;
        this.allColors = null;
        this.setupColors();
    }

    private void setupColors() {
        if (this.leftColor != null && this.data != null) {
            if (this.allColors != null && this.allColors.length == this.data.length) {
                return;
            }
            float len = this.data.length;
            float r0 = (float)this.leftColor.getRed() / len;
            float r1 = (float)this.rightColor.getRed() / len;
            float g0 = (float)this.leftColor.getGreen() / len;
            float g1 = (float)this.rightColor.getGreen() / len;
            float b0 = (float)this.leftColor.getBlue() / len;
            float b1 = (float)this.rightColor.getBlue() / len;
            this.allColors = new Color[this.data.length];
            int i = 0;
            int n = this.data.length;
            while (i < n) {
                this.allColors[i] = new Color((int)(r0 * (float)(n - i) + r1 * (float)i), (int)(g0 * (float)(n - i) + g1 * (float)i), (int)(b0 * (float)(n - i) + b1 * (float)i));
                ++i;
            }
        }
    }

    public boolean isShowingValue() {
        return this.isShowingValue;
    }

    public void setShowingValue(boolean isShowingValue) {
        if (this.isShowingValue != isShowingValue) {
            this.isShowingValue = isShowingValue;
            this.repaint();
        }
    }

    public void setValueColor(Color color) {
        this.outValueColor = this.inValueColor = color;
    }

    public void setValueColors(Color inBarColor, Color outBarColor) {
        this.inValueColor = inBarColor;
        this.outValueColor = outBarColor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        int hei;
        Color oldColor = g.getColor();
        int width = this.getWidth();
        int height = this.getHeight();
        if (this.isOpaque()) {
            g.setColor(this.getBackground());
            g.fillRect(0, 0, width, height);
        }
        this.insets = this.getInsets(this.insets);
        int x = this.insets.left;
        int y = this.insets.top;
        width = width - this.insets.left - this.insets.right;
        height = height - this.insets.top - this.insets.bottom;
        if (width != this.sizeX || height != this.sizeY || this.rescale) {
            this.rescale = false;
            this.sizeY = height;
            this.sizeX = width;
            this.lowerY = this.sizeY - 3;
            this.factor = this.minData == this.maxData ? 1.0 : (double)(this.sizeY - 15) / (double)(this.maxData - this.minData);
            this.xspace = this.data == null ? 2 : this.sizeX / this.data.length;
            this.xfill = this.xspace - 2;
        }
        g.setColor(Color.black);
        g.drawLine(x, y + this.lowerY, x + this.sizeX, y + this.lowerY);
        if (this.data == null) {
            g.setColor(oldColor);
            return;
        }
        int[] drawData = this.data;
        if (this.allColors == null) {
            if (this.barColor != null) {
                g.setColor(this.barColor);
            } else {
                g.setColor(this.getForeground());
            }
        }
        int i = 0;
        int n = drawData.length;
        while (i < n) {
            hei = (int)(this.factor * (double)drawData[i]);
            if (this.allColors != null) {
                g.setColor(this.allColors[i]);
            }
            g.fillRect(x + i * this.xspace, y + this.lowerY - hei, this.xfill, hei);
            ++i;
        }
        g.setColor(Color.black);
        i = 0;
        n = drawData.length;
        while (i < n) {
            hei = (int)(this.factor * (double)drawData[i]);
            g.drawRect(x + i * this.xspace, y + this.lowerY - hei, this.xfill, hei);
            ++i;
        }
        if (this.isShowingValue && g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D)g;
            g2d.rotate(-1.570796);
            int i2 = 0;
            int n2 = drawData.length;
            while (i2 < n2) {
                if (drawData[i2] > 0) {
                    int py = (int)(this.factor * (double)drawData[i2]) - y - this.lowerY;
                    if (py > -50) {
                        py = -80;
                        g.setColor(this.inValueColor);
                    } else {
                        g.setColor(this.outValueColor);
                    }
                    g2d.drawString(Integer.toString(drawData[i2]), py, x + i2 * this.xspace + 4 + this.xspace / 2);
                }
                ++i2;
            }
            g2d.rotate(1.570796);
        }
        g.setColor(oldColor);
    }

    public static void main(String[] args) throws Exception {
        JFrame jf = new JFrame("test");
        jf.setDefaultCloseOperation(3);
        BarDiagram bd = new BarDiagram();
        bd.setBarColors(Color.red, Color.green);
        bd.setShowingValue(true);
        bd.setData(new int[]{12, 42, 12, 21, 55, 3, 15, 12, 42, 12, 21, 55, 3, 15});
        jf.setSize(800, 200);
        jf.getContentPane().setLayout(new BorderLayout());
        jf.getContentPane().add((Component)bd, "Center");
        jf.setVisible(true);
        int i = 0;
        while (i < 10) {
            bd.setData(new int[]{12, 42, 12, 21, 55, 3, 15, 12, 42, 12, 21, 55, 13, 15});
            Thread.sleep(500);
            bd.setData(new int[]{12, 24, 21, 12, 12, 21, 55, 3, 15, 32, 55, 23, 33, 15});
            Thread.sleep(500);
            ++i;
        }
    }
}

