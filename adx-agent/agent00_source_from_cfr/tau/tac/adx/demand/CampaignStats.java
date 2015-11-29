/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.demand;

public class CampaignStats {
    double tartgetedImps;
    double otherImps;
    double cost;

    public CampaignStats(double timps, double oimps, double cost) {
        this.tartgetedImps = timps;
        this.otherImps = oimps;
        this.cost = cost;
    }

    public double getTargetedImps() {
        return this.tartgetedImps;
    }

    public double getOtherImps() {
        return this.otherImps;
    }

    public double getCost() {
        return this.cost;
    }

    public String toString() {
        return "CampaignStats [tartgetedImps=" + this.tartgetedImps + ", otherImps=" + this.otherImps + ", cost=" + this.cost + "]";
    }

    public void setValues(CampaignStats other) {
        this.tartgetedImps = other.tartgetedImps;
        this.otherImps = other.otherImps;
        this.cost = other.cost;
    }

    CampaignStats add(CampaignStats other) {
        if (other != null) {
            this.tartgetedImps += other.tartgetedImps;
            this.otherImps += other.otherImps;
            this.cost += other.cost;
        }
        return this;
    }
}

