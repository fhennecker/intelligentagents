/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.mortbay.http.HttpException
 *  org.mortbay.http.HttpRequest
 *  org.mortbay.http.HttpResponse
 *  org.mortbay.util.URI
 */
package se.sics.tasim.is.common;

import java.io.IOException;
import java.util.logging.Logger;
import org.mortbay.http.HttpException;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import org.mortbay.util.URI;
import se.sics.tasim.is.common.Competition;
import se.sics.tasim.is.common.HttpPage;
import se.sics.tasim.is.common.SimServer;

public class ScorePage
extends HttpPage {
    private static final Logger log = Logger.getLogger(ScorePage.class.getName());
    private final SimServer simServer;
    private final String gamePath;

    public ScorePage(SimServer simServer, String gamePath) {
        this.simServer = simServer;
        if (gamePath != null && !gamePath.endsWith("/")) {
            gamePath = String.valueOf(gamePath) + '/';
        }
        this.gamePath = gamePath;
    }

    @Override
    public void handle(String pathInContext, String pathParams, HttpRequest request, HttpResponse response) throws HttpException, IOException {
        String scorePath;
        String location;
        Competition competition = this.simServer.getCurrentCompetition();
        String string = scorePath = competition != null ? "competition/" + competition.getID() + "/" : "default/";
        if (this.gamePath != null) {
            location = String.valueOf(this.gamePath) + scorePath;
        } else {
            StringBuffer buf = request.getRequestURL();
            location = URI.addPaths((String)buf.toString(), (String)("../history/" + scorePath));
        }
        response.setField("Location", location);
        response.setStatus(302);
        request.setHandled(true);
    }
}

