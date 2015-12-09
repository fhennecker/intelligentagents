/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.agentware;

import com.botbox.util.ArrayQueue;
import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.isl.transport.Context;
import se.sics.isl.transport.Transportable;
import se.sics.isl.util.ConfigManager;
import se.sics.isl.util.LogFormatter;
import se.sics.tasim.aw.Agent;
import se.sics.tasim.aw.Message;
import se.sics.tasim.props.AdminContent;
import se.sics.tasim.props.Alert;
import se.sics.tasim.props.SimulationStatus;
import se.sics.tasim.props.StartInfo;
import tau.tac.adx.agentware.AgentServiceImpl;
import tau.tac.adx.agentware.ServerConnection;
import tau.tac.adx.props.AdxInfoContextFactory;

public class SimClient
implements Runnable {
    private static final Logger log = Logger.getLogger(SimClient.class.getName());
    private static final String CLIENT_VERSION = "0.9.6";
    private static final int MIN_MILLIS_BEFORE_DISCONNECT = 50000;
    private final ConfigManager config;
    private final String userName;
    private final String userPassword;
    private final String serverHost;
    private final int serverPort;
    private final Context currentContext;
    private long serverTimeDiff = 0;
    private int autoJoinCount;
    private ServerConnection connection;
    private boolean isQuitPending = false;
    private boolean isAutoJoinPending = false;
    private final ArrayQueue messageQueue = new ArrayQueue();
    private final String agentImpl;
    private Agent agent;
    private AgentServiceImpl agentService;
    private final String logFilePrefix;
    private final String logSimPrefix;
    private final LogFormatter formatter;
    private FileHandler rootFileHandler;
    private FileHandler simLogHandler;
    private String simLogName;

    public SimClient(ConfigManager config, String serverHost, int serverPort, String name, String password, String agentImpl) throws IOException {
        this.config = config;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.userName = name;
        this.userPassword = password;
        this.agentImpl = agentImpl;
        this.autoJoinCount = config.getPropertyAsInt("autojoin", 1);
        String logPrefix = config.getProperty("log.prefix", "aw");
        this.logFilePrefix = this.getLogDirectory("log.directory", logPrefix);
        this.logSimPrefix = this.getLogDirectory("log.sim.directory", logPrefix);
        this.formatter = new LogFormatter();
        this.formatter.setAliasLevel(2);
        LogFormatter.setFormatterForAllHandlers(this.formatter);
        this.setLogging();
        this.currentContext = new AdxInfoContextFactory().createContext();
        if (!this.createAgentInstance()) {
            this.showWarning("Agent Setup Failed", "could not setup the agent");
            System.exit(1);
        }
        this.connection = new ServerConnection(this, 0);
        this.connection.open();
        new Thread((Runnable)this, "SimClient").start();
    }

    public String getUserName() {
        return this.userName;
    }

    public String getServerHost() {
        return this.serverHost;
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public Context getContext() {
        return this.currentContext;
    }

    public long getServerTime() {
        return System.currentTimeMillis() - this.serverTimeDiff;
    }

    public long getTimeDiff() {
        return this.serverTimeDiff;
    }

    private void requestServerTime() {
        AdminContent time = new AdminContent(5);
        Message msg = new Message(this.userName, "admin", time);
        this.deliverToServer(msg);
    }

    public void requestQuit() {
        this.isQuitPending = true;
        this.clearMessages();
        ServerConnection connection = this.connection;
        if (connection != null) {
            connection.close();
        } else {
            System.exit(1);
        }
    }

    public void autoJoinSimulation(boolean force) {
        if (this.autoJoinCount > 0 || force) {
            if (this.isAutoJoinPending) {
                this.isAutoJoinPending = false;
            } else {
                --this.autoJoinCount;
            }
            this.requestJoinSimulation();
        }
    }

    public void requestJoinSimulation() {
        AdminContent content = new AdminContent(7);
        Message msg = new Message(this.userName, "admin", content);
        this.deliverToServer(msg);
    }

    private boolean createAgentInstance() {
        if (this.agent != null) {
            return true;
        }
        try {
            log.finer("creating agent instance of " + this.agentImpl);
            this.agent = (Agent)Class.forName(this.agentImpl).newInstance();
            return true;
        }
        catch (ThreadDeath e) {
            throw e;
        }
        catch (Throwable e) {
            log.log(Level.SEVERE, "could not create an agent instance of " + this.agentImpl, e);
            return false;
        }
    }

    private boolean setupAgent(Message setupMessage) {
        this.shutdownAgent();
        if (!this.createAgentInstance()) {
            this.showWarning("Agent Setup Failed", "could not setup the agent");
            this.requestQuit();
            return false;
        }
        StartInfo info = (StartInfo)setupMessage.getContent();
        this.enterSimulationLog(info.getSimulationID());
        try {
            Agent agent = this.agent;
            this.agent = null;
            log.finer("creating agent service based on " + info);
            this.agentService = new AgentServiceImpl(this, this.userName, agent, setupMessage);
            this.agentService.deliverToAgent(setupMessage);
            return true;
        }
        finally {
            if (this.agentService == null) {
                this.exitSimulationLog();
            }
        }
    }

    private void shutdownAgent() {
        AgentServiceImpl oldAgentService = this.agentService;
        if (oldAgentService != null) {
            this.agentService = null;
            try {
                try {
                    log.finer("stopping agent service");
                    oldAgentService.stopAgent();
                }
                catch (Exception e) {
                    log.log(Level.SEVERE, "could not stop old agent", e);
                    this.exitSimulationLog();
                }
            }
            finally {
                this.exitSimulationLog();
            }
        }
    }

    protected void stopSimulation(AgentServiceImpl agentService) {
        if (this.agentService == agentService) {
            this.shutdownAgent();
            this.autoJoinSimulation(false);
        }
    }

    protected boolean deliverToServer(Message msg) {
        ServerConnection connection = this.connection;
        if (connection == null) {
            return false;
        }
        if (connection.sendMessage(msg)) {
            return true;
        }
        return false;
    }

    boolean connectionOpened(ServerConnection connection) {
        if (this.connection != connection) {
            connection.close();
            return false;
        }
        AdminContent auth = new AdminContent(4);
        auth.setAttribute("name", this.userName);
        auth.setAttribute("password", this.userPassword);
        auth.setAttribute("client.version", "0.9.6");
        Message msg = new Message(this.userName, "admin", auth);
        return connection.sendMessage(msg);
    }

    void connectionClosed(ServerConnection connection) {
        if (this.connection == connection) {
            this.connection = null;
            this.clearMessages();
            if (this.isQuitPending) {
                System.exit(0);
            } else {
                this.isAutoJoinPending = true;
                this.connection = new ServerConnection(this, 30000);
                this.connection.open();
                this.showWarning("Connection Lost", "Lost connection to " + this.serverHost + " (will reconnect in 30 seconds)");
                this.shutdownAgent();
            }
        }
    }

    void showWarning(String title, String message) {
        log.severe("************************************************************");
        log.severe("* " + title);
        log.severe("* " + message);
        log.severe("************************************************************");
    }

    void messageFromServer(ServerConnection connection, Message message) {
        if (this.connection != connection) {
            connection.close();
        } else {
            this.addMessage(message, connection.getID());
        }
    }

    void adminFromServer(ServerConnection connection, AdminContent admin) {
        if (this.connection != connection) {
            connection.close();
        } else {
            if (log.isLoggable(Level.FINEST)) {
                log.finest("(" + connection.getID() + ") received " + admin);
            }
            this.handleAdminContent(admin);
        }
    }

    void alertFromServer(ServerConnection connection, Alert alert) {
        if (this.connection != connection) {
            connection.close();
        } else {
            if (log.isLoggable(Level.FINEST)) {
                log.finest("(" + connection.getID() + ") received " + alert);
            }
            this.handleAlert(alert);
        }
    }

    private synchronized void addMessage(Message message, int connectionID) {
        if (log.isLoggable(Level.FINEST)) {
            log.finest("(" + connectionID + ") received " + message);
        }
        this.messageQueue.add(message);
        this.notify();
    }

    private synchronized Message nextMessage() {
        while (this.messageQueue.size() == 0) {
            try {
                this.wait();
                continue;
            }
            catch (InterruptedException var1_1) {
                // empty catch block
            }
        }
        return (Message)this.messageQueue.remove(0);
    }

    private synchronized void clearMessages() {
        this.messageQueue.clear();
    }

    @Override
    public void run() {
        do {
            Message msg = null;
            try {
                msg = this.nextMessage();
                Transportable content = msg.getContent();
                if (content instanceof AdminContent) {
                    this.handleAdminContent((AdminContent)content);
                    continue;
                }
                if (content instanceof StartInfo) {
                    this.setupAgent(msg);
                    continue;
                }
                if (content instanceof Alert) {
                    this.handleAlert((Alert)content);
                    continue;
                }
                if (this.agentService != null) {
                    SimulationStatus status;
                    this.agentService.deliverToAgent(msg);
                    if (!(content instanceof SimulationStatus) || !(status = (SimulationStatus)content).isSimulationEnded()) continue;
                    this.stopSimulation(this.agentService);
                    continue;
                }
                log.severe("No agent registered to receive " + msg);
                continue;
            }
            catch (ThreadDeath e) {
                log.log(Level.SEVERE, "message thread died", e);
                throw e;
            }
            catch (Throwable e) {
                log.log(Level.SEVERE, "could not handle message " + msg, e);
                continue;
            }
            break;
        } while (true);
    }

    private void handleAdminContent(AdminContent admin) {
        ServerConnection connection = this.connection;
        int type = admin.getType();
        if (admin.isError()) {
            if (type == 4) {
                this.showWarning("Authentication Failed", "could not login as " + this.userName + ": " + admin.getErrorReason());
                this.requestQuit();
            } else {
                this.showWarning("Request Failed", "Failed to " + AdminContent.getTypeAsString(type) + ": " + AdminContent.getErrorAsString(admin.getError()) + " (" + admin.getErrorReason() + ')');
            }
        } else if (connection != null) {
            switch (type) {
                case 4: {
                    String serverVersion = admin.getAttribute("server.version");
                    connection.setAuthenticated(true);
                    if (ConfigManager.compareVersion(serverVersion, "0.8.13") >= 0) {
                        connection.setTransportSupported("tables");
                    }
                    if (ConfigManager.compareVersion(serverVersion, "0.9") >= 0) break;
                    this.requestServerTime();
                    this.autoJoinSimulation(false);
                    break;
                }
                case 5: {
                    long serverTime = admin.getAttributeAsLong("time", 0);
                    if (serverTime <= 0) break;
                    this.serverTimeDiff = System.currentTimeMillis() - serverTime;
                    this.formatter.setLogTime(serverTime);
                    break;
                }
                case 6: 
                case 7: {
                    long delay;
                    long currentTime = this.getServerTime();
                    long startTime = admin.getAttributeAsLong("startTime", 0);
                    long nextTime = 0;
                    if (startTime > 0) {
                        String simText;
                        int simulationID = admin.getAttributeAsInt("simulation", -1);
                        String string = simText = simulationID >= 0 ? " " + simulationID : "";
                        if (this.agentService == null && !this.createAgentInstance()) {
                            this.showWarning("Agent Setup Failed", "could not setup the agent");
                            this.requestQuit();
                        }
                        if (startTime > currentTime) {
                            log.info("next simulation" + simText + " starts in " + (startTime - currentTime) / 1000 + " seconds");
                        } else {
                            log.info("next simulation" + simText + " has already started");
                        }
                        nextTime = startTime - 15000;
                    } else if (this.autoJoinCount > 0 && type == 6) {
                        this.autoJoinSimulation(false);
                    } else {
                        nextTime = admin.getAttributeAsLong("nextTime", 0);
                        if (nextTime < currentTime) {
                            nextTime = currentTime + 60 * (long)(20000.0 + Math.random() * 5000.0);
                        }
                    }
                    if (nextTime <= currentTime || (delay = nextTime - currentTime) <= 50000) break;
                    long maxSleep = 60 * (long)(56000.0 + Math.random() * 1000.0);
                    if (delay > maxSleep) {
                        delay = maxSleep;
                    }
                    log.info("[will reconnect in " + delay / 60000 + " minutes, " + delay / 1000 % 60 + " seconds]");
                    this.isAutoJoinPending = true;
                    this.connection = new ServerConnection(this, delay);
                    this.connection.open();
                    connection.close();
                    break;
                }
                default: {
                    log.warning("unhandled admin content: " + admin);
                }
            }
        }
    }

    private void handleAlert(Alert alert) {
        this.showWarning("ALERT: " + alert.getTitle(), alert.getMessage());
    }

    private synchronized void setLogging() throws IOException {
        int consoleLevel = this.config.getPropertyAsInt("log.consoleLevel", 0);
        int fileLevel = this.config.getPropertyAsInt("log.fileLevel", 0);
        Level consoleLogLevel = LogFormatter.getLogLevel(consoleLevel);
        Level fileLogLevel = LogFormatter.getLogLevel(fileLevel);
        Level logLevel = consoleLogLevel.intValue() < fileLogLevel.intValue() ? consoleLogLevel : fileLogLevel;
        boolean showThreads = this.config.getPropertyAsBoolean("log.threads", false);
        String[] packages = this.config.getPropertyAsArray("log.packages", "se.sics");
        if (packages != null && packages.length > 0) {
            int i = 0;
            int n = packages.length;
            while (i < n) {
                Logger.getLogger(packages[i]).setLevel(logLevel);
                ++i;
            }
        } else {
            Logger awRoot = Logger.getLogger("se.sics");
            awRoot.setLevel(logLevel);
        }
        this.formatter.setShowingThreads(showThreads);
        LogFormatter.setConsoleLevel(consoleLogLevel);
        Logger root = Logger.getLogger("");
        if (fileLogLevel != Level.OFF) {
            if (this.rootFileHandler == null) {
                this.rootFileHandler = new FileHandler(String.valueOf(this.logFilePrefix) + "%g.log", 1000000, 10);
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

    private String getLogDirectory(String property, String name) throws IOException {
        String logDirectory = this.config.getProperty(property);
        if (logDirectory != null) {
            File fp = new File(logDirectory);
            if (!fp.exists() && !fp.mkdirs() || !fp.isDirectory()) {
                throw new IOException("could not create directory '" + logDirectory + '\'');
            }
            return String.valueOf(fp.getAbsolutePath()) + File.separatorChar + name;
        }
        return name;
    }

    private synchronized void enterSimulationLog(int simulationID) {
        this.exitSimulationLog();
        if (this.rootFileHandler != null) {
            LogFormatter.separator(log, Level.FINE, "Entering log for simulation " + simulationID);
            try {
                Logger root = Logger.getLogger("");
                String name = String.valueOf(this.logSimPrefix) + "_SIM_" + simulationID + ".log";
                this.simLogHandler = new FileHandler(name, true);
                this.simLogHandler.setFormatter(this.formatter);
                this.simLogHandler.setLevel(this.rootFileHandler.getLevel());
                this.simLogName = name;
                root.addHandler(this.simLogHandler);
                root.removeHandler(this.rootFileHandler);
                LogFormatter.separator(log, Level.FINE, "Log for simulation " + simulationID + " at " + this.serverHost + ':' + this.serverPort + " started");
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "could not open log file for simulation " + simulationID, e);
            }
        }
    }

    private synchronized void exitSimulationLog() {
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

