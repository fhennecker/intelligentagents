/*
 * Decompiled with CFR 0_110.
 */
package adx.query;

import adx.query.IQuerySelectStar;

public interface IQuerySelect {
    public IQuerySelectStar property(String var1);

    public IQuerySelectStar index(int var1);

    public IQuerySelectStar all();
}

