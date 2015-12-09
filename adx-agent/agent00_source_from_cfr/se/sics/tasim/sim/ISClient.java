/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.sim;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import se.sics.isl.util.ConfigManager;
import se.sics.tasim.is.EventWriter;
import se.sics.tasim.sim.Admin;
import se.sics.tasim.sim.BuiltinGUIWriter;
import se.sics.tasim.viewer.ChatListener;
import se.sics.tasim.viewer.ViewerConnection;
import se.sics.tasim.viewer.ViewerPanel;

final class ISClient
implements ChatListener {
    private final Admin admin;
    private JFrame window;
    private ViewerPanel viewer;
    private ViewerConnection writer;

    public ISClient(Admin admin, EventWriter realWriter) {
        this.admin = admin;
        ConfigManager config = admin.getConfig();
        if (config.getPropertyAsBoolean("sim.gui.systemLookAndFeel", false)) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (UnsupportedLookAndFeelException exc) {
                Logger.global.warning("ISClient: unsupported look-and-feel: " + exc);
            }
            catch (Exception exc) {
                Logger.global.warning("ISClient: could not change look-and-feel: " + exc);
            }
        }
        String serverName = admin.getServerName();
        this.window = new JFrame("Simulation Viewer for " + serverName);
        this.window.setDefaultCloseOperation(3);
        this.viewer = new ViewerPanel("admin", serverName);
        this.viewer.setChatListener(this);
        this.writer = new BuiltinGUIWriter(this.viewer, realWriter);
        this.window.getContentPane().add(this.viewer.getComponent());
        this.window.setSize(config.getPropertyAsInt("sim.gui.width", 800), config.getPropertyAsInt("sim.gui.height", 750));
        Dimension screenSize = this.window.getToolkit().getScreenSize();
        this.window.setLocation(config.getPropertyAsInt("sim.gui.x", (screenSize.width - this.window.getWidth()) / 2), config.getPropertyAsInt("sim.gui.y", (screenSize.height - this.window.getHeight()) / 2));
    }

    public void start() {
        this.window.setVisible(true);
    }

    public EventWriter getEventWriter() {
        return this.writer;
    }

    public ViewerConnection getViewerConnection() {
        return this.writer;
    }

    public void addChatMessage(long time, String serverName, String userName, String message) {
        this.viewer.addChatMessage(time, serverName, userName, message);
    }

    @Override
    public void sendChatMessage(String message) {
        this.admin.sendChatMessage(message);
    }
}

