/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.is.common;

import com.botbox.util.ArrayUtils;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.isl.db.DBMatcher;
import se.sics.isl.db.DBResult;
import se.sics.isl.db.DBTable;
import se.sics.isl.db.Database;
import se.sics.isl.util.ArgumentManager;
import se.sics.isl.util.ConfigManager;
import se.sics.isl.util.FormatUtils;
import se.sics.isl.util.LogFormatter;
import se.sics.tasim.is.AgentLookup;
import se.sics.tasim.is.common.Competition;
import se.sics.tasim.is.common.CompetitionParticipant;
import se.sics.tasim.is.common.DatabaseUtils;
import se.sics.tasim.is.common.DefaultScoreGenerator;

public class ScoreMerger
extends DefaultScoreGenerator {
    private static final String DEFAULT_CONFIG = "merge.conf";
    private static final String CONF = "is.";
    private static final Logger log = Logger.getLogger(ScoreMerger.class.getName());
    private String[] statPath;
    private String[] statName;
    private String[] statShortName;
    private boolean isAddingSourceInfo = false;
    private boolean isUsingShortStatName = false;
    private String shortDescription = null;

    public boolean isAddingSourceInfo() {
        return this.isAddingSourceInfo;
    }

    public void setAddingSourceInfo(boolean isAddingSourceInfo) {
        this.isAddingSourceInfo = isAddingSourceInfo;
    }

    public boolean isUsingShortStatName() {
        return this.isUsingShortStatName;
    }

    public void setUsingShortStatName(boolean isUsingShortStatName) {
        this.isUsingShortStatName = isUsingShortStatName;
    }

    public String getShortDescription() {
        return this.shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void addUserStatPage(String path, String name) {
        this.statPath = (String[])ArrayUtils.add(String.class, this.statPath, path);
        this.statName = (String[])ArrayUtils.add(String.class, this.statName, name);
        String shortName = name;
        int index = name.indexOf(46);
        if (index > 0) {
            shortName = name.substring(0, index);
        }
        this.statShortName = (String[])ArrayUtils.add(String.class, this.statShortName, shortName);
    }

    @Override
    protected String createUserName(CompetitionParticipant usr, int pos, int numberOfAgents) {
        StringBuffer sb = new StringBuffer().append("<b>").append(usr.getName()).append("</b>");
        if (this.statPath != null) {
            sb.append(" (");
            int i = 0;
            int n = this.statPath.length;
            while (i < n) {
                if (i > 0) {
                    sb.append(",&nbsp;");
                }
                sb.append("<a href='").append(this.statPath[i]).append(usr.getID()).append(".html'>").append(this.isUsingShortStatName ? this.statShortName[i] : this.statName[i]).append("</a>");
                ++i;
            }
            sb.append(')');
        }
        return sb.toString();
    }

    @Override
    protected void addPostInfo(StringBuffer page) {
        if (this.isAddingSourceInfo && this.statPath != null) {
            page.append("<em>The scores have been combined from");
            if (this.shortDescription != null) {
                page.append(" the ").append(this.shortDescription).append(" at");
            }
            int i = 0;
            int n = this.statPath.length;
            while (i < n) {
                if (i > 0) {
                    if (i >= n - 1) {
                        page.append(i > 1 ? ", and" : " and");
                    } else {
                        page.append(',');
                    }
                }
                page.append(" <a href='").append(this.statPath[i]).append("'>").append(this.statName[i]).append("</a>");
                ++i;
            }
            page.append(".</em>");
        }
    }

    public static void main(String[] args) throws IOException {
        ScoreMerger scoreGen;
        long mergeStartTime = System.currentTimeMillis();
        ArgumentManager config = new ArgumentManager("ScoreMerger", args);
        config.addOption("config", "configfile", "set the config file to use");
        config.addOption("is.database.sql.url", "jdbc:mysql://localhost:3306/mysql", "set the database url");
        config.addOption("log.consoleLevel", "level", "set the console log level");
        config.addOption("n", "Do not change any files or access any databases.");
        config.addHelp("h", "show this help message");
        config.addHelp("help");
        config.validateArguments();
        String configFile = config.getArgument("config", "merge.conf");
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
        String databaseURL = config.getProperty("is.database.sql.url");
        boolean check = config.getPropertyAsBoolean("n", false);
        config.finishArguments();
        ScoreMerger.setLogging(config);
        String competitionName = config.getProperty("competition.name");
        if (competitionName == null) {
            throw new IllegalStateException("no competition name");
        }
        Competition competition = new Competition(0, competitionName);
        String scoreClass = config.getProperty("competition.generator");
        if (scoreClass == null) {
            scoreGen = new ScoreMerger();
        } else {
            try {
                scoreGen = (ScoreMerger)Class.forName(scoreClass).newInstance();
            }
            catch (ThreadDeath e) {
                throw e;
            }
            catch (Throwable e) {
                throw (IOException)new IOException("could not create score merger of type '" + scoreClass + '\'').initCause(e);
            }
        }
        boolean generateWeights = true;
        if (!config.getPropertyAsBoolean("competition.useWeight", false)) {
            generateWeights = false;
            competition.setFlags(2);
        }
        int i = 1;
        int n = Integer.MAX_VALUE;
        while (i < n) {
            String serverName;
            String serverConfigFileName = config.getProperty("competition." + i + ".config");
            int competitionID = config.getPropertyAsInt("competition." + i + ".id", 0);
            if (serverConfigFileName == null || competitionID <= 0) break;
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
            scoreGen.addUserStatPage(config.getProperty("competition." + i + ".url"), serverName);
            if (!check) {
                Database userDatabase = DatabaseUtils.createUserDatabase(serverConfig, "is.", null);
                DBTable userTable = userDatabase.getTable("users");
                if (userTable == null) {
                    userDatabase.close();
                    throw new IllegalStateException("could not find user database for  competition " + competitionID);
                }
                Database database = DatabaseUtils.createDatabase(serverConfig, "is.");
                log.finer("DB Name: " + database.getName() + " class: " + database);
                Database serverDatabase = DatabaseUtils.createChildDatabase(serverConfig, "is.", serverName, database);
                log.finer("Server DB Name: " + serverDatabase.getName() + " class: " + serverDatabase);
                ScoreMerger.loadCompetitionByParticipants(competition, serverDatabase, userTable, competitionID);
                userDatabase.close();
                serverDatabase.close();
                database.close();
            }
            ++i;
        }
        scoreGen.init(null, config.getProperty("competition.destination", "."));
        scoreGen.setAddingSourceInfo(config.getPropertyAsBoolean("competition.addSourceInfo", false));
        scoreGen.setUsingShortStatName(config.getPropertyAsBoolean("competition.shortServerName", false));
        scoreGen.setShortDescription(config.getProperty("competition.shortDescription"));
        scoreGen.setShowingCompetitionTimes(false);
        log.info("Competition: " + competition.getName());
        log.info("Competition has " + competition.getParticipantCount() + " participants");
        log.info("Competition is written to " + scoreGen.getScoreFileName());
        if (!check) {
            scoreGen.createScoreTable(competition, -1);
        }
        log.info("Competition merged in " + (System.currentTimeMillis() - mergeStartTime) + " msek");
        scoreGen.listTopAgents(competition, generateWeights);
    }

    private void listTopAgents(Competition competition, boolean isWeightUsed) {
        CompetitionParticipant[] users = competition.getParticipants();
        if (users != null) {
            users = (CompetitionParticipant[])users.clone();
            Arrays.sort(users, this.getComparator(isWeightUsed));
            System.err.println();
            System.err.println("Top Agents");
            System.err.println("----------");
            int i = 0;
            int n = Math.min(10, users.length);
            while (i < n) {
                CompetitionParticipant usr = users[i];
                if (i < 9) {
                    System.err.print(' ');
                }
                System.err.print(String.valueOf(i + 1) + " " + usr.getName() + '\t');
                if (usr.getName().length() < 13) {
                    System.err.print('\t');
                }
                if (isWeightUsed) {
                    System.err.print(String.valueOf(FormatUtils.formatAmount((long)usr.getAvgWeightedScore())) + '\t');
                }
                System.err.println(String.valueOf(FormatUtils.formatAmount((long)usr.getAvgScore())) + "\t Games: " + usr.getGamesPlayed() + '\t' + usr.getZeroGamesPlayed());
                ++i;
            }
        }
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

    private static void loadCompetitionByResult(Competition competition, Database serverDatabase, DBTable userTable, int competitionID, boolean generateWeights) {
        Competition currentCompetition = null;
        DBTable competitionTable = serverDatabase.getTable("competitions");
        DBMatcher compDBM = new DBMatcher();
        compDBM.setInt("id", competitionID);
        compDBM.setLimit(1);
        DBResult compResult = competitionTable.select(compDBM);
        if (compResult.next()) {
            int parentID = compResult.getInt("parent");
            String name = compResult.getString("name");
            int flags = compResult.getInt("flags");
            long startTime = compResult.getLong("starttime");
            long endTime = compResult.getLong("endtime");
            int startUniqueID = compResult.getInt("startuniqid");
            int startPublicID = compResult.getInt("startsimid");
            int simulationCount = compResult.getInt("simulations");
            double startWeight = compResult.getDouble("startweight");
            String scoreClass = compResult.getString("scoreclass");
            currentCompetition = new Competition(competitionID, name, startTime, endTime, startUniqueID, simulationCount, startWeight);
            if (startPublicID >= 0) {
                currentCompetition.setStartSimulationID(startPublicID);
            }
            if (scoreClass != null) {
                currentCompetition.setScoreClassName(scoreClass);
            }
            currentCompetition.setFlags(flags);
            if (parentID > 0) {
                currentCompetition.setParentCompetitionID(parentID);
                log.warning("SCORE MERGER RESULT RECALCULATION DOES NOT SUPPORT PARENT COMPETITIONS!!!");
            }
        } else {
            log.warning("could not find competition " + competitionID);
        }
        compResult.close();
        log.finer("current competition: " + (currentCompetition == null ? "<null>" : currentCompetition.getName()));
        DBTable competitionParticipantTable = serverDatabase.getTable("competitionparts");
        DBMatcher dbm = new DBMatcher();
        dbm.setInt("competition", competitionID);
        DBResult result = competitionParticipantTable.select(dbm);
        while (result.next()) {
            int pid = result.getInt("participantid");
            if (competition.getParticipantByID(pid) != null) continue;
            DBMatcher dbm2 = new DBMatcher();
            dbm2.setInt("id", pid);
            dbm2.setLimit(1);
            DBResult user = userTable.select(dbm2);
            if (!user.next()) {
                log.severe("Could not find user " + pid + " in user db");
                return;
            }
            String userName = user.getString("name");
            log.finer("Adding user " + userName);
            CompetitionParticipant cp = new CompetitionParticipant(pid, userName);
            competition.addParticipant(cp);
            user.close();
        }
        result.close();
        DBTable competitionResult = serverDatabase.getTable("competitionresults");
        dbm.clear();
        dbm.setInt("competition", competitionID);
        result = competitionResult.select(dbm);
        while (result.next()) {
            CompetitionParticipant participant = competition.getParticipantByID(result.getInt("participantid"));
            if (participant != null) {
                double weight;
                int simID = result.getInt("simid");
                int flags = result.getInt("flags");
                long score = result.getLong("score");
                double d = weight = generateWeights && currentCompetition != null ? currentCompetition.getWeight(result.getInt("id")) : result.getDouble("weight");
                if (generateWeights) {
                    log.finer("weight for game " + simID + ": " + weight);
                }
                participant.addScore(simID, score, weight, score == 0 || (flags & 32) != 0);
                continue;
            }
            log.severe("Can not find participant " + result.getInt("participantid") + " in Competition");
            return;
        }
        result.close();
    }

    private static void loadCompetitionByParticipants(Competition competition, Database serverDatabase, DBTable userTable, int competitionID) throws IOException {
        DBTable competitionParticipantTable;
        AgentLookup agentLookup = new AgentLookup();
        DBTable competitionTable = serverDatabase.getTable("competitions");
        Competition currentCompetition = ScoreMerger.loadCompetition(userTable, agentLookup, competitionTable, competitionParticipantTable = serverDatabase.getTable("competitionparts"), competitionID);
        if (currentCompetition == null) {
            IOException ioe = new IOException("competition not found");
            log.log(Level.SEVERE, "could not find competition " + competitionID, ioe);
            throw ioe;
        }
        log.finer("loaded competition " + currentCompetition.getName());
        ScoreMerger.addScores(competition, currentCompetition, true);
        while (currentCompetition.hasParentCompetition()) {
            int parentID = currentCompetition.getParentCompetitionID();
            currentCompetition = ScoreMerger.loadCompetition(userTable, agentLookup, competitionTable, competitionParticipantTable, parentID);
            if (currentCompetition == null) {
                IOException ioe = new IOException("competition not found");
                log.log(Level.SEVERE, "could not find competition " + parentID, ioe);
                throw ioe;
            }
            log.finer("loaded parent competition " + currentCompetition.getName());
            ScoreMerger.addScores(competition, currentCompetition, false);
        }
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
            if (parentID > 0) {
                competition.setParentCompetitionID(parentID);
            }
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
                cp.setScores(res2.getLong("score"), res2.getDouble("wscore"), res2.getInt("gamesplayed"), res2.getInt("zgamesplayed"), res2.getDouble("wgamesplayed"), res2.getDouble("zwgamesplayed"));
                cp.setAvgScores(res2.getDouble("avgsc1"), res2.getDouble("avgsc2"), res2.getDouble("avgsc3"), res2.getDouble("avgsc4"));
                competition.addParticipant(cp);
            }
            res2.close();
            theCompetition = competition;
        }
        res.close();
        return theCompetition;
    }
}

