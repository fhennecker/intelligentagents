/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.is.common;

import com.botbox.util.ArrayUtils;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.is.EventWriter;

public class ViewerCache
extends EventWriter {
    private static final int AGENT = 0;
    private static final int ROLE = 1;
    private static final int PARTICIPANT_ID = 2;
    private static final int PARTS = 3;
    private int[] partData;
    private String[] partNames;
    private int partNumber;

    public void writeCache(EventWriter eventWriter) {
        int i = 0;
        int index = 0;
        int n = this.partNumber;
        while (i < n) {
            eventWriter.participant(this.partData[index + 0], this.partData[index + 1], this.partNames[i], this.partData[index + 2]);
            ++i;
            index += 3;
        }
    }

    @Override
    public void participant(int agent, int role, String name, int participantID) {
        if (this.partData == null) {
            this.partData = new int[24];
            this.partNames = new String[8];
        } else if (this.partNumber == this.partNames.length) {
            this.partData = ArrayUtils.setSize(this.partData, (this.partNumber + 8) * 3);
            this.partNames = (String[])ArrayUtils.setSize(this.partNames, this.partNumber + 8);
        }
        int index = this.partNumber * 3;
        this.partData[index + 0] = agent;
        this.partData[index + 1] = role;
        this.partData[index + 2] = participantID;
        this.partNames[this.partNumber++] = name;
    }

    @Override
    public void nextTimeUnit(int timeUnit) {
    }

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
    public void dataUpdated(int agent, int type, Transportable content) {
    }

    @Override
    public void dataUpdated(int type, Transportable content) {
    }

    @Override
    public void interaction(int fromAgent, int toAgent, int type) {
    }

    @Override
    public void interactionWithRole(int fromAgent, int role, int type) {
    }

    @Override
    public void intCache(int agent, int type, int[] cache) {
    }
}

