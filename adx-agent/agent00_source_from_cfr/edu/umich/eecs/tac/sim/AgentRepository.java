/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.sim;

import edu.umich.eecs.tac.props.AdvertiserInfo;
import edu.umich.eecs.tac.sim.SalesAnalyst;
import java.util.Map;
import se.sics.tasim.sim.SimulationAgent;

public interface AgentRepository {
    public Map<String, AdvertiserInfo> getAdvertiserInfo();

    public SimulationAgent[] getPublishers();

    public SalesAnalyst getSalesAnalyst();

    public int getNumberOfAdvertisers();

    public String[] getAdvertiserAddresses();
}

