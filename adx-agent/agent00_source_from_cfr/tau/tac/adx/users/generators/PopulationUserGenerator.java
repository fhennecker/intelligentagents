/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.users.generators;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import tau.tac.adx.users.AdxUser;
import tau.tac.adx.users.generators.AdxUserGenerator;
import tau.tac.adx.util.EnumGenerator;

public class PopulationUserGenerator
implements AdxUserGenerator {
    private final EnumGenerator<AdxUser> userGenerator;
    private int uniqueId;
    private final Logger logger;

    public PopulationUserGenerator(Map<AdxUser, Integer> weights) {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.userGenerator = new EnumGenerator<AdxUser>(weights);
    }

    @Override
    public List<AdxUser> generate(int amount) {
        LinkedList<AdxUser> users = new LinkedList<AdxUser>();
        int i = 0;
        while (i < amount) {
            users.add(this.getRandomUser());
            ++i;
        }
        this.logger.fine("Generated " + amount + " " + AdxUser.class.getName() + "s");
        return users;
    }

    private AdxUser getRandomUser() {
        double pContinue = 0.1;
        AdxUser user = (AdxUser)this.userGenerator.randomType().clone();
        user.setpContinue(pContinue);
        user.setUniqueId(this.uniqueId);
        ++this.uniqueId;
        return user;
    }
}

