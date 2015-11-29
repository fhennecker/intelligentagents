/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.users.generators;

import java.util.Collection;
import tau.tac.adx.generators.GenericGenerator;
import tau.tac.adx.users.AdxUser;

public interface AdxUserGenerator
extends GenericGenerator<AdxUser> {
    @Override
    public Collection<AdxUser> generate(int var1);
}

