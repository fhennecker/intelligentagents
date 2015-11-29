/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.user.QueryState;
import edu.umich.eecs.tac.user.User;
import edu.umich.eecs.tac.user.UserQueryManager;
import edu.umich.eecs.tac.util.sampling.Sampler;
import edu.umich.eecs.tac.util.sampling.WheelSampler;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DefaultUserQueryManager
implements UserQueryManager {
    private Map<User, Sampler<Query>> querySamplers;

    public DefaultUserQueryManager(RetailCatalog catalog) {
        this(catalog, new Random());
    }

    public DefaultUserQueryManager(RetailCatalog catalog, Random random) {
        if (catalog == null) {
            throw new NullPointerException("Retail catalog cannot be null");
        }
        if (random == null) {
            throw new NullPointerException("Random number generator cannot be null");
        }
        this.querySamplers = this.buildQuerySamplers(catalog, random);
    }

    @Override
    public Query generateQuery(User user) {
        if (user.isSearching()) {
            return this.querySamplers.get(user).getSample();
        }
        return null;
    }

    @Override
    public void nextTimeUnit(int timeUnit) {
    }

    private Map<User, Sampler<Query>> buildQuerySamplers(RetailCatalog catalog, Random random) {
        HashMap<User, Sampler<Query>> map = new HashMap<User, Sampler<Query>>(catalog.size() * 4);
        for (Product product : catalog) {
            Query f0 = new Query(null, null);
            Query f1_manufacturer = new Query(product.getManufacturer(), null);
            Query f1_component = new Query(null, product.getComponent());
            Query f2 = new Query(product.getManufacturer(), product.getComponent());
            User isUser = new User();
            isUser.setProduct(product);
            isUser.setState(QueryState.INFORMATIONAL_SEARCH);
            User f0User = new User();
            f0User.setProduct(product);
            f0User.setState(QueryState.FOCUS_LEVEL_ZERO);
            User f1User = new User();
            f1User.setProduct(product);
            f1User.setState(QueryState.FOCUS_LEVEL_ONE);
            User f2User = new User();
            f2User.setProduct(product);
            f2User.setState(QueryState.FOCUS_LEVEL_TWO);
            WheelSampler<Query> isSampler = new WheelSampler<Query>(random);
            isSampler.addState(1.0, f0);
            isSampler.addState(0.5, f1_component);
            isSampler.addState(0.5, f1_manufacturer);
            isSampler.addState(1.0, f2);
            WheelSampler<Query> f0Sampler = new WheelSampler<Query>(random);
            f0Sampler.addState(1.0, f0);
            WheelSampler<Query> f1Sampler = new WheelSampler<Query>(random);
            f1Sampler.addState(1.0, f1_component);
            f1Sampler.addState(1.0, f1_manufacturer);
            WheelSampler<Query> f2Sampler = new WheelSampler<Query>(random);
            f2Sampler.addState(1.0, f2);
            map.put(isUser, isSampler);
            map.put(f0User, f0Sampler);
            map.put(f1User, f1Sampler);
            map.put(f2User, f2Sampler);
        }
        return map;
    }
}

