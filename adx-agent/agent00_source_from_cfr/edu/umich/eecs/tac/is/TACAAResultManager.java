/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.is;

import com.botbox.html.HtmlWriter;
import edu.umich.eecs.tac.Participant;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.logging.Logger;
import se.sics.isl.transport.Context;
import se.sics.isl.util.FormatUtils;
import se.sics.tasim.is.common.InfoServer;
import se.sics.tasim.is.common.ResultManager;
import se.sics.tasim.logtool.LogReader;
import se.sics.tasim.logtool.ParticipantInfo;
import tau.tac.adx.TACAdxSimulationInfo;
import tau.tac.adx.props.AdxInfoContextFactory;

public class TACAAResultManager
extends ResultManager {
    private static final Logger log = Logger.getLogger(TACAAResultManager.class.getName());
    private static final String POSITIVE_PARTICIPANT_COLOR = "#0000c0";
    private static final String NEUTRAL_PARTICIPANT_COLOR = null;
    private static final String NEGATIVE_PARTICIPANT_COLOR = "#c00000";

    @Override
    protected void generateResult() throws IOException {
        TACAdxSimulationInfo simInfo;
        LogReader reader = this.getLogReader();
        int simulationID = reader.getSimulationID();
        String serverName = reader.getServerName();
        reader.setContext(new AdxInfoContextFactory().createContext());
        try {
            simInfo = new TACAdxSimulationInfo(reader);
        }
        catch (Exception e) {
            throw (IOException)new IOException("could not parse simulation log " + simulationID).initCause(e);
        }
        String destinationFile = String.valueOf(this.getDestinationPath()) + "index.html";
        log.info("generating results for simulation " + simulationID + " to " + destinationFile);
        HtmlWriter html = new HtmlWriter(new FileWriter(destinationFile));
        Participant[] participants = simInfo.getParticipantsByRole(5);
        if (participants != null) {
            participants = (Participant[])participants.clone();
            Arrays.sort(participants, Participant.getResultComparator());
        }
        html.pageStart("Results for game " + simulationID + '@' + serverName);
        html.h3("Result for game " + simulationID + '@' + serverName + " played at " + InfoServer.getServerTimeAsString(reader.getStartTime()));
        html.table("border=1").colgroup(1).colgroup(11, "align=right").tr().th("Player").th("Revenue", "align=center").th("ADX Cost", "align=center").th("UCS Cost", "align=center").th("Impressions", "align=center").th("Result", "align=center");
        ParticipantInfo[] agentInfos = null;
        String[] agentColors = null;
        Object agentScores = null;
        if (participants != null) {
            agentInfos = new ParticipantInfo[participants.length];
            agentColors = new String[participants.length];
            agentScores = new double[participants.length];
            int i = 0;
            int n = participants.length;
            while (i < n) {
                Participant player = participants[i];
                ParticipantInfo agentInfo = player.getInfo();
                String name = agentInfo.getName();
                double adxCost = player.getADXCost();
                double ucsCost = player.getUCSCost();
                double revenue = player.getRevenue();
                double result = player.getResult();
                long impressions = player.getImpressions();
                agentInfos[i] = agentInfo;
                agentColors[i] = result < 0.0 ? "#c00000" : (result > 0.0 ? "#0000c0" : NEUTRAL_PARTICIPANT_COLOR);
                agentScores[i] = result;
                html.tr().td(agentInfo.isBuiltinAgent() ? "<em>" + name + "</em>" : name).td(this.getAmountAsString(revenue)).td(this.getAmountAsString(adxCost)).td(this.getAmountAsString(ucsCost)).td(this.getAmountAsString(impressions));
                html.td();
                this.formatAmount(html, result);
                ++i;
            }
        }
        html.tableEnd();
        html.text("Download game data ").tag('a').attr("href", this.getGameLogName()).text("here").tagEnd('a').p();
        html.p();
        html.p().tag("hr");
        html.pageEnd();
        html.close();
        this.addSimulationToHistory(agentInfos, agentColors);
        this.addSimulationResult(agentInfos, (double[])agentScores);
    }

    private String getAmountAsString(double amount) {
        return FormatUtils.formatDouble(amount, "&nbsp;");
    }

    private void formatAmount(HtmlWriter html, double amount) {
        this.formatAmount(html, amount, "");
    }

    private void formatAmount(HtmlWriter html, double amount, String postfix) {
        if (amount < 0.0) {
            html.tag("font", "color=red").text(this.getAmountAsString(amount)).text(postfix).tagEnd("font");
        } else {
            html.text(this.getAmountAsString(amount)).text(postfix);
        }
    }
}

