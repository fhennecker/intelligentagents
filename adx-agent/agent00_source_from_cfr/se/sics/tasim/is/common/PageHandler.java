/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.mortbay.http.HttpException
 *  org.mortbay.http.HttpRequest
 *  org.mortbay.http.HttpResponse
 *  org.mortbay.http.PathMap
 *  org.mortbay.http.handler.AbstractHttpHandler
 *  org.mortbay.util.Code
 */
package se.sics.tasim.is.common;

import java.io.IOException;
import java.util.Map;
import org.mortbay.http.HttpException;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import org.mortbay.http.PathMap;
import org.mortbay.http.handler.AbstractHttpHandler;
import org.mortbay.util.Code;
import se.sics.tasim.is.common.HttpPage;

public class PageHandler
extends AbstractHttpHandler {
    private PathMap pathMap = new PathMap();

    public void addPage(String pathSpec, HttpPage page) {
        if (!pathSpec.startsWith("/") && !pathSpec.startsWith("*")) {
            Code.warning((String)("pathSpec should start with '/' or '*' : " + pathSpec));
            pathSpec = "/" + pathSpec;
        }
        this.pathMap.put((Object)pathSpec, (Object)page);
    }

    public Map.Entry getPageEntry(String pathInContext) {
        return this.pathMap.getMatch(pathInContext);
    }

    public void handle(String pathInContext, String pathParams, HttpRequest request, HttpResponse response) throws HttpException, IOException {
        HttpPage page;
        if (!this.isStarted()) {
            return;
        }
        Map.Entry pageEntry = this.getPageEntry(pathInContext);
        HttpPage httpPage = page = pageEntry == null ? null : (HttpPage)pageEntry.getValue();
        if (page != null) {
            page.handle(pathInContext, pathParams, request, response);
        }
    }
}

