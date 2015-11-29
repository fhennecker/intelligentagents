/*
 * Decompiled with CFR 0_110.
 */
package adx.query;

import adx.query.IQueryExec;
import adx.query.IQueryGroup;
import adx.query.IQueryLValue;

public interface IQueryClosureAgg
extends IQueryGroup,
IQueryExec {
    public IQueryLValue<IQueryClosureAgg> and();

    public IQueryLValue<IQueryClosureAgg> or();
}

