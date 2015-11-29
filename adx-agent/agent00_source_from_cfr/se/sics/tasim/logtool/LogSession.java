/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.logtool;

import java.io.File;
import java.io.FileFilter;
import java.util.logging.Logger;
import se.sics.isl.util.ConfigManager;
import se.sics.tasim.logtool.LogManager;
import se.sics.tasim.logtool.ValueSet;

public class LogSession
implements FileFilter {
    private static final Logger log = Logger.getLogger(LogSession.class.getName());
    private static final String CONF = "game.directory.";
    private LogManager logManager;
    private String server;
    private ValueSet games;
    private ValueSet excludes;
    private boolean isServerTree = true;
    private boolean isGameTree = false;
    private String gameDirectory;

    public LogSession(LogManager logManager, String server, ValueSet games, ValueSet excludes) {
        this.logManager = logManager;
        this.games = games;
        this.excludes = excludes;
        this.server = server;
        ConfigManager config = logManager.getConfig();
        this.isGameTree = config.getPropertyAsBoolean("game.directory.gameTree", false);
        this.isServerTree = config.getPropertyAsBoolean("game.directory.serverTree", true);
        this.gameDirectory = logManager.getGameDirectory().getAbsolutePath();
    }

    public void start() {
        boolean showGUI;
        ConfigManager config = this.logManager.getConfig();
        boolean oldShowGUI = config.getPropertyAsBoolean("showGUI", true);
        int startGame = this.games.getMin();
        int endGame = this.games.getMax();
        boolean bl = showGUI = endGame == startGame;
        if (!showGUI) {
            config.setProperty("showGUI", "false");
        }
        try {
            this.logManager.sessionStarted();
            int i = startGame;
            while (i <= endGame) {
                if (this.games.isIncluded(i) && !this.excludes.isIncluded(i)) {
                    String name = this.getFileName(i);
                    this.logManager.processDataFile(name);
                }
                ++i;
            }
        }
        finally {
            if (!showGUI && oldShowGUI) {
                config.setProperty("showGUI", "true");
            }
            this.logManager.sessionEnded();
        }
    }

    private String getFileName(int gameID) {
        StringBuffer path = new StringBuffer().append(this.gameDirectory).append(File.separatorChar);
        if (this.isServerTree && this.server != null) {
            path.append(this.server).append(File.separatorChar);
        }
        if (this.isGameTree) {
            path.append(gameID).append(File.separatorChar).append("game.slg.gz");
        } else {
            path.append("game");
            if (!this.isServerTree && this.server != null) {
                path.append('-').append(this.server).append('-');
            }
            path.append(gameID).append(".slg.gz");
        }
        return path.toString();
    }

    @Override
    public boolean accept(File file) {
        String name = file.getName();
        if (this.isGameTree) {
            int gameID = this.getNumber(name, 0, name.length());
            if (gameID > 0 && this.checkGame(gameID) && file.isDirectory()) {
                return true;
            }
            return false;
        }
        int nameLength = this.getLogEnd(name);
        if (nameLength > 0) {
            int gameID = this.getNumber(name, 6, nameLength);
            if (gameID > 0 && this.checkGame(gameID)) {
                return true;
            }
            return false;
        }
        return false;
    }

    private boolean checkGame(int gameID) {
        if (this.games.isIncluded(gameID) && !this.excludes.isIncluded(gameID)) {
            return true;
        }
        return false;
    }

    public int compare(Object o1, Object o2) {
        String n1 = ((File)o1).getName();
        String n2 = ((File)o2).getName();
        if (this.isGameTree) {
            int v1 = this.getNumber(n1, 0, n1.length());
            int v2 = this.getNumber(n2, 0, n2.length());
            if (v1 > 0 && v2 > 0) {
                return v1 - v2;
            }
        } else {
            int n2len;
            int n1len = this.getLogEnd(n1);
            if (n1len > 0 && (n2len = this.getLogEnd(n2)) > 0) {
                int v1 = this.getNumber(n1, 6, n1len);
                int v2 = this.getNumber(n2, 6, n2len);
                if (v1 > 0 && v2 > 0) {
                    return v1 - v2;
                }
            }
        }
        return n1.compareTo(n2);
    }

    private int getLogEnd(String text) {
        if (!text.startsWith("game")) {
            return -1;
        }
        if (text.endsWith(".slg")) {
            return text.length() - 4;
        }
        if (text.endsWith(".slg.gz")) {
            return text.length() - 7;
        }
        return -1;
    }

    private int getNumber(String text, int start, int end) {
        int value = 0;
        int i = start;
        while (i < end) {
            char c = text.charAt(i);
            if (c < '0' || c > '9') {
                return -1;
            }
            value = value * 10 + c - 48;
            ++i;
        }
        return value;
    }
}

