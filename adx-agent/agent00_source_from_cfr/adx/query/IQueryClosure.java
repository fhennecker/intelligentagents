/*
 * Decompiled with CFR 0_110.
 */
package adx.query;

import adx.query.IQueryExec;
import adx.query.IQueryLValue;

public interface IQueryClosure
extends IQueryExec {
    public IQueryLValue<IQueryClosure> and();

    public IQueryLValue<IQueryClosure> or();
}

