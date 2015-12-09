/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.auction;

import edu.umich.eecs.tac.props.Ad;

public class ResultsItem
implements Comparable<ResultsItem> {
    private final String advertiser;
    private final Ad ad;
    private final double position;

    public ResultsItem(String advertiser, Ad ad, double position) {
        this.advertiser = advertiser;
        this.ad = ad;
        this.position = position;
    }

    public String getAdvertiser() {
        return this.advertiser;
    }

    public Ad getAd() {
        return this.ad;
    }

    public double getPosition() {
        return this.position;
    }

    @Override
    public int compareTo(ResultsItem o) {
        return Double.compare(this.getPosition(), o.getPosition());
    }
}

