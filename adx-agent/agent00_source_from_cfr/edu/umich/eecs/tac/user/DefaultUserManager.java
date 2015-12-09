/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.props.Auction;
import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.UserClickModel;
import edu.umich.eecs.tac.sim.Auctioneer;
import edu.umich.eecs.tac.user.DefaultUsersInitializer;
import edu.umich.eecs.tac.user.QueryState;
import edu.umich.eecs.tac.user.User;
import edu.umich.eecs.tac.user.UserEventListener;
import edu.umich.eecs.tac.user.UserManager;
import edu.umich.eecs.tac.user.UserQueryManager;
import edu.umich.eecs.tac.user.UserTransitionManager;
import edu.umich.eecs.tac.user.UserViewManager;
import edu.umich.eecs.tac.user.UsersInitializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Message;

public class DefaultUserManager
implements UserManager {
    protected Logger log = Logger.getLogger(DefaultUserManager.class.getName());
    private final Object lock = new Object();
    private List<User> users;
    private Random random;
    private RetailCatalog retailCatalog;
    private UserQueryManager queryManager;
    private UserTransitionManager transitionManager;
    private UserViewManager viewManager;
    private UserClickModel userClickModel;
    private UsersInitializer usersInitializer;

    public DefaultUserManager(RetailCatalog retailCatalog, UserTransitionManager transitionManager, UserQueryManager queryManager, UserViewManager viewManager, int populationSize) {
        this(retailCatalog, transitionManager, queryManager, viewManager, populationSize, new Random());
    }

    public DefaultUserManager(RetailCatalog retailCatalog, UserTransitionManager transitionManager, UserQueryManager queryManager, UserViewManager viewManager, int populationSize, Random random) {
        if (retailCatalog == null) {
            throw new NullPointerException("Retail catalog cannot be null");
        }
        if (transitionManager == null) {
            throw new NullPointerException("User transition manager cannot be null");
        }
        if (queryManager == null) {
            throw new NullPointerException("User query manager cannot be null");
        }
        if (viewManager == null) {
            throw new NullPointerException("User view manager cannot be null");
        }
        if (populationSize < 0) {
            throw new IllegalArgumentException("Population size cannot be negative");
        }
        if (random == null) {
            throw new NullPointerException("Random number generator cannot be null");
        }
        this.retailCatalog = retailCatalog;
        this.random = random;
        this.transitionManager = transitionManager;
        this.queryManager = queryManager;
        this.viewManager = viewManager;
        this.usersInitializer = new DefaultUsersInitializer(transitionManager);
        this.users = this.buildUsers(retailCatalog, populationSize);
    }

    private List<User> buildUsers(RetailCatalog catalog, int populationSize) {
        ArrayList<User> users = new ArrayList<User>();
        for (Product product : catalog) {
            int i = 0;
            while (i < populationSize) {
                users.add(new User(QueryState.NON_SEARCHING, product));
                ++i;
            }
        }
        return users;
    }

    @Override
    public void initialize(int virtualDays) {
        this.usersInitializer.initialize(this.users, virtualDays);
    }

    @Override
    public void triggerBehavior(Auctioneer auctioneer) {
        Object object = this.lock;
        synchronized (object) {
            this.log.finest("START OF USER TRIGGER");
            Collections.shuffle(this.users, this.random);
            for (User user : this.users) {
                boolean transacted = this.handleSearch(user, auctioneer);
                this.handleTransition(user, transacted);
            }
            this.log.finest("FINISH OF USER TRIGGER");
        }
    }

    private boolean handleSearch(User user, Auctioneer auctioneer) {
        boolean transacted = false;
        Query query = this.generateQuery(user);
        if (query != null) {
            Auction auction = auctioneer.runAuction(query);
            transacted = this.handleImpression(query, auction, user);
        }
        return transacted;
    }

    private boolean handleImpression(Query query, Auction auction, User user) {
        return this.viewManager.processImpression(user, query, auction);
    }

    private void handleTransition(User user, boolean transacted) {
        user.setState(this.transitionManager.transition(user, transacted));
    }

    private Query generateQuery(User user) {
        return this.queryManager.generateQuery(user);
    }

    @Override
    public boolean addUserEventListener(UserEventListener listener) {
        Object object = this.lock;
        synchronized (object) {
            return this.viewManager.addUserEventListener(listener);
        }
    }

    @Override
    public boolean containsUserEventListener(UserEventListener listener) {
        Object object = this.lock;
        synchronized (object) {
            return this.viewManager.containsUserEventListener(listener);
        }
    }

    @Override
    public boolean removeUserEventListener(UserEventListener listener) {
        Object object = this.lock;
        synchronized (object) {
            return this.viewManager.removeUserEventListener(listener);
        }
    }

    @Override
    public void nextTimeUnit(int timeUnit) {
        this.viewManager.nextTimeUnit(timeUnit);
        this.queryManager.nextTimeUnit(timeUnit);
        this.transitionManager.nextTimeUnit(timeUnit);
    }

    @Override
    public int[] getStateDistribution() {
        int[] distribution = new int[QueryState.values().length];
        for (User user : this.users) {
            int[] arrn = distribution;
            int n = user.getState().ordinal();
            arrn[n] = arrn[n] + 1;
        }
        return distribution;
    }

    @Override
    public int[] getStateDistribution(Product product) {
        int[] distribution = new int[QueryState.values().length];
        for (User user : this.users) {
            if (user.getProduct() != product) continue;
            int[] arrn = distribution;
            int n = user.getState().ordinal();
            arrn[n] = arrn[n] + 1;
        }
        return distribution;
    }

    @Override
    public RetailCatalog getRetailCatalog() {
        return this.retailCatalog;
    }

    @Override
    public UserClickModel getUserClickModel() {
        return this.userClickModel;
    }

    @Override
    public void setUserClickModel(UserClickModel userClickModel) {
        this.userClickModel = userClickModel;
        this.viewManager.setUserClickModel(userClickModel);
    }

    @Override
    public void messageReceived(Message message) {
        Transportable content = message.getContent();
        if (content instanceof UserClickModel) {
            this.setUserClickModel((UserClickModel)content);
        }
    }
}

