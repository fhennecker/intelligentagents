/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 */
package tau.tac.adx.users;

import com.google.inject.Inject;
import edu.umich.eecs.tac.util.sampling.Sampler;
import edu.umich.eecs.tac.util.sampling.WheelSampler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import tau.tac.adx.AdxManager;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.devices.Device;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.props.PublisherCatalog;
import tau.tac.adx.props.PublisherCatalogEntry;
import tau.tac.adx.publishers.AdxPublisher;
import tau.tac.adx.users.AdxUser;
import tau.tac.adx.users.AdxUserQueryManager;
import tau.tac.adx.util.EnumGenerator;

public class DefaultAdxUserQueryManager
implements AdxUserQueryManager {
    private final Map<AdxUser, Sampler<AdxQuery>> querySamplers;
    private final Map<Device, Integer> deviceDeistributionMap;
    private final Map<AdType, Integer> adTypeDeistributionMap;

    @Inject
    public DefaultAdxUserQueryManager(PublisherCatalog catalog, List<AdxUser> users, Map<Device, Integer> deviceDistributionMap, Map<AdType, Integer> adTypeDistributionMap, Random random) {
        if (catalog == null) {
            throw new NullPointerException("Publisher catalog cannot be null");
        }
        if (users == null) {
            throw new NullPointerException("User list cannot be null");
        }
        if (random == null) {
            throw new NullPointerException("Random number generator cannot be null");
        }
        if (deviceDistributionMap == null) {
            throw new NullPointerException("Device distribution map cannot be null");
        }
        if (adTypeDistributionMap == null) {
            throw new NullPointerException("Ad Type distribution map cannot be null");
        }
        this.deviceDeistributionMap = deviceDistributionMap;
        this.adTypeDeistributionMap = adTypeDistributionMap;
        this.querySamplers = this.buildQuerySamplers(catalog, users, random);
    }

    @Override
    public AdxQuery generateQuery(AdxUser user) {
        Sampler<AdxQuery> sampler = this.querySamplers.get(user);
        if (sampler == null) {
            return null;
        }
        return sampler.getSample();
    }

    @Override
    public void nextTimeUnit(int timeUnit) {
    }

    private Map<AdxUser, Sampler<AdxQuery>> buildQuerySamplers(PublisherCatalog catalog, List<AdxUser> users, Random random) {
        EnumGenerator<Device> deviceGenerator = new EnumGenerator<Device>(this.deviceDeistributionMap);
        EnumGenerator<AdType> adTypeGenerator = new EnumGenerator<AdType>(this.adTypeDeistributionMap);
        HashMap<AdxUser, Sampler<AdxQuery>> samplingMap = new HashMap<AdxUser, Sampler<AdxQuery>>();
        for (AdxUser user : users) {
            WheelSampler<AdxQuery> sampler = new WheelSampler<AdxQuery>(random);
            for (PublisherCatalogEntry publisherEntry : catalog) {
                Device device = deviceGenerator.randomType();
                AdType adType = adTypeGenerator.randomType();
                AdxQuery query = new AdxQuery(publisherEntry.getPublisherName(), user, device, adType);
                AdxPublisher publisher = AdxManager.getInstance().getPublisher(publisherEntry.getPublisherName());
                double weight = publisher.userAffiliation(user);
                sampler.addState(weight, query);
            }
            samplingMap.put(user, sampler);
        }
        return samplingMap;
    }
}

