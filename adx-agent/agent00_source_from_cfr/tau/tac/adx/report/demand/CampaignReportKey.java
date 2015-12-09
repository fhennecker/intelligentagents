/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.report.demand;

import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

public class CampaignReportKey
implements Transportable {
    private static final String CAMPAIGN_ID_KEY = "CAMPAIGN_ID_KEY";
    private Integer campaignId;

    public CampaignReportKey(Integer id) {
        this.campaignId = id;
    }

    public CampaignReportKey() {
    }

    public Integer getCampaignId() {
        return this.campaignId;
    }

    public void setCampaignId(Integer id) {
        this.campaignId = id;
    }

    @Override
    public String getTransportName() {
        return this.getClass().getName();
    }

    @Override
    public void read(TransportReader reader) throws ParseException {
        this.campaignId = Integer.valueOf(reader.getAttribute("CAMPAIGN_ID_KEY", null));
    }

    @Override
    public void write(TransportWriter writer) {
        if (this.campaignId != null) {
            writer.attr("CAMPAIGN_ID_KEY", this.campaignId.toString());
        }
    }

    public String toString() {
        return "CampaignReportKey [campaignId=" + this.campaignId + "]";
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.campaignId == null ? 0 : this.campaignId.hashCode());
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
        CampaignReportKey other = (CampaignReportKey)obj;
        if (this.campaignId == null ? other.campaignId != null : !this.campaignId.equals(other.campaignId)) {
            return false;
        }
        return true;
    }
}

