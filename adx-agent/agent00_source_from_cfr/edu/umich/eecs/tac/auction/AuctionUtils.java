/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.auction;

public class AuctionUtils {
    private AuctionUtils() {
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

    public static void generalizedSecondPrice(int[] indices, double[] weights, double[] bids, double[] cpc, boolean[] promoted, int promotedSlots, double promotedReserve, int regularSlots, double regularReserve) {
        int positions = Math.min(indices.length, regularSlots);
        int promotedCount = 0;
        int i = 0;
        while (i < positions) {
            double secondWeight;
            double secondBid;
            double weight = weights[indices[i]];
            double bid = bids[indices[i]];
            if (i < indices.length - 1) {
                secondWeight = weights[indices[i + 1]];
                secondBid = bids[indices[i + 1]];
            } else {
                secondWeight = Double.NaN;
                secondBid = Double.NaN;
            }
            if (promotedCount < promotedSlots && weight * bid >= promotedReserve) {
                cpc[indices[i]] = AuctionUtils.calculateSecondPriceWithReserve(weight, secondWeight, secondBid, promotedReserve);
                promoted[indices[i]] = true;
                ++promotedCount;
            } else if (weight * bid >= regularReserve) {
                cpc[indices[i]] = AuctionUtils.calculateSecondPriceWithReserve(weight, secondWeight, secondBid, regularReserve);
                promoted[indices[i]] = false;
            } else {
                cpc[indices[i]] = Double.NaN;
                promoted[indices[i]] = false;
            }
            ++i;
        }
        i = positions;
        while (i < indices.length) {
            cpc[indices[i]] = Double.NaN;
            ++i;
        }
    }

    public static double calculateSecondPriceWithReserve(double weight, double secondWeight, double secondBid, double reserve) {
        double price = reserve <= secondWeight * secondBid ? secondWeight / weight * secondBid : reserve / weight;
        return price;
    }
}

