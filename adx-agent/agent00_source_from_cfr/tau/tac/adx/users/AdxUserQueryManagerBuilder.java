/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.users;

import edu.umich.eecs.tac.util.config.ConfigProxy;
import java.util.List;
import java.util.Map;
import java.util.Random;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.devices.Device;
import tau.tac.adx.props.PublisherCatalog;
import tau.tac.adx.sim.AdxAgentRepository;
import tau.tac.adx.users.AdxUser;
import tau.tac.adx.users.AdxUserBehaviorBuilder;
import tau.tac.adx.users.AdxUserQueryManager;
import tau.tac.adx.users.DefaultAdxUserQueryManager;

public class AdxUserQueryManagerBuilder
implements AdxUserBehaviorBuilder<AdxUserQueryManager> {
    @Override
    public DefaultAdxUserQueryManager build(ConfigProxy userConfigProxy, AdxAgentRepository repository, Random random) {
        return new DefaultAdxUserQueryManager(repository.getPublisherCatalog(), repository.getUserPopulation(), repository.getDeviceDistributionMap(), repository.getAdTypeDistributionMap(), random);
    }
}

