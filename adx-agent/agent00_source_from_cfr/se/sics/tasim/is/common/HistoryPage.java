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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;
import org.mortbay.http.HttpException;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import org.mortbay.util.ByteArrayISO8859Writer;
import se.sics.tasim.is.common.HttpPage;
import se.sics.tasim.is.common.SimServer;

public class HistoryPage
extends HttpPage {
    private static final Logger log = Logger.getLogger(HistoryPage.class.getName());
    private final String pathInfo;
    private final SimServer simServer;
    private final String gamePath;
    private int simulationsPerPage;
    private String simTablePrefix;

    public HistoryPage(String pathInfo, SimServer simServer, String gamePath, String simTablePrefix, int simulationsPerPage) {
        this.pathInfo = pathInfo;
        this.simServer = simServer;
        if (gamePath.length() > 0 && !gamePath.endsWith(File.separator)) {
            gamePath = String.valueOf(gamePath) + File.separator;
        }
        this.gamePath = gamePath;
        this.simTablePrefix = simTablePrefix;
        this.simulationsPerPage = simulationsPerPage;
    }

    @Override
    public void handle(String pathInContext, String pathParams, HttpRequest request, HttpResponse response) throws HttpException, IOException {
        if (!this.pathInfo.equals(pathInContext)) {
            return;
        }
        String gameStr = request.getParameter("id");
        if ("last".equals(gameStr)) {
            this.showLastGame(response);
            return;
        }
        int lastGameID = this.simServer.getLastPlayedSimulationID();
        StringBuffer game = new StringBuffer();
        game.append("<html><body bgcolor=white link='#204020' vlink='#204020'><font face='Arial,Helvetica,sans-serif' size='+2'><b>Game History for ").append(this.simServer.getServerName()).append("</b></font><p>\r\n");
        if (lastGameID == -1) {
            game.append("<font face='Arial,Helvetica,sans-serif' size='+1'>No games played</font><p>\r\n");
        } else {
            int gameID = lastGameID;
            if (gameStr != null) {
                try {
                    gameID = Integer.parseInt(gameStr);
                }
                catch (Exception var9_10) {
                    // empty catch block
                }
            }
            int id = 1 + (gameID - --gameID % this.simulationsPerPage);
            StringBuffer sb = new StringBuffer();
            sb.append("<font face='Arial,Helvetica,sans-serif' size=2>");
            this.link(sb, "First", 1, id > 1);
            this.link(sb, "Previous", id - this.simulationsPerPage, id > 1);
            this.link(sb, "Next", id + this.simulationsPerPage, id + this.simulationsPerPage <= lastGameID);
            this.link(sb, "Last", lastGameID, id + this.simulationsPerPage <= lastGameID);
            sb.append("</font>");
            String links = sb.toString();
            game.append("<form><table border=0 width='100%'><tr><td>").append(links).append("</td><td align=right><font face='Arial,Helvetica,sans-serif' size=2>Go to page with game <input type=text name=id size=5 border=0><input type=submit value=Go></font></td></tr></table></form>\r\n<p>\r\n<table border=1 width='100%'><colgroup span=1 align=right></colgroup><colgroup span=2></colgroup><tr><th>Game</th><th>Start Time (Duration)</th><th>Participants</th></tr>");
            this.readPage(game, (id - 1) / this.simulationsPerPage + 1);
            game.append("</table>\r\n<p>\r\n").append(links);
        }
        game.append("</body></html>\r\n");
        ByteArrayISO8859Writer writer = new ByteArrayISO8859Writer();
        writer.write(game.toString());
        response.setContentType("text/html");
        response.setContentLength(writer.size());
        writer.writeTo(response.getOutputStream());
        response.commit();
    }

    /*
     * Exception decompiling
     */
    private void readPage(StringBuffer data, int id) {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 9[CATCHBLOCK]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:394)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:446)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:2859)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:805)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:220)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:165)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:91)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:354)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:751)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:683)
        // org.benf.cfr.reader.Main.doJar(Main.java:128)
        // org.benf.cfr.reader.Main.main(Main.java:178)
        throw new IllegalStateException("Decompilation failed");
    }

    private void showLastGame(HttpResponse response) throws HttpException, IOException {
        int lastGameID = this.simServer.getLastPlayedSimulationID();
        StringBuffer page = new StringBuffer();
        int delay = this.simServer.getSecondsToNextSimulationEnd();
        page.append("<html><head><title>").append("Last game played at ").append(this.simServer.getServerName()).append("</title>\r\n<META http-equiv=\"refresh\" content=\"").append(delay).append(this.pathInfo).append("?id=last\">\r\n</head>\r\n");
        if (lastGameID < 1) {
            page.append("<body>Waiting for first game</body></html>\r\n");
        } else {
            this.readResultPage(page, lastGameID);
        }
        ByteArrayISO8859Writer writer = new ByteArrayISO8859Writer();
        writer.write(page.toString());
        response.setContentType("text/html");
        response.setContentLength(writer.size());
        writer.writeTo(response.getOutputStream());
        response.commit();
    }

    /*
     * Exception decompiling
     */
    private void readResultPage(StringBuffer data, int id) {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 9[CATCHBLOCK]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:394)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:446)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:2859)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:805)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:220)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:165)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:91)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:354)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:751)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:683)
        // org.benf.cfr.reader.Main.doJar(Main.java:128)
        // org.benf.cfr.reader.Main.main(Main.java:178)
        throw new IllegalStateException("Decompilation failed");
    }

    private void link(StringBuffer sb, String title, int pos, boolean link) {
        if (link) {
            sb.append("<a href='?id=").append(pos).append("'>").append(title).append("</a> &nbsp; ");
        } else {
            sb.append(title).append(" &nbsp; ");
        }
    }
}

