/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.agents;

import edu.umich.eecs.tac.props.AdvertiserInfo;
import edu.umich.eecs.tac.sim.AgentRepository;
import edu.umich.eecs.tac.sim.SalesAnalyst;
import edu.umich.eecs.tac.user.DefaultUsersBehavior;
import edu.umich.eecs.tac.user.UserEventListener;
import edu.umich.eecs.tac.user.UsersBehavior;
import edu.umich.eecs.tac.user.UsersTransactor;
import edu.umich.eecs.tac.util.config.ConfigProxy;
import java.util.Map;
import java.util.logging.Logger;
import se.sics.tasim.aw.Message;
import se.sics.tasim.is.EventWriter;
import se.sics.tasim.sim.SimulationAgent;
import tau.tac.adx.sim.TACAdxSimulation;
import tau.tac.adx.sim.Users;

public class DefaultUsers
extends Users {
    private final UsersBehavior usersBehavior;

    public DefaultUsers() {
        this.usersBehavior = new DefaultUsersBehavior(new UsersConfigProxy(), new AgentRepositoryProxy(), new UsersTransactorProxy());
    }

    @Override
    public void nextTimeUnit(int date) {
        this.usersBehavior.nextTimeUnit(date);
    }

    @Override
    protected void setup() {
        super.setup();
        this.log = Logger.getLogger(DefaultUsers.class.getName());
        this.usersBehavior.setup();
    }

    @Override
    protected void stopped() {
        this.usersBehavior.stopped();
    }

    @Override
    protected void shutdown() {
        this.usersBehavior.shutdown();
    }

    @Override
    protected void messageReceived(Message message) {
        this.usersBehavior.messageReceived(message);
    }

    @Override
    public boolean addUserEventListener(UserEventListener listener) {
        return this.usersBehavior.addUserEventListener(listener);
    }

    @Override
    public boolean containsUserEventListener(UserEventListener listener) {
        return this.usersBehavior.containsUserEventListener(listener);
    }

    @Override
    public boolean removeUserEventListener(UserEventListener listener) {
        return this.usersBehavior.removeUserEventListener(listener);
    }

    @Override
    public void broadcastUserDistribution() {
        this.usersBehavior.broadcastUserDistribution(this.getIndex(), this.getEventWriter());
    }

    protected class AgentRepositoryProxy
    implements AgentRepository {
        protected AgentRepositoryProxy() {
        }

        @Override
        public Map<String, AdvertiserInfo> getAdvertiserInfo() {
            return DefaultUsers.this.getSimulation().getAdvertiserInfo();
        }

        @Override
        public SimulationAgent[] getPublishers() {
            return DefaultUsers.this.getSimulation().getPublishers();
        }

        @Override
        public SalesAnalyst getSalesAnalyst() {
            return null;
        }

        @Override
        public int getNumberOfAdvertisers() {
            return DefaultUsers.this.getSimulation().getNumberOfAdvertisers();
        }

        @Override
        public String[] getAdvertiserAddresses() {
            return DefaultUsers.this.getSimulation().getAdvertiserAddresses();
        }
    }

    protected class UsersConfigProxy
    implements ConfigProxy {
        protected UsersConfigProxy() {
        }

        @Override
        public String getProperty(String name) {
            return DefaultUsers.this.getProperty(name);
        }

        @Override
        public String getProperty(String name, String defaultValue) {
            return DefaultUsers.this.getProperty(name, defaultValue);
        }

        @Override
        public String[] getPropertyAsArray(String name) {
            return DefaultUsers.this.getPropertyAsArray(name);
        }

        @Override
        public String[] getPropertyAsArray(String name, String defaultValue) {
            return DefaultUsers.this.getPropertyAsArray(name, defaultValue);
        }

        @Override
        public int getPropertyAsInt(String name, int defaultValue) {
            return DefaultUsers.this.getPropertyAsInt(name, defaultValue);
        }

        @Override
        public int[] getPropertyAsIntArray(String name) {
            return DefaultUsers.this.getPropertyAsIntArray(name);
        }

        @Override
        public int[] getPropertyAsIntArray(String name, String defaultValue) {
            return DefaultUsers.this.getPropertyAsIntArray(name, defaultValue);
        }

        @Override
        public long getPropertyAsLong(String name, long defaultValue) {
            return DefaultUsers.this.getPropertyAsLong(name, defaultValue);
        }

        @Override
        public float getPropertyAsFloat(String name, float defaultValue) {
            return DefaultUsers.this.getPropertyAsFloat(name, defaultValue);
        }

        @Override
        public double getPropertyAsDouble(String name, double defaultValue) {
            return DefaultUsers.this.getPropertyAsDouble(name, defaultValue);
        }
    }

    protected class UsersTransactorProxy
    implements UsersTransactor {
        protected UsersTransactorProxy() {
        }

        @Override
        public void transact(String address, double amount) {
            DefaultUsers.this.transact(address, amount);
        }
    }

}

