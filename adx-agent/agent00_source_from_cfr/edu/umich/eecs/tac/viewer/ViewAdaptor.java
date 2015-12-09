/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer;

import edu.umich.eecs.tac.viewer.ViewListener;
import se.sics.isl.transport.Transportable;

public class ViewAdaptor
implements ViewListener {
    @Override
    public void dataUpdated(int agent, int type, int value) {
    }

    @Override
    public void dataUpdated(int agent, int type, long value) {
    }

    @Override
    public void dataUpdated(int agent, int type, float value) {
    }

    @Override
    public void dataUpdated(int agent, int type, double value) {
    }

    @Override
    public void dataUpdated(int agent, int type, String value) {
    }

    @Override
    public void dataUpdated(int agent, int type, Transportable value) {
    }

    @Override
    public void dataUpdated(int type, Transportable value) {
    }

    @Override
    public void participant(int agent, int role, String name, int participantID) {
    }
}

