/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.publishers.generators;

import java.util.Collection;
import tau.tac.adx.generators.GenericGenerator;
import tau.tac.adx.publishers.AdxPublisher;

public interface AdxPublisherGenerator
extends GenericGenerator<AdxPublisher> {
    @Override
    public Collection<AdxPublisher> generate(int var1);
}

