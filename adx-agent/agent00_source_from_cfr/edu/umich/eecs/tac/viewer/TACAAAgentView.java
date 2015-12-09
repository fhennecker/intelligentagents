/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.ViewListener;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JPanel;
import se.sics.isl.util.ConfigManager;

public abstract class TACAAAgentView
extends JPanel
implements ViewListener {
    private static final Logger log = Logger.getLogger(TACAAAgentView.class.getName());
    private TACAASimulationPanel parent;
    private int index;
    private String name;
    private int role;
    private String roleName;
    private Icon agentIcon;

    protected final void initialized() {
        this.initializeView();
    }

    protected abstract void initializeView();

    final void init(TACAASimulationPanel parent, int index, String name, int role, String roleName) {
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
    }

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

    protected void nextTimeUnit(long serverTime, int timeUnit) {
    }
}

