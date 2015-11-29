/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.viewer;

import com.botbox.util.ArrayUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import se.sics.isl.transport.Transportable;
import se.sics.isl.util.ConfigManager;
import se.sics.tasim.viewer.ChatListener;
import se.sics.tasim.viewer.ChatPanel;
import se.sics.tasim.viewer.DialogPanel;
import se.sics.tasim.viewer.SimulationViewer;
import se.sics.tasim.viewer.StatusPanel;
import se.sics.tasim.viewer.TickListener;
import se.sics.tasim.viewer.ViewerConnection;

public class ViewerPanel
extends ViewerConnection {
    private static final Logger log = Logger.getLogger(ViewerPanel.class.getName());
    private static final Integer DIALOG_LAYER = new Integer(199);
    private Hashtable iconTable = new Hashtable();
    private ConfigManager config;
    private SimulationViewer viewer;
    private String userName;
    private String serverName;
    private long timeDiff = 0;
    private JPanel mainPanel;
    private StatusPanel statusPanel;
    private JLabel statusLabel;
    private ChatPanel chatPanel;
    private JComponent viewerPanel;
    private DialogPanel dialogPanel;
    private JComponent currentDialog;
    private TickListener[] tickListeners;
    private ChatListener chatListener;
    private Color backgroundColor = Color.black;
    private Color foregroundColor = Color.white;

    public ViewerPanel(String userName, String serverName) {
        this.serverName = serverName;
        this.userName = userName;
        String configFile = "tasim_viewer.conf";
        URL configURL = ViewerPanel.class.getResource("/config/" + configFile);
        this.config = new ConfigManager();
        try {
            if (configURL != null) {
                this.config.loadConfiguration(configURL);
            } else if (!this.config.loadConfiguration("config" + File.separatorChar + configFile)) {
                log.severe("could not find config " + configFile);
            }
        }
        catch (Exception e) {
            log.severe("could not find config " + configFile);
        }
        this.viewer = null;
        try {
            String simulationViewerClass = this.config.getProperty("simulationViewer");
            this.viewer = (SimulationViewer)Class.forName(simulationViewerClass).newInstance();
        }
        catch (InstantiationException e) {
            log.severe("Could not instantiate the simulation viewer");
        }
        catch (IllegalAccessException e) {
            log.severe("Could not instantiate the simulation viewer");
        }
        catch (ClassNotFoundException e) {
            log.severe("Could not find the simulation viewer class");
        }
        this.viewer.init(this);
        this.viewerPanel = this.viewer.getComponent();
        this.mainPanel = new JPanel(new BorderLayout());
        this.mainPanel.setForeground(this.foregroundColor);
        this.mainPanel.setBackground(this.backgroundColor);
        this.mainPanel.add((Component)this.viewerPanel, "Center");
        this.statusLabel = new JLabel("Status:");
        this.statusLabel.setOpaque(true);
        this.statusLabel.setForeground(this.foregroundColor);
        this.statusLabel.setBackground(this.backgroundColor);
        this.chatPanel = new ChatPanel(this);
        this.chatPanel.setStatusLabel(this.statusLabel);
        this.mainPanel.add((Component)this.chatPanel, "South");
        this.statusPanel = new StatusPanel(this, this.foregroundColor, this.backgroundColor);
        this.mainPanel.add((Component)this.statusPanel, "North");
    }

    public JComponent getComponent() {
        return this.mainPanel;
    }

    public ConfigManager getConfig() {
        return this.config;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getServerName() {
        return this.serverName;
    }

    public long getServerTime() {
        return System.currentTimeMillis() + this.timeDiff;
    }

    @Override
    public void setServerTime(long serverTime) {
        this.timeDiff = serverTime - System.currentTimeMillis();
    }

    public ImageIcon getIcon(String name) {
        ImageIcon icon = (ImageIcon)this.iconTable.get(name);
        if (icon != null) {
            return icon;
        }
        try {
            URL url = ViewerPanel.class.getResource(name.indexOf(47) >= 0 ? name : "/images/" + name);
            if (url != null) {
                icon = new ImageIcon(url);
                if (icon.getIconHeight() > 0) {
                    this.iconTable.put(name, icon);
                    return icon;
                }
            } else {
                log.severe("could not find icon " + name);
            }
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "could not load icon " + name, e);
        }
        return null;
    }

    public void setStatusMessage(String message) {
        this.statusLabel.setText("Status: " + message);
    }

    public void addChatMessage(long time, String serverName, String userName, String message) {
        this.chatPanel.addChatMessage(time, serverName, userName, message);
    }

    public void sendChatMessage(String message) {
        if (this.chatListener != null) {
            this.chatListener.sendChatMessage(message);
        } else {
            log.warning("no listener for chat message '" + message + '\'');
        }
    }

    public void setChatListener(ChatListener listener) {
        this.chatListener = listener;
    }

    public void showDialog(JComponent dialog) {
        this.closeDialog();
        JRootPane rootPane = SwingUtilities.getRootPane(this.mainPanel);
        if (rootPane == null) {
            log.severe("could not find root pane for viewer to show dialog " + dialog);
        } else {
            JLayeredPane layeredPane = rootPane.getLayeredPane();
            Dimension d = dialog.getPreferredSize();
            if (this.dialogPanel == null) {
                this.dialogPanel = new DialogPanel(this, new BorderLayout());
            }
            Insets insets = this.dialogPanel.getInsets();
            int width = this.viewerPanel.getWidth() - insets.left - insets.right;
            int height = this.viewerPanel.getHeight() - insets.top - insets.bottom;
            if (d.width > width) {
                d.width = width;
            }
            if (d.height > height) {
                d.height = height;
            }
            this.dialogPanel.add((Component)dialog, "Center");
            this.dialogPanel.setBounds((width - d.width >> 1) + insets.left, (height - d.height >> 1) + insets.top, d.width + insets.left + insets.right, d.height + insets.top + insets.bottom);
            dialog.setVisible(true);
            layeredPane.add((Component)this.dialogPanel, DIALOG_LAYER);
            this.currentDialog = dialog;
            this.mainPanel.repaint();
        }
    }

    final void closeDialog() {
        JRootPane rootPane;
        if (this.currentDialog != null && (rootPane = SwingUtilities.getRootPane(this.mainPanel)) != null) {
            JLayeredPane layeredPane = rootPane.getLayeredPane();
            layeredPane.remove(this.dialogPanel);
            this.currentDialog.setVisible(false);
            this.currentDialog = null;
            this.mainPanel.repaint();
        }
    }

    public synchronized void addTickListener(TickListener listener) {
        this.tickListeners = (TickListener[])ArrayUtils.add(TickListener.class, this.tickListeners, listener);
    }

    public synchronized void removeTickListener(TickListener listener) {
        this.tickListeners = (TickListener[])ArrayUtils.remove(this.tickListeners, listener);
    }

    @Override
    public void nextTimeUnit(int timeUnit) {
        this.statusPanel.nextTimeUnit(timeUnit);
        this.viewer.nextTimeUnit(timeUnit);
    }

    @Override
    public void participant(int index, int role, String name, int participantID) {
        this.viewer.participant(index, role, name, participantID);
    }

    @Override
    public void dataUpdated(int agent, int type, int value) {
        this.viewer.dataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int agent, int type, long value) {
        this.viewer.dataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int agent, int type, float value) {
        this.viewer.dataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int agent, int type, double value) {
        this.viewer.dataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int agent, int type, String value) {
        this.viewer.dataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int agent, int type, Transportable value) {
        this.viewer.dataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int type, Transportable value) {
        this.viewer.dataUpdated(type, value);
    }

    @Override
    public void interaction(int fromAgent, int toAgent, int type) {
        this.viewer.interaction(fromAgent, toAgent, type);
    }

    @Override
    public void interactionWithRole(int fromAgent, int role, int type) {
        this.viewer.interactionWithRole(fromAgent, role, type);
    }

    @Override
    public void simulationStarted(int realSimID, String type, long startTime, long endTime, String timeUnitName, int timeUnitCount) {
        this.closeDialog();
        this.statusPanel.simulationStarted(realSimID, type, startTime, endTime, timeUnitName, timeUnitCount);
        this.viewer.simulationStarted(realSimID, type, startTime, endTime, timeUnitName, timeUnitCount);
        this.setStatusMessage("Game " + realSimID + " is running");
    }

    @Override
    public void simulationStopped(int realSimID) {
        this.setStatusMessage("Game " + realSimID + " has finished");
        this.statusPanel.simulationStopped(realSimID);
        this.viewer.simulationStopped(realSimID);
    }

    @Override
    public void nextSimulation(int realSimID, long startTime) {
        if (startTime > 0) {
            StringBuffer sb = new StringBuffer();
            sb.append("Next game ");
            if (realSimID > 0) {
                sb.append(realSimID).append(' ');
            }
            sb.append("starts at ");
            this.statusPanel.appendTime(sb, startTime);
            this.setStatusMessage(sb.toString());
        } else {
            this.setStatusMessage("No future games scheduled");
        }
    }

    @Override
    public void intCache(int agent, int type, int[] cache) {
        if (cache == null) {
            System.out.println("**** CACHE IS NULL????");
            return;
        }
        int i = 0;
        int n = cache.length;
        while (i < n) {
            this.dataUpdated(agent, type, (long)cache[i]);
            ++i;
        }
    }

    final void tick(long serverTime) {
        TickListener[] listeners = this.tickListeners;
        if (listeners != null) {
            int i = 0;
            int n = listeners.length;
            while (i < n) {
                listeners[i].tick(serverTime);
                ++i;
            }
        }
    }

    final void simulationTick(long serverTime, int timeUnit) {
        TickListener[] listeners = this.tickListeners;
        if (listeners != null) {
            int i = 0;
            int n = listeners.length;
            while (i < n) {
                listeners[i].simulationTick(serverTime, timeUnit);
                ++i;
            }
        }
    }
}

