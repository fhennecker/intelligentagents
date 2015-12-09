/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.report.adn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import tau.tac.adx.users.AdxUser;
import tau.tac.adx.users.properties.Age;
import tau.tac.adx.users.properties.Gender;
import tau.tac.adx.users.properties.Income;

public enum MarketSegment {
    MALE,
    FEMALE,
    YOUNG,
    OLD,
    LOW_INCOME,
    HIGH_INCOME;
    
    private static final Random RANDOM;
    private static int[] uc;
    private static Map<Set<MarketSegment>, Integer> segmentsUsersMap;
    private static List<Set<MarketSegment>> segmentsList;

    static {
        RANDOM = new Random();
        uc = new int[]{1836, 1795, 1980, 2401, 517, 808, 256, 407};
        segmentsUsersMap = MarketSegment.usersInMarketSegments();
        segmentsList = MarketSegment.marketSegments();
    }

    private MarketSegment(String string2, int n2) {
    }

    public static Set<MarketSegment> compundMarketSegment1(MarketSegment s1) {
        ArrayList<MarketSegment> marketSegments = new ArrayList<MarketSegment>();
        marketSegments.add(s1);
        return new HashSet<MarketSegment>(marketSegments);
    }

    public static Set<MarketSegment> compundMarketSegment2(MarketSegment s1, MarketSegment s2) {
        ArrayList<MarketSegment> marketSegments = new ArrayList<MarketSegment>();
        marketSegments.add(s1);
        marketSegments.add(s2);
        return new HashSet<MarketSegment>(marketSegments);
    }

    public static Set<MarketSegment> compundMarketSegment3(MarketSegment s1, MarketSegment s2, MarketSegment s3) {
        ArrayList<MarketSegment> marketSegments = new ArrayList<MarketSegment>();
        marketSegments.add(s1);
        marketSegments.add(s2);
        marketSegments.add(s3);
        return new HashSet<MarketSegment>(marketSegments);
    }

    public static Map<Set<MarketSegment>, Integer> usersInMarketSegments() {
        HashMap<Set<MarketSegment>, Integer> cmarketSegments = new HashMap<Set<MarketSegment>, Integer>();
        cmarketSegments.put(MarketSegment.compundMarketSegment1(FEMALE), uc[2] + uc[3] + uc[6] + uc[7]);
        cmarketSegments.put(MarketSegment.compundMarketSegment1(MALE), uc[0] + uc[1] + uc[4] + uc[5]);
        cmarketSegments.put(MarketSegment.compundMarketSegment1(YOUNG), uc[0] + uc[2] + uc[4] + uc[6]);
        cmarketSegments.put(MarketSegment.compundMarketSegment1(OLD), uc[1] + uc[3] + uc[5] + uc[7]);
        cmarketSegments.put(MarketSegment.compundMarketSegment1(LOW_INCOME), uc[0] + uc[1] + uc[2] + uc[3]);
        cmarketSegments.put(MarketSegment.compundMarketSegment1(HIGH_INCOME), uc[4] + uc[5] + uc[6] + uc[7]);
        cmarketSegments.put(MarketSegment.compundMarketSegment2(FEMALE, YOUNG), uc[2] + uc[6]);
        cmarketSegments.put(MarketSegment.compundMarketSegment2(FEMALE, OLD), uc[3] + uc[7]);
        cmarketSegments.put(MarketSegment.compundMarketSegment2(MALE, YOUNG), uc[0] + uc[4]);
        cmarketSegments.put(MarketSegment.compundMarketSegment2(MALE, OLD), uc[1] + uc[5]);
        cmarketSegments.put(MarketSegment.compundMarketSegment2(FEMALE, LOW_INCOME), uc[2] + uc[3]);
        cmarketSegments.put(MarketSegment.compundMarketSegment2(FEMALE, HIGH_INCOME), uc[6] + uc[7]);
        cmarketSegments.put(MarketSegment.compundMarketSegment2(MALE, LOW_INCOME), uc[0] + uc[1]);
        cmarketSegments.put(MarketSegment.compundMarketSegment2(MALE, HIGH_INCOME), uc[4] + uc[5]);
        cmarketSegments.put(MarketSegment.compundMarketSegment2(YOUNG, LOW_INCOME), uc[0] + uc[2]);
        cmarketSegments.put(MarketSegment.compundMarketSegment2(YOUNG, HIGH_INCOME), uc[4] + uc[6]);
        cmarketSegments.put(MarketSegment.compundMarketSegment2(OLD, LOW_INCOME), uc[1] + uc[3]);
        cmarketSegments.put(MarketSegment.compundMarketSegment2(OLD, HIGH_INCOME), uc[5] + uc[7]);
        cmarketSegments.put(MarketSegment.compundMarketSegment3(FEMALE, LOW_INCOME, YOUNG), uc[2]);
        cmarketSegments.put(MarketSegment.compundMarketSegment3(FEMALE, LOW_INCOME, OLD), uc[3]);
        cmarketSegments.put(MarketSegment.compundMarketSegment3(MALE, LOW_INCOME, YOUNG), uc[0]);
        cmarketSegments.put(MarketSegment.compundMarketSegment3(MALE, LOW_INCOME, OLD), uc[1]);
        cmarketSegments.put(MarketSegment.compundMarketSegment3(FEMALE, HIGH_INCOME, YOUNG), uc[6]);
        cmarketSegments.put(MarketSegment.compundMarketSegment3(FEMALE, HIGH_INCOME, OLD), uc[7]);
        cmarketSegments.put(MarketSegment.compundMarketSegment3(MALE, HIGH_INCOME, YOUNG), uc[4]);
        cmarketSegments.put(MarketSegment.compundMarketSegment3(MALE, HIGH_INCOME, OLD), uc[5]);
        return cmarketSegments;
    }

    public static List<Set<MarketSegment>> marketSegments() {
        ArrayList<Set<MarketSegment>> cmarketSegments = new ArrayList<Set<MarketSegment>>();
        cmarketSegments.add(MarketSegment.compundMarketSegment1(FEMALE));
        cmarketSegments.add(MarketSegment.compundMarketSegment1(MALE));
        cmarketSegments.add(MarketSegment.compundMarketSegment1(YOUNG));
        cmarketSegments.add(MarketSegment.compundMarketSegment1(OLD));
        cmarketSegments.add(MarketSegment.compundMarketSegment1(LOW_INCOME));
        cmarketSegments.add(MarketSegment.compundMarketSegment1(HIGH_INCOME));
        cmarketSegments.add(MarketSegment.compundMarketSegment2(FEMALE, YOUNG));
        cmarketSegments.add(MarketSegment.compundMarketSegment2(FEMALE, OLD));
        cmarketSegments.add(MarketSegment.compundMarketSegment2(MALE, YOUNG));
        cmarketSegments.add(MarketSegment.compundMarketSegment2(MALE, OLD));
        cmarketSegments.add(MarketSegment.compundMarketSegment2(FEMALE, LOW_INCOME));
        cmarketSegments.add(MarketSegment.compundMarketSegment2(FEMALE, HIGH_INCOME));
        cmarketSegments.add(MarketSegment.compundMarketSegment2(MALE, LOW_INCOME));
        cmarketSegments.add(MarketSegment.compundMarketSegment2(MALE, HIGH_INCOME));
        cmarketSegments.add(MarketSegment.compundMarketSegment2(YOUNG, LOW_INCOME));
        cmarketSegments.add(MarketSegment.compundMarketSegment2(YOUNG, HIGH_INCOME));
        cmarketSegments.add(MarketSegment.compundMarketSegment2(OLD, LOW_INCOME));
        cmarketSegments.add(MarketSegment.compundMarketSegment2(OLD, HIGH_INCOME));
        cmarketSegments.add(MarketSegment.compundMarketSegment3(FEMALE, LOW_INCOME, YOUNG));
        cmarketSegments.add(MarketSegment.compundMarketSegment3(FEMALE, LOW_INCOME, OLD));
        cmarketSegments.add(MarketSegment.compundMarketSegment3(MALE, LOW_INCOME, YOUNG));
        cmarketSegments.add(MarketSegment.compundMarketSegment3(MALE, LOW_INCOME, OLD));
        cmarketSegments.add(MarketSegment.compundMarketSegment3(FEMALE, HIGH_INCOME, YOUNG));
        cmarketSegments.add(MarketSegment.compundMarketSegment3(FEMALE, HIGH_INCOME, OLD));
        cmarketSegments.add(MarketSegment.compundMarketSegment3(MALE, HIGH_INCOME, YOUNG));
        cmarketSegments.add(MarketSegment.compundMarketSegment3(MALE, HIGH_INCOME, OLD));
        return cmarketSegments;
    }

    public static Set<MarketSegment> randomMarketSegment() {
        return segmentsList.get(RANDOM.nextInt(segmentsList.size()));
    }

    public static Set<MarketSegment> randomMarketSegment2() {
        return segmentsList.get(6 + RANDOM.nextInt(12));
    }

    public static Integer marketSegmentSize(Set<MarketSegment> segment) {
        return segmentsUsersMap.get(segment);
    }

    public static String names(Set<MarketSegment> segments) {
        String ret = new String();
        for (MarketSegment segment : segments) {
            ret = String.valueOf(ret) + " " + segment.name();
        }
        return ret;
    }

    public static Set<MarketSegment> extractSegment(AdxUser user) {
        HashSet<MarketSegment> marketSegments = new HashSet<MarketSegment>();
        if (user.getGender() == Gender.male) {
            marketSegments.add(MALE);
        } else {
            marketSegments.add(FEMALE);
        }
        if (user.getIncome() == Income.low || user.getIncome() == Income.medium) {
            marketSegments.add(LOW_INCOME);
        } else {
            marketSegments.add(HIGH_INCOME);
        }
        if (user.getAge() == Age.Age_18_24 || user.getAge() == Age.Age_25_34 || user.getAge() == Age.Age_35_44) {
            marketSegments.add(YOUNG);
        } else {
            marketSegments.add(OLD);
        }
        return marketSegments;
    }
}

