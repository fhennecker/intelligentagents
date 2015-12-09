/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.agents;

import edu.umich.eecs.tac.auction.BidBundleWriter;
import edu.umich.eecs.tac.auction.ClickCharger;
import edu.umich.eecs.tac.auction.DefaultPublisherBehavior;
import edu.umich.eecs.tac.auction.PublisherBehavior;
import edu.umich.eecs.tac.props.AdvertiserInfo;
import edu.umich.eecs.tac.props.Auction;
import edu.umich.eecs.tac.props.BidBundle;
import edu.umich.eecs.tac.props.PublisherInfo;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.sim.AgentRepository;
import edu.umich.eecs.tac.sim.QueryReportSender;
import edu.umich.eecs.tac.sim.SalesAnalyst;
import edu.umich.eecs.tac.util.config.ConfigProxy;
import java.util.Map;
import java.util.logging.Logger;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Message;
import se.sics.tasim.aw.TimeListener;
import se.sics.tasim.is.EventWriter;
import se.sics.tasim.sim.SimulationAgent;
import tau.tac.adx.sim.Publisher;
import tau.tac.adx.sim.TACAdxSimulation;

public class DefaultPublisher
extends Publisher {
    private final PublisherBehavior publisherBehavior;

    public DefaultPublisher() {
        this.publisherBehavior = new DefaultPublisherBehavior(new PublisherConfigProxy(), new AgentRepositoryProxy(), this, new ClickChargerProxy(), new BidBundleWriterProxy());
    }

    @Override
    public void nextTimeUnit(int date) {
        this.publisherBehavior.nextTimeUnit(date);
    }

    @Override
    protected void setup() {
        this.log = Logger.getLogger(DefaultPublisher.class.getName());
        this.publisherBehavior.setup();
        this.addTimeListener(this);
    }

    @Override
    protected void stopped() {
        this.removeTimeListener(this);
        this.publisherBehavior.stopped();
    }

    @Override
    protected void shutdown() {
        this.publisherBehavior.stopped();
    }

    @Override
    protected void messageReceived(Message message) {
        this.publisherBehavior.messageReceived(message);
    }

    @Override
    public PublisherInfo getPublisherInfo() {
        return this.publisherBehavior.getPublisherInfo();
    }

    void setPublisherInfo(PublisherInfo publisherInfo) {
        this.publisherBehavior.setPublisherInfo(publisherInfo);
    }

    @Override
    public void sendQueryReportsToAll() {
        this.publisherBehavior.sendQueryReportsToAll();
    }

    @Override
    public Auction runAuction(Query query) {
        return this.publisherBehavior.runAuction(query);
    }

    @Override
    public void applyBidUpdates() {
        this.publisherBehavior.applyBidUpdates();
    }

    protected class AgentRepositoryProxy
    implements AgentRepository {
        protected AgentRepositoryProxy() {
        }

        @Override
        public Map<String, AdvertiserInfo> getAdvertiserInfo() {
            return DefaultPublisher.this.getSimulation().getAdvertiserInfo();
        }

        @Override
        public SimulationAgent[] getPublishers() {
            return DefaultPublisher.this.getSimulation().getPublishers();
        }

        @Override
        public SalesAnalyst getSalesAnalyst() {
            return null;
        }

        @Override
        public int getNumberOfAdvertisers() {
            return DefaultPublisher.this.getSimulation().getNumberOfAdvertisers();
        }

        @Override
        public String[] getAdvertiserAddresses() {
            return DefaultPublisher.this.getSimulation().getAdvertiserAddresses();
        }
    }

    protected class BidBundleWriterProxy
    implements BidBundleWriter {
        protected BidBundleWriterProxy() {
        }

        @Override
        public void writeBundle(String advertiser, BidBundle bundle) {
            int agentIndex = DefaultPublisher.this.getSimulation().agentIndex(advertiser);
            DefaultPublisher.this.getEventWriter().dataUpdated(agentIndex, 300, bundle);
        }
    }

    protected class ClickChargerProxy
    implements ClickCharger {
        protected ClickChargerProxy() {
        }

        @Override
        public void charge(String advertiser, double cpc) {
            DefaultPublisher.this.charge(advertiser, cpc);
        }
    }

    protected class PublisherConfigProxy
    implements ConfigProxy {
        protected PublisherConfigProxy() {
        }

        @Override
        public String getProperty(String name) {
            return DefaultPublisher.this.getProperty(name);
        }

        @Override
        public String getProperty(String name, String defaultValue) {
            return DefaultPublisher.this.getProperty(name, defaultValue);
        }

        @Override
        public String[] getPropertyAsArray(String name) {
            return DefaultPublisher.this.getPropertyAsArray(name);
        }

        @Override
        public String[] getPropertyAsArray(String name, String defaultValue) {
            return DefaultPublisher.this.getPropertyAsArray(name, defaultValue);
        }

        @Override
        public int getPropertyAsInt(String name, int defaultValue) {
            return DefaultPublisher.this.getPropertyAsInt(name, defaultValue);
        }

        @Override
        public int[] getPropertyAsIntArray(String name) {
            return DefaultPublisher.this.getPropertyAsIntArray(name);
        }

        @Override
        public int[] getPropertyAsIntArray(String name, String defaultValue) {
            return DefaultPublisher.this.getPropertyAsIntArray(name, defaultValue);
        }

        @Override
        public long getPropertyAsLong(String name, long defaultValue) {
            return DefaultPublisher.this.getPropertyAsLong(name, defaultValue);
        }

        @Override
        public float getPropertyAsFloat(String name, float defaultValue) {
            return DefaultPublisher.this.getPropertyAsFloat(name, defaultValue);
        }

        @Override
        public double getPropertyAsDouble(String name, double defaultValue) {
            return DefaultPublisher.this.getPropertyAsDouble(name, defaultValue);
        }
    }

}

