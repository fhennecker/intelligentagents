/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.sim;

import com.botbox.util.ArrayQueue;
import com.botbox.util.ArrayUtils;
import com.botbox.util.ThreadPool;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.isl.inet.InetServer;
import se.sics.isl.transport.Transportable;
import se.sics.isl.util.ConfigManager;
import se.sics.isl.util.IllegalConfigurationException;
import se.sics.isl.util.LogFormatter;
import se.sics.tasim.aw.Message;
import se.sics.tasim.is.AgentLookup;
import se.sics.tasim.is.CompetitionSchedule;
import se.sics.tasim.is.EventWriter;
import se.sics.tasim.is.InfoConnection;
import se.sics.tasim.is.SimConnection;
import se.sics.tasim.is.SimulationInfo;
import se.sics.tasim.props.AdminContent;
import se.sics.tasim.props.Alert;
import se.sics.tasim.sim.AgentChannel;
import se.sics.tasim.sim.ISClient;
import se.sics.tasim.sim.NoSuchManagerException;
import se.sics.tasim.sim.SimConnectionImpl;
import se.sics.tasim.sim.Simulation;
import se.sics.tasim.sim.SimulationManager;
import se.sics.tasim.viewer.ViewerConnection;

public final class Admin {
    private static final Logger log = Logger.getLogger(Admin.class.getName());
    public static final String SERVER_VERSION = "0.8.19";
    public static final String CONF = "sim.";
    public static final String ADMIN = "admin";
    private static final int SIM_DELAY = 120000;
    private static final int MAX_TIME_BEFORE_LOCK = 240000;
    private static final String SIM_FILE_PREFIX = "sim";
    private static final String SIM_FILE_SUFFIX = ".slg";
    private final ConfigManager config;
    private final AgentLookup lookup;
    private final SimConnectionImpl simConnection;
    private final InfoConnection infoConnection;
    private final ISClient isClient;
    private final EventWriter eventWriter;
    private final ViewerConnection viewerConnection;
    private final boolean allowUnregisteredAgents;
    private final int gameStartOffset;
    private final String defaultSimulationType;
    private final ThreadPool simulationThreadPool;
    private final Hashtable simulationManagerTable = new Hashtable();
    private final Hashtable channelTable = new Hashtable();
    private AgentChannel[] channelList;
    private int channelNumber = 0;
    private Simulation currentSimulation;
    private final ArrayQueue simQueue = new ArrayQueue();
    private long timeDiff = 0;
    private final LogFormatter formatter;
    private FileHandler rootFileHandler;
    private String serverName;
    private final String configDirectory;
    private final String runAfterSimulation;
    private final String logName;
    private final String logPrefix;
    private final String simPrefix;
    private FileHandler simLogHandler;
    private String simLogName;
    private int startDelay;
    private boolean allowEmptySimulations = false;
    private int maxgames = -1;
    private int startsimid = -1;
    private int lastSimulationID = 0;
    private int lastUniqueSimulationID = 0;
    private final Timer timer = new Timer();

    public Admin(ConfigManager config) throws IllegalConfigurationException, IOException {
        this.config = config;
        this.serverName = config.getProperty("sim.server.name", config.getProperty("server.name"));
        if (this.serverName == null) {
            this.serverName = this.generateServerName();
        }
        this.defaultSimulationType = config.getProperty("sim.simulation.defaultType", config.getProperty("simulation.defaultType", "tac13adx"));
        this.runAfterSimulation = config.getProperty("sim.runAfterSimulation");
        this.configDirectory = config.getProperty("sim.config.directory", "config");
        this.logName = this.getLogDirectory("sim.", "log.directory", this.serverName);
        this.logPrefix = this.getLogDirectory("sim.", "log.simlogs", this.serverName);
        this.simPrefix = this.getLogDirectory("sim.", "log.sims", String.valueOf(this.serverName) + '_' + "sim");
        this.allowEmptySimulations = config.getPropertyAsBoolean("sim.allowEmptySimulations", false);
        this.allowUnregisteredAgents = config.getPropertyAsBoolean("sim.allowUnregisteredAgents", false);
        this.gameStartOffset = config.getPropertyAsInt("sim.gameStartOffset", 0) * 1000;
        this.startDelay = config.getPropertyAsInt("sim.startDelay", 60) * 1000;
        this.startDelay = this.startDelay <= 0 ? 0 : (this.startDelay + 59000) / 60000 * 60000;
        this.startsimid = config.getPropertyAsInt("sim.sim.startsimid", config.getPropertyAsInt("sim.startsimid", 0));
        this.maxgames = config.getPropertyAsInt("sim.sim.gamestorun", config.getPropertyAsInt("sim.gamestorun", -1));
        int i = 0;
        int n = this.serverName.length();
        while (i < n) {
            char c = this.serverName.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '.' && c != '_') {
                throw new IllegalConfigurationException("server name may only contain letters and digits: " + this.serverName);
            }
            ++i;
        }
        this.formatter = new LogFormatter();
        this.formatter.setAliasLevel(2);
        LogFormatter.setFormatterForAllHandlers(this.formatter);
        this.setLogging(config);
        String infoClass = config.getProperty("sim.ic.class");
        if (infoClass == null) {
            throw new IllegalConfigurationException("no InfoConnection specified");
        }
        try {
            this.infoConnection = (InfoConnection)Class.forName(infoClass).newInstance();
        }
        catch (Exception e) {
            throw (IllegalConfigurationException)new IllegalConfigurationException("could not create info connection of type " + infoClass).initCause(e);
        }
        this.infoConnection.init(config);
        this.simulationThreadPool = ThreadPool.getThreadPool("simpool");
        this.simulationThreadPool.setMinThreads(6);
        this.simulationThreadPool.setMaxThreads(40);
        this.simulationThreadPool.setMaxIdleThreads(15);
        this.simulationThreadPool.setInterruptThreadsAfter(120000);
        this.lookup = new AgentLookup();
        this.simConnection = new SimConnectionImpl(this, this.lookup);
        if (config.getPropertyAsBoolean("sim.gui", false)) {
            this.isClient = new ISClient(this, this.infoConnection);
            this.eventWriter = this.isClient.getEventWriter();
            this.viewerConnection = this.isClient.getViewerConnection();
        } else {
            this.isClient = null;
            this.eventWriter = this.infoConnection;
            this.viewerConnection = null;
        }
        this.infoConnection.setSimConnection(this.simConnection);
        this.simConnection.setInfoConnection(this.infoConnection);
        if (this.isClient != null) {
            this.isClient.start();
        }
        this.infoConnection.auth(this.serverName, "password", "0.8.19");
        long currentTime = System.currentTimeMillis();
        long nextTime = currentTime / 60000 * 60000 + 60000 + (long)this.gameStartOffset;
        long delay = nextTime - currentTime;
        this.timer.scheduleAtFixedRate(new TimerTask(){

            @Override
            public void run() {
                try {
                    Admin.this.checkSimulation();
                }
                catch (ThreadDeath e) {
                    log.log(Level.SEVERE, "could not handle timeout", e);
                    throw e;
                }
                catch (Throwable e) {
                    log.log(Level.SEVERE, "could not handle timeout", e);
                }
            }
        }, delay, 60000);
    }

    private String generateServerName() {
        return InetServer.getLocalHostName();
    }

    private String getLogDirectory(String base, String property, String name) throws IOException {
        String logDirectory = this.config.getProperty(String.valueOf(base) + property);
        if (logDirectory == null) {
            logDirectory = this.config.getProperty(property);
        }
        if (logDirectory != null) {
            File fp = new File(logDirectory);
            if (!fp.exists() && !fp.mkdirs() || !fp.isDirectory()) {
                throw new IOException("could not create directory '" + logDirectory + '\'');
            }
            return name == null ? fp.getAbsolutePath() : String.valueOf(fp.getAbsolutePath()) + File.separatorChar + name;
        }
        return name;
    }

    private long roundTimeToMinute(long time) {
        long delta = time % 60000;
        if (delta > 0) {
            time += 60000 - delta;
        }
        return time;
    }

    public ConfigManager getConfig() {
        return this.config;
    }

    public String getServerName() {
        return this.serverName;
    }

    public String getConfigDirectory() {
        return this.configDirectory;
    }

    public EventWriter getEventWriter() {
        return this.eventWriter;
    }

    public long getServerTime() {
        return System.currentTimeMillis() + this.timeDiff;
    }

    public long getServerTimeSeconds() {
        return (System.currentTimeMillis() + this.timeDiff) / 1000;
    }

    public void setServerTime(long serverTime) {
        this.timeDiff = serverTime - System.currentTimeMillis();
        this.formatter.setLogTime(serverTime);
        if (this.viewerConnection != null) {
            this.viewerConnection.setServerTime(serverTime);
        }
    }

    long getTimeDiff() {
        return this.timeDiff;
    }

    public String getUserName(int userID) {
        AgentChannel channel = this.getAgentChannel(userID);
        if (channel != null) {
            return channel.getName();
        }
        return this.lookup.getAgentName(userID);
    }

    synchronized int getNextUniqueSimulationID() {
        if (this.startsimid - 1 > this.lastUniqueSimulationID) {
            this.lastUniqueSimulationID = this.startsimid - 1;
        }
        return ++this.lastUniqueSimulationID;
    }

    private synchronized int getNextSimulationID() {
        if (this.startsimid - 1 > this.lastSimulationID) {
            this.lastSimulationID = this.startsimid - 1;
        }
        return ++this.lastSimulationID;
    }

    public synchronized SimulationInfo nextSimulation(int userID, String simTypePattern) {
        int i = 0;
        int n = this.simQueue.size();
        while (i < n) {
            SimulationInfo simulation = (SimulationInfo)this.simQueue.get(i);
            if (!simulation.hasSimulationID()) break;
            if (simulation.isParticipant(userID) && (simTypePattern == null || this.matchSimulationType(simTypePattern, simulation.getType()))) {
                return simulation;
            }
            ++i;
        }
        return null;
    }

    private boolean matchSimulationType(String simTypePattern, String simulationType) {
        int len;
        int n = len = simTypePattern != null ? simTypePattern.length() : 0;
        if (len == 0) {
            return false;
        }
        if (simTypePattern.charAt(len - 1) == '*') {
            if (simulationType != null && simulationType.regionMatches(0, simTypePattern, 0, len - 1)) {
                return true;
            }
            return false;
        }
        return simTypePattern.equals(simulationType);
    }

    public synchronized SimulationInfo joinSimulation(int userID, String simType, String simParams, String simRoleName) throws NoSuchManagerException {
        if (simType == null) {
            simType = this.defaultSimulationType;
        }
        SimulationManager manager = this.getSimulationManager(simType);
        int simulationLength = manager.getSimulationLength(simType, simParams) + 120000;
        int simRole = manager.getSimulationRoleID(simType, simRoleName);
        long time = (this.getServerTime() + (long)this.startDelay + 59000) / 60000 * 60000;
        if (time < this.getServerTime() + 2000) {
            time += 60000;
        }
        int i = 0;
        int n = this.simQueue.size();
        while (i < n) {
            SimulationInfo simulation = (SimulationInfo)this.simQueue.get(i);
            int gid = simulation.getSimulationID();
            long startTime = simulation.getStartTime();
            if (gid >= 0) {
                if (simType.equals(simulation.getType())) {
                    if (simulation.isParticipant(userID)) {
                        return simulation;
                    }
                    if (manager.join(userID, simRole, simulation)) {
                        this.simulationJoined(simulation, userID);
                        return simulation;
                    }
                }
                time = this.roundTimeToMinute(simulation.getEndTime() + 120000);
            } else {
                if (startTime - time > (long)simulationLength) {
                    simulation = this.createSimulationInfo(manager, simType, simParams);
                    simulation.setStartTime(time + (long)this.gameStartOffset);
                    if (manager.join(userID, simRole, simulation)) {
                        this.simQueue.add(i, simulation);
                        this.simulationCreated(simulation);
                        return simulation;
                    }
                    return null;
                }
                if (!simulation.isReservation() && simType.equals(simulation.getType())) {
                    if (simulation.isParticipant(userID)) {
                        return simulation;
                    }
                    if (manager.join(userID, simRole, simulation)) {
                        this.simulationJoined(simulation, userID);
                        return simulation;
                    }
                }
                time = this.roundTimeToMinute(simulation.getEndTime() + 120000);
            }
            ++i;
        }
        SimulationInfo simulation = this.createSimulationInfo(manager, simType, simParams);
        simulation.setStartTime(time + (long)this.gameStartOffset);
        if (manager.join(userID, simRole, simulation)) {
            this.simQueue.add(simulation);
            this.simulationCreated(simulation);
            return simulation;
        }
        return null;
    }

    private void simulationCreated(SimulationInfo info) {
        log.info("created simulation " + info.getID());
        this.infoConnection.simulationCreated(info);
    }

    private void simulationCreated(SimulationInfo info, int competitionID) {
        log.info("created simulation " + info.getID() + " for competition " + competitionID);
        this.infoConnection.simulationCreated(info, competitionID);
    }

    private void simulationRemoved(SimulationInfo info, String message) {
        this.infoConnection.simulationRemoved(info.getID(), message);
    }

    private void simulationJoined(SimulationInfo info, int userID) {
        int index = info.indexOfParticipant(userID);
        if (index >= 0) {
            this.simulationJoined(info, userID, info.getParticipantRole(index));
        }
    }

    final void simulationJoined(SimulationInfo info, int userID, int role) {
        this.infoConnection.simulationJoined(info.getID(), userID, role);
    }

    private void simulationLocked(SimulationInfo info) {
        int sid = info.getSimulationID();
        if (sid >= 0) {
            this.infoConnection.simulationLocked(info.getID(), sid);
        }
    }

    private void simulationStarted(Simulation simulation) {
        SimulationInfo info = simulation.getSimulationInfo();
        String timeUnitName = simulation.getTimeUnitName();
        int timeUnitCount = simulation.getTimeUnitCount();
        if (this.viewerConnection != null) {
            this.viewerConnection.simulationStarted(info.getSimulationID(), info.getType(), info.getStartTime(), info.getEndTime(), timeUnitName, timeUnitCount);
        }
        this.infoConnection.simulationStarted(info.getID(), timeUnitName, timeUnitCount);
    }

    private void simulationStopped(Simulation simulation, boolean error) {
        SimulationInfo info = simulation.getSimulationInfo();
        if (this.viewerConnection != null) {
            this.viewerConnection.simulationStopped(info.getSimulationID());
        }
        this.infoConnection.simulationStopped(info.getID(), info.getSimulationID(), error);
    }

    void sendStateToInfoSystem() {
        this.infoConnection.dataUpdated(3, this.lastSimulationID);
        this.infoConnection.dataUpdated(2, this.lastUniqueSimulationID);
        if (this.simQueue.size() > 0) {
            SimulationInfo[] sims;
            Admin admin = this;
            synchronized (admin) {
                sims = (SimulationInfo[])this.simQueue.toArray(new SimulationInfo[this.simQueue.size()]);
            }
            if (sims != null) {
                int i = 0;
                int n = sims.length;
                while (i < n) {
                    this.infoConnection.simulationCreated(sims[i]);
                    ++i;
                }
            }
        }
    }

    void dataUpdated(int type, int value) {
        if (type != 1) {
            if (type == 2) {
                if (value > this.lastUniqueSimulationID) {
                    this.lastUniqueSimulationID = value;
                }
            } else if (type == 3 && value > this.lastSimulationID) {
                this.lastSimulationID = value;
            }
        }
    }

    synchronized boolean addSimulation(SimulationInfo info) {
        long startTime = info.getStartTime();
        long currentTime = this.getServerTime();
        if (currentTime >= startTime) {
            Simulation sim = this.currentSimulation;
            if (sim == null || !info.equals(sim.getSimulationInfo())) {
                this.simulationRemoved(info, null);
            }
            return false;
        }
        int index = 0;
        int ugid = info.getID();
        int n = this.simQueue.size();
        while (index < n) {
            SimulationInfo sim = (SimulationInfo)this.simQueue.get(index);
            if (sim.getID() == ugid) {
                sim.copyParticipants(info);
                return false;
            }
            if (sim.getStartTime() > startTime) break;
            ++index;
        }
        this.simQueue.add(index, info);
        log.finer("Added simulation " + info.getID() + " (" + info.getSimulationID() + ')');
        return true;
    }

    void resultsGenerated(int simulationID) {
        String targetName;
        String simFileName = "sim" + simulationID + ".slg";
        File sourceFile = new File(simFileName);
        if (!sourceFile.renameTo(new File(targetName = String.valueOf(this.simPrefix) + simulationID + ".slg"))) {
            log.warning("could not use simple rename to move simulation log " + simulationID + " from " + simFileName + " to " + targetName);
            try {
                int n;
                FileInputStream input = new FileInputStream(simFileName);
                FileOutputStream fout = new FileOutputStream(targetName);
                byte[] buffer = new byte[4096];
                while ((n = input.read(buffer)) > 0) {
                    fout.write(buffer, 0, n);
                }
                fout.close();
                input.close();
                if (!sourceFile.delete()) {
                    log.warning("could not remove old simulation file " + simFileName);
                }
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "could not move " + simFileName, e);
            }
        }
        if (this.runAfterSimulation != null) {
            final String simulationIDAsString = "" + simulationID;
            new Thread(new Runnable(){

                @Override
                public void run() {
                    try {
                        String command = Admin.this.format(Admin.this.runAfterSimulation, "sg", new String[]{Admin.this.serverName, simulationIDAsString});
                        if (command != null) {
                            log.fine("running '" + command + '\'');
                            Runtime.getRuntime().exec(command);
                        }
                    }
                    catch (Throwable e) {
                        log.log(Level.SEVERE, "could not run '" + Admin.this.runAfterSimulation + "' after simulation " + simulationIDAsString, e);
                    }
                }
            }, "afterSim" + simulationIDAsString).start();
        }
    }

    private String format(String format, String fnames, String[] data) {
        if (format == null) {
            return null;
        }
        int flen = format.length();
        StringBuffer sb = new StringBuffer();
        int i = 0;
        while (i < flen) {
            char c = format.charAt(i);
            if (c == '\\' && i + 1 < flen) {
                sb.append(format.charAt(++i));
            } else if (c == '%' && i + 1 < flen) {
                char c2 = format.charAt(i + 1);
                int index = fnames.indexOf(c2);
                if (index >= 0) {
                    sb.append(data[index]);
                    ++i;
                } else {
                    sb.append('%');
                }
            } else {
                sb.append(c);
            }
            ++i;
        }
        return sb.toString();
    }

    void sendChatMessage(String message) {
        this.infoConnection.sendChatMessage(this.getServerTime(), message);
    }

    void addChatMessage(long time, String serverName, String userName, String message) {
        if (this.isClient != null) {
            this.isClient.addChatMessage(time, serverName, userName, message);
        }
    }

    synchronized void scheduleCompetition(CompetitionSchedule schedule, boolean notifyInfoServer) {
        block21 : {
            int competitionID = schedule.getID();
            try {
                long startTime = schedule.getStartTime();
                int count = schedule.getSimulationCount();
                int startIndex = this.getInsertionIndex(startTime);
                if (startIndex < 0) {
                    if (notifyInfoServer) {
                        this.infoConnection.requestFailed(10, competitionID, "conflicting locked game");
                    }
                } else if (count == 0) {
                    if (notifyInfoServer) {
                        this.infoConnection.requestSuccessful(10, competitionID);
                    }
                } else {
                    String simType = schedule.getSimulationType();
                    String simParams = schedule.getSimulationParams();
                    if (simType == null) {
                        simType = this.defaultSimulationType;
                    }
                    SimulationManager manager = this.getSimulationManager(simType);
                    SimulationInfo[] simulations = new SimulationInfo[count];
                    boolean isSimulationsClosed = schedule.isSimulationsClosed();
                    int i = 0;
                    while (i < count) {
                        SimulationInfo info;
                        int[] participants = schedule.getParticipants(i);
                        int[] roles = schedule.getRoles(i);
                        simulations[i] = info = this.createSimulationInfo(manager, simType, simParams);
                        if (participants != null) {
                            int roleLen = roles == null ? 0 : roles.length;
                            int j = 0;
                            int m = participants.length;
                            while (j < m) {
                                int role;
                                int n = role = j < roleLen ? roles[j] : 0;
                                if (!manager.join(participants[j], role, info)) {
                                    throw new IllegalStateException("could not join participant " + participants[j] + " as " + role + " in simulation " + i + " for competition " + competitionID);
                                }
                                ++j;
                            }
                        }
                        if (isSimulationsClosed) {
                            info.setFull();
                        }
                        ++i;
                    }
                    int timeBetween = schedule.getTimeBetweenSimulations();
                    if (timeBetween < 120000) {
                        timeBetween = 120000;
                    }
                    int reserveIndex = 0;
                    long nextReservationTime = schedule.getReservationCount() > 0 ? schedule.getReservationStartTime(0) : Long.MAX_VALUE;
                    long nextTime = startTime;
                    long nextExisting = startIndex < this.simQueue.size() ? ((SimulationInfo)this.simQueue.get(startIndex)).getStartTime() : Long.MAX_VALUE;
                    int gamesCounter = 0;
                    int gamesBetweenReservations = schedule.getSimulationsBeforeReservation();
                    int reservationLength = schedule.getSimulationsReservationLength();
                    if (gamesBetweenReservations <= 0 || reservationLength < 60000) {
                        gamesBetweenReservations = Integer.MAX_VALUE;
                    }
                    int i2 = 0;
                    while (i2 < count) {
                        SimulationInfo info = simulations[i2];
                        int length = info.getSimulationLength();
                        long endTime = nextTime + (long)length;
                        if (endTime > nextReservationTime) {
                            int reserveLength = schedule.getReservationLength(reserveIndex);
                            SimulationInfo reservedInfo = this.createTimeReservation(nextReservationTime, reserveLength);
                            this.simQueue.add(startIndex++, reservedInfo);
                            this.simulationCreated(reservedInfo);
                            nextReservationTime = ++reserveIndex < schedule.getReservationCount() ? schedule.getReservationStartTime(reserveIndex) : Long.MAX_VALUE;
                            nextTime = this.roundTimeToMinute(reservedInfo.getEndTime() + (long)timeBetween);
                            endTime = nextTime + (long)length;
                        }
                        endTime += (long)timeBetween;
                        if (gamesCounter++ == gamesBetweenReservations) {
                            SimulationInfo reservedInfo = this.createTimeReservation(nextTime, reservationLength);
                            this.simQueue.add(startIndex++, reservedInfo);
                            this.simulationCreated(reservedInfo);
                            nextTime = this.roundTimeToMinute(reservedInfo.getEndTime() + (long)timeBetween);
                            endTime = nextTime + (long)length + (long)timeBetween;
                            gamesCounter = 1;
                        }
                        while (endTime > nextExisting) {
                            this.simulationRemoved((SimulationInfo)this.simQueue.remove(startIndex), "by competition");
                            long l = nextExisting = startIndex < this.simQueue.size() ? ((SimulationInfo)this.simQueue.get(startIndex)).getStartTime() : Long.MAX_VALUE;
                        }
                        info.setStartTime(nextTime + (long)this.gameStartOffset);
                        this.simQueue.add(startIndex++, info);
                        this.simulationCreated(info, competitionID);
                        nextTime = endTime;
                        ++i2;
                    }
                    if (notifyInfoServer) {
                        this.infoConnection.requestSuccessful(10, competitionID);
                    }
                }
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "could not schedule competition " + competitionID, e);
                if (!notifyInfoServer) break block21;
                this.infoConnection.requestFailed(10, competitionID, e.getMessage());
            }
        }
    }

    private int getInsertionIndex(long startTime) {
        int index = this.simQueue.size() - 1;
        while (index >= 0) {
            SimulationInfo simulation = (SimulationInfo)this.simQueue.get(index);
            long simEnd = simulation.getEndTime() + 120000;
            if (simEnd <= startTime) {
                return index + 1;
            }
            if (simulation.hasSimulationID()) {
                return -1;
            }
            --index;
        }
        return 0;
    }

    synchronized void lockNextSimulations(int simulationCount) {
        int i = 0;
        int len = this.simQueue.size();
        int n = len < simulationCount ? len : simulationCount;
        while (i < n) {
            SimulationInfo simulation = (SimulationInfo)this.simQueue.get(i);
            if (!simulation.hasSimulationID() && !this.assignSimulationIDs(simulation)) break;
            ++i;
        }
    }

    synchronized void addTimeReservation(long startTime, int lengthInMillis, boolean notifyInfoServer) {
        long endTime = startTime + (long)lengthInMillis + 120000;
        int index = this.simQueue.size() - 1;
        while (index >= 0) {
            SimulationInfo simulation = (SimulationInfo)this.simQueue.get(index);
            long simEnd = simulation.getEndTime();
            if (simEnd <= startTime) break;
            if (simulation.getStartTime() < endTime && notifyInfoServer) {
                this.infoConnection.requestFailed(4, 0, "conflicting simulation " + simulation.getID());
                startTime = -1;
            }
            --index;
        }
        if (startTime > 0) {
            SimulationInfo reservedInfo = this.createTimeReservation(startTime, lengthInMillis);
            this.simQueue.add(index + 1, reservedInfo);
            this.simulationCreated(reservedInfo);
            if (notifyInfoServer) {
                this.infoConnection.requestSuccessful(4, reservedInfo.getID());
            }
        }
    }

    synchronized boolean createSimulation(String simType, String simParams, boolean notifyInfoServer) {
        block11 : {
            if (simType == null) {
                simType = this.defaultSimulationType;
            }
            log.finer("the maxgames value is: " + this.maxgames + " ?");
            if (this.maxgames != 0) break block11;
            log.finer("Maxgames exceeded, not adding game");
            return false;
        }
        try {
            if (this.maxgames > 0) {
                --this.maxgames;
                log.finer("added game, " + this.maxgames + " remaining in this run");
            }
            SimulationManager manager = this.getSimulationManager(simType);
            SimulationInfo newSimulation = this.createSimulationInfo(manager, simType, simParams);
            int simulationLength = newSimulation.getSimulationLength() + 120000;
            int index = 0;
            long time = (this.getServerTime() + (long)this.startDelay + 59000) / 60000 * 60000;
            if (time < this.getServerTime() + 2000) {
                time += 60000;
            }
            int n = this.simQueue.size();
            while (index < n) {
                SimulationInfo simulation = (SimulationInfo)this.simQueue.get(index);
                long startTime = simulation.getStartTime();
                if (simulation.hasSimulationID()) {
                    time = this.roundTimeToMinute(simulation.getEndTime() + 120000);
                } else {
                    if (startTime - time > (long)simulationLength) break;
                    time = this.roundTimeToMinute(simulation.getEndTime() + 120000);
                }
                ++index;
            }
            newSimulation.setStartTime(time + (long)this.gameStartOffset);
            this.simQueue.add(index, newSimulation);
            this.simulationCreated(newSimulation);
            if (notifyInfoServer) {
                this.infoConnection.requestSuccessful(1, newSimulation.getID());
            }
            return true;
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "could not create simulation of type '" + simType + "' (" + simParams + ')', e);
            if (notifyInfoServer) {
                this.infoConnection.requestFailed(1, 0, e.getMessage());
            }
            return false;
        }
    }

    synchronized void removeSimulation(int simulationUniqID) {
        int i = this.simQueue.size() - 1;
        while (i >= 0) {
            SimulationInfo sim = (SimulationInfo)this.simQueue.get(i);
            if (sim.hasSimulationID()) break;
            if (sim.getID() == simulationUniqID) {
                this.simQueue.remove(i);
                this.simulationRemoved(sim, "by request");
                break;
            }
            --i;
        }
    }

    synchronized void joinSimulation(int simulationUniqID, int agentID, String simRoleName) throws NoSuchManagerException {
        String simType;
        int simRole;
        SimulationManager manager;
        SimulationInfo simulation = this.getSimulationInfo(simulationUniqID);
        if (simulation != null && !simulation.isParticipant(agentID) && !simulation.isReservation() && (manager = this.getSimulationManager(simType = simulation.getType())).join(agentID, simRole = manager.getSimulationRoleID(simType, simRoleName), simulation)) {
            this.simulationJoined(simulation, agentID);
        }
    }

    synchronized void joinSimulation(int simulationUniqID, int agentID, int simRole) throws NoSuchManagerException {
        String simType;
        SimulationManager manager;
        SimulationInfo simulation = this.getSimulationInfo(simulationUniqID);
        if (simulation != null && !simulation.isParticipant(agentID) && !simulation.isReservation() && (manager = this.getSimulationManager(simType = simulation.getType())).join(agentID, simRole, simulation)) {
            this.simulationJoined(simulation, agentID);
        }
    }

    private synchronized SimulationInfo getSimulationInfo(int uid) {
        int i = 0;
        int n = this.simQueue.size();
        while (i < n) {
            SimulationInfo sim = (SimulationInfo)this.simQueue.get(i);
            if (sim.getID() == uid) {
                return sim;
            }
            ++i;
        }
        return null;
    }

    private synchronized SimulationInfo getFirstSimulation() {
        return this.simQueue.size() > 0 ? (SimulationInfo)this.simQueue.get(0) : null;
    }

    private synchronized void removeFirstSimulation(SimulationInfo info, String message) {
        if (this.simQueue.size() > 0 && this.simQueue.get(0) == info) {
            this.simQueue.remove(0);
            this.simulationRemoved(info, message);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void checkSimulation() {
        long startTime;
        SimulationInfo info = this.getFirstSimulation();
        if (info == null) return;
        long currentTime = this.getServerTime();
        if (currentTime >= (startTime = info.getStartTime())) {
            Simulation simulation = this.currentSimulation;
            if (simulation != null) {
                if (currentTime <= info.getEndTime() + 1000) return;
                log.warning("requesting overdue simulation " + simulation.getSimulationInfo().getSimulationID() + " to end");
                simulation.requestStopSimulation();
                return;
            } else if (info.isReservation()) {
                if (currentTime < info.getEndTime()) return;
                this.removeFirstSimulation(info, null);
                return;
            } else if (!info.hasSimulationID() && !this.assignSimulationIDs(info)) {
                log.info("scratching started simulation without simulation id");
                this.removeFirstSimulation(info, null);
                return;
            } else if (info.isEmpty() && !this.allowEmptySimulations) {
                log.info("scratching simulation " + info.getSimulationID() + " without participants");
                this.removeFirstSimulation(info, null);
                return;
            } else if (currentTime >= startTime + 30000) {
                log.severe("scratching simulation " + info.getSimulationID() + " because it was started too late!!!");
                this.removeFirstSimulation(info, "delayed start");
                return;
            } else {
                try {
                    SimulationManager manager = this.getSimulationManager(info.getType());
                    String simLogFile = "sim" + info.getSimulationID() + ".slg";
                    int inError = 0;
                    simulation = manager.createSimulation(info);
                    this.enterSimulationLog(info.getSimulationID());
                    try {
                        try {
                            simulation.init(this, info, simLogFile, this.simulationThreadPool);
                            simulation.setup();
                            inError = 1;
                            this.simulationStarted(simulation);
                            simulation.start();
                            this.currentSimulation = simulation;
                            inError = 2;
                            return;
                        }
                        catch (Exception e) {
                            boolean i = false;
                            log.log(Level.SEVERE, "could not start simulation " + info.getSimulationID(), e);
                            if (inError == 2) return;
                            simulation.close();
                            if (inError == 1) {
                                this.simulationStopped(simulation, true);
                            }
                            this.exitSimulationLog();
                            new File(simLogFile).renameTo(new File("ERROR_" + simLogFile));
                        }
                        return;
                    }
                    finally {
                        if (inError != 2) {
                            simulation.close();
                            if (inError == 1) {
                                this.simulationStopped(simulation, true);
                            }
                            this.exitSimulationLog();
                            new File(simLogFile).renameTo(new File("ERROR_" + simLogFile));
                        }
                    }
                }
                catch (Exception e) {
                    log.log(Level.SEVERE, "could not start simulation " + info.getSimulationID(), e);
                    this.removeFirstSimulation(info, "setup failed");
                    return;
                }
            }
        } else {
            if (startTime - currentTime >= 240000 || info.hasSimulationID() || info.isReservation()) return;
            this.assignSimulationIDs(info);
        }
    }

    private boolean assignSimulationIDs(SimulationInfo info) {
        if (info.isReservation()) {
            return true;
        }
        if (!info.isEmpty() || this.allowEmptySimulations) {
            info.setSimulationID(this.getNextSimulationID());
            this.simulationLocked(info);
            return true;
        }
        return false;
    }

    final void stopSimulation(Simulation simulation) {
        SimulationInfo info = simulation.getSimulationInfo();
        Admin admin = this;
        synchronized (admin) {
            if (this.simQueue.size() > 0 && this.simQueue.get(0) == info) {
                this.simQueue.remove(0);
            }
        }
        if (simulation == this.currentSimulation) {
            this.currentSimulation = null;
        }
        try {
            try {
                simulation.completeStop();
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "could not stop simulation " + info.getSimulationID(), e);
                simulation.close();
                this.simulationStopped(simulation, false);
                this.exitSimulationLog();
            }
        }
        finally {
            simulation.close();
            this.simulationStopped(simulation, false);
            this.exitSimulationLog();
        }
    }

    final int loginAgentChannel(AgentChannel agent, String password) {
        String name = agent.getName();
        int userID = this.lookup.getAgentID(name);
        if (userID == -1) {
            this.infoConnection.checkUser(name);
            userID = this.lookup.getAgentID(name);
            if (userID == -1) {
                if (this.allowUnregisteredAgents) {
                    userID = this.infoConnection.addUser(name, password, "autocreated");
                }
                if (userID == -1) {
                    throw new IllegalArgumentException("No user with name '" + name + "' found");
                }
            }
        }
        if (!this.lookup.validateAgent(userID, password)) {
            throw new IllegalArgumentException("Password incorrect");
        }
        return userID;
    }

    final void agentChannelAvailable(AgentChannel agent) {
        Simulation sim;
        AgentChannel oldChannel;
        String name = agent.getName();
        int userID = agent.getUserID();
        Admin admin = this;
        synchronized (admin) {
            int index = AgentChannel.indexOf(this.channelList, 0, this.channelNumber, userID);
            if (index >= 0) {
                this.channelList[index] = agent;
            } else {
                if (this.channelList == null) {
                    this.channelList = new AgentChannel[8];
                } else if (this.channelNumber == this.channelList.length) {
                    this.channelList = (AgentChannel[])ArrayUtils.setSize(this.channelList, this.channelNumber + 8);
                }
                this.channelList[this.channelNumber++] = agent;
            }
            oldChannel = (AgentChannel)this.channelTable.get(name);
            this.channelTable.put(name, agent);
        }
        if (oldChannel != null) {
            log.warning("closing old channel " + oldChannel.getName() + " due to new connection " + agent.getName());
            String messageText = "The server connection was closed due to new connection\nfrom agent " + name + '@' + oldChannel.getRemoteHost() + '.';
            Alert alert = new Alert("Multiple Connections", messageText);
            oldChannel.deliverToAgent(new Message("admin", name, alert));
            oldChannel.close();
        }
        if ((sim = this.currentSimulation) != null) {
            sim.agentChannelAvailable(agent);
        }
    }

    synchronized void logoutAgentChannel(AgentChannel agent) {
        int index;
        String name = agent.getName();
        AgentChannel oldChannel = (AgentChannel)this.channelTable.get(name);
        if (oldChannel == agent) {
            this.channelTable.remove(name);
        }
        if ((index = AgentChannel.indexOf(this.channelList, 0, this.channelNumber, agent.getUserID())) >= 0 && this.channelList[index] == agent) {
            --this.channelNumber;
            this.channelList[index] = this.channelList[this.channelNumber];
            this.channelList[this.channelNumber] = null;
        }
    }

    synchronized AgentChannel getAgentChannel(int userID) {
        int index = AgentChannel.indexOf(this.channelList, 0, this.channelNumber, userID);
        return index >= 0 ? this.channelList[index] : null;
    }

    void deliverMessageToAgent(Message message) {
        AgentChannel agent = (AgentChannel)this.channelTable.get(message.getReceiver());
        if (agent != null) {
            agent.deliverToAgent(message);
        }
    }

    void deliverMessageFromAgent(AgentChannel agent, Message message) {
        Transportable content = this.handleMessage(agent, message);
        if (content != null) {
            agent.deliverToAgent(message.createReply(content));
        }
    }

    final void messageReceived(Simulation simulation, Message message) {
        Transportable content = this.handleMessage(null, message);
        if (content != null) {
            simulation.deliverMessage(message.createReply(content));
        }
    }

    private Transportable handleMessage(AgentChannel channel, Message message) {
        Transportable content = message.getContent();
        if (content.getClass() == AdminContent.class && "admin".equals(message.getReceiver())) {
            AdminContent adminContent = (AdminContent)content;
            if (adminContent.isError()) {
                log.severe("received admin error: " + adminContent);
                return null;
            }
            switch (adminContent.getType()) {
                case 5: {
                    return this.replyServerTime(message);
                }
                case 6: {
                    return this.replyNextSimulation(channel, adminContent);
                }
                case 7: {
                    return this.replyJoinSimulation(channel, adminContent);
                }
            }
            log.warning("could not handle admin " + adminContent);
            return new AdminContent(adminContent.getType(), 1);
        }
        AdminContent adminContent = new AdminContent(1);
        adminContent.setError(1, String.valueOf(content.getTransportName()) + " not supported");
        adminContent.setAttribute("name", content.getTransportName());
        return adminContent;
    }

    private Transportable replyServerTime(Message message) {
        AdminContent content = new AdminContent(5);
        content.setAttribute("time", this.getServerTime());
        return content;
    }

    private Transportable replyNextSimulation(AgentChannel agent, AdminContent content) {
        if (agent == null) {
            return new AdminContent(6, 1);
        }
        String simType = content.getAttribute("type", null);
        int userID = agent.getUserID();
        SimulationInfo info = this.nextSimulation(userID, simType);
        content = new AdminContent(6);
        if (info != null) {
            String role = this.getSimulationRoleName(info, userID);
            if (info.hasSimulationID()) {
                content.setAttribute("simulation", info.getSimulationID());
            }
            content.setAttribute("startTime", info.getStartTime());
            content.setAttribute("type", info.getType());
            if (role != null) {
                content.setAttribute("role", role);
            }
        } else {
            int delay = this.startDelay >= 30000 ? 30000 : this.startDelay;
            long currentTime = this.getServerTime();
            long time = currentTime + (long)delay;
            Simulation simulation = this.currentSimulation;
            if (simulation != null) {
                long endTime = simulation.getSimulationInfo().getEndTime() + 60000;
                if (endTime > time) {
                    time = endTime;
                }
            } else {
                long startTime;
                info = this.getFirstSimulation();
                if (info != null && (startTime = info.getStartTime() - 30000) < time) {
                    time = startTime;
                }
            }
            if (time < currentTime + 10000) {
                time = currentTime + 10000;
            }
            content.setAttribute("nextTime", time);
        }
        return content;
    }

    private Transportable replyJoinSimulation(AgentChannel agent, AdminContent content) {
        if (agent == null) {
            return new AdminContent(7, 1);
        }
        String simType = content.getAttribute("type", null);
        String simParams = content.getAttribute("params", null);
        String simRole = content.getAttribute("role", null);
        int userID = agent.getUserID();
        try {
            SimulationInfo info = this.joinSimulation(userID, simType, simParams, simRole);
            if (info != null) {
                String role = this.getSimulationRoleName(info, userID);
                content = new AdminContent(7);
                content.setAttribute("type", info.getType());
                if (info.hasSimulationID()) {
                    content.setAttribute("simulation", info.getSimulationID());
                }
                content.setAttribute("startTime", info.getStartTime());
                if (role != null) {
                    content.setAttribute("role", role);
                }
                return content;
            }
            return new AdminContent(7, 3);
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "could not join simulation", e);
            return new AdminContent(7, 3, e.getMessage());
        }
    }

    private SimulationManager getSimulationManager(String simulationType) throws NoSuchManagerException {
        SimulationManager manager = (SimulationManager)this.simulationManagerTable.get(simulationType);
        if (manager == null) {
            throw new NoSuchManagerException(simulationType);
        }
        return manager;
    }

    private String getSimulationRoleName(SimulationInfo info, int userID) {
        int index;
        String type = info.getType();
        SimulationManager manager = (SimulationManager)this.simulationManagerTable.get(type);
        if (manager != null && (index = info.indexOfParticipant(userID)) >= 0) {
            int role = info.getParticipantRole(index);
            return manager.getSimulationRoleName(type, role);
        }
        return null;
    }

    private SimulationInfo createSimulationInfo(SimulationManager manager, String simulationType, String simulationParams) throws NoSuchManagerException {
        SimulationInfo info = manager.createSimulationInfo(simulationType, simulationParams);
        if (info == null) {
            throw new NoSuchManagerException(simulationType);
        }
        return info;
    }

    private SimulationInfo createTimeReservation(long startTime, int length) {
        SimulationInfo info = new SimulationInfo(this.getNextUniqueSimulationID(), "reserved", null, length);
        info.setStartTime(startTime + (long)this.gameStartOffset);
        info.setFull();
        return info;
    }

    void addSimulationManager(String type, SimulationManager manager) {
        log.info("adding manager for simulation type " + type);
        this.simulationManagerTable.put(type, manager);
    }

    public InfoConnection getInfoConnection() {
        return this.infoConnection;
    }

    private synchronized void setLogging(ConfigManager config) throws IOException {
        int consoleLevel = config.getPropertyAsInt("sim.log.consoleLevel", config.getPropertyAsInt("log.consoleLevel", 0));
        int fileLevel = config.getPropertyAsInt("sim.log.fileLevel", config.getPropertyAsInt("log.fileLevel", 0));
        Level consoleLogLevel = LogFormatter.getLogLevel(consoleLevel);
        Level fileLogLevel = LogFormatter.getLogLevel(fileLevel);
        Level logLevel = consoleLogLevel.intValue() < fileLogLevel.intValue() ? consoleLogLevel : fileLogLevel;
        boolean showThreads = config.getPropertyAsBoolean("sim.log.threads", config.getPropertyAsBoolean("log.threads", false));
        Logger root = Logger.getLogger("");
        root.setLevel(Level.OFF);
        Logger.getLogger("com.botbox").setLevel(logLevel);
        Logger.getLogger("edu").setLevel(logLevel);
        Logger.getLogger("se.sics").setLevel(logLevel);
        Logger.getLogger("tau").setLevel(logLevel);
        this.formatter.setShowingThreads(showThreads);
        LogFormatter.setConsoleLevel(consoleLogLevel);
        if (fileLogLevel != Level.OFF) {
            if (this.rootFileHandler == null) {
                this.rootFileHandler = new FileHandler(String.valueOf(this.logName) + "%g.log", 1000000, 10);
                this.rootFileHandler.setFormatter(this.formatter);
                root.addHandler(this.rootFileHandler);
            }
            this.rootFileHandler.setLevel(fileLogLevel);
            if (this.simLogHandler != null) {
                this.simLogHandler.setLevel(fileLogLevel);
            }
        } else if (this.rootFileHandler != null) {
            this.exitSimulationLog();
            root.removeHandler(this.rootFileHandler);
            this.rootFileHandler.close();
            this.rootFileHandler = null;
        }
    }

    synchronized void enterSimulationLog(int simulationID) {
        this.exitSimulationLog();
        if (this.rootFileHandler != null) {
            LogFormatter.separator(log, Level.FINE, "Entering log for simulation " + simulationID);
            try {
                Logger root = Logger.getLogger("");
                String name = String.valueOf(this.logPrefix) + "_SIM_" + simulationID + ".log";
                this.simLogHandler = new FileHandler(name, true);
                this.simLogHandler.setFormatter(this.formatter);
                this.simLogHandler.setLevel(this.rootFileHandler.getLevel());
                this.simLogName = name;
                root.addHandler(this.simLogHandler);
                root.removeHandler(this.rootFileHandler);
                LogFormatter.separator(log, Level.FINE, "Log for simulation " + simulationID + " started");
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "could not open log file for simulation " + simulationID, e);
            }
        }
    }

    synchronized void exitSimulationLog() {
        if (this.simLogHandler != null) {
            Logger root = Logger.getLogger("");
            LogFormatter.separator(log, Level.FINE, "Simulation log complete");
            root.addHandler(this.rootFileHandler);
            root.removeHandler(this.simLogHandler);
            this.simLogHandler.close();
            this.simLogHandler = null;
            if (this.simLogName != null) {
                new File(String.valueOf(this.simLogName) + ".lck").delete();
                this.simLogName = null;
            }
        }
    }

}

