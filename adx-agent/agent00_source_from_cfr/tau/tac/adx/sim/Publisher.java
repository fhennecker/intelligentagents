/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.sim;

import edu.umich.eecs.tac.props.PublisherInfo;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.sim.Auctioneer;
import edu.umich.eecs.tac.sim.PublisherInfoSender;
import edu.umich.eecs.tac.sim.QueryReportSender;
import java.util.logging.Logger;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.is.EventWriter;
import tau.tac.adx.sim.Builtin;
import tau.tac.adx.sim.TACAdxSimulation;

public abstract class Publisher
extends Builtin
implements QueryReportSender,
Auctioneer,
PublisherInfoSender {
    private static final String CONF = "publisher.";
    protected Logger log = Logger.getLogger(Publisher.class.getName());

    public Publisher() {
        super("publisher.");
    }

    public abstract void sendQueryReportsToAll();

    protected void finalize() throws Throwable {
        Logger.global.info("PUBLISHER " + this.getName() + " IS BEING GARBAGED");
        super.finalize();
    }

    protected void charge(String advertiser, double amount) {
        this.getSimulation().transaction(advertiser, this.getAddress(), amount);
    }

    @Override
    public void sendQueryReport(String advertiser, QueryReport report) {
    }

    @Override
    public void sendPublisherInfo(String advertiser) {
        this.sendMessage(advertiser, this.getPublisherInfo());
    }

    @Override
    public void sendPublisherInfoToAll() {
        String[] arrstring = this.getAdvertiserAddresses();
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String advertiser = arrstring[n2];
            this.sendPublisherInfo(advertiser);
            ++n2;
        }
        this.getEventWriter().dataUpdated(this.getSimulation().agentIndex(this.getAddress()), 306, this.getPublisherInfo());
    }

    @Override
    public void broadcastImpressions(String advertiser, int impressions) {
        this.getSimulation().broadcastImpressions(advertiser, impressions);
    }

    @Override
    public void broadcastClicks(String advertiser, int clicks) {
        this.getSimulation().broadcastClicks(advertiser, clicks);
    }

    public abstract void applyBidUpdates();
}

