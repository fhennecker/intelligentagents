/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.role;

import com.botbox.util.ArrayUtils;
import edu.umich.eecs.tac.viewer.ViewAdaptor;

public class AgentSupport
extends ViewAdaptor {
    private int[] agents = new int[0];
    private int[] roles = new int[0];
    private int[] participants = new int[0];
    private String[] names = new String[0];
    private int agentCount;

    public int indexOfAgent(int agent) {
        return ArrayUtils.indexOf(this.agents, 0, this.agentCount, agent);
    }

    public int size() {
        return this.agentCount;
    }

    public int agent(int index) {
        return this.agents[index];
    }

    public int role(int index) {
        return this.roles[index];
    }

    public int participant(int index) {
        return this.agents[index];
    }

    public String name(int index) {
        return this.names[index];
    }

    @Override
    public void participant(int agent, int role, String name, int participantID) {
        this.setAgent(agent, role, name, participantID);
    }

    protected void addAgent(int agent) {
        int index = ArrayUtils.indexOf(this.agents, 0, this.agentCount, agent);
        if (index < 0) {
            this.doAddAgent(agent);
        }
    }

    private int doAddAgent(int agent) {
        if (this.agentCount == this.participants.length) {
            int newSize = this.agentCount + 8;
            this.agents = ArrayUtils.setSize(this.agents, newSize);
            this.roles = ArrayUtils.setSize(this.roles, newSize);
            this.participants = ArrayUtils.setSize(this.participants, newSize);
            this.names = (String[])ArrayUtils.setSize(this.names, newSize);
        }
        this.agents[this.agentCount] = agent;
        return this.agentCount++;
    }

    private void setAgent(int agent, int role, String name, int participantID) {
        this.addAgent(agent);
        int index = ArrayUtils.indexOf(this.agents, 0, this.agentCount, agent);
        this.roles[index] = role;
        this.names[index] = name;
        this.participants[index] = participantID;
    }
}

