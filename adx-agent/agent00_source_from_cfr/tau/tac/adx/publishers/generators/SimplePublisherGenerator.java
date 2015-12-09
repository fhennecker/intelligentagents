/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.publishers.generators;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import tau.tac.adx.ads.properties.AdAttributeProbabilityMaps;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.devices.Device;
import tau.tac.adx.publishers.AdxPublisher;
import tau.tac.adx.publishers.generators.AdxPublisherGenerator;
import tau.tac.adx.publishers.reserve.UserAdTypeReservePriceManager;
import tau.tac.adx.users.AdxUser;
import tau.tac.adx.users.properties.AdxUserAttributeProbabilityMaps;
import tau.tac.adx.users.properties.Age;
import tau.tac.adx.users.properties.Gender;
import tau.tac.adx.users.properties.Income;
import tau.tac.adx.util.MapGenerator;

public class SimplePublisherGenerator
implements AdxPublisherGenerator {
    private static final double MAX_BASELINE_RANGE = 0.0;
    MapGenerator<AdType> adTypeMapGenerator = new MapGenerator();
    MapGenerator<Age> ageMapGenerator = new MapGenerator();
    MapGenerator<Device> deviceMapGenerator = new MapGenerator();
    MapGenerator<Gender> genderMapGenerator = new MapGenerator();
    MapGenerator<Income> incomeMapGenerator = new MapGenerator();
    private final Logger logger;
    MapGenerator<AdxPublisher> publisherMapGenerator;

    public SimplePublisherGenerator() {
        this.logger = Logger.getLogger(this.getClass().getCanonicalName());
        this.publisherMapGenerator = new MapGenerator();
    }

    @Override
    public Collection<AdxPublisher> generate(int amount) {
        LinkedList<AdxPublisher> publishers = new LinkedList<AdxPublisher>();
        int i = 0;
        while (i < amount) {
            publishers.add(this.getRandomPublisher());
            ++i;
        }
        this.normalizePublisherPopularity(publishers);
        this.logger.fine("Generated " + amount + " " + AdxUser.class.getName() + "s");
        return publishers;
    }

    private AdxPublisher getRandomPublisher() {
        AdxUserAttributeProbabilityMaps probabilityMaps = this.randomAttributeProbabilityMaps();
        AdAttributeProbabilityMaps adAttributeProbabilityMaps = this.randomAdAttributeProbabilityMaps();
        Map<Device, Double> deviceProbabilityMap = this.randomDeviceProbabilityMaps();
        double relativePopularity = Math.random();
        double pImpressions = Math.random();
        UserAdTypeReservePriceManager reservePriceManager = this.randomReservePriceManager();
        String name = this.randomName();
        AdxPublisher publisher = new AdxPublisher(probabilityMaps, adAttributeProbabilityMaps, deviceProbabilityMap, relativePopularity, pImpressions, reservePriceManager, name);
        return publisher;
    }

    private void normalizePublisherPopularity(Collection<AdxPublisher> publishers) {
        double sum = 0.0;
        for (AdxPublisher publisher2 : publishers) {
            sum += publisher2.getRelativePopularity();
        }
        for (AdxPublisher publisher2 : publishers) {
            publisher2.setRelativePopularity(publisher2.getRelativePopularity() / sum);
        }
    }

    private AdAttributeProbabilityMaps randomAdAttributeProbabilityMaps() {
        Map<AdType, Double> adTypeDistribution = this.adTypeMapGenerator.randomizeProbabilityMap((T[])AdType.values());
        AdAttributeProbabilityMaps probabilityMaps = new AdAttributeProbabilityMaps(adTypeDistribution);
        return probabilityMaps;
    }

    private AdxUserAttributeProbabilityMaps randomAttributeProbabilityMaps() {
        Map<Age, Double> ageDistribution = this.ageMapGenerator.randomizeProbabilityMap((T[])Age.values());
        Map<Gender, Double> genderDistribution = this.genderMapGenerator.randomizeProbabilityMap((T[])Gender.values());
        Map<Income, Double> incomeDistribution = this.incomeMapGenerator.randomizeProbabilityMap((T[])Income.values());
        AdxUserAttributeProbabilityMaps probabilityMaps = new AdxUserAttributeProbabilityMaps(ageDistribution, genderDistribution, incomeDistribution);
        return probabilityMaps;
    }

    private Map<Device, Double> randomDeviceProbabilityMaps() {
        Map<Device, Double> deviceProbabilityMap = this.deviceMapGenerator.randomizeProbabilityMap((T[])Device.values());
        return deviceProbabilityMap;
    }

    private String randomName() {
        Random random = new Random();
        return "Publisher-" + Math.abs(random.nextInt());
    }

    private UserAdTypeReservePriceManager randomReservePriceManager() {
        double dailyBaselineAverage = Math.random() * 100.0;
        double baselineRange = Math.random() * 0.0;
        double updateCoefficient = Math.random();
        UserAdTypeReservePriceManager reservePriceManager = new UserAdTypeReservePriceManager(dailyBaselineAverage, baselineRange, updateCoefficient);
        return reservePriceManager;
    }
}

