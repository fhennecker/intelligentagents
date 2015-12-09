/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.messages;

public class Contract {
    private final String adNetowrk;
    private final int contractId;
    private final int contractLength;
    private final int daysLeft;

    public Contract(String adNetowrk, int contractId, int contractLength, int daysLeft) {
        this.adNetowrk = adNetowrk;
        this.contractId = contractId;
        this.contractLength = contractLength;
        this.daysLeft = daysLeft;
    }

    public String getAdNetowrk() {
        return this.adNetowrk;
    }

    public int getContractId() {
        return this.contractId;
    }

    public int getContractLength() {
        return this.contractLength;
    }

    public int getDaysLeft() {
        return this.daysLeft;
    }
}

