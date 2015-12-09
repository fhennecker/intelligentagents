/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.logviewer.gui;

import edu.umich.eecs.tac.logviewer.gui.PositiveBoundedRangeModel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import se.sics.isl.util.FormatUtils;

public class PositiveRangeDiagram
extends JComponent {
    private static final double PADDING = 0.1;
    private static final BasicStroke defaultStroke = new BasicStroke(1.0f);
    private static final BasicStroke wideStroke = new BasicStroke(8.0f);
    private BufferedImage buffImg;
    private Line2D.Double slider;
    private int[][] data;
    private int[] maxData;
    private int[] minData;
    private int totMax;
    private int totMin;
    private int[] step;
    private String title;
    private Border border;
    private String titleUnit;
    private int titleDiag;
    private boolean lockMinMax = false;
    private double scaleFactor;
    private int[] constantY;
    private Color[] constantColor;
    private Color[] lineColor;
    private boolean[] visible;
    private boolean[] emphasized;
    private boolean rescale = false;
    private Insets insets;
    private PositiveBoundedRangeModel dayModel;

    public PositiveRangeDiagram(int diagrams, PositiveBoundedRangeModel dm) {
        this.data = new int[diagrams][];
        this.lineColor = new Color[diagrams];
        this.step = new int[diagrams];
        this.maxData = new int[diagrams];
        this.minData = new int[diagrams];
        this.visible = new boolean[diagrams];
        this.emphasized = new boolean[diagrams];
        int i = 0;
        while (i < diagrams) {
            this.lineColor[i] = Color.black;
            ++i;
        }
        this.dayModel = dm;
        if (this.dayModel != null) {
            this.dayModel.addChangeListener(new ChangeListener(){

                @Override
                public void stateChanged(ChangeEvent ce) {
                    PositiveRangeDiagram.this.repaint();
                }
            });
        }
        this.addComponentListener(new ComponentAdapter(){

            @Override
            public void componentResized(ComponentEvent ce) {
                PositiveRangeDiagram.access$0(PositiveRangeDiagram.this, true);
            }
        });
        this.setOpaque(true);
    }

    public void setTitle(int diag, String title, String unit) {
        this.titleDiag = diag;
        this.title = title;
        this.titleUnit = unit;
        this.repaint();
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
    }

    public void setData(int diag, int[] data, int step) {
        int maxData = Integer.MIN_VALUE;
        int minData = Integer.MAX_VALUE;
        if (data == null) {
            throw new NullPointerException();
        }
        if (data.length == 0 || step <= 0) {
            return;
        }
        int i = 0;
        int n = data.length;
        while (i < n) {
            if (maxData < data[i]) {
                maxData = data[i];
            }
            if (minData > data[i]) {
                minData = data[i];
            }
            ++i;
        }
        if (minData > 0) {
            minData = 0;
        }
        if (maxData < minData) {
            maxData = minData;
        }
        this.data[diag] = data;
        this.step[diag] = step;
        this.maxData[diag] = maxData;
        this.minData[diag] = minData;
        this.visible[diag] = true;
        this.rescale = true;
        this.repaint();
    }

    public void setDotColor(int diag, Color color) {
        if (color == null) {
            throw new NullPointerException();
        }
        this.lineColor[diag] = color;
        this.repaint();
    }

    public void setVisible(int diag, boolean vis) {
        this.visible[diag] = vis;
    }

    public void setEmphasized(int diag, boolean emph) {
        this.emphasized[diag] = emph;
    }

    private void drawImg() {
        Graphics2D g2 = this.buffImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int width = this.buffImg.getWidth(null);
        int height = this.buffImg.getHeight(null);
        if (this.isOpaque()) {
            g2.setPaint(Color.white);
            g2.fill(new Rectangle(0, 0, width, height));
        }
        if (this.constantY != null) {
            int i = 0;
            int n = this.constantY.length;
            while (i < n) {
                double cy = (double)height - (double)height * 0.1 * 0.5 - this.scaleFactor * (double)Math.abs(this.constantY[i] - this.totMin);
                g2.setPaint(this.constantColor[i]);
                g2.draw(new Line2D.Double(0.0, cy, width, cy));
                ++i;
            }
        }
        int j = 0;
        int m = this.data.length;
        while (j < m) {
            if (this.data[j] != null && this.visible[j]) {
                double acctualStepLength = (double)this.step[j] * ((double)width / (double)this.dayModel.getLast());
                g2.setPaint(this.lineColor[j]);
                double lastY = (double)height - (double)height * 0.1 * 0.5 - this.scaleFactor * (double)Math.abs(this.data[j][0] - this.totMin);
                double lastX = 0.0;
                int i = 1;
                int n = this.data[j].length;
                while (i < n) {
                    double y0 = (double)height - (double)height * 0.1 * 0.5 - this.scaleFactor * (double)Math.abs(this.data[j][i] - this.totMin);
                    double x0 = (double)i * acctualStepLength;
                    if (this.emphasized[j]) {
                        g2.setStroke(wideStroke);
                    } else {
                        g2.setStroke(defaultStroke);
                    }
                    g2.draw(new Line2D.Double(lastX, lastY, x0, y0));
                    lastY = y0;
                    lastX = x0;
                    ++i;
                }
            }
            ++j;
        }
    }

    @Override
    public void update(Graphics g) {
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color oldColor = g.getColor();
        int totalWidth = this.getWidth();
        int totalHeight = this.getHeight();
        this.border = this.getBorder();
        if (this.border != null && this.title != null && this.titleUnit != null && this.data[this.titleDiag].length > this.dayModel.getCurrent() && this.border instanceof TitledBorder) {
            ((TitledBorder)this.border).setTitle(" " + this.title + FormatUtils.formatAmount(this.data[this.titleDiag][this.dayModel.getCurrent()]) + this.titleUnit + " ");
        }
        this.insets = this.getInsets(this.insets);
        int x = this.insets.left;
        int y = this.insets.top;
        int width = totalWidth - this.insets.left - this.insets.right;
        int height = totalHeight - this.insets.top - this.insets.bottom;
        if (x > 0) {
            g2.clearRect(0, 0, x, totalHeight);
        }
        if (y > 0) {
            g2.clearRect(0, 0, totalWidth, y);
        }
        if (this.insets.right > 0) {
            g2.clearRect(x + width, 0, totalWidth, totalHeight);
        }
        if (this.insets.bottom > 0) {
            g2.clearRect(0, y + height, totalWidth, totalHeight);
        }
        if (this.buffImg == null) {
            this.buffImg = new BufferedImage(width, height, 1);
            this.drawImg();
        }
        if (this.buffImg.getHeight(null) != height || this.buffImg.getWidth(null) != width) {
            this.buffImg = new BufferedImage(width, height, 1);
            this.drawImg();
        }
        if (this.rescale) {
            this.rescaleData(height);
            this.rescale = false;
            this.drawImg();
        }
        g2.drawImage(this.buffImg, null, x, y);
        double acctualStepLength = (double)(width - 1) / (double)this.dayModel.getLast();
        double sliderPosition = (double)x + (double)this.dayModel.getCurrent() * acctualStepLength;
        if (this.slider == null) {
            this.slider = new Line2D.Double();
        }
        this.slider.y1 = y;
        this.slider.y2 = y + height;
        this.slider.x2 = this.slider.x1 = sliderPosition;
        g2.setPaint(Color.magenta);
        g2.draw(this.slider);
        g2.setPaint(oldColor);
    }

    protected void rescaleData(int height) {
        if (!this.lockMinMax) {
            this.totMax = Integer.MIN_VALUE;
            this.totMin = Integer.MAX_VALUE;
            int i = 0;
            int n = this.data.length;
            while (i < n) {
                if (this.totMax < this.maxData[i]) {
                    this.totMax = this.maxData[i];
                }
                if (this.totMin > this.minData[i]) {
                    this.totMin = this.minData[i];
                }
                ++i;
            }
            if (this.constantY != null) {
                i = 0;
                n = this.constantY.length;
                while (i < n) {
                    int cy = this.constantY[i];
                    if (cy < this.totMin) {
                        this.totMin = cy;
                    }
                    if (cy > this.totMax) {
                        this.totMax = cy;
                    }
                    ++i;
                }
            }
            if (this.totMin == Integer.MAX_VALUE) {
                this.totMin = 0;
                this.totMax = 0;
            }
        }
        this.scaleFactor = this.totMax == this.totMin ? 1.0 : ((double)height - (double)height * 0.1) / (double)Math.abs(this.totMax - this.totMin);
    }

    static /* synthetic */ void access$0(PositiveRangeDiagram positiveRangeDiagram, boolean bl) {
        positiveRangeDiagram.rescale = bl;
    }

}

