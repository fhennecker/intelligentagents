/*
 * Decompiled with CFR 0_110.
 */
package adx.query;

import adx.query.IQueryClosureAgg;
import adx.query.IQueryExec;
import adx.query.IQueryGroup;
import adx.query.IQuerySelectAggStar;
import adx.query.IQueryWhere;

public interface IQuerySelectAgg
extends IQueryWhere<IQueryClosureAgg>,
IQueryGroup,
IQueryExec {
    public IQuerySelectAggStar propery(String var1);

    public IQuerySelectAggStar index(int var1);
}

