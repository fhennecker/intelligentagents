/*
 * Decompiled with CFR 0_110.
 */
package adx.query;

import adx.query.IQueryGrouperStar;

public interface IQueryGrouper {
    public IQueryGrouperStar property(String var1);

    public IQueryGrouperStar index(int var1);
}

