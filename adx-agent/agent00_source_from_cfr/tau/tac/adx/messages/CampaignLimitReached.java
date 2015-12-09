/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.messages;

import tau.tac.adx.messages.AdxMessage;

public class CampaignLimitReached
implements AdxMessage {
    int campaignId;
    String AdNetwork;

    public CampaignLimitReached(int campaignId, String adNet) {
        this.campaignId = campaignId;
        this.AdNetwork = adNet;
    }

    public int getCampaignId() {
        return this.campaignId;
    }

    public String getAdNetwork() {
        return this.AdNetwork;
    }

    public String toString() {
        return "CampaignLimitReached [campaignId=" + this.campaignId + ", AdNetwork=" + this.AdNetwork + "]";
    }
}

