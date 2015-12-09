/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.agentware;

import com.botbox.util.ArrayUtils;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Agent;
import se.sics.tasim.aw.AgentService;
import se.sics.tasim.aw.Message;
import se.sics.tasim.aw.TimeListener;
import se.sics.tasim.props.SimulationStatus;
import se.sics.tasim.props.StartInfo;
import tau.tac.adx.agentware.SimClient;

public class AgentServiceImpl
extends AgentService {
    private static final Logger log = Logger.getLogger(AgentServiceImpl.class.getName());
    private final StartInfo startInfo;
    private TimeListener[] timeListeners;
    private final SimClient client;
    private int currentTimeUnit = -1;
    private int maxTimeUnits = Integer.MAX_VALUE;
    private int simulationDay = -1;
    private boolean isAwaitingNewDay = true;
    private int timerTimeUnit;
    private Timer timer;
    private TimerTask timerTask;

    public AgentServiceImpl(SimClient client, String name, Agent agent, Message setupMessage) {
        super(agent, name);
        this.client = client;
        this.startInfo = (StartInfo)setupMessage.getContent();
        this.initializeAgent();
        this.simulationSetup(setupMessage.getReceiver());
        int millisPerTimeUnit = this.startInfo.getSecondsPerDay() * 1000;
        if (millisPerTimeUnit > 0) {
            this.maxTimeUnits = this.startInfo.getNumberOfDays() + 1;
            this.setupTimer(this.startInfo.getStartTime(), millisPerTimeUnit);
        }
    }

    final void stopAgent() {
        if (this.timerTask != null) {
            this.timerTask.cancel();
        }
        if (this.timer != null) {
            this.timer.cancel();
        }
        this.timerTask = null;
        this.timer = null;
        this.simulationStopped();
        this.simulationFinished();
    }

    @Override
    protected void deliverToServer(Message message) {
        this.client.deliverToServer(message);
    }

    @Override
    protected void deliverToServer(int role, Transportable message) {
        log.severe("Agent can not deliver to role " + role);
    }

    @Override
    protected long getServerTime() {
        return this.client.getServerTime();
    }

    @Override
    protected void deliverToAgent(Message message) {
        if (this.isAwaitingNewDay) {
            this.isAwaitingNewDay = false;
            this.notifyTimeListeners(++this.simulationDay);
        }
        try {
            Transportable content = message.getContent();
            if (content instanceof SimulationStatus) {
                this.simulationDay = ((SimulationStatus)content).getCurrentDate();
                this.isAwaitingNewDay = true;
                this.notifyTimeListeners(this.simulationDay);
            }
            super.deliverToAgent(message);
        }
        catch (ThreadDeath e) {
            log.log(Level.SEVERE, "message thread died", e);
            throw e;
        }
        catch (Throwable e) {
            log.log(Level.SEVERE, "agent could not handle message " + message, e);
        }
    }

    private void setupTimer(long startServerTime, int millisPerTimeUnit) {
        this.timer = new Timer();
        this.timerTask = new TimerTask(){

            @Override
            public void run() {
                AgentServiceImpl.this.tick();
            }
        };
        long startTime = startServerTime + this.client.getTimeDiff();
        long currentServerTime = this.client.getServerTime();
        if (currentServerTime > startServerTime) {
            this.currentTimeUnit = (int)((currentServerTime - startServerTime) / (long)millisPerTimeUnit);
            startTime += (long)(this.currentTimeUnit * millisPerTimeUnit);
        }
        this.timer.scheduleAtFixedRate(this.timerTask, new Date(startTime), (long)millisPerTimeUnit);
    }

    private void tick() {
        this.notifyTimeListeners(this.timerTimeUnit++);
    }

    private void notifyTimeListeners(int unit) {
        boolean notify = false;
        AgentServiceImpl agentServiceImpl = this;
        synchronized (agentServiceImpl) {
            if (unit > this.currentTimeUnit) {
                this.currentTimeUnit = unit;
                notify = true;
            }
        }
        if (notify) {
            log.fine("*** TIME UNIT " + this.currentTimeUnit);
            if (unit > this.maxTimeUnits) {
                this.client.showWarning("Forced Simulation End", "forcing simulation to end at time unit " + unit + " (max " + this.maxTimeUnits + " time units)");
                this.client.stopSimulation(this);
            } else {
                TimeListener[] listeners = this.timeListeners;
                if (listeners != null) {
                    int i = 0;
                    int n = listeners.length;
                    while (i < n) {
                        try {
                            listeners[i].nextTimeUnit(this.currentTimeUnit);
                        }
                        catch (ThreadDeath e) {
                            throw e;
                        }
                        catch (Throwable e) {
                            log.log(Level.SEVERE, "could not deliver time unit " + this.currentTimeUnit + " to " + listeners[i], e);
                        }
                        ++i;
                    }
                }
            }
        }
    }

    @Override
    protected synchronized void addTimeListener(TimeListener listener) {
        this.timeListeners = (TimeListener[])ArrayUtils.add(TimeListener.class, this.timeListeners, listener);
    }

    @Override
    protected synchronized void removeTimeListener(TimeListener listener) {
        this.timeListeners = (TimeListener[])ArrayUtils.remove(this.timeListeners, listener);
    }

}

