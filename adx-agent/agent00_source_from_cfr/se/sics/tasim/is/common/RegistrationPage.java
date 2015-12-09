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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mortbay.http.HttpException;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import org.mortbay.util.ByteArrayISO8859Writer;
import se.sics.tasim.is.common.HttpPage;
import se.sics.tasim.is.common.InfoServer;

public class RegistrationPage
extends HttpPage {
    private static final Logger log = Logger.getLogger(RegistrationPage.class.getName());
    private static final boolean SUPPORT_CMD = true;
    private static final boolean SUPPORT_CLAIM = true;
    private final InfoServer infoServer;
    private String password;
    private boolean isRemoteRegistrationEnabled = false;
    private URL[] notificationTargets;

    public RegistrationPage(InfoServer infoServer) {
        this(infoServer, null, null, false);
    }

    public RegistrationPage(InfoServer infoServer, String notification, String password, boolean isRemoteRegistrationEnabled) {
        StringTokenizer tok;
        int len;
        this.infoServer = infoServer;
        this.password = password;
        this.isRemoteRegistrationEnabled = isRemoteRegistrationEnabled;
        if (notification != null && (len = (tok = new StringTokenizer(notification, ", \t")).countTokens()) > 0) {
            try {
                URL[] n = new URL[len];
                int i = 0;
                while (i < len) {
                    n[i] = new URL(tok.nextToken());
                    ++i;
                }
                this.notificationTargets = n;
            }
            catch (Exception e) {
                log.log(Level.WARNING, "could not handle notifications " + notification, e);
            }
        }
    }

    @Override
    public void handle(String pathInContext, String pathParams, HttpRequest request, HttpResponse response) throws HttpException, IOException {
        String cmd;
        String message = null;
        boolean created = false;
        String name = null;
        String email = null;
        if (this.password != null && !this.password.equals(request.getParameter("pw"))) {
            response.sendError(403);
            request.setHandled(true);
            return;
        }
        String string = cmd = this.isRemoteRegistrationEnabled ? this.trim(request.getParameter("cmd")) : null;
        if ("POST".equals(request.getMethod()) || cmd != null) {
            String pw1 = this.trim(request.getParameter("p1"));
            String pw2 = this.trim(request.getParameter("p2"));
            name = this.trim(request.getParameter("name"));
            email = this.trim(request.getParameter("email"));
            if (name == null) {
                message = "You must enter a user name";
            } else if (cmd != null) {
                created = true;
                if ("validate".equals(cmd)) {
                    String password = this.infoServer.getUserPassword(name);
                    if (password == null) {
                        try {
                            this.infoServer.validateUserInfo(name, pw1, email);
                            message = "ok:create";
                        }
                        catch (Exception e) {
                            message = "error:" + e.getMessage();
                        }
                    } else {
                        message = password.equals(pw1) ? "ok:exists(" + this.infoServer.getUserID(name) + ')' : "error:agent already exists with another password";
                    }
                } else if ("create".equals(cmd)) {
                    String password = this.infoServer.getUserPassword(name);
                    if (password == null) {
                        try {
                            int userID = this.infoServer.createUser(name, pw1, email);
                            this.callNotification(userID);
                            message = "ok:create";
                        }
                        catch (Exception e) {
                            message = "error:" + e.getMessage();
                        }
                    } else {
                        message = password.equals(pw1) ? "ok:exists" : "error:agent already exists with another password";
                    }
                } else if ("claim".equals(cmd)) {
                    try {
                        int userID = this.infoServer.claimUser(name, pw1, email);
                        this.callNotification(userID);
                        message = "ok:create";
                    }
                    catch (Exception e) {
                        message = "error:" + e.getMessage();
                    }
                } else {
                    message = "error:unknown command";
                }
                message = "<cmd>" + message + "</cmd>";
            } else if (pw1 == null || pw1.length() < 4) {
                message = "No password (please use at least 4 characters)";
            } else if (pw1.equals(pw2)) {
                try {
                    int userID = this.infoServer.createUser(name, pw1, email);
                    message = "User " + name + " has been registered";
                    created = true;
                    this.callNotification(userID);
                }
                catch (Exception e) {
                    message = "Error: " + e.getMessage();
                }
            } else {
                message = "Passwords do not match";
            }
        }
        HtmlWriter page = new HtmlWriter();
        if (created) {
            page.text(message);
        } else {
            page.pageStart("Agent/User Registration");
            if (message != null) {
                page.tag("font", "color=red").h3(message).tagEnd("font").p().tag("hr").p();
            }
            page.h2("Register new Agent/User").form("", "POST").table(1).attr("cellpadding", 2).td("Agent/User Name").td("<input name=name type=text length=22");
            if (name != null) {
                page.text(" value='").text(name).text('\'');
            }
            page.text('>').tr().td("Email").td("<input name=email type=text length=22");
            if (email != null) {
                page.text(" value='").text(email).text('\'');
            }
            page.text('>').tr().td("Password").td("<input name=p1 type=password length=22>").tr().td("Password (retype)").td("<input name=p2 type=password length=22>").tableEnd().text("<input type=submit value='Register'>").formEnd();
        }
        page.close();
        ByteArrayISO8859Writer writer = new ByteArrayISO8859Writer();
        page.write((Writer)writer);
        response.setContentType("text/html");
        response.setContentLength(writer.size());
        writer.writeTo(response.getOutputStream());
        response.commit();
    }

    private void callNotification(final int userID) {
        if (this.notificationTargets != null) {
            new Thread(new Runnable(){

                @Override
                public void run() {
                    int i = 0;
                    int n = RegistrationPage.this.notificationTargets.length;
                    while (i < n) {
                        try {
                            URL url = new URL(RegistrationPage.this.notificationTargets[i], "?id=" + userID);
                            URLConnection conn = url.openConnection();
                            int length = conn.getContentLength();
                            conn.getInputStream().close();
                        }
                        catch (Exception e) {
                            log.log(Level.WARNING, "could not notify " + RegistrationPage.this.notificationTargets[i], e);
                        }
                        ++i;
                    }
                }
            }).start();
        }
    }

    private String trim(String text) {
        return text != null && (text = text.trim()).length() > 0 ? text : null;
    }

}

