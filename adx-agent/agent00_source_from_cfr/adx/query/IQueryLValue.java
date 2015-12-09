/*
 * Decompiled with CFR 0_110.
 */
package adx.query;

import adx.query.IQueryOp;

public interface IQueryLValue<T> {
    public IQueryOp<T> property(String var1);

    public IQueryOp<T> index(int var1);

    public IQueryOp<T> element();
}

