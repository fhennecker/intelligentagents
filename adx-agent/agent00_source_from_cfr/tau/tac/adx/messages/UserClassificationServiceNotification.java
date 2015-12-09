/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.messages;

import tau.tac.adx.demand.UserClassificationService;
import tau.tac.adx.messages.AdxMessage;

public class UserClassificationServiceNotification
implements AdxMessage {
    UserClassificationService ucs;

    public UserClassificationServiceNotification(UserClassificationService ucs) {
        this.ucs = ucs;
    }

    public UserClassificationService getUserClassificationService() {
        return this.ucs;
    }
}

