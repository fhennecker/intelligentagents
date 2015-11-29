/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.messages;

import tau.tac.adx.messages.AdxMessage;

public class CampaignLimitSet
implements AdxMessage {
    boolean isTotal;
    int campaignId;
    String AdNetwork;
    double budgetLimit;
    private final int impressionLimit;

    public CampaignLimitSet(boolean isTotal, int campaignId, String adNet, int impressionLimit, double budgetLimit) {
        this.isTotal = isTotal;
        this.campaignId = campaignId;
        this.AdNetwork = adNet;
        this.budgetLimit = budgetLimit;
        this.impressionLimit = impressionLimit;
    }

    public boolean getIsTotal() {
        return this.isTotal;
    }

    public int getCampaignId() {
        return this.campaignId;
    }

    public String getAdNetwork() {
        return this.AdNetwork;
    }

    public double getBudgetLimit() {
        return this.budgetLimit;
    }

    public int getImpressionLimit() {
        return this.impressionLimit;
    }
}

