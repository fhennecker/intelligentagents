/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.border.Border;
import se.sics.isl.gui.Clock;
import se.sics.tasim.viewer.AdvPanel;
import se.sics.tasim.viewer.ViewerPanel;

public class StatusPanel
extends JPanel
implements ActionListener {
    private static final int MILLIS_PER_DAY = 86400000;
    private ViewerPanel viewerPanel;
    private JLabel serverTimeLabel;
    private long lastServerTime = 0;
    private AdvPanel advPanel;
    private JLabel simLabel;
    private Clock simClock;
    private JLabel simTimeUnitLabel;
    private int simTimeUnit = 0;
    private long millisPerUnit = 1000;
    private long nextTimeUnit = Long.MAX_VALUE;
    private int simRatio = 1;
    private int timeUnitCount = 0;
    private long simStartTime;
    private long lastTimeUnitUpdate;
    private String timeUnitName = null;
    private boolean useSimulationTime = false;
    private JProgressBar timeProgress;
    private Timer timer;
    private Timer simTimer;

    public StatusPanel(ViewerPanel viewerPanel, Color foregroundColor, Color backgroundColor) {
        super(new BorderLayout());
        this.timer = new Timer(1000, this);
        this.simTimer = null;
        this.viewerPanel = viewerPanel;
        this.setForeground(foregroundColor);
        this.setBackground(backgroundColor);
        this.setBorder(BorderFactory.createLineBorder(foregroundColor));
        Dimension dim = new Dimension(180, 50);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        panel.setForeground(foregroundColor);
        this.serverTimeLabel = new JLabel();
        this.serverTimeLabel.setForeground(foregroundColor);
        panel.add((Component)this.serverTimeLabel, "North");
        this.simLabel = new JLabel();
        this.simLabel.setForeground(foregroundColor);
        panel.add((Component)this.simLabel, "South");
        panel.setPreferredSize(dim);
        this.add((Component)panel, "West");
        this.advPanel = new AdvPanel(viewerPanel);
        this.advPanel.setForeground(foregroundColor);
        this.advPanel.setBackground(backgroundColor);
        this.add((Component)this.advPanel, "Center");
        panel = new JPanel(new BorderLayout());
        panel.setForeground(foregroundColor);
        panel.setBackground(backgroundColor);
        this.simClock = new Clock();
        this.simClock.setPreferredSize(new Dimension(50, 50));
        panel.add((Component)this.simClock, "East");
        JPanel panel2 = new JPanel(new BorderLayout());
        panel2.setBackground(backgroundColor);
        this.simTimeUnitLabel = new JLabel("", 2);
        this.simTimeUnitLabel.setVerticalAlignment(1);
        this.simTimeUnitLabel.setForeground(foregroundColor);
        panel2.add((Component)this.simTimeUnitLabel, "Center");
        this.timeProgress = new JProgressBar(0, 0, 100);
        this.timeProgress.setOpaque(false);
        this.timeProgress.setBorderPainted(false);
        this.timeProgress.setPreferredSize(new Dimension(120, 5));
        this.timeProgress.setBorder(BorderFactory.createEmptyBorder(0, 5, 3, 5));
        panel2.add((Component)this.timeProgress, "South");
        panel.add((Component)panel2, "West");
        panel.setPreferredSize(dim);
        this.add((Component)panel, "East");
        long currentTime = viewerPanel.getServerTime();
        this.updateTime(currentTime);
        this.timer.setInitialDelay((int)(currentTime / 1000 * 1000 + 1000 - currentTime));
        this.timer.setRepeats(true);
        this.timer.start();
    }

    public void simulationStarted(int simID, String simType, long startTime, long endTime, String timeUnitName, int timeUnitCount) {
        long currentTime = this.viewerPanel.getServerTime();
        this.simStartTime = startTime;
        this.simTimeUnit = 0;
        if (timeUnitCount > 0) {
            this.millisPerUnit = (endTime - startTime) / (long)timeUnitCount;
            if (this.millisPerUnit < 1000) {
                this.millisPerUnit = 1000;
            }
            this.timeUnitName = timeUnitName;
            this.simRatio = (int)(86400000 / this.millisPerUnit);
            this.timeUnitCount = timeUnitCount;
            if (currentTime > startTime) {
                this.simTimeUnit = (int)((currentTime - startTime) / this.millisPerUnit);
                this.nextTimeUnit = startTime + (long)this.simTimeUnit * this.millisPerUnit;
            } else {
                this.nextTimeUnit = startTime;
            }
        } else {
            this.millisPerUnit = Long.MAX_VALUE;
            this.timeUnitName = null;
            this.simRatio = 1;
            this.timeUnitCount = 1;
            this.nextTimeUnit = Long.MAX_VALUE;
        }
        this.lastTimeUnitUpdate = currentTime;
        this.simLabel.setText("Game " + simID);
        if (this.simRatio <= 1 || timeUnitName == null) {
            this.useSimulationTime = false;
            this.simClock.setShowingSeconds(true);
            this.simTimeUnitLabel.setText("");
        } else {
            this.useSimulationTime = true;
            this.simClock.setShowingSeconds(false);
            if (this.simTimer == null) {
                this.simTimer = new Timer(100, this);
                this.simTimer.setRepeats(true);
            }
            this.simTimer.start();
        }
        this.updateTime(currentTime);
    }

    public void simulationStopped(int simID) {
        this.useSimulationTime = false;
        if (this.simTimer != null) {
            this.simTimer.stop();
        }
        this.simRatio = 1;
        this.nextTimeUnit = Long.MAX_VALUE;
        this.simClock.setShowingSeconds(true);
        this.timeProgress.setValue(0);
    }

    public void nextTimeUnit(int timeUnit) {
        if (timeUnit < this.timeUnitCount) {
            this.nextTimeUnit = Long.MAX_VALUE;
            this.lastTimeUnitUpdate = this.viewerPanel.getServerTime();
            this.simTimeUnit = timeUnit;
            this.simTimeUnitLabel.setText(String.valueOf(this.timeUnitName) + ": " + this.simTimeUnit + " / " + (this.timeUnitCount - 1));
        }
    }

    public StringBuffer appendTime(StringBuffer sb, long time) {
        long sek = (time /= 1000) % 60;
        long minutes = time / 60 % 60;
        long hours = time / 3600 % 24;
        if (hours < 10) {
            sb.append('0');
        }
        sb.append(hours).append(':');
        if (minutes < 10) {
            sb.append('0');
        }
        sb.append(minutes).append(':');
        if (sek < 10) {
            sb.append('0');
        }
        sb.append(sek);
        return sb;
    }

    private void updateTime(long serverTime) {
        StringBuffer sb = new StringBuffer();
        sb.append("Server time: ");
        this.appendTime(sb, serverTime);
        this.serverTimeLabel.setText(sb.toString());
        if (serverTime >= this.nextTimeUnit) {
            this.nextTimeUnit += this.millisPerUnit;
            this.lastTimeUnitUpdate = serverTime;
            if (this.simTimeUnit < this.timeUnitCount - 1) {
                ++this.simTimeUnit;
                this.simTimeUnitLabel.setText(String.valueOf(this.timeUnitName) + ": " + this.simTimeUnit + " / " + (this.timeUnitCount - 1));
            }
        }
        if (this.useSimulationTime) {
            long progress = (serverTime - this.lastTimeUnitUpdate) * 100 / this.millisPerUnit;
            this.timeProgress.setValue(progress < 100 ? (int)progress : 100);
        }
        this.updateClock(serverTime);
    }

    private void updateClock(long serverTime) {
        if (this.useSimulationTime) {
            serverTime = (serverTime - this.simStartTime) * (long)this.simRatio;
        }
        this.simClock.setTime(serverTime);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == this.timer) {
            long serverTime = this.viewerPanel.getServerTime();
            this.updateTime(serverTime);
            this.viewerPanel.tick(serverTime);
        } else if (source == this.simTimer) {
            long serverTime = this.viewerPanel.getServerTime();
            this.updateClock(serverTime);
            this.viewerPanel.simulationTick(serverTime, this.simTimeUnit);
        }
    }
}

