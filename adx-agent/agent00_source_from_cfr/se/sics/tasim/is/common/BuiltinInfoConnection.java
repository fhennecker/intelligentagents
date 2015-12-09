/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.is.common;

import java.io.IOException;
import se.sics.isl.util.ConfigManager;
import se.sics.isl.util.IllegalConfigurationException;
import se.sics.tasim.is.SimConnection;
import se.sics.tasim.is.common.InfoConnectionImpl;
import se.sics.tasim.is.common.InfoServer;

public class BuiltinInfoConnection
extends InfoConnectionImpl {
    private InfoServer infoServer;

    @Override
    public void init(ConfigManager config) throws IllegalConfigurationException, IOException {
        super.init(config);
        if (this.infoServer == null) {
            this.infoServer = new InfoServer(config);
        }
        this.checkInitialized();
    }

    @Override
    public void setSimConnection(SimConnection connection) {
        super.setSimConnection(connection);
        this.checkInitialized();
    }

    @Override
    public void auth(String serverName, String serverPassword, String serverVersion) {
        super.auth(serverName, serverPassword, serverVersion);
        this.checkInitialized();
    }

    private void checkInitialized() {
        if (this.getServerName() != null && this.getSimConnection() != null && this.infoServer != null) {
            this.infoServer.addInfoConnection(this);
        }
    }
}

