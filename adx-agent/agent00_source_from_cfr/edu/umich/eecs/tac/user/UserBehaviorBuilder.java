/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.sim.AgentRepository;
import edu.umich.eecs.tac.util.config.ConfigProxy;
import java.util.Random;

public interface UserBehaviorBuilder<T> {
    public T build(ConfigProxy var1, AgentRepository var2, Random var3);
}

