/*
 * Decompiled with CFR 0_110.
 */
package com.botbox.html;

public class TableStyle {
    private static TableStyle normalTable;
    private static TableStyle lineTable;
    private static TableStyle borderTable;
    private String outerAttributes;
    private String attributes;
    private String trAttributes;
    private String thAttributes;
    private String tdAttributes;
    private boolean isDoubleTable;

    static TableStyle getNormalTable() {
        if (normalTable == null) {
            normalTable = new TableStyle();
        }
        return normalTable;
    }

    static TableStyle getLineTable() {
        if (lineTable == null) {
            lineTable = new TableStyle("cellspacing=0 cellpadding=0 border=0 bgcolor=black", "cellspacing=1 cellpadding=2 border=0", null, "bgcolor='#e0e0ff'", "bgcolor='#f0f0f0'");
        }
        return lineTable;
    }

    static TableStyle getBorderTable() {
        if (borderTable == null) {
            borderTable = new TableStyle("cellspacing=0 cellpadding=1 border=0 bgcolor=black", "cellspacing=0 border=0", null, "bgcolor='#e0e0ff'", "bgcolor='#f0f0f0'");
        }
        return borderTable;
    }

    public TableStyle() {
    }

    public TableStyle(String outerAttributes, String attributes, String trAttributes, String thAttributes, String tdAttributes) {
        this.outerAttributes = outerAttributes;
        this.attributes = attributes;
        this.trAttributes = trAttributes;
        this.thAttributes = thAttributes;
        this.tdAttributes = tdAttributes;
        this.isDoubleTable = true;
    }

    public boolean isDoubleTable() {
        return this.isDoubleTable;
    }

    public String getOuterAttributes() {
        return this.outerAttributes;
    }

    public String getAttributes() {
        return this.attributes;
    }

    public String getTrAttributes() {
        return this.trAttributes;
    }

    public String getThAttributes() {
        return this.thAttributes;
    }

    public String getTdAttributes() {
        return this.tdAttributes;
    }
}

