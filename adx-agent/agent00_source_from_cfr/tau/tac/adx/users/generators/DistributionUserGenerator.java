/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.users.generators;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;
import tau.tac.adx.users.AdxUser;
import tau.tac.adx.users.generators.AdxUserGenerator;
import tau.tac.adx.users.properties.AdxUserDistributionMaps;
import tau.tac.adx.users.properties.Age;
import tau.tac.adx.users.properties.Gender;
import tau.tac.adx.users.properties.Income;
import tau.tac.adx.util.EnumGenerator;

public class DistributionUserGenerator
implements AdxUserGenerator {
    private final EnumGenerator<Age> ageGenerator;
    private final EnumGenerator<Gender> genderGenerator;
    private final EnumGenerator<Income> incomeGenerator;
    private int uniqueId;
    private final Logger logger;

    public DistributionUserGenerator(AdxUserDistributionMaps distributionMaps) {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.ageGenerator = new EnumGenerator<Age>(distributionMaps.getAgeDistribution());
        this.genderGenerator = new EnumGenerator<Gender>(distributionMaps.getGenderDistribution());
        this.incomeGenerator = new EnumGenerator<Income>(distributionMaps.getIncomeDistribution());
    }

    @Override
    public Collection<AdxUser> generate(int amount) {
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
        Age age = this.ageGenerator.randomType();
        Gender gender = this.genderGenerator.randomType();
        Income income = this.incomeGenerator.randomType();
        double pContinue = 0.1;
        AdxUser user = new AdxUser(age, gender, income, pContinue, this.uniqueId);
        ++this.uniqueId;
        return user;
    }
}

