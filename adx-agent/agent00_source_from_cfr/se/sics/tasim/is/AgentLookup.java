/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.is;

import com.botbox.util.ArrayUtils;
import java.util.Hashtable;
import se.sics.tasim.is.AgentInfo;

public class AgentLookup {
    private static final int AGENTS_PER_USER = 11;
    private Hashtable nameLookup = new Hashtable();
    private AgentInfo[] agents = new AgentInfo[50];
    private AgentInfo[] agentCache;

    public AgentInfo getAgentInfo(int id) {
        int index = id / 11;
        return index < this.agents.length ? this.agents[index] : null;
    }

    public String getAgentName(int id) {
        AgentInfo info;
        int index = id / 11;
        if (index < this.agents.length && (info = this.agents[index]) != null) {
            int rest = id % 11;
            return rest == 0 ? info.getName() : String.valueOf(info.getName()) + (rest - 1);
        }
        return null;
    }

    public int getAgentID(String name) {
        int len;
        char c;
        AgentInfo agent = (AgentInfo)this.nameLookup.get(name);
        int add = 0;
        if (agent == null && (len = name.length()) > 1 && (c = name.charAt(len - 1)) >= '0' && c <= '9') {
            add = c - 48 + 1;
            agent = (AgentInfo)this.nameLookup.get(name.substring(0, len - 1));
        }
        return agent != null ? add + agent.getID() : -1;
    }

    public String getAgentPassword(int agentID) {
        int index = agentID / 11;
        return index < this.agents.length && this.agents[index] != null ? this.agents[index].getPassword() : null;
    }

    public boolean validateAgent(int id, String password) {
        int index = id / 11;
        if (index < this.agents.length && this.agents[index] != null && this.agents[index].getPassword().equals(password)) {
            return true;
        }
        return false;
    }

    public void setUser(String agentName, String password, int agentID) {
        this.setUser(agentName, password, agentID, -1);
    }

    public void setUser(String agentName, String password, int agentID, int parentID) {
        AgentInfo agent;
        int index = agentID / 11;
        if (index >= this.agents.length) {
            this.agents = (AgentInfo[])ArrayUtils.setSize(this.agents, index + 50);
        }
        this.agents[index] = agent = new AgentInfo(agentName, password, agentID - agentID % 11, parentID);
        this.nameLookup.put(agentName, agent);
        if (this.agentCache != null) {
            AgentLookup agentLookup = this;
            synchronized (agentLookup) {
                this.agentCache = null;
            }
        }
    }

    public AgentInfo[] getAgentInfos() {
        Object[] infos = this.agentCache;
        if (infos == null) {
            AgentLookup agentLookup = this;
            synchronized (agentLookup) {
                int index = 0;
                infos = new AgentInfo[this.agents.length];
                int i = 0;
                int n = this.agents.length;
                while (i < n) {
                    if (this.agents[i] != null) {
                        infos[index++] = this.agents[i];
                    }
                    ++i;
                }
                if (index < infos.length) {
                    infos = (AgentInfo[])ArrayUtils.setSize(infos, index);
                }
                this.agentCache = infos;
            }
        }
        return infos;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("AgentLookup[\n");
        int i = 0;
        while (i < this.agents.length) {
            if (this.agents[i] != null) {
                sb.append(this.agents[i].getID()).append(',').append(this.agents[i].getName()).append(',').append(this.agents[i].getPassword()).append('\n');
            }
            ++i;
        }
        sb.append("]\n");
        return sb.toString();
    }
}

