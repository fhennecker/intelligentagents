/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.report.demand;

import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.tasim.props.SimpleContent;
import tau.tac.adx.demand.Campaign;
import tau.tac.adx.demand.UserClassificationServiceAdNetData;

public class AdNetworkDailyNotification
extends SimpleContent {
    private static final long serialVersionUID = -2893212570481112391L;
    int effectiveDay;
    double serviceLevel;
    double price;
    double qualityScore;
    int campaignId;
    String winner;
    long costMillis;

    public AdNetworkDailyNotification() {
    }

    public AdNetworkDailyNotification(int effectiveDay, double serviceLevel, double price, double qualityScore, int campaignId, String winner, long costMillis) {
        this.effectiveDay = effectiveDay;
        this.serviceLevel = serviceLevel;
        this.price = price;
        this.qualityScore = qualityScore;
        this.campaignId = campaignId;
        this.winner = winner;
        this.costMillis = costMillis;
    }

    public AdNetworkDailyNotification(UserClassificationServiceAdNetData ucsData, Campaign campaign, double qualityScore) {
        this.qualityScore = qualityScore;
        if (ucsData != null) {
            this.effectiveDay = ucsData.getEffectiveDay();
            this.serviceLevel = ucsData.getServiceLevel();
            this.price = ucsData.getPrice();
        } else {
            this.effectiveDay = 0;
        }
        if (campaign != null) {
            this.campaignId = campaign.getId();
            this.winner = campaign.getAdvertiser();
            this.costMillis = campaign.getBudgetMillis();
        } else {
            this.campaignId = 0;
            this.winner = "NONE";
            this.costMillis = 0;
        }
    }

    public int getEffectiveDay() {
        return this.effectiveDay;
    }

    public double getServiceLevel() {
        return this.serviceLevel;
    }

    public double getPrice() {
        return this.price;
    }

    public double getQualityScore() {
        return this.qualityScore;
    }

    public int getCampaignId() {
        return this.campaignId;
    }

    public String getWinner() {
        return this.winner;
    }

    public long getCostMillis() {
        return this.costMillis;
    }

    public void zeroCost() {
        this.costMillis = 0;
    }

    @Override
    public void read(TransportReader reader) throws ParseException {
        if (this.isLocked()) {
            throw new IllegalStateException("locked");
        }
        this.effectiveDay = reader.getAttributeAsInt("effectiveDay");
        this.serviceLevel = reader.getAttributeAsDouble("serviceLevel");
        this.price = reader.getAttributeAsDouble("price");
        this.qualityScore = reader.getAttributeAsDouble("qualityScore");
        this.campaignId = reader.getAttributeAsInt("campaignId");
        this.winner = reader.getAttribute("winner");
        this.costMillis = reader.getAttributeAsLong("costMillis");
        super.read(reader);
    }

    @Override
    public void write(TransportWriter writer) {
        writer.attr("effectiveDay", this.effectiveDay).attr("serviceLevel", this.serviceLevel).attr("price", this.price).attr("qualityScore", this.qualityScore).attr("campaignId", this.campaignId).attr("winner", this.winner).attr("costMillis", this.costMillis);
        super.write(writer);
    }

    @Override
    public String getTransportName() {
        return this.getClass().getName();
    }

    public String toString() {
        return "AdNetworkDailyNotification [effectiveDay=" + this.effectiveDay + ", serviceLevel=" + this.serviceLevel + ", price=" + this.price + ", qualityScore=" + this.qualityScore + ", campaignId=" + this.campaignId + ", winner=" + this.winner + ", costMillis=" + this.costMillis + "]";
    }
}

