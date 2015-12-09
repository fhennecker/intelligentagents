/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.EventBus
 *  com.google.common.eventbus.Subscribe
 */
package tau.tac.adx;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import tau.tac.adx.bids.Bidder;
import tau.tac.adx.demand.Campaign;
import tau.tac.adx.demand.UserClassificationService;
import tau.tac.adx.messages.CampaignNotification;
import tau.tac.adx.publishers.AdxPublisher;
import tau.tac.adx.sim.TACAdxSimulation;

public class AdxManager {
    private final Map<String, AdxPublisher> publishersNamingMap = new HashMap<String, AdxPublisher>();
    private final Map<Integer, Campaign> campaignMap = new HashMap<Integer, Campaign>();
    private final Map<String, Bidder> bidderMap = new HashMap<String, Bidder>();
    private TACAdxSimulation simulation;
    private static AdxManager instance;
    private UserClassificationService userClassificationService;
    private final Logger log = Logger.getLogger(AdxManager.class.getName());
    private String adxAgentAddress;

    public UserClassificationService getUserClassificationService() {
        return this.userClassificationService;
    }

    public void setUserClassificationService(UserClassificationService userClassificationService) {
        this.userClassificationService = userClassificationService;
    }

    public String getAdxAgentAddress() {
        return this.adxAgentAddress;
    }

    private AdxManager() {
    }

    public static AdxManager getInstance() {
        if (instance == null) {
            instance = new AdxManager();
        }
        return instance;
    }

    public void setup() {
        this.simulation.getEventBus().register((Object)this);
    }

    public AdxPublisher getPublisher(String publisherName) {
        return this.publishersNamingMap.get(publisherName);
    }

    public Collection<AdxPublisher> getPublishers() {
        return this.publishersNamingMap.values();
    }

    public void addPublisher(AdxPublisher publisher) {
        this.publishersNamingMap.put(publisher.getName(), publisher);
    }

    @Subscribe
    public void addCampaign(CampaignNotification campaign) {
        this.campaignMap.put(campaign.getCampaign().getId(), campaign.getCampaign());
    }

    public Campaign getCampaign(int campaignId) {
        return this.campaignMap.get(campaignId);
    }

    public void addBidder(Bidder bidder) {
        this.bidderMap.put(bidder.getName(), bidder);
    }

    public Bidder getBidder(int advertiserId) {
        return this.bidderMap.get(advertiserId);
    }

    public TACAdxSimulation getSimulation() {
        return this.simulation;
    }

    public void setSimulation(TACAdxSimulation simulation) {
        this.simulation = simulation;
    }

    public void setAdxAgentAddress(String adxAgentAddress) {
        this.adxAgentAddress = adxAgentAddress;
    }
}

