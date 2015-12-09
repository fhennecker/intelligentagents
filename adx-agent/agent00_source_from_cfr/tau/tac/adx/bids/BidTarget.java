/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.bids;

import tau.tac.adx.publishers.AdxPublisher;
import tau.tac.adx.users.AdxUser;

public class BidTarget {
    private final AdxUser adxUser;
    private final AdxPublisher adxPublisher;

    public BidTarget(AdxUser adxUser, AdxPublisher adxPublisher) {
        this.adxUser = adxUser;
        this.adxPublisher = adxPublisher;
    }

    public AdxUser getAdxUser() {
        return this.adxUser;
    }

    public AdxPublisher getAdxPublisher() {
        return this.adxPublisher;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.adxPublisher == null ? 0 : this.adxPublisher.hashCode());
        result = 31 * result + (this.adxUser == null ? 0 : this.adxUser.hashCode());
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
        BidTarget other = (BidTarget)obj;
        if (this.adxPublisher == null ? other.adxPublisher != null : !this.adxPublisher.equals(other.adxPublisher)) {
            return false;
        }
        if (this.adxUser == null ? other.adxUser != null : !this.adxUser.equals(other.adxUser)) {
            return false;
        }
        return true;
    }
}

