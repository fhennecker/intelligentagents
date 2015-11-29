/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.mortbay.http.HttpContext
 *  org.mortbay.http.SecurityConstraint
 */
package se.sics.tasim.is.common;

import com.botbox.util.ArrayQueue;
import com.botbox.util.ArrayUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.mortbay.http.HttpContext;
import org.mortbay.http.SecurityConstraint;
import se.sics.isl.db.DBField;
import se.sics.isl.db.DBMatcher;
import se.sics.isl.db.DBObject;
import se.sics.isl.db.DBResult;
import se.sics.isl.db.DBTable;
import se.sics.isl.db.Database;
import se.sics.isl.transport.BinaryTransportReader;
import se.sics.isl.transport.BinaryTransportWriter;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;
import se.sics.isl.util.ConfigManager;
import se.sics.tasim.is.AgentInfo;
import se.sics.tasim.is.CompetitionSchedule;
import se.sics.tasim.is.EventWriter;
import se.sics.tasim.is.InfoConnection;
import se.sics.tasim.is.SimConnection;
import se.sics.tasim.is.SimulationInfo;
import se.sics.tasim.is.TransportEventWriter;
import se.sics.tasim.is.common.AdminPage;
import se.sics.tasim.is.common.BlockingViewerChannel;
import se.sics.tasim.is.common.ChatMessage;
import se.sics.tasim.is.common.ComingPage;
import se.sics.tasim.is.common.Competition;
import se.sics.tasim.is.common.CompetitionParticipant;
import se.sics.tasim.is.common.DefaultScoreGenerator;
import se.sics.tasim.is.common.GameScheduler;
import se.sics.tasim.is.common.HistoryPage;
import se.sics.tasim.is.common.HttpPage;
import se.sics.tasim.is.common.InfoConnectionImpl;
import se.sics.tasim.is.common.InfoManager;
import se.sics.tasim.is.common.InfoServer;
import se.sics.tasim.is.common.PageHandler;
import se.sics.tasim.is.common.ResultManager;
import se.sics.tasim.is.common.ScoreGenerator;
import se.sics.tasim.is.common.ScorePage;
import se.sics.tasim.is.common.SimulationArchiver;
import se.sics.tasim.is.common.StatPageGenerator;
import se.sics.tasim.is.common.ViewerCache;
import se.sics.tasim.is.common.ViewerPage;
import se.sics.tasim.logtool.LogReader;
import se.sics.tasim.logtool.ParticipantInfo;

public class SimServer {
    private static final Logger log = Logger.getLogger(SimServer.class.getName());
    private static final String GAME_LOG_NAME = "game.slg.gz";
    public static final int SIMULATION_SCRATCHED = 16;
    public static final int ZERO_GAME = 32;
    private boolean hasVerifiedFlag = false;
    private final ComingPage comingPage;
    private final HttpPage historyPage;
    private final HttpPage scorePage;
    private final HttpPage viewerPage;
    private final AdminPage adminPage;
    private HttpPage schedulePage;
    private final InfoServer infoServer;
    private final String serverName;
    private final String resultsPath;
    private final String urlGamePath;
    private String simTablePrefix = "simtable-";
    private int simulationsPerPage = 20;
    private String serverMessageFile;
    private String serverMessage;
    private final Database database;
    private DBTable simulationTable;
    private DBTable participantTable;
    private DBTable playedTable;
    private DBTable resultTable;
    private boolean storeResults = false;
    private DBTable stateTable;
    private int lastSimulationID = 0;
    private int lastUniqueSimulationID = 0;
    private int lastPlayedSimulationID = -1;
    private int lastCompetitionID = 0;
    private int lastFinishedCompetitionID = -1;
    private ArrayQueue comingQueue = new ArrayQueue();
    private SimulationInfo[] comingCache;
    private SimulationInfo nextComingSimulation = null;
    private DBTable competitionTable;
    private DBTable competitionResultTable;
    private DBTable competitionParticipantTable;
    private ArrayQueue comingCompetitions = new ArrayQueue();
    private Competition[] competitions;
    private Competition currentCompetition;
    private Competition nextCompetition;
    private Competition[] pendingCompetitions;
    private InfoConnectionImpl infoConnection;
    private SimConnection simConnection;
    private boolean isConnected = false;
    private SimulationInfo currentSimulation = null;
    private String currentTimeUnitName;
    private int currentTimeUnitCount;
    private ViewerCache currentViewerCache;
    private String[] currentNames = null;
    private BlockingViewerChannel[] viewerConnections;
    private static final int CHAT_CACHE_SIZE = 20;
    private static final int MAX_CHAT_CACHE_RESTORE_SIZE = 3072;
    private PrintWriter chatlog;
    private ChatMessage[] chatCache = new ChatMessage[20];
    private int chatCacheNumber;
    private int chatCacheIndex;
    private BinaryTransportWriter transportWriter = new BinaryTransportWriter();
    private EventWriter transportEventWriter = new TransportEventWriter(this.transportWriter);
    private int maxAgentScheduled = 0;

    public SimServer(InfoServer infoServer, Database database, InfoConnectionImpl connection, String resultsPath, boolean storeResults) {
        this.infoServer = infoServer;
        this.database = database;
        this.serverName = connection.getServerName();
        this.resultsPath = resultsPath = String.valueOf(resultsPath) + "history" + File.separatorChar;
        this.storeResults = storeResults;
        this.urlGamePath = "http://" + this.serverName + ':' + infoServer.getHttpPort() + '/' + this.serverName + "/history/";
        String chatLogFileName = String.valueOf(this.serverName) + "_chat.log";
        this.restoreChatCache(chatLogFileName);
        try {
            this.chatlog = new PrintWriter(new FileWriter(chatLogFileName, true), true);
        }
        catch (IOException e) {
            log.log(Level.SEVERE, "could not open chat log '" + chatLogFileName + '\'', e);
        }
        this.serverMessageFile = String.valueOf(this.serverName) + "_msg.txt";
        this.serverMessage = this.readFile(this.serverMessageFile);
        this.setupStateTable(database);
        this.participantTable = database.getTable("comingparticipants");
        if (this.participantTable == null) {
            this.participantTable = database.createTable("comingparticipants");
            this.participantTable.createField("id", 0, 32, 0);
            this.participantTable.createField("participantid", 0, 32, 0);
            this.participantTable.createField("participantrole", 0, 32, 0);
            this.participantTable.flush();
        }
        this.simulationTable = database.getTable("comingsimulations");
        if (this.simulationTable == null) {
            this.simulationTable = database.createTable("comingsimulations");
            this.simulationTable.createField("id", 0, 32, 21);
            this.simulationTable.createField("simid", 0, 32, 0);
            this.simulationTable.createField("type", 4, 32, 0);
            this.simulationTable.createField("params", 4, 255, 8);
            this.simulationTable.createField("starttime", 1, 64, 0);
            this.simulationTable.createField("length", 0, 32, 0);
            this.simulationTable.createField("flags", 0, 32, 0);
            this.simulationTable.flush();
        } else {
            boolean hasFlags;
            DBMatcher dbm = new DBMatcher();
            DBMatcher dbm2 = new DBMatcher();
            DBResult res = this.simulationTable.select(dbm);
            long currentTime = infoServer.getServerTimeMillis();
            ArrayList<SimulationInfo> removeList = null;
            if (this.hasVerifiedFlag || this.simulationTable.hasField("flags")) {
                this.hasVerifiedFlag = true;
                hasFlags = true;
            } else {
                hasFlags = false;
            }
            while (res.next()) {
                int id = res.getInt("id");
                int simID = res.getInt("simid");
                String type = res.getString("type");
                String params = res.getString("params");
                long startTime = res.getLong("starttime");
                int length = res.getInt("length") * 1000;
                int flags = hasFlags ? res.getInt("flags") : 0;
                SimulationInfo info = new SimulationInfo(id, type, params, length);
                if (simID >= 0) {
                    info.setSimulationID(simID);
                }
                info.setStartTime(startTime);
                dbm2.setInt("id", id);
                DBResult res2 = this.participantTable.select(dbm2);
                while (res2.next()) {
                    info.addParticipant(res2.getInt("participantid"), res2.getInt("participantrole"));
                }
                res2.close();
                info.setFlags(flags);
                if (currentTime > info.getStartTime()) {
                    if (removeList == null) {
                        removeList = new ArrayList<SimulationInfo>();
                    }
                    removeList.add(info);
                    continue;
                }
                this.addSimulation(info, null);
            }
            res.close();
            if (removeList != null) {
                int i = 0;
                int n = removeList.size();
                while (i < n) {
                    SimulationInfo info = (SimulationInfo)removeList.get(i);
                    dbm.clear();
                    dbm.setInt("id", info.getID());
                    this.participantTable.remove(dbm);
                    dbm.setLimit(1);
                    this.simulationTable.remove(dbm);
                    ++i;
                }
                this.participantTable.flush();
                this.simulationTable.flush();
            }
        }
        if (storeResults) {
            this.playedTable = database.getTable("playedsimulations");
            if (this.playedTable == null) {
                this.playedTable = database.createTable("playedsimulations");
                this.playedTable.createField("id", 0, 32, 21);
                this.playedTable.createField("simid", 0, 32, 1);
                this.playedTable.createField("type", 4, 32, 0);
                this.playedTable.createField("starttime", 1, 64, 0);
                this.playedTable.createField("length", 0, 32, 0);
                this.playedTable.createField("flags", 0, 32, 0);
                this.playedTable.flush();
            }
            this.resultTable = database.getTable("results");
            if (this.resultTable == null) {
                this.resultTable = database.createTable("results");
                this.resultTable.createField("id", 0, 32, 0);
                this.resultTable.createField("participantid", 0, 32, 0);
                this.resultTable.createField("participantrole", 0, 32, 0);
                this.resultTable.createField("score", 3, 64, 0);
                this.resultTable.flush();
            }
        }
        this.setupCompetitionTable(database);
        PageHandler pageHandler = infoServer.getPageHandler();
        SecurityConstraint security = new SecurityConstraint(infoServer.getServerType(), "*");
        infoServer.getHttpContext().addSecurityConstraint("/" + this.serverName + "/games/*", security);
        infoServer.getHttpContext().addSecurityConstraint("/" + this.serverName + "/viewer/*", security);
        security = new SecurityConstraint(infoServer.getServerType(), "admin");
        infoServer.getHttpContext().addSecurityConstraint("/" + this.serverName + "/admin/*", security);
        infoServer.getHttpContext().addSecurityConstraint("/" + this.serverName + "/schedule/*", security);
        this.comingPage = new ComingPage(infoServer, this);
        pageHandler.addPage("/" + this.serverName + "/games/", this.comingPage);
        this.viewerPage = new ViewerPage(infoServer, this);
        pageHandler.addPage("/" + this.serverName + "/viewer/", this.viewerPage);
        this.scorePage = new ScorePage(this, null);
        pageHandler.addPage("/" + this.serverName + "/scores/", this.scorePage);
        String path = "/" + this.serverName + "/history/";
        this.historyPage = new HistoryPage(path, this, resultsPath, this.simTablePrefix, this.simulationsPerPage);
        pageHandler.addPage(path, this.historyPage);
        if (infoServer.getConfig().getPropertyAsBoolean("admin.pages", true)) {
            String adminHeader = "<table border=0 bgcolor=black cellspacing=0 cellpadding=1 width='100%'><tr><td><table border=0 bgcolor='#e0e0e0' cellspacing=0 width='100%'><tr><td align=center><font face=arial><a href='/" + this.serverName + "/admin/'>Administration</a> | " + "<a href='/" + this.serverName + "/admin/games/'>Game Manager</a> | " + "<a href='/" + this.serverName + "/admin/competition/'>" + "Competition Manager</a> | " + "<a href='/" + this.serverName + "/schedule/'>Competition Scheduler</a>" + " | " + "<a href='http://www.sics.se/tac/docs/scm/server/0.8.8/admin.html' " + "target='sadmin'>Help</a>" + "</font>" + "</td></tr></table></td></tr></table>\r\n<p>";
            this.adminPage = new AdminPage(infoServer, this, path, adminHeader);
            pageHandler.addPage("/" + this.serverName + "/admin/*", this.adminPage);
            String className = infoServer.getConfig().getProperty("pages.gamescheduler.class", null);
            try {
                Class sp = Class.forName(className);
                Constructor spc = sp.getDeclaredConstructor(InfoServer.class, SimServer.class, String.class);
                this.schedulePage = (HttpPage)spc.newInstance(infoServer, this, adminHeader);
            }
            catch (Exception e) {
                e.printStackTrace();
                this.schedulePage = new GameScheduler(infoServer, this, adminHeader);
            }
            pageHandler.addPage("/" + this.serverName + "/schedule/", this.schedulePage);
        } else {
            this.adminPage = null;
            this.schedulePage = null;
        }
        this.transportWriter.setSupported("tables", true);
        this.setInfoConnection(connection);
    }

    private String readFile(String filename) {
        try {
            StringBuffer data;
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            data = new StringBuffer();
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    data.append(line).append('\n');
                }
            }
            finally {
                reader.close();
            }
            return data.toString();
        }
        catch (FileNotFoundException reader) {
        }
        catch (Exception e) {
            log.log(Level.WARNING, "could not load text from " + filename, e);
        }
        return null;
    }

    private void saveFile(String filename, String text) {
        try {
            FileWriter writer = new FileWriter(filename, false);
            try {
                writer.write(text);
            }
            finally {
                writer.close();
            }
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "could not save text to " + filename + " (" + text + ')', e);
        }
    }

    public String getServerName() {
        return this.serverName;
    }

    public String getSimulationTablePrefix() {
        return this.simTablePrefix;
    }

    public int getSecondsToNextSimulationEnd() {
        long currentTime;
        long endTime;
        SimulationInfo info = this.currentSimulation;
        if (info != null && (currentTime = this.infoServer.getServerTimeMillis()) < (endTime = info.getEndTime())) {
            return (int)((endTime - currentTime) / 1000) + 10;
        }
        return 60;
    }

    public int getSimulationsPerPage() {
        return this.simulationsPerPage;
    }

    public boolean isConnected() {
        return this.isConnected;
    }

    public void setInfoConnection(InfoConnectionImpl connection) {
        SimulationInfo[] sims;
        SimServer simServer = this;
        synchronized (simServer) {
            if (this.infoConnection != null) {
                this.infoConnection.close();
            }
            if (this.simConnection != null) {
                this.simConnection.close();
            }
            this.isConnected = true;
            this.infoConnection = connection;
            this.simConnection = connection.getSimConnection();
            connection.setSimServer(this);
        }
        this.simConnection.setServerTime(this.infoServer.getServerTimeMillis());
        this.simConnection.dataUpdated(3, this.lastSimulationID);
        this.simConnection.dataUpdated(2, this.lastUniqueSimulationID);
        AgentInfo[] agents = this.infoServer.getAgentInfos();
        if (agents != null) {
            int i = 0;
            int n = agents.length;
            while (i < n) {
                AgentInfo agent = agents[i];
                this.simConnection.setUser(agent.getName(), agent.getPassword(), agent.getID());
                ++i;
            }
        }
        if ((sims = this.getComingSimulations()) != null) {
            int i = 0;
            int n = sims.length;
            while (i < n) {
                this.simConnection.simulationInfo(sims[i]);
                ++i;
            }
        }
        this.simConnection.dataUpdated(1, 1);
        ChatMessage[] messages = this.getChatMessages();
        if (messages != null) {
            int i = 0;
            int n = messages.length;
            while (i < n) {
                ChatMessage chat = messages[i];
                this.simConnection.addChatMessage(chat.getTime(), chat.getServerName(), chat.getUserName(), chat.getMessage());
                ++i;
            }
        }
    }

    public void close() {
        if (this.isConnected) {
            InfoConnection iConnection = null;
            SimConnection sConnection = null;
            SimServer simServer = this;
            synchronized (simServer) {
                if (this.isConnected) {
                    this.isConnected = false;
                    if (this.infoConnection != null) {
                        iConnection = this.infoConnection;
                        this.infoConnection = null;
                        sConnection = this.simConnection;
                        this.simConnection = null;
                    }
                }
            }
            if (iConnection != null) {
                iConnection.close();
                if (sConnection != null) {
                    sConnection.close();
                }
            }
        }
    }

    public void setUser(String name, String password, int userID) {
        if (this.isConnected) {
            this.simConnection.setUser(name, password, userID);
        }
    }

    synchronized SimulationInfo getSimulationInfo(int uid) {
        int i = 0;
        int n = this.comingQueue.size();
        while (i < n) {
            SimulationInfo sim = (SimulationInfo)this.comingQueue.get(i);
            if (sim.getID() == uid) {
                return sim;
            }
            ++i;
        }
        return null;
    }

    synchronized int getAgentScheduledCount(int agentid) {
        int count = 0;
        Competition next = this.nextCompetition;
        int startGame = next == null ? Integer.MAX_VALUE : next.getStartUniqueID();
        int i = 0;
        int n = this.comingQueue.size();
        while (i < n) {
            SimulationInfo sim = (SimulationInfo)this.comingQueue.get(i);
            if (sim.getID() == startGame) break;
            if (sim.isParticipant(agentid)) {
                ++count;
            }
            ++i;
        }
        return count;
    }

    public synchronized void requestSuccessful(int operation, final int id) {
        int index;
        if (operation == 10 && (index = Competition.indexOf(this.pendingCompetitions, id)) >= 0) {
            Competition competition = this.pendingCompetitions[index];
            this.pendingCompetitions = (Competition[])ArrayUtils.remove(this.pendingCompetitions, index);
            if (competition.getSimulationCount() > 0) {
                this.addCompetition(competition, true);
                new Thread("generate.comp." + id){

                    @Override
                    public void run() {
                        try {
                            SimServer.this.generateCompetitionResults(id);
                        }
                        catch (Exception e) {
                            log.log(Level.SEVERE, "could not generate results for competition " + id, e);
                        }
                    }
                }.start();
            }
        }
    }

    public synchronized void requestFailed(int operation, int id, String reason) {
        int index;
        if (operation == 10 && (index = Competition.indexOf(this.pendingCompetitions, id)) >= 0) {
            this.pendingCompetitions = (Competition[])ArrayUtils.remove(this.pendingCompetitions, index);
        }
    }

    public void checkUser(String userName) {
        this.infoServer.updateUser(userName);
    }

    public int addUser(String name, String password, String email) {
        return this.infoServer.createUser(name, password, email);
    }

    public void dataUpdated(int type, int value) {
        if (type != 1) {
            if (type == 2) {
                if (value > this.lastUniqueSimulationID) {
                    this.lastUniqueSimulationID = value;
                    this.setStateTable("lastUniqueSimulationID", this.lastUniqueSimulationID, null, null);
                }
            } else if (type == 3 && value > this.lastSimulationID) {
                this.lastSimulationID = value;
                this.setStateTable("lastSimulationID", this.lastSimulationID, null, null);
            }
        }
    }

    public synchronized void simulationCreated(SimulationInfo info) {
        long startTime = info.getStartTime();
        long currentTime = this.infoServer.getServerTimeMillis();
        boolean index = false;
        int ugid = info.getID();
        DBObject o = new DBObject();
        if (ugid > this.lastUniqueSimulationID) {
            this.lastUniqueSimulationID = ugid;
            this.setStateTable("lastUniqueSimulationID", this.lastUniqueSimulationID, null, o);
            o.clear();
        }
        this.addSimulation(info, o);
        this.comingPage.simulationCreated(info);
    }

    public void simulationCreated(SimulationInfo info, int competitionID) {
        this.simulationCreated(info);
        Competition[] pending = this.pendingCompetitions;
        int index = Competition.indexOf(pending, competitionID);
        if (index >= 0) {
            pending[index].addSimulation(info);
        }
    }

    private boolean addSimulation(SimulationInfo info, DBObject o) {
        int index = 0;
        int ugid = info.getID();
        long startTime = info.getStartTime();
        int n = this.comingQueue.size();
        while (index < n) {
            SimulationInfo sim = (SimulationInfo)this.comingQueue.get(index);
            if (sim.getID() == ugid) {
                if (o != null) {
                    o.setInt("id", ugid);
                    try {
                        int j = 0;
                        int m = info.getParticipantCount();
                        while (j < m) {
                            int role;
                            int pid = info.getParticipantID(j);
                            if (sim.addParticipant(pid, role = info.getParticipantRole(j))) {
                                o.setInt("participantid", pid);
                                o.setInt("participantrole", role);
                                this.participantTable.insert(o);
                            }
                            ++j;
                        }
                        this.participantTable.flush();
                    }
                    catch (Exception e) {
                        log.log(Level.SEVERE, "could not save coming participants for " + info, e);
                    }
                }
                return false;
            }
            if (sim.getStartTime() > startTime) break;
            ++index;
        }
        this.comingQueue.add(index, info);
        this.comingCache = null;
        if (o != null) {
            try {
                o.setInt("id", ugid);
                o.setInt("simid", info.getSimulationID());
                o.setString("type", info.getType());
                if (info.getParams() != null) {
                    o.setString("params", info.getParams());
                }
                o.setLong("starttime", info.getStartTime());
                o.setInt("length", info.getSimulationLength() / 1000);
                if (this.hasVerifiedFlag || this.simulationTable.hasField("flags")) {
                    this.hasVerifiedFlag = true;
                    o.setInt("flags", info.getFlags());
                }
                this.simulationTable.insert(o);
                o.clear();
                o.setInt("id", ugid);
                int i = 0;
                int n2 = info.getParticipantCount();
                while (i < n2) {
                    o.setInt("participantid", info.getParticipantID(i));
                    o.setInt("participantrole", info.getParticipantRole(i));
                    this.participantTable.insert(o);
                    ++i;
                }
                this.simulationTable.flush();
                this.participantTable.flush();
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "could not store coming simulation " + info, e);
            }
        }
        this.checkNextSimulation();
        return true;
    }

    public synchronized void simulationRemoved(int simulationUniqID, String msg) {
        try {
            DBMatcher dbm = new DBMatcher();
            dbm.setInt("id", simulationUniqID);
            this.participantTable.remove(dbm);
            dbm.setLimit(1);
            this.simulationTable.remove(dbm);
            this.participantTable.flush();
            this.simulationTable.flush();
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "could not remove simulation " + simulationUniqID, e);
        }
        int i = 0;
        int n = this.comingQueue.size();
        while (i < n) {
            SimulationInfo sim = (SimulationInfo)this.comingQueue.get(i);
            if (sim.getID() == simulationUniqID) {
                this.comingQueue.remove(i);
                this.comingCache = null;
                this.checkNextSimulation();
                break;
            }
            ++i;
        }
    }

    private void checkNextSimulation() {
        boolean notify = false;
        if (this.comingQueue.size() > 0) {
            SimulationInfo info = (SimulationInfo)this.comingQueue.get(0);
            if (info != this.nextComingSimulation) {
                this.nextComingSimulation = info;
                notify = true;
            }
        } else if (this.nextComingSimulation != null) {
            this.nextComingSimulation = null;
            notify = true;
        }
        if (notify && this.viewerConnections != null) {
            byte[] data;
            BinaryTransportWriter binaryTransportWriter = this.transportWriter;
            synchronized (binaryTransportWriter) {
                this.transportWriter.clear();
                this.addNextSimulation(this.nextComingSimulation, this.transportWriter);
                this.transportWriter.finish();
                data = this.transportWriter.getBytes();
            }
            this.sendToViewers(data);
        }
    }

    private void addNextSimulation(SimulationInfo info, BinaryTransportWriter writer) {
        writer.node("nextSimulation");
        if (info != null) {
            if (info.hasSimulationID()) {
                writer.attr("id", info.getSimulationID());
            }
            writer.attr("startTime", info.getStartTime());
        }
        writer.endNode("nextSimulation");
    }

    public synchronized void simulationJoined(int simulationUniqID, int agentID, int role) {
        SimulationInfo sim = this.getSimulationInfo(simulationUniqID);
        if (sim != null) {
            sim.addParticipant(agentID, role);
            try {
                DBObject o = new DBObject();
                o.setInt("id", simulationUniqID);
                o.setInt("participantid", agentID);
                o.setInt("participantrole", role);
                this.participantTable.insert(o);
                this.participantTable.flush();
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "could not join simulation " + simulationUniqID, e);
            }
            this.comingPage.simulationJoined(simulationUniqID, agentID);
        }
    }

    public void simulationLocked(int simulationUniqID, int simID) {
        SimulationInfo sim = this.getSimulationInfo(simulationUniqID);
        if (sim != null) {
            DBMatcher dbm = new DBMatcher();
            DBObject o = new DBObject();
            if (simID > this.lastSimulationID) {
                this.lastSimulationID = simID;
                this.setStateTable("lastSimulationID", this.lastSimulationID, dbm, o);
                dbm.clear();
                o.clear();
            }
            dbm.setInt("id", simulationUniqID);
            dbm.setLimit(1);
            o.setInt("simid", simID);
            try {
                this.simulationTable.update(dbm, o);
                this.simulationTable.flush();
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "could not store simulation id " + simID, e);
            }
            sim.setSimulationID(simID);
        }
        this.checkCompetitionSimulationID(simulationUniqID, simID);
    }

    public void simulationStarted(int simulationUniqID, String timeUnitName, int timeUnitCount) {
        SimulationInfo sim = this.getSimulationInfo(simulationUniqID);
        if (sim != null) {
            this.currentSimulation = sim;
            this.currentTimeUnitName = timeUnitName;
            this.currentTimeUnitCount = timeUnitCount;
            String simType = sim.getType();
            InfoManager infoManager = this.infoServer.getInfoManager(simType);
            ViewerCache cache = null;
            if (infoManager != null && (cache = infoManager.createViewerCache(simType)) != null) {
                this.currentViewerCache = cache;
            }
            if (cache == null) {
                cache = new ViewerCache();
            }
            if (this.viewerConnections != null) {
                byte[] data;
                BinaryTransportWriter binaryTransportWriter = this.transportWriter;
                synchronized (binaryTransportWriter) {
                    this.transportWriter.clear();
                    this.addSimulationStarted(sim, timeUnitName, timeUnitCount, this.transportWriter);
                    this.transportWriter.finish();
                    data = this.transportWriter.getBytes();
                }
                this.sendToViewers(data);
            }
            this.checkCompetitionStart(simulationUniqID, sim.getSimulationID());
        }
    }

    private void addSimulationStarted(SimulationInfo info, String timeUnitName, int timeUnitCount, BinaryTransportWriter writer) {
        writer.node("simulationStarted").attr("id", info.getSimulationID()).attr("type", info.getType()).attr("startTime", info.getStartTime()).attr("endTime", info.getEndTime());
        if (timeUnitName != null) {
            writer.attr("timeUnitName", timeUnitName);
        }
        if (timeUnitCount > 0) {
            writer.attr("timeUnitCount", timeUnitCount);
        }
        writer.endNode("simulationStarted");
    }

    public void simulationStopped(int simulationUniqID, int simulationID, boolean error) {
        SimulationInfo sim = this.getSimulationInfo(simulationUniqID);
        if (sim != null) {
            this.simulationRemoved(simulationUniqID, null);
            int id = sim.getSimulationID();
            if (id > this.lastPlayedSimulationID) {
                this.lastPlayedSimulationID = id;
                this.setStateTable("lastPlayedSimulationID", this.lastPlayedSimulationID, null, null);
            }
            if (this.currentSimulation == sim) {
                this.currentSimulation = null;
                this.currentTimeUnitName = null;
                this.currentTimeUnitCount = 0;
                this.currentViewerCache = null;
                this.currentNames = null;
            }
            this.checkCompetitionEnd(simulationUniqID + 1);
        }
        if (!error) {
            this.infoServer.getSimulationArchiver().addSimulation(this, simulationID);
        }
        if (this.viewerConnections != null) {
            byte[] data;
            BinaryTransportWriter binaryTransportWriter = this.transportWriter;
            synchronized (binaryTransportWriter) {
                this.transportWriter.clear();
                this.transportWriter.node("simulationStopped").attr("id", simulationID).endNode("simulationStopped");
                this.transportWriter.finish();
                data = this.transportWriter.getBytes();
            }
            this.sendToViewers(data);
        }
    }

    public void sendChatMessage(long time, String message) {
        if (message != null) {
            if (message.startsWith("!")) {
                SimConnection simc;
                String commandResult = this.handleChatCommand(message);
                if (commandResult != null && (simc = this.simConnection) != null) {
                    simc.addChatMessage(this.infoServer.getServerTimeMillis(), this.serverName, "[" + this.serverName + ']', commandResult);
                }
            } else {
                this.sendChatMessage(time, "admin", message);
            }
        }
    }

    public void nextTimeUnit(int timeUnit) {
        ViewerCache cache = this.currentViewerCache;
        if (cache != null) {
            cache.nextTimeUnit(timeUnit);
        }
        if (this.viewerConnections != null) {
            byte[] data;
            BinaryTransportWriter binaryTransportWriter = this.transportWriter;
            synchronized (binaryTransportWriter) {
                this.transportWriter.clear();
                this.transportEventWriter.nextTimeUnit(timeUnit);
                this.transportWriter.finish();
                data = this.transportWriter.getBytes();
            }
            this.sendToViewers(data);
        }
    }

    public void participant(int id, int role, String name, int participantID) {
        int index;
        ViewerCache cache;
        SimulationInfo info = this.currentSimulation;
        if (info != null && (index = info.indexOfParticipant(participantID)) >= 0) {
            Object[] names = this.currentNames;
            int count = info.getParticipantCount();
            if (names == null) {
                names = new String[count];
            } else if (names.length < count) {
                names = (String[])ArrayUtils.setSize(names, count);
            }
            names[index] = name;
            this.currentNames = names;
        }
        if ((cache = this.currentViewerCache) != null) {
            cache.participant(id, role, name, participantID);
        }
        if (this.viewerConnections != null) {
            byte[] data;
            BinaryTransportWriter binaryTransportWriter = this.transportWriter;
            synchronized (binaryTransportWriter) {
                this.transportWriter.clear();
                this.transportEventWriter.participant(id, role, name, participantID);
                this.transportWriter.finish();
                data = this.transportWriter.getBytes();
            }
            this.sendToViewers(data);
        }
    }

    public void dataUpdated(int agent, int type, int value) {
        ViewerCache cache = this.currentViewerCache;
        if (cache != null) {
            cache.dataUpdated(agent, type, value);
        }
        if (this.viewerConnections != null) {
            byte[] data;
            BinaryTransportWriter binaryTransportWriter = this.transportWriter;
            synchronized (binaryTransportWriter) {
                this.transportWriter.clear();
                this.transportEventWriter.dataUpdated(agent, type, value);
                this.transportWriter.finish();
                data = this.transportWriter.getBytes();
            }
            this.sendToViewers(data);
        }
    }

    public void dataUpdated(int agent, int type, long value) {
        ViewerCache cache = this.currentViewerCache;
        if (cache != null) {
            cache.dataUpdated(agent, type, value);
        }
        if (this.viewerConnections != null) {
            byte[] data;
            BinaryTransportWriter binaryTransportWriter = this.transportWriter;
            synchronized (binaryTransportWriter) {
                this.transportWriter.clear();
                this.transportEventWriter.dataUpdated(agent, type, value);
                this.transportWriter.finish();
                data = this.transportWriter.getBytes();
            }
            this.sendToViewers(data);
        }
    }

    public void dataUpdated(int agent, int type, float value) {
        ViewerCache cache = this.currentViewerCache;
        if (cache != null) {
            cache.dataUpdated(agent, type, value);
        }
        if (this.viewerConnections != null) {
            byte[] data;
            BinaryTransportWriter binaryTransportWriter = this.transportWriter;
            synchronized (binaryTransportWriter) {
                this.transportWriter.clear();
                this.transportEventWriter.dataUpdated(agent, type, value);
                this.transportWriter.finish();
                data = this.transportWriter.getBytes();
            }
            this.sendToViewers(data);
        }
    }

    public void dataUpdated(int agent, int type, double value) {
        ViewerCache cache = this.currentViewerCache;
        if (cache != null) {
            cache.dataUpdated(agent, type, value);
        }
        if (this.viewerConnections != null) {
            byte[] data;
            BinaryTransportWriter binaryTransportWriter = this.transportWriter;
            synchronized (binaryTransportWriter) {
                this.transportWriter.clear();
                this.transportEventWriter.dataUpdated(agent, type, value);
                this.transportWriter.finish();
                data = this.transportWriter.getBytes();
            }
            this.sendToViewers(data);
        }
    }

    public void dataUpdated(int agent, int type, String value) {
        ViewerCache cache = this.currentViewerCache;
        if (cache != null) {
            cache.dataUpdated(agent, type, value);
        }
        if (this.viewerConnections != null) {
            byte[] data;
            BinaryTransportWriter binaryTransportWriter = this.transportWriter;
            synchronized (binaryTransportWriter) {
                this.transportWriter.clear();
                this.transportEventWriter.dataUpdated(agent, type, value);
                this.transportWriter.finish();
                data = this.transportWriter.getBytes();
            }
            this.sendToViewers(data);
        }
    }

    public void dataUpdated(int agent, int type, Transportable value) {
        ViewerCache cache = this.currentViewerCache;
        if (cache != null) {
            cache.dataUpdated(agent, type, value);
        }
        if (this.viewerConnections != null) {
            byte[] data;
            BinaryTransportWriter binaryTransportWriter = this.transportWriter;
            synchronized (binaryTransportWriter) {
                this.transportWriter.clear();
                this.transportEventWriter.dataUpdated(agent, type, value);
                this.transportWriter.finish();
                data = this.transportWriter.getBytes();
            }
            this.sendToViewers(data);
        }
    }

    public void dataUpdated(int type, Transportable value) {
        ViewerCache cache = this.currentViewerCache;
        if (cache != null) {
            cache.dataUpdated(type, value);
        }
        if (this.viewerConnections != null) {
            byte[] data;
            BinaryTransportWriter binaryTransportWriter = this.transportWriter;
            synchronized (binaryTransportWriter) {
                this.transportWriter.clear();
                this.transportEventWriter.dataUpdated(type, value);
                this.transportWriter.finish();
                data = this.transportWriter.getBytes();
            }
            this.sendToViewers(data);
        }
    }

    public void interaction(int fromAgent, int toAgent, int type) {
        ViewerCache cache = this.currentViewerCache;
        if (cache != null) {
            cache.interaction(fromAgent, toAgent, type);
        }
        if (this.viewerConnections != null) {
            byte[] data;
            BinaryTransportWriter binaryTransportWriter = this.transportWriter;
            synchronized (binaryTransportWriter) {
                this.transportWriter.clear();
                this.transportEventWriter.interaction(fromAgent, toAgent, type);
                this.transportWriter.finish();
                data = this.transportWriter.getBytes();
            }
            this.sendToViewers(data);
        }
    }

    public void interactionWithRole(int fromAgent, int role, int type) {
        ViewerCache cache = this.currentViewerCache;
        if (cache != null) {
            cache.interactionWithRole(fromAgent, role, type);
        }
        if (this.viewerConnections != null) {
            byte[] data;
            BinaryTransportWriter binaryTransportWriter = this.transportWriter;
            synchronized (binaryTransportWriter) {
                this.transportWriter.clear();
                this.transportEventWriter.interactionWithRole(fromAgent, role, type);
                this.transportWriter.finish();
                data = this.transportWriter.getBytes();
            }
            this.sendToViewers(data);
        }
    }

    private void restoreChatCache(String filename) {
        try {
            RandomAccessFile fp = new RandomAccessFile(filename, "r");
            try {
                long length = fp.length();
                if (length > 0) {
                    long seek = length - 3072;
                    if (seek > 0) {
                        fp.seek(seek);
                    }
                    if (seek <= 0 || fp.readLine() != null) {
                        String line;
                        while ((line = fp.readLine()) != null) {
                            int index = (this.chatCacheIndex + this.chatCacheNumber) % 20;
                            int i0 = line.indexOf(44);
                            int i1 = line.indexOf(44, i0 + 1);
                            int i2 = line.indexOf(44, i1 + 1);
                            long time = Long.parseLong(line.substring(0, i0));
                            String serverName = line.substring(i0 + 1, i1);
                            String userName = line.substring(i1 + 1, i2);
                            String message = line.substring(i2 + 1);
                            if (this.chatCache[index] == null) {
                                this.chatCache[index] = new ChatMessage(time, serverName, userName, message);
                            } else {
                                this.chatCache[index].setMessage(time, serverName, userName, message);
                            }
                            if (this.chatCacheNumber < 20) {
                                ++this.chatCacheNumber;
                                continue;
                            }
                            this.chatCacheIndex = (this.chatCacheIndex + 1) % 20;
                        }
                    }
                }
            }
            finally {
                fp.close();
            }
        }
        catch (FileNotFoundException fp) {
        }
        catch (Exception e) {
            log.log(Level.WARNING, "could not restore chat messages from " + filename, e);
        }
    }

    private ChatMessage[] getChatMessages() {
        BinaryTransportWriter binaryTransportWriter = this.transportWriter;
        synchronized (binaryTransportWriter) {
            block5 : {
                if (this.chatCacheNumber != 0) break block5;
                return null;
            }
            ChatMessage[] messages = new ChatMessage[this.chatCacheNumber];
            int i = 0;
            while (i < this.chatCacheNumber) {
                messages[i] = this.chatCache[(this.chatCacheIndex + i) % 20];
                ++i;
            }
            return messages;
        }
    }

    public void addViewerConnection(BlockingViewerChannel connection) {
        try {
            BinaryTransportWriter binaryTransportWriter = this.transportWriter;
            synchronized (binaryTransportWriter) {
                this.viewerConnections = (BlockingViewerChannel[])ArrayUtils.add(BlockingViewerChannel.class, this.viewerConnections, connection);
                byte[] buffer = this.transportWriter.getInitBytes();
                if (buffer != null) {
                    connection.write(buffer);
                }
                this.transportWriter.clear();
                this.transportWriter.node("serverTime").attr("time", this.infoServer.getServerTimeMillis()).endNode("serverTime");
                this.transportWriter.finish();
                connection.write(this.transportWriter.getBytes());
                this.transportWriter.clear();
                this.addNextSimulation(this.nextComingSimulation, this.transportWriter);
                int i = 0;
                while (i < this.chatCacheNumber) {
                    this.chatCache[(this.chatCacheIndex + i) % 20].writeMessage(this.transportWriter);
                    ++i;
                }
                this.transportWriter.finish();
                connection.write(this.transportWriter.getBytes());
                String currentTimeUnitName = this.currentTimeUnitName;
                int currentTimeUnitCount = this.currentTimeUnitCount;
                SimulationInfo currentSimulation = this.currentSimulation;
                if (currentSimulation != null) {
                    ViewerCache currentViewerCache = this.currentViewerCache;
                    this.transportWriter.clear();
                    this.addSimulationStarted(currentSimulation, currentTimeUnitName, currentTimeUnitCount, this.transportWriter);
                    if (currentViewerCache != null) {
                        currentViewerCache.writeCache(this.transportEventWriter);
                    }
                    this.transportWriter.finish();
                    connection.write(this.transportWriter.getBytes());
                }
            }
        }
        catch (Exception e) {
            log.log(Level.WARNING, "could not send init to " + connection.getName(), e);
            connection.close();
        }
    }

    public void removeViewerConnection(BlockingViewerChannel connection) {
        BinaryTransportWriter binaryTransportWriter = this.transportWriter;
        synchronized (binaryTransportWriter) {
            this.viewerConnections = (BlockingViewerChannel[])ArrayUtils.remove(this.viewerConnections, connection);
        }
    }

    public void viewerDataReceived(BlockingViewerChannel connection, BinaryTransportReader reader) {
        try {
            while (reader.nextNode(false)) {
                if (!reader.isNode("chat")) continue;
                String message = reader.getAttribute("message");
                if (message.startsWith("!")) {
                    byte[] data;
                    String commandResult = this.handleChatCommand(message);
                    if (commandResult == null) continue;
                    BinaryTransportWriter binaryTransportWriter = this.transportWriter;
                    synchronized (binaryTransportWriter) {
                        this.transportWriter.clear();
                        this.transportWriter.node("chat").attr("time", this.infoServer.getServerTimeMillis()).attr("server", this.serverName).attr("user", "[" + this.serverName + ']').attr("message", commandResult).endNode("chat");
                        this.transportWriter.finish();
                        data = this.transportWriter.getBytes();
                    }
                    connection.write(data);
                    continue;
                }
                this.sendChatMessage(this.infoServer.getServerTimeMillis(), connection.getUserName(), reader.getAttribute("message"));
            }
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "could not parse message from viewer " + connection.getName(), e);
        }
    }

    private String handleChatCommand(String command) {
        if (command.equals("!who")) {
            StringBuffer sb = new StringBuffer();
            BlockingViewerChannel[] viewers = this.viewerConnections;
            if (viewers == null) {
                sb.append("No viewers connected");
            } else {
                int i = 0;
                int n = viewers.length;
                while (i < n) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    sb.append(viewers[i].getUserName());
                    ++i;
                }
            }
            return sb.toString();
        }
        if (command.equals("!ip")) {
            StringBuffer sb = new StringBuffer();
            BlockingViewerChannel[] viewers = this.viewerConnections;
            if (viewers == null) {
                sb.append("No viewers connected");
            } else {
                int i = 0;
                int n = viewers.length;
                while (i < n) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    sb.append(viewers[i].getUserName()).append(" (").append(viewers[i].getRemoteHost()).append(')');
                    ++i;
                }
            }
            return sb.toString();
        }
        return null;
    }

    private void sendChatMessage(long time, String userName, String message) {
        Object data;
        SimConnection simc;
        String chatMessage = "" + time + ',' + this.serverName + ',' + userName + ',' + message;
        log.info("CHAT: " + chatMessage);
        if (this.chatlog != null) {
            this.chatlog.println(chatMessage);
        }
        BinaryTransportWriter binaryTransportWriter = this.transportWriter;
        synchronized (binaryTransportWriter) {
            int index = (this.chatCacheIndex + this.chatCacheNumber) % 20;
            ChatMessage chat = this.chatCache[index];
            if (chat == null) {
                chat = this.chatCache[index] = new ChatMessage(time, this.serverName, userName, message);
            } else {
                chat.setMessage(time, this.serverName, userName, message);
            }
            if (this.chatCacheNumber < 20) {
                ++this.chatCacheNumber;
            } else {
                this.chatCacheIndex = (this.chatCacheIndex + 1) % 20;
            }
            if (this.viewerConnections != null) {
                this.transportWriter.clear();
                chat.writeMessage(this.transportWriter);
                this.transportWriter.finish();
                data = this.transportWriter.getBytes();
            } else {
                data = null;
            }
        }
        if (data != null) {
            this.sendToViewers((byte[])data);
        }
        if ((simc = this.simConnection) != null) {
            simc.addChatMessage(time, this.serverName, userName, message);
        }
    }

    private void sendToViewers(byte[] data) {
        BlockingViewerChannel[] channels = this.viewerConnections;
        if (channels != null) {
            int i = 0;
            int n = channels.length;
            while (i < n) {
                try {
                    channels[i].write(data);
                }
                catch (Exception e) {
                    log.log(Level.WARNING, "could not send to " + channels[i].getName(), e);
                    channels[i].close();
                }
                ++i;
            }
        }
    }

    private void setupCompetitionTable(Database database) {
        this.competitionParticipantTable = database.getTable("competitionparts");
        if (this.competitionParticipantTable == null) {
            this.competitionParticipantTable = database.createTable("competitionparts");
            this.competitionParticipantTable.createField("competition", 0, 32, 0);
            this.competitionParticipantTable.createField("participantid", 0, 32, 0);
            this.competitionParticipantTable.createField("flags", 0, 32, 0);
            this.competitionParticipantTable.createField("score", 3, 64, 0);
            this.competitionParticipantTable.createField("wscore", 3, 64, 0);
            this.competitionParticipantTable.createField("gamesplayed", 0, 32, 0);
            this.competitionParticipantTable.createField("zgamesplayed", 0, 32, 0);
            this.competitionParticipantTable.createField("wgamesplayed", 3, 64, 0);
            this.competitionParticipantTable.createField("zwgamesplayed", 3, 64, 0);
            this.competitionParticipantTable.createField("avgsc1", 3, 64, 0);
            this.competitionParticipantTable.createField("avgsc2", 3, 64, 0);
            this.competitionParticipantTable.createField("avgsc3", 3, 64, 0);
            this.competitionParticipantTable.createField("avgsc4", 3, 64, 0);
            this.competitionParticipantTable.flush();
        }
        this.competitionTable = database.getTable("competitions");
        if (this.competitionTable == null) {
            this.competitionTable = database.createTable("competitions");
            this.competitionTable.createField("id", 0, 32, 21);
            this.competitionTable.createField("parent", 0, 32, 0, new Integer(0));
            this.competitionTable.createField("name", 4, 80, 0);
            this.competitionTable.createField("flags", 0, 32, 0);
            this.competitionTable.createField("starttime", 1, 64, 0);
            this.competitionTable.createField("endtime", 1, 64, 0);
            this.competitionTable.createField("startuniqid", 0, 32, 0, new Integer(-1));
            this.competitionTable.createField("startsimid", 0, 32, 0, new Integer(-1));
            this.competitionTable.createField("simulations", 0, 32, 0);
            this.competitionTable.createField("startweight", 3, 64, 0);
            this.competitionTable.createField("scoreclass", 4, 80, 8);
            this.competitionTable.flush();
        } else {
            if (!this.competitionTable.hasField("parent")) {
                this.competitionTable.createField("parent", 0, 32, 0, new Integer(0));
            }
            this.loadCompetitions(false);
        }
        this.competitionResultTable = database.getTable("competitionresults");
        if (this.competitionResultTable == null) {
            this.competitionResultTable = database.createTable("competitionresults");
            this.competitionResultTable.createField("id", 0, 32, 0);
            this.competitionResultTable.createField("simid", 0, 32, 0);
            this.competitionResultTable.createField("competition", 0, 32, 0);
            this.competitionResultTable.createField("participantid", 0, 32, 0);
            this.competitionResultTable.createField("participantrole", 0, 32, 0);
            this.competitionResultTable.createField("flags", 0, 32, 0);
            this.competitionResultTable.createField("score", 3, 64, 0);
            this.competitionResultTable.createField("weight", 3, 64, 0);
            this.competitionResultTable.flush();
        }
    }

    private void loadCompetitions(boolean checkAlreadyLoaded) {
        Competition[] competitions;
        boolean hasCompetitionChain = false;
        DBMatcher dbm = new DBMatcher();
        DBResult res = this.competitionTable.select(dbm);
        while (res.next()) {
            int id = res.getInt("id");
            if (id <= this.lastFinishedCompetitionID || checkAlreadyLoaded && Competition.indexOf(this.competitions, id) >= 0) continue;
            int parentID = res.getInt("parent");
            String name = res.getString("name");
            int flags = res.getInt("flags");
            long startTime = res.getLong("starttime");
            long endTime = res.getLong("endtime");
            int startUniqueID = res.getInt("startuniqid");
            int startPublicID = res.getInt("startsimid");
            int simulationCount = res.getInt("simulations");
            double startWeight = res.getDouble("startweight");
            String scoreClass = res.getString("scoreclass");
            Competition competition = new Competition(id, name, startTime, endTime, startUniqueID, simulationCount, startWeight);
            if (startPublicID >= 0) {
                competition.setStartSimulationID(startPublicID);
            }
            if (scoreClass != null) {
                competition.setScoreClassName(scoreClass);
            }
            competition.setFlags(flags);
            if (parentID > 0) {
                competition.setParentCompetitionID(parentID);
                hasCompetitionChain = true;
            }
            DBMatcher dbm2 = new DBMatcher();
            dbm2.setInt("competition", id);
            DBResult res2 = this.competitionParticipantTable.select(dbm2);
            while (res2.next()) {
                int pid;
                String uname = this.infoServer.getUserName(pid = res2.getInt("participantid"));
                CompetitionParticipant cp = new CompetitionParticipant(pid, uname == null ? "unknown" : uname);
                cp.setFlags(res2.getInt("flags"));
                cp.setScores(res2.getDouble("score"), res2.getDouble("wscore"), res2.getInt("gamesplayed"), res2.getInt("zgamesplayed"), res2.getDouble("wgamesplayed"), res2.getDouble("zwgamesplayed"));
                cp.setAvgScores(res2.getDouble("avgsc1"), res2.getDouble("avgsc2"), res2.getDouble("avgsc3"), res2.getDouble("avgsc4"));
                competition.addParticipant(cp);
            }
            res2.close();
            this.addCompetition(competition, false);
        }
        res.close();
        if (hasCompetitionChain && (competitions = this.getCompetitions()) != null) {
            int i = 0;
            int n = competitions.length;
            while (i < n) {
                Competition comp = competitions[i];
                if (comp.hasParentCompetition()) {
                    int parentID = comp.getParentCompetitionID();
                    Competition parentCompetition = this.getCompetitionByID(parentID);
                    if (parentCompetition == null) {
                        log.log(Level.SEVERE, "could not find parent competition " + parentID + " for competition " + comp.getName(), new IllegalStateException("competition not found"));
                    } else if (parentCompetition.isParentCompetition(comp)) {
                        log.log(Level.SEVERE, "circular dependencies for competition " + comp.getName(), new IllegalStateException("circular competition chain"));
                    } else {
                        comp.setParentCompetition(parentCompetition);
                    }
                }
                ++i;
            }
        }
    }

    private void addCompetition(Competition competition, boolean addToDatabase) {
        long endTime;
        long currentTime;
        this.competitions = (Competition[])ArrayUtils.add(Competition.class, this.competitions, competition);
        if (addToDatabase) {
            try {
                DBObject object = new DBObject();
                object.setInt("id", competition.getID());
                if (competition.hasParentCompetition()) {
                    object.setInt("parent", competition.getParentCompetitionID());
                }
                object.setString("name", competition.getName());
                object.setInt("flags", competition.getFlags());
                object.setLong("starttime", competition.getStartTime());
                object.setLong("endtime", competition.getEndTime());
                object.setInt("startuniqid", competition.getStartUniqueID());
                object.setInt("startsimid", competition.getStartSimulationID());
                object.setInt("simulations", competition.getSimulationCount());
                object.setDouble("startweight", competition.getStartWeight());
                String scoreClass = competition.getScoreClassName();
                if (scoreClass != null) {
                    object.setString("scoreclass", scoreClass);
                }
                this.competitionTable.insert(object);
                this.competitionTable.flush();
                CompetitionParticipant[] participants = competition.getParticipants();
                if (participants != null) {
                    object.clear();
                    object.setInt("competition", competition.getID());
                    int i = 0;
                    int n = participants.length;
                    while (i < n) {
                        CompetitionParticipant cp = participants[i];
                        object.setInt("participantid", cp.getID());
                        this.competitionParticipantTable.insert(object);
                        ++i;
                    }
                    this.competitionParticipantTable.flush();
                }
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "could not add competition '" + competition.getName() + "' to database", e);
            }
        }
        if ((endTime = competition.getEndTime()) > (currentTime = this.infoServer.getServerTimeMillis())) {
            long startTime = competition.getStartTime();
            int index = this.comingCompetitions.size() - 1;
            while (index >= 0) {
                Competition comp = (Competition)this.comingCompetitions.get(index);
                if (comp.getStartTime() < startTime) break;
                --index;
            }
            this.comingCompetitions.add(++index, competition);
            if (index == 0) {
                this.nextCompetition = competition;
                if (startTime < currentTime) {
                    this.currentCompetition = competition;
                }
            }
        }
    }

    private void removeCompetition(int competitionID, boolean removeFromDatabase) {
        int index;
        int i = 0;
        int n = this.comingCompetitions.size();
        while (i < n) {
            Competition comp = (Competition)this.comingCompetitions.get(i);
            if (comp.getID() == competitionID) {
                this.comingCompetitions.remove(i);
                if (i == 0) {
                    Competition competition = this.nextCompetition = n > 1 ? (Competition)this.comingCompetitions.get(0) : null;
                }
                if (this.currentCompetition != comp) break;
                this.currentCompetition = null;
                break;
            }
            ++i;
        }
        if ((index = Competition.indexOf(this.competitions, competitionID)) >= 0) {
            this.competitions = (Competition[])ArrayUtils.remove(this.competitions, index);
        }
        if (removeFromDatabase) {
            try {
                DBMatcher dbm = new DBMatcher();
                dbm.setInt("id", competitionID);
                dbm.setLimit(1);
                this.competitionTable.remove(dbm);
                this.competitionTable.flush();
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "could not remove competition '" + competitionID + "' from database", e);
            }
        }
    }

    private void checkCompetitionStart(int simulationUniqID, int simID) {
        Competition nextCompetition;
        this.checkCompetitionEnd(simulationUniqID);
        this.checkCompetitionSimulationID(simulationUniqID, simID);
        if (this.currentCompetition == null && (nextCompetition = this.nextCompetition) != null && nextCompetition.isSimulationIncluded(simulationUniqID)) {
            this.currentCompetition = nextCompetition;
            this.lockCompetitionSimulations(nextCompetition, simID);
        }
    }

    private synchronized void checkCompetitionEnd(int simulationUniqID) {
        if (this.currentCompetition != null && !this.currentCompetition.isSimulationIncluded(simulationUniqID)) {
            if (this.comingCompetitions.size() > 0 && this.comingCompetitions.get(0) == this.currentCompetition) {
                this.comingCompetitions.remove(0);
            }
            this.currentCompetition = null;
            this.nextCompetition = this.comingCompetitions.size() > 0 ? (Competition)this.comingCompetitions.get(0) : null;
        }
    }

    private void checkCompetitionSimulationID(int simulationUniqID, int simID) {
        Competition competition = this.nextCompetition;
        if (competition != null && !competition.hasSimulationID() && competition.isSimulationIncluded(simulationUniqID)) {
            int startID = competition.getStartUniqueID();
            int startSimID = simID + startID - simulationUniqID;
            competition.setStartSimulationID(startSimID);
            try {
                DBMatcher dbm = new DBMatcher();
                DBObject object = new DBObject();
                dbm.setInt("id", competition.getID());
                dbm.setLimit(1);
                object.setInt("startsimid", startSimID);
                this.competitionTable.update(dbm, object);
                this.competitionTable.flush();
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "could not set simulation id " + simID + " in competition '" + competition.getName() + "' in database", e);
            }
            this.lockCompetitionSimulations(competition, simID);
        }
    }

    private void lockCompetitionSimulations(Competition competition, int simID) {
        int numSimulations;
        if (competition.hasSimulationID() && (numSimulations = competition.getSimulationCount() - (simID - competition.getStartSimulationID())) > 0) {
            log.finer("requesting lock of " + numSimulations + " simulations due to start of competition " + competition.getName());
            SimConnection connection = this.simConnection;
            if (connection != null) {
                connection.lockNextSimulations(numSimulations);
            }
        }
    }

    public Competition getCurrentCompetition() {
        return this.currentCompetition;
    }

    public Competition getNextCompetition() {
        return this.nextCompetition;
    }

    public Competition getCompetitionBySimulation(int simID) {
        Competition[] comps = this.competitions;
        if (comps != null) {
            int i = 0;
            int n = comps.length;
            while (i < n) {
                if (comps[i].isSimulationIncluded(simID)) {
                    return comps[i];
                }
                ++i;
            }
        }
        return null;
    }

    public Competition getCompetitionByID(int competitionID) {
        Competition[] competitions = this.getCompetitions();
        int index = competitions == null ? -1 : Competition.indexOf(competitions, competitionID);
        return index < 0 ? null : competitions[index];
    }

    public Competition[] getCompetitions() {
        return this.competitions;
    }

    public void setCompetitionInfo(int competitionID, String newName, String scoreGenerator) {
        Competition competition = this.getCompetitionByID(competitionID);
        if (competition == null) {
            throw new IllegalArgumentException("competition " + competitionID + " not found");
        }
        if (scoreGenerator != null) {
            try {
                ScoreGenerator scoreGenerator2 = (ScoreGenerator)Class.forName(scoreGenerator).newInstance();
            }
            catch (ThreadDeath e) {
                throw e;
            }
            catch (Throwable e) {
                throw (IllegalArgumentException)new IllegalArgumentException("could not create score generator of type '" + scoreGenerator + '\'').initCause(e);
            }
        }
        competition.setName(newName);
        competition.setScoreClassName(scoreGenerator);
        try {
            DBMatcher dbm = new DBMatcher();
            dbm.setInt("id", competition.getID());
            dbm.setLimit(1);
            DBObject object = new DBObject();
            object.setString("name", newName);
            object.setString("scoreclass", scoreGenerator);
            this.competitionTable.update(dbm, object);
            this.competitionTable.flush();
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "could not update info for competition '" + competition.getName() + "' in database", e);
        }
    }

    public void scheduleCompetition(CompetitionSchedule schedule) {
        SimConnection connection = this.simConnection;
        if (connection == null) {
            throw new IllegalStateException("no connection with aa server");
        }
        SimServer simServer = this;
        synchronized (simServer) {
            int competitionID = this.lastCompetitionID + 1;
            schedule.setID(competitionID);
            schedule.validate();
            Competition competition = new Competition(competitionID, schedule.getName());
            competition.setStartWeight(schedule.getStartWeight());
            competition.setFlags(schedule.getFlags());
            competition.setScoreClassName(schedule.getScoreClassName());
            int[] participants = schedule.getParticipants();
            int parentID = schedule.getParentCompetitionID();
            if (parentID > 0) {
                Competition parentCompetition = this.getCompetitionByID(parentID);
                if (parentCompetition == null) {
                    throw new IllegalArgumentException("parent competition " + parentID + " not found");
                }
                if (parentCompetition.isParentCompetition(competition)) {
                    throw new IllegalStateException("circular competition chain");
                }
                if (parentCompetition.getStartTime() >= schedule.getStartTime()) {
                    throw new IllegalStateException("parent competition must start before child competition");
                }
                competition.setParentCompetitionID(parentCompetition.getID());
                competition.setParentCompetition(parentCompetition);
            }
            int i = 0;
            int n = participants.length;
            while (i < n) {
                String userName = this.infoServer.getUserName(participants[i]);
                if (userName == null) {
                    throw new IllegalArgumentException("participant " + participants[i] + " not found");
                }
                CompetitionParticipant cp = new CompetitionParticipant(participants[i], userName);
                competition.addParticipant(cp);
                ++i;
            }
            this.pendingCompetitions = (Competition[])ArrayUtils.add(Competition.class, this.pendingCompetitions, competition);
            this.setStateTable("lastCompetitionID", ++this.lastCompetitionID, null, null);
        }
        connection.scheduleCompetition(schedule);
    }

    public void addTimeReservation(long startTime, int lengthInMillis) {
        SimConnection connection = this.simConnection;
        if (connection == null) {
            throw new IllegalStateException("no connection with aa server");
        }
        connection.addTimeReservation(startTime, lengthInMillis);
    }

    public synchronized void removeCompetition(int competitionID) {
        Competition[] competitions = this.getCompetitions();
        if (competitions != null) {
            int i = 0;
            int n = competitions.length;
            while (i < n) {
                if (competitions[i].getParentCompetitionID() == competitionID) {
                    throw new IllegalArgumentException("can not remove parent competition " + competitionID + " of child competition " + competitions[i].getName());
                }
                ++i;
            }
        }
        this.removeCompetition(competitionID, true);
    }

    public synchronized void scratchSimulation(int simulationID) {
        if (simulationID <= 0) {
            throw new IllegalArgumentException("illegal simulation id: " + simulationID);
        }
        DBMatcher dbm = new DBMatcher();
        dbm.setInt("simid", simulationID);
        dbm.setLimit(1);
        DBResult res = this.playedTable.select(dbm);
        if (!res.next()) {
            res.close();
            throw new IllegalArgumentException("simulation " + simulationID + " not found");
        }
        int uniqueSimulationID = res.getInt("id");
        int flags = res.getInt("flags");
        res.close();
        if ((flags & 16) != 0) {
            throw new IllegalArgumentException("simulation already scratched!");
        }
        DBObject object = new DBObject();
        object.setInt("flags", flags | 16);
        this.playedTable.update(dbm, object);
        this.playedTable.flush();
        log.info("scratching simulation " + simulationID + ": flag set to scratched");
        int[] crParticipantIDs = new int[10];
        int[] crFlags = new int[10];
        double[] crScore = new double[10];
        double[] crWeight = new double[10];
        int competitionID = -1;
        int count = 0;
        dbm.clear();
        dbm.setInt("id", uniqueSimulationID);
        res = this.competitionResultTable.select(dbm);
        while (res.next()) {
            if (count == crParticipantIDs.length) {
                crParticipantIDs = ArrayUtils.setSize(crParticipantIDs, count + 10);
                crFlags = ArrayUtils.setSize(crFlags, count + 10);
                crScore = ArrayUtils.setSize(crScore, count + 10);
                crWeight = ArrayUtils.setSize(crWeight, count + 10);
            }
            if (competitionID < 0) {
                competitionID = res.getInt("competition");
            }
            crParticipantIDs[count] = res.getInt("participantid");
            crFlags[count] = res.getInt("flags");
            crScore[count] = res.getDouble("score");
            crWeight[count] = res.getDouble("weight");
            ++count;
        }
        res.close();
        dbm.clear();
        dbm.setInt("id", uniqueSimulationID);
        dbm.setLimit(1);
        object.clear();
        int i = 0;
        while (i < count) {
            dbm.setInt("participantid", crParticipantIDs[i]);
            object.setInt("flags", crFlags[i] | 16);
            this.competitionResultTable.update(dbm, object);
            log.info("scratching simulation " + simulationID + ": flag set to scratched in competition result for " + "participant " + crParticipantIDs[i]);
            ++i;
        }
        this.competitionResultTable.flush();
        Competition competition = this.getCompetitionBySimulation(uniqueSimulationID);
        if (competition != null) {
            dbm.clear();
            dbm.setInt("competition", competition.getID());
            dbm.setLimit(1);
            object.clear();
            int i2 = 0;
            while (i2 < count) {
                CompetitionParticipant cp = competition.getParticipantByID(crParticipantIDs[i2]);
                if (cp != null) {
                    cp.removeScore(simulationID, crScore[i2], crWeight[i2], crScore[i2] == 0.0 || (crFlags[i2] & 32) != 0);
                    dbm.setInt("participantid", crParticipantIDs[i2]);
                    object.setDouble("score", cp.getTotalScore());
                    object.setDouble("wscore", cp.getTotalWeightedScore());
                    object.setInt("gamesplayed", cp.getGamesPlayed());
                    object.setInt("zgamesplayed", cp.getZeroGamesPlayed());
                    object.setDouble("wgamesplayed", cp.getWeightedGamesPlayed());
                    object.setDouble("zwgamesplayed", cp.getZeroWeightedGamesPlayed());
                    if (this.competitionParticipantTable.update(dbm, object) == 0) {
                        log.severe("scratching simulation " + simulationID + ": failed to update scores for " + cp.getName() + " in competition " + competition.getName());
                    } else {
                        log.info("scratching simulation " + simulationID + ": updated score for " + cp.getName() + " in competition " + competition.getName());
                    }
                } else {
                    log.severe("scratching simulation " + simulationID + ": failed to update scores for " + crParticipantIDs[i2] + " in competition " + competition.getName() + " (participants not loaded?)");
                }
                ++i2;
            }
            this.competitionParticipantTable.flush();
        } else if (competitionID >= 0) {
            dbm.clear();
            dbm.setInt("competition", competitionID);
            dbm.setLimit(1);
            int i3 = 0;
            while (i3 < count) {
                dbm.setInt("participantid", crParticipantIDs[i3]);
                res = this.competitionParticipantTable.select(dbm);
                if (res.next()) {
                    object.clear();
                    object.setDouble("score", res.getDouble("score") - crScore[i3]);
                    object.setDouble("wscore", res.getDouble("wscore") - crScore[i3] * crWeight[i3]);
                    object.setInt("gamesplayed", res.getInt("gamesplayed") - 1);
                    if (crScore[i3] == 0.0 || (crFlags[i3] & 32) != 0) {
                        object.setInt("zgamesplayed", res.getInt("zgamesplayed") - 1);
                    }
                    object.setDouble("wgamesplayed", res.getDouble("wgamesplayed") - crWeight[i3]);
                    if (crScore[i3] == 0.0 || (crFlags[i3] & 32) != 0) {
                        object.setDouble("zwgamesplayed", res.getDouble("zwgamesplayed") - crWeight[i3]);
                    }
                    if (this.competitionParticipantTable.update(dbm, object) > 0) {
                        log.info("scratching simulation " + simulationID + ": updated score for " + crParticipantIDs[i3] + " in competition " + competitionID);
                    } else {
                        log.severe("scratching simulation " + simulationID + ": failed to update scores for " + crParticipantIDs[i3] + " in non-loaded competition " + competitionID);
                    }
                }
                res.close();
                ++i3;
            }
            this.competitionParticipantTable.flush();
        } else {
            log.info("scratching simulation " + simulationID + ": no competition for simulation");
        }
    }

    public String getUserName(SimulationInfo info, int userID) {
        if (userID < 0) {
            int index;
            String[] names = this.currentNames;
            if (names != null && info == this.currentSimulation && info != null && (index = info.indexOfParticipant(userID)) >= 0 && index < names.length && names[index] != null) {
                return names[index];
            }
            return "dummy" + (userID + 1);
        }
        return this.infoServer.getUserName(userID);
    }

    public synchronized SimulationInfo[] getComingSimulations() {
        if (this.comingQueue.size() == 0) {
            return null;
        }
        SimulationInfo[] infos = this.comingCache;
        if (infos == null) {
            infos = this.comingCache = (SimulationInfo[])this.comingQueue.toArray(new SimulationInfo[this.comingQueue.size()]);
        }
        return infos;
    }

    public String[] getSimulationTypes() {
        return null;
    }

    public String getSimulationTypeName(String type) {
        return type;
    }

    public int getLastPlayedSimulationID() {
        return this.lastPlayedSimulationID;
    }

    public int getLastFinishedCompetitionID() {
        return this.lastFinishedCompetitionID;
    }

    public void setLastFinishedCompetitionID(int competitionID) {
        if (this.lastFinishedCompetitionID != competitionID) {
            int oldID = this.lastFinishedCompetitionID;
            this.lastFinishedCompetitionID = competitionID;
            this.setStateTable("lastFinishedCompetitionID", competitionID, null, null);
            if (oldID > competitionID) {
                SimServer simServer = this;
                synchronized (simServer) {
                    this.loadCompetitions(true);
                }
            }
            SimServer simServer = this;
            synchronized (simServer) {
                Competition[] competitions = this.getCompetitions();
                if (competitions != null) {
                    int i = 0;
                    int n = competitions.length;
                    while (i < n) {
                        if (competitions[i].getID() <= this.lastFinishedCompetitionID) {
                            this.removeCompetition(competitions[i].getID(), false);
                        }
                        ++i;
                    }
                }
            }
        }
    }

    public void createSimulation(String type, String params) {
        SimConnection connection = this.simConnection;
        if (connection != null) {
            connection.createSimulation(type, params);
        }
    }

    public void removeSimulation(int uniqueSimID) {
        SimConnection connection = this.simConnection;
        if (connection == null) {
            throw new IllegalStateException("no connection with aa server");
        }
        SimServer simServer = this;
        synchronized (simServer) {
            int i = 0;
            int n = this.comingCompetitions.size();
            while (i < n) {
                Competition competition = (Competition)this.comingCompetitions.get(i);
                if (competition.isSimulationIncluded(uniqueSimID)) {
                    throw new IllegalStateException("can not remove simulation " + uniqueSimID + " (part of competition " + competition.getName() + ')');
                }
                ++i;
            }
        }
        connection.removeSimulation(uniqueSimID);
    }

    public void joinSimulation(int uniqueSimID, int agentID, String role) {
        SimConnection connection = this.simConnection;
        if (connection != null) {
            connection.joinSimulation(uniqueSimID, agentID, role);
        }
    }

    public String getServerMessage() {
        return this.serverMessage;
    }

    public void setServerMessage(String serverMessage) {
        if (serverMessage == null) {
            if (this.serverMessage != null) {
                this.serverMessage = null;
                new File(this.serverMessageFile).delete();
            }
        } else if (!serverMessage.equals(this.serverMessage)) {
            this.serverMessage = serverMessage;
            this.saveFile(this.serverMessageFile, serverMessage);
            this.infoServer.serverMessageChanged(this);
        }
    }

    public boolean isWebJoinActive() {
        return true;
    }

    public int getMaxAgentScheduled() {
        return this.maxAgentScheduled;
    }

    public void setMaxAgentScheduled(int max) {
        this.maxAgentScheduled = max;
    }

    public void generateResults(int simulationID, boolean addToTable) {
        this.generateResults(simulationID, addToTable, false);
    }

    public void generateResults(int simulationID, boolean addToTable, boolean regenerateResults) {
        String path = String.valueOf(this.resultsPath) + simulationID + File.separatorChar;
        String filename = String.valueOf(path) + "game.slg.gz";
        LogReader reader = null;
        try {
            try {
                Competition competition;
                File destPath = new File(path);
                if (!destPath.exists() && !destPath.mkdirs()) {
                    throw new IOException("could not create simulation result directory '" + path + "' for simulation " + simulationID);
                }
                if (!regenerateResults) {
                    this.archiveSimulationLog(simulationID, filename);
                }
                if ((reader = new LogReader(this.getSimulationLogStream(filename))).getSimulationID() != simulationID) {
                    throw new IOException("log file " + filename + " did not contain log for simulation " + simulationID);
                }
                String simType = reader.getSimulationType();
                InfoManager infoManager = this.infoServer.getInfoManager(simType);
                if (infoManager == null) {
                    throw new IOException("could not find information manager for simulation " + simulationID + " of type " + simType);
                }
                ResultManager resultManager = infoManager.createResultManager(simType);
                resultManager.init(this, addToTable, "game.slg.gz");
                if (this.playedTable != null) {
                    try {
                        DBObject object = new DBObject();
                        object.setInt("id", reader.getUniqueID());
                        object.setInt("simid", simulationID);
                        object.setString("type", simType);
                        object.setLong("starttime", reader.getStartTime());
                        object.setInt("length", reader.getSimulationLength() / 1000);
                        object.setInt("flags", 0);
                        this.playedTable.insert(object);
                        this.playedTable.flush();
                    }
                    catch (Exception e) {
                        log.log(Level.SEVERE, "could not store results for simulation " + simulationID, e);
                    }
                }
                resultManager.generateResult(reader, path);
                reader.close();
                if (!regenerateResults) {
                    this.simConnection.resultsGenerated(simulationID);
                }
                if ((competition = this.getCompetitionBySimulation(reader.getUniqueID())) != null) {
                    if (!competition.hasSimulationID()) {
                        this.checkCompetitionSimulationID(reader.getUniqueID(), reader.getSimulationID());
                    }
                    this.generateCompetitionResults(competition, reader, this.urlGamePath);
                }
            }
            catch (FileNotFoundException e) {
                log.log(Level.SEVERE, "could not find log for simulation " + simulationID, e);
                if (reader != null) {
                    reader.close();
                }
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "could not generate results for simulation " + simulationID, e);
                if (reader != null) {
                    reader.close();
                }
            }
        }
        finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private void generateCompetitionResults(Competition competition, LogReader reader, String urlGamePath) {
        try {
            String path = String.valueOf(this.resultsPath) + "competition" + File.separatorChar + competition.getID() + File.separatorChar;
            File destPath = new File(path);
            if (!destPath.exists() && !destPath.mkdirs()) {
                throw new IOException("could not create competition result directory '" + path + "' for competition " + competition.getName() + " (" + competition.getID() + ')');
            }
            ParticipantInfo[] participants = reader.getParticipants();
            if (participants != null) {
                int i = 0;
                int n = participants.length;
                while (i < n) {
                    ParticipantInfo info = participants[i];
                    CompetitionParticipant cp = competition.getParticipantByID(info.getUserID());
                    if (cp != null) {
                        StatPageGenerator.generateStatisticsPage(this.competitionResultTable, path, urlGamePath, competition, cp, true);
                    }
                    ++i;
                }
            }
            ScoreGenerator gen = null;
            String scoreClassName = competition.getScoreClassName();
            if (scoreClassName != null) {
                try {
                    gen = (ScoreGenerator)Class.forName(scoreClassName).newInstance();
                }
                catch (Throwable t) {
                    log.log(Level.SEVERE, "could not create score generator of type " + scoreClassName, t);
                }
            }
            if (gen == null) {
                gen = new DefaultScoreGenerator();
            }
            gen.init(this.getServerName(), path);
            gen.createScoreTable(competition, reader.getSimulationID());
        }
        catch (ThreadDeath e) {
            throw e;
        }
        catch (Throwable e) {
            log.log(Level.SEVERE, "could not generate score page", e);
        }
    }

    public void generateCompetitionResults(int competitionID) throws IOException {
        Competition competition = this.getCompetitionByID(competitionID);
        if (competition == null) {
            throw new IllegalArgumentException("competition " + competitionID + " not found in memory");
        }
        String path = String.valueOf(this.resultsPath) + "competition" + File.separatorChar + competition.getID() + File.separatorChar;
        File destPath = new File(path);
        if (!destPath.exists() && !destPath.mkdirs()) {
            throw new IOException("could not create competition result directory '" + path + "' for competition " + competition.getName() + " (" + competition.getID() + ')');
        }
        int i = 0;
        int n = competition.getParticipantCount();
        while (i < n) {
            CompetitionParticipant cp = competition.getParticipant(i);
            StatPageGenerator.generateStatisticsPage(this.competitionResultTable, path, this.urlGamePath, competition, cp, true);
            ++i;
        }
        ScoreGenerator gen = null;
        String scoreClassName = competition.getScoreClassName();
        if (scoreClassName != null) {
            try {
                gen = (ScoreGenerator)Class.forName(scoreClassName).newInstance();
            }
            catch (Throwable t) {
                log.log(Level.SEVERE, "could not create score generator of type " + scoreClassName, t);
            }
        }
        if (gen == null) {
            gen = new DefaultScoreGenerator();
        }
        gen.init(this.getServerName(), path);
        gen.createScoreTable(competition, this.lastPlayedSimulationID);
    }

    private InputStream getServerLogStream(int simulationID) throws IOException {
        return new FileInputStream("sim" + simulationID + ".slg");
    }

    private InputStream getSimulationLogStream(String filename) throws IOException {
        boolean gzip = filename.endsWith(".gz");
        try {
            InputStream input = new FileInputStream(filename);
            return gzip ? new GZIPInputStream(input) : input;
        }
        catch (FileNotFoundException e) {
            return gzip ? new FileInputStream(filename.substring(0, filename.length() - 3)) : new GZIPInputStream(new FileInputStream(String.valueOf(filename) + ".gz"));
        }
    }

    private void archiveSimulationLog(int simulationID, String targetFile) throws IOException {
        InputStream input = this.getServerLogStream(simulationID);
        try {
            GZIPOutputStream output = new GZIPOutputStream(new FileOutputStream(targetFile));
            try {
                int n;
                byte[] buffer = new byte[4096];
                while ((n = input.read(buffer)) > 0) {
                    output.write(buffer, 0, n);
                }
            }
            finally {
                output.close();
            }
        }
        finally {
            input.close();
        }
    }

    final void addSimulationToHistory(LogReader logReader, ParticipantInfo[] participants, String[] participantColors) {
        int simulationID = logReader.getSimulationID();
        try {
            String filename = String.valueOf(this.resultsPath) + this.simTablePrefix + ((simulationID - 1) / this.simulationsPerPage + 1) + ".html";
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));
            log.finest("adding simulation " + simulationID + " to " + filename);
            try {
                long startTime = logReader.getStartTime();
                int length = logReader.getSimulationLength() / 1000;
                int minutes = length / 60;
                int seconds = length % 60;
                out.print("<tr><td>&nbsp;<a href=\"" + simulationID + "/\">" + simulationID + "</a>&nbsp;</td><td>" + InfoServer.getServerTimeAsString(startTime) + " (" + minutes + "&nbsp;min" + (seconds > 0 ? new StringBuilder("&nbsp;").append(seconds).append("&nbsp;sec").toString() : "") + ")</td><td>");
                if (participants != null) {
                    String currentColor = null;
                    boolean isEm = false;
                    int i = 0;
                    int n = participants.length;
                    while (i < n) {
                        ParticipantInfo info = participants[i];
                        if (i > 0) {
                            out.print(' ');
                        }
                        if (participantColors != null && currentColor != participantColors[i]) {
                            if (isEm) {
                                out.print("</em>");
                                isEm = false;
                            }
                            currentColor = this.setHtmlColor(out, currentColor, participantColors[i]);
                        }
                        if (isEm != info.isBuiltinAgent()) {
                            out.print(isEm ? "</em>" : "<em>");
                            isEm = !isEm;
                        }
                        out.print(info.getName());
                        ++i;
                    }
                    if (isEm) {
                        out.print("</em>");
                    }
                    this.setHtmlColor(out, currentColor, null);
                }
                out.print(" (<a href=\"" + simulationID + '/' + "game.slg.gz" + "\">data</a>)");
                out.println("</td></tr>");
            }
            finally {
                out.close();
            }
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "could not add simulation " + simulationID + " to simulation table", e);
        }
    }

    private String setHtmlColor(PrintWriter out, String currentColor, String newColor) {
        if (currentColor == newColor) {
            return currentColor;
        }
        if (currentColor != null) {
            out.print("</font>");
        }
        if (newColor != null) {
            out.print("<font color='" + newColor + "'>");
        }
        return newColor;
    }

    final void addSimulationResult(LogReader logReader, ParticipantInfo[] participants, long[] scores, boolean update) {
        Competition competition;
        if (participants == null) {
            return;
        }
        int simulationUniqID = logReader.getUniqueID();
        DBObject object = null;
        if (this.resultTable != null) {
            try {
                object = new DBObject();
                object.setInt("id", simulationUniqID);
                int i = 0;
                int n = participants.length;
                while (i < n) {
                    ParticipantInfo info = participants[i];
                    if (!info.isBuiltinAgent()) {
                        object.setInt("participantid", info.getUserID());
                        object.setInt("participantrole", info.getRole());
                        object.setDouble("score", scores[i]);
                        this.resultTable.insert(object);
                    }
                    ++i;
                }
                this.resultTable.flush();
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "could not store results for simulation " + logReader.getSimulationID(), e);
            }
        }
        if (this.competitionResultTable != null && (competition = this.getCompetitionBySimulation(simulationUniqID)) != null) {
            try {
                int n;
                int i;
                boolean lowestScoreForZero = (competition.getFlags() & 64) != 0;
                double weight = competition.getWeight(simulationUniqID);
                long lowestScore = 0;
                if (object == null) {
                    object = new DBObject();
                } else {
                    object.clear();
                }
                if (lowestScoreForZero) {
                    i = 0;
                    n = scores.length;
                    while (i < n) {
                        if (scores[i] < lowestScore) {
                            lowestScore = scores[i];
                        }
                        ++i;
                    }
                }
                if (!update) {
                    DBMatcher dbm = new DBMatcher();
                    dbm.setInt("competition", competition.getID());
                    int i2 = 0;
                    int n2 = participants.length;
                    while (i2 < n2) {
                        CompetitionParticipant cp;
                        ParticipantInfo info = participants[i2];
                        if (!info.isBuiltinAgent() && (cp = competition.getParticipantByID(info.getUserID())) != null) {
                            boolean isZeroGame = scores[i2] == 0;
                            dbm.setInt("participantid", info.getUserID());
                            cp.addScore(logReader.getSimulationID(), lowestScoreForZero && isZeroGame ? lowestScore : scores[i2], weight, isZeroGame);
                            object.setDouble("score", cp.getTotalScore());
                            object.setDouble("wscore", cp.getTotalWeightedScore());
                            object.setInt("gamesplayed", cp.getGamesPlayed());
                            object.setInt("zgamesplayed", cp.getZeroGamesPlayed());
                            object.setDouble("wgamesplayed", cp.getWeightedGamesPlayed());
                            object.setDouble("zwgamesplayed", cp.getZeroWeightedGamesPlayed());
                            if (this.competitionParticipantTable.update(dbm, object) == 0) {
                                log.severe("failed to update scores for simulation " + logReader.getSimulationID() + " in competition " + competition.getName() + " for " + cp.getName());
                            }
                        }
                        ++i2;
                    }
                    this.competitionParticipantTable.flush();
                    object.clear();
                }
                object.setInt("id", simulationUniqID);
                object.setInt("simid", logReader.getSimulationID());
                object.setInt("competition", competition.getID());
                i = 0;
                n = participants.length;
                while (i < n) {
                    ParticipantInfo info = participants[i];
                    if (!info.isBuiltinAgent()) {
                        boolean isZeroGame = scores[i] == 0;
                        object.setInt("participantid", info.getUserID());
                        object.setInt("participantrole", info.getRole());
                        object.setDouble("score", lowestScoreForZero && isZeroGame ? lowestScore : scores[i]);
                        object.setDouble("weight", weight);
                        object.setInt("flags", isZeroGame ? 32 : 0);
                        this.competitionResultTable.insert(object);
                    }
                    ++i;
                }
                this.competitionResultTable.flush();
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "could not store results for simulation " + logReader.getSimulationID(), e);
            }
        }
    }

    final void addSimulationResult(LogReader logReader, ParticipantInfo[] participants, double[] scores, boolean update) {
        Competition competition;
        if (participants == null) {
            return;
        }
        int simulationUniqID = logReader.getUniqueID();
        DBObject object = null;
        if (this.resultTable != null) {
            try {
                object = new DBObject();
                object.setInt("id", simulationUniqID);
                int i = 0;
                int n = participants.length;
                while (i < n) {
                    ParticipantInfo info = participants[i];
                    if (!info.isBuiltinAgent()) {
                        object.setInt("participantid", info.getUserID());
                        object.setInt("participantrole", info.getRole());
                        object.setDouble("score", scores[i]);
                        this.resultTable.insert(object);
                    }
                    ++i;
                }
                this.resultTable.flush();
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "could not store results for simulation " + logReader.getSimulationID(), e);
            }
        }
        if (this.competitionResultTable != null && (competition = this.getCompetitionBySimulation(simulationUniqID)) != null) {
            try {
                int n;
                int i;
                boolean lowestScoreForZero = (competition.getFlags() & 64) != 0;
                double weight = competition.getWeight(simulationUniqID);
                double lowestScore = 0.0;
                if (object == null) {
                    object = new DBObject();
                } else {
                    object.clear();
                }
                if (lowestScoreForZero) {
                    i = 0;
                    n = scores.length;
                    while (i < n) {
                        if (scores[i] < lowestScore) {
                            lowestScore = scores[i];
                        }
                        ++i;
                    }
                }
                if (!update) {
                    DBMatcher dbm = new DBMatcher();
                    dbm.setInt("competition", competition.getID());
                    int i2 = 0;
                    int n2 = participants.length;
                    while (i2 < n2) {
                        CompetitionParticipant cp;
                        ParticipantInfo info = participants[i2];
                        if (!info.isBuiltinAgent() && (cp = competition.getParticipantByID(info.getUserID())) != null) {
                            boolean isZeroGame = scores[i2] == 0.0;
                            dbm.setInt("participantid", info.getUserID());
                            cp.addScore(logReader.getSimulationID(), lowestScoreForZero && isZeroGame ? lowestScore : scores[i2], weight, isZeroGame);
                            object.setDouble("score", cp.getTotalScore());
                            object.setDouble("wscore", cp.getTotalWeightedScore());
                            object.setInt("gamesplayed", cp.getGamesPlayed());
                            object.setInt("zgamesplayed", cp.getZeroGamesPlayed());
                            object.setDouble("wgamesplayed", cp.getWeightedGamesPlayed());
                            object.setDouble("zwgamesplayed", cp.getZeroWeightedGamesPlayed());
                            if (this.competitionParticipantTable.update(dbm, object) == 0) {
                                log.severe("failed to update scores for simulation " + logReader.getSimulationID() + " in competition " + competition.getName() + " for " + cp.getName());
                            }
                        }
                        ++i2;
                    }
                    this.competitionParticipantTable.flush();
                    object.clear();
                }
                object.setInt("id", simulationUniqID);
                object.setInt("simid", logReader.getSimulationID());
                object.setInt("competition", competition.getID());
                i = 0;
                n = participants.length;
                while (i < n) {
                    ParticipantInfo info = participants[i];
                    if (!info.isBuiltinAgent()) {
                        boolean isZeroGame = scores[i] == 0.0;
                        object.setInt("participantid", info.getUserID());
                        object.setInt("participantrole", info.getRole());
                        object.setDouble("score", lowestScoreForZero && isZeroGame ? lowestScore : scores[i]);
                        object.setDouble("weight", weight);
                        object.setInt("flags", isZeroGame ? 32 : 0);
                        this.competitionResultTable.insert(object);
                    }
                    ++i;
                }
                this.competitionResultTable.flush();
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "could not store results for simulation " + logReader.getSimulationID(), e);
            }
        }
    }

    private void setupStateTable(Database database) {
        this.stateTable = database.getTable("state");
        if (this.stateTable == null) {
            this.stateTable = database.createTable("state");
            this.stateTable.createField("name", 4, 32, 21);
            this.stateTable.createField("value", 0, 32, 0);
            DBObject o = new DBObject();
            o.setString("name", "lastSimulationID");
            o.setInt("value", this.lastSimulationID);
            this.stateTable.insert(o);
            o.setString("name", "lastUniqueSimulationID");
            o.setInt("value", this.lastUniqueSimulationID);
            this.stateTable.insert(o);
            o.setString("name", "lastPlayedSimulationID");
            o.setInt("value", this.lastPlayedSimulationID);
            this.stateTable.insert(o);
            o.setString("name", "lastCompetitionID");
            o.setInt("value", this.lastCompetitionID);
            this.stateTable.insert(o);
            o.setString("name", "lastFinishedCompetitionID");
            o.setInt("value", this.lastFinishedCompetitionID);
            this.stateTable.insert(o);
            this.stateTable.flush();
        } else {
            DBMatcher dbm = new DBMatcher();
            dbm.setString("name", "lastSimulationID");
            DBResult res = this.stateTable.select(dbm);
            if (res.next()) {
                this.lastSimulationID = res.getInt("value");
            }
            res.close();
            dbm.setString("name", "lastUniqueSimulationID");
            res = this.stateTable.select(dbm);
            if (res.next()) {
                this.lastUniqueSimulationID = res.getInt("value");
            }
            res.close();
            dbm.setString("name", "lastPlayedSimulationID");
            res = this.stateTable.select(dbm);
            if (res.next()) {
                this.lastPlayedSimulationID = res.getInt("value");
            }
            res.close();
            dbm.setString("name", "lastCompetitionID");
            res = this.stateTable.select(dbm);
            if (res.next()) {
                this.lastCompetitionID = res.getInt("value");
            }
            res.close();
            dbm.setString("name", "lastFinishedCompetitionID");
            res = this.stateTable.select(dbm);
            if (res.next()) {
                this.lastFinishedCompetitionID = res.getInt("value");
            }
            res.close();
        }
    }

    private void setStateTable(String name, int value, DBMatcher dbm, DBObject object) {
        try {
            if (dbm == null) {
                dbm = new DBMatcher();
            }
            if (object == null) {
                object = new DBObject();
            }
            dbm.setString("name", name);
            dbm.setLimit(1);
            object.setInt("value", value);
            int updateCount = this.stateTable.update(dbm, object);
            if (updateCount == 0) {
                object.setString("name", name);
                this.stateTable.insert(object);
            }
            this.stateTable.flush();
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "could not set '" + name + "' to " + value, e);
        }
    }

    public static int indexOf(SimServer[] array, String serverName) {
        if (array != null) {
            int i = 0;
            int n = array.length;
            while (i < n) {
                if (serverName.equals(array[i].serverName)) {
                    return i;
                }
                ++i;
            }
        }
        return -1;
    }

}

