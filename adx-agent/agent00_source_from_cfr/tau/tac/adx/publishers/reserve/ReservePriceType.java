/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.publishers.reserve;

import java.util.Set;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.report.adn.MarketSegment;

class ReservePriceType {
    private Set<MarketSegment> marketSegment;
    private AdType adType;

    public ReservePriceType(AdxQuery adxQuery) {
        this.marketSegment = adxQuery.getMarketSegments();
        this.adType = adxQuery.getAdType();
    }

    public String toString() {
        return "ReservePriceType [marketSegment=" + this.marketSegment + ", adType=" + (Object)((Object)this.adType) + "]";
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.adType == null ? 0 : this.adType.hashCode());
        result = 31 * result + (this.marketSegment == null ? 0 : this.marketSegment.hashCode());
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
        ReservePriceType other = (ReservePriceType)obj;
        if (this.adType != other.adType) {
            return false;
        }
        if (this.marketSegment == null ? other.marketSegment != null : !this.marketSegment.equals(other.marketSegment)) {
            return false;
        }
        return true;
    }
}

