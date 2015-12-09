/*
 * Decompiled with CFR 0_110.
 */
package adx.logging;

import adx.logging.LogUtils;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.devices.Device;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.report.adn.MarketSegment;

public class BidBundleFormatter
implements Iterable<String> {
    protected List<Entry> entries = new LinkedList<Entry>();
    protected int campaignId;
    protected int gender;
    protected int age;
    protected int income;
    protected int publisher;
    protected int device;
    protected int adType;
    protected int bid_int = 1;
    protected int bid_frac;

    public BidBundleFormatter add(int campaignId, AdxQuery query, double bid) {
        this.campaignId = Math.max(this.campaignId, Integer.toString(campaignId).length());
        MarketSegmentTriplet triplet = new MarketSegmentTriplet(query.getMarketSegments());
        this.gender = Math.max(this.gender, triplet.getGender().length());
        this.age = Math.max(this.age, triplet.getAge().length());
        this.income = Math.max(this.income, triplet.getIncome().length());
        this.publisher = Math.max(this.publisher, query.getPublisher().length());
        this.device = Math.max(this.device, query.getDevice().toString().length());
        this.adType = Math.max(this.adType, query.getAdType().toString().length());
        String dec = new DecimalFormat("0.######").format(bid);
        this.bid_int = Math.max(this.bid_int, dec.replaceFirst("\\.\\d*", "").length());
        this.bid_frac = Math.max(this.bid_frac, dec.replaceFirst("\\d*", "").length());
        this.entries.add(new Entry(campaignId, query, bid));
        return this;
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>(){
            protected final Iterator<Entry> iterator;
            protected final String format;

            @Override
            public boolean hasNext() {
                return this.iterator.hasNext();
            }

            @Override
            public String next() {
                Entry entry = this.iterator.next();
                MarketSegmentTriplet triplet = new MarketSegmentTriplet(entry.query.getMarketSegments());
                return String.format(this.format, new Object[]{Integer.toString(entry.campaignId), triplet.getGender(), triplet.getAge(), triplet.getIncome(), entry.query.getPublisher(), entry.query.getDevice(), entry.query.getAdType(), LogUtils.formatDouble(entry.bid, BidBundleFormatter.this.bid_int, BidBundleFormatter.this.bid_frac)});
            }

            @Override
            public void remove() {
                this.iterator.remove();
            }
        };
    }

    protected static class Entry {
        public int campaignId;
        public AdxQuery query;
        public double bid;

        public Entry(int campaignId, AdxQuery query, double bid) {
            this.campaignId = campaignId;
            this.query = query;
            this.bid = bid;
        }
    }

    protected static class MarketSegmentTriplet {
        protected String[] triplet = new String[3];
        private static /* synthetic */ int[] $SWITCH_TABLE$tau$tac$adx$report$adn$MarketSegment;

        public MarketSegmentTriplet(Set<MarketSegment> marketSegments) {
            Arrays.fill(this.triplet, "");
            for (MarketSegment segment : marketSegments) {
                switch (MarketSegmentTriplet.$SWITCH_TABLE$tau$tac$adx$report$adn$MarketSegment()[segment.ordinal()]) {
                    case 1: 
                    case 2: {
                        this.triplet[0] = segment.toString().toLowerCase();
                        break;
                    }
                    case 3: 
                    case 4: {
                        this.triplet[1] = segment.toString().toLowerCase();
                        break;
                    }
                    case 5: 
                    case 6: {
                        this.triplet[2] = segment.toString().replaceFirst("_.*", "").toLowerCase();
                    }
                }
            }
        }

        public String getGender() {
            return this.triplet[0];
        }

        public String getAge() {
            return this.triplet[1];
        }

        public String getIncome() {
            return this.triplet[2];
        }

        static /* synthetic */ int[] $SWITCH_TABLE$tau$tac$adx$report$adn$MarketSegment() {
            int[] arrn;
            int[] arrn2 = $SWITCH_TABLE$tau$tac$adx$report$adn$MarketSegment;
            if (arrn2 != null) {
                return arrn2;
            }
            arrn = new int[MarketSegment.values().length];
            try {
                arrn[MarketSegment.FEMALE.ordinal()] = 2;
            }
            catch (NoSuchFieldError v1) {}
            try {
                arrn[MarketSegment.HIGH_INCOME.ordinal()] = 6;
            }
            catch (NoSuchFieldError v2) {}
            try {
                arrn[MarketSegment.LOW_INCOME.ordinal()] = 5;
            }
            catch (NoSuchFieldError v3) {}
            try {
                arrn[MarketSegment.MALE.ordinal()] = 1;
            }
            catch (NoSuchFieldError v4) {}
            try {
                arrn[MarketSegment.OLD.ordinal()] = 4;
            }
            catch (NoSuchFieldError v5) {}
            try {
                arrn[MarketSegment.YOUNG.ordinal()] = 3;
            }
            catch (NoSuchFieldError v6) {}
            $SWITCH_TABLE$tau$tac$adx$report$adn$MarketSegment = arrn;
            return $SWITCH_TABLE$tau$tac$adx$report$adn$MarketSegment;
        }
    }

}

