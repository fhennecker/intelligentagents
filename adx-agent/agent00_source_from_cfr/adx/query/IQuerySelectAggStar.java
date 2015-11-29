/*
 * Decompiled with CFR 0_110.
 */
package adx.query;

import adx.query.IQueryAgg;
import adx.query.IQueryClosureAgg;
import adx.query.IQueryExec;
import adx.query.IQueryGroup;
import adx.query.IQuerySelectAgg;
import adx.query.IQueryWhere;

public interface IQuerySelectAggStar
extends IQueryAgg,
IQueryWhere<IQueryClosureAgg>,
IQueryGroup,
IQueryExec {
    public IQuerySelectAgg propery(String var1);

    public IQuerySelectAgg index(int var1);
}

