/*
 * Decompiled with CFR 0_110.
 */
package adx.query;

import adx.query.IQueryRValue;

public interface IQueryOp<T> {
    public IQueryRValue<T> eq();

    public IQueryRValue<T> neq();

    public IQueryRValue<T> lt();

    public IQueryRValue<T> lte();

    public IQueryRValue<T> gt();

    public IQueryRValue<T> gte();

    public IQueryRValue<T> in();

    public IQueryRValue<T> contains();
}

