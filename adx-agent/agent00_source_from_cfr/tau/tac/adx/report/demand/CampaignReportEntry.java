/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.report.demand;

import edu.umich.eecs.tac.props.AbstractTransportableEntry;
import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;
import tau.tac.adx.demand.CampaignStats;
import tau.tac.adx.report.adn.AdNetworkKey;
import tau.tac.adx.report.demand.CampaignReportKey;

public class CampaignReportEntry
extends AbstractTransportableEntry<CampaignReportKey> {
    private static final long serialVersionUID = 2856461805359063656L;
    private static final String TGT_IMPS = "TGT_IMPS";
    private static final String NTG_IMPS = "NTG_IMPS";
    private static final String COST = "COST";
    private CampaignStats stats = new CampaignStats(0.0, 0.0, 0.0);

    public CampaignReportEntry(CampaignReportKey key) {
        this.setKey(key);
    }

    public CampaignReportEntry() {
    }

    public CampaignStats getCampaignStats() {
        return this.stats;
    }

    public void setCampaignStats(CampaignStats other) {
        this.stats.setValues(other);
    }

    @Override
    protected final void readEntry(TransportReader reader) throws ParseException {
        this.stats = new CampaignStats(reader.getAttributeAsDouble("TGT_IMPS"), reader.getAttributeAsDouble("NTG_IMPS"), reader.getAttributeAsDouble("COST"));
        reader.nextNode(CampaignReportKey.class.getCanonicalName(), true);
        this.setKey((CampaignReportKey)reader.readTransportable());
    }

    @Override
    protected final void writeEntry(TransportWriter writer) {
        writer.attr("TGT_IMPS", this.stats.getTargetedImps());
        writer.attr("NTG_IMPS", this.stats.getOtherImps());
        writer.attr("COST", this.stats.getCost());
        writer.write((Transportable)this.getKey());
    }

    @Override
    protected String keyNodeName() {
        return AdNetworkKey.class.getName();
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
        CampaignReportEntry other = (CampaignReportEntry)obj;
        if (this.stats.getTargetedImps() != other.stats.getTargetedImps()) {
            return false;
        }
        if (this.stats.getOtherImps() != other.stats.getOtherImps()) {
            return false;
        }
        if (this.stats.getCost() != other.stats.getCost()) {
            return false;
        }
        return true;
    }

    public String toString() {
        return "CampaignReportEntry [tgt imps=" + this.stats.getTargetedImps() + ", ntg imps=" + this.stats.getOtherImps() + ", cost=" + this.stats.getCost() + "]";
    }
}

