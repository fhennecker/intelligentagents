/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.viewer;

import com.botbox.util.ArrayUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Point;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import se.sics.isl.util.ConfigManager;
import se.sics.tasim.viewer.AgentView;
import se.sics.tasim.viewer.SimulationLayout;
import se.sics.tasim.viewer.TickListener;
import se.sics.tasim.viewer.ViewerPanel;

public class SimulationPanel
extends JPanel
implements TickListener {
    public static final int LEFT = 1;
    public static final int CENTER = 2;
    public static final int RIGHT = 3;
    public static final int TYPE_BLUE = 0;
    public static final int TYPE_YELLOW = 1;
    public static final int TYPE_GREEN = 2;
    public static final int MAX_TYPE_VALUE = 2;
    private static final int DEFAULT_PHASE_NO = 1;
    private static final int DEFAULT_LINE_NO = 72;
    private static final int X1 = 0;
    private static final int Y1 = 1;
    private static final int X2 = 2;
    private static final int Y2 = 3;
    private static final int TYPE = 4;
    private static final int ANIMATION_POS = 5;
    private static final int PARTS = 6;
    private static final int MAX_COLOR = 6;
    private static final Color LIGHTER_BLUE = new Color(10526975);
    private static final Color[] FADING_BLUES = new Color[]{new Color(128), new Color(160), new Color(208), new Color(4210928), new Color(8421631), new Color(10526975)};
    private static final Color[] FADING_YELLOW = new Color[]{new Color(8421376), new Color(10526720), new Color(13684736), new Color(15790144), new Color(16777088), new Color(16777120)};
    private static final Color[] FADING_GREENS = new Color[]{new Color(32768), new Color(40960), new Color(53248), new Color(4255808), new Color(8454016), new Color(10551200)};
    private static final int[] arrowAnim;
    private int[][] connections = new int[1][432];
    private int[] connectionCount = new int[1];
    private int currentPhase = 0;
    private int phaseIndex = 0;
    private int phaseSize;
    private int phaseNumber;
    private boolean isDoublePhase;
    private boolean animation;
    private long millisPerPhase;
    private long nextFrame;
    private long nextPhaseShift;
    private boolean isRepaintRequested;
    private Icon[] backgroundIcons;
    private int[] backgroundIconInfo;
    private int iconCount;
    private AgentView[] agentViews;
    private int participants;
    private int lastTimeUnit;
    private int[] xp;
    private int[] yp;
    private ViewerPanel viewerPanel;
    private boolean isRunning;

    static {
        int[] arrn = new int[6];
        arrn[1] = 1;
        arrn[2] = 2;
        arrn[3] = 3;
        arrn[4] = 4;
        arrn[5] = 3;
        arrowAnim = arrn;
    }

    public SimulationPanel(ViewerPanel viewerPanel) {
        super(null);
        this.phaseNumber = this.phaseSize = 1;
        this.isDoublePhase = false;
        this.animation = false;
        this.millisPerPhase = 86400000;
        this.nextPhaseShift = Long.MAX_VALUE;
        this.isRepaintRequested = false;
        this.agentViews = new AgentView[15];
        this.lastTimeUnit = 0;
        this.xp = new int[4];
        this.yp = new int[4];
        this.viewerPanel = viewerPanel;
        this.setLayout(new SimulationLayout(this, 1, 50, 2));
        this.setBackground(Color.black);
    }

    public boolean isDoublePhase() {
        return this.isDoublePhase;
    }

    public void setDoublePhase(boolean isDoublePhase) {
        this.isDoublePhase = isDoublePhase;
        if (isDoublePhase) {
            this.phaseSize = this.phaseNumber * 2;
            this.ensureConnectionCapacity(this.phaseSize);
        } else {
            this.phaseIndex = 0;
        }
    }

    public int getPhaseNumber() {
        return this.phaseNumber;
    }

    public void setPhaseNumber(int phaseNumber) {
        if (phaseNumber < 1) {
            throw new IllegalArgumentException("phase number must be positive");
        }
        this.phaseNumber = phaseNumber;
        this.phaseIndex = 0;
        this.phaseSize = this.isDoublePhase ? phaseNumber * 2 : phaseNumber;
        this.ensureConnectionCapacity(this.phaseSize);
    }

    private void ensureConnectionCapacity(int size) {
        if (this.connectionCount.length < size) {
            this.connectionCount = ArrayUtils.setSize(this.connectionCount, size);
            this.connections = (int[][])ArrayUtils.setSize((Object[])this.connections, size);
        } else {
            int i = size;
            int n = this.connectionCount.length;
            while (i < n) {
                this.connectionCount[i] = 0;
                ++i;
            }
        }
    }

    public synchronized void addIcon(Icon icon, int dx, int dy) {
        if (this.backgroundIcons == null) {
            this.backgroundIcons = new Icon[2];
            this.backgroundIconInfo = new int[2];
        } else if (this.iconCount == this.backgroundIcons.length) {
            this.backgroundIcons = (Icon[])ArrayUtils.setSize(this.backgroundIcons, this.iconCount + 5);
            this.backgroundIconInfo = ArrayUtils.setSize(this.backgroundIconInfo, this.iconCount + 5);
        }
        if (dx != 1 && dx != 2 && dx != 3) {
            throw new IllegalArgumentException("illegal dx: " + dx);
        }
        if (dy != 1 && dy != 2 && dy != 3) {
            throw new IllegalArgumentException("illegal dy: " + dy);
        }
        this.backgroundIcons[this.iconCount] = icon;
        this.backgroundIconInfo[this.iconCount++] = dx << 8 | dy;
    }

    public AgentView getAgentView(int agentID) {
        return agentID < this.participants ? this.agentViews[agentID] : null;
    }

    public String getAgentName(int agentIndex) {
        AgentView view = this.getAgentView(agentIndex);
        return view != null ? view.getName() : Integer.toString(agentIndex);
    }

    public int getHighestAgentIndex() {
        return this.participants;
    }

    public void addAgentView(AgentView view, int index, String name, int role, String roleName, int container) {
        if (this.agentViews.length <= index) {
            this.agentViews = (AgentView[])ArrayUtils.setSize(this.agentViews, index + 10);
        }
        if (this.participants <= index) {
            this.participants = index + 1;
        }
        view.init(this, index, name, role, roleName);
        this.agentViews[index] = view;
        this.add((Component)view, new Integer(container));
    }

    public void removeAgentView(AgentView view) {
        int id = view.getIndex();
        if (id < this.participants) {
            this.agentViews[id] = null;
        }
        this.remove(view);
    }

    public void simulationStarted(long startTime, long endTime, int timeUnitCount) {
        this.clear();
        if (timeUnitCount < 1) {
            timeUnitCount = 1;
        }
        this.millisPerPhase = (endTime - startTime) / (long)(timeUnitCount * this.phaseNumber);
        if (this.millisPerPhase < 100) {
            this.millisPerPhase = 100;
        }
        long currentTime = this.viewerPanel.getServerTime();
        this.phaseIndex = 0;
        if (currentTime > startTime) {
            int currentPhase = (int)((currentTime - startTime) / this.millisPerPhase);
            this.setPhase(currentPhase % this.phaseSize);
            this.nextPhaseShift = startTime + (long)(currentPhase + 1) * this.millisPerPhase;
        } else {
            this.setPhase(0);
            this.nextPhaseShift = startTime + this.millisPerPhase;
        }
        if (!this.isRunning) {
            this.viewerPanel.addTickListener(this);
            this.isRunning = true;
        }
    }

    public void simulationStopped() {
        this.isRunning = false;
        this.viewerPanel.removeTickListener(this);
        this.nextPhaseShift = Long.MAX_VALUE;
        int i = 0;
        while (i < this.phaseSize) {
            this.connectionCount[i] = 0;
            ++i;
        }
        this.animation = false;
        this.repaint();
    }

    public void clear() {
        this.nextPhaseShift = Long.MAX_VALUE;
        int i = 0;
        while (i < this.phaseSize) {
            this.connectionCount[i] = 0;
            ++i;
        }
        int participants = this.participants;
        this.participants = 0;
        int i2 = 0;
        int n = participants;
        while (i2 < n) {
            this.agentViews[i2] = null;
            ++i2;
        }
        this.removeAll();
        this.repaint();
    }

    public void nextTimeUnit(int timeUnit) {
        if (this.isDoublePhase) {
            this.phaseIndex = timeUnit % 2 * this.phaseNumber;
        }
    }

    @Override
    public void tick(long serverTime) {
        if (serverTime >= this.nextPhaseShift) {
            this.nextPhaseShift += this.millisPerPhase;
            this.setPhase((this.currentPhase + 1) % this.phaseSize);
            int i = 0;
            while (i < this.participants) {
                AgentView view = this.agentViews[i];
                if (view != null) {
                    view.nextPhase(this.currentPhase);
                }
                ++i;
            }
            this.repaint();
        }
    }

    @Override
    public void simulationTick(long serverTime, int timeUnit) {
        if (timeUnit != this.lastTimeUnit) {
            this.lastTimeUnit = timeUnit;
            int i = 0;
            while (i < this.participants) {
                AgentView view = this.agentViews[i];
                if (view != null) {
                    view.nextTimeUnit(serverTime, timeUnit);
                }
                ++i;
            }
        }
        if (this.isRepaintRequested) {
            this.repaint();
            this.isRepaintRequested = false;
        }
    }

    public void setPhase(int phase) {
        if (phase >= this.phaseSize) {
            throw new IllegalArgumentException("phase: " + phase + ", phaseNumber: " + this.phaseNumber + ", phaseSize: " + this.phaseSize);
        }
        this.connectionCount[this.currentPhase] = 0;
        this.currentPhase = phase;
        this.animation = true;
        this.nextFrame = 0;
    }

    public void addConnection(AgentView fromView, AgentView toView, int phase, int type) {
        int height = this.getHeight();
        int x = toView.getX() + toView.getWidth() / 2;
        int y = toView.getY() + toView.getHeight() / 2;
        Point from = fromView.getConnectionPoint(type, x, y, false);
        int x1 = from.x;
        int y1 = from.y;
        from = toView.getConnectionPoint(type, x1, y1, true, from);
        if (height > 0) {
            from.y += (y1 - y) * toView.getHeight() / (2 * height);
        }
        this.addConnection(x1, y1, from.x, from.y, phase, type);
    }

    private void addConnection(int x1, int y1, int x2, int y2, int phase, int type) {
        int index = this.connectionCount[phase += this.phaseIndex] * 6;
        int[] conns = this.connections[phase];
        if (conns == null) {
            conns = this.connections[phase] = new int[index + 30];
        } else if (index >= conns.length) {
            conns = this.connections[phase] = ArrayUtils.setSize(conns, index + 30);
        }
        conns[index + 0] = x1;
        conns[index + 1] = y1;
        conns[index + 2] = x2;
        conns[index + 3] = y2;
        conns[index + 4] = type;
        conns[index + 5] = 0;
        int[] arrn = this.connectionCount;
        int n = phase;
        arrn[n] = arrn[n] + 1;
        this.animation = true;
        this.requestRepaint();
    }

    ConfigManager getConfig() {
        return this.viewerPanel.getConfig();
    }

    Icon getIcon(String name) {
        return this.viewerPanel.getIcon(name);
    }

    void showDialog(JComponent dialog) {
        this.viewerPanel.showDialog(dialog);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Color oldColor = g.getColor();
        int width = this.getWidth();
        int height = this.getHeight();
        if (this.isOpaque()) {
            g.setColor(this.getBackground());
            g.fillRect(0, 0, width, height);
        }
        int i = 0;
        while (i < this.iconCount) {
            int info = this.backgroundIconInfo[i];
            int dx = info >> 8;
            int dy = info & 255;
            Icon icon = this.backgroundIcons[i];
            dx = dx == 1 ? 0 : (dx == 3 ? width - icon.getIconWidth() - 1 : (width - icon.getIconWidth()) / 2);
            dy = dy == 1 ? 0 : (dy == 3 ? height - icon.getIconHeight() - 1 : (height - icon.getIconHeight()) / 2);
            icon.paintIcon(this, g, dx, dy);
            ++i;
        }
        this.updateLines(g);
        g.setColor(oldColor);
    }

    private void updateLines(Graphics g) {
        long currentTime;
        Color[] colors = null;
        int[] conns = this.connections[this.currentPhase];
        int maxAnim = arrowAnim.length;
        boolean newFrame = false;
        if (this.animation && this.nextFrame < (currentTime = System.currentTimeMillis())) {
            newFrame = true;
            this.nextFrame = currentTime + 100;
            this.animation = false;
        }
        int i = 0;
        int n = this.connectionCount[this.currentPhase] * 6;
        while (i < n) {
            int type = conns[i + 4];
            int x1 = conns[i + 0];
            int x2 = conns[i + 2];
            int y1 = conns[i + 1];
            int y2 = conns[i + 3];
            int xl = x1 - x2;
            int yl = y1 - y2;
            double len = Math.sqrt(xl * xl + yl * yl);
            double dx = (double)(x1 - x2) / len;
            double dy = (double)(y1 - y2) / len;
            int animPos = conns[i + 5];
            int intensity = arrowAnim[animPos];
            if (newFrame && animPos < maxAnim - 1) {
                int[] arrn = conns;
                int n2 = i + 5;
                arrn[n2] = arrn[n2] + 1;
                this.animation = true;
            }
            switch (type) {
                case 1: {
                    colors = FADING_YELLOW;
                    break;
                }
                case 2: {
                    colors = FADING_GREENS;
                    break;
                }
                default: {
                    colors = FADING_BLUES;
                }
            }
            g.setColor(colors[intensity]);
            this.makeArrow(x1, y1, x2, y2, 4, 2, dx, dy, this.xp, this.yp);
            g.fillPolygon(this.xp, this.yp, 4);
            this.makeArrow(x2, y2, x2 - (int)(dx * 10.0), y2 - (int)(dy * 10.0), 6, 1, dx, dy, this.xp, this.yp);
            g.fillPolygon(this.xp, this.yp, 4);
            g.setColor(colors[intensity + 1]);
            this.makeArrow(x1 -= (int)(dx * 4.0), y1 -= (int)(dy * 4.0), x2 -= (int)(dx * 2.0), y2 -= (int)(dy * 2.0), 2, 1, dx, dy, this.xp, this.yp);
            g.fillPolygon(this.xp, this.yp, 4);
            this.makeArrow(x2, y2, x2 - (int)(dx * 6.0), y2 - (int)(dy * 6.0), 5, 1, dx, dy, this.xp, this.yp);
            g.fillPolygon(this.xp, this.yp, 4);
            i += 6;
        }
        if (this.animation) {
            this.requestRepaint();
        }
    }

    private void makeArrow(int x1, int y1, int x2, int y2, int width1, int width2, double dx, double dy, int[] xp, int[] yp) {
        xp[0] = x1 + (int)(dy * (double)width1);
        yp[0] = y1 - (int)(dx * (double)width1);
        xp[1] = x1 - (int)(dy * (double)width1);
        yp[1] = y1 + (int)(dx * (double)width1);
        xp[3] = x2 + (int)(dy * (double)width2);
        yp[3] = y2 - (int)(dx * (double)width2);
        xp[2] = x2 - (int)(dy * (double)width2);
        yp[2] = y2 + (int)(dx * (double)width2);
    }

    private void requestRepaint() {
        this.isRepaintRequested = true;
    }
}

