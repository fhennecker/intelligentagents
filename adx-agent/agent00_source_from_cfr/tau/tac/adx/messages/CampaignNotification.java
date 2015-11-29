/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.messages;

import tau.tac.adx.demand.Campaign;
import tau.tac.adx.messages.AdxMessage;

public class CampaignNotification
implements AdxMessage {
    Campaign campaign;

    public CampaignNotification(Campaign campaign) {
        this.campaign = campaign;
    }

    public Campaign getCampaign() {
        return this.campaign;
    }
}

