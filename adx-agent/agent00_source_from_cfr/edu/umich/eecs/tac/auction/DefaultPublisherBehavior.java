/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.auction.AuctionFactory;
import edu.umich.eecs.tac.auction.BidBundleWriter;
import edu.umich.eecs.tac.auction.BidManager;
import edu.umich.eecs.tac.auction.BidManagerImpl;
import edu.umich.eecs.tac.auction.BidTracker;
import edu.umich.eecs.tac.auction.BidTrackerImpl;
import edu.umich.eecs.tac.auction.ClickCharger;
import edu.umich.eecs.tac.auction.PublisherBehavior;
import edu.umich.eecs.tac.auction.QueryReportManager;
import edu.umich.eecs.tac.auction.QueryReportManagerImpl;
import edu.umich.eecs.tac.auction.SpendTracker;
import edu.umich.eecs.tac.auction.SpendTrackerImpl;
import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.Auction;
import edu.umich.eecs.tac.props.BidBundle;
import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.PublisherInfo;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.ReserveInfo;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.SlotInfo;
import edu.umich.eecs.tac.props.UserClickModel;
import edu.umich.eecs.tac.sim.AgentRepository;
import edu.umich.eecs.tac.sim.QueryReportSender;
import edu.umich.eecs.tac.user.UserEventListener;
import edu.umich.eecs.tac.util.config.ConfigProxy;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Message;

public class DefaultPublisherBehavior
implements PublisherBehavior {
    private Logger log;
    private AuctionFactory auctionFactory;
    private RetailCatalog retailCatalog;
    private UserClickModel userClickModel;
    private QueryReportManager queryReportManager;
    private Set<Query> querySpace;
    private BidTracker bidTracker;
    private SpendTracker spendTracker;
    private BidManager bidManager;
    private ConfigProxy publisherConfigProxy;
    private SlotInfo slotInfo;
    private ReserveInfo reserveInfo;
    private PublisherInfo publisherInfo;
    private Random random;
    private final ConfigProxy config;
    private final AgentRepository agentRepository;
    private final QueryReportSender queryReportSender;
    private final ClickCharger clickCharger;
    private final BidBundleWriter bidBundleWriter;

    public DefaultPublisherBehavior(ConfigProxy config, AgentRepository agentRepository, QueryReportSender queryReportSender, ClickCharger clickCharger, BidBundleWriter bidBundleWriter) {
        if (config == null) {
            throw new NullPointerException("config cannot be null");
        }
        this.config = config;
        if (agentRepository == null) {
            throw new NullPointerException("agent repository cannot be null");
        }
        this.agentRepository = agentRepository;
        if (queryReportSender == null) {
            throw new NullPointerException("query report sender cannot be null");
        }
        this.queryReportSender = queryReportSender;
        if (clickCharger == null) {
            throw new NullPointerException("click charger cannot be null");
        }
        this.clickCharger = clickCharger;
        if (bidBundleWriter == null) {
            throw new NullPointerException("bid bundle writer cannot be null");
        }
        this.bidBundleWriter = bidBundleWriter;
    }

    @Override
    public void nextTimeUnit(int date) {
        this.spendTracker.reset();
        if (this.bidManager != null) {
            this.bidManager.nextTimeUnit(date);
        }
    }

    @Override
    public void setup() {
        this.log = Logger.getLogger(DefaultPublisherBehavior.class.getName());
        this.random = new Random();
        this.spendTracker = this.createSpendTracker();
        this.bidTracker = this.createBidTracker();
        this.setPublisherInfo(this.createPublisherInfo());
        this.auctionFactory = this.createAuctionFactory();
        this.auctionFactory.setPublisherInfo(this.getPublisherInfo());
        this.queryReportManager = this.createQueryReportManager();
    }

    private PublisherInfo createPublisherInfo() {
        double squashingMin = this.config.getPropertyAsDouble("squashing.min", 0.0);
        double squashingMax = this.config.getPropertyAsDouble("squashing.max", 1.0);
        double squashingPower = this.config.getPropertyAsDouble("squashing.power", 1.0);
        double squashing = Math.pow(squashingMin + this.random.nextDouble() * (squashingMax - squashingMin), 1.0 / squashingPower);
        PublisherInfo publisherInfo = new PublisherInfo();
        publisherInfo.setSquashingParameter(squashing);
        publisherInfo.lock();
        return publisherInfo;
    }

    private BidTracker createBidTracker() {
        BidTrackerImpl bidTracker = new BidTrackerImpl(0);
        return bidTracker;
    }

    private SpendTracker createSpendTracker() {
        SpendTrackerImpl spendTracker = new SpendTrackerImpl(0);
        return spendTracker;
    }

    private QueryReportManager createQueryReportManager() {
        QueryReportManagerImpl queryReportManager = new QueryReportManagerImpl(this.queryReportSender, 0);
        String[] arrstring = this.agentRepository.getAdvertiserAddresses();
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String advertiser = arrstring[n2];
            queryReportManager.addAdvertiser(advertiser);
            ++n2;
        }
        return queryReportManager;
    }

    private BidManager createBidManager(BidTracker bidTracker, SpendTracker spendTracker) {
        BidManagerImpl bidManager = new BidManagerImpl(this.userClickModel, bidTracker, spendTracker);
        String[] advertisers = this.agentRepository.getAdvertiserAddresses();
        int i = 0;
        int n = advertisers.length;
        while (i < n) {
            bidManager.addAdvertiser(advertisers[i]);
            ++i;
        }
        return bidManager;
    }

    private AuctionFactory createAuctionFactory() {
        String auctionFactoryClass = this.config.getProperty("auctionfactory.class", "edu.umich.eecs.tac.auction.LahaiePennockAuctionFactory");
        AuctionFactory factory = null;
        try {
            factory = (AuctionFactory)Class.forName(auctionFactoryClass).newInstance();
        }
        catch (InstantiationException e) {
            this.log.log(Level.SEVERE, "error creating auction factory", e);
        }
        catch (IllegalAccessException e) {
            this.log.log(Level.SEVERE, "error creating auction factory", e);
        }
        catch (ClassNotFoundException e) {
            this.log.log(Level.SEVERE, "error creating auction factory", e);
        }
        return factory;
    }

    @Override
    public void stopped() {
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void messageReceived(Message message) {
        String sender = message.getSender();
        Transportable content = message.getContent();
        if (content instanceof BidBundle) {
            this.handleBidBundle(sender, (BidBundle)content);
        } else if (content instanceof UserClickModel) {
            this.handleUserClickModel((UserClickModel)content);
        } else if (content instanceof RetailCatalog) {
            this.handleRetailCatalog((RetailCatalog)content);
        } else if (content instanceof SlotInfo) {
            this.handleSlotInfo((SlotInfo)content);
        } else if (content instanceof ReserveInfo) {
            this.handleReserveInfo((ReserveInfo)content);
        }
    }

    private void handleSlotInfo(SlotInfo slotInfo) {
        this.slotInfo = slotInfo;
        if (this.auctionFactory != null) {
            this.auctionFactory.setSlotInfo(slotInfo);
        }
    }

    private void handleReserveInfo(ReserveInfo reserveInfo) {
        this.reserveInfo = reserveInfo;
        if (this.auctionFactory != null) {
            this.auctionFactory.setReserveInfo(reserveInfo);
        }
    }

    private void handleUserClickModel(UserClickModel userClickModel) {
        this.userClickModel = userClickModel;
        this.bidManager = this.createBidManager(this.bidTracker, this.spendTracker);
        if (this.auctionFactory != null) {
            this.auctionFactory.setBidManager(this.bidManager);
        }
    }

    private void handleBidBundle(String advertiser, BidBundle bidBundle) {
        if (this.bidManager == null) {
            this.log.warning("Received BidBundle from " + advertiser + " before initialization");
        } else {
            this.bidManager.updateBids(advertiser, bidBundle);
            this.bidBundleWriter.writeBundle(advertiser, bidBundle);
        }
    }

    private void handleRetailCatalog(RetailCatalog retailCatalog) {
        this.retailCatalog = retailCatalog;
        this.generatePossibleQueries();
        this.bidTracker.initializeQuerySpace(this.querySpace);
    }

    private void generatePossibleQueries() {
        if (this.retailCatalog != null && this.querySpace == null) {
            this.querySpace = new HashSet<Query>();
            for (Product product : this.retailCatalog) {
                Query f0 = new Query();
                Query f1_manufacturer = new Query(product.getManufacturer(), null);
                Query f1_component = new Query(null, product.getComponent());
                Query f2 = new Query(product.getManufacturer(), product.getComponent());
                this.querySpace.add(f0);
                this.querySpace.add(f1_manufacturer);
                this.querySpace.add(f1_component);
                this.querySpace.add(f2);
            }
        }
    }

    @Override
    public void sendQueryReportsToAll() {
        if (this.queryReportManager != null) {
            this.queryReportManager.sendQueryReportToAll();
        }
    }

    @Override
    public Auction runAuction(Query query) {
        if (this.auctionFactory != null) {
            return this.auctionFactory.runAuction(query);
        }
        return null;
    }

    @Override
    public PublisherInfo getPublisherInfo() {
        return this.publisherInfo;
    }

    @Override
    public void setPublisherInfo(PublisherInfo publisherInfo) {
        this.publisherInfo = publisherInfo;
    }

    @Override
    public void applyBidUpdates() {
        this.bidManager.applyBidUpdates();
    }

    protected class ClickMonitor
    implements UserEventListener {
        protected ClickMonitor() {
        }

        @Override
        public void queryIssued(Query query) {
        }

        @Override
        public void viewed(Query query, Ad ad, int slot, String advertiser, boolean isPromoted) {
        }

        @Override
        public void clicked(Query query, Ad ad, int slot, double cpc, String advertiser) {
            DefaultPublisherBehavior.this.clickCharger.charge(advertiser, cpc);
            DefaultPublisherBehavior.this.spendTracker.addCost(advertiser, query, cpc);
        }

        @Override
        public void converted(Query query, Ad ad, int slot, double salesProfit, String advertiser) {
        }
    }

}

