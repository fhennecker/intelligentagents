/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.Query;

public enum QueryType {
    FOCUS_LEVEL_ZERO,
    FOCUS_LEVEL_ONE,
    FOCUS_LEVEL_TWO;
    

    private QueryType(String string2, int n2) {
    }

    public static QueryType value(Query query) {
        int components = 0;
        if (query.getManufacturer() != null) {
            ++components;
        }
        if (query.getComponent() != null) {
            ++components;
        }
        return QueryType.values()[components];
    }
}

