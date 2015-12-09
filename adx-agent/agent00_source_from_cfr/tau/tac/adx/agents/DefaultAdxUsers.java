/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.agents;

import edu.umich.eecs.tac.util.config.ConfigProxy;
import java.util.logging.Logger;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Message;
import se.sics.tasim.is.EventWriter;
import tau.tac.adx.AdxManager;
import tau.tac.adx.agents.behaviors.DefaultAdxUsersBehavior;
import tau.tac.adx.auction.AdxBidBundleWriter;
import tau.tac.adx.props.AdxBidBundle;
import tau.tac.adx.report.adn.AdNetworkReportSender;
import tau.tac.adx.report.publisher.AdxPublisherReportSender;
import tau.tac.adx.sim.AdxAgentRepository;
import tau.tac.adx.sim.AdxUsers;
import tau.tac.adx.sim.TACAdxSimulation;
import tau.tac.adx.users.AdxUsersBehavior;

public class DefaultAdxUsers
extends AdxUsers {
    private final AdxUsersBehavior usersBehavior;

    public DefaultAdxUsers() {
        this.usersBehavior = new DefaultAdxUsersBehavior(new UsersConfigProxy(), AdxManager.getInstance().getSimulation(), this, this, new BidBundleWriterProxy());
    }

    @Override
    public void nextTimeUnit(int date) {
        this.usersBehavior.nextTimeUnit(date);
        if (date > 0) {
            this.sendReportsToAll();
        }
    }

    public void preNextTimeUnit(int date) {
    }

    @Override
    protected void setup() {
        super.setup();
        this.log = Logger.getLogger(DefaultAdxUsers.class.getName());
        AdxManager.getInstance().setAdxAgentAddress(this.getAddress());
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
    public void sendReportsToAll() {
        this.usersBehavior.sendReportsToAll();
    }

    protected class BidBundleWriterProxy
    implements AdxBidBundleWriter {
        protected BidBundleWriterProxy() {
        }

        @Override
        public void writeBundle(String advertiser, AdxBidBundle bundle) {
            int agentIndex = DefaultAdxUsers.this.getSimulation().agentIndex(advertiser);
            DefaultAdxUsers.this.getEventWriter().dataUpdated(agentIndex, 402, bundle);
        }
    }

    protected class UsersConfigProxy
    implements ConfigProxy {
        protected UsersConfigProxy() {
        }

        @Override
        public String getProperty(String name) {
            return DefaultAdxUsers.this.getProperty(name);
        }

        @Override
        public String getProperty(String name, String defaultValue) {
            return DefaultAdxUsers.this.getProperty(name, defaultValue);
        }

        @Override
        public String[] getPropertyAsArray(String name) {
            return DefaultAdxUsers.this.getPropertyAsArray(name);
        }

        @Override
        public String[] getPropertyAsArray(String name, String defaultValue) {
            return DefaultAdxUsers.this.getPropertyAsArray(name, defaultValue);
        }

        @Override
        public int getPropertyAsInt(String name, int defaultValue) {
            return DefaultAdxUsers.this.getPropertyAsInt(name, defaultValue);
        }

        @Override
        public int[] getPropertyAsIntArray(String name) {
            return DefaultAdxUsers.this.getPropertyAsIntArray(name);
        }

        @Override
        public int[] getPropertyAsIntArray(String name, String defaultValue) {
            return DefaultAdxUsers.this.getPropertyAsIntArray(name, defaultValue);
        }

        @Override
        public long getPropertyAsLong(String name, long defaultValue) {
            return DefaultAdxUsers.this.getPropertyAsLong(name, defaultValue);
        }

        @Override
        public float getPropertyAsFloat(String name, float defaultValue) {
            return DefaultAdxUsers.this.getPropertyAsFloat(name, defaultValue);
        }

        @Override
        public double getPropertyAsDouble(String name, double defaultValue) {
            return DefaultAdxUsers.this.getPropertyAsDouble(name, defaultValue);
        }
    }

}

