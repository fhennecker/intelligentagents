/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.viewer.applet;

import java.awt.Component;
import java.awt.Container;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JApplet;
import javax.swing.JComponent;
import se.sics.isl.transport.ContextFactory;
import se.sics.tasim.viewer.ChatListener;
import se.sics.tasim.viewer.ViewerConnection;
import se.sics.tasim.viewer.ViewerPanel;
import se.sics.tasim.viewer.applet.AppletConnection;

public class ViewerApplet
extends JApplet
implements ChatListener {
    private static final Logger log = Logger.getLogger(ViewerApplet.class.getName());
    public static final String VERSION = "0.8.19";
    private String serverName;
    private int serverPort = 4042;
    private String userName;
    private ContextFactory contextFactory;
    private ViewerPanel mainPanel;
    private AppletConnection connection;

    @Override
    public String getAppletInfo() {
        return "TAC SIM Game Viewer v0.8.19, by SICS";
    }

    @Override
    public String[][] getParameterInfo() {
        String[][] info = new String[][]{{"user", "name", "the user name"}, {"port", "int", "the viewer connection port"}, {"serverName", "name", "the server name"}, {"contextFactory", "name", "the context factory class"}};
        return info;
    }

    @Override
    public void init() {
        String contextFactoryClassName;
        this.serverName = this.getParameter("serverName");
        this.userName = this.getParameter("user");
        if (this.serverName == null) {
            throw new IllegalArgumentException("no server name specified");
        }
        if (this.userName == null) {
            throw new IllegalArgumentException("no user name specified");
        }
        String portDesc = this.getParameter("port");
        if (portDesc != null) {
            try {
                this.serverPort = Integer.parseInt(portDesc);
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "could not parse server port '" + portDesc + '\'', e);
            }
        }
        if ((contextFactoryClassName = this.getParameter("contextFactory")) == null) {
            throw new IllegalArgumentException("no contextFactory specified");
        }
        try {
            this.contextFactory = (ContextFactory)Class.forName(contextFactoryClassName).newInstance();
        }
        catch (ClassNotFoundException e) {
            log.severe("unable to load context factory: Class not found");
        }
        catch (InstantiationException e) {
            log.severe("unable to load context factory: Class cannot be instantiated");
        }
        catch (IllegalAccessException e) {
            log.severe("unable to load context factory: Illegal access exception");
        }
        this.mainPanel = new ViewerPanel(this.userName, this.serverName);
        this.mainPanel.setChatListener(this);
        this.getContentPane().add(this.mainPanel.getComponent());
    }

    @Override
    public void start() {
        this.connection = new AppletConnection(this, this.mainPanel);
        this.connection.start();
    }

    @Override
    public void stop() {
        this.connection.stop();
    }

    @Override
    public void destroy() {
    }

    public String getServerName() {
        return this.serverName;
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public String getUserName() {
        return this.userName;
    }

    public ContextFactory getContextFactory() {
        return this.contextFactory;
    }

    public void addChatMessage(long time, String serverName, String userName, String message) {
        this.mainPanel.addChatMessage(time, serverName, userName, message);
    }

    public void setStatusMessage(String message) {
        this.mainPanel.setStatusMessage(message);
    }

    @Override
    public void sendChatMessage(String message) {
        AppletConnection connection = this.connection;
        if (connection != null) {
            connection.sendChatMessage(message);
        }
    }
}

