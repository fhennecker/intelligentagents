/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.mortbay.http.HashUserRealm
 *  org.mortbay.http.HttpRequest
 *  org.mortbay.http.UserPrincipal
 */
package se.sics.tasim.is.common;

import org.mortbay.http.HashUserRealm;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.UserPrincipal;
import se.sics.tasim.is.common.InfoServer;

public class AgentRealm
extends HashUserRealm {
    public static final String ADMIN_ROLE = "admin";
    private InfoServer infoServer;

    public AgentRealm(InfoServer infoServer, String realmName) {
        super(realmName);
        this.infoServer = infoServer;
    }

    void setAdminUser(String name, String password) {
        this.put((Object)name, (Object)password);
        this.addUserToRole(name, "admin");
    }

    public UserPrincipal authenticate(String username, Object credentials, HttpRequest request) {
        if (this.get((Object)username) == null) {
            this.updateUser(username);
        }
        return super.authenticate(username, credentials, request);
    }

    public void updateUser(String name) {
        String password = this.infoServer.getUserPassword(name);
        if (password != null) {
            this.put((Object)name, (Object)password);
        }
    }
}

