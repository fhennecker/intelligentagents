/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.logviewer;

import edu.umich.eecs.tac.logviewer.gui.MainWindow;
import edu.umich.eecs.tac.logviewer.gui.PositiveBoundedRangeModel;
import edu.umich.eecs.tac.logviewer.info.GameInfo;
import edu.umich.eecs.tac.logviewer.monitor.ParserMonitor;
import edu.umich.eecs.tac.logviewer.util.SimulationParser;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import se.sics.isl.util.ConfigManager;
import se.sics.isl.util.IllegalConfigurationException;
import se.sics.tasim.logtool.LogHandler;
import se.sics.tasim.logtool.LogReader;

public class Visualizer
extends LogHandler {
    MainWindow mainWindow = null;

    @Override
    protected void start(LogReader reader) throws IllegalConfigurationException, IOException, ParseException {
        SimulationParser sp = new SimulationParser(this, reader);
        PositiveBoundedRangeModel dayModel = new PositiveBoundedRangeModel();
        this.createMonitors(sp, dayModel);
        sp.start();
        if (sp.errorParsing()) {
            System.err.println("Error while parsing file");
            return;
        }
        GameInfo gameInfo = new GameInfo(sp);
        if (this.getConfig().getPropertyAsBoolean("showGUI", true) && gameInfo != null) {
            dayModel.setLast(gameInfo.getNumberOfDays() - 1);
            this.mainWindow = new MainWindow(gameInfo, dayModel, sp.getMonitors());
            this.mainWindow.setVisible(true);
        }
        sp = null;
    }

    private void createMonitors(SimulationParser sp, PositiveBoundedRangeModel dayModel) throws IllegalConfigurationException, IOException {
        String[] names = this.getConfig().getPropertyAsArray("monitor.names");
        ParserMonitor[] monitors = (ParserMonitor[])this.getConfig().createInstances("monitor", ParserMonitor.class, names);
        if (monitors == null) {
            return;
        }
        int i = 0;
        int n = monitors.length;
        while (i < n) {
            monitors[i].init(names[i], this, sp, dayModel);
            sp.addMonitor(monitors[i]);
            ++i;
        }
    }
}

