/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 */
package tau.tac.adx.props;

import com.google.inject.Inject;
import edu.umich.eecs.tac.props.AbstractTransportable;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import tau.tac.adx.Adx;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.devices.Device;
import tau.tac.adx.props.TacQuery;
import tau.tac.adx.publishers.AdxPublisher;
import tau.tac.adx.report.adn.MarketSegment;
import tau.tac.adx.users.AdxUser;

public class AdxQuery
extends AbstractTransportable
implements TacQuery<Adx> {
    private static final String AD_TYPE_KEY = "AD_TYPE_KEY";
    private static final String DEVICE_KEY = "DEVICE_KEY";
    private static final String PUBLISHER_KEY = "PUBLISHER_KEY";
    private static final String MARKET_SEGMENT_KEY = "USER_KEY";
    private static final long serialVersionUID = -7210442551464879289L;
    private String publisher;
    private Set<MarketSegment> marketSegments;
    private Device device;
    private AdType adType;

    public AdxQuery(String publisher, Set<MarketSegment> marketSegments, Device device, AdType adType) {
        this.publisher = publisher;
        this.marketSegments = marketSegments;
        this.device = device;
        this.adType = adType;
    }

    public AdxQuery(String publisher, MarketSegment marketSegment, Device device, AdType adType) {
        this.publisher = publisher;
        this.marketSegments = new HashSet<MarketSegment>();
        this.marketSegments.add(marketSegment);
        this.device = device;
        this.adType = adType;
    }

    public AdxQuery() {
        this.marketSegments = new HashSet<MarketSegment>();
    }

    @Inject
    public AdxQuery(AdxPublisher publisher, AdxUser user, Device device, AdType adType) {
        this.publisher = publisher.getName();
        this.marketSegments = MarketSegment.extractSegment(user);
        this.device = device;
        this.adType = adType;
    }

    public AdxQuery clone() {
        return new AdxQuery(this.publisher, this.marketSegments, this.device, this.adType);
    }

    public AdxQuery(String publisher, AdxUser user, Device device, AdType adType) {
        this.publisher = publisher;
        this.marketSegments = MarketSegment.extractSegment(user);
        this.device = device;
        this.adType = adType;
    }

    public String getPublisher() {
        return this.publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Device getDevice() {
        return this.device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public AdType getAdType() {
        return this.adType;
    }

    public void setAdType(AdType adType) {
        this.adType = adType;
    }

    public Set<MarketSegment> getMarketSegments() {
        return this.marketSegments;
    }

    public void setMarketSegments(Set<MarketSegment> marketSegments) {
        this.marketSegments = marketSegments;
    }

    @Override
    protected void readWithLock(TransportReader reader) throws ParseException {
        this.setAdType(AdType.valueOf(reader.getAttribute("AD_TYPE_KEY", null)));
        this.setDevice(Device.valueOf(reader.getAttribute("DEVICE_KEY", null)));
        this.setPublisher(reader.getAttribute("PUBLISHER_KEY", null));
        while (reader.nextNode("USER_KEY", false)) {
            this.marketSegments.add(MarketSegment.valueOf(reader.getAttribute("USER_KEY")));
        }
    }

    @Override
    protected void writeWithLock(TransportWriter writer) {
        if (this.getAdType() != null) {
            writer.attr("AD_TYPE_KEY", this.getAdType().name());
        }
        if (this.getDevice() != null) {
            writer.attr("DEVICE_KEY", this.getDevice().name());
        }
        if (this.getPublisher() != null) {
            writer.attr("PUBLISHER_KEY", this.getPublisher());
        }
        for (MarketSegment marketSegment : this.marketSegments) {
            writer.node("USER_KEY").attr("USER_KEY", marketSegment.toString()).endNode("USER_KEY");
        }
    }

    public String toString() {
        return "AdxQuery [publisher=" + this.publisher + ", marketSegments=" + this.marketSegments + ", device=" + (Object)((Object)this.device) + ", adType=" + (Object)((Object)this.adType) + "]";
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.adType == null ? 0 : this.adType.hashCode());
        result = 31 * result + (this.device == null ? 0 : this.device.hashCode());
        result = 31 * result + (this.marketSegments == null ? 0 : this.marketSegments.hashCode());
        result = 31 * result + (this.publisher == null ? 0 : this.publisher.hashCode());
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
        AdxQuery other = (AdxQuery)obj;
        if (this.adType != other.adType) {
            return false;
        }
        if (this.device != other.device) {
            return false;
        }
        if (this.marketSegments == null ? other.marketSegments != null : !this.marketSegments.equals(other.marketSegments)) {
            return false;
        }
        if (this.publisher == null ? other.publisher != null : !this.publisher.equals(other.publisher)) {
            return false;
        }
        return true;
    }
}

