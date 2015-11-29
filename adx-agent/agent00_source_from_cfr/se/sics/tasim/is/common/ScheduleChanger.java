/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.is.common;

import com.botbox.util.ArrayQueue;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.isl.db.DBMatcher;
import se.sics.isl.db.DBResult;
import se.sics.isl.db.DBTable;
import se.sics.isl.db.Database;
import se.sics.isl.util.ArgumentManager;
import se.sics.isl.util.ConfigManager;
import se.sics.isl.util.LogFormatter;
import se.sics.tasim.is.AgentLookup;
import se.sics.tasim.is.SimulationInfo;
import se.sics.tasim.is.common.Competition;
import se.sics.tasim.is.common.CompetitionParticipant;
import se.sics.tasim.is.common.DatabaseUtils;

public class ScheduleChanger {
    private static final String DEFAULT_CONFIG = "schedule.conf";
    private static final String CONF = "is.";
    private static final Logger log = Logger.getLogger(ScheduleChanger.class.getName());

    public static void main(String[] args) throws IOException {
        String serverName;
        String theAgentName;
        long scheduleStartTime = System.currentTimeMillis();
        ArgumentManager config = new ArgumentManager("ScheduleChanger", args);
        config.addOption("config", "configfile", "set the config file to use");
        config.addOption("is.database.sql.url", "jdbc:mysql://localhost:3306/mysql", "set the database url");
        config.addOption("log.consoleLevel", "level", "set the console log level");
        config.addOption("n", "Do not change any files or access any databases.");
        config.addHelp("h", "show this help message");
        config.addHelp("help");
        config.validateArguments();
        String configFile = config.getArgument("config", "schedule.conf");
        try {
            config.loadConfiguration(configFile);
            config.removeArgument("config");
        }
        catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            config.usage(1);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        String agentToAdd = config.getProperty("agentToAdd");
        String agentToRemove = config.getProperty("agentToRemove");
        if (agentToAdd != null) {
            if (agentToRemove != null) {
                throw new IllegalArgumentException("both agent to add and agent to remove specified");
            }
            theAgentName = agentToAdd;
        } else {
            if (agentToRemove == null) {
                throw new IllegalArgumentException("no agent specified");
            }
            theAgentName = agentToRemove;
        }
        int firstGame = config.getPropertyAsInt("firstGame", 0);
        int gamesToSchedule = config.getPropertyAsInt("gamesToSchedule", -1);
        if (gamesToSchedule <= 0 && agentToAdd != null) {
            throw new IllegalArgumentException("no games to schedule specified");
        }
        String databaseURL = config.getProperty("is.database.sql.url");
        boolean check = config.getPropertyAsBoolean("n", false);
        config.finishArguments();
        ScheduleChanger.setLogging(config);
        ScheduleChanger scoreGen = new ScheduleChanger();
        Random random = new Random();
        String serverConfigFileName = config.getProperty("competition.config");
        int competitionID = config.getPropertyAsInt("competition.id", 0);
        if (serverConfigFileName == null || competitionID <= 0) {
            throw new IllegalArgumentException("no server config or no competition id");
        }
        log.info("using server config '" + serverConfigFileName + "' for competition " + competitionID);
        ConfigManager serverConfig = new ConfigManager();
        serverConfig.loadConfiguration(serverConfigFileName);
        if (databaseURL != null) {
            serverConfig.setProperty("is.database.sql.url", databaseURL);
            serverConfig.setProperty("is.user.database.sql.url", databaseURL);
        }
        if ((serverName = serverConfig.getProperty("server.name")) == null) {
            throw new IllegalStateException("no server name for " + serverConfigFileName);
        }
        Database userDatabase = DatabaseUtils.createUserDatabase(serverConfig, "is.", null);
        DBTable userTable = userDatabase.getTable("users");
        if (userTable == null) {
            userDatabase.close();
            throw new IllegalStateException("could not find user database for  competition " + competitionID);
        }
        DBMatcher dbm2 = new DBMatcher();
        dbm2.setString("name", theAgentName);
        dbm2.setLimit(1);
        DBResult user = userTable.select(dbm2);
        if (!user.next()) {
            log.severe("Could not find user '" + theAgentName + "' in user db");
            return;
        }
        int pid = user.getInt("id");
        log.finer("Using user " + theAgentName + " as " + pid);
        CompetitionParticipant theAgent = new CompetitionParticipant(pid, theAgentName);
        user.close();
        Database database = DatabaseUtils.createDatabase(serverConfig, "is.");
        log.finer("DB Name: " + database.getName() + " class: " + database);
        Database serverDatabase = DatabaseUtils.createChildDatabase(serverConfig, "is.", serverName, database);
        log.finer("Server DB Name: " + serverDatabase.getName() + " class: " + serverDatabase);
        Competition competition = ScheduleChanger.loadCompetitionByParticipants(serverDatabase, userTable, competitionID);
        userDatabase.close();
        log.info("Competition has " + competition.getParticipantCount() + " participants");
        log.info("Competition starts at " + competition.getStartSimulationID() + " and ends at " + competition.getEndSimulationID());
        if (firstGame < competition.getStartSimulationID()) {
            firstGame = competition.getStartSimulationID();
        }
        int endGame = competition.getEndSimulationID();
        DBTable participantTable = serverDatabase.getTable("comingparticipants");
        String participantTableName = participantTable.getName();
        DBTable simulationTable = serverDatabase.getTable("comingsimulations");
        DBMatcher dbm = new DBMatcher();
        DBMatcher dbm22 = new DBMatcher();
        DBResult res = simulationTable.select(dbm);
        ArrayList<SimulationInfo> list = new ArrayList<SimulationInfo>();
        while (res.next()) {
            int id = res.getInt("id");
            int simID = res.getInt("simid");
            if (simID < 0 || simID < firstGame || simID > endGame) continue;
            String type = res.getString("type");
            String params = res.getString("params");
            long startTime = res.getLong("starttime");
            int length = res.getInt("length") * 1000;
            SimulationInfo info = new SimulationInfo(id, type, params, length);
            if (simID >= 0) {
                info.setSimulationID(simID);
            }
            info.setStartTime(startTime);
            dbm22.setInt("id", id);
            DBResult res2 = participantTable.select(dbm22);
            while (res2.next()) {
                info.addParticipant(res2.getInt("participantid"), res2.getInt("participantrole"));
            }
            res2.close();
            list.add(info);
        }
        res.close();
        log.info("found " + list.size() + " games");
        serverDatabase.close();
        database.close();
        if (agentToAdd != null) {
            Hashtable<Integer, Integer> pTable = new Hashtable<Integer, Integer>();
            while (gamesToSchedule > 0) {
                if (list.size() == 0) {
                    log.log(Level.SEVERE, "COULD NOT COMPLETELY CHANGE SCHEDULE!!!");
                    return;
                }
                boolean done = false;
                int index = random.nextInt(list.size());
                SimulationInfo info = (SimulationInfo)list.get(index);
                int plen = info.getParticipantCount();
                int[] p = new int[plen];
                int j = 0;
                int m = plen;
                while (j < m) {
                    p[j] = info.getParticipantID(j);
                    ++j;
                }
                while (plen > 0) {
                    int pindex = random.nextInt(plen);
                    Integer part = new Integer(p[pindex]);
                    if (pTable.get(part) == null) {
                        System.out.println("# " + info.getID() + " (" + info.getSimulationID() + ')' + " REPL " + p[pindex] + " with " + theAgent.getID() + "  (" + competition.getParticipantByID(p[pindex]).getName() + " with " + theAgentName + ')');
                        System.out.println("UPDATE " + participantTableName + " SET " + "participantid='" + theAgent.getID() + "' WHERE id='" + info.getID() + "' AND participantid='" + p[pindex] + "' LIMIT 1;");
                        pTable.put(part, part);
                        done = true;
                        break;
                    }
                    p[pindex] = p[--plen];
                    p[plen] = -1;
                }
                list.remove(index);
                if (!done) continue;
                --gamesToSchedule;
            }
        } else {
            String ia;
            Hashtable<String, String> ignoreTable = new Hashtable<String, String>();
            String[] ignoreAgents = config.getPropertyAsArray("ignoreAgents");
            if (ignoreAgents != null) {
                int i = 0;
                int n = ignoreAgents.length;
                while (i < n) {
                    ignoreTable.put(ignoreAgents[i], ignoreAgents[i]);
                    ++i;
                }
            }
            int i = 1;
            while ((ia = config.getProperty("ignoreAgent." + i)) != null) {
                ignoreTable.put(ia, ia);
                ++i;
            }
            ignoreTable.put(theAgentName, theAgentName);
            CompetitionParticipant[] participants = competition.getParticipants();
            if (participants == null || participants.length < 2) {
                log.severe("no participants or too few participants in competition");
                return;
            }
            AgentChooser chooser = new AgentChooser(participants, ignoreTable, random);
            int i2 = 0;
            int n = list.size();
            while (i2 < n) {
                SimulationInfo game = (SimulationInfo)list.get(i2);
                if (game.isParticipant(theAgent.getID())) {
                    CompetitionParticipant replacer = chooser.getNextParticipant(game);
                    if (replacer == null) {
                        log.severe("could not find a agent to replace removed agent in game " + game.getSimulationID());
                        return;
                    }
                    System.out.println("# " + game.getID() + " (" + game.getSimulationID() + ')' + " REPL " + theAgent.getID() + " with " + replacer.getID() + "  (" + theAgentName + " with " + replacer.getName() + ')');
                    System.out.println("UPDATE " + participantTableName + " SET " + "participantid='" + replacer.getID() + "' WHERE id='" + game.getID() + "' AND participantid='" + theAgent.getID() + "' LIMIT 1;");
                }
                ++i2;
            }
            System.out.println();
            System.out.println();
            System.out.println("Agents to ignore next time:");
            StringBuffer sb = null;
            int i3 = 0;
            int cnt = 0;
            int n2 = chooser.participants.length;
            while (i3 < n2) {
                if (chooser.participants[i3].count > 0) {
                    if (chooser.participants[i3].part.getName().indexOf(32) >= 0) {
                        System.out.println("ignoreAgent." + ++cnt + "=" + chooser.participants[i3].part.getName());
                    } else {
                        if (sb == null) {
                            sb = new StringBuffer();
                        } else {
                            sb.append(',');
                        }
                        sb.append(chooser.participants[i3].part.getName());
                    }
                }
                ++i3;
            }
            if (sb != null) {
                System.out.println("ignoreAgents=" + sb);
            }
            System.out.println();
            System.out.println("Agent counts (debug output)");
            i3 = 0;
            int n3 = chooser.participants.length;
            while (i3 < n3) {
                System.out.println("AGENT " + chooser.participants[i3].part.getName() + ": " + chooser.participants[i3].count);
                ++i3;
            }
        }
        log.info("Schedule change generated in " + (System.currentTimeMillis() - scheduleStartTime) + " msek");
    }

    private static void setLogging(ConfigManager config) {
        int consoleLevel = config.getPropertyAsInt("log.consoleLevel", 0);
        Level logLevel = LogFormatter.getLogLevel(consoleLevel);
        boolean showThreads = config.getPropertyAsBoolean("log.threads", false);
        Logger root = Logger.getLogger("");
        root.setLevel(logLevel);
        LogFormatter formatter = new LogFormatter();
        formatter.setAliasLevel(2);
        formatter.setShowingThreads(showThreads);
        LogFormatter.setConsoleLevel(logLevel);
        LogFormatter.setFormatterForAllHandlers(formatter);
    }

    private static Competition loadCompetitionByParticipants(Database serverDatabase, DBTable userTable, int competitionID) throws IOException {
        DBTable competitionParticipantTable;
        AgentLookup agentLookup = new AgentLookup();
        DBTable competitionTable = serverDatabase.getTable("competitions");
        Competition currentCompetition = ScheduleChanger.loadCompetition(userTable, agentLookup, competitionTable, competitionParticipantTable = serverDatabase.getTable("competitionparts"), competitionID);
        if (currentCompetition == null) {
            IOException ioe = new IOException("competition not found");
            log.log(Level.SEVERE, "could not find competition " + competitionID, ioe);
            throw ioe;
        }
        log.finer("loaded competition " + currentCompetition.getName());
        return currentCompetition;
    }

    private static void addScores(Competition targetComp, Competition source, boolean createParticipants) {
        CompetitionParticipant[] participants = source.getParticipants();
        if (participants == null) {
            log.warning("no participants found in competition " + source.getName());
        } else {
            int j = 0;
            int m = participants.length;
            while (j < m) {
                CompetitionParticipant cp = participants[j];
                CompetitionParticipant targetParticipant = targetComp.getParticipantByID(cp.getID());
                if (targetParticipant != null) {
                    targetParticipant.addScore(cp);
                } else if (createParticipants) {
                    targetParticipant = new CompetitionParticipant(cp.getID(), cp.getName());
                    targetComp.addParticipant(targetParticipant);
                    targetParticipant.addScore(cp);
                } else {
                    log.finer("ignoring parent participant " + cp.getName());
                }
                ++j;
            }
        }
    }

    private static Competition loadCompetition(DBTable userTable, AgentLookup agentLookup, DBTable competitionTable, DBTable competitionParticipantTable, int competitionID) {
        Competition theCompetition = null;
        DBMatcher dbm = new DBMatcher();
        dbm.setLimit(1);
        dbm.setInt("id", competitionID);
        DBResult res = competitionTable.select(dbm);
        if (res.next()) {
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
            Competition competition = new Competition(competitionID, name, startTime, endTime, startUniqueID, simulationCount, startWeight);
            if (startPublicID >= 0) {
                competition.setStartSimulationID(startPublicID);
            }
            if (scoreClass != null) {
                competition.setScoreClassName(scoreClass);
            }
            competition.setFlags(flags);
            DBMatcher dbm2 = new DBMatcher();
            dbm2.setInt("competition", competitionID);
            DBResult res2 = competitionParticipantTable.select(dbm2);
            while (res2.next()) {
                int pid = res2.getInt("participantid");
                String uname = agentLookup.getAgentName(pid);
                if (uname == null) {
                    DBMatcher userDbm = new DBMatcher();
                    userDbm.setInt("id", pid);
                    userDbm.setLimit(1);
                    DBResult user = userTable.select(userDbm);
                    if (user.next()) {
                        int userID = user.getInt("id");
                        int parentUserID = user.getInt("parent");
                        uname = user.getString("name");
                        String password = user.getString("password");
                        if (uname != null) {
                            log.finer("Adding user " + uname + " with id " + userID);
                            agentLookup.setUser(uname, password, userID, parentUserID);
                        }
                    }
                    user.close();
                }
                if (uname == null) {
                    log.warning("could not find user " + pid);
                    uname = "unknown";
                }
                CompetitionParticipant cp = new CompetitionParticipant(pid, uname);
                cp.setFlags(res2.getInt("flags"));
                competition.addParticipant(cp);
            }
            res2.close();
            theCompetition = competition;
        }
        res.close();
        return theCompetition;
    }

    private static class AgentChooser {
        public AgentInfo[] participants;
        private ArrayQueue priority = new ArrayQueue();
        private ArrayQueue queue = new ArrayQueue();

        public AgentChooser(CompetitionParticipant[] p, Hashtable agentsToIgnore, Random random) {
            this.participants = new AgentInfo[p.length];
            int i = 0;
            int n = this.participants.length;
            while (i < n) {
                this.participants[i] = new AgentInfo(p[i]);
                ++i;
            }
            i = 0;
            n = this.participants.length - 1;
            while (i < n) {
                int index = i + random.nextInt(n + 1 - i);
                AgentInfo cp = this.participants[i];
                this.participants[i] = this.participants[index];
                this.participants[index] = cp;
                ++i;
            }
            i = 0;
            n = this.participants.length;
            while (i < n) {
                if (agentsToIgnore.get(this.participants[i].part.getName()) == null) {
                    this.queue.add(this.participants[i]);
                }
                ++i;
            }
        }

        public CompetitionParticipant getNextParticipant(SimulationInfo game) {
            AgentInfo info;
            int i = 0;
            int n = this.priority.size();
            while (i < n) {
                info = (AgentInfo)this.priority.get(i);
                if (!game.isParticipant(info.part.getID())) {
                    this.priority.remove(i);
                    ++info.count;
                    return info.part;
                }
                ++i;
            }
            i = 0;
            n = this.queue.size();
            while (i < n) {
                info = (AgentInfo)this.queue.remove(0);
                this.queue.add(info);
                if (!game.isParticipant(info.part.getID())) {
                    ++info.count;
                    return info.part;
                }
                this.priority.add(info);
                ++i;
            }
            return null;
        }
    }

    private static class AgentInfo {
        public CompetitionParticipant part;
        public int count;

        public AgentInfo(CompetitionParticipant part) {
            this.part = part;
        }
    }

}

