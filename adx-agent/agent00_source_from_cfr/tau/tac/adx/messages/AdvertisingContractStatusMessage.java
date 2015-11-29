/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.messages;

import tau.tac.adx.messages.AdxMessage;
import tau.tac.adx.messages.Contract;

public class AdvertisingContractStatusMessage
implements AdxMessage {
    private final Contract contract;

    public AdvertisingContractStatusMessage(Contract contract) {
        this.contract = contract;
    }

    public Contract getContract() {
        return this.contract;
    }
}

