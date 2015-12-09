/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.publishers;

import edu.umich.eecs.tac.props.KeyedEntry;
import java.text.ParseException;
import java.util.Map;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;
import tau.tac.adx.ads.properties.AdAttributeProbabilityMaps;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.devices.Device;
import tau.tac.adx.publishers.reserve.UserAdTypeReservePriceManager;
import tau.tac.adx.users.AdxUser;
import tau.tac.adx.users.properties.AdxUserAttributeProbabilityMaps;
import tau.tac.adx.users.properties.Age;
import tau.tac.adx.users.properties.Gender;
import tau.tac.adx.users.properties.Income;

public class AdxPublisher
implements KeyedEntry<AdxPublisher> {
    private static final long serialVersionUID = -8433553718771938842L;
    private Map<AdType, Double> adTypeDistribution;
    private Map<Device, Double> deviceProbabilityMap;
    private String name;
    private double pImpressions;
    private AdxUserAttributeProbabilityMaps probabilityMaps;
    private double relativePopularity;
    private UserAdTypeReservePriceManager reservePriceManager;

    public AdxPublisher(AdxUserAttributeProbabilityMaps probabilityMaps, AdAttributeProbabilityMaps adAttributeProbabilityMaps, Map<Device, Double> deviceProbabilityMap, double relativePopularity, double pImpressions, UserAdTypeReservePriceManager reservePriceManager, String name) {
        this.probabilityMaps = probabilityMaps;
        this.adTypeDistribution = adAttributeProbabilityMaps.getAdTypeDistribution();
        this.relativePopularity = relativePopularity;
        this.pImpressions = pImpressions;
        this.reservePriceManager = reservePriceManager;
        this.deviceProbabilityMap = deviceProbabilityMap;
        this.name = name;
    }

    public Map<AdType, Double> getAdTypeDistribution() {
        return this.adTypeDistribution;
    }

    public Map<Device, Double> getDeviceProbabilityMap() {
        return this.deviceProbabilityMap;
    }

    public String getName() {
        return this.name;
    }

    public double getpImpressions() {
        return this.pImpressions;
    }

    public AdxUserAttributeProbabilityMaps getProbabilityMaps() {
        return this.probabilityMaps;
    }

    public double getRelativePopularity() {
        return this.relativePopularity;
    }

    public UserAdTypeReservePriceManager getReservePriceManager() {
        return this.reservePriceManager;
    }

    public void setAdTypeDistribution(Map<AdType, Double> adTypeDistribution) {
        this.adTypeDistribution = adTypeDistribution;
    }

    public void setDeviceProbabilityMap(Map<Device, Double> deviceProbabilityMap) {
        this.deviceProbabilityMap = deviceProbabilityMap;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setpImpressions(double pImpressions) {
        this.pImpressions = pImpressions;
    }

    public void setProbabilityMaps(AdxUserAttributeProbabilityMaps probabilityMaps) {
        this.probabilityMaps = probabilityMaps;
    }

    public void setRelativePopularity(double relativePopularity) {
        this.relativePopularity = relativePopularity;
    }

    public void setReservePriceManager(UserAdTypeReservePriceManager reservePriceManager) {
        this.reservePriceManager = reservePriceManager;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.adTypeDistribution == null ? 0 : this.adTypeDistribution.hashCode());
        result = 31 * result + (this.deviceProbabilityMap == null ? 0 : this.deviceProbabilityMap.hashCode());
        result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
        long temp = Double.doubleToLongBits(this.pImpressions);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        result = 31 * result + (this.probabilityMaps == null ? 0 : this.probabilityMaps.hashCode());
        temp = Double.doubleToLongBits(this.relativePopularity);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        AdxPublisher other = (AdxPublisher)obj;
        if (this.adTypeDistribution == null ? other.adTypeDistribution != null : !this.adTypeDistribution.equals(other.adTypeDistribution)) {
            return false;
        }
        if (this.deviceProbabilityMap == null ? other.deviceProbabilityMap != null : !this.deviceProbabilityMap.equals(other.deviceProbabilityMap)) {
            return false;
        }
        if (this.name == null ? other.name != null : !this.name.equals(other.name)) {
            return false;
        }
        if (Double.doubleToLongBits(this.pImpressions) != Double.doubleToLongBits(other.pImpressions)) {
            return false;
        }
        if (this.probabilityMaps == null ? other.probabilityMaps != null : !this.probabilityMaps.equals(other.probabilityMaps)) {
            return false;
        }
        if (Double.doubleToLongBits(this.relativePopularity) != Double.doubleToLongBits(other.relativePopularity)) {
            return false;
        }
        return true;
    }

    @Override
    public String getTransportName() {
        return this.getClass().getName();
    }

    @Override
    public synchronized void read(TransportReader reader) throws ParseException {
        AdxPublisher publisherReader = (AdxPublisher)reader.readTransportable();
        this.adTypeDistribution = publisherReader.getAdTypeDistribution();
        this.deviceProbabilityMap = publisherReader.getDeviceProbabilityMap();
        this.name = publisherReader.getName();
        this.pImpressions = publisherReader.getpImpressions();
        this.probabilityMaps = publisherReader.getProbabilityMaps();
        this.relativePopularity = publisherReader.getRelativePopularity();
    }

    @Override
    public synchronized void write(TransportWriter writer) {
        writer.write(this);
    }

    @Override
    public AdxPublisher getKey() {
        return this;
    }

    public double userAffiliation(AdxUser user) {
        double d = this.probabilityMaps.getAgeDistribution().get((Object)user.getAge()) * this.probabilityMaps.getGenderDistribution().get((Object)user.getGender()) * this.probabilityMaps.getIncomeDistribution().get((Object)user.getIncome()) * this.relativePopularity;
        return d;
    }
}

