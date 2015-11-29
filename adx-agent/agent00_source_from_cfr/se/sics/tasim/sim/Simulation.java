/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.sim;

import com.botbox.util.ArrayUtils;
import com.botbox.util.ThreadPool;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.isl.transport.Transportable;
import se.sics.isl.util.ConfigManager;
import se.sics.isl.util.IllegalConfigurationException;
import se.sics.tasim.aw.Agent;
import se.sics.tasim.aw.Message;
import se.sics.tasim.aw.TimeListener;
import se.sics.tasim.is.EventWriter;
import se.sics.tasim.is.SimulationInfo;
import se.sics.tasim.sim.Admin;
import se.sics.tasim.sim.AgentChannel;
import se.sics.tasim.sim.ExternalAgent;
import se.sics.tasim.sim.LogWriter;
import se.sics.tasim.sim.MessageDispatcher;
import se.sics.tasim.sim.SimulationAgent;

public abstract class Simulation {
    private static final Logger log = Logger.getLogger(Simulation.class.getName());
    public static final String COORDINATOR = "coordinator";
    public static final int COORDINATOR_INDEX = 0;
    private static final int INIT_STATUS = 0;
    private static final int STARTED_STATUS = 1;
    private static final int PREPARED_STOP_STATUS = 2;
    private static final int STOPPED_STATUS = 3;
    protected static final int RECOVERY_NONE = 0;
    protected static final int RECOVERY_IMMEDIATELY = 1;
    protected static final int RECOVERY_AFTER_NEXT_TICK = 2;
    private Random random = new Random();
    private Admin admin;
    private ConfigManager config;
    private SimulationInfo info;
    private EventWriter rootEventWriter;
    private EventWriter eventWriter;
    private ThreadPool simulationThreadPool;
    private Hashtable agentTable = new Hashtable();
    private SimulationAgent[] agentList;
    private int[] agentRoles;
    private SimulationAgent[][] agentsPerRole;
    private int agentRoleNumber;
    private int runtimeStatus = 0;
    private int currentTimeUnit;
    private String logFileName;
    private LogWriter logWriter;
    private TimeListener[] timeListeners;
    private MessageDispatcher dispatcher;
    private Timer timer;
    private TimerTask tickTask;
    private SimulationAgent[] agentsToRecover;
    private boolean hasAgentsToRecover = false;
    private int dummyAgentCounter = -1;

    protected Simulation(ConfigManager config) {
        this.config = config;
    }

    final void init(Admin admin, SimulationInfo info, String logFileName, ThreadPool simulationThreadPool) {
        this.admin = admin;
        this.info = info;
        this.logFileName = logFileName;
        this.simulationThreadPool = simulationThreadPool;
        this.rootEventWriter = admin.getEventWriter();
        if (this.config == null) {
            this.config = admin.getConfig();
        }
        this.dispatcher = new MessageDispatcher(admin, this, "sim" + info.getSimulationID());
    }

    final void setup() throws IllegalConfigurationException {
        if (this.runtimeStatus == 0) {
            this.setupSimulation();
            this.getSimulationInfo().setFull();
        }
    }

    final void start() {
        if (this.runtimeStatus == 0) {
            LogWriter writer;
            this.runtimeStatus = 1;
            LogWriter logWriter = writer = this.getLogWriter();
            synchronized (logWriter) {
                String params = this.info.getParams();
                writer.node("simulation").attr("simID", this.info.getSimulationID()).attr("id", this.info.getID()).attr("type", this.info.getType());
                if (params != null) {
                    writer.attr("params", params);
                }
                writer.attr("startTime", this.info.getStartTime()).attr("length", this.info.getSimulationLength() / 1000).attr("serverName", this.admin.getServerName()).attr("version", "0.8.19");
                SimulationAgent[] agents = this.getAgents();
                if (agents != null) {
                    int i = 0;
                    int n = agents.length;
                    while (i < n) {
                        SimulationAgent a = agents[i];
                        int participantID = a.getParticipantID();
                        String name = a.getName();
                        String address = a.getAddress();
                        int index = a.getIndex();
                        int role = a.getRole();
                        writer.node("participant");
                        writer.attr("index", index);
                        writer.attr("role", role);
                        writer.attr("address", address);
                        if (participantID >= 0) {
                            writer.attr("name", name);
                            writer.attr("id", participantID);
                        }
                        writer.endNode("participant");
                        this.rootEventWriter.participant(index, role, name, participantID);
                        ++i;
                    }
                }
                writer.endNode("simulation");
                writer.commit();
            }
            this.setupAgents();
            this.dispatcher.startDispatcher();
            this.startSimulation();
        }
    }

    private void setupAgents() {
        SimulationAgent[] agents = this.getAgents();
        if (agents != null) {
            int i = 0;
            while (i < agents.length) {
                SimulationAgent agent = agents[i];
                int participantID = agent.getParticipantID();
                if (participantID >= 0 && agent.isProxy()) {
                    AgentChannel channel = agent.getAgentChannel();
                    if (channel != null) {
                        this.setupAgentChannel(channel);
                    } else {
                        channel = this.admin.getAgentChannel(participantID);
                        if (channel != null) {
                            this.setupAgentChannel(channel);
                            agent.setAgentChannel(channel, false);
                        }
                    }
                }
                ++i;
            }
        }
    }

    private void setupAgentChannel(AgentChannel channel) {
        SimulationAgent[] agents = this.getAgents();
        if (agents != null) {
            int i = 0;
            while (i < agents.length) {
                channel.addTransportConstant(agents[i].getAddress());
                ++i;
            }
        }
    }

    final void prepareStop() {
        if (this.runtimeStatus == 1) {
            this.runtimeStatus = 2;
            SimulationInfo info = this.getSimulationInfo();
            log.fine("Simulation " + info.getSimulationID() + " is preparing to stop");
            SimulationAgent[] agents = this.getAgents();
            if (agents != null) {
                int i = 0;
                while (i < agents.length) {
                    try {
                        agents[i].stop();
                    }
                    catch (ThreadDeath e) {
                        log.log(Level.SEVERE, "could not stop agent " + agents[i].getName(), e);
                        throw e;
                    }
                    catch (Throwable e) {
                        log.log(Level.SEVERE, "could not stop agent " + agents[i].getName(), e);
                    }
                    ++i;
                }
            }
            try {
                this.prepareStopSimulation();
            }
            finally {
                this.dispatcher.stopDispatcher();
            }
        }
    }

    final void completeStop() {
        if (this.runtimeStatus == 2 || this.runtimeStatus == 1) {
            SimulationAgent[] agents;
            this.runtimeStatus = 3;
            SimulationInfo info = this.getSimulationInfo();
            log.fine("Simulation " + info.getSimulationID() + " is being stopped");
            if (this.tickTask != null) {
                this.tickTask.cancel();
                this.tickTask = null;
            }
            if (this.timer != null) {
                this.timer.cancel();
                this.timer = null;
            }
            if ((agents = this.getAgents()) != null) {
                int i = 0;
                while (i < agents.length) {
                    try {
                        agents[i].shutdown();
                    }
                    catch (ThreadDeath e) {
                        log.log(Level.SEVERE, "could not shutdown agent " + agents[i].getName(), e);
                        throw e;
                    }
                    catch (Throwable e) {
                        log.log(Level.SEVERE, "could not shutdown agent " + agents[i].getName(), e);
                    }
                    ++i;
                }
            }
            this.completeStopSimulation();
            this.clearTimeListeners();
            Simulation i = this;
            synchronized (i) {
                this.agentTable.clear();
                this.agentList = null;
                this.agentRoleNumber = 0;
                this.agentRoles = null;
                this.agentsPerRole = null;
            }
        }
    }

    final void close() {
        if (this.logWriter != null) {
            this.logWriter.close();
            this.eventWriter = this.rootEventWriter;
        }
    }

    final void agentChannelAvailable(AgentChannel channel) {
        SimulationAgent agent;
        if (this.runtimeStatus != 3 && (agent = this.getAgent(channel.getUserID())) != null && agent.isProxy()) {
            this.setupAgentChannel(channel);
            agent.setAgentChannel(channel, this.runtimeStatus == 1);
        }
    }

    final void requestAgentRecovery(SimulationAgent agent) {
        int mode = this.getAgentRecoverMode(agent);
        switch (mode) {
            case 0: {
                break;
            }
            case 1: {
                if (this.dispatcher == null) break;
                agent.setBlocked(true);
                this.dispatcher.callAgentRecovery(agent);
                break;
            }
            case 2: {
                agent.setBlocked(true);
                Simulation simulation = this;
                synchronized (simulation) {
                    if (ArrayUtils.indexOf(this.agentsToRecover, agent) < 0) {
                        this.agentsToRecover = (SimulationAgent[])ArrayUtils.add(SimulationAgent.class, this.agentsToRecover, agent);
                    }
                    this.hasAgentsToRecover = true;
                    break;
                }
            }
            default: {
                log.warning("unknown agent recovery mode " + mode + " for agent " + agent.getName());
            }
        }
    }

    final void callRecoverAgent(SimulationAgent agent) {
        try {
            if (agent.isBlocked()) {
                if (this.dispatcher == null) {
                    agent.setBlocked(false);
                } else {
                    this.dispatcher.callAgentUnblock(agent);
                }
            }
            this.recoverAgent(agent);
        }
        catch (ThreadDeath e) {
            log.log(Level.SEVERE, "could not recover agent " + agent.getName(), e);
            throw e;
        }
        catch (Throwable e) {
            log.log(Level.SEVERE, "could not recover agent " + agent.getName(), e);
        }
    }

    protected void requestStopSimulation() {
        log.finest("***** END OF SIMULATION REQUESTED *****");
        if (this.tickTask != null) {
            this.tickTask.cancel();
            this.tickTask = null;
        }
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
        this.clearTimeListeners();
        if (this.runtimeStatus == 1) {
            SimulationInfo info = this.getSimulationInfo();
            log.fine("Simulation " + info.getSimulationID() + " is requested to stop");
            this.dispatcher.callPrepareStop();
        }
    }

    public ConfigManager getConfig() {
        return this.config;
    }

    public long getServerTime() {
        return this.admin.getServerTime();
    }

    public SimulationInfo getSimulationInfo() {
        return this.info;
    }

    public Random getRandom() {
        return this.random;
    }

    public ThreadPool getSimulationThreadPool() {
        return this.simulationThreadPool;
    }

    public EventWriter getEventWriter() {
        if (this.eventWriter == null) {
            this.getLogWriter();
        }
        return this.eventWriter;
    }

    protected LogWriter getLogWriter() {
        if (this.logWriter == null) {
            Simulation simulation = this;
            synchronized (simulation) {
                if (this.logWriter == null) {
                    try {
                        FileOutputStream out = new FileOutputStream(this.logFileName);
                        this.eventWriter = this.logWriter = new LogWriter(this.rootEventWriter, out);
                    }
                    catch (Exception e) {
                        log.log(Level.SEVERE, "could not open simulation log for simulation " + this.info.getSimulationID(), e);
                    }
                    if (this.logWriter == null) {
                        this.eventWriter = this.logWriter = new LogWriter(this.rootEventWriter);
                    }
                    this.logFileName = null;
                }
            }
        }
        return this.logWriter;
    }

    public int agentIndex(String name) {
        return ((SimulationAgent)this.agentTable.get(name)).getIndex();
    }

    protected SimulationAgent getAgent(String name) {
        return (SimulationAgent)this.agentTable.get(name);
    }

    protected SimulationAgent getAgent(int participantID) {
        SimulationAgent[] agents = this.getAgents();
        int index = SimulationAgent.indexOf(agents, participantID);
        return index >= 0 ? agents[index] : null;
    }

    protected SimulationAgent[] getAgents() {
        SimulationAgent[] agents = this.agentList;
        if (agents == null) {
            Simulation simulation = this;
            synchronized (simulation) {
                agents = this.agentList;
                if (agents == null) {
                    agents = new SimulationAgent[this.agentTable.size()];
                    Enumeration e = this.agentTable.elements();
                    while (e.hasMoreElements()) {
                        SimulationAgent a;
                        agents[a.getIndex() - 1] = a = (SimulationAgent)e.nextElement();
                    }
                    this.agentList = agents;
                }
            }
        }
        return agents;
    }

    public SimulationAgent[] getAgents(int role) {
        if (role == 0 || role == 2) {
            return new SimulationAgent[0];
        }
        int index = ArrayUtils.indexOf(this.agentRoles, 0, this.agentRoleNumber, role);
        if (index < 0) {
            Simulation simulation = this;
            synchronized (simulation) {
                index = ArrayUtils.indexOf(this.agentRoles, 0, this.agentRoleNumber, role);
                if (index < 0) {
                    if (this.agentRoles == null) {
                        this.agentRoles = new int[5];
                        this.agentsPerRole = new SimulationAgent[5][];
                    } else if (this.agentRoleNumber == this.agentRoles.length) {
                        this.agentRoles = ArrayUtils.setSize(this.agentRoles, this.agentRoleNumber + 5);
                        this.agentsPerRole = (SimulationAgent[][])ArrayUtils.setSize((Object[])this.agentsPerRole, this.agentRoleNumber + 5);
                    }
                    ArrayList<SimulationAgent> list = new ArrayList<SimulationAgent>();
                    SimulationAgent[] agents = this.getAgents();
                    if (agents != null) {
                        int i = 0;
                        int n = agents.length;
                        while (i < n) {
                            SimulationAgent a = agents[i];
                            if (a != null && a.getRole() == role) {
                                list.add(a);
                            }
                            ++i;
                        }
                    }
                    index = this.agentRoleNumber;
                    this.agentsPerRole[this.agentRoleNumber] = list.size() > 0 ? list.toArray(new SimulationAgent[list.size()]) : null;
                    this.agentRoles[this.agentRoleNumber++] = role;
                }
            }
        }
        return this.agentsPerRole[index];
    }

    protected SimulationAgent registerAgent(Agent agent, String name, int role, int participantID) {
        int index;
        String userName;
        String agentName = name;
        if (participantID >= 0 && (userName = this.admin.getUserName(participantID)) != null) {
            agentName = userName;
        }
        SimulationAgent simAgent = new SimulationAgent(agent, agentName);
        Simulation simulation = this;
        synchronized (simulation) {
            index = this.agentTable.size() + 1;
            this.agentTable.put(name, simAgent);
            this.agentList = null;
            this.agentRoleNumber = 0;
        }
        simAgent.setup(this, index, name, role, participantID);
        return simAgent;
    }

    protected void invokeLater(Runnable target) {
        this.dispatcher.callRunnable(target);
    }

    protected void startTickTimer(long startServerTime, int millisPerTimeUnit) {
        if (millisPerTimeUnit <= 0) {
            throw new IllegalArgumentException("millisPerTimeUnit must be positive: " + millisPerTimeUnit);
        }
        if (this.runtimeStatus == 3) {
            throw new IllegalStateException("simulation has ended");
        }
        if (this.timer != null) {
            throw new IllegalStateException("timer already started");
        }
        this.timer = new Timer();
        this.tickTask = new TimerTask(){

            @Override
            public void run() {
                Simulation.this.performTick();
            }
        };
        long startTime = startServerTime - this.admin.getTimeDiff();
        this.timer.scheduleAtFixedRate(this.tickTask, new Date(startTime), (long)millisPerTimeUnit);
    }

    private void performTick() {
        int timeUnit = this.currentTimeUnit++;
        log.finest("***** START OF TIME " + timeUnit + " REQUESTED *****");
        this.dispatcher.callNextTimeUnit(timeUnit);
    }

    protected TimeListener[] getTimeListeners() {
        return this.timeListeners;
    }

    protected synchronized void addTimeListener(TimeListener listener) {
        this.timeListeners = (TimeListener[])ArrayUtils.add(TimeListener.class, this.timeListeners, listener);
    }

    protected synchronized void removeTimeListener(TimeListener listener) {
        this.timeListeners = (TimeListener[])ArrayUtils.remove(this.timeListeners, listener);
    }

    protected void clearTimeListeners() {
        this.timeListeners = null;
    }

    final void callNextTimeUnit(int timeUnit) {
        log.info("***** START OF TIME " + timeUnit + " *****");
        try {
            this.nextTimeUnitStarted(timeUnit);
        }
        catch (ThreadDeath e) {
            log.log(Level.SEVERE, "could not deliver time unit " + timeUnit + " to simulation", e);
            throw e;
        }
        catch (Throwable e) {
            log.log(Level.SEVERE, "could not deliver time unit " + timeUnit + " to simulation", e);
        }
        this.preNextTimeUnit(timeUnit);
        TimeListener[] listeners = this.getTimeListeners();
        if (listeners != null) {
            int i = 0;
            int n = listeners.length;
            while (i < n) {
                try {
                    listeners[i].nextTimeUnit(timeUnit);
                }
                catch (ThreadDeath e) {
                    log.log(Level.SEVERE, "could not deliver time unit " + timeUnit + " to " + listeners[i], e);
                    throw e;
                }
                catch (Throwable e) {
                    log.log(Level.SEVERE, "could not deliver time unit " + timeUnit + " to " + listeners[i], e);
                }
                ++i;
            }
        }
        try {
            this.nextTimeUnitFinished(timeUnit);
        }
        catch (ThreadDeath e) {
            log.log(Level.SEVERE, "could not deliver time unit " + timeUnit + " to simulation", e);
            throw e;
        }
        catch (Throwable e) {
            log.log(Level.SEVERE, "could not deliver time unit " + timeUnit + " to simulation", e);
        }
        log.info("***** START OF TIME " + timeUnit + " COMPLETE *****");
        if (this.hasAgentsToRecover) {
            SimulationAgent[] agents;
            Simulation n = this;
            synchronized (n) {
                agents = this.agentsToRecover;
                this.agentsToRecover = null;
                this.hasAgentsToRecover = false;
            }
            if (agents != null) {
                int i = 0;
                int n2 = agents.length;
                while (i < n2) {
                    this.dispatcher.callAgentRecovery(agents[i]);
                    ++i;
                }
            }
        }
    }

    protected abstract void preNextTimeUnit(int var1);

    protected abstract void setupSimulation() throws IllegalConfigurationException;

    protected String getTimeUnitName() {
        return null;
    }

    protected int getTimeUnitCount() {
        return 0;
    }

    protected abstract void startSimulation();

    protected abstract void prepareStopSimulation();

    protected abstract void completeStopSimulation();

    protected void nextTimeUnitStarted(int timeUnit) {
    }

    protected void nextTimeUnitFinished(int timeUnit) {
    }

    protected abstract int getAgentRecoverMode(SimulationAgent var1);

    protected abstract void recoverAgent(SimulationAgent var1);

    protected void sendMessage(String receiver, Transportable content) {
        this.sendMessage(new Message(receiver, content));
    }

    protected void sendMessage(Message message) {
        String sender = message.getSender();
        if (sender == null) {
            message.setSender("coordinator");
        } else if (!sender.equals("coordinator")) {
            throw new SecurityException("Can not send message from other than self");
        }
        this.deliverMessage(message);
    }

    protected void sendToRole(int role, Transportable content) {
        this.dispatcher.deliverToRole(null, role, content);
    }

    final void deliverMessage(Message message) {
        this.dispatcher.deliver(message);
    }

    final void deliverMessageToRole(SimulationAgent senderAgent, int role, Transportable content) {
        this.dispatcher.deliverToRole(senderAgent, role, content);
    }

    protected abstract boolean validateMessage(SimulationAgent var1, Message var2);

    protected abstract boolean validateMessageToRole(SimulationAgent var1, int var2, Transportable var3);

    protected abstract boolean validateMessageToRole(int var1, Transportable var2);

    protected abstract void messageReceived(Message var1);

    protected void createExternalAgent(String name, int role, int participantID) {
        ExternalAgent agent = new ExternalAgent();
        SimulationAgent simAgent = this.registerAgent(agent, name, role, participantID);
        simAgent.setProxy(true);
    }

    protected int createBuiltinAgents(String base, int role) throws IllegalConfigurationException {
        return this.createBuiltinAgents(base, role, null);
    }

    protected int createBuiltinAgents(String base, int role, Class baseClass) throws IllegalConfigurationException {
        int numberCreated = 0;
        String names = this.config.getProperty(String.valueOf(base) + ".names");
        if (names == null) {
            throw new IllegalConfigurationException("No specified " + base + " in config");
        }
        String name = null;
        String className = null;
        String defaultClassName = this.config.getProperty(String.valueOf(base) + ".class");
        try {
            StringTokenizer tok = new StringTokenizer(names, ", \t");
            while (tok.hasMoreTokens()) {
                name = tok.nextToken();
                className = this.config.getProperty(String.valueOf(base) + '.' + name + ".class", defaultClassName);
                if (className == null) {
                    throw new IllegalConfigurationException("No class definition for " + base + ' ' + name);
                }
                Agent agent = (Agent)Class.forName(className).newInstance();
                if (baseClass != null && !baseClass.isInstance(agent)) {
                    throw new ClassCastException(String.valueOf(className) + " is not an instance of " + baseClass);
                }
                this.registerAgent(agent, name, role, -1);
                ++numberCreated;
            }
        }
        catch (IllegalConfigurationException e) {
            throw e;
        }
        catch (ClassCastException e) {
            throw (IllegalConfigurationException)new IllegalConfigurationException(String.valueOf(name) + " of class " + className + " is not an object of type " + (baseClass != null ? baseClass.getName() : "Agent")).initCause(e);
        }
        catch (Exception e) {
            throw (IllegalConfigurationException)new IllegalConfigurationException("could not create agent " + name).initCause(e);
        }
        return numberCreated;
    }

    protected int createDummies(String base, int role, int numberOfAgents) throws IllegalConfigurationException {
        return this.createDummies(base, role, numberOfAgents, null);
    }

    protected int createDummies(String base, int role, int numberOfAgents, String namePrefix) throws IllegalConfigurationException {
        int numberCreated = 0;
        String names = this.config.getProperty(String.valueOf(base) + ".names");
        if (names == null) {
            throw new IllegalConfigurationException("No specified dummy " + base + " in config");
        }
        String name = null;
        String className = null;
        String defaultClassName = this.config.getProperty(String.valueOf(base) + ".class");
        try {
            StringTokenizer tok = new StringTokenizer(names, ", \t");
            int tokCount = tok.countTokens();
            String[] nameSplit = new String[tokCount * 2];
            int i = 0;
            int n = nameSplit.length;
            while (i < n) {
                name = tok.nextToken();
                className = this.config.getProperty(String.valueOf(base) + '.' + name + ".class", defaultClassName);
                if (className == null) {
                    throw new IllegalConfigurationException("No class definition for dummy " + base + ' ' + name);
                }
                nameSplit[i] = name;
                nameSplit[i + 1] = className;
                i += 2;
            }
            i = 0;
            int index = 0;
            int n2 = nameSplit.length;
            while (i < numberOfAgents) {
                className = nameSplit[index + 1];
                Agent dummyAgent = (Agent)Class.forName(className).newInstance();
                int agentID = --this.dummyAgentCounter;
                if (namePrefix == null) {
                    int nameIndex = 2;
                    String agentName = name = nameSplit[index];
                    while (this.getAgent(agentName) != null) {
                        agentName = String.valueOf(name) + '-' + nameIndex++;
                    }
                    this.registerAgent(dummyAgent, agentName, role, agentID);
                } else {
                    this.registerAgent(dummyAgent, String.valueOf(namePrefix) + (i + 1), role, agentID);
                }
                ++numberCreated;
                index = (index + 2) % n2;
                this.info.addParticipant(agentID, role);
                this.admin.simulationJoined(this.info, agentID, role);
                ++i;
            }
        }
        catch (IllegalConfigurationException e) {
            throw e;
        }
        catch (ClassCastException e) {
            throw (IllegalConfigurationException)new IllegalConfigurationException("dummy " + name + " of class " + className + " is not an agent").initCause(e);
        }
        catch (Exception e) {
            throw (IllegalConfigurationException)new IllegalConfigurationException("could not create agent " + name).initCause(e);
        }
        return numberCreated;
    }

    protected void finalize() throws Throwable {
        SimulationInfo info = this.info;
        if (info != null) {
            log.info("SIMULATION " + info.getSimulationID() + " IS BEING GARBAGED");
        } else {
            log.info("SIMULATION WITHOUT INFO IS BEING GARBAGED");
        }
        super.finalize();
    }

}

