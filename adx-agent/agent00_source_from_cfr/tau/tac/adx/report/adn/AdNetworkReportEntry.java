/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.report.adn;

import edu.umich.eecs.tac.props.AbstractTransportableEntry;
import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;
import tau.tac.adx.auction.AdxAuctionResult;
import tau.tac.adx.report.adn.AdNetworkKey;

public class AdNetworkReportEntry
extends AbstractTransportableEntry<AdNetworkKey> {
    private static final long serialVersionUID = 5614233336418249331L;
    private static final String BID_COUNT = "BID_COUNT";
    private static final String WIN_COUNT = "WIN_COUNT";
    private static final String COST_COUNT = "COST_COUNT";
    private static final String KEY_NODE_TRANSPORT_NAME = "KEY_TRANSPORT_NAME";
    private int bidCount;
    private int winCount;
    private double cost;

    public AdNetworkReportEntry(AdNetworkKey key) {
        this.setKey(key);
    }

    public AdNetworkReportEntry() {
    }

    public int getBidCount() {
        return this.bidCount;
    }

    public void setBidCount(int bidCount) {
        this.bidCount = bidCount;
    }

    public int getWinCount() {
        return this.winCount;
    }

    public void setWinCount(int winCount) {
        this.winCount = winCount;
    }

    public double getCost() {
        return this.cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    protected final void readEntry(TransportReader reader) throws ParseException {
        this.bidCount = reader.getAttributeAsInt("BID_COUNT");
        this.winCount = reader.getAttributeAsInt("WIN_COUNT");
        this.cost = reader.getAttributeAsDouble("COST_COUNT");
        reader.nextNode(AdNetworkKey.class.getSimpleName(), true);
        this.setKey((AdNetworkKey)reader.readTransportable());
    }

    @Override
    protected final void writeEntry(TransportWriter writer) {
        writer.attr("BID_COUNT", this.bidCount);
        writer.attr("WIN_COUNT", this.winCount);
        writer.attr("COST_COUNT", this.cost);
        writer.write((Transportable)this.getKey());
    }

    @Override
    protected String keyNodeName() {
        return AdNetworkKey.class.getName();
    }

    public void addAuctionResult(AdxAuctionResult auctionResult, boolean hasWon) {
        if (hasWon) {
            ++this.winCount;
            this.cost += auctionResult.getWinningPrice().doubleValue();
        }
        ++this.bidCount;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.bidCount;
        long temp = Double.doubleToLongBits(this.cost);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        result = 31 * result + this.winCount;
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
        AdNetworkReportEntry other = (AdNetworkReportEntry)obj;
        if (this.bidCount != other.bidCount) {
            return false;
        }
        if (Double.doubleToLongBits(this.cost) != Double.doubleToLongBits(other.cost)) {
            return false;
        }
        if (this.winCount != other.winCount) {
            return false;
        }
        return true;
    }

    public String toString() {
        return "AdNetworkReportEntry " + this.getKey() + "[bidCount=" + this.bidCount + ", winCount=" + this.winCount + ", cost=" + this.cost + "]";
    }
}

