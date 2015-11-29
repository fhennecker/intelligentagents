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

import com.botbox.html.HtmlWriter;
import com.botbox.util.ArrayQueue;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mortbay.http.HttpException;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import org.mortbay.util.ByteArrayISO8859Writer;
import se.sics.tasim.is.SimulationInfo;
import se.sics.tasim.is.common.Competition;
import se.sics.tasim.is.common.HttpPage;
import se.sics.tasim.is.common.InfoServer;
import se.sics.tasim.is.common.SimServer;

public class ComingPage
extends HttpPage {
    private static final Logger log = Logger.getLogger(ComingPage.class.getName());
    private static final int MAX_VISIBLE_SIMULATIONS = 100;
    private static final int TIMEOUT = 5000;
    private final InfoServer infoServer;
    private final SimServer simServer;
    private ArrayQueue eventQueue = new ArrayQueue();
    private String timeLimitedMessage;
    private long timeLimit;

    public ComingPage(InfoServer infoServer, SimServer simServer) {
        this.infoServer = infoServer;
        this.simServer = simServer;
    }

    public void setTimeLimitedMessage(String message, long timeLimit) {
        this.timeLimitedMessage = message;
        this.timeLimit = timeLimit;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    @Override
    public void handle(String pathInContext, String pathParams, HttpRequest request, HttpResponse response) throws HttpException, IOException {
        userName = request.getAuthUser();
        userID = this.infoServer.getUserID(userName);
        agentID = 0;
        message = null;
        simType = this.infoServer.getDefaultSimulationType();
        if (!"POST".equals(request.getMethod())) ** GOTO lbl60
        if (request.getParameter("createsim") == null) ** GOTO lbl24
        if (this.simServer.isWebJoinActive()) {
            event = new Event(simType);
            this.addEvent(event);
            this.simServer.createSimulation(simType, null);
            message = Event.access$0(event);
            if (message == null) {
                this.removeEvent(event);
                message = "<font color=red>Could not create the game at this time. Please try again later.</font>\r\n";
            }
        } else {
            message = "<font color=red>Could not create the game at this time. Please try again later.</font>\r\n";
        }
        if ((agentNoStr = request.getParameter("agent_no")) == null) ** GOTO lbl60
        try {
            agentID = Integer.parseInt(request.getParameter("agent_no")) + userID;
        }
        catch (Exception var11_12) {}
        ** GOTO lbl60
lbl24: // 1 sources:
        names = request.getParameterNames();
        for (String name : names) {
            if (!name.startsWith("jg_")) continue;
            if (this.simServer.isWebJoinActive()) {
                sim = null;
                try {
                    uniqSimID = Integer.parseInt(name.substring(3));
                    maxScheduled = this.simServer.getMaxAgentScheduled();
                    agentID = Integer.parseInt(request.getParameter("agent_no")) + userID;
                    sim = this.simServer.getSimulationInfo(uniqSimID);
                    if (sim == null) {
                        message = "Could not find game with id " + uniqSimID;
                        break;
                    }
                    if (sim.isParticipant(agentID)) {
                        message = "Agent " + this.simServer.getUserName(sim, agentID) + " is already participating in game " + this.getSimIDAsString(sim);
                        break;
                    }
                    if (sim.isFull()) {
                        message = "Game " + this.getSimIDAsString(sim) + " is already full";
                        break;
                    }
                    if (maxScheduled > 0 && (scheduledCount = this.simServer.getAgentScheduledCount(agentID)) >= maxScheduled) {
                        message = "Your agent is already scheduled in " + scheduledCount + " games. " + "Please do not schedule your agent in many games " + "in advanced since it makes it hard for other " + "teams to practice.";
                        continue;
                    }
                    event = new Event(sim.getID(), agentID);
                    this.addEvent(event);
                    this.simServer.joinSimulation(uniqSimID, agentID, null);
                    message = Event.access$0(event);
                    if (message != null) continue;
                    this.removeEvent(event);
                    message = "Failed to join game " + this.getSimIDAsString(sim);
                }
                catch (Exception e) {
                    message = "Could not join game " + this.getSimIDAsString(sim) + ": " + e.getMessage();
                    ComingPage.log.log(Level.WARNING, message, e);
                }
                continue;
            }
            message = "<font color=red>Could not join the game at this time. Please try again later.</font>\r\n";
            break;
        }
lbl60: // 9 sources:
        this.displayPage(userName, userID, agentID, response, simType, message);
    }

    private String getSimIDAsString(SimulationInfo info) {
        if (info == null) {
            return "";
        }
        int gid = info.getSimulationID();
        return gid <= 0 ? "starting at " + InfoServer.getServerTimeAsString(info.getStartTime()) : Integer.toString(gid);
    }

    private void displayPage(String userName, int userID, int agentID, HttpResponse response, String simType, String message) throws HttpException, IOException {
        String serverMessage = this.simServer.getServerMessage();
        String timeLimitedMessage = this.timeLimitedMessage;
        Competition currentComp = this.simServer.getCurrentCompetition();
        boolean allowJoin = currentComp == null && this.simServer.isWebJoinActive();
        SimulationInfo[] simulations = this.simServer.getComingSimulations();
        int simulationsLen = simulations == null ? 0 : simulations.length;
        long currentTime = this.infoServer.getServerTimeMillis();
        String serverName = this.simServer.getServerName();
        String title = "Coming Games at " + serverName;
        HtmlWriter page = new HtmlWriter();
        page.pageStart(title).h2(title).text("The coming game page is used to view and create TAC games. To view games click <a href='../viewer/' target=tacviewer><b>Launch Game Viewer</b></a> (requires <a href='http://java.sun.com/plugin/' target='_top'>J2SDK 1.4 Plugin</a>). ");
        simType = this.infoServer.getDefaultSimulationType();
        if (allowJoin && simulationsLen <= 100) {
            page.text("To create a game, click on the <b>Create Game</b> button below.");
        }
        page.p();
        if (serverMessage != null) {
            page.text(serverMessage).p();
        }
        page.text("<hr noshade color='#202080'>\r\n");
        if (timeLimitedMessage != null) {
            if (this.timeLimit <= currentTime) {
                this.timeLimitedMessage = null;
            } else {
                page.text(timeLimitedMessage).p();
            }
        }
        if (message != null) {
            page.h3(message);
        }
        if (allowJoin) {
            page.text("<form method=post>");
        }
        if (simulationsLen == 0) {
            page.text("<p>Current server time is ").text(InfoServer.getServerTimeAsString(currentTime)).text("<p>No games scheduled\r\n");
        } else {
            Competition nextCompetition = null;
            String nextCompetitionStarts = null;
            int numberOfSimulations = simulationsLen;
            int minAgentID = userID;
            int maxAgentID = minAgentID + 10;
            int startSimulation = -1;
            int endSimulation = -1;
            if (currentComp != null) {
                page.text("<p><h3>Playing competition ").text(currentComp.getName());
                if (currentComp.hasSimulationID()) {
                    page.text(" (game ").text(currentComp.getStartSimulationID()).text(" - ").text(currentComp.getEndSimulationID()).text(')');
                }
                page.text("</h3>\r\n").text("<em>").text("Competition started at ").text(InfoServer.getServerTimeAsString(currentComp.getStartTime())).text(" and ends at ").text(InfoServer.getServerTimeAsString(currentComp.getEndTime())).text(".</em>\r\n");
                endSimulation = currentComp.getEndUniqueID();
            } else {
                nextCompetition = this.simServer.getNextCompetition();
                if (nextCompetition != null) {
                    nextCompetitionStarts = InfoServer.getServerTimeAsString(nextCompetition.getStartTime());
                    page.text("<b>Next competition '").text(nextCompetition.getName()).text("' begins at ").text(nextCompetitionStarts).text("</b><p>\r\n");
                    startSimulation = nextCompetition.getStartUniqueID();
                    endSimulation = nextCompetition.getEndUniqueID();
                }
            }
            if (allowJoin) {
                page.text("Select agent for joining: <select name=agent_no><option value=0>").text(userName);
                int id = agentID;
                if (id > 0 && ((id -= userID) < 0 || id > 10)) {
                    id = 0;
                }
                int i = 1;
                while (i < 11) {
                    page.text("<option value=").text(i);
                    if (i == id) {
                        page.text(" selected");
                    }
                    page.text('>').text(userName).text(i - 1).text("</option>\r\n");
                    ++i;
                }
                page.text("</select>");
            }
            page.text("<p>Current server time is ").text(InfoServer.getServerTimeAsString(currentTime));
            page.text("\r\n<table border=1><tr><th>ID</th><th>Time</th><th>Type</th><th>Participants</th><th>Status</th><th>Join</th></tr\r\n>");
            if (numberOfSimulations > 100) {
                numberOfSimulations = 100;
            }
            int i = 0;
            int n = numberOfSimulations;
            while (i < n) {
                String columnStart;
                boolean isRunning;
                SimulationInfo simulation = simulations[i];
                int uniqSimulationID = simulation.getID();
                int simulationID = simulation.getSimulationID();
                boolean bl = isRunning = simulation.getStartTime() < currentTime;
                if (isRunning) {
                    columnStart = "<td bgcolor='#e0e0ff'>";
                } else {
                    columnStart = "<td>";
                    if (startSimulation == uniqSimulationID && nextCompetition != null) {
                        page.text("<tr><td bgcolor='#e0e0ff' colspan=6>&nbsp;</td></tr><tr><td colspan=6 align=center><font size=+1 color='#800000'><b>Competition ").text(nextCompetition.getName()).text(" begins");
                        if (nextCompetitionStarts != null) {
                            page.text(" (").text(nextCompetitionStarts).text(')');
                        }
                        page.text("</b></font></td></tr\r\n><tr><td bgcolor='#e0e0ff' colspan=6>&nbsp;</td></tr>");
                    }
                }
                page.text("<tr>").text(columnStart);
                if (simulationID > 0) {
                    page.text(simulationID);
                } else {
                    page.text("&nbsp;");
                }
                page.text("</td>").text(columnStart);
                this.appendTimeMillis(page, simulation.getStartTime()).text("- ");
                this.appendTimeMillis(page, simulation.getEndTime()).text("</td>").text(columnStart);
                page.text(this.simServer.getSimulationTypeName(simulation.getType())).text("</td>").text(columnStart);
                int p = 0;
                int np = simulation.getParticipantCount();
                while (p < np) {
                    int participant = simulation.getParticipantID(p);
                    if (p > 0) {
                        page.text(", ");
                    }
                    if (participant >= minAgentID && participant <= maxAgentID) {
                        page.text("<font size=+1 color='#800000'><b>").text(this.simServer.getUserName(simulation, participant)).text("</b></font>");
                    } else if (participant < 0) {
                        page.tag("em").text(this.simServer.getUserName(simulation, participant)).tagEnd("em");
                    } else {
                        page.text(this.simServer.getUserName(simulation, participant));
                    }
                    ++p;
                }
                page.text("&nbsp;</td>").text(columnStart).text(isRunning ? "Running" : "Coming").text("</td>").text(columnStart);
                if (isRunning || !allowJoin || simulation.isFull()) {
                    page.text("&nbsp;");
                } else {
                    page.text("<input type=submit value='Join' name='jg_").text(uniqSimulationID).text("'>");
                }
                page.text("</td></tr\r\n>");
                if (endSimulation == uniqSimulationID) {
                    page.text("<tr><td bgcolor='#e0e0ff' colspan=6>&nbsp;</td></tr><tr><td colspan=6 align=center><font size=+1 color='#800000'><b>Competition ");
                    if (currentComp != null) {
                        page.text(currentComp.getName());
                    } else if (nextCompetition != null) {
                        page.text(nextCompetition.getName());
                    }
                    page.text(" ends</b></font></td></tr\r\n><tr><td bgcolor='#e0e0ff' colspan=6>&nbsp;</td></tr>");
                }
                ++i;
            }
            page.text("</table>");
            if (numberOfSimulations < simulationsLen) {
                page.text("<br><em>(Only showing the first 100 of the coming ").text(simulationsLen).text(" games)</em>");
            }
        }
        if (allowJoin && simulationsLen <= 100) {
            Object simTypes = null;
            page.p();
            page.text("<input type=hidden value='" + simType + "' name='simType'>");
            page.text("<input type=submit value='Create Game' name='createsim'>\r\n</form>\r\n");
        }
        page.text("<p><hr noshade color='#202080'>\r\n<center><font face='Arial,Helvetica,sans-serif' size='-2'>" + this.infoServer.getServerType() + " " + this.infoServer.getVersion() + "</font></center>\r\n");
        page.close();
        ByteArrayISO8859Writer writer = new ByteArrayISO8859Writer();
        page.write((Writer)writer);
        response.setContentType("text/html");
        response.setContentLength(writer.size());
        writer.writeTo(response.getOutputStream());
        response.commit();
    }

    private HtmlWriter appendTimeMillis(HtmlWriter page, long td) {
        long sek = (td /= 1000) % 60;
        long minutes = td / 60 % 60;
        long hours = td / 3600 % 24;
        if (hours < 10) {
            page.text('0');
        }
        page.text(hours).text(':');
        if (minutes < 10) {
            page.text('0');
        }
        page.text(minutes).text(':');
        if (sek < 10) {
            page.text('0');
        }
        page.text(sek);
        return page;
    }

    private synchronized void addEvent(Event event) {
        this.eventQueue.add(event);
    }

    private synchronized void removeEvent(Event event) {
        int index = this.eventQueue.indexOf(event);
        if (index >= 0) {
            this.eventQueue.remove(index);
        }
    }

    public synchronized void simulationCreated(SimulationInfo info) {
        String simType = info.getType();
        int i = 0;
        int n = this.eventQueue.size();
        while (i < n) {
            Event event = (Event)this.eventQueue.get(i);
            if (simType.equals(event.simType)) {
                String message = "A new game starting at " + InfoServer.getServerTimeAsString(info.getStartTime()) + " was created";
                this.eventQueue.remove(i);
                event.notifyResult(message);
                break;
            }
            ++i;
        }
    }

    public synchronized void simulationJoined(int uniqSimID, int agentID) {
        int i = 0;
        int n = this.eventQueue.size();
        while (i < n) {
            Event event = (Event)this.eventQueue.get(i);
            if (uniqSimID == event.simID && agentID == event.agentID && event.simType == null) {
                this.eventQueue.remove(i);
                --i;
                --n;
                event.notifyResult(String.valueOf(this.simServer.getUserName(null, agentID)) + " successfully joined coming game.");
            }
            ++i;
        }
    }

    private static class Event {
        public String simType;
        public int simID;
        public int agentID;
        private String message;

        public Event(String simType) {
            this.simType = simType;
        }

        public Event(int simID, int agentID) {
            this.simID = simID;
            this.agentID = agentID;
        }

        public synchronized void notifyResult(String message) {
            this.message = message;
            this.notify();
        }

        private synchronized String waitForResult() {
            if (this.message == null) {
                try {
                    this.wait(5000);
                }
                catch (Exception var1_1) {
                    // empty catch block
                }
            }
            return this.message;
        }

        static /* synthetic */ String access$0(Event event) {
            return event.waitForResult();
        }
    }

}

