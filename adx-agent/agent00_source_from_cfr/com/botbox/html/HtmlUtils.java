/*
 * Decompiled with CFR 0_110.
 */
package com.botbox.html;

import com.botbox.html.HtmlWriter;

public class HtmlUtils {
    private static final String loColor = "#40ff40";
    private static final String miColor = "yellow";
    private static final String hiColor = "red";

    private HtmlUtils() {
    }

    public static void progress(HtmlWriter out, int width, int height, int lo, int mi, int hi) {
        out.tag("table").attr("border=0 cellpadding=0 cellspacing=1 bgcolor=black").attr("width", width).attr("height", 8).text("<tr>");
        if (lo > 0) {
            out.text("<td bgcolor='").text("#40ff40").text("' width='").text(lo).text("%'></td>").newLine();
        }
        if (mi > 0) {
            out.text("<td bgcolor='").text("yellow").text("' width='").text(mi).text("%'></td>").newLine();
        }
        if (hi > 0) {
            out.text("<td bgcolor='").text("red").text("' width='").text(hi).text("%'></td>").newLine();
        }
        out.text("</tr>");
        out.tagEnd("table");
    }
}

