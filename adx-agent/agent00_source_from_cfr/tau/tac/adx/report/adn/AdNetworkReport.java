/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.report.adn;

import edu.umich.eecs.tac.props.AbstractKeyedEntryList;
import edu.umich.eecs.tac.props.KeyedEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.auction.AdxAuctionResult;
import tau.tac.adx.devices.Device;
import tau.tac.adx.messages.AuctionMessage;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.report.adn.AdNetworkKey;
import tau.tac.adx.report.adn.AdNetworkReportEntry;
import tau.tac.adx.users.AdxUser;

public class AdNetworkReport
extends AbstractKeyedEntryList<AdNetworkKey, AdNetworkReportEntry> {
    private static final long serialVersionUID = -7957495904471250085L;
    private Map<AdNetworkKey, AdNetworkReportEntry> entryMap = new HashMap<AdNetworkKey, AdNetworkReportEntry>();

    @Override
    protected final Class<AdNetworkReportEntry> entryClass() {
        return AdNetworkReportEntry.class;
    }

    @Override
    protected AdNetworkReportEntry createEntry(AdNetworkKey key) {
        return new AdNetworkReportEntry(key);
    }

    public AdNetworkReportEntry addReportEntry(AdNetworkKey adNetworkKey) {
        this.lockCheck();
        int index = this.addKey(adNetworkKey);
        AdNetworkReportEntry entry = (AdNetworkReportEntry)this.getEntry(index);
        this.entryMap.put(adNetworkKey, entry);
        return entry;
    }

    public AdNetworkReportEntry getAdNetworkReportEntry(AdNetworkKey adNetworkKey) {
        return (AdNetworkReportEntry)this.getEntry(adNetworkKey);
    }

    private AdNetworkReportEntry getCachedAdNetworkReportEntry(AdNetworkKey adNetworkKey) {
        return this.entryMap.get(adNetworkKey);
    }

    private AdNetworkKey getAdNetworkKey(AuctionMessage message, int campaignId) {
        AdxQuery query = message.getQuery();
        return new AdNetworkKey(message.getUser(), query.getPublisher(), query.getDevice(), query.getAdType(), campaignId);
    }

    public void addBid(AuctionMessage auctionMessage, int campaignId, boolean hasWon) {
        AdNetworkKey adNetworkKey = this.getAdNetworkKey(auctionMessage, campaignId);
        AdNetworkReportEntry reportEntry = this.getCachedAdNetworkReportEntry(adNetworkKey);
        if (reportEntry == null) {
            reportEntry = this.addReportEntry(adNetworkKey);
        }
        reportEntry.addAuctionResult(auctionMessage.getAuctionResult(), hasWon);
    }

    public double getDailyCost() {
        double result = 0.0;
        for (AdNetworkReportEntry adNetworkReportEntry : this.getEntries()) {
            result += adNetworkReportEntry.getCost();
        }
        return result / 1000.0;
    }
}

