/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.is.common;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.isl.util.FormatUtils;
import se.sics.tasim.is.common.Competition;
import se.sics.tasim.is.common.CompetitionParticipant;
import se.sics.tasim.is.common.InfoServer;
import se.sics.tasim.is.common.ScoreGenerator;

public class DefaultScoreGenerator
extends ScoreGenerator {
    private static final Logger log = Logger.getLogger(DefaultScoreGenerator.class.getName());
    private int agentsToAdvance = 0;
    private String advanceColor = null;
    private boolean isShowingCompetitionTimes = true;
    private boolean isShowingAllAgents = false;
    private boolean isShowingAvgScoreWhenWeighted = true;
    private boolean isShowingWeightedAvgScoreWithoutZeroGames = false;
    private boolean isShowingAvgScoreWithoutZeroGames = false;
    private boolean isShowingZeroGameAgents = true;
    private boolean isAddingLastUpdated = true;
    private boolean isAddingStatisticsLink = true;
    private boolean isIgnoringWeight = false;

    public int getAgentsToAdvance() {
        return this.agentsToAdvance;
    }

    public void setAgentsToAdvance(int agentsToAdvance) {
        this.agentsToAdvance = agentsToAdvance;
    }

    public String getAdvanceColor() {
        return this.advanceColor;
    }

    public void setAdvanceColor(String advanceColor) {
        this.advanceColor = advanceColor;
    }

    public boolean isShowingCompetitionTimes() {
        return this.isShowingCompetitionTimes;
    }

    public void setShowingCompetitionTimes(boolean isShowingCompetitionTimes) {
        this.isShowingCompetitionTimes = isShowingCompetitionTimes;
    }

    public boolean isShowingAllAgents() {
        return this.isShowingAllAgents;
    }

    public void setShowingAllAgents(boolean isShowingAllAgents) {
        this.isShowingAllAgents = isShowingAllAgents;
    }

    public boolean isShowingAverageScoreWhenWeighted() {
        return this.isShowingAvgScoreWhenWeighted;
    }

    public void setShowingAverageScoreWhenWeighted(boolean isShowingAvgScoreWhenWeighted) {
        this.isShowingAvgScoreWhenWeighted = isShowingAvgScoreWhenWeighted;
    }

    public boolean isShowingAverageScoreWithoutZeroGames() {
        return this.isShowingAvgScoreWithoutZeroGames;
    }

    public void setShowingAverageScoreWithoutZeroGames(boolean isShowingAvgScoreWithoutZeroGames) {
        this.isShowingAvgScoreWithoutZeroGames = isShowingAvgScoreWithoutZeroGames;
    }

    public boolean isShowingWeightedAverageScoreWithoutZeroGames() {
        return this.isShowingWeightedAvgScoreWithoutZeroGames;
    }

    public void setShowingWeightedAverageScoreWithoutZeroGames(boolean isShowingWeightedAvgScoreWithoutZeroGames) {
        this.isShowingWeightedAvgScoreWithoutZeroGames = isShowingWeightedAvgScoreWithoutZeroGames;
    }

    public boolean isShowingZeroGameAgents() {
        return this.isShowingZeroGameAgents;
    }

    public void setShowingZeroGameAgents(boolean isShowingZeroGameAgents) {
        this.isShowingZeroGameAgents = isShowingZeroGameAgents;
    }

    public boolean isAddingLastUpdated() {
        return this.isAddingLastUpdated;
    }

    public void setAddingLastUpdated(boolean isAddingLastUpdated) {
        this.isAddingLastUpdated = isAddingLastUpdated;
    }

    public boolean isAddingStatisticsLink() {
        return this.isAddingStatisticsLink;
    }

    public void setAddingStatisticsLink(boolean isAddingStatisticsLink) {
        this.isAddingStatisticsLink = isAddingStatisticsLink;
    }

    public boolean isIgnoringWeight() {
        return this.isIgnoringWeight;
    }

    public void setIgnoringWeight(boolean isIgnoringWeight) {
        this.isIgnoringWeight = isIgnoringWeight;
    }

    @Override
    public boolean createScoreTable(Competition competition, int gameID) {
        String scoreFile = this.getScoreFileName();
        try {
            String serverName = this.getServerName();
            boolean isWeightUsed = !this.isIgnoringWeight && competition.isWeightUsed();
            boolean isShowingZeroGameAgents = this.isShowingAllAgents || this.isShowingZeroGameAgents;
            StringBuffer page = new StringBuffer();
            page.append("<html><head><title>TAC SIM - Score Page for ").append(competition.getName()).append("</title></head>\r\n<body>\r\n");
            CompetitionParticipant[] users = this.getCombinedParticipants(competition);
            if (users != null) {
                users = (CompetitionParticipant[])users.clone();
                Arrays.sort(users, this.getComparator(isWeightUsed));
                page.append("<h3>Scores");
                if (competition != null) {
                    page.append(" for ").append(competition.getName());
                    if (competition.hasSimulationID()) {
                        page.append(" (game ");
                        this.addCompetitionSimulationIDs(page, competition);
                        page.append(')');
                    }
                }
                if (serverName != null) {
                    page.append(" at ").append(serverName);
                }
                page.append("</h3>\r\n");
                if (this.isShowingCompetitionTimes) {
                    page.append("<em>");
                    if (competition.hasSimulationID() && gameID >= competition.getStartSimulationID()) {
                        page.append("Competition started at ");
                    } else {
                        page.append("Competition starts at ");
                    }
                    page.append(this.getRootStartTime(competition));
                    if (competition.hasSimulationID() && gameID >= competition.getEndSimulationID()) {
                        page.append(" and ended at ");
                    } else {
                        page.append(" and ends at ");
                    }
                    page.append(InfoServer.getServerTimeAsString(competition.getEndTime())).append(".</em><br>\r\n");
                }
                page.append("<table border=1>\r\n<tr><th>Position</th><th>Agent</th>");
                if (isWeightUsed) {
                    page.append("<th>Average Weighted Score</th>");
                    if (this.isShowingWeightedAvgScoreWithoutZeroGames) {
                        page.append("<th>Average Weighted Score - Zero</th>");
                    }
                }
                if (this.isShowingAvgScoreWhenWeighted || !isWeightUsed) {
                    page.append("<th>Average Score</th>");
                    if (this.isShowingAvgScoreWithoutZeroGames) {
                        page.append("<th>Average Score - Zero</th>");
                    }
                }
                page.append("<th>Games Played</th>");
                if (isShowingZeroGameAgents) {
                    page.append("<th>Zero Games</th>");
                }
                page.append("</tr>\r\n");
                int pos = 1;
                int i = 0;
                int n = users.length;
                while (i < n) {
                    CompetitionParticipant usr = users[i];
                    if (this.isShowingAllAgents || (isShowingZeroGameAgents ? usr.getGamesPlayed() > 0 : usr.getGamesPlayed() > usr.getZeroGamesPlayed())) {
                        String td;
                        String tdright;
                        String userName = this.createUserName(usr, pos, n);
                        String rankColor = this.getRankColor(usr, pos, n);
                        String color = this.getAgentColor(usr, pos, n);
                        if (color != null) {
                            td = "<td bgcolor='" + color + "'>";
                            tdright = "<td bgcolor='" + color + "' align=right>";
                        } else {
                            td = "<td>";
                            tdright = "<td align=right>";
                        }
                        String tdrank = rankColor != null ? "<td bgcolor='" + rankColor + "'>" : td;
                        page.append("<tr>").append(tdrank).append(pos++).append("</td>").append(td).append(userName);
                        if (isWeightUsed) {
                            page.append("</td>").append(tdright).append(FormatUtils.formatAmount((long)usr.getAvgWeightedScore()));
                            if (this.isShowingWeightedAvgScoreWithoutZeroGames) {
                                page.append("</td>").append(tdright).append(FormatUtils.formatAmount((long)usr.getAvgWeightedScoreWithoutZeroGames()));
                            }
                        }
                        if (this.isShowingAvgScoreWhenWeighted || !isWeightUsed) {
                            page.append("</td>").append(tdright).append(FormatUtils.formatAmount((long)usr.getAvgScore()));
                            if (this.isShowingAvgScoreWithoutZeroGames) {
                                page.append("</td>").append(tdright).append(FormatUtils.formatAmount((long)usr.getAvgScoreWithoutZeroGames()));
                            }
                        }
                        page.append("</td>").append(tdright).append(usr.getGamesPlayed()).append("</td>");
                        if (isShowingZeroGameAgents) {
                            page.append(tdright).append(usr.getZeroGamesPlayed()).append("</td>");
                        }
                        page.append("</tr>\r\n");
                    }
                    ++i;
                }
                page.append("</table>\r\n");
                this.addPostInfo(page);
                if (isShowingZeroGameAgents) {
                    page.append("<p><b>Zero Games</b> is the number of games that resulted in a score of zero (probably due to inactivity).");
                }
                if (this.isShowingAvgScoreWithoutZeroGames || isWeightUsed && this.isShowingWeightedAvgScoreWithoutZeroGames) {
                    page.append(isShowingZeroGameAgents ? "<br>" : "<p>");
                    page.append("<b>- Zero</b> is the score without zero score games");
                }
                page.append("<br>\r\n");
            } else {
                page.append("No TAC agents registered\r\n");
            }
            if (this.isAddingLastUpdated) {
                this.addLastUpdated(page);
            }
            page.append("</body>\r\n</html>\r\n");
            FileWriter out = new FileWriter(scoreFile);
            out.write(page.toString());
            out.close();
            return true;
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "could not create score page for game " + gameID + " in " + scoreFile, e);
            return false;
        }
    }

    private void addCompetitionSimulationIDs(StringBuffer page, Competition competition) {
        Competition parentCompetition = competition.getParentCompetition();
        if (parentCompetition != null && parentCompetition.hasSimulationID()) {
            this.addCompetitionSimulationIDs(page, parentCompetition);
            page.append(", ");
        }
        page.append(competition.getStartSimulationID()).append(" - ").append(competition.getEndSimulationID());
    }

    private String getRootStartTime(Competition competition) {
        Competition parentCompetition = competition.getParentCompetition();
        if (parentCompetition != null) {
            return this.getRootStartTime(parentCompetition);
        }
        return InfoServer.getServerTimeAsString(competition.getStartTime());
    }

    protected Comparator getComparator(boolean isWeightUsed) {
        return isWeightUsed ? CompetitionParticipant.getAvgWeightedComparator() : CompetitionParticipant.getAvgComparator();
    }

    protected String getRankColor(CompetitionParticipant agent, int pos, int numberOfAgents) {
        return null;
    }

    protected String getAgentColor(CompetitionParticipant agent, int pos, int numberOfAgents) {
        if (pos <= this.agentsToAdvance) {
            return this.advanceColor;
        }
        return null;
    }

    protected String createUserName(CompetitionParticipant usr, int pos, int numberOfAgents) {
        if (this.isAddingStatisticsLink) {
            return "<a href='" + usr.getID() + ".html'>" + this.getAgentNameStyle(usr.getName(), pos, numberOfAgents) + "</a>";
        }
        return usr.getName();
    }

    protected String getAgentNameStyle(String agentName, int pos, int numberOfAgents) {
        return agentName;
    }

    protected void addLastUpdated(StringBuffer page) {
        page.append("<p><hr>\r\n<em>Table last updated ");
        SimpleDateFormat dFormat = new SimpleDateFormat("dd MMM HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        dFormat.setTimeZone(new SimpleTimeZone(0, "UTC"));
        page.append(dFormat.format(date));
        page.append("</em>\r\n");
    }

    protected void addPostInfo(StringBuffer page) {
    }
}

