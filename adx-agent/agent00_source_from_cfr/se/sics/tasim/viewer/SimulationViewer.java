/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.viewer;

import javax.swing.JComponent;
import se.sics.tasim.viewer.ViewerConnection;
import se.sics.tasim.viewer.ViewerPanel;

public abstract class SimulationViewer
extends ViewerConnection {
    public abstract void init(ViewerPanel var1);

    public abstract JComponent getComponent();
}

