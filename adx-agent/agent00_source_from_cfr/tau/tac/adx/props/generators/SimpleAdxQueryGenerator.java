/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 */
package tau.tac.adx.props.generators;

import com.google.inject.Inject;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.ads.properties.generators.AdTypeGenerator;
import tau.tac.adx.devices.Device;
import tau.tac.adx.devices.generators.DeviceGenerator;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.props.generators.AdxQueryGenerator;
import tau.tac.adx.publishers.AdxPublisher;
import tau.tac.adx.publishers.generators.AdxPublisherGenerator;
import tau.tac.adx.users.AdxUser;
import tau.tac.adx.users.generators.AdxUserGenerator;

public class SimpleAdxQueryGenerator
implements AdxQueryGenerator {
    private final AdxUserGenerator userGenerator;
    private final AdxPublisherGenerator publisherGenerator;
    private final DeviceGenerator deviceGenerator;
    private final AdTypeGenerator adTypeGenerator;
    private final Logger logger;

    @Inject
    public SimpleAdxQueryGenerator(AdxUserGenerator userGenerator, AdxPublisherGenerator publisherGenerator, DeviceGenerator deviceGenerator, AdTypeGenerator adTypeGenerator) {
        this.logger = Logger.getLogger(this.getClass().getCanonicalName());
        this.userGenerator = userGenerator;
        this.publisherGenerator = publisherGenerator;
        this.deviceGenerator = deviceGenerator;
        this.adTypeGenerator = adTypeGenerator;
    }

    @Override
    public Collection<AdxQuery> generate(int amount) {
        LinkedList<AdxQuery> publishers = new LinkedList<AdxQuery>();
        int i = 0;
        while (i < amount) {
            publishers.add(this.getRandomAdxQuery());
            ++i;
        }
        this.logger.fine("Generated " + amount + " " + AdxQuery.class.getName() + "s");
        return publishers;
    }

    private AdxQuery getRandomAdxQuery() {
        AdxPublisher publisher = this.publisherGenerator.generate(1).iterator().next();
        AdxUser user = this.userGenerator.generate(1).iterator().next();
        Device device = this.deviceGenerator.generate(1).iterator().next();
        AdType adType = this.adTypeGenerator.generate(1).iterator().next();
        AdxQuery query = new AdxQuery(publisher, user, device, adType);
        return query;
    }
}

