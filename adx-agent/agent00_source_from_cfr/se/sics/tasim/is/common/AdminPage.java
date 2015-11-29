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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mortbay.http.HttpException;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import org.mortbay.util.ByteArrayISO8859Writer;
import se.sics.tasim.is.SimulationInfo;
import se.sics.tasim.is.common.Competition;
import se.sics.tasim.is.common.CompetitionParticipant;
import se.sics.tasim.is.common.GameScheduler;
import se.sics.tasim.is.common.HttpPage;
import se.sics.tasim.is.common.InfoServer;
import se.sics.tasim.is.common.SimServer;

public class AdminPage
extends HttpPage {
    private static final boolean SERVER_TIME = false;
    private static final boolean AGENT_LOOKUP = false;
    private final InfoServer infoServer;
    private final SimServer simServer;
    private final String historyPath;
    private final String serverName;
    private final String header;

    public AdminPage(InfoServer infoServer, SimServer simServer, String historyPath, String header) {
        this.infoServer = infoServer;
        this.simServer = simServer;
        this.historyPath = historyPath;
        this.serverName = simServer.getServerName();
        this.header = header;
    }

    @Override
    public void handle(String pathInContext, String pathParams, HttpRequest request, HttpResponse response) throws HttpException, IOException {
        block15 : {
            String userName = request.getAuthUser();
            int userID = this.infoServer.getUserID(userName);
            if (!this.infoServer.isAdministrator(userID)) {
                return;
            }
            String name = this.getName(pathInContext);
            StringBuffer page = null;
            int error = 0;
            try {
                try {
                    if ("admin".equals(name) || "".equals(name)) {
                        page = this.generateAdmin(request);
                        break block15;
                    }
                    if ("competition".equals(name)) {
                        page = this.generateCompetition(request);
                        break block15;
                    }
                    if ("games".equals(name)) {
                        page = this.generateGames(request);
                        break block15;
                    }
                    error = 404;
                }
                catch (Exception e) {
                    Logger.global.log(Level.WARNING, "AdminPage: could not generate page " + name, e);
                    if (error > 0) {
                        response.sendError(error);
                        request.setHandled(true);
                        break block15;
                    }
                    if (page == null) {
                        response.sendError(500);
                        request.setHandled(true);
                        break block15;
                    }
                    ByteArrayISO8859Writer writer = new ByteArrayISO8859Writer();
                    writer.write(page.toString());
                    response.setContentType("text/html");
                    response.setContentLength(writer.size());
                    writer.writeTo(response.getOutputStream());
                    response.commit();
                    request.setHandled(true);
                }
            }
            finally {
                if (error > 0) {
                    response.sendError(error);
                    request.setHandled(true);
                } else if (page == null) {
                    response.sendError(500);
                    request.setHandled(true);
                } else {
                    ByteArrayISO8859Writer writer = new ByteArrayISO8859Writer();
                    writer.write(page.toString());
                    response.setContentType("text/html");
                    response.setContentLength(writer.size());
                    writer.writeTo(response.getOutputStream());
                    response.commit();
                    request.setHandled(true);
                }
            }
        }
    }

    private String getName(String url) {
        int start = url.indexOf(47, 1);
        if (start > 1 && (start = url.indexOf(47, start + 1)) > 1 && url.length() > start + 1) {
            int end = url.indexOf(47, start + 1);
            return end > 0 ? url.substring(start + 1, end) : url.substring(start + 1);
        }
        return "admin";
    }

    private StringBuffer pageStart(String title) {
        StringBuffer page = new StringBuffer();
        page.append("<html><head><title>").append(this.infoServer.getServerType()).append(" - ").append(title).append('@').append(this.serverName).append("</title></head>\r\n<body>\r\n").append(this.header).append("<font face='arial' size='+2'>").append(title).append(" at ").append(this.serverName).append("</font><p>\r\n");
        return page;
    }

    private StringBuffer pageEnd(StringBuffer page) {
        return page.append("</body>\r\n</html>\r\n");
    }

    private StringBuffer generateAdmin(HttpRequest req) {
        String id;
        StringBuffer page = this.pageStart("Administration");
        int gameID = -1;
        int competitionID = -1;
        if (req.getParameter("generateResults") != null) {
            try {
                id = req.getParameter("gameID");
                if (id == null || id.length() == 0) {
                    throw new IllegalArgumentException("no game id specified");
                }
                gameID = Integer.parseInt(id);
                this.simServer.generateResults(gameID, false, true);
                page.append("<hr><p>Result page generated for game ").append(gameID).append("<p><hr><p>\r\n");
            }
            catch (Exception e) {
                Logger.global.log(Level.WARNING, "AdminPage: could not generate score page", e);
                page.append("<hr><p><font color=red>could not generate score page: ").append(e).append("</font>").append("<p><hr><p>\r\n");
            }
        } else if (req.getParameter("generateCompetition") != null) {
            try {
                id = req.getParameter("competitionID");
                if (id == null || id.length() == 0) {
                    throw new IllegalArgumentException("no competition id specified");
                }
                competitionID = Integer.parseInt(id);
                this.simServer.generateCompetitionResults(competitionID);
                page.append("<hr><p>Result page generated for competition ").append(competitionID).append("<p><hr><p>\r\n");
            }
            catch (Exception e) {
                Logger.global.log(Level.WARNING, "AdminPage: could not generate competition results", e);
                page.append("<hr><p><font color=red>could not generate competition results: ").append(e).append("</font>").append("<p><hr><p>\r\n");
            }
        } else if (req.getParameter("scratchGame") != null) {
            try {
                id = req.getParameter("scratchID");
                if (id == null || id.length() == 0) {
                    throw new IllegalArgumentException("no game id specified");
                }
                int scratchID = Integer.parseInt(id);
                this.simServer.scratchSimulation(scratchID);
                page.append("<hr><p>Game ").append(scratchID).append(" has been scratched!<p><hr><p>\r\n");
            }
            catch (Exception e) {
                Logger.global.log(Level.WARNING, "AdminPage: could not scratch game", e);
                page.append("<hr><p><font color=red>could not scratch game: ").append(e).append("</font>").append("<p><hr><p>\r\n");
            }
        } else if (req.getParameter("reserve") != null) {
            String len = req.getParameter("reserveLength");
            String tim = req.getParameter("reserveTime");
            if (len != null && len.length() > 0 && tim != null && tim.length() > 0) {
                try {
                    int length = Integer.parseInt(len);
                    long startTime = GameScheduler.parseServerTimeDate(tim);
                    this.simServer.addTimeReservation(startTime, length * 60 * 1000);
                    page.append("<hr><p>Requested time reservation starting at ").append(GameScheduler.formatServerTimeDate(startTime)).append(" lasting for ").append(length).append(" minutes.").append("<p><hr><p>\r\n");
                }
                catch (Exception e) {
                    Logger.global.log(Level.WARNING, "AdminPage: could not reserve time", e);
                    page.append("<hr><p><font color=red>Could not reserve time: ").append(e).append("</font><hr><p>\r\n");
                }
            }
        } else if (req.getParameter("Force") != null) {
            String w = req.getParameter("forceWeight");
            if (w != null && w.length() > 0) {
                try {
                    float weight = Float.parseFloat(w);
                    Competition.setForcedWeight(weight, weight >= 0.0f);
                    page.append("<hr><p>Weight force to " + weight + "<hr><p>");
                }
                catch (Exception e) {
                    Logger.global.log(Level.WARNING, "AdminPage: could not force weight", e);
                    page.append("<hr><p><font color=red>Could not set forced weight: ").append(e).append("</font><hr><p>\r\n");
                }
            }
        } else if (req.getParameter("maxSchedule") != null) {
            String m = req.getParameter("maxScheduleCount");
            if (m != null && m.length() > 0) {
                try {
                    int max = Integer.parseInt(m);
                    this.simServer.setMaxAgentScheduled(max);
                    page.append("<hr><p>Agents are limited to be scheduled in " + max + " games in advanced<hr><p>");
                }
                catch (Exception e) {
                    Logger.global.log(Level.WARNING, "AdminPage: could not limit agent scheduling", e);
                    page.append("<hr><p><font color=red>Could not limit the number of games an agent can schedule in advanced: ").append(e).append("</font><hr><p>\r\n");
                }
            }
        } else if (req.getParameter("setServerMessage") != null) {
            String message = this.trim(req.getParameter("message"));
            this.simServer.setServerMessage(message);
            if (message == null) {
                page.append("<hr><p>Server message removed");
            } else {
                page.append("<hr><p>Server message set to '").append(message).append('\'');
            }
            page.append("<p><hr><p>\r\n");
        }
        long serverTime = 0;
        Object serverTimeAsString = null;
        Object timeMessage = null;
        if (serverTime == 0) {
            serverTime = this.infoServer.getServerTimeMillis();
        }
        page.append("<font face=arial size='+1'>Generate Result Page</font><p>\r\n<form method=post>Game ID: <input name=gameID type=text");
        if (gameID > 0) {
            page.append(" value='").append(gameID).append('\'');
        }
        page.append("> \r\n<input type=submit name=generateResults value='Generate Results'></form>\r\n");
        page.append("<font face=arial size='+1'>Generate Competition Results</font><p>\r\n<form method=post>Competition ID: <input name=competitionID type=text");
        if (competitionID > 0) {
            page.append(" value='").append(competitionID).append('\'');
        }
        page.append("> \r\n<input type=submit name=generateCompetition value='Generate Competition Results'></form>\r\n");
        page.append("<font face=arial size='+1'>Competition Force Weight</font>\r\n<form method=post>Force weight <input type=text name=forceWeight> &nbsp; <input type='submit' value='Force' name='Force'> ");
        if (Competition.isWeightForced()) {
            page.append("Weight is currently forced to ").append(Competition.getForcedWeight()).append(". Set to <code>-1</code> to disable forced weight.");
        } else {
            page.append("Weight is currently not forced.");
        }
        page.append("</form>\r\n");
        page.append("<font face=arial size='+1'>Web Join</font>\r\n<form method=post>Limit number of games in advanced <input type=text name=maxScheduleCount> &nbsp; <input type='submit' value='Set' name='maxSchedule'> ");
        int maxSchedule = this.simServer.getMaxAgentScheduled();
        if (maxSchedule > 0) {
            page.append("Currently limited to ").append(maxSchedule).append(". Set to <code>0</code> for unlimited games.");
        } else {
            page.append("No limit.");
        }
        page.append("</form>\r\n");
        page.append("<font face=arial size='+1'>Scratch Game</font><p>\r\n<form method=post><font color=red size='-1'>WARNING: MAKE SURE YOU KNOW WHAT YOU ARE DOING WHEN SCRATCHING GAMES!!!</font><br>Game ID: <input name=scratchID type=text>\r\n<input type=submit name=scratchGame value='Scratch Game'><br><font color=red size='-1'>WARNING: MAKE SURE YOU KNOW WHAT YOU ARE DOING WHEN SCRATCHING GAMES!!!</font></form>\r\n");
        page.append("<p><font face=arial size='+1'>Time Reservation</font><p>\r\n<form method=post>Reserve <input type=text name=reserveLength> minutes starting at <input type=text name=reserveTime value='").append(GameScheduler.formatServerTimeDate(serverTime)).append("'> &nbsp; <input type='submit' value='Reserve' name='reserve'></form>\r\n");
        String message = this.simServer.getServerMessage();
        page.append("<p><font face=arial size='+1'>Server Message</font><p>\r\n<form method=post><textarea cols=30 rows=6 name=message wrap=soft style='width: 90%;'>");
        if (message != null) {
            page.append(message);
        }
        page.append("</textarea><br> <input type='submit' value='Set Server Message' name='setServerMessage'></form>\r\n");
        return this.pageEnd(page);
    }

    private StringBuffer generateGames(HttpRequest req) {
        StringBuffer page = this.pageStart("Game Administration");
        Set params = req.getParameterNames();
        Iterator paramIterator = params.iterator();
        while (paramIterator.hasNext()) {
            String p;
            p = paramIterator.next().toString();
            if (!p.startsWith("remove")) continue;
            p = p.substring(6);
            try {
                try {
                    int uniqSimID = Integer.parseInt(p);
                    this.simServer.removeSimulation(uniqSimID);
                    page.append("<b>Requested that game ").append(uniqSimID).append(" should be removed</b><p>\r\n");
                    p = null;
                }
                catch (Exception e) {
                    p = String.valueOf(p) + ": " + e;
                    if (p == null) break;
                    page.append("<font color=red>Could not remove game ").append(p).append("</font><p>\r\n");
                    break;
                }
            }
            catch (Throwable var7_9) {
                if (p != null) {
                    page.append("<font color=red>Could not remove game ").append(p).append("</font><p>\r\n");
                }
                throw var7_9;
            }
            if (p == null) break;
            page.append("<font color=red>Could not remove game ").append(p).append("</font><p>\r\n");
            break;
        }
        long currentTime = this.infoServer.getServerTimeMillis();
        SimulationInfo[] simulations = this.simServer.getComingSimulations();
        page.append("<p>Current server time is ").append(InfoServer.getServerTimeAsString(currentTime)).append("<p><form method=post>\r\n<table border=1>\r\n<tr><th>Game</th><th>Start Time (Duration)</th><th>Type</th><th>Participants</th><th>Status</th><th>&nbsp;</th>\r\n");
        if (simulations != null) {
            int i = 0;
            int n = simulations.length;
            while (i < n) {
                SimulationInfo g = simulations[i];
                int length = g.getSimulationLength() / 1000;
                int minutes = length / 60;
                int seconds = length % 60;
                page.append("<tr><td>");
                if (g.hasSimulationID()) {
                    page.append(g.getSimulationID());
                } else {
                    page.append('?');
                }
                page.append(" (<em>").append(g.getID()).append("</em>)</td><td>").append(InfoServer.getServerTimeAsString(g.getStartTime())).append(" (").append(minutes).append("&nbsp;min").append(seconds > 0 ? "&nbsp;" + seconds + "&nbsp;sec" : "").append(")</td><td>").append(this.simServer.getSimulationTypeName(g.getType())).append("</td><td>");
                int j = 0;
                int m = g.getParticipantCount();
                while (j < m) {
                    if (j > 0) {
                        page.append(", ");
                    }
                    page.append(this.simServer.getUserName(g, g.getParticipantID(j)));
                    ++j;
                }
                page.append("&nbsp;").append("</td><td>").append(g.getStartTime() <= currentTime ? "Running" : "Coming").append("</td><td>");
                if (g.hasSimulationID()) {
                    page.append("&nbsp;");
                } else {
                    page.append("<input type=submit name='remove").append(g.getID()).append("' value=Remove>");
                }
                page.append("</td></tr>\r\n");
                ++i;
            }
        }
        page.append("</table>\r\n</form>\r\n");
        return this.pageEnd(page);
    }

    private StringBuffer generateCompetition(HttpRequest req) {
        StringBuffer page = this.pageStart("Competition Administration");
        Competition[] competitions = this.simServer.getCompetitions();
        if (req.getParameter("setLastFinished") != null) {
            try {
                int lastID = Integer.parseInt(req.getParameter("lastFinished"));
                this.simServer.setLastFinishedCompetitionID(lastID);
                competitions = this.simServer.getCompetitions();
                page.append("<b>Only  competitions newer than competition id ").append(lastID).append(" will be loaded</b><p>\r\n");
            }
            catch (Exception e) {
                page.append("<font color=red><b>could not parse competition id: ").append(e).append("</b></font><p>\r\n");
            }
        } else if (req.getParameter("changeComp") != null) {
            try {
                int id = Integer.parseInt(req.getParameter("compid"));
                String name = this.trim(req.getParameter("compname"));
                String generator = this.trim(req.getParameter("compgen"));
                Competition competition = this.simServer.getCompetitionByID(id);
                if (name == null) {
                    throw new IllegalArgumentException("no name specified");
                }
                this.simServer.setCompetitionInfo(id, name, generator);
                page.append("<b>Competition ").append(name).append(" (").append(id).append(") has been changed!");
            }
            catch (Exception e) {
                page.append("<font color=red><b>could not change competition: ").append(e).append("</b></font><p>\r\n");
            }
        } else {
            Set params = req.getParameterNames();
            Iterator paramIterator = params.iterator();
            while (paramIterator.hasNext()) {
                String p;
                p = paramIterator.next().toString();
                if (!p.startsWith("remove")) continue;
                p = p.substring(6);
                try {
                    try {
                        int index = Competition.indexOf(competitions, Integer.parseInt(p));
                        if (index >= 0) {
                            Competition c = competitions[index];
                            this.simServer.removeCompetition(c.getID());
                            competitions = this.simServer.getCompetitions();
                            p = null;
                            page.append("<b>Competition ").append(c.getName()).append(" has been removed</b><p>\r\n");
                        } else {
                            p = String.valueOf(p) + ": not found";
                        }
                    }
                    catch (Exception e) {
                        p = String.valueOf(p) + ": " + e;
                        if (p == null) break;
                        page.append("<font color=red>Could not remove competition ").append(p).append("</font><p>\r\n");
                        break;
                    }
                }
                catch (Throwable var9_19) {
                    if (p != null) {
                        page.append("<font color=red>Could not remove competition ").append(p).append("</font><p>\r\n");
                    }
                    throw var9_19;
                }
                if (p == null) break;
                page.append("<font color=red>Could not remove competition ").append(p).append("</font><p>\r\n");
                break;
            }
        }
        page.append("<form method=post>\r\n<table border=1>\r\n<tr><th>ID</th><th>Name</th><th>Start Time</th><th>End time</th><th>Game IDs</th><th>Agents/Games</th><th>&nbsp;</th>\r\n");
        if (competitions == null) {
            page.append("<tr><td colspan=7><em>No competitions found</em></td></tr>\r\n");
        } else {
            int i = 0;
            int n = competitions.length;
            while (i < n) {
                Competition comp = competitions[i];
                page.append("<tr><td>");
                if (comp.hasParentCompetition()) {
                    page.append(comp.getParentCompetitionID()).append(" -&gt; ");
                }
                if (this.historyPath != null) {
                    page.append("<a href='").append(this.historyPath).append("competition/").append(comp.getID()).append("/'>").append(comp.getID()).append("</a>");
                } else {
                    page.append(comp.getID());
                }
                page.append("</td><td><a href='?edit=").append(comp.getID()).append("'>").append(comp.getName()).append("</a></td><td>").append(InfoServer.getServerTimeAsString(comp.getStartTime())).append("</td><td>").append(InfoServer.getServerTimeAsString(comp.getEndTime())).append("</td><td>");
                if (comp.hasSimulationID()) {
                    page.append(comp.getStartSimulationID()).append(" - ").append(comp.getEndSimulationID());
                } else {
                    page.append("? - ?");
                }
                page.append(" (<em>").append(comp.getStartUniqueID()).append(" - ").append(comp.getEndUniqueID()).append("</em>)</td><td>").append(comp.getParticipantCount()).append(" / ").append(comp.getSimulationCount()).append("</td><td><input type=submit name='remove").append(comp.getID()).append("' value=Remove></td></tr>\r\n");
                ++i;
            }
        }
        page.append("</table>\r\n<p>Do not load competitions with this id or older: <input type=text name=lastFinished value='").append(this.simServer.getLastFinishedCompetitionID()).append("'> <input type=submit name='setLastFinished' value='Set'>\r\n</form>\r\n");
        String editID = req.getParameter("edit");
        if (editID != null) {
            try {
                int id = Integer.parseInt(editID);
                Competition competition = this.simServer.getCompetitionByID(id);
                if (competition == null) {
                    throw new IllegalArgumentException("could not find competition " + id);
                }
                page.append("<font face='arial' size='+1'>Edit competition ").append(competition.getName()).append("</font><p>\r\n<form method=post><input type=hidden name=compid value='").append(id).append("'>\r\n<p><table borde='0'><tr><td>Competition name:</td><td><input type=text size=32 name=compname value='").append(competition.getName()).append("'></td></tr><td>Competition score table generator</td><td><input type=text size=32 name=compgen value='");
                String generator = competition.getScoreClassName();
                if (generator != null) {
                    page.append(generator);
                }
                page.append("'></td></tr></table>\r\n<p><font face='arial' size='+1'>Agents in competition ").append(competition.getName()).append("</font><p>\r\n");
                CompetitionParticipant[] pUsers = competition.getParticipants();
                if (pUsers != null) {
                    int i = 0;
                    int n = pUsers.length;
                    while (i < n) {
                        if (i > 0) {
                            page.append(", ");
                        }
                        page.append(pUsers[i].getName());
                        ++i;
                    }
                }
                page.append("<p><input type=submit name=changeComp value='Set Changes!'></form>\r\n");
            }
            catch (Exception e) {
                page.append("<p><font color=red>Could not view competition ").append(editID).append(": ").append(e).append("</font>");
            }
        }
        return this.pageEnd(page);
    }

    private String trim(String text) {
        return text != null && (text = text.trim()).length() > 0 ? text : null;
    }
}

