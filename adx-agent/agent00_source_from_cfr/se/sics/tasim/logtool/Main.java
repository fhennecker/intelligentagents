/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.logtool;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.PrintStream;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import se.sics.isl.util.ArgumentManager;
import se.sics.isl.util.ConfigManager;
import se.sics.isl.util.IllegalConfigurationException;
import se.sics.tasim.logtool.LogManager;

public class Main
extends WindowAdapter {
    private Main() {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        System.exit(1);
    }

    public static void main(String[] args) throws IllegalConfigurationException, IOException {
        ArgumentManager config = new ArgumentManager("LogManager", args);
        config.addOption("config", "configfile", "set the config file to use");
        config.addOption("file", "datafile", "set the game data file to use");
        config.addOption("games", "games", "set the games as 1-2,5,7");
        config.addOption("excludes", "games", "set the games to exclude as 1-2,5,7");
        config.addOption("server", "host", "set the server for the games");
        config.addOption("handler", "loghandler", "set the game data handler to use");
        config.addOption("showGUI", "true", "set if GUI should be used when supported");
        config.addOption("xml", "show the game data as XML");
        config.addOption("game.directory", "directory", "the local game directory");
        config.addOption("game.directory.gameTree", "false", "set if each game has its own directory");
        config.addOption("game.directory.serverTree", "true", "set if each server has its own directory with games");
        config.addOption("verbose", "set for verbose output");
        config.addOption("log.consoleLevel", "level", "set the console log level");
        config.addOption("ucs", "Show user classification messages");
        config.addOption("rating", "Show rating messages");
        config.addOption("bank", "Show bank balance messages");
        config.addOption("campaign", "Show campaign messages");
        config.addOption("adnet", "Show adnet messages");
        config.addOption("all", "Show all messages");
        config.addOption("version", "show the version");
        config.addHelp("h", "show this help message");
        config.addHelp("help");
        config.validateArguments();
        if (config.hasArgument("version")) {
            System.out.println("LogManager version 0.4.1 beta");
            System.exit(0);
        }
        String configFile = config.getArgument("config", "log.conf");
        try {
            config.loadConfiguration(configFile);
            config.removeArgument("config");
        }
        catch (IllegalArgumentException e) {
            Main.showWarning(config, "could not load config", "could not load config from '" + configFile + "': " + e);
            return;
        }
        catch (Exception e) {
            e.printStackTrace();
            Main.showWarning(config, "could not load config", "could not load config from '" + configFile + "': " + e);
            return;
        }
        config.finishArguments();
        String version = System.getProperty("java.version");
        if (ConfigManager.compareVersion("1.4", version) > 0) {
            Main.showWarning(config, "Wrong Java version", "Java 2 SE 1.4 or newer required! Version " + version + " detected.");
            return;
        }
        if (config.getPropertyAsBoolean("showGUI", true) && config.getPropertyAsBoolean("useSystemUI", true)) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (UnsupportedLookAndFeelException exc) {
                System.err.println("unsupported look-and-feel: " + exc);
            }
            catch (Exception exc) {
                System.err.println("could not change look-and-feel: " + exc);
            }
        }
        new se.sics.tasim.logtool.LogManager(config);
    }

    private static void showWarning(ConfigManager config, String title, String message) {
        System.err.println(message);
        if (config.getPropertyAsBoolean("showGUI", true)) {
            Frame w = new Frame(title);
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            w.add(new Label(message));
            w.pack();
            w.setLocation((d.width - w.getWidth()) / 2, (d.height - w.getHeight()) / 2);
            w.addWindowListener(new Main());
            w.setVisible(true);
        } else {
            System.exit(1);
        }
    }
}

