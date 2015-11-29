/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.props.generators;

import java.util.Collection;
import tau.tac.adx.generators.GenericGenerator;
import tau.tac.adx.props.AdxQuery;

public interface AdxQueryGenerator
extends GenericGenerator<AdxQuery> {
    @Override
    public Collection<AdxQuery> generate(int var1);
}

