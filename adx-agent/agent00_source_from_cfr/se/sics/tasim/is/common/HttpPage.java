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

public abstract class HttpPage {
    public abstract void handle(String var1, String var2, HttpRequest var3, HttpResponse var4) throws HttpException, IOException;
}

