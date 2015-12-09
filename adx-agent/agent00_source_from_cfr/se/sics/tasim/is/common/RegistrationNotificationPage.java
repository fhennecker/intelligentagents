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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mortbay.http.HttpException;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import org.mortbay.util.ByteArrayISO8859Writer;
import se.sics.tasim.is.common.HttpPage;
import se.sics.tasim.is.common.InfoServer;

public class RegistrationNotificationPage
extends HttpPage {
    private static final Logger log = Logger.getLogger(RegistrationNotificationPage.class.getName());
    private final InfoServer infoServer;

    public RegistrationNotificationPage(InfoServer infoServer) {
        this.infoServer = infoServer;
    }

    @Override
    public void handle(String pathInContext, String pathParams, HttpRequest request, HttpResponse response) throws HttpException, IOException {
        String name = request.getParameter("id");
        String message = null;
        if (name != null) {
            try {
                int id = Integer.parseInt(name);
                this.infoServer.updateUser(id);
                log.info("updated user " + id + " using notification");
                message = "<html><body>User " + id + " has been updated</body></html>";
            }
            catch (Exception e) {
                log.log(Level.WARNING, "illegal user update '" + name + '\'', e);
            }
        }
        if (message == null) {
            message = "<html><body>Failed to update user " + name + "</body></html>";
        }
        ByteArrayISO8859Writer writer = new ByteArrayISO8859Writer();
        writer.write(message);
        response.setContentType("text/html");
        response.setContentLength(writer.size());
        writer.writeTo(response.getOutputStream());
        response.commit();
    }
}

