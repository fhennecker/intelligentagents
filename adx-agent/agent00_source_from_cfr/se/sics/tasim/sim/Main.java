/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.sim;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.StringTokenizer;
import se.sics.isl.util.AdminMonitor;
import se.sics.isl.util.ArgumentManager;
import se.sics.isl.util.ConfigManager;
import se.sics.isl.util.IllegalConfigurationException;
import se.sics.tasim.sim.Admin;
import se.sics.tasim.sim.Gateway;
import se.sics.tasim.sim.SimulationManager;

public class Main {
    private static final String DEFAULT_CONFIG = "config/server.conf";
    public static final String CONF = "sim.";

    private Main() {
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalConfigurationException {
        AdminMonitor adminMonitor;
        ArgumentManager config = new ArgumentManager("Simulator", args);
        config.addOption("config", "configfile", "set the config file to use");
        config.addOption("serverName", "serverName", "set the server name");
        config.addOption("log.consoleLevel", "level", "set the console log level");
        config.addOption("log.fileLevel", "level", "set the file log level");
        config.addHelp("h", "show this help message");
        config.addHelp("help");
        config.validateArguments();
        String configFile = config.getArgument("config", "config/server.conf");
        try {
            config.loadConfiguration(configFile);
            config.removeArgument("config");
        }
        catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            config.usage(1);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        config.finishArguments();
        Admin admin = new Admin(config);
        String[] simNames = Main.split(config.getProperty("sim.manager.names"));
        SimulationManager[] simManagers = (SimulationManager[])Main.createInstances(config, "sim.manager", SimulationManager.class, simNames, true);
        int i = 0;
        int n = simNames.length;
        while (i < n) {
            simManagers[i].init(admin, simNames[i]);
            ++i;
        }
        String[] gateways = Main.split(config.getProperty("sim.gateway.names"));
        Gateway[] ways = (Gateway[])Main.createInstances(config, "sim.gateway", Gateway.class, gateways, false);
        if (ways != null) {
            int i2 = 0;
            int n2 = ways.length;
            while (i2 < n2) {
                ways[i2].init(admin, gateways[i2]);
                ways[i2].start();
                ++i2;
            }
        }
        if (config.getPropertyAsBoolean("admin.gui", false) && (adminMonitor = AdminMonitor.getDefault()) != null) {
            String bounds = config.getProperty("admin.bounds");
            if (bounds != null) {
                adminMonitor.setBounds(bounds);
            }
            adminMonitor.setTitle(admin.getServerName());
            adminMonitor.start();
        }
        if (config.getPropertyAsBoolean("sim.createSimulation", false) && !admin.createSimulation(null, null, false)) {
            System.exit(1);
        }
    }

    private static String[] split(String nList) {
        StringTokenizer tok;
        int len;
        if (nList != null && (len = (tok = new StringTokenizer(nList, ", \t")).countTokens()) > 0) {
            String[] names = new String[len];
            int i = 0;
            while (i < len) {
                names[i] = tok.nextToken();
                ++i;
            }
            return names;
        }
        return null;
    }

    private static Object[] createInstances(ConfigManager config, String name, Class type, String[] simNames, boolean exitIfEmpty) {
        if (simNames != null && simNames.length > 0) {
            String className = null;
            String iName = null;
            try {
                Object[] vector = (Object[])Array.newInstance(type, simNames.length);
                int i = 0;
                int n = simNames.length;
                while (i < n) {
                    iName = simNames[i];
                    className = config.getProperty(String.valueOf(name) + '.' + iName + ".class");
                    if (className == null) {
                        throw new IllegalArgumentException("no class for manager " + iName + " specified");
                    }
                    vector[i] = Class.forName(className).newInstance();
                    ++i;
                }
                return vector;
            }
            catch (Exception e) {
                System.err.println("could not create " + name + ' ' + iName + " '" + className + '\'');
                e.printStackTrace();
                System.exit(1);
            }
        }
        if (exitIfEmpty) {
            System.err.println("no " + name + " specified in configuration");
            System.exit(1);
        }
        return null;
    }
}

