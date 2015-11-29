/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.EventBus
 *  com.google.common.eventbus.Subscribe
 */
package tau.tac.adx.report.adn;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import tau.tac.adx.auction.AdxAuctionResult;
import tau.tac.adx.bids.BidInfo;
import tau.tac.adx.bids.Bidder;
import tau.tac.adx.demand.Campaign;
import tau.tac.adx.messages.AuctionMessage;
import tau.tac.adx.report.adn.AdNetworkReport;
import tau.tac.adx.report.adn.AdNetworkReportManager;
import tau.tac.adx.report.adn.AdNetworkReportSender;

public class AdNetworkReportManagerImpl
implements AdNetworkReportManager {
    protected Logger log = Logger.getLogger(AdNetworkReportManagerImpl.class.getName());
    private final Map<String, AdNetworkReport> adNetworkReports = new HashMap<String, AdNetworkReport>();
    private final AdNetworkReportSender adNetworkReportSender;

    public AdNetworkReportManagerImpl(AdNetworkReportSender adNetworkReportSender, EventBus eventBus) {
        this.adNetworkReportSender = adNetworkReportSender;
        eventBus.register((Object)this);
        this.log.info("AdxQueryReportManager created.");
    }

    @Override
    public int size() {
        return this.adNetworkReports.size();
    }

    @Subscribe
    public void auctionPerformed(AuctionMessage message) {
        for (BidInfo bidInfo : message.getAuctionResult().getBidInfos()) {
            String participant = bidInfo.getBidder().getName();
            AdNetworkReport report = this.adNetworkReports.get(participant);
            if (report == null) {
                report = new AdNetworkReport();
                this.adNetworkReports.put(participant, report);
            }
            boolean hasWon = false;
            BidInfo winningBidInfo = message.getAuctionResult().getWinningBidInfo();
            if (winningBidInfo != null) {
                hasWon = winningBidInfo.getBidder().getName().equals(participant);
            }
            if (!bidInfo.getCampaign().getAdvertiser().equals(bidInfo.getBidder().getName())) {
                this.log.log(Level.SEVERE, String.valueOf(bidInfo.getBidder().getName()) + " placed a bid for campaign  #" + bidInfo.getCampaign().getId() + " which belongs to " + bidInfo.getCampaign().getAdvertiser());
            }
            report.addBid(message, bidInfo.getCampaign().getId(), hasWon);
        }
    }

    @Override
    public void sendReportsToAll() {
        for (Map.Entry<String, AdNetworkReport> entry : this.adNetworkReports.entrySet()) {
            this.adNetworkReportSender.broadcastAdNetowrkReport(entry.getKey(), entry.getValue());
        }
        this.adNetworkReports.clear();
    }

    public Map<String, AdNetworkReport> getAdNetworkReports() {
        return this.adNetworkReports;
    }
}

