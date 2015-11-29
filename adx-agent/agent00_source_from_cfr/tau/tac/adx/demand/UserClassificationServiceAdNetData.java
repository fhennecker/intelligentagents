/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.demand;

public class UserClassificationServiceAdNetData {
    int effectiveDay;
    double serviceLevel;
    double price;
    double bid;
    int daySubmitted;

    public UserClassificationServiceAdNetData(int effectiveDay, double serviceLevel, double price, double bid, int daySubmitted) {
        this.effectiveDay = effectiveDay;
        this.serviceLevel = serviceLevel;
        this.price = price;
        this.bid = bid;
        this.daySubmitted = daySubmitted;
    }

    public UserClassificationServiceAdNetData() {
    }

    public UserClassificationServiceAdNetData clone() {
        return new UserClassificationServiceAdNetData(this.effectiveDay, this.serviceLevel, this.price, this.bid, this.daySubmitted);
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

    public void setBid(double bid, int day) {
        this.bid = bid;
        this.daySubmitted = day;
    }

    public void setAuctionResult(double price, double serviceLevel, int day) {
        this.price = price;
        this.serviceLevel = serviceLevel;
        this.effectiveDay = day;
    }

    public String logToString() {
        return "[efday=" + this.effectiveDay + ", level=" + this.serviceLevel + ", price=" + this.price + "] ";
    }
}

