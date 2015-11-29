/*
 * Decompiled with CFR 0_110.
 */
package adx.logging;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.devices.Device;
import tau.tac.adx.report.adn.AdNetworkKey;
import tau.tac.adx.report.adn.AdNetworkReport;
import tau.tac.adx.report.adn.AdNetworkReportEntry;
import tau.tac.adx.users.properties.Age;
import tau.tac.adx.users.properties.Gender;
import tau.tac.adx.users.properties.Income;

public class AdNetworkReportSorted
implements Iterable<AdNetworkReportEntry> {
    protected SortedSet<AdNetworkReportEntry> sortedSet;

    public AdNetworkReportSorted(AdNetworkReport report) {
        this.sortedSet = new TreeSet<AdNetworkReportEntry>(new Comparator<AdNetworkReportEntry>(){

            @Override
            public int compare(AdNetworkReportEntry o1, AdNetworkReportEntry o2) {
                int campaign = ((AdNetworkKey)o1.getKey()).getCampaignId() - ((AdNetworkKey)o2.getKey()).getCampaignId();
                if (campaign != 0) {
                    return campaign;
                }
                int gender = ((AdNetworkKey)o1.getKey()).getGender().compareTo(((AdNetworkKey)o2.getKey()).getGender());
                if (gender != 0) {
                    return gender;
                }
                int age = ((AdNetworkKey)o1.getKey()).getAge().compareTo(((AdNetworkKey)o2.getKey()).getAge());
                if (age != 0) {
                    return age;
                }
                int income = ((AdNetworkKey)o1.getKey()).getIncome().compareTo(((AdNetworkKey)o2.getKey()).getIncome());
                if (income != 0) {
                    return income;
                }
                int publisher = ((AdNetworkKey)o1.getKey()).getPublisher().compareTo(((AdNetworkKey)o2.getKey()).getPublisher());
                if (publisher != 0) {
                    return publisher;
                }
                int device = ((AdNetworkKey)o1.getKey()).getDevice().compareTo(((AdNetworkKey)o2.getKey()).getDevice());
                if (device != 0) {
                    return device;
                }
                int adtype = ((AdNetworkKey)o1.getKey()).getAdType().compareTo(((AdNetworkKey)o2.getKey()).getAdType());
                if (adtype != 0) {
                    return adtype;
                }
                return 0;
            }
        });
        for (AdNetworkKey key : report) {
            this.sortedSet.add(report.getAdNetworkReportEntry(key));
        }
    }

    @Override
    public Iterator<AdNetworkReportEntry> iterator() {
        return this.sortedSet.iterator();
    }

}

