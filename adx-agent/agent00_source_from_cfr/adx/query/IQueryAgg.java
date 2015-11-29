/*
 * Decompiled with CFR 0_110.
 */
package adx.query;

import adx.query.IQuerySelectAgg;

public interface IQueryAgg {
    public IQuerySelectAgg first();

    public IQuerySelectAgg last();

    public IQuerySelectAgg count();

    public IQuerySelectAgg sum();

    public IQuerySelectAgg min();

    public IQuerySelectAgg max();
}

