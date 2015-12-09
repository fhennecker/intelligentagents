/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.sim.config;

import edu.umich.eecs.tac.props.AdvertiserInfo;
import edu.umich.eecs.tac.sim.Bank;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import se.sics.isl.util.ConfigManager;
import se.sics.tasim.sim.SimulationAgent;
import tau.tac.adx.ads.properties.AdAttributeProbabilityMaps;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.devices.Device;
import tau.tac.adx.props.PublisherCatalog;
import tau.tac.adx.publishers.AdxPublisher;
import tau.tac.adx.publishers.reserve.UserAdTypeReservePriceManager;
import tau.tac.adx.sim.TACAdxSimulation;
import tau.tac.adx.users.AdxUser;
import tau.tac.adx.users.generators.PopulationUserGenerator;
import tau.tac.adx.users.properties.AdxUserAttributeProbabilityMaps;
import tau.tac.adx.users.properties.Age;
import tau.tac.adx.users.properties.Gender;
import tau.tac.adx.users.properties.Income;

public class AdxConfigurationParser {
    private final ConfigManager config;
    private String[] names = new String[]{"yahoo", "cnn", "nyt", "hfn", "msn", "fox", "amazon", "ebay", "wallmart", "target", "bestbuy", "sears", "webmd", "ehow", "ask", "tripadvisor", "cnet", "weather"};
    private double[] ratings = new double[]{0.16, 0.022, 0.031, 0.081, 0.182, 0.031, 0.128, 0.085, 0.38, 0.02, 0.016, 0.016, 0.025, 0.025, 0.05, 0.016, 0.17, 0.058};
    private double[] adTypeText = new double[]{0.7, 0.5, 0.5, 0.7, 0.5, 0.9, 0.7, 0.5, 0.9, 0.7, 0.5, 0.9, 0.7, 0.5, 0.5, 0.9, 0.7, 0.5};
    private double[] adTypeVideo = new double[]{0.3, 0.5, 0.5, 0.3, 0.5, 0.1, 0.3, 0.5, 0.1, 0.3, 0.5, 0.1, 0.3, 0.5, 0.5, 0.1, 0.3, 0.5};
    private double[] age1 = new double[]{0.122, 0.102, 0.092, 0.102, 0.102, 0.092, 0.092, 0.092, 0.072, 0.092, 0.102, 0.092, 0.092, 0.102, 0.102, 0.082, 0.122, 0.092};
    private double[] age2 = new double[]{0.171, 0.161, 0.151, 0.161, 0.161, 0.151, 0.151, 0.161, 0.151, 0.171, 0.141, 0.121, 0.151, 0.151, 0.131, 0.161, 0.151, 0.151};
    private double[] age3 = new double[]{0.167, 0.167, 0.167, 0.167, 0.167, 0.167, 0.167, 0.157, 0.167, 0.177, 0.167, 0.167, 0.157, 0.157, 0.157, 0.177, 0.157, 0.167};
    private double[] age4 = new double[]{0.184, 0.194, 0.194, 0.194, 0.194, 0.194, 0.194, 0.194, 0.204, 0.184, 0.204, 0.204, 0.194, 0.194, 0.204, 0.204, 0.184, 0.204};
    private double[] age5 = new double[]{0.164, 0.174, 0.174, 0.174, 0.174, 0.184, 0.184, 0.174, 0.184, 0.173, 0.174, 0.184, 0.184, 0.174, 0.184, 0.174, 0.174, 0.184};
    private double[] age6 = new double[]{0.192, 0.202, 0.202, 0.202, 0.202, 0.212, 0.212, 0.222, 0.222, 0.202, 0.212, 0.232, 0.222, 0.222, 0.222, 0.212, 0.212, 0.202};
    private double[] genderMale = new double[]{0.496, 0.486, 0.476, 0.466, 0.476, 0.486, 0.476, 0.486, 0.456, 0.456, 0.476, 0.466, 0.456, 0.476, 0.486, 0.466, 0.506, 0.476};
    private double[] income1 = new double[]{0.53, 0.48, 0.47, 0.47, 0.49, 0.46, 0.5, 0.5, 0.17, 0.45, 0.465, 0.45, 0.46, 0.5, 0.5, 0.465, 0.48, 0.455};
    private double[] income2 = new double[]{0.27, 0.27, 0.26, 0.27, 0.27, 0.26, 0.27, 0.27, 0.47, 0.27, 0.26, 0.25, 0.265, 0.27, 0.28, 0.26, 0.265, 0.265};
    private double[] income3 = new double[]{0.13, 0.16, 0.17, 0.17, 0.16, 0.18, 0.15, 0.15, 0.28, 0.19, 0.18, 0.2, 0.185, 0.15, 0.15, 0.175, 0.165, 0.185};
    private double[] income4 = new double[]{0.07, 0.09, 0.1, 0.09, 0.08, 0.1, 0.08, 0.08, 0.17, 0.09, 0.095, 0.1, 0.09, 0.08, 0.07, 0.1, 0.09, 0.095};
    private double[] devicePc = new double[]{0.74, 0.76, 0.77, 0.78, 0.75, 0.76, 0.79, 0.78, 0.82, 0.81, 0.8, 0.81, 0.76, 0.72, 0.72, 0.7, 0.73, 0.69};
    private double[] deviceMobile = new double[]{0.26, 0.24, 0.23, 0.22, 0.25, 0.24, 0.21, 0.22, 0.18, 0.19, 0.2, 0.19, 0.24, 0.28, 0.28, 0.3, 0.27, 0.31};
    private double[] reservePriceDailyBaselineAverage = new double[]{0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1};
    private double[] reservePriceBaselineRange = new double[]{0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1};
    private double[] reservePriceUpdateCoeffecient = new double[]{0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6};

    public AdxConfigurationParser(ConfigManager config) {
        this.config = config;
    }

    public Map<AdType, Integer> createAdTypeDistributionMap() {
        HashMap<AdType, Integer> deviceDistribution = new HashMap<AdType, Integer>();
        deviceDistribution.put(AdType.text, this.config.getPropertyAsInt("adxusers.adtype.text", 0));
        deviceDistribution.put(AdType.video, this.config.getPropertyAsInt("adxusers.adtype.video", 0));
        return deviceDistribution;
    }

    public Map<Device, Integer> createDeviceDistributionMap() {
        HashMap<Device, Integer> deviceDistribution = new HashMap<Device, Integer>();
        deviceDistribution.put(Device.pc, this.config.getPropertyAsInt("adxusers.device.pc", 0));
        deviceDistribution.put(Device.mobile, this.config.getPropertyAsInt("adxusers.device.mobile", 0));
        return deviceDistribution;
    }

    public List<AdxUser> createUserPopulation() {
        HashMap<AdxUser, Integer> weights = new HashMap<AdxUser, Integer>();
        int populationSize = this.config.getPropertyAsInt("adxusers.population_size", 0);
        int populationTypesSize = this.config.getPropertyAsInt("population.types.size", 0);
        int i = 1;
        while (i <= populationTypesSize) {
            Age age = Age.valueOf(this.config.getProperty(String.format("population.%s.age", i)));
            Gender gender = Gender.valueOf(this.config.getProperty(String.format("population.%s.gender", i)));
            Income income = Income.valueOf(this.config.getProperty(String.format("population.%s.income", i)));
            int probability = this.config.getPropertyAsInt(String.format("population.%s.probability", i), 0);
            AdxUser adxUser = new AdxUser(age, gender, income, 0.0, 0);
            weights.put(adxUser, probability);
            ++i;
        }
        PopulationUserGenerator generator = new PopulationUserGenerator(weights);
        return generator.generate(populationSize);
    }

    public PublisherCatalog createPublisherCatalog() {
        Random r = new Random();
        PublisherCatalog catalog = new PublisherCatalog();
        String[] skus1 = this.config.getPropertyAsArray("publishers.list.1");
        String[] skus2 = this.config.getPropertyAsArray("publishers.list.2");
        String[] skus3 = this.config.getPropertyAsArray("publishers.list.3");
        HashSet<Integer> subsetskus = new HashSet<Integer>();
        int subsetsize = this.config.getPropertyAsInt("publishers.subset.size", 2);
        while (subsetskus.size() < subsetsize) {
            subsetskus.add(Integer.parseInt(skus1[r.nextInt(skus1.length)]));
        }
        while (subsetskus.size() < 2 * subsetsize) {
            subsetskus.add(Integer.parseInt(skus2[r.nextInt(skus2.length)]));
        }
        while (subsetskus.size() < 3 * subsetsize) {
            subsetskus.add(Integer.parseInt(skus3[r.nextInt(skus3.length)]));
        }
        for (Integer sku : subsetskus) {
            String name = this.names[sku];
            double rating = this.ratings[sku];
            AdAttributeProbabilityMaps adAttributeProbabilityMaps = this.extractAdTypeAffiliation(sku);
            AdxUserAttributeProbabilityMaps adxUserAttributeProbabilityMaps = this.extractUserAffiliation(sku);
            Map<Device, Double> deviceAffiliation = this.extractDeviceAffiliation(sku);
            UserAdTypeReservePriceManager reservePriceManager = this.extractReservePriceInfo(sku);
            AdxPublisher publisher = new AdxPublisher(adxUserAttributeProbabilityMaps, adAttributeProbabilityMaps, deviceAffiliation, rating, 0.0, reservePriceManager, name);
            catalog.addPublisher(publisher);
        }
        catalog.lock();
        return catalog;
    }

    private UserAdTypeReservePriceManager extractReservePriceInfo(Integer sku) {
        double dailyBaselineAverage = this.reservePriceDailyBaselineAverage[sku];
        double baselineRange = this.reservePriceBaselineRange[sku];
        double updateCoefficient = this.reservePriceUpdateCoeffecient[sku];
        UserAdTypeReservePriceManager reservePriceManager = new UserAdTypeReservePriceManager(dailyBaselineAverage, baselineRange, updateCoefficient);
        return reservePriceManager;
    }

    private Map<Device, Double> extractDeviceAffiliation(Integer sku) {
        HashMap<Device, Double> deviceDistribution = new HashMap<Device, Double>();
        deviceDistribution.put(Device.pc, this.devicePc[sku]);
        deviceDistribution.put(Device.mobile, this.deviceMobile[sku]);
        return deviceDistribution;
    }

    private AdxUserAttributeProbabilityMaps extractUserAffiliation(Integer sku) {
        Map<Age, Double> ageDistribution = this.extractAgeDistribution(sku);
        Map<Gender, Double> genderDistribution = this.extractGenderDistribution(sku);
        Map<Income, Double> incomeDistribution = this.extractIncomeDistribution(sku);
        AdxUserAttributeProbabilityMaps adxUserAttributeProbabilityMaps = new AdxUserAttributeProbabilityMaps(ageDistribution, genderDistribution, incomeDistribution);
        return adxUserAttributeProbabilityMaps;
    }

    private Map<Income, Double> extractIncomeDistribution(Integer sku) {
        HashMap<Income, Double> ageDistribution = new HashMap<Income, Double>();
        ageDistribution.put(Income.low, this.income1[sku]);
        ageDistribution.put(Income.medium, this.income2[sku]);
        ageDistribution.put(Income.high, this.income3[sku]);
        ageDistribution.put(Income.very_high, this.income3[sku]);
        return ageDistribution;
    }

    private Map<Gender, Double> extractGenderDistribution(Integer sku) {
        HashMap<Gender, Double> genderDistribution = new HashMap<Gender, Double>();
        double maleAffiliation = this.genderMale[sku];
        genderDistribution.put(Gender.male, maleAffiliation);
        genderDistribution.put(Gender.female, 1.0 - maleAffiliation);
        return genderDistribution;
    }

    private Map<Age, Double> extractAgeDistribution(Integer sku) {
        HashMap<Age, Double> ageDistribution = new HashMap<Age, Double>();
        ageDistribution.put(Age.Age_18_24, this.age1[sku]);
        ageDistribution.put(Age.Age_25_34, this.age2[sku]);
        ageDistribution.put(Age.Age_35_44, this.age3[sku]);
        ageDistribution.put(Age.Age_45_54, this.age4[sku]);
        ageDistribution.put(Age.Age_55_64, this.age5[sku]);
        ageDistribution.put(Age.Age_65_PLUS, this.age6[sku]);
        return ageDistribution;
    }

    private AdAttributeProbabilityMaps extractAdTypeAffiliation(Integer sku) {
        HashMap<AdType, Double> adTypeDistribution = new HashMap<AdType, Double>();
        adTypeDistribution.put(AdType.text, this.adTypeText[sku]);
        adTypeDistribution.put(AdType.video, this.adTypeVideo[sku]);
        AdAttributeProbabilityMaps adAttributeProbabilityMaps = new AdAttributeProbabilityMaps(adTypeDistribution);
        return adAttributeProbabilityMaps;
    }

    public void initializeAdvertisers(TACAdxSimulation tacAdxSimulation) {
        Random r = new Random();
        SimulationAgent[] adnetowrkAgents = tacAdxSimulation.getAgents(5);
        tacAdxSimulation.setAdvertiserInfoMap(new HashMap<String, AdvertiserInfo>());
        int i = 0;
        int n = adnetowrkAgents.length;
        while (i < n) {
            String agentAddress;
            tacAdxSimulation.getAdxAdvertiserAddresses()[i] = adnetowrkAgents[i].getAddress();
            SimulationAgent agent = adnetowrkAgents[i];
            tacAdxSimulation.getAdvertiserAddresses()[i] = agentAddress = agent.getAddress();
            AdvertiserInfo advertiserInfo = new AdvertiserInfo();
            advertiserInfo.setAdvertiserId(agentAddress);
            advertiserInfo.lock();
            tacAdxSimulation.getAdvertiserInfoMap().put(agentAddress, advertiserInfo);
            tacAdxSimulation.getBank().addAccount(agentAddress);
            ++i;
        }
    }
}

