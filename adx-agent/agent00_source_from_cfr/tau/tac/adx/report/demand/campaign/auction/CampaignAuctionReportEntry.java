/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.report.demand.campaign.auction;

import edu.umich.eecs.tac.props.AbstractTransportableEntry;
import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import tau.tac.adx.report.demand.campaign.auction.CampaignAuctionReportKey;

public class CampaignAuctionReportEntry
extends AbstractTransportableEntry<CampaignAuctionReportKey> {
    private static final long serialVersionUID = 2856461805359063646L;
    private static final String ACTUAL_BID_KEY = "ACTUAL_BID_KEY";
    private static final String EFFECTIVE_BID_KEY = "EFFECTIVE_BID_KEY";
    private double actualBid;
    private double effectiveBid;

    public CampaignAuctionReportEntry() {
    }

    public CampaignAuctionReportEntry(CampaignAuctionReportKey key) {
        this.setKey(key);
    }

    public CampaignAuctionReportEntry(double actualBid, double effctiveBid) {
        this.actualBid = actualBid;
        this.effectiveBid = effctiveBid;
    }

    public double getActualBid() {
        return this.actualBid;
    }

    public void setActualBid(double actualBid) {
        this.actualBid = actualBid;
    }

    public double getEffctiveBid() {
        return this.effectiveBid;
    }

    public void setEffctiveBid(double effctiveBid) {
        this.effectiveBid = effctiveBid;
    }

    @Override
    protected final void readEntry(TransportReader reader) throws ParseException {
        this.actualBid = reader.getAttributeAsDouble("ACTUAL_BID_KEY");
        this.effectiveBid = reader.getAttributeAsDouble("EFFECTIVE_BID_KEY");
    }

    @Override
    protected final void writeEntry(TransportWriter writer) {
        writer.attr("ACTUAL_BID_KEY", this.actualBid);
        writer.attr("EFFECTIVE_BID_KEY", this.effectiveBid);
    }

    @Override
    protected String keyNodeName() {
        return CampaignAuctionReportKey.class.getName();
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        long temp = Double.doubleToLongBits(this.actualBid);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.effectiveBid);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        CampaignAuctionReportEntry other = (CampaignAuctionReportEntry)obj;
        if (Double.doubleToLongBits(this.actualBid) != Double.doubleToLongBits(other.actualBid)) {
            return false;
        }
        if (Double.doubleToLongBits(this.effectiveBid) != Double.doubleToLongBits(other.effectiveBid)) {
            return false;
        }
        return true;
    }

    public String toString() {
        return "CampaignAuctionReportEntry [actualBid=" + this.actualBid + ", effectiveBid=" + this.effectiveBid + "]";
    }
}

