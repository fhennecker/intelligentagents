/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.sim;

import edu.umich.eecs.tac.props.PublisherInfo;

public interface PublisherInfoSender {
    public void sendPublisherInfoToAll();

    public PublisherInfo getPublisherInfo();

    public void sendPublisherInfo(String var1);
}

