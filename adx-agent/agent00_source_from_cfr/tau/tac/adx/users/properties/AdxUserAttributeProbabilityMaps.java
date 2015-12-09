/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.users.properties;

import java.util.Map;
import tau.tac.adx.users.properties.Age;
import tau.tac.adx.users.properties.Gender;
import tau.tac.adx.users.properties.Income;

public class AdxUserAttributeProbabilityMaps {
    Map<Age, Double> ageDistribution;
    Map<Gender, Double> genderDistribution;
    Map<Income, Double> incomeDistribution;

    public AdxUserAttributeProbabilityMaps(Map<Age, Double> ageDistribution, Map<Gender, Double> genderDistribution, Map<Income, Double> incomeDistribution) {
        this.ageDistribution = ageDistribution;
        this.genderDistribution = genderDistribution;
        this.incomeDistribution = incomeDistribution;
    }

    public Map<Age, Double> getAgeDistribution() {
        return this.ageDistribution;
    }

    public void setAgeDistribution(Map<Age, Double> ageDistribution) {
        this.ageDistribution = ageDistribution;
    }

    public Map<Gender, Double> getGenderDistribution() {
        return this.genderDistribution;
    }

    public void setGenderDistribution(Map<Gender, Double> genderDistribution) {
        this.genderDistribution = genderDistribution;
    }

    public Map<Income, Double> getIncomeDistribution() {
        return this.incomeDistribution;
    }

    public void setIncomeDistribution(Map<Income, Double> incomeDistribution) {
        this.incomeDistribution = incomeDistribution;
    }
}

