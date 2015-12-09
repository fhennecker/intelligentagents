/*
 * Decompiled with CFR 0_110.
 */
package adx.query;

import adx.query.IQueryAgg;
import adx.query.IQueryClosure;
import adx.query.IQueryExec;
import adx.query.IQuerySelect;
import adx.query.IQueryWhere;

public interface IQuerySelectStar
extends IQuerySelect,
IQueryWhere<IQueryClosure>,
IQueryAgg,
IQueryExec {
}

