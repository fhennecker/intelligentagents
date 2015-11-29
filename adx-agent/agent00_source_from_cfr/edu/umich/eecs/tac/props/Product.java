/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.ManufacturerComponentComposable;

public class Product
extends ManufacturerComponentComposable {
    public Product() {
        this.calculateHashCode();
    }

    public Product(String manufacturer, String component) {
        this.setManufacturer(manufacturer);
        this.setComponent(component);
    }
}

