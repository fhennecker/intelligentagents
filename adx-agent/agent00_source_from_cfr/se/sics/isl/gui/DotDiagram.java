/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class DotDiagram
extends JComponent {
    public static final int NORMAL = 0;
    public static final int ADDITIVE = 1;
    public static final int FILLED_ADDITIVE = 2;
    private int[][] data;
    private int[] dataLen;
    private int[] start;
    private int[] maxData;
    private int[] minData;
    private int totMax;
    private int totMin;
    private boolean lockMinMax = false;
    private int maxDataLen;
    private double factor;
    private int[] constantY;
    private Color[] constantColor;
    private int sizeX;
    private int sizeY;
    private int lowerY;
    private double xspace;
    private int ySpacing = 0;
    private int xSpacing = 0;
    private boolean rescale = false;
    private boolean gridVisible = false;
    private String yLabel = null;
    private String xLabel = null;
    private boolean isAdditive = false;
    private boolean isFilled = false;
    private Insets insets;
    private Color[] lineColor;

    public DotDiagram(int diagrams) {
        this(diagrams, 0);
    }

    public DotDiagram(int diagrams, int mode) {
        this.data = new int[diagrams][];
        this.lineColor = new Color[diagrams];
        this.dataLen = new int[diagrams];
        this.start = new int[diagrams];
        this.maxData = new int[diagrams];
        this.minData = new int[diagrams];
        int i = 0;
        while (i < diagrams) {
            this.lineColor[i] = Color.black;
            ++i;
        }
        this.setOpaque(true);
        this.isFilled = mode == 2;
        this.isAdditive = this.isFilled || mode == 1;
    }

    public void setShowGrid(boolean on) {
        this.gridVisible = on;
    }

    public void setGridYSpacing(int spacing) {
        this.ySpacing = spacing;
    }

    public void setYLabel(String yLabel) {
        this.yLabel = yLabel;
    }

    public void setXLabel(String xLabel) {
        this.xLabel = xLabel;
    }

    public void setName(int index, String name) {
    }

    public void setToolTipVisible(boolean showToolTip) {
    }

    public void addConstant(Color color, int y) {
        int index;
        if (this.constantY == null) {
            index = 0;
            this.constantY = new int[1];
            this.constantColor = new Color[1];
        } else {
            index = this.constantY.length;
            int[] tmpY = new int[index + 1];
            Color[] tmpC = new Color[index + 1];
            int i = 0;
            while (i < index) {
                tmpY[i] = this.constantY[i];
                tmpC[i] = this.constantColor[i];
                ++i;
            }
            this.constantY = tmpY;
            this.constantColor = tmpC;
        }
        this.constantY[index] = y;
        this.constantColor[index] = color == null ? Color.black : color;
        this.rescale = true;
        this.repaint();
    }

    public void setMinMax(int min, int max) {
        this.totMax = max;
        this.totMin = min;
        this.lockMinMax = true;
        this.rescale = true;
    }

    public void setData(int diag, int[] data, int start, int len) {
        int maxData = Integer.MIN_VALUE;
        int minData = Integer.MAX_VALUE;
        if (len > 0) {
            int totLen = data.length;
            int i = start;
            int n = start + len;
            while (i < n) {
                int val2 = data[i % totLen];
                if (maxData < val2) {
                    maxData = val2;
                }
                if (minData > val2) {
                    minData = val2;
                }
                ++i;
            }
        }
        if (minData > 0) {
            minData = 0;
        }
        if (maxData < minData) {
            maxData = minData;
        }
        this.dataLen[diag] = len;
        this.start[diag] = start;
        this.data[diag] = data;
        this.maxData[diag] = maxData;
        this.minData[diag] = minData;
        this.rescale = true;
        this.repaint();
    }

    public void setDotColor(int diag, Color color) {
        if (color == null) {
            throw new NullPointerException();
        }
        this.lineColor[diag] = color;
        if (this.dataLen[diag] > 0) {
            this.repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g0) {
        int i;
        int n;
        Graphics2D g = (Graphics2D)g0;
        Color oldColor = g.getColor();
        int width = this.getWidth();
        int height = this.getHeight();
        int yLabelSize = 0;
        if (this.yLabel != null || this.xLabel != null) {
            FontMetrics fm = g.getFontMetrics();
            yLabelSize = fm.stringWidth(this.yLabel);
        }
        if (this.isOpaque()) {
            g.setColor(Color.white);
            g.fillRect(0, 0, width, height);
        }
        this.insets = this.getInsets(this.insets);
        int reserveX = this.yLabel != null ? 12 * width / 200 : 0;
        int x = this.insets.left + reserveX;
        int y = this.insets.top;
        width = width - this.insets.left - this.insets.right - reserveX;
        height = height - this.insets.top - this.insets.bottom;
        if (this.rescale || width != this.sizeX || height != this.sizeY) {
            if (this.rescale) {
                int n2;
                int i2;
                this.rescale = false;
                this.maxDataLen = 0;
                if (this.lockMinMax) {
                    i2 = 0;
                    n2 = this.data.length;
                    while (i2 < n2) {
                        if (this.dataLen[i2] > this.maxDataLen) {
                            this.maxDataLen = this.dataLen[i2];
                        }
                        ++i2;
                    }
                } else {
                    this.totMin = Integer.MAX_VALUE;
                    this.totMax = Integer.MIN_VALUE;
                    if (this.constantY != null) {
                        i2 = 0;
                        n2 = this.constantY.length;
                        while (i2 < n2) {
                            int cy = this.constantY[i2];
                            if (cy < this.totMin) {
                                this.totMin = cy;
                            }
                            if (cy > this.totMax) {
                                this.totMax = cy;
                            }
                            ++i2;
                        }
                    }
                    int sum = 0;
                    i = 0;
                    n = this.data.length;
                    while (i < n) {
                        if (this.dataLen[i] > 0) {
                            int min = this.minData[i];
                            int max = this.maxData[i];
                            if (min < this.totMin) {
                                this.totMin = min;
                            }
                            if (max > this.totMax) {
                                this.totMax = max;
                            }
                            if (this.dataLen[i] > this.maxDataLen) {
                                this.maxDataLen = this.dataLen[i];
                            }
                            sum += max;
                        }
                        ++i;
                    }
                    if (this.isAdditive && sum > this.totMax) {
                        this.totMax = sum;
                    }
                    if (this.totMin > 0) {
                        this.totMin = 0;
                    }
                    if (this.totMax < this.totMin) {
                        this.totMax = this.totMin;
                    }
                }
            }
            this.sizeY = height;
            this.sizeX = width - 2;
            this.factor = this.totMax < 0 ? (double)(this.sizeY - 15) / (double)(0 - this.totMin) : (this.totMax == this.totMin ? 1.0 : (double)(this.sizeY - 15) / (double)(this.totMax - this.totMin));
            this.lowerY = this.sizeY - 5;
            this.xspace = this.maxDataLen == 0 ? 1.0 : (double)this.sizeX / (double)this.maxDataLen;
        }
        x += 2;
        int zero = y + this.lowerY - (int)(this.factor * (double)(0 - this.totMin));
        if (this.gridVisible) {
            g.setColor(Color.lightGray);
            i = 0;
            n = 10;
            while (i < n) {
                g.drawLine(x + i * this.sizeX / 10, y, x + i * this.sizeX / 10, y + this.sizeY - 1);
                ++i;
            }
            int z0 = zero - y;
            int z1 = zero - (y + this.sizeY);
            double tot = (double)this.ySpacing * this.factor == 0.0 ? (double)this.sizeY / 10.0 : this.factor * (double)this.ySpacing;
            double i3 = 0.0;
            while (i3 < (double)z0) {
                g.drawLine(x + 1, (int)((double)zero - i3), x + this.sizeX, (int)((double)zero - i3));
                i3 += tot;
            }
            i3 = 0.0;
            while (i3 > (double)z1) {
                g.drawLine(x + 1, (int)((double)zero - i3), x + this.sizeX, (int)((double)zero - i3));
                i3 -= tot;
            }
        }
        if (this.isAdditive) {
            if (this.isFilled) {
                int delta = (int)(this.xspace + 1.0);
                int i4 = 0;
                int n3 = this.maxDataLen;
                while (i4 < n3) {
                    int lastY = zero;
                    int j = 0;
                    int m = this.data.length;
                    while (j < m) {
                        int y0;
                        int[] drawData;
                        int pos;
                        if (this.dataLen[j] > i4 && (y0 = (int)(this.factor * (double)(drawData = this.data[j])[(pos = this.start[j] + i4) % drawData.length])) > 0) {
                            g.setColor(this.lineColor[j]);
                            g.fillRect(x + (int)((double)i4 * this.xspace), lastY - y0, delta, y0);
                            lastY -= y0;
                        }
                        ++j;
                    }
                    ++i4;
                }
            } else {
                i = 0;
                n = this.maxDataLen;
                while (i < n) {
                    int lastY = zero;
                    int j = 0;
                    int m = this.data.length;
                    while (j < m) {
                        int pos;
                        int y0;
                        int[] drawData;
                        if (this.dataLen[j] > i && (y0 = (int)(this.factor * (double)(drawData = this.data[j])[(pos = this.start[j] + i) % drawData.length])) > 0) {
                            g.setColor(this.lineColor[j]);
                            g.drawLine(x + (int)((double)(i - 1) * this.xspace), lastY - y0, x + (int)((double)i * this.xspace), lastY - y0);
                            lastY -= y0;
                        }
                        ++j;
                    }
                    ++i;
                }
            }
        } else {
            int j = 0;
            int m = this.data.length;
            while (j < m) {
                if (this.dataLen[j] > 0) {
                    int[] drawData = this.data[j];
                    int maxLen = drawData.length;
                    int startData = this.start[j];
                    int lastY = (int)(this.factor * (double)(drawData[startData % maxLen] - this.totMin));
                    g.setColor(this.lineColor[j]);
                    int i5 = 1;
                    int n4 = this.dataLen[j];
                    while (i5 < n4) {
                        int pos = startData + i5;
                        int y0 = (int)(this.factor * (double)(drawData[pos % maxLen] - this.totMin));
                        g.drawLine(x + (int)((double)(i5 - 1) * this.xspace), y + this.lowerY - lastY, x + (int)((double)i5 * this.xspace), y + this.lowerY - y0);
                        lastY = y0;
                        ++i5;
                    }
                }
                ++j;
            }
        }
        if (this.constantY != null) {
            i = 0;
            n = this.constantY.length;
            while (i < n) {
                int cy = y + this.lowerY - (int)(this.factor * (double)(this.constantY[i] - this.totMin));
                g.setColor(this.constantColor[i]);
                g.drawLine(x, cy, x + this.sizeX, cy);
                ++i;
            }
        }
        g.setColor(Color.black);
        g.drawLine(x, zero, (x -= 2) + this.sizeX, zero);
        g.drawLine(x + 1, y, x + 1, y + this.sizeY - 1);
        if (this.yLabel != null) {
            g.rotate(-1.57075);
            g.scale((double)height / 200.0, (double)width / 200.0);
            g.drawString(this.yLabel, -100 - yLabelSize / 2, 10);
        }
    }

    public static void main(String[] args) throws Exception {
        JFrame jf = new JFrame("DotDiagram - Test");
        int[] data = new int[100];
        int[] data2 = new int[100];
        int[] data3 = new int[100];
        boolean neg = true;
        int pos = 100;
        int pos2 = 50;
        int pos3 = 0;
        int s = neg ? -10 : 0;
        int i = 0;
        while (i < 60) {
            data[i] = pos += (int)((double)s + 20.0 * Math.random());
            data2[i] = pos2 += (int)((double)s + 20.0 * Math.random());
            data3[i] = pos3 += (int)((double)s + 20.0 * Math.random());
            ++i;
        }
        DotDiagram bd = DotDiagram.setupDiagram(10, data, data2, data3, 0);
        DotDiagram bd2 = DotDiagram.setupDiagram(10, data, data2, data3, 1);
        DotDiagram bd3 = DotDiagram.setupDiagram(10, data, data2, data3, 2);
        jf.setDefaultCloseOperation(3);
        jf.setSize(600, 200);
        jf.getContentPane().setLayout(new GridLayout(0, 3));
        jf.getContentPane().add(bd);
        jf.getContentPane().add(bd2);
        jf.getContentPane().add(bd3);
        jf.setVisible(true);
        int i2 = 0;
        while (i2 < 20000) {
            Thread.sleep(100);
            pos2 += (int)(5.0 - 10.0 * Math.random());
            pos3 += (int)(5.0 - 10.0 * Math.random());
            if ((pos += (int)(5.0 - 10.0 * Math.random())) < 0) {
                pos = 0;
            }
            if (pos3 < 0) {
                pos3 = 0;
            }
            data[(60 + i2) % data.length] = pos;
            data2[(60 + i2) % data2.length] = pos2;
            data3[(60 + i2) % data3.length] = pos3;
            bd.setData(0, data, i2 % data.length, 60);
            bd.setData(1, data2, i2 % data2.length, 60);
            bd.setData(2, data3, i2 % data3.length, 60);
            bd2.setData(0, data, i2 % data.length, 60);
            bd2.setData(1, data2, i2 % data2.length, 60);
            bd2.setData(2, data3, i2 % data3.length, 60);
            bd3.setData(0, data, i2 % data.length, 60);
            bd3.setData(1, data2, i2 % data2.length, 60);
            bd3.setData(2, data3, i2 % data3.length, 60);
            ++i2;
        }
    }

    private static DotDiagram setupDiagram(int v, int[] d1, int[] d2, int[] d3, int mode) {
        DotDiagram bd = new DotDiagram(v, mode);
        bd.setDotColor(0, Color.red);
        bd.setDotColor(1, Color.green);
        bd.setDotColor(2, Color.blue);
        bd.addConstant(Color.yellow, 100);
        bd.setShowGrid(true);
        bd.setYLabel("The Y-Axis (" + (mode == 0 ? "normal" : "additive") + ')');
        bd.setData(0, d1, 0, 60);
        bd.setData(1, d2, 0, 60);
        bd.setData(2, d3, 0, 60);
        return bd;
    }
}

