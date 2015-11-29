/*
 * Decompiled with CFR 0_110.
 */
package adx.logging;

import adx.logging.LogUtils;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.devices.Device;
import tau.tac.adx.report.adn.AdNetworkKey;
import tau.tac.adx.report.adn.AdNetworkReportEntry;
import tau.tac.adx.users.properties.Age;
import tau.tac.adx.users.properties.Gender;
import tau.tac.adx.users.properties.Income;

public class AdNetworkReportFormatter
implements Iterable<String> {
    protected List<AdNetworkReportEntry> entries = new LinkedList<AdNetworkReportEntry>();
    protected int campaignId;
    protected int gender;
    protected int age;
    protected int income;
    protected int publisher;
    protected int device;
    protected int adType;
    protected int win;
    protected int bid;
    protected int cost_int = 1;
    protected int cost_frac;

    public AdNetworkReportFormatter add(AdNetworkReportEntry entry) {
        AdNetworkKey key = (AdNetworkKey)entry.getKey();
        this.campaignId = Math.max(this.campaignId, Integer.toString(key.getCampaignId()).length());
        this.gender = Math.max(this.gender, key.getGender().toString().length());
        this.age = Math.max(this.age, LogUtils.age2string.get((Object)key.getAge()).length());
        this.income = Math.max(this.income, key.getIncome().toString().replace("very_high", "high+").length());
        this.publisher = Math.max(this.publisher, key.getPublisher().length());
        this.device = Math.max(this.device, key.getDevice().toString().length());
        this.adType = Math.max(this.adType, key.getAdType().toString().length());
        this.win = Math.max(this.win, Integer.toString(entry.getWinCount()).length());
        this.bid = Math.max(this.bid, Integer.toString(entry.getBidCount()).length());
        String dec = new DecimalFormat("0.######").format(entry.getCost());
        this.cost_int = Math.max(this.cost_int, dec.replaceFirst("\\.\\d*", "").length());
        this.cost_frac = Math.max(this.cost_frac, dec.replaceFirst("\\d*", "").length());
        this.entries.add(entry);
        return this;
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>(){
            protected final Iterator<AdNetworkReportEntry> iterator;
            protected final String format;

            @Override
            public boolean hasNext() {
                return this.iterator.hasNext();
            }

            @Override
            public String next() {
                AdNetworkReportEntry entry = this.iterator.next();
                AdNetworkKey key = (AdNetworkKey)entry.getKey();
                return String.format(this.format, new Object[]{Integer.toString(key.getCampaignId()), key.getGender(), LogUtils.age2string.get((Object)key.getAge()), key.getIncome().toString().replace("very_high", "high+"), key.getPublisher(), key.getDevice(), key.getAdType(), Integer.toString(entry.getWinCount()), Integer.toString(entry.getBidCount()), LogUtils.formatDouble(entry.getCost(), AdNetworkReportFormatter.this.cost_int, AdNetworkReportFormatter.this.cost_frac)});
            }

            @Override
            public void remove() {
                this.iterator.remove();
            }
        };
    }

}

