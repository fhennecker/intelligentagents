/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.is;

import edu.umich.eecs.tac.is.TACAAResultManager;
import edu.umich.eecs.tac.is.TACAAViewerCache;
import se.sics.tasim.is.common.InfoManager;
import se.sics.tasim.is.common.ResultManager;
import se.sics.tasim.is.common.ViewerCache;
import tau.tac.adx.sim.TACAdxConstants;

public class TACAAInfoManager
extends InfoManager {
    @Override
    protected void init() {
        int i = 0;
        int n = TACAdxConstants.SUPPORTED_TYPES.length;
        while (i < n) {
            this.registerType(TACAdxConstants.SUPPORTED_TYPES[i]);
            ++i;
        }
    }

    @Override
    public ViewerCache createViewerCache(String simType) {
        return new TACAAViewerCache();
    }

    @Override
    public ResultManager createResultManager(String simType) {
        return new TACAAResultManager();
    }
}

