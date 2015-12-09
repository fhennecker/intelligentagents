/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.users;

import edu.umich.eecs.tac.util.config.ConfigProxy;
import java.util.Random;
import tau.tac.adx.sim.AdxAgentRepository;

public interface AdxUserBehaviorBuilder<T> {
    public T build(ConfigProxy var1, AdxAgentRepository var2, Random var3);
}

