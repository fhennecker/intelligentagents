/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.EventBus
 *  com.google.common.eventbus.Subscribe
 */
package tau.tac.adx.report.publisher;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.util.logging.Logger;
import tau.tac.adx.messages.AuctionMessage;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.report.publisher.AdxPublisherReport;
import tau.tac.adx.report.publisher.AdxPublisherReportManager;
import tau.tac.adx.report.publisher.AdxPublisherReportSender;

public class AdxPublisherReportManagerImpl
implements AdxPublisherReportManager {
    protected Logger log = Logger.getLogger(AdxPublisherReportManagerImpl.class.getName());
    private AdxPublisherReport publisherReport = new AdxPublisherReport();
    private final AdxPublisherReportSender publisherReportSender;

    public AdxPublisherReportManagerImpl(AdxPublisherReportSender publisherReportSender, EventBus eventBus) {
        this.publisherReportSender = publisherReportSender;
        eventBus.register((Object)this);
        this.log.info("AdxQueryReportManager created.");
    }

    @Override
    public int size() {
        return this.publisherReport.size();
    }

    @Subscribe
    public void queryIssued(AuctionMessage message) {
        this.publisherReport.addQuery(message.getQuery());
    }

    @Override
    public void sendReportsToAll() {
        this.publisherReportSender.broadcastPublisherReport(this.publisherReport);
        this.publisherReport = new AdxPublisherReport();
    }
}

