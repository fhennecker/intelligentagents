/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.logviewer.util;

import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.Query;

public class VisualizerUtils {
    private VisualizerUtils() {
    }

    public static String formatToString(Ad ad) {
        Product product = ad.getProduct();
        if (product == null) {
            return new String("GENERIC");
        }
        return String.format("(%s,%s)", product.getManufacturer(), product.getComponent());
    }

    public static String formatToString(Query query) {
        return String.format("(%s,%s)", query.getManufacturer(), query.getComponent());
    }

    public static void hardSort(double[] scores, int[] indices) {
        int i = 0;
        while (i < indices.length - 1) {
            int j = i + 1;
            while (j < indices.length) {
                if (scores[indices[i]] < scores[indices[j]] || Double.isNaN(scores[indices[i]])) {
                    int sw = indices[i];
                    indices[i] = indices[j];
                    indices[j] = sw;
                }
                ++j;
            }
            ++i;
        }
    }
}

