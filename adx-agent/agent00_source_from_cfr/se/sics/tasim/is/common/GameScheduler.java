/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.mortbay.http.HttpException
 *  org.mortbay.http.HttpRequest
 *  org.mortbay.http.HttpResponse
 *  org.mortbay.util.ByteArrayISO8859Writer
 */
package se.sics.tasim.is.common;

import com.botbox.util.ArrayUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mortbay.http.HttpException;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import org.mortbay.util.ByteArrayISO8859Writer;
import se.sics.tasim.is.AgentInfo;
import se.sics.tasim.is.CompetitionSchedule;
import se.sics.tasim.is.common.Competition;
import se.sics.tasim.is.common.CompetitionParticipant;
import se.sics.tasim.is.common.HttpPage;
import se.sics.tasim.is.common.InfoServer;
import se.sics.tasim.is.common.ScoreGenerator;
import se.sics.tasim.is.common.SimServer;

public class GameScheduler
extends HttpPage {
    private static final Logger log = Logger.getLogger(GameScheduler.class.getName());
    private final InfoServer infoServer;
    private final SimServer simServer;
    private final String serverName;
    private final String header;
    private static SimpleDateFormat dateFormat = null;
    private int agentsPerGame = 8;

    public GameScheduler(InfoServer infoServer, SimServer simServer, String header) {
        this.infoServer = infoServer;
        this.simServer = simServer;
        this.serverName = simServer.getServerName();
        this.header = header;
    }

    @Override
    public void handle(String pathInContext, String pathParams, HttpRequest req, HttpResponse response) throws HttpException, IOException {
        String userName = req.getAuthUser();
        int httpUserID = this.infoServer.getUserID(userName);
        if (!this.infoServer.isAdministrator(httpUserID)) {
            return;
        }
        StringBuffer page = this.pageStart("Competition Scheduler");
        if (req.getParameter("submit") != null) {
            this.handlePreview(req, page);
        } else if (req.getParameter("execute") != null) {
            this.handleExecute(req, page);
        } else {
            this.handleConfiguration(req, page);
        }
        page = this.pageEnd(page);
        ByteArrayISO8859Writer writer = new ByteArrayISO8859Writer();
        writer.write(page.toString());
        response.setContentType("text/html");
        response.setContentLength(writer.size());
        writer.writeTo(response.getOutputStream());
        response.commit();
        req.setHandled(true);
    }

    private void handlePreview(HttpRequest req, StringBuffer page) {
        AgentInfo[] users = this.infoServer.getAgentInfos();
        int nrUsers = 0;
        int[] agentIDs = new int[]{-1};
        Object[] agentNames = new String[]{"dummy"};
        String addIDs = this.trim(req.getParameter("agents"));
        String message = null;
        page.append("<font face='arial' size='+1'>Agents in competition</font><p>\r\n");
        int i = 0;
        int n = users.length;
        while (i < n) {
            if (req.getParameter("join-" + users[i].getID()) != null) {
                if (nrUsers > 0) {
                    page.append(", ");
                }
                page.append(users[i].getName());
                ++nrUsers;
                agentIDs = ArrayUtils.add(agentIDs, users[i].getID());
                agentNames = (String[])ArrayUtils.add(agentNames, users[i].getName());
            }
            ++i;
        }
        if (addIDs != null) {
            StringTokenizer stok = new StringTokenizer(addIDs, "\r\n, ");
            while (stok.hasMoreTokens()) {
                String token = stok.nextToken();
                AgentInfo usr = this.getUser(token, users);
                if (usr != null) {
                    if (nrUsers > 0) {
                        page.append(", ");
                    }
                    page.append(usr.getName());
                    ++nrUsers;
                    agentIDs = ArrayUtils.add(agentIDs, usr.getID());
                    agentNames = (String[])ArrayUtils.add(agentNames, usr.getName());
                    continue;
                }
                message = message == null ? token : String.valueOf(message) + ", " + token;
            }
        }
        if (message != null) {
            if (nrUsers == 0) {
                page.append("<font color=red>No agents in competition</font>");
            }
            page.append("<p>\r\n<font face='arial' size='+1'>Agents that could not be found</font><p>\r\n<font color=red>").append(message).append("</font>\r\n");
        }
        try {
            Competition parentCompetition = null;
            String parentIDStr = this.trim(req.getParameter("parent"));
            if (parentIDStr != null) {
                int parentID = Integer.parseInt(parentIDStr);
                parentCompetition = this.simServer.getCompetitionByID(parentID);
                if (parentCompetition == null) {
                    throw new IllegalArgumentException("could not find parent competition " + parentID);
                }
                page.append("<p><font face='arial' size='+1'>Agents in parent competition " + parentCompetition.getName() + "</font>" + "<p>\r\n");
                CompetitionParticipant[] pUsers = parentCompetition.getParticipants();
                if (pUsers != null) {
                    int i2 = 0;
                    int n2 = pUsers.length;
                    while (i2 < n2) {
                        if (i2 > 0) {
                            page.append(", ");
                        }
                        page.append(pUsers[i2].getName());
                        ++i2;
                    }
                }
            }
            page.append("<p>\r\n");
            String totalGamesStr = req.getParameter("games");
            if (totalGamesStr == null || totalGamesStr.length() == 0) {
                throw new IllegalArgumentException("total number of games not specified");
            }
            int totalGames = Integer.parseInt(totalGamesStr);
            long time = GameScheduler.parseServerTimeDate(req.getParameter("time"));
            float weight = Float.parseFloat(req.getParameter("weight"));
            boolean startWeightDuringWeekends = req.getParameter("weekend") != null;
            boolean noWeights = req.getParameter("useweights") == null;
            boolean lowestScoreAsZero = req.getParameter("lowestscore") != null;
            String name = this.trim(req.getParameter("name"));
            int timeBetween = Integer.parseInt(req.getParameter("timeBetween"));
            int reserveBetween = Integer.parseInt(req.getParameter("reserveBetween"));
            int reserveTime = Integer.parseInt(req.getParameter("reserveTime"));
            boolean totalAgent = "agent".equals(req.getParameter("type"));
            String scoreGenerator = this.trim(req.getParameter("scoregen"));
            if (nrUsers == 0) {
                throw new IllegalArgumentException("No agents in competition");
            }
            if (name == null || name.length() == 0) {
                throw new IllegalArgumentException("No competition name");
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
            int[][] games = GameScheduler.scheduleGames(nrUsers, this.agentsPerGame);
            int minGames = games.length;
            int perAgent = this.gamesPerAgent(nrUsers);
            int rounds = totalAgent ? totalGames / perAgent : totalGames / minGames;
            page.append("<font face='arial' size='+1'>Competition Data</font><p>\r\n<table border='0'><tr><td>Competition name:</td><td>").append(name);
            if (parentCompetition != null) {
                page.append("</td></tr\r\n><tr><td>Continuation of competition:</td><td>").append(parentCompetition.getName());
            }
            if (scoreGenerator != null) {
                page.append("</td></tr\r\n><tr><td>Competition score table generator</td><td>").append(scoreGenerator);
            }
            page.append("</td></tr\r\n><tr><td>Total number of players:</td><td>").append(nrUsers).append("</td></tr\r\n><tr><td>Requested number of games:</td><td>").append(totalGames);
            if (totalAgent) {
                page.append(" per agent");
            }
            page.append("</td></tr\r\n><tr><td>Number of games scheduled:</td><td>").append(rounds * minGames).append("</td></tr\r\n><tr><td>Number of games per round:</td><td>").append(minGames).append("</td></tr><tr><td>Number of rounds:</td><td>" + rounds + "</td></tr\r\n>" + "<tr><td>Number of games per agent/round:</td><td>" + perAgent + "</td></tr\r\n>" + "<tr><td>Number of games per agent:</td><td>").append(perAgent * rounds).append("</td></tr\r\n><tr><td>Start Time:</td><td>").append(GameScheduler.formatServerTimeDate(time)).append("</td></tr\r\n><tr><td>Approx End Time (55 min games):</td><td>");
            long endTime = time + (long)(rounds * minGames * (55 + timeBetween)) * 60000;
            if (reserveTime > 0 && reserveBetween > 0) {
                endTime += (long)(reserveTime * (rounds * minGames / reserveBetween)) * 60000;
            }
            page.append(GameScheduler.formatServerTimeDate(endTime)).append("</td></tr\r\n><tr><td>Start Weight:</td><td>").append(weight);
            if (noWeights) {
                page.append(" (does not use weighted scores");
                if (startWeightDuringWeekends) {
                    page.append("; start weight during weekends");
                }
                page.append(')');
            } else if (startWeightDuringWeekends) {
                page.append(" (use start weight during weekends)");
            }
            if (lowestScoreAsZero) {
                page.append("</td></tr\r\n><tr><td>Score for zero games</td><td>Use lowest score if smaller than zero");
            }
            page.append("</td></tr\r\n><tr><td>Delay between games (minutes)</td><td>").append(timeBetween).append("</td></tr\r\n><tr><td>Reserve time for admin (minutes)</td><td>").append(reserveTime).append("</td></tr\r\n><tr><td>Played games between time reservation:</td><td>").append(reserveBetween).append("</td></tr\r\n></table>\r\n<p>\r\n<font face='arial' size='+1'>Example round</font><p>\r\n<table border=1><tr><th>Game</th><th>Agents</th></tr>");
            int i3 = 0;
            while (i3 < minGames) {
                page.append("<tr><td>").append(i3 + 1).append("</td><td>");
                int a = 0;
                while (a < this.agentsPerGame) {
                    page.append((String)agentNames[games[i3][a]]).append(' ');
                    ++a;
                }
                page.append("</td></tr>");
                ++i3;
            }
            page.append("</table>\r\n<p>\r\n<form method=post><input type=hidden name=agentNo value=").append(nrUsers).append("><input type=hidden name=rounds value=").append(rounds).append('>');
            i3 = 0;
            while (i3 < nrUsers) {
                page.append("<input type=hidden name=agent").append(i3).append(" value=").append(agentIDs[i3 + 1]).append('>');
                ++i3;
            }
            page.append("<input type=hidden name=time value=").append(time).append('>');
            page.append("<input type=hidden name=weight value=").append(weight).append('>');
            if (!noWeights) {
                page.append("<input type=hidden name=useweights value='true'>");
            }
            if (startWeightDuringWeekends) {
                page.append("<input type=hidden name=weekend value='true'>");
            }
            if (lowestScoreAsZero) {
                page.append("<input type=hidden name=lowestscore value='true'>");
            }
            page.append("<input type=hidden name=timeBetween value=").append(timeBetween).append('>');
            page.append("<input type=hidden name=reserveTime value=").append(reserveTime).append('>');
            page.append("<input type=hidden name=reserveBetween value=").append(reserveBetween).append('>');
            page.append("<input type=hidden name=name value='").append(name).append("'>");
            if (parentCompetition != null) {
                page.append("<input type=hidden name=parent value='").append(parentCompetition.getID()).append("'>");
            }
            if (scoreGenerator != null) {
                page.append("<input type=hidden name=scoregen value='").append(scoreGenerator).append("'>");
            }
            page.append("\r\n<input type=submit name=execute value='Create Competition'> &nbsp; <input type=submit name=cancel value='Cancel'></form>\r\n");
        }
        catch (Exception e) {
            log.log(Level.WARNING, "could not schedule games", e);
            page.append("Could not schedule games: <font color=red>").append(e).append("</font><p>Try to go back and enter correct information");
        }
    }

    private void handleExecute(HttpRequest req, StringBuffer page) {
        AgentInfo[] users = this.infoServer.getAgentInfos();
        try {
            int parentID;
            int rounds = Integer.parseInt(req.getParameter("rounds"));
            long startTime = Long.parseLong(req.getParameter("time"));
            float startWeight = Float.parseFloat(req.getParameter("weight"));
            boolean noWeights = req.getParameter("useweights") == null;
            boolean startWeightDuringWeekends = req.getParameter("weekend") != null;
            boolean lowestScoreAsZero = req.getParameter("lowestscore") != null;
            String name = req.getParameter("name");
            Competition parentCompetition = null;
            String parentIDStr = this.trim(req.getParameter("parent"));
            if (parentIDStr != null && ((parentCompetition = this.simServer.getCompetitionByID(parentID = Integer.parseInt(parentIDStr))) == null || parentID <= 0)) {
                throw new IllegalArgumentException("could not find parent competition " + parentIDStr);
            }
            int timeBetween = Integer.parseInt(req.getParameter("timeBetween")) * 60000;
            int reserveTimeMillis = Integer.parseInt(req.getParameter("reserveTime")) * 60000;
            int reserveBetween = Integer.parseInt(req.getParameter("reserveBetween"));
            String scoreGenerator = this.trim(req.getParameter("scoregen"));
            int nrUsers = Integer.parseInt(req.getParameter("agentNo"));
            int[] participantIDs = new int[nrUsers];
            int[] idMap = new int[nrUsers + 1];
            idMap[0] = -1;
            int i = 0;
            while (i < nrUsers) {
                int userID = Integer.parseInt(req.getParameter("agent" + i));
                AgentInfo usr = this.getUser(userID, users);
                if (usr == null || usr.getID() != userID) {
                    throw new IllegalStateException("user " + userID + " not found");
                }
                idMap[i + 1] = userID;
                participantIDs[i] = userID;
                ++i;
            }
            long currentTime = this.infoServer.getServerTimeMillis();
            if (currentTime > startTime) {
                throw new IllegalStateException("start time already passed or too close into the future");
            }
            if (startWeight == 0.0f) {
                throw new IllegalStateException("start weight may not be 0");
            }
            Object scheduledGames = null;
            int nextGame = 0;
            int scheduledAgentsPerGame = nrUsers < this.agentsPerGame ? nrUsers : this.agentsPerGame;
            int i2 = 0;
            while (i2 < rounds) {
                int[][] games = GameScheduler.scheduleGames(nrUsers, this.agentsPerGame);
                if (scheduledGames == null) {
                    scheduledGames = new int[games.length * rounds][scheduledAgentsPerGame];
                }
                int g = 0;
                int m = games.length;
                while (g < m) {
                    int index = 0;
                    int a = 0;
                    while (a < this.agentsPerGame) {
                        if (games[g][a] != 0) {
                            scheduledGames[nextGame][index++] = idMap[games[g][a]];
                        }
                        ++a;
                    }
                    ++nextGame;
                    ++g;
                }
                ++i2;
            }
            if (scheduledGames == null) {
                page.append("No games created");
            } else {
                CompetitionSchedule schedule = new CompetitionSchedule(name);
                schedule.setStartTime(startTime);
                schedule.setParticipants(participantIDs);
                if (parentCompetition != null) {
                    schedule.setParentCompetitionID(parentCompetition.getID());
                }
                schedule.setTimeBetweenSimulations(timeBetween);
                schedule.setReservationBetweenSimulations(reserveBetween, reserveTimeMillis);
                schedule.setStartWeight(startWeight);
                if (noWeights) {
                    schedule.setFlags(schedule.getFlags() | 2);
                }
                if (startWeightDuringWeekends) {
                    schedule.setFlags(schedule.getFlags() | 1);
                }
                if (lowestScoreAsZero) {
                    schedule.setFlags(schedule.getFlags() | 64);
                }
                if (scoreGenerator != null) {
                    schedule.setScoreClassName(scoreGenerator);
                }
                schedule.setSimulationsClosed(true);
                int i3 = 0;
                int n = scheduledGames.length;
                while (i3 < n) {
                    schedule.addSimulation((int[])scheduledGames[i3]);
                    ++i3;
                }
                this.simServer.scheduleCompetition(schedule);
                page.append("Requested ").append(scheduledGames.length).append(" scheduled games in competition ").append(name).append(".<p>");
            }
        }
        catch (Exception e) {
            log.log(Level.WARNING, "could not create competition", e);
            page.append("Competition could not be created: <font color=red>").append(e).append("</font>");
        }
    }

    private void handleConfiguration(HttpRequest req, StringBuffer page) {
        int n;
        int i;
        Competition[] comps = this.simServer.getCompetitions();
        if (comps != null) {
            Competition currentComp = this.simServer.getCurrentCompetition();
            page.append("<table border=1 width='100%'><tr><th colspan=6>Existing Competitions");
            if (currentComp != null) {
                page.append(" (now running: ").append(currentComp.getName()).append(')');
            }
            page.append("</th></tr><tr><th>ID</th><th>Name</th><th>Start Time</th><th>End Time</th><th>Games IDs</th><th>Agents/Games</th></tr>");
            i = 0;
            n = comps.length;
            while (i < n) {
                Competition comp = comps[i];
                int numAgents = comp.getParticipantCount();
                int numGames = comp.getSimulationCount();
                page.append("<tr><td>");
                if (comp.hasParentCompetition()) {
                    page.append(comp.getParentCompetitionID()).append(" -&gt; ");
                }
                page.append(comp.getID()).append("</td><td>").append(comp.getName()).append("</td><td>").append(GameScheduler.formatServerTimeDate(comp.getStartTime())).append("</td><td>").append(GameScheduler.formatServerTimeDate(comp.getEndTime())).append("</td><td>");
                if (comp.hasSimulationID()) {
                    page.append(comp.getStartSimulationID()).append(" - ").append(comp.getEndSimulationID());
                } else {
                    page.append("? - ?");
                }
                page.append(" (<em>").append(comp.getStartUniqueID()).append(" - ").append(comp.getEndUniqueID()).append("</em>)</td><td>").append(numAgents).append(" / ").append(numGames).append("</td></tr>\r\n");
                ++i;
            }
            page.append("</table><p>\r\n");
        }
        page.append("<p><font face='arial' size='+1'>Create new competition:</font>\r\n<form method=post>\r\n").append("<table border='0'>\r\n<tr><td>Name of competition (unique)</td><td><input type=text name=name size=32></td></tr\r\n><tr><td>Continuation of competition</td><td><input type=text name=parent size=32></td></tr>\r\n</td></tr\r\n><tr><td><select name=type><option value=total>Total number of games (int)<option value=agent>Number of games per agent (int)</select></td><td><input type=text name=games size=32></td></tr><tr><td>Start Time (YYYY-MM-DD HH:mm)</td><td><input type=text name=time size=32 value='").append(GameScheduler.formatServerTimeDate(this.infoServer.getServerTimeMillis())).append("'></td></tr><tr><td>Start Weight (float)</td><td><input type=text name=weight value='1.0' size=32></td></tr\r\n><tr><td>&nbsp;</td><td><input type=checkbox name=useweights> Use weighted scores</td></tr><tr><td>&nbsp;</td><td><input type=checkbox name=weekend> Use start weight during weekends</td></tr><tr><td>Score for zero games</td><td><input type=checkbox name=lowestscore> Use lowest score if smaller than zero</td></tr><tr><td>Delay between games (minutes)</td><td><input type=text name=timeBetween value=5 size=32></td></tr\r\n><tr><td>Time to reserve for admin (minutes)</td><td><input type=text name=reserveTime value=0 size=32></td></tr\r\n><tr><td>Played games between time reservations (int)</td><td><input type=text name=reserveBetween value=0 size=32></td></tr\r\n><tr><td>Competition score table generator</td><td><input type=text name=scoregen size=32></td></tr\r\n><tr><td colspan=2>&nbsp;</td></tr\r\n><tr><td colspan=2>Specify agents that should be scheduled as comma separated list of agent names<br>(you can also select agents in the list below)</td></tr><tr><td colspan=2><textarea name=agents cols=75 rows=6></textarea></td></tr><tr><td colspan=2><input type=submit name=submit value='Preview Schedule!'></td></tr><tr><td colspan=2>&nbsp;</td></tr>\r\n<tr><td colspan=2><table border=0 width='100%' bgcolor=black cellspacing=0 cellpadding=1><tr><td><table border=0 bgcolor='#f0f0f0' align=left width='100%' cellspacing=0><tr><td colspan=5><b>Available agents:</b></td></tr\r\n><tr>");
        AgentInfo[] users = this.infoServer.getAgentInfos();
        i = 0;
        n = users.length;
        while (i < n) {
            if (i % 5 == 0 && i > 0) {
                page.append("</tr><tr>");
            }
            page.append("<td><input type=checkbox name=join-").append(users[i].getID()).append('>').append(users[i].getName()).append("</td>");
            ++i;
        }
        if (users.length % 5 > 0) {
            page.append("<td colspan=").append(5 - users.length % 5).append(">&nbsp;</td>");
        }
        page.append("</tr></table></td></tr></table></td></tr></table><p>\r\n</form>\r\n");
    }

    private AgentInfo getUser(String user, AgentInfo[] users) {
        int i = 0;
        int n = users.length;
        while (i < n) {
            if (users[i].getName().equals(user)) {
                return users[i];
            }
            ++i;
        }
        return null;
    }

    private AgentInfo getUser(int userID, AgentInfo[] users) {
        int i = 0;
        int n = users.length;
        while (i < n) {
            if (users[i].getID() == userID) {
                return users[i];
            }
            ++i;
        }
        return null;
    }

    private String trim(String text) {
        return text != null && (text = text.trim()).length() > 0 ? text : null;
    }

    public int gamesPerAgent(int noAgents) {
        if (noAgents < this.agentsPerGame + 1) {
            return 1;
        }
        return this.agentsPerGame / GameScheduler.findLargestDivisor(noAgents, this.agentsPerGame);
    }

    private static int findLargestDivisor(int a, int b) {
        if (a == 1 || b == 1) {
            return 1;
        }
        int[] primes = new int[]{2, 3, 5, 7, 11};
        int div = 1;
        int pos = 0;
        int max = primes.length;
        while (a > 1 && b > 1 && pos < max) {
            int prime = primes[pos];
            if (a % prime == 0 && b % prime == 0) {
                a /= prime;
                b /= prime;
                div *= prime;
                continue;
            }
            ++pos;
        }
        return div;
    }

    public static int[][] scheduleGames(int noAgents, int agentsPerGame) {
        Object games = null;
        if (noAgents <= agentsPerGame) {
            games = new int[1][agentsPerGame];
            int i = 0;
            while (i < noAgents) {
                games[0][i] = i + 1;
                ++i;
            }
        } else {
            int perAgent = agentsPerGame / GameScheduler.findLargestDivisor(noAgents, agentsPerGame);
            int totalGames = perAgent * noAgents / agentsPerGame;
            games = new int[totalGames][agentsPerGame];
            int agent = 0;
            int game = 0;
            while (game < totalGames) {
                int a = 0;
                while (a < agentsPerGame) {
                    games[game][a] = agent++ % noAgents + 1;
                    ++a;
                }
                ++game;
            }
            int i = 0;
            while (i < totalGames * 48) {
                int pos1 = (int)(Math.random() * (double)agentsPerGame);
                int pos2 = (int)(Math.random() * (double)agentsPerGame);
                int game1 = (int)(Math.random() * (double)totalGames);
                int game2 = i % totalGames;
                Object agent1 = games[game1][pos1];
                Object agent2 = games[game2][pos2];
                boolean found = false;
                int a = 0;
                while (a < agentsPerGame && !found) {
                    found = games[game1][a] == agent2 || games[game2][a] == agent1;
                    ++a;
                }
                if (!found) {
                    games[game1][pos1] = agent2;
                    games[game2][pos2] = agent1;
                }
                ++i;
            }
        }
        return games;
    }

    private StringBuffer pageStart(String title) {
        StringBuffer page = new StringBuffer();
        page.append("<html><head><title>").append(this.infoServer.getServerType()).append(" - ").append(title).append('@').append(this.serverName).append("</title></head>\r\n<body>\r\n").append(this.header).append("<font face='arial' size='+2'>").append(title).append(" at ").append(this.serverName).append("</font><p>\r\n");
        return page;
    }

    private StringBuffer pageEnd(StringBuffer page) {
        return page.append("</body>\r\n</html>\r\n");
    }

    public static synchronized String formatServerTimeDate(long time) {
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            dateFormat.setTimeZone(new SimpleTimeZone(0, "UTC"));
        }
        return dateFormat.format(new Date(time));
    }

    public static synchronized long parseServerTimeDate(String date) throws ParseException {
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            dateFormat.setTimeZone(new SimpleTimeZone(0, "UTC"));
        }
        return dateFormat.parse(date).getTime();
    }

    public static void main(String[] args) {
        int gameNr = 0;
        if (args.length < 1) {
            System.out.println("Usage: GameScheduler <NoAgents>");
            System.exit(0);
        }
        try {
            gameNr = Integer.parseInt(args[0]);
        }
        catch (Exception e) {
            System.out.println("Error in nr");
        }
        int aPerGame = 6;
        int[][] games = GameScheduler.scheduleGames(gameNr, aPerGame);
        int[] agentGames = new int[gameNr + 1];
        int i = 0;
        int n = games.length;
        while (i < n) {
            System.out.print("Game " + i + " | ");
            int j = 0;
            while (j < aPerGame) {
                System.out.print("" + games[i][j] + ' ');
                int[] arrn = agentGames;
                int n2 = games[i][j];
                arrn[n2] = arrn[n2] + 1;
                ++j;
            }
            System.out.println();
            ++i;
        }
        i = 0;
        while (i < gameNr + 1) {
            System.out.println("Agent " + i + " played " + agentGames[i]);
            ++i;
        }
    }
}

