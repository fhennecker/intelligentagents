/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.ads.properties.generators;

import java.util.Collection;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.generators.GenericGenerator;

public interface AdTypeGenerator
extends GenericGenerator<AdType> {
    @Override
    public Collection<AdType> generate(int var1);
}

