/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.report.demand;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.tasim.props.SimpleContent;
import tau.tac.adx.demand.Campaign;
import tau.tac.adx.report.adn.MarketSegment;

public class CampaignOpportunityMessage
extends SimpleContent {
    private static final long serialVersionUID = -2501549665857756943L;
    private static final String MARKET_SEGMENT_KEY = "USER_KEY";
    private int id;
    private Long reachImps;
    private int dayStart;
    private int dayEnd;
    private Set<MarketSegment> targetSegment = new HashSet<MarketSegment>();
    private double videoCoef;
    private double mobileCoef;
    private int day;

    public CampaignOpportunityMessage() {
    }

    public CampaignOpportunityMessage(Campaign campaign, int day) {
        this.id = campaign.getId();
        this.reachImps = campaign.getReachImps();
        this.dayStart = campaign.getDayStart();
        this.dayEnd = campaign.getDayEnd();
        this.targetSegment = campaign.getTargetSegment();
        this.videoCoef = campaign.getVideoCoef();
        this.mobileCoef = campaign.getMobileCoef();
        this.day = day;
    }

    public CampaignOpportunityMessage(int id, Long reachImps, int dayStart, int dayEnd, Set<MarketSegment> targetSegment, double videoCoef, double mobileCoef, int day) {
        this.id = id;
        this.reachImps = reachImps;
        this.dayStart = dayStart;
        this.dayEnd = dayEnd;
        this.targetSegment = targetSegment;
        this.videoCoef = videoCoef;
        this.mobileCoef = mobileCoef;
        this.day = day;
    }

    public int getId() {
        return this.id;
    }

    public Long getReachImps() {
        return this.reachImps;
    }

    public long getDayStart() {
        return this.dayStart;
    }

    public long getDayEnd() {
        return this.dayEnd;
    }

    public Set<MarketSegment> getTargetSegment() {
        return this.targetSegment;
    }

    public double getVideoCoef() {
        return this.videoCoef;
    }

    public double getMobileCoef() {
        return this.mobileCoef;
    }

    public int getDay() {
        return this.day;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer().append(this.getTransportName()).append('[').append(this.id).append(',').append(this.reachImps).append(',').append(this.dayStart).append(',').append(this.dayEnd).append(',').append(this.targetSegment).append(',').append(this.videoCoef).append(',').append(this.mobileCoef).append(',').append(this.day).append(',');
        return this.params(buf).append(']').toString();
    }

    @Override
    public String getTransportName() {
        return this.getClass().getName();
    }

    @Override
    public void read(TransportReader reader) throws ParseException {
        if (this.isLocked()) {
            throw new IllegalStateException("locked");
        }
        this.id = reader.getAttributeAsInt("id");
        this.reachImps = reader.getAttributeAsLong("reachImps");
        this.dayStart = reader.getAttributeAsInt("dayStart");
        this.dayEnd = reader.getAttributeAsInt("dayEnd");
        this.videoCoef = reader.getAttributeAsDouble("videoCoef");
        this.mobileCoef = reader.getAttributeAsDouble("mobileCoef");
        this.day = reader.getAttributeAsInt("day");
        while (reader.nextNode("USER_KEY", false)) {
            this.targetSegment.add(MarketSegment.valueOf(reader.getAttribute("USER_KEY")));
        }
        super.read(reader);
    }

    @Override
    public void write(TransportWriter writer) {
        writer.attr("id", this.id).attr("reachImps", this.reachImps).attr("dayStart", this.dayStart).attr("dayEnd", this.dayEnd).attr("videoCoef", this.videoCoef).attr("mobileCoef", this.mobileCoef).attr("day", this.day);
        for (MarketSegment marketSegment : this.targetSegment) {
            writer.node("USER_KEY").attr("USER_KEY", marketSegment.toString()).endNode("USER_KEY");
        }
        super.write(writer);
    }
}

