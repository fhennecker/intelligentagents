/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 */
package tau.tac.adx.messages;

import com.google.common.collect.BiMap;
import tau.tac.adx.messages.AdxMessage;

public class ClassificationStatusMessage
implements AdxMessage {
    private final BiMap<String, Integer> adNetworkClassification;

    public ClassificationStatusMessage(BiMap<String, Integer> adNetworkClassification) {
        this.adNetworkClassification = adNetworkClassification;
    }

    public BiMap<String, Integer> getAdNetworkClassification() {
        return this.adNetworkClassification;
    }
}

