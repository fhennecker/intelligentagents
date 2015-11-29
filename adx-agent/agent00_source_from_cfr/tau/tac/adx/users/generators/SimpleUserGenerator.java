/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.users.generators;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import tau.tac.adx.users.AdxUser;
import tau.tac.adx.users.generators.AdxUserGenerator;
import tau.tac.adx.users.properties.Age;
import tau.tac.adx.users.properties.Gender;
import tau.tac.adx.users.properties.Income;

public class SimpleUserGenerator
implements AdxUserGenerator {
    private static AtomicInteger uniqueId = new AtomicInteger();
    private double pContinue;
    private final Logger logger;

    public SimpleUserGenerator(double pContinue) {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.pContinue = pContinue;
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

    public static Collection<AdxUser> generateAllPossibleUsers() {
        LinkedList<AdxUser> users = new LinkedList<AdxUser>();
        Age[] arrage = Age.values();
        int n = arrage.length;
        int n2 = 0;
        while (n2 < n) {
            Age age = arrage[n2];
            Gender[] arrgender = Gender.values();
            int n3 = arrgender.length;
            int n4 = 0;
            while (n4 < n3) {
                Gender gender = arrgender[n4];
                Income[] arrincome = Income.values();
                int n5 = arrincome.length;
                int n6 = 0;
                while (n6 < n5) {
                    Income income = arrincome[n6];
                    users.add(new AdxUser(age, gender, income, Double.NaN, uniqueId.incrementAndGet()));
                    ++n6;
                }
                ++n4;
            }
            ++n2;
        }
        return users;
    }

    private AdxUser getRandomUser() {
        Age age = this.randomAge();
        Gender gender = this.randomGender();
        Income income = this.randomIncome();
        AdxUser user = new AdxUser(age, gender, income, this.pContinue, uniqueId.incrementAndGet());
        return user;
    }

    private Income randomIncome() {
        Random random = new Random();
        return Income.values()[random.nextInt(Income.values().length)];
    }

    private Gender randomGender() {
        Random random = new Random();
        return Gender.values()[random.nextInt(Gender.values().length)];
    }

    private Age randomAge() {
        Random random = new Random();
        return Age.values()[random.nextInt(Age.values().length)];
    }
}

