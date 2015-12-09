/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.is.common;

import se.sics.tasim.is.common.InfoServer;
import se.sics.tasim.is.common.ResultManager;
import se.sics.tasim.is.common.ViewerCache;

public abstract class InfoManager {
    private InfoServer infoServer;
    private String name;

    protected InfoManager() {
    }

    final void init(InfoServer infoServer, String name) {
        this.infoServer = infoServer;
        this.name = name;
        this.init();
    }

    protected abstract void init();

    protected void registerType(String type) {
        this.infoServer.addInfoManager(type, this);
    }

    public abstract ResultManager createResultManager(String var1);

    public abstract ViewerCache createViewerCache(String var1);
}

