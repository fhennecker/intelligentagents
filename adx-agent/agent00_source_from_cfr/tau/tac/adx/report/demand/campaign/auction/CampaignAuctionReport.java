/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.report.demand.campaign.auction;

import edu.umich.eecs.tac.props.AbstractKeyedEntryList;
import edu.umich.eecs.tac.props.KeyedEntry;
import java.text.ParseException;
import java.util.List;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import tau.tac.adx.report.demand.campaign.auction.CampaignAuctionReportEntry;
import tau.tac.adx.report.demand.campaign.auction.CampaignAuctionReportKey;

public class CampaignAuctionReport
extends AbstractKeyedEntryList<CampaignAuctionReportKey, CampaignAuctionReportEntry> {
    private static final long serialVersionUID = -383908225939942653L;
    private static String CAMPAIGN_ID_KEY = "CAMPAIGN_ID_KEY";
    private static String WINNER_KEY = "WINNER_KEY";
    private int campaignID;
    private String winner;

    public CampaignAuctionReport() {
    }

    public int getCampaignID() {
        return this.campaignID;
    }

    public void setCampaignID(int campaignID) {
        this.campaignID = campaignID;
    }

    public String getWinner() {
        return this.winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public CampaignAuctionReport(int campaignID) {
        this.campaignID = campaignID;
    }

    @Override
    protected CampaignAuctionReportEntry createEntry(CampaignAuctionReportKey key) {
        return new CampaignAuctionReportEntry(key);
    }

    @Override
    protected final Class<CampaignAuctionReportEntry> entryClass() {
        return CampaignAuctionReportEntry.class;
    }

    public String toMyString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (CampaignAuctionReportEntry entry : this.getEntries()) {
            stringBuilder.append(entry.getKey()).append(" : ").append(entry).append("\n");
        }
        return "CampaignReport: " + stringBuilder;
    }

    public CampaignAuctionReportEntry addReportEntry(CampaignAuctionReportKey campaignReportKey) {
        this.lockCheck();
        int index = this.addKey(campaignReportKey);
        return (CampaignAuctionReportEntry)this.getEntry(index);
    }

    public CampaignAuctionReportEntry getCampaignReportEntry(CampaignAuctionReportKey campaignReportKey) {
        return (CampaignAuctionReportEntry)this.getEntry(campaignReportKey);
    }

    @Override
    protected void readBeforeEntries(TransportReader reader) throws ParseException {
        this.campaignID = reader.getAttributeAsInt(CAMPAIGN_ID_KEY);
        this.winner = reader.getAttribute(WINNER_KEY);
    }

    @Override
    protected void writeBeforeEntries(TransportWriter writer) {
        writer.attr(CAMPAIGN_ID_KEY, this.campaignID);
        writer.attr(WINNER_KEY, this.winner);
    }
}

