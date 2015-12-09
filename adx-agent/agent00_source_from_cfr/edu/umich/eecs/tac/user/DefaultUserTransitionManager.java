/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.user.QueryState;
import edu.umich.eecs.tac.user.User;
import edu.umich.eecs.tac.user.UserTransitionManager;
import edu.umich.eecs.tac.util.sampling.MutableSampler;
import edu.umich.eecs.tac.util.sampling.WheelSampler;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

public class DefaultUserTransitionManager
implements UserTransitionManager {
    protected Logger log = Logger.getLogger(DefaultUserTransitionManager.class.getName());
    private Map<QueryState, MutableSampler<QueryState>> standardSamplers;
    private Map<QueryState, MutableSampler<QueryState>> burstSamplers;
    private double burstProbability;
    private double successiveBurstProbability;
    private int burstEffectLength;
    private boolean[] bursts;
    private int[] burstEffectCounter;
    private Random random;
    private RetailCatalog retailCatalog;

    public DefaultUserTransitionManager(RetailCatalog retailCatalog) {
        this(retailCatalog, new Random());
    }

    public DefaultUserTransitionManager(RetailCatalog retailCatalog, Random random) {
        if (random == null) {
            throw new NullPointerException("Random number generator cannot be null");
        }
        if (retailCatalog == null) {
            throw new NullPointerException("retail catalog cannot be null");
        }
        this.standardSamplers = new HashMap<QueryState, MutableSampler<QueryState>>(QueryState.values().length);
        this.burstSamplers = new HashMap<QueryState, MutableSampler<QueryState>>(QueryState.values().length);
        this.random = random;
        this.retailCatalog = retailCatalog;
        this.bursts = new boolean[retailCatalog.size()];
        this.burstEffectCounter = new int[retailCatalog.size()];
        int i = 0;
        while (i < this.burstEffectCounter.length) {
            this.burstEffectCounter[i] = 0;
            ++i;
        }
        this.updateBurst();
    }

    @Override
    public void nextTimeUnit(int timeUnit) {
        this.updateBurst();
    }

    public void addStandardTransitionProbability(QueryState from, QueryState to, double probability) {
        MutableSampler<QueryState> sampler = this.standardSamplers.get((Object)from);
        if (sampler == null) {
            sampler = new WheelSampler<QueryState>(this.random);
            this.standardSamplers.put(from, sampler);
        }
        sampler.addState(probability, to);
    }

    public void addBurstTransitionProbability(QueryState from, QueryState to, double probability) {
        MutableSampler<QueryState> sampler = this.burstSamplers.get((Object)from);
        if (sampler == null) {
            sampler = new WheelSampler<QueryState>(this.random);
            this.burstSamplers.put(from, sampler);
        }
        sampler.addState(probability, to);
    }

    public double getBurstProbability() {
        return this.burstProbability;
    }

    public void setBurstProbability(double burstProbability, double successiveBurstProbability, int burstEffectLength) {
        this.burstProbability = burstProbability;
        this.successiveBurstProbability = successiveBurstProbability;
        this.burstEffectLength = burstEffectLength;
    }

    @Override
    public QueryState transition(User user, boolean transacted) {
        if (transacted) {
            return QueryState.TRANSACTED;
        }
        if (this.bursts[this.retailCatalog.indexForEntry(user.getProduct())]) {
            return this.burstSamplers.get((Object)user.getState()).getSample();
        }
        return this.standardSamplers.get((Object)user.getState()).getSample();
    }

    private void updateBurst() {
        int i = 0;
        while (i < this.bursts.length) {
            if (this.burstEffectCounter[i] > 0) {
                this.bursts[i] = this.random.nextDouble() < this.successiveBurstProbability;
            } else {
                boolean bl = this.bursts[i] = this.random.nextDouble() < this.burstProbability;
            }
            this.burstEffectCounter[i] = this.bursts[i] ? this.burstEffectLength : (this.burstEffectCounter[i] > 0 ? this.burstEffectCounter[i] - 1 : this.burstEffectCounter[i]);
            ++i;
        }
    }

    public boolean isBurst(Product product) {
        return this.bursts[this.retailCatalog.indexForEntry(product)];
    }
}

