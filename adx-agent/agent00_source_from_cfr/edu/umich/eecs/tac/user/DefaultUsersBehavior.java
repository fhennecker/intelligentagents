/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.Auction;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.Ranking;
import edu.umich.eecs.tac.sim.AgentRepository;
import edu.umich.eecs.tac.sim.Auctioneer;
import edu.umich.eecs.tac.user.DefaultDistributionBroadcaster;
import edu.umich.eecs.tac.user.DistributionBroadcaster;
import edu.umich.eecs.tac.user.UserBehaviorBuilder;
import edu.umich.eecs.tac.user.UserEventListener;
import edu.umich.eecs.tac.user.UserManager;
import edu.umich.eecs.tac.user.UsersBehavior;
import edu.umich.eecs.tac.user.UsersTransactor;
import edu.umich.eecs.tac.util.config.ConfigProxy;
import edu.umich.eecs.tac.util.config.ConfigProxyUtils;
import java.util.Random;
import se.sics.tasim.aw.Agent;
import se.sics.tasim.aw.Message;
import se.sics.tasim.is.EventWriter;
import se.sics.tasim.sim.SimulationAgent;
import tau.tac.adx.sim.Publisher;

public class DefaultUsersBehavior
implements UsersBehavior {
    private UserManager userManager;
    private DistributionBroadcaster distributionBroadcaster;
    private int virtualDays;
    private ConfigProxy config;
    private AgentRepository agentRepository;
    private UsersTransactor usersTransactor;

    public DefaultUsersBehavior(ConfigProxy config, AgentRepository agentRepository, UsersTransactor usersTransactor) {
        if (config == null) {
            throw new NullPointerException("config cannot be null");
        }
        this.config = config;
        if (agentRepository == null) {
            throw new NullPointerException("agent repository cannot be null");
        }
        this.agentRepository = agentRepository;
        if (usersTransactor == null) {
            throw new NullPointerException("users transactor cannot be null");
        }
        this.usersTransactor = usersTransactor;
    }

    @Override
    public void nextTimeUnit(int date) {
        if (date == 0) {
            this.userManager.initialize(this.virtualDays);
        }
        this.userManager.nextTimeUnit(date);
        this.userManager.triggerBehavior((Publisher)this.agentRepository.getPublishers()[0].getAgent());
    }

    @Override
    public void setup() {
        this.virtualDays = this.config.getPropertyAsInt("virtual_days", 0);
        try {
            UserBehaviorBuilder<UserManager> managerBuilder = this.createBuilder();
            this.userManager = managerBuilder.build(this.config, this.agentRepository, new Random());
            this.addUserEventListener(new ConversionMonitor());
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
    }

    protected UserBehaviorBuilder<UserManager> createBuilder() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return (UserBehaviorBuilder)ConfigProxyUtils.createObjectFromProperty(this.config, "usermanger.builder", "edu.umich.eecs.tac.user.DefaultUserManagerBuilder");
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
    }

    @Override
    public boolean addUserEventListener(UserEventListener listener) {
        return this.userManager.addUserEventListener(listener);
    }

    @Override
    public boolean containsUserEventListener(UserEventListener listener) {
        return this.userManager.containsUserEventListener(listener);
    }

    @Override
    public boolean removeUserEventListener(UserEventListener listener) {
        return this.userManager.removeUserEventListener(listener);
    }

    @Override
    public void broadcastUserDistribution(int usersIndex, EventWriter eventWriter) {
        if (this.distributionBroadcaster == null) {
            this.distributionBroadcaster = new DefaultDistributionBroadcaster(this.userManager);
        }
        this.distributionBroadcaster.broadcastUserDistribution(usersIndex, eventWriter);
    }

    protected class ConversionMonitor
    implements UserEventListener {
        protected ConversionMonitor() {
        }

        @Override
        public void queryIssued(Query query) {
        }

        @Override
        public void viewed(Query query, Ad ad, int slot, String advertiser, boolean isPromoted) {
        }

        @Override
        public void clicked(Query query, Ad ad, int slot, double cpc, String advertiser) {
        }

        @Override
        public void converted(Query query, Ad ad, int slot, double salesProfit, String advertiser) {
            DefaultUsersBehavior.this.usersTransactor.transact(advertiser, salesProfit);
        }
    }

}

