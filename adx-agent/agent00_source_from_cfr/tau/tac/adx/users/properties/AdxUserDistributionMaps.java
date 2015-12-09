/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.users.properties;

import java.util.Map;
import tau.tac.adx.users.properties.Age;
import tau.tac.adx.users.properties.Gender;
import tau.tac.adx.users.properties.Income;

public class AdxUserDistributionMaps {
    Map<Age, Integer> ageDistribution;
    Map<Gender, Integer> genderDistribution;
    Map<Income, Integer> incomeDistribution;

    public AdxUserDistributionMaps(Map<Age, Integer> ageDistribution, Map<Gender, Integer> genderDistribution, Map<Income, Integer> incomeDistribution) {
        this.ageDistribution = ageDistribution;
        this.genderDistribution = genderDistribution;
        this.incomeDistribution = incomeDistribution;
    }

    public Map<Age, Integer> getAgeDistribution() {
        return this.ageDistribution;
    }

    public void setAgeDistribution(Map<Age, Integer> ageDistribution) {
        this.ageDistribution = ageDistribution;
    }

    public Map<Gender, Integer> getGenderDistribution() {
        return this.genderDistribution;
    }

    public void setGenderDistribution(Map<Gender, Integer> genderDistribution) {
        this.genderDistribution = genderDistribution;
    }

    public Map<Income, Integer> getIncomeDistribution() {
        return this.incomeDistribution;
    }

    public void setIncomeDistribution(Map<Income, Integer> incomeDistribution) {
        this.incomeDistribution = incomeDistribution;
    }
}

