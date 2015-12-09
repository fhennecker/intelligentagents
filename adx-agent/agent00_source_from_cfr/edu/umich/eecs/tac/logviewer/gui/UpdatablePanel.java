/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.logviewer.gui;

import edu.umich.eecs.tac.logviewer.gui.PositiveBoundedRangeModel;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class UpdatablePanel {
    protected JPanel mainPane;
    protected PositiveBoundedRangeModel dayModel;

    public UpdatablePanel(PositiveBoundedRangeModel dm) {
        this.dayModel = dm;
        this.mainPane = new JPanel();
        if (this.dayModel != null) {
            this.dayModel.addChangeListener(new ChangeListener(){

                @Override
                public void stateChanged(ChangeEvent ce) {
                    UpdatablePanel.this.updateMePlz();
                }
            });
        }
    }

    protected abstract void updateMePlz();

    public final Component getMainPane() {
        return this.mainPane;
    }

}

