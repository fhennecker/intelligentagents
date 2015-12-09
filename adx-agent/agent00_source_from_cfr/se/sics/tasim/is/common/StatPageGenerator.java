/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.is.common;

import com.botbox.html.HtmlWriter;
import com.botbox.util.ArrayUtils;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import se.sics.isl.db.DBMatcher;
import se.sics.isl.db.DBResult;
import se.sics.isl.db.DBTable;
import se.sics.isl.util.FormatUtils;
import se.sics.tasim.is.common.Competition;
import se.sics.tasim.is.common.CompetitionParticipant;

public class StatPageGenerator {
    private static final Logger log = Logger.getLogger(StatPageGenerator.class.getName());
    private static final int WIDTH = 580;
    private static final int HEIGHT = 320;
    private static final int MX = 24;
    private static final int MY = 20;
    private static final int TXT = 40;
    private static final Color LIGHTGRAY = new Color(210, 210, 210);
    private static boolean hasGUI = true;
    private static int averageCount = 15;

    public static boolean createImage(String file, double[] results) {
        return StatPageGenerator.createImage(file, results, 0, results.length);
    }

    public static boolean createImage(String file, double[] scores, int start, int end) {
        Graphics2D g2d;
        BufferedImage image;
        if (!hasGUI || end <= start) {
            return false;
        }
        float avgPos = (float)averageCount / 2.0f;
        int noLines = 10;
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        int len = end - start;
        int i = start;
        while (i < end) {
            double score = scores[i];
            if (max < score) {
                max = score;
            }
            if (min > score) {
                min = score;
            }
            ++i;
        }
        if (max < 100.0) {
            max = 100.0;
        }
        if (min > 0.0) {
            min = 0.0;
        }
        long tagSpacing = 2000;
        double interval = max - min;
        int zeroLine = min < 0.0 ? (int)(20.0 + 280.0 * max / interval) : 300;
        int noZeros = (int)(Math.log(interval) / Math.log(10.0));
        int ceil = (int)Math.ceil(interval / Math.pow(10.0, noZeros));
        long newInterval = (long)((double)ceil * Math.pow(10.0, noZeros));
        noLines = ceil > 4 ? ceil : (ceil > 2 ? ceil * 2 : ceil * 4);
        tagSpacing = newInterval / (long)noLines;
        while ((double)(newInterval - tagSpacing) > interval) {
            newInterval -= tagSpacing;
            --noLines;
        }
        interval = newInterval;
        float resolution = 280.0f / (1.0f * (float)noLines * (float)tagSpacing);
        float xResolution = 492.0f / (float)len;
        try {
            image = new BufferedImage(580, 320, 1);
            g2d = image.createGraphics();
        }
        catch (InternalError e) {
            log.log(Level.SEVERE, "could not access graphics environment", e);
            hasGUI = false;
            return false;
        }
        g2d.setBackground(Color.white);
        g2d.setColor(Color.black);
        g2d.clearRect(0, 0, 580, 320);
        int low = (int)((double)(noLines + 1) * min / interval);
        int hi = (int)((double)(noLines + 1) * max / interval);
        long i2 = low;
        while (i2 <= (long)hi) {
            long score = i2 * tagSpacing;
            int ypos = zeroLine - (int)(resolution * (float)score);
            g2d.setColor(LIGHTGRAY);
            g2d.drawLine(64, ypos, 556, ypos);
            g2d.setColor(Color.black);
            g2d.drawString(FormatUtils.formatAmount(i2 * tagSpacing), 4, 4 + ypos);
            ++i2;
        }
        g2d.drawLine(64, 20, 64, 300);
        g2d.drawLine(64, zeroLine, 556, zeroLine);
        g2d.drawLine(64, 20, 68, 26);
        g2d.drawLine(64, 20, 60, 26);
        g2d.drawLine(556, zeroLine, 550, zeroLine + 4);
        g2d.drawLine(556, zeroLine, 550, zeroLine - 4);
        float med = 0.0f;
        float oldMed = 0.0f;
        int medP = -1;
        int oldMedP = -1;
        int i3 = 0;
        int n = len;
        while (i3 < n) {
            int xp = (int)(64.0f + (float)i3 * xResolution);
            double sc = scores[start + i3];
            med = (float)((double)med + sc);
            if (i3 >= averageCount) {
                oldMed = med;
                med = (float)((double)med - scores[start + i3 - averageCount]);
                oldMedP = medP;
                medP = zeroLine - (int)(resolution * med / (float)averageCount);
                if (oldMedP == -1) {
                    oldMedP = medP;
                }
                g2d.setColor(Color.red);
                g2d.drawLine(xp - (int)((avgPos + 1.0f) * xResolution), oldMedP, xp - (int)(avgPos * xResolution), medP);
            }
            if (sc < min) {
                sc = min;
            }
            int yp = zeroLine - (int)((double)resolution * sc);
            g2d.setColor(Color.black);
            g2d.drawLine(xp - 1, yp - 1, xp + 1, yp + 1);
            g2d.drawLine(xp + 1, yp - 1, xp - 1, yp + 1);
            ++i3;
        }
        try {
            return ImageIO.write((RenderedImage)image, "png", new File(file));
        }
        catch (Exception ioe) {
            log.log(Level.SEVERE, "could not write statistics image " + file, ioe);
            return false;
        }
    }

    public static boolean generateStatisticsPage(DBTable compResults, String path, String urlGamePath, Competition competition, CompetitionParticipant user, boolean generateImage) {
        if (competition == null) {
            return false;
        }
        log.fine("Generating statistics page for " + user.getName());
        try {
            String imageFile;
            String file = String.valueOf(path) + user.getID() + ".html";
            HtmlWriter out = new HtmlWriter(new BufferedWriter(new FileWriter(file)));
            ArrayList<Competition> comps = null;
            if (competition.hasParentCompetition()) {
                Competition parentCompetition = competition.getParentCompetition();
                user = new CompetitionParticipant(user);
                comps = new ArrayList<Competition>();
                comps.add(competition);
                while (parentCompetition != null) {
                    CompetitionParticipant u = parentCompetition.getParticipantByID(user.getID());
                    if (u != null) {
                        user.addScore(u);
                    }
                    comps.add(parentCompetition);
                    parentCompetition = parentCompetition.getParentCompetition();
                }
            }
            int numGames = competition.getSimulationCount();
            int lastNumberOfGames = 0;
            int[] gameIDs = new int[numGames];
            double[] scores = new double[numGames];
            int[] gameFlags = new int[numGames];
            Object scratchedGames = null;
            int scratchedCount = 0;
            long zeroScore = 0;
            int zeroScoreCount = 0;
            do {
                Competition currentCompetition;
                if ((numGames = (currentCompetition = comps != null ? (Competition)comps.remove(comps.size() - 1) : competition).getSimulationCount()) > gameIDs.length - lastNumberOfGames) {
                    gameIDs = ArrayUtils.setSize(gameIDs, lastNumberOfGames + numGames);
                    scores = ArrayUtils.setSize(scores, lastNumberOfGames + numGames);
                    gameFlags = ArrayUtils.setSize(gameFlags, lastNumberOfGames + numGames);
                }
                DBMatcher dbm = new DBMatcher();
                dbm.setInt("competition", currentCompetition.getID());
                dbm.setInt("participantid", user.getID());
                dbm.setLimit(numGames);
                DBResult res = compResults.select(dbm);
                while (res.next()) {
                    int gameID = res.getInt("simid");
                    int flags = res.getInt("flags");
                    double agentScore = res.getDouble("score");
                    if ((flags & 16) == 0) {
                        gameIDs[lastNumberOfGames] = gameID;
                        gameFlags[lastNumberOfGames] = flags;
                        scores[lastNumberOfGames] = agentScore;
                        ++lastNumberOfGames;
                        if (agentScore != 0.0 && (flags & 32) == 0) continue;
                        zeroScore = (long)((double)zeroScore + agentScore);
                        ++zeroScoreCount;
                        continue;
                    }
                    if (scratchedGames == null) {
                        scratchedGames = new int[numGames - lastNumberOfGames];
                    } else if (scratchedCount == scratchedGames.length) {
                        scratchedGames = ArrayUtils.setSize((int[])scratchedGames, scratchedCount + 10);
                    }
                    scratchedGames[scratchedCount++] = gameID;
                }
            } while (comps != null && comps.size() > 0);
            String title = "Statistics for " + user.getName() + " in competition " + competition.getName();
            boolean isWeightUsed = competition.isWeightUsed();
            out.pageStart(title);
            out.h3(title);
            if (generateImage) {
                if (lastNumberOfGames == 0) {
                    generateImage = false;
                } else if (hasGUI) {
                    out.table("border=0 cellpadding=1 cellspacing=0 bgcolor='#0'");
                    out.tr().td("<img src='" + user.getID() + "_gst.png' alt='Agent Scores'>");
                    out.tableEnd();
                    out.text("<em>Scores of the last " + lastNumberOfGames + " games played</em><p>");
                } else {
                    generateImage = false;
                    log.severe("No graphics environment available. Could not generate statistics image for agent " + user.getName());
                }
            }
            out.table("border=1").tr();
            if (isWeightUsed) {
                out.th("Avg Weighted Score");
            }
            out.th("Avg Score").th("Avg Score - Zero").th("Games Played").th("Zero Games");
            out.tr();
            if (isWeightUsed) {
                out.td(StatPageGenerator.toString(user.getAvgWeightedScore()), "align=right");
            }
            out.td(StatPageGenerator.toString(user.getAvgScore()), "align=right");
            if (user.getZeroGamesPlayed() != zeroScoreCount) {
                log.log(Level.SEVERE, "Competition " + competition.getID() + ", participant " + user.getName() + " has " + user.getZeroGamesPlayed() + " zero games but " + " found " + zeroScoreCount + " zero games", new IllegalStateException("mismatching zero games"));
            }
            if ((competition.getFlags() & 64) != 0) {
                double baseScore = user.getTotalScore() - (double)zeroScore;
                int games = user.getGamesPlayed() - zeroScoreCount;
                double avgScore = games <= 0 ? 0.0 : baseScore / (double)games;
                out.td(StatPageGenerator.toString(avgScore), "align=right");
            } else {
                out.td(StatPageGenerator.toString(user.getAvgScoreWithoutZeroGames()), "align=right");
            }
            out.td(Integer.toString(user.getGamesPlayed()), "align=right");
            out.td(Integer.toString(user.getZeroGamesPlayed()), "align=right");
            out.tableEnd();
            out.text("-Zero = without zero score games</em></font><p>\r\n");
            if (lastNumberOfGames > 0) {
                out.text("<b>The last ").text(lastNumberOfGames).text(" games played</b><br>");
                out.table("border=1");
                int i = 0;
                while (i < 4) {
                    out.th("Game").th("Score");
                    if (i % 4 != 3) {
                        out.th("&nbsp;", "bgcolor='#e0e0e0'");
                    }
                    ++i;
                }
                i = 0;
                while (i < lastNumberOfGames) {
                    if (i % 4 == 0) {
                        out.tr();
                    } else {
                        out.td("&nbsp;", "bgcolor='#e0e0e0'");
                    }
                    if (gameIDs[i] > 0) {
                        String gameIDStr = Integer.toString(gameIDs[i]);
                        out.td("", "align=right").text("<a href='").text(urlGamePath).text(gameIDStr).text("/'>").text(gameIDStr).text("</a>");
                    } else {
                        out.td("*[NOID]*", "align=right");
                    }
                    out.td("", "align=right");
                    if ((gameFlags[i] & 32) != 0 && scores[i] < 0.0) {
                        out.text("0 (");
                        StatPageGenerator.formatDouble(out, scores[i]);
                        out.text(')');
                    } else {
                        StatPageGenerator.formatDouble(out, scores[i]);
                    }
                    ++i;
                }
                out.tableEnd();
            }
            if (scratchedCount > 0) {
                out.text("<p><em>Scratched games: ");
                int i = 0;
                while (i < scratchedCount) {
                    if (i > 0) {
                        out.text(", ");
                    }
                    out.text((int)scratchedGames[i]);
                    ++i;
                }
                out.text("</em>");
            }
            out.pageEnd();
            out.close();
            if (generateImage && !StatPageGenerator.createImage(imageFile = String.valueOf(path) + user.getID() + "_gst.png", scores, 0, lastNumberOfGames)) {
                log.severe("could not create statistics image for agent " + user.getName());
            }
            return true;
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "could not create statistics for agent " + user.getName(), e);
            return false;
        }
    }

    private static HtmlWriter formatDouble(HtmlWriter out, double score) {
        if (score < 0.0) {
            out.text("<font color=red>").text(FormatUtils.formatDouble(score, "&nbsp;")).text("</font>");
        } else {
            out.text(FormatUtils.formatDouble(score, "&nbsp;"));
        }
        return out;
    }

    private static String toString(double score) {
        return score < 0.0 ? "<font color=red>" + FormatUtils.formatDouble(score, "&nbsp;") + "</font>" : FormatUtils.formatDouble(score, "&nbsp;");
    }
}

