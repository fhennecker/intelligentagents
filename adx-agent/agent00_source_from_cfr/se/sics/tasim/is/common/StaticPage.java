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
import java.io.OutputStream;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mortbay.http.HttpException;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import org.mortbay.util.ByteArrayISO8859Writer;
import se.sics.tasim.is.common.HttpPage;

public class StaticPage
extends HttpPage {
    private static final Logger log = Logger.getLogger(StaticPage.class.getName());
    private String path;
    private byte[] pageData;
    private String contentType = "text/html";

    private StaticPage(String path) {
        if (path == null) {
            throw new NullPointerException();
        }
        this.path = path;
    }

    public StaticPage(String path, String page) {
        this(path);
        this.setPage(page);
    }

    public StaticPage(String path, HtmlWriter writer) {
        this(path);
        this.setPage(writer);
    }

    public void setPage(String page) {
        try {
            ByteArrayISO8859Writer writer = new ByteArrayISO8859Writer();
            writer.write(page);
            this.pageData = writer.getByteArray();
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "could not set page data for " + this.path, e);
        }
    }

    public void setPage(HtmlWriter page) {
        try {
            ByteArrayISO8859Writer writer = new ByteArrayISO8859Writer();
            page.close();
            page.write((Writer)writer);
            this.pageData = writer.getByteArray();
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "could not set page data for " + this.path, e);
        }
    }

    @Override
    public void handle(String pathInContext, String pathParams, HttpRequest request, HttpResponse response) throws HttpException, IOException {
        if (this.path.equals(pathInContext) && this.pageData != null) {
            response.setContentType(this.contentType);
            response.setContentLength(this.pageData.length);
            response.getOutputStream().write(this.pageData);
            response.commit();
        }
    }

    public String toString() {
        return "StaticPage[" + this.path + ',' + (this.pageData == null ? 0 : this.pageData.length) + ']';
    }
}

