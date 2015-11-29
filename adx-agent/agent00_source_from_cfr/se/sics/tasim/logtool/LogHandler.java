/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.logtool;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import se.sics.isl.util.ConfigManager;
import se.sics.isl.util.IllegalConfigurationException;
import se.sics.tasim.logtool.LogManager;
import se.sics.tasim.logtool.LogReader;

public abstract class LogHandler {
    private LogManager logManager;

    protected LogHandler() {
    }

    final void init(LogManager logManager) {
        if (this.logManager != null) {
            throw new IllegalStateException("already initialized");
        }
        if (logManager == null) {
            throw new NullPointerException();
        }
        this.logManager = logManager;
        this.init();
    }

    protected LogManager getLogManager() {
        return this.logManager;
    }

    public ConfigManager getConfig() {
        return this.logManager.getConfig();
    }

    public File getTempDirectory(String name) throws IOException {
        return this.logManager.getTempDirectory(name);
    }

    public void warn(String warningMessage) {
        this.logManager.warn(warningMessage);
    }

    protected void init() {
    }

    protected void sessionStarted() {
    }

    protected void sessionEnded() {
    }

    protected abstract void start(LogReader var1) throws IllegalConfigurationException, IOException, ParseException;
}

