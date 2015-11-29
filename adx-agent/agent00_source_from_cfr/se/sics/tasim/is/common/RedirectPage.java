/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.mortbay.http.HttpException
 *  org.mortbay.http.HttpRequest
 *  org.mortbay.http.HttpResponse
 */
package se.sics.tasim.is.common;

import java.io.IOException;
import org.mortbay.http.HttpException;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import se.sics.tasim.is.common.HttpPage;

public class RedirectPage
extends HttpPage {
    private String redirectPath;
    private boolean appendPath;

    public void setRedirectPath(String redirectPath, boolean appendPath) {
        if (redirectPath != null && !redirectPath.startsWith("/")) {
            redirectPath = String.valueOf('/') + redirectPath;
        }
        this.redirectPath = redirectPath;
        this.appendPath = appendPath;
    }

    @Override
    public void handle(String pathInContext, String pathParams, HttpRequest request, HttpResponse response) throws HttpException, IOException {
        String redirectPath = this.redirectPath;
        if (redirectPath != null) {
            StringBuffer buf = request.getRootURL();
            buf.append(redirectPath);
            if (this.appendPath) {
                buf.append(request.getPath());
            }
            String location = buf.toString();
            response.setField("Location", location);
            response.setStatus(302);
            request.setHandled(true);
        }
    }
}

