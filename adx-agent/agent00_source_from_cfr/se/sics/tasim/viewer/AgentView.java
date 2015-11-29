/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.viewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import se.sics.isl.transport.Transportable;
import se.sics.isl.util.ConfigManager;
import se.sics.tasim.viewer.SimulationPanel;

public abstract class AgentView
extends JComponent {
    private static final Logger log = Logger.getLogger(AgentView.class.getName());
    public static final int X_AXIS = 1;
    public static final int Y_AXIS = 2;
    public static final int BOTH_AXIS = 3;
    private SimulationPanel parent;
    private int index;
    private String name;
    private int role;
    private String roleName;
    private Icon agentIcon;
    private int minWidth = 0;
    private int minHeight = 0;
    private int axis = 3;
    private int connectionDistance = 5;

    public AgentView() {
        this.setOpaque(true);
    }

    final void init(SimulationPanel parent, int index, String name, int role, String roleName) {
        Icon icon;
        if (this.name != null) {
            throw new IllegalStateException("already initialized");
        }
        this.parent = parent;
        this.index = index;
        this.name = name;
        this.role = role;
        this.roleName = roleName;
        String iconName = this.getConfigProperty("image");
        if (iconName != null && (icon = this.getIcon(iconName)) != null) {
            this.setIcon(icon);
        }
        this.initialized();
        if (this.getLayout() == null && this.minWidth > 0 && this.minHeight > 0) {
            Dimension size = new Dimension(this.minWidth, this.minHeight);
            this.setPreferredSize(size);
            this.setMinimumSize(size);
        }
        this.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                JComponent dialog;
                Object source = mouseEvent.getSource();
                if (source == AgentView.this && !AgentView.this.handleMenu(mouseEvent) && SwingUtilities.isLeftMouseButton(mouseEvent) && (dialog = AgentView.this.getDialog()) != null) {
                    AgentView.this.showDialog(dialog);
                }
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if (mouseEvent.getSource() == AgentView.this) {
                    AgentView.this.handleMenu(mouseEvent);
                }
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                if (mouseEvent.getSource() == AgentView.this) {
                    AgentView.this.handleMenu(mouseEvent);
                }
            }
        });
    }

    protected boolean handleMenu(MouseEvent event) {
        return false;
    }

    protected abstract void initialized();

    public int getIndex() {
        return this.index;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public int getRole() {
        return this.role;
    }

    public String getRoleName() {
        return this.roleName;
    }

    public Icon getIcon() {
        return this.agentIcon;
    }

    public void setIcon(Icon agentIcon) {
        this.agentIcon = agentIcon;
        int width = agentIcon.getIconWidth();
        int height = agentIcon.getIconHeight();
        if (width > this.minWidth) {
            this.minWidth = width;
        }
        if (height > this.minHeight) {
            this.minHeight = height;
        }
    }

    public int getConnectionAxis() {
        return this.axis;
    }

    public void setConnectionAxis(int axis) {
        this.axis = axis;
    }

    public int getConnectionDistance() {
        return this.connectionDistance;
    }

    public void setConnectionDistance(int connectionDistance) {
        this.connectionDistance = connectionDistance;
    }

    public Point getConnectionPoint(int type, int x, int y, boolean isTarget) {
        return this.getConnectionPoint(type, x, y, isTarget, null);
    }

    public Point getConnectionPoint(int type, int toX, int toY, boolean isTarget, Point cache) {
        if (cache == null) {
            cache = new Point();
        }
        int x = this.getX();
        int y = this.getY();
        int width = this.getWidth();
        int height = this.getHeight();
        cache.x = this.axis == 2 ? x + width / 2 : (toX < x ? x - this.connectionDistance : (toX > x + width ? x + width + this.connectionDistance : x + width / 2));
        cache.y = this.axis == 1 ? y + height / 2 : (toY < y ? y - this.connectionDistance : (toY > y + height ? y + height + this.connectionDistance : y + height / 2));
        return cache;
    }

    protected ConfigManager getConfig() {
        return this.parent.getConfig();
    }

    protected Icon getIcon(String iconName) {
        return this.parent.getIcon(iconName);
    }

    protected String getConfigProperty(String prop) {
        return this.getConfigProperty(prop, null);
    }

    protected String getConfigProperty(String prop, String defaultValue) {
        ConfigManager config = this.parent.getConfig();
        String value = config.getProperty(String.valueOf(this.roleName) + '.' + this.getName() + '.' + prop);
        if (value == null) {
            value = config.getProperty(String.valueOf(this.roleName) + '.' + prop);
        }
        return value == null ? defaultValue : value;
    }

    protected Point getConfigPoint(String name) {
        return this.getConfigPoint(name, null);
    }

    protected Point getConfigPoint(String name, Point point) {
        int index;
        String value = this.getConfigProperty(String.valueOf(name) + ".location");
        if (value != null && (index = value.indexOf(44)) > 0) {
            try {
                int x = Integer.parseInt(value.substring(0, index).trim());
                int y = Integer.parseInt(value.substring(index + 1).trim());
                if (point == null) {
                    point = new Point(x, y);
                } else {
                    point.setLocation(x, y);
                }
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "could not parse point " + this.roleName + '.' + this.getName() + '.' + name + ": " + value, e);
            }
        }
        return point;
    }

    protected Rectangle getConfigBounds(String name) {
        return this.getConfigBounds(name, null);
    }

    protected Rectangle getConfigBounds(String name, Rectangle bounds) {
        String value = this.getConfigProperty(String.valueOf(name) + ".bounds");
        if (value != null) {
            try {
                int index = value.indexOf(44);
                int x = Integer.parseInt(value.substring(0, index).trim());
                int index2 = value.indexOf(44, index + 1);
                int y = Integer.parseInt(value.substring(index + 1, index2).trim());
                index = value.indexOf(44, index2 + 1);
                int width = Integer.parseInt(value.substring(index2 + 1, index).trim());
                int height = Integer.parseInt(value.substring(index + 1).trim());
                if (bounds == null) {
                    bounds = new Rectangle(x, y, width, height);
                } else {
                    bounds.setBounds(x, y, width, height);
                }
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "could not parse bounds " + this.roleName + '.' + this.getName() + '.' + name + ": " + value, e);
            }
        }
        return bounds;
    }

    protected Color getConfigColor(String name, String sub) {
        return this.getConfigColor(name, sub, null);
    }

    protected Color getConfigColor(String name, String sub, Color defaultColor) {
        String value = this.getConfigProperty(String.valueOf(name) + '.' + sub);
        if (value != null) {
            try {
                int radix = 10;
                char c = value.charAt(0);
                if (c == '#' || c == '$') {
                    value = value.substring(1);
                    radix = 16;
                } else if (c == '0' && value.charAt(1) == 'x') {
                    value = value.substring(2);
                    radix = 16;
                }
                int colorValue = Integer.parseInt(value, radix);
                return new Color(colorValue);
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "could not parse color " + this.roleName + '.' + this.getName() + '.' + name + '.' + sub + ": " + value, e);
            }
        }
        return defaultColor;
    }

    protected void layoutComponent(String name, JComponent c) {
        String opaque;
        Color col = this.getConfigColor(name, "foreground");
        if (col != null) {
            c.setForeground(col);
        }
        if ((col = this.getConfigColor(name, "background")) != null) {
            c.setBackground(col);
        }
        if ((opaque = this.getConfigProperty(String.valueOf(name) + ".opaque")) != null) {
            c.setOpaque("true".equalsIgnoreCase(opaque));
        }
        if (this.getLayout() == null) {
            Rectangle bounds = this.getConfigBounds(name);
            if (bounds == null) {
                log.warning("no bounds for " + this.roleName + '.' + this.getName() + '.' + name + ": reverting to layout manager");
                BoxLayout layout = new BoxLayout(this, 1);
                this.setLayout(layout);
            } else {
                c.setBounds(bounds);
                if (bounds.x + bounds.width > this.minWidth) {
                    this.minWidth = bounds.x + bounds.width;
                }
                if (bounds.y + bounds.height > this.minHeight) {
                    this.minHeight = bounds.y + bounds.height;
                }
            }
        }
        this.add(c);
    }

    protected JScrollPane createScrollPane(JComponent component) {
        return this.createScrollPane(component, null, false);
    }

    protected JScrollPane createScrollPane(JComponent component, String title) {
        return this.createScrollPane(component, title, false);
    }

    protected JScrollPane createScrollPane(JComponent component, String title, boolean horizontalScrollbar) {
        JScrollPane scrollPane = new JScrollPane(component, 20, horizontalScrollbar ? 30 : 31);
        Color color = component.getBackground();
        if (color == null) {
            color = Color.white;
        }
        scrollPane.setBackground(color);
        scrollPane.getViewport().setBackground(color);
        if (title != null) {
            scrollPane.setBorder(BorderFactory.createTitledBorder(title));
        }
        JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
        scrollBar.setPreferredSize(new Dimension(10, scrollBar.getHeight()));
        if (horizontalScrollbar) {
            scrollBar = scrollPane.getHorizontalScrollBar();
            scrollBar.setPreferredSize(new Dimension(scrollBar.getWidth(), 10));
        }
        return scrollPane;
    }

    protected void showDialog(JComponent dialog) {
        this.parent.showDialog(dialog);
    }

    protected JComponent getDialog() {
        return null;
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        if (d.width < this.minWidth) {
            d.width = this.minWidth;
        }
        if (d.height < this.minHeight) {
            d.height = this.minHeight;
        }
        return d;
    }

    @Override
    public Dimension getMinimumSize() {
        Dimension d = super.getMinimumSize();
        if (d.width < this.minWidth) {
            d.width = this.minWidth;
        }
        if (d.height < this.minHeight) {
            d.height = this.minHeight;
        }
        return d;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (this.isOpaque()) {
            Color originalColor = g.getColor();
            g.setColor(this.getBackground());
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
            g.setColor(originalColor);
        }
        if (this.agentIcon != null) {
            int iconWidth = this.agentIcon.getIconWidth();
            int iconHeight = this.agentIcon.getIconHeight();
            this.agentIcon.paintIcon(this, g, (this.getWidth() - iconWidth) / 2, (this.getHeight() - iconHeight) / 2);
        }
    }

    public abstract void dataUpdated(int var1, int var2);

    public abstract void dataUpdated(int var1, long var2);

    public abstract void dataUpdated(int var1, float var2);

    public abstract void dataUpdated(int var1, String var2);

    public abstract void dataUpdated(int var1, Transportable var2);

    protected void nextPhase(int phase) {
    }

    protected void nextTimeUnit(long serverTime, int timeUnit) {
    }

}

