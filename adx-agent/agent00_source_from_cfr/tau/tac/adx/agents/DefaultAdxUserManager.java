/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.EventBus
 */
package tau.tac.adx.agents;

import com.google.common.eventbus.EventBus;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import se.sics.tasim.aw.Message;
import tau.tac.adx.AdxManager;
import tau.tac.adx.auction.AdxAuctionResult;
import tau.tac.adx.messages.AuctionMessage;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.props.PublisherCatalog;
import tau.tac.adx.publishers.AdxPublisher;
import tau.tac.adx.publishers.reserve.UserAdTypeReservePriceManager;
import tau.tac.adx.sim.AdxAuctioneer;
import tau.tac.adx.users.AdxUser;
import tau.tac.adx.users.AdxUserManager;
import tau.tac.adx.users.AdxUserQueryManager;

public class DefaultAdxUserManager
implements AdxUserManager {
    protected Logger log = Logger.getLogger(DefaultAdxUserManager.class.getName());
    private final Object lock = new Object();
    private final List<AdxUser> users;
    private final Random random;
    private final PublisherCatalog publisherCatalog;
    private final AdxUserQueryManager queryManager;
    private final EventBus eventBus;

    public DefaultAdxUserManager(PublisherCatalog publisherCatalog, List<AdxUser> users, AdxUserQueryManager queryManager, int populationSize, EventBus eventBus) {
        if (publisherCatalog == null) {
            throw new NullPointerException("Publisher catalog cannot be null");
        }
        if (users == null) {
            throw new NullPointerException("User list cannot be null");
        }
        if (queryManager == null) {
            throw new NullPointerException("User query manager cannot be null");
        }
        if (populationSize < 0) {
            throw new IllegalArgumentException("Population size cannot be negative");
        }
        if (eventBus == null) {
            throw new NullPointerException("Event bus cannot be null");
        }
        this.publisherCatalog = publisherCatalog;
        this.random = new Random();
        this.queryManager = queryManager;
        this.users = users;
        this.eventBus = eventBus;
    }

    @Override
    public void initialize(int virtualDays) {
    }

    @Override
    public void triggerBehavior(AdxAuctioneer auctioneer) {
        Object object = this.lock;
        synchronized (object) {
            this.log.finest("START OF USER TRIGGER");
            Collections.shuffle(this.users, this.random);
            for (AdxPublisher publisher : AdxManager.getInstance().getPublishers()) {
                publisher.getReservePriceManager().updateDailyBaselineAverage();
            }
            this.log.fine("##################################### S-Invoking users activity");
            long pre = System.currentTimeMillis();
            for (AdxUser user : this.users) {
                this.handleUserActivity(user, auctioneer);
            }
            long post = System.currentTimeMillis();
            this.log.fine("##################################### E-Invoking users activity - total time in millis: " + (post - pre));
            this.log.finest("FINISH OF USER TRIGGER");
        }
    }

    private void handleUserActivity(AdxUser user, AdxAuctioneer auctioneer) {
        do {
            this.handleSearch(user, auctioneer);
        } while (user.getpContinue() > this.random.nextDouble());
    }

    private void handleSearch(AdxUser user, AdxAuctioneer auctioneer) {
        boolean transacted = false;
        AdxQuery query = this.generateQuery(user);
        if (query != null) {
            AdxAuctionResult auctionResult = auctioneer.runAuction(query);
            this.eventBus.post((Object)new AuctionMessage(auctionResult, query, user));
        }
    }

    private AdxQuery generateQuery(AdxUser user) {
        return this.queryManager.generateQuery(user);
    }

    @Override
    public void nextTimeUnit(int timeUnit) {
        this.queryManager.nextTimeUnit(timeUnit);
    }

    @Override
    public void messageReceived(Message message) {
    }

    @Override
    public PublisherCatalog getPublisherCatalog() {
        return this.publisherCatalog;
    }
}

