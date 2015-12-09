/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.report.demand.campaign.auction;

import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

public class CampaignAuctionReportKey
implements Transportable {
    private static final String AD_NET_NAME_KEY = "AD_NET_NAME_KEY";
    private String adnetName;

    public CampaignAuctionReportKey(String adnetName) {
        this.adnetName = adnetName;
    }

    public CampaignAuctionReportKey() {
    }

    public String getAdnetName() {
        return this.adnetName;
    }

    public void setAdnetName(String adnetName) {
        this.adnetName = adnetName;
    }

    @Override
    public String getTransportName() {
        return this.getClass().getName();
    }

    @Override
    public void read(TransportReader reader) throws ParseException {
        this.adnetName = reader.getAttribute("AD_NET_NAME_KEY", null);
    }

    @Override
    public void write(TransportWriter writer) {
        writer.attr("AD_NET_NAME_KEY", this.adnetName);
    }

    public String toString() {
        return "CampaignAuctionReportKey [adnetName=" + this.adnetName + "]";
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.adnetName == null ? 0 : this.adnetName.hashCode());
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
        CampaignAuctionReportKey other = (CampaignAuctionReportKey)obj;
        if (this.adnetName == null ? other.adnetName != null : !this.adnetName.equals(other.adnetName)) {
            return false;
        }
        return true;
    }
}

