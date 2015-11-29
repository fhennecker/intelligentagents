/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.EventBus
 */
package tau.tac.adx.users;

import com.google.common.eventbus.EventBus;
import edu.umich.eecs.tac.util.config.ConfigProxy;
import edu.umich.eecs.tac.util.config.ConfigProxyUtils;
import java.util.List;
import java.util.Random;
import tau.tac.adx.agents.DefaultAdxUserManager;
import tau.tac.adx.props.PublisherCatalog;
import tau.tac.adx.sim.AdxAgentRepository;
import tau.tac.adx.users.AdxUser;
import tau.tac.adx.users.AdxUserBehaviorBuilder;
import tau.tac.adx.users.AdxUserQueryManager;
import tau.tac.adx.users.AdxUserQueryManagerBuilder;
import tau.tac.adx.users.DefaultAdxUserQueryManager;

public class DefaultAdxUserManagerBuilder
implements AdxUserBehaviorBuilder<DefaultAdxUserManager> {
    private static final String ADX_BASE = "adx_usermanager";
    private static final String POPULATION_SIZE_KEY = "populationsize";
    private static final int POPULATION_SIZE_DEFAULT = 10000;
    private static final String VIEW_MANAGER_KEY = "viewmanager";
    private static final String QUERY_MANAGER_KEY = "querymanager";
    private static final String ADX_QUERY_MANAGER_DEFAULT = AdxUserQueryManagerBuilder.class.getName();

    @Override
    public DefaultAdxUserManager build(ConfigProxy userConfigProxy, AdxAgentRepository repository, Random random) {
        try {
            AdxUserBehaviorBuilder queryBuilder = (AdxUserBehaviorBuilder)ConfigProxyUtils.createObjectFromProperty(userConfigProxy, "adx_usermanager.querymanager", ADX_QUERY_MANAGER_DEFAULT);
            DefaultAdxUserQueryManager queryManager = (DefaultAdxUserQueryManager)queryBuilder.build(userConfigProxy, repository, random);
            int populationSize = userConfigProxy.getPropertyAsInt("adx_usermanager.populationsize", 10000);
            return new DefaultAdxUserManager(repository.getPublisherCatalog(), repository.getUserPopulation(), queryManager, populationSize, repository.getEventBus());
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

