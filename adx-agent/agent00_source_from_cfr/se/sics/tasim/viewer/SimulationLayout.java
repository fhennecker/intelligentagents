/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.viewer;

import com.botbox.util.ArrayUtils;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;

public class SimulationLayout
implements LayoutManager2 {
    public static final int X_AXIS = 0;
    public static final int Y_AXIS = 1;
    private Component[][] containers = null;
    private Container target;
    private boolean isVerticalLayout;
    private int hgap;
    private int vgap;

    public SimulationLayout(Container target, int axis) {
        this(target, axis, 5, 20);
    }

    public SimulationLayout(Container target, int axis, int hgap, int vgap) {
        if (axis == 1) {
            this.isVerticalLayout = true;
        } else if (axis != 0) {
            throw new IllegalArgumentException("axis must be X_AXIS or Y_AXIS");
        }
        this.target = target;
        this.hgap = hgap;
        this.vgap = vgap;
    }

    protected int getContainerCount() {
        return this.containers == null ? 0 : this.containers.length;
    }

    public int getAxis() {
        return this.isVerticalLayout ? 1 : 0;
    }

    public int getVgap() {
        return this.vgap;
    }

    public void setVgap(int vgap) {
        this.vgap = vgap;
    }

    public int getHgap() {
        return this.hgap;
    }

    public void setHgap(int hgap) {
        this.hgap = hgap;
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        if (constraints instanceof String) {
            this.addLayoutComponent((String)constraints, comp);
        } else if (constraints instanceof Integer) {
            this.addLayoutComponent(comp, (Integer)constraints);
        } else {
            if (constraints != null) {
                throw new IllegalArgumentException("cannot add to layout: constraints must be an Integer");
            }
            this.addLayoutComponent(comp, 0);
        }
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
        try {
            this.addLayoutComponent(comp, Integer.parseInt(name));
        }
        catch (Exception e) {
            throw new IllegalArgumentException("cannot add to layout: constraints must be an Integer");
        }
    }

    private void addLayoutComponent(Component comp, int index) {
        Object object = comp.getTreeLock();
        synchronized (object) {
            if (this.containers == null) {
                this.containers = new Component[index + 1][];
            } else if (index >= this.containers.length) {
                this.containers = (Component[][])ArrayUtils.setSize((Object[])this.containers, index + 1);
            }
            this.containers[index] = (Component[])ArrayUtils.add(Component.class, this.containers[index], comp);
        }
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        Object object = comp.getTreeLock();
        synchronized (object) {
            int i = 0;
            int n = this.getContainerCount();
            while (i < n) {
                Object[] cont = this.containers[i];
                int index = ArrayUtils.indexOf(cont, comp);
                if (index >= 0) {
                    if ((cont = (Component[])ArrayUtils.remove(cont, index)) == null && i == n - 1) {
                        this.containers = (Component[][])ArrayUtils.setSize((Object[])this.containers, i);
                        break;
                    }
                    this.containers[i] = cont;
                    break;
                }
                ++i;
            }
        }
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        if (parent != this.target) {
            throw new IllegalArgumentException("this layout can not be shared");
        }
        Object object = parent.getTreeLock();
        synchronized (object) {
            int totalWidth = 0;
            int totalHeight = 0;
            int contNumber = this.getContainerCount();
            if (contNumber > 0) {
                int i = 0;
                while (i < contNumber) {
                    Component[] r = this.containers[i];
                    if (r != null) {
                        int maxWidth = 0;
                        int maxHeight = 0;
                        int compNumber = r.length;
                        int j = 0;
                        while (j < compNumber) {
                            Dimension cd = r[j].getPreferredSize();
                            if (cd.width > maxWidth) {
                                maxWidth = cd.width;
                            }
                            if (cd.height > maxHeight) {
                                maxHeight = cd.height;
                            }
                            ++j;
                        }
                        if (this.isVerticalLayout) {
                            maxHeight = maxHeight * compNumber + this.vgap * (compNumber - 1);
                        } else {
                            maxWidth = maxWidth * compNumber + this.hgap * (compNumber - 1);
                        }
                        if (maxWidth > totalWidth) {
                            totalWidth = maxWidth;
                        }
                        if (maxHeight > totalHeight) {
                            totalHeight = maxHeight;
                        }
                    }
                    ++i;
                }
                if (this.isVerticalLayout) {
                    totalWidth = totalWidth * contNumber + this.hgap * (contNumber - 1);
                } else {
                    totalHeight = totalHeight * contNumber + this.vgap * (contNumber - 1);
                }
            }
            Insets insets = parent.getInsets();
            return new Dimension(totalWidth + insets.left + insets.right, totalHeight + insets.top + insets.bottom);
        }
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        if (parent != this.target) {
            throw new IllegalArgumentException("this layout can not be shared");
        }
        Object object = parent.getTreeLock();
        synchronized (object) {
            int totalWidth = 0;
            int totalHeight = 0;
            int contNumber = this.getContainerCount();
            if (contNumber > 0) {
                int i = 0;
                while (i < contNumber) {
                    Component[] r = this.containers[i];
                    if (r != null) {
                        int maxWidth = 0;
                        int maxHeight = 0;
                        int compNumber = r.length;
                        int j = 0;
                        while (j < compNumber) {
                            Dimension cd = r[j].getMinimumSize();
                            if (cd.width > maxWidth) {
                                maxWidth = cd.width;
                            }
                            if (cd.height > maxHeight) {
                                maxHeight = cd.height;
                            }
                            ++j;
                        }
                        if (this.isVerticalLayout) {
                            maxHeight = maxHeight * compNumber + this.vgap * (compNumber - 1);
                        } else {
                            maxWidth = maxWidth * compNumber + this.hgap * (compNumber - 1);
                        }
                        if (maxWidth > totalWidth) {
                            totalWidth = maxWidth;
                        }
                        if (maxHeight > totalHeight) {
                            totalHeight = maxHeight;
                        }
                    }
                    ++i;
                }
                if (this.isVerticalLayout) {
                    totalWidth = totalWidth * contNumber + this.hgap * (contNumber - 1);
                } else {
                    totalHeight = totalHeight * contNumber + this.vgap * (contNumber - 1);
                }
            }
            Insets insets = parent.getInsets();
            return new Dimension(totalWidth + insets.left + insets.right, totalHeight + insets.top + insets.bottom);
        }
    }

    @Override
    public Dimension maximumLayoutSize(Container parent) {
        if (parent != this.target) {
            throw new IllegalArgumentException("this layout can not be shared");
        }
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public float getLayoutAlignmentX(Container parent) {
        return 0.5f;
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        return 0.5f;
    }

    @Override
    public void layoutContainer(Container parent) {
        if (parent != this.target) {
            throw new IllegalArgumentException("this layout can not be shared");
        }
        Object object = parent.getTreeLock();
        synchronized (object) {
            int contNumber = this.getContainerCount();
            if (contNumber > 0) {
                Insets insets = parent.getInsets();
                int x = insets.left;
                int y = insets.top;
                int height = parent.getHeight() - y - insets.bottom;
                int width = parent.getWidth() - x - insets.right;
                if (this.isVerticalLayout) {
                    int compWidth = (width - this.hgap * (contNumber - 1)) / contNumber;
                    int i = 0;
                    int n = contNumber;
                    while (i < n) {
                        Component[] r = this.containers[i];
                        if (r != null) {
                            int compNumber = r.length;
                            int tempY = y;
                            int compHeight = (height - this.vgap * (compNumber - 1)) / compNumber;
                            int j = 0;
                            while (j < compNumber) {
                                Component c = r[j];
                                Dimension d = c.getPreferredSize();
                                if (d.width > compWidth) {
                                    d.width = compWidth;
                                }
                                if (d.height > compHeight) {
                                    d.height = compHeight;
                                }
                                c.setBounds(x + (compWidth - d.width >> 1), tempY + (compHeight - d.height >> 1), d.width, d.height);
                                tempY += compHeight + this.vgap;
                                ++j;
                            }
                        }
                        x += compWidth + this.hgap;
                        ++i;
                    }
                } else {
                    int compHeight = (height - this.vgap * (contNumber - 1)) / contNumber;
                    int i = 0;
                    int n = contNumber;
                    while (i < n) {
                        Component[] r = this.containers[i];
                        if (r != null) {
                            int compNumber = r.length;
                            int tempX = x;
                            int compWidth = (width - this.hgap * (compNumber - 1)) / compNumber;
                            int j = 0;
                            while (j < compNumber) {
                                Component c = r[j];
                                Dimension d = c.getPreferredSize();
                                if (d.width > compWidth) {
                                    d.width = compWidth;
                                }
                                if (d.height > compHeight) {
                                    d.height = compHeight;
                                }
                                c.setBounds(tempX + (compWidth - d.width >> 1), y + (compHeight - d.height >> 1), d.width, d.height);
                                tempX += compHeight + this.hgap;
                                ++j;
                            }
                        }
                        y += compHeight + this.vgap;
                        ++i;
                    }
                }
            }
        }
    }

    @Override
    public void invalidateLayout(Container target) {
    }
}

