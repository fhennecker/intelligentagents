/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.EventBus
 */
package tau.tac.adx.agents.behaviors;

import com.google.common.eventbus.EventBus;
import edu.umich.eecs.tac.props.Auction;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.Ranking;
import edu.umich.eecs.tac.sim.Auctioneer;
import edu.umich.eecs.tac.user.DistributionBroadcaster;
import edu.umich.eecs.tac.util.config.ConfigProxy;
import edu.umich.eecs.tac.util.config.ConfigProxyUtils;
import java.util.Random;
import java.util.logging.Logger;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Message;
import tau.tac.adx.AdxManager;
import tau.tac.adx.auction.AdxBidBundleWriter;
import tau.tac.adx.auction.manager.AdxBidManager;
import tau.tac.adx.props.AdxBidBundle;
import tau.tac.adx.report.adn.AdNetworkReportManager;
import tau.tac.adx.report.adn.AdNetworkReportManagerImpl;
import tau.tac.adx.report.adn.AdNetworkReportSender;
import tau.tac.adx.report.publisher.AdxPublisherReportManager;
import tau.tac.adx.report.publisher.AdxPublisherReportManagerImpl;
import tau.tac.adx.report.publisher.AdxPublisherReportSender;
import tau.tac.adx.sim.AdxAgentRepository;
import tau.tac.adx.sim.AdxAuctioneer;
import tau.tac.adx.sim.TACAdxSimulation;
import tau.tac.adx.users.AdxUserBehaviorBuilder;
import tau.tac.adx.users.AdxUserManager;
import tau.tac.adx.users.AdxUsersBehavior;

public class DefaultAdxUsersBehavior
implements AdxUsersBehavior {
    private AdxUserManager userManager;
    private DistributionBroadcaster distributionBroadcaster;
    private int virtualDays;
    private final ConfigProxy config;
    private final AdxAgentRepository agentRepository;
    AdxPublisherReportManager publisherReportManager;
    AdxPublisherReportSender publisherReportSender;
    AdNetworkReportManager adNetworkReportManager;
    AdNetworkReportSender adNetworkReportSender;
    private final AdxBidManager bidManager;
    private Logger log;
    private final AdxBidBundleWriter bidBundleWriter;

    public DefaultAdxUsersBehavior(ConfigProxy config, AdxAgentRepository agentRepository, AdxPublisherReportSender publisherReportSender, AdNetworkReportSender adNetworkReportSender, AdxBidBundleWriter bidBundleWriter) {
        if (config == null) {
            throw new NullPointerException("config cannot be null");
        }
        this.config = config;
        if (agentRepository == null) {
            throw new NullPointerException("agent repository cannot be null");
        }
        this.agentRepository = agentRepository;
        if (publisherReportSender == null) {
            throw new NullPointerException("Publisher report sender cannot be null");
        }
        this.publisherReportSender = publisherReportSender;
        if (adNetworkReportSender == null) {
            throw new NullPointerException("Ad Network report sender cannot be null");
        }
        this.adNetworkReportSender = adNetworkReportSender;
        if (bidBundleWriter == null) {
            throw new NullPointerException("Bid Bundle Writer cannot be null");
        }
        this.bidBundleWriter = bidBundleWriter;
        this.bidManager = agentRepository.getAdxBidManager();
    }

    private void setupBidManager() {
        String[] advertisers = AdxManager.getInstance().getSimulation().getAdxAdvertiserAddresses();
        int i = 0;
        int n = advertisers.length;
        while (i < n) {
            this.bidManager.addAdvertiser(advertisers[i]);
            ++i;
        }
    }

    @Override
    public void nextTimeUnit(int date) {
        if (date == 0) {
            this.userManager.initialize(this.virtualDays);
        } else {
            this.userManager.nextTimeUnit(date);
            this.userManager.triggerBehavior(AdxManager.getInstance().getSimulation().getAuctioneer());
        }
    }

    @Override
    public void setup() {
        this.log = Logger.getLogger(this.getClass().getName());
        this.virtualDays = this.config.getPropertyAsInt("virtual_days", 0);
        this.setupBidManager();
        try {
            AdxUserBehaviorBuilder<AdxUserManager> managerBuilder = this.createBuilder();
            this.userManager = managerBuilder.build(this.config, this.agentRepository, new Random());
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        this.publisherReportManager = this.createPublisherReportManager();
        this.adNetworkReportManager = this.createAdNetworkReportManager();
    }

    private AdNetworkReportManager createAdNetworkReportManager() {
        AdNetworkReportManagerImpl adNetworkReportManager = new AdNetworkReportManagerImpl(this.adNetworkReportSender, this.agentRepository.getEventBus());
        return adNetworkReportManager;
    }

    private AdxPublisherReportManager createPublisherReportManager() {
        AdxPublisherReportManagerImpl queryReportManager = new AdxPublisherReportManagerImpl(this.publisherReportSender, this.agentRepository.getEventBus());
        return queryReportManager;
    }

    protected AdxUserBehaviorBuilder<AdxUserManager> createBuilder() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return (AdxUserBehaviorBuilder)ConfigProxyUtils.createObjectFromProperty(this.config, "adxusermanger.builder", "tau.tac.adx.users.DefaultAdxUserManagerBuilder");
    }

    @Override
    public void stopped() {
    }

    @Override
    public void shutdown() {
    }

    @Override
    public Ranking getRanking(Query query, Auctioneer auctioneer) {
        return auctioneer.runAuction(query).getRanking();
    }

    @Override
    public void messageReceived(Message message) {
        this.userManager.messageReceived(message);
        Transportable content = message.getContent();
        if (content instanceof AdxBidBundle) {
            AdxBidBundle bundle = (AdxBidBundle)content;
            this.log.finer("Recieved " + AdxBidBundle.class.getName() + ": " + bundle);
            this.bidManager.updateBids(message.getSender(), bundle);
        }
    }

    @Override
    public void sendReportsToAll() {
        this.publisherReportManager.sendReportsToAll();
        this.adNetworkReportManager.sendReportsToAll();
    }
}

