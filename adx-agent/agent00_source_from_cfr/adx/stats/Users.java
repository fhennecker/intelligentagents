/*
 * Decompiled with CFR 0_110.
 */
package adx.stats;

import adx.query.IQueryClosure;
import adx.query.IQueryClosureAgg;
import adx.query.IQueryLValue;
import adx.query.IQueryOp;
import adx.query.IQueryRValue;
import adx.query.IQuerySelectAgg;
import adx.query.IQuerySelectStar;
import adx.query.Query;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.devices.Device;
import tau.tac.adx.report.adn.MarketSegment;
import tau.tac.adx.users.AdxUser;
import tau.tac.adx.users.properties.Age;
import tau.tac.adx.users.properties.Gender;
import tau.tac.adx.users.properties.Income;

public final class Users
extends Enum<Users> {
    public static final /* enum */ Users INSTANCE = new Users("INSTANCE", 0);
    protected AdxUserExt[] adxUsers = new AdxUserExt[]{new AdxUserExt(Age.Age_18_24, Gender.male, Income.low, 526), new AdxUserExt(Age.Age_18_24, Gender.male, Income.medium, 71), new AdxUserExt(Age.Age_25_34, Gender.male, Income.low, 371), new AdxUserExt(Age.Age_25_34, Gender.male, Income.medium, 322), new AdxUserExt(Age.Age_35_44, Gender.male, Income.low, 263), new AdxUserExt(Age.Age_35_44, Gender.male, Income.medium, 283), new AdxUserExt(Age.Age_18_24, Gender.male, Income.high, 11), new AdxUserExt(Age.Age_18_24, Gender.male, Income.very_high, 5), new AdxUserExt(Age.Age_25_34, Gender.male, Income.high, 140), new AdxUserExt(Age.Age_25_34, Gender.male, Income.very_high, 51), new AdxUserExt(Age.Age_35_44, Gender.male, Income.high, 185), new AdxUserExt(Age.Age_35_44, Gender.male, Income.very_high, 125), new AdxUserExt(Age.Age_18_24, Gender.female, Income.low, 546), new AdxUserExt(Age.Age_18_24, Gender.female, Income.medium, 52), new AdxUserExt(Age.Age_25_34, Gender.female, Income.low, 460), new AdxUserExt(Age.Age_25_34, Gender.female, Income.medium, 264), new AdxUserExt(Age.Age_35_44, Gender.female, Income.low, 403), new AdxUserExt(Age.Age_35_44, Gender.female, Income.medium, 255), new AdxUserExt(Age.Age_18_24, Gender.female, Income.high, 6), new AdxUserExt(Age.Age_18_24, Gender.female, Income.very_high, 3), new AdxUserExt(Age.Age_25_34, Gender.female, Income.high, 75), new AdxUserExt(Age.Age_25_34, Gender.female, Income.very_high, 21), new AdxUserExt(Age.Age_35_44, Gender.female, Income.high, 104), new AdxUserExt(Age.Age_35_44, Gender.female, Income.very_high, 47), new AdxUserExt(Age.Age_45_54, Gender.male, Income.low, 290), new AdxUserExt(Age.Age_45_54, Gender.male, Income.medium, 280), new AdxUserExt(Age.Age_55_64, Gender.male, Income.low, 284), new AdxUserExt(Age.Age_55_64, Gender.male, Income.medium, 245), new AdxUserExt(Age.Age_65_PLUS, Gender.male, Income.low, 461), new AdxUserExt(Age.Age_65_PLUS, Gender.male, Income.medium, 235), new AdxUserExt(Age.Age_45_54, Gender.male, Income.high, 197), new AdxUserExt(Age.Age_45_54, Gender.male, Income.very_high, 163), new AdxUserExt(Age.Age_55_64, Gender.male, Income.high, 157), new AdxUserExt(Age.Age_55_64, Gender.male, Income.very_high, 121), new AdxUserExt(Age.Age_65_PLUS, Gender.male, Income.high, 103), new AdxUserExt(Age.Age_65_PLUS, Gender.male, Income.very_high, 67), new AdxUserExt(Age.Age_45_54, Gender.female, Income.low, 457), new AdxUserExt(Age.Age_45_54, Gender.female, Income.medium, 275), new AdxUserExt(Age.Age_55_64, Gender.female, Income.low, 450), new AdxUserExt(Age.Age_55_64, Gender.female, Income.medium, 228), new AdxUserExt(Age.Age_65_PLUS, Gender.female, Income.low, 827), new AdxUserExt(Age.Age_65_PLUS, Gender.female, Income.medium, 164), new AdxUserExt(Age.Age_45_54, Gender.female, Income.high, 122), new AdxUserExt(Age.Age_45_54, Gender.female, Income.very_high, 57), new AdxUserExt(Age.Age_55_64, Gender.female, Income.high, 109), new AdxUserExt(Age.Age_55_64, Gender.female, Income.very_high, 48), new AdxUserExt(Age.Age_65_PLUS, Gender.female, Income.high, 53), new AdxUserExt(Age.Age_65_PLUS, Gender.female, Income.very_high, 18)};
    protected Object[][][] adxPreferences = new Object[][][]{{{"yahoo"}, {0.122, 0.171, 0.167, 0.184, 0.164, 0.192}, {0.53, 0.27, 0.13, 0.07}, {0.496, 0.504}, {0.26, 0.74}, {0.16}}, {{"cnn"}, {0.102, 0.161, 0.167, 0.194, 0.174, 0.202}, {0.48, 0.27, 0.16, 0.09}, {0.486, 0.514}, {0.24, 0.76}, {0.022}}, {{"nyt"}, {0.092, 0.151, 0.167, 0.194, 0.174, 0.222}, {0.47, 0.26, 0.17, 0.1}, {0.476, 0.524}, {0.23, 0.77}, {0.031}}, {{"hfn"}, {0.102, 0.161, 0.167, 0.194, 0.174, 0.202}, {0.47, 0.27, 0.17, 0.09}, {0.466, 0.534}, {0.22, 0.78}, {0.081}}, {{"msn"}, {0.102, 0.161, 0.167, 0.194, 0.174, 0.202}, {0.49, 0.27, 0.16, 0.08}, {0.476, 0.524}, {0.25, 0.75}, {0.182}}, {{"fox"}, {0.092, 0.151, 0.167, 0.194, 0.184, 0.212}, {0.46, 0.26, 0.18, 0.1}, {0.486, 0.514}, {0.24, 0.76}, {0.031}}, {{"amazon"}, {0.092, 0.151, 0.167, 0.194, 0.184, 0.212}, {0.5, 0.27, 0.15, 0.08}, {0.476, 0.524}, {0.21, 0.79}, {0.128}}, {{"ebay"}, {0.092, 0.161, 0.157, 0.194, 0.174, 0.222}, {0.5, 0.27, 0.15, 0.08}, {0.486, 0.514}, {0.22, 0.78}, {0.085}}, {{"wallmart"}, {0.072, 0.151, 0.167, 0.204, 0.184, 0.222}, {0.47, 0.28, 0.19, 0.06}, {0.456, 0.544}, {0.18, 0.82}, {0.038}}, {{"target"}, {0.092, 0.171, 0.177, 0.184, 0.174, 0.202}, {0.45, 0.27, 0.19, 0.09}, {0.456, 0.544}, {0.19, 0.81}, {0.02}}, {{"bestbuy"}, {0.102, 0.141, 0.167, 0.204, 0.174, 0.212}, {0.465, 0.26, 0.18, 0.095}, {0.476, 0.524}, {0.2, 0.8}, {0.016}}, {{"sears"}, {0.092, 0.121, 0.167, 0.204, 0.184, 0.232}, {0.45, 0.25, 0.2, 0.1}, {0.466, 0.534}, {0.19, 0.81}, {0.016}}, {{"webmd"}, {0.092, 0.151, 0.157, 0.194, 0.184, 0.222}, {0.46, 0.265, 0.185, 0.09}, {0.456, 0.544}, {0.24, 0.76}, {0.025}}, {{"ehow"}, {0.102, 0.151, 0.157, 0.194, 0.174, 0.222}, {0.5, 0.27, 0.15, 0.08}, {0.476, 0.524}, {0.28, 0.72}, {0.025}}, {{"ask"}, {0.102, 0.131, 0.157, 0.204, 0.184, 0.222}, {0.5, 0.28, 0.15, 0.07}, {0.486, 0.514}, {0.28, 0.72}, {0.05}}, {{"tripadvisor"}, {0.082, 0.161, 0.177, 0.204, 0.174, 0.202}, {0.465, 0.26, 0.175, 0.1}, {0.466, 0.534}, {0.3, 0.7}, {0.016}}, {{"cnet"}, {0.122, 0.151, 0.157, 0.184, 0.174, 0.212}, {0.48, 0.265, 0.165, 0.09}, {0.506, 0.494}, {0.27, 0.73}, {0.017}}, {{"weather"}, {0.092, 0.151, 0.167, 0.204, 0.184, 0.202}, {0.455, 0.265, 0.185, 0.095}, {0.476, 0.524}, {0.31, 0.69}, {0.058}}};
    protected Map<String, Double[]> adTypePreferences = new HashMap<String, Double[]>();
    protected Object[][][] preferences;
    protected List<Object[]> users = new LinkedList<Object[]>();
    private static final /* synthetic */ Users[] ENUM$VALUES;

    static {
        ENUM$VALUES = new Users[0];
    }

    private Users(String string2, int n2) {
        super(string, n);
    }

    public void initialize(Collection<String> publishers) {
        this.adTypePreferences.clear();
        this.preferences = new Object[publishers.size()][][];
        int i = 0;
        Object[][][] arrobject = this.adxPreferences;
        int n = arrobject.length;
        int n2 = 0;
        while (n2 < n) {
            Object[][] preference = arrobject[n2];
            if (publishers.contains(preference[0][0])) {
                this.preferences[i++] = preference;
                this.adTypePreferences.put((String)preference[0][0], new Double[]{0.33, 0.67, 0.0, 0.0});
            }
            ++n2;
        }
        this.users.clear();
        arrobject = this.adxUsers;
        n = arrobject.length;
        n2 = 0;
        while (n2 < n) {
            Object[][] user = arrobject[n2];
            Device[] arrdevice = Device.values();
            int n3 = arrdevice.length;
            int n4 = 0;
            while (n4 < n3) {
                Device device = arrdevice[n4];
                double complete = 0.0;
                LinkedList<Object[]> userEntries = new LinkedList<Object[]>();
                Object[][][] arrobject2 = this.preferences;
                int n5 = arrobject2.length;
                int n6 = 0;
                while (n6 < n5) {
                    Object[][] preference = arrobject2[n6];
                    double userProb = (Double)preference[1][user.getAge().ordinal()] * (Double)preference[2][user.getIncome().ordinal()] * (Double)preference[3][user.getGender().ordinal()] * (Double)preference[5][0];
                    Object[] entry = new Object[]{user.getSegments(), user.getAge(), user.getGender(), user.getIncome(), preference[0][0], device, userProb * (Double)preference[4][device.ordinal()]};
                    userEntries.add(entry);
                    this.users.add(entry);
                    complete += userProb;
                    ++n6;
                }
                for (Object[] entry : userEntries) {
                    entry[6] = (double)user.getCount() * (Double)entry[6] / complete;
                }
                ++n4;
            }
            ++n2;
        }
    }

    public void updateAdTypePreferences(Iterable<Object[]> preferences) {
        for (Object[] preference : preferences) {
            Double[] ratios;
            Double[] arrdouble = ratios = this.adTypePreferences.get((String)preference[0]);
            arrdouble[2] = arrdouble[2] + (Double)preference[1];
            Double[] arrdouble2 = ratios;
            arrdouble2[3] = arrdouble2[3] + (Double)preference[2];
            ratios[0] = ratios[2] / ratios[3];
            ratios[1] = 1.0 - ratios[0];
        }
    }

    public Iterable<Object[]> generatePriorities(Set<MarketSegment> segments) {
        LinkedList<Object[]> priorities = new LinkedList<Object[]>();
        for (Object entry : ((IQueryClosure)Query.select(this.users).index(1).index(2).index(3).index(4).index(5).index(6).where().index(0).contains().value(segments)).exec()) {
            Object[] entryArr = (Object[])entry;
            AdType[] arradType = AdType.values();
            int n = arradType.length;
            int n2 = 0;
            while (n2 < n) {
                AdType adType = arradType[n2];
                priorities.add(new Object[]{entryArr[0], entryArr[1], entryArr[2], entryArr[3], entryArr[4], adType, (Double)entryArr[5] * this.adTypePreferences.get((String)entryArr[3])[adType.ordinal()]});
                ++n2;
            }
        }
        return priorities;
    }

    public double countImpressions(Set<MarketSegment> segments, double mobileC, double videoC) {
        Iterable<Object[]> uniqueImpressions = this.generatePriorities(segments);
        double impressions = 0.0;
        for (Object[] i : uniqueImpressions) {
            impressions += ((Device)((Object)i[4]) == Device.mobile ? mobileC : 1.0) * ((AdType)((Object)i[5]) == AdType.video ? videoC : 1.0) * (Double)i[6];
        }
        return impressions;
    }

    public long countSegments(Set<MarketSegment> segments) {
        return ((Integer)((Object[])((IQueryClosureAgg)Query.select(Arrays.asList(this.adxUsers)).property("count").sum().where().property("segments").contains().value(segments)).exec().iterator().next())[0]).intValue();
    }

    public static Users getInstance() {
        return INSTANCE;
    }

    public static Users[] values() {
        Users[] arrusers = ENUM$VALUES;
        int n = arrusers.length;
        Users[] arrusers2 = new Users[n];
        System.arraycopy(arrusers, 0, arrusers2, 0, n);
        return arrusers2;
    }

    public static Users valueOf(String string) {
        return (Users)((Object)Enum.valueOf(Users.class, string));
    }

    protected static class AdxUserExt
    extends AdxUser {
        protected Set<MarketSegment> segments;
        protected int count;

        public AdxUserExt(Age age, Gender gender, Income income, int count) {
            super(age, gender, income, 0.0, 0);
            this.segments = MarketSegment.extractSegment(this);
            this.count = count;
        }

        public Set<MarketSegment> getSegments() {
            return this.segments;
        }

        public void setSegments(Set<MarketSegment> segments) {
            this.segments = segments;
        }

        public int getCount() {
            return this.count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

}

