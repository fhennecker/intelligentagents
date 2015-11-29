/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer;

import edu.umich.eecs.tac.viewer.TACAAAgentView;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.role.AgentSupport;
import edu.umich.eecs.tac.viewer.role.adnet.AdNetTabPanel;
import edu.umich.eecs.tac.viewer.role.adx.AdxDashboardTabPanel;
import edu.umich.eecs.tac.viewer.role.campaign.CampaignTabPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.LayoutManager;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import se.sics.isl.transport.Transportable;
import se.sics.isl.util.ConfigManager;
import se.sics.tasim.viewer.TickListener;
import se.sics.tasim.viewer.ViewerPanel;

public class TACAASimulationPanel
extends JPanel
implements TickListener,
ViewListener {
    private final Object lock;
    AgentSupport agentSupport = new AgentSupport();
    private JTabbedPane tabbedPane;
    private final ViewerPanel viewerPanel;
    private boolean isRunning;
    private final List<ViewListener> viewListeners;
    private final List<TickListener> tickListeners;

    public TACAASimulationPanel(ViewerPanel viewerPanel) {
        super(null);
        this.viewerPanel = viewerPanel;
        this.viewListeners = new CopyOnWriteArrayList<ViewListener>();
        this.tickListeners = new CopyOnWriteArrayList<TickListener>();
        this.lock = new Object();
        this.initialize();
    }

    protected void initialize() {
        this.setLayout(new BorderLayout());
        this.tabbedPane = new JTabbedPane(2);
        this.setBackground(Color.WHITE);
        this.add((Component)this.tabbedPane, "Center");
        this.tabbedPane.setBackground(Color.WHITE);
    }

    protected void createTabs() {
        this.tabbedPane.addTab("ADX Dashboard", null, new AdxDashboardTabPanel(this), "Click to view ADX Dashboard");
        this.tabbedPane.addTab("AdNet", null, new AdNetTabPanel(this), "Click to view AdNets");
        this.tabbedPane.addTab("Campaigns", null, new CampaignTabPanel(this), "Click to view Campaigns");
    }

    public TACAAAgentView getAgentView(int agentID) {
        return null;
    }

    public String getAgentName(int agentIndex) {
        TACAAAgentView view = this.getAgentView(agentIndex);
        return view != null ? view.getName() : Integer.toString(agentIndex);
    }

    public int getHighestAgentIndex() {
        return this.agentSupport.size();
    }

    public void addAgentView(TACAAAgentView view, int index, String name, int role, String roleName, int container) {
    }

    public void removeAgentView(TACAAAgentView view) {
    }

    public void simulationStarted(long startTime, long endTime, int timeUnitCount) {
        this.clear();
        this.createTabs();
        if (!this.isRunning) {
            this.viewerPanel.addTickListener(this);
            this.isRunning = true;
        }
    }

    public void simulationStopped() {
        this.isRunning = false;
        this.viewerPanel.removeTickListener(this);
        this.repaint();
    }

    public void clear() {
        this.agentSupport = new AgentSupport();
        this.tabbedPane.removeAll();
        this.clearViewListeners();
        this.clearTickListeners();
        this.repaint();
    }

    public void nextTimeUnit(int timeUnit) {
    }

    @Override
    public void tick(long serverTime) {
        this.fireTick(serverTime);
    }

    @Override
    public void simulationTick(long serverTime, int timeUnit) {
        this.fireSimulationTick(serverTime, timeUnit);
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

    public void addViewListener(ViewListener listener) {
        Object object = this.lock;
        synchronized (object) {
            int i = 0;
            while (i < this.agentSupport.size()) {
                listener.participant(this.agentSupport.agent(i), this.agentSupport.role(i), this.agentSupport.name(i), this.agentSupport.participant(i));
                ++i;
            }
            this.viewListeners.add(listener);
        }
    }

    public void removeViewListener(ViewListener listener) {
        Object object = this.lock;
        synchronized (object) {
            this.viewListeners.remove(listener);
        }
    }

    protected void clearViewListeners() {
        Object object = this.lock;
        synchronized (object) {
            this.viewListeners.clear();
        }
    }

    public void addTickListener(TickListener listener) {
        Object object = this.lock;
        synchronized (object) {
            this.tickListeners.add(listener);
        }
    }

    public void removeTickListener(TickListener listener) {
        Object object = this.lock;
        synchronized (object) {
            this.tickListeners.remove(listener);
        }
    }

    protected void clearTickListeners() {
        Object object = this.lock;
        synchronized (object) {
            this.tickListeners.clear();
        }
    }

    @Override
    public void dataUpdated(int agent, int type, int value) {
        this.fireDataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int agent, int type, long value) {
        this.fireDataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int agent, int type, float value) {
        this.fireDataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int agent, int type, double value) {
        this.fireDataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int agent, int type, String value) {
        this.fireDataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int agent, int type, Transportable value) {
        this.fireDataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int type, Transportable value) {
        this.fireDataUpdated(type, value);
    }

    @Override
    public void participant(int agent, int role, String name, int participantID) {
        this.agentSupport.participant(agent, role, name, participantID);
        this.fireParticipant(agent, role, name, participantID);
    }

    protected void fireParticipant(int agent, int role, String name, int participantID) {
        Object object = this.lock;
        synchronized (object) {
            for (ViewListener listener : this.viewListeners) {
                listener.participant(agent, role, name, participantID);
            }
        }
    }

    protected void fireDataUpdated(int agent, int type, int value) {
        Object object = this.lock;
        synchronized (object) {
            for (ViewListener listener : this.viewListeners) {
                listener.dataUpdated(agent, type, value);
            }
        }
    }

    protected void fireDataUpdated(int agent, int type, long value) {
        Object object = this.lock;
        synchronized (object) {
            for (ViewListener listener : this.viewListeners) {
                listener.dataUpdated(agent, type, value);
            }
        }
    }

    protected void fireDataUpdated(int agent, int type, float value) {
        Object object = this.lock;
        synchronized (object) {
            for (ViewListener listener : this.viewListeners) {
                listener.dataUpdated(agent, type, value);
            }
        }
    }

    protected void fireDataUpdated(int agent, int type, double value) {
        for (ViewListener listener : this.viewListeners) {
            listener.dataUpdated(agent, type, value);
        }
    }

    protected void fireDataUpdated(int agent, int type, String value) {
        Object object = this.lock;
        synchronized (object) {
            for (ViewListener listener : this.viewListeners) {
                listener.dataUpdated(agent, type, value);
            }
        }
    }

    protected void fireDataUpdated(int agent, int type, Transportable value) {
        Object object = this.lock;
        synchronized (object) {
            for (ViewListener listener : this.viewListeners) {
                listener.dataUpdated(agent, type, value);
            }
        }
    }

    protected void fireDataUpdated(int type, Transportable value) {
        Object object = this.lock;
        synchronized (object) {
            for (ViewListener listener : this.viewListeners) {
                listener.dataUpdated(type, value);
            }
        }
    }

    protected void fireTick(long serverTime) {
        Object object = this.lock;
        synchronized (object) {
            for (TickListener listener : new CopyOnWriteArrayList<TickListener>(this.tickListeners)) {
                listener.tick(serverTime);
            }
        }
    }

    protected void fireSimulationTick(long serverTime, int timeUnit) {
        Object object = this.lock;
        synchronized (object) {
            for (TickListener listener : new CopyOnWriteArrayList<TickListener>(this.tickListeners)) {
                listener.simulationTick(serverTime, timeUnit);
            }
        }
    }
}

