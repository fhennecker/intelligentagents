/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.EventBus
 */
package tau.tac.adx.sim;

import com.google.common.eventbus.EventBus;
import edu.umich.eecs.tac.props.AdvertiserInfo;
import java.util.List;
import java.util.Map;
import se.sics.tasim.sim.SimulationAgent;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.auction.manager.AdxBidManager;
import tau.tac.adx.auction.tracker.AdxBidTracker;
import tau.tac.adx.devices.Device;
import tau.tac.adx.props.PublisherCatalog;
import tau.tac.adx.sim.AdxAuctioneer;
import tau.tac.adx.users.AdxUser;

public interface AdxAgentRepository {
    public PublisherCatalog getPublisherCatalog();

    public Map<String, AdvertiserInfo> getAdvertiserInfo();

    public SimulationAgent[] getPublishers();

    public int getNumberOfAdvertisers();

    public String[] getAdvertiserAddresses();

    public List<AdxUser> getUserPopulation();

    public Map<Device, Integer> getDeviceDistributionMap();

    public Map<AdType, Integer> getAdTypeDistributionMap();

    public AdxAuctioneer getAuctioneer();

    public AdxBidManager getAdxBidManager();

    public EventBus getEventBus();

    public AdxBidTracker getAdxBidTracker();
}

