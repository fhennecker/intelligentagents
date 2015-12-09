/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.report.demand;

import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.tasim.props.SimpleContent;

public class AdNetBidMessage
extends SimpleContent {
    private static final long serialVersionUID = -4773426215378916401L;
    private double ucsBid;
    private int campaignId;
    private Long campaignBudgetBid;

    public AdNetBidMessage(double ucsBid, int campaignId, Long campaignBudgetBid) {
        this.ucsBid = ucsBid;
        this.campaignId = campaignId;
        this.campaignBudgetBid = campaignBudgetBid;
    }

    public AdNetBidMessage(int id, Long budget) {
        this.campaignId = id;
        this.campaignBudgetBid = budget;
    }

    public AdNetBidMessage() {
    }

    public double getUcsBid() {
        return this.ucsBid;
    }

    public Long getCampaignBudget() {
        return this.campaignBudgetBid;
    }

    public int getCampaignId() {
        return this.campaignId;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer().append(this.getTransportName()).append('[').append(this.ucsBid).append(',').append(this.campaignId).append(',').append(this.campaignBudgetBid).append(',');
        return buf.append(']').toString();
    }

    @Override
    public void read(TransportReader reader) throws ParseException {
        if (this.isLocked()) {
            throw new IllegalStateException("locked");
        }
        this.ucsBid = reader.getAttributeAsDouble("ucs");
        this.campaignId = reader.getAttributeAsInt("id");
        this.campaignBudgetBid = reader.getAttributeAsLong("budget");
        super.read(reader);
    }

    @Override
    public void write(TransportWriter writer) {
        writer.attr("ucs", this.ucsBid).attr("id", this.campaignId).attr("budget", this.campaignBudgetBid);
        super.write(writer);
    }

    @Override
    public String getTransportName() {
        return this.getClass().getName();
    }
}

