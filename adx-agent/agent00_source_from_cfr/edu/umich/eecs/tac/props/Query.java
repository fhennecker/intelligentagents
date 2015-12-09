/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.ManufacturerComponentComposable;
import edu.umich.eecs.tac.props.QueryType;

public class Query
extends ManufacturerComponentComposable {
    public Query(String manufacturer, String component) {
        this.setManufacturer(manufacturer);
        this.setComponent(component);
        this.calculateHashCode();
    }

    public Query() {
        this.calculateHashCode();
    }

    public final QueryType getType() {
        return QueryType.value(this);
    }
}

