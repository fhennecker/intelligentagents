/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.ads.properties;

import java.util.Map;
import tau.tac.adx.ads.properties.AdType;

public class AdAttributeProbabilityMaps {
    Map<AdType, Double> adTypeDistribution;

    public AdAttributeProbabilityMaps(Map<AdType, Double> adTypeDistribution) {
        this.adTypeDistribution = adTypeDistribution;
    }

    public Map<AdType, Double> getAdTypeDistribution() {
        return this.adTypeDistribution;
    }

    public void setAdTypeDistribution(Map<AdType, Double> adTypeDistribution) {
        this.adTypeDistribution = adTypeDistribution;
    }
}

