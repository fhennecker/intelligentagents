/*
 * Decompiled with CFR 0_110.
 */
package adx.stats;

import adx.query.IQueryClosureAgg;
import adx.query.IQueryGrouper;
import adx.query.IQueryGrouperStar;
import adx.query.IQueryLValue;
import adx.query.IQueryOp;
import adx.query.IQueryRValue;
import adx.query.IQuerySelectAgg;
import adx.query.IQuerySelectStar;
import adx.query.Query;
import adx.stats.CampaignSegmentTracker;
import adx.stats.LoadBalancer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.devices.Device;
import tau.tac.adx.report.adn.AdNetworkKey;
import tau.tac.adx.report.adn.AdNetworkReportEntry;
import tau.tac.adx.report.adn.MarketSegment;
import tau.tac.adx.users.AdxUser;
import tau.tac.adx.users.properties.Age;
import tau.tac.adx.users.properties.Gender;
import tau.tac.adx.users.properties.Income;

public final class Neuro
extends Enum<Neuro> {
    public static final /* enum */ Neuro INSTANCE = new Neuro("INSTANCE", 0);
    protected final int numKnownAgents = 10;
    protected Map<String, Integer> publishers = new HashMap<String, Integer>();
    protected double[][][] ages = new double[Age.values().length][10][10];
    protected double[][][] genders = new double[Gender.values().length][10][10];
    protected double[][][] incomes = new double[Income.values().length][10][10];
    protected double[][][] devices = new double[Device.values().length][10][10];
    protected double[][][] types = new double[AdType.values().length][10][10];
    protected double[][][] pubs;
    private static final /* synthetic */ Neuro[] ENUM$VALUES;

    static {
        ENUM$VALUES = new Neuro[0];
    }

    private Neuro(String string2, int n2) {
        super(string, n);
    }

    public void initialize(Collection<String> publishers) {
        double[][][][] maps;
        this.publishers.clear();
        for (String publisher : publishers) {
            this.publishers.put(publisher, this.publishers.size());
        }
        this.pubs = new double[publishers.size()][10][10];
        double[][][][] arrarrd = maps = new double[][][][]{this.ages, this.genders, this.incomes, this.devices, this.types, this.pubs};
        int n = arrarrd.length;
        int n2 = 0;
        while (n2 < n) {
            double[][][] m1;
            double[][][] arrd = m1 = arrarrd[n2];
            int n3 = arrd.length;
            int n4 = 0;
            while (n4 < n3) {
                double[][] m2;
                double[][] arrd2 = m2 = arrd[n4];
                int n5 = arrd2.length;
                int n6 = 0;
                while (n6 < n5) {
                    double[] m3 = arrd2[n6];
                    Arrays.fill(m3, 0.0);
                    ++n6;
                }
                ++n4;
            }
            ++n2;
        }
    }

    public void update(Iterable<AdNetworkReportEntry> entries, Map<Integer, Boolean> finished) {
        for (AdNetworkReportEntry entry : entries) {
            AdNetworkKey key = (AdNetworkKey)entry.getKey();
            double[][][] maps = new double[][][]{this.ages[key.getAge().ordinal()], this.genders[key.getGender().ordinal()], this.incomes[key.getIncome().ordinal()], this.devices[key.getDevice().ordinal()], this.types[key.getAdType().ordinal()], this.pubs[this.publishers.get(key.getPublisher())]};
            Set<MarketSegment> segments = MarketSegment.extractSegment(new AdxUser(key.getAge(), key.getGender(), key.getIncome(), 0.0, 0));
            Iterable<Object> agents = ((IQueryClosureAgg)Query.select(LoadBalancer.getInstance().get(segments)).property("agent").property("agent").count().where().property("agent").neq().value("self")).groupBy().property("agent").exec();
            double x = (double)entry.getWinCount() / (double)entry.getBidCount();
            if (finished.get(key.getCampaignId()).booleanValue()) {
                x = 1.0;
            }
            int unit = x > 0.95 ? 1 : -1;
            for (Object agentEntry : agents) {
                String agent = (String)((Object[])agentEntry)[0];
                int a = LoadBalancer.getInstance().getAgentId(agent);
                double[][][] arrarrd = maps;
                int n = arrarrd.length;
                int n2 = 0;
                while (n2 < n) {
                    double[][] m = arrarrd[n2];
                    int i = 0;
                    while (i < a) {
                        double[] arrd = m[a];
                        int n3 = i++;
                        arrd[n3] = arrd[n3] + (double)unit;
                    }
                    i = a;
                    while (i < 10) {
                        double[] arrd = m[i];
                        int n4 = a;
                        arrd[n4] = arrd[n4] + (double)unit;
                        ++i;
                    }
                    ++n2;
                }
            }
        }
    }

    public double test(Age age, Gender gender, Income income, Device device, AdType adType, String publisher) {
        double[][][] maps = new double[][][]{this.ages[age.ordinal()], this.genders[gender.ordinal()], this.incomes[income.ordinal()], this.devices[device.ordinal()], this.types[adType.ordinal()], this.pubs[this.publishers.get(publisher)]};
        Set<MarketSegment> segments = MarketSegment.extractSegment(new AdxUser(age, gender, income, 0.0, 0));
        Iterable<Object> agents = ((IQueryClosureAgg)Query.select(LoadBalancer.getInstance().get(segments)).property("agent").property("agent").count().where().property("agent").neq().value("self")).and().property("daysLeft").gt().value(0).groupBy().property("agent").exec();
        double factor = 1.0;
        if (Query.count(agents) == 0) {
            double[][][] arrarrd = maps;
            int n = arrarrd.length;
            int n2 = 0;
            while (n2 < n) {
                double[][] m = arrarrd[n2];
                double max = 0.0;
                int i = 0;
                while (i < 10) {
                    int j = 0;
                    while (j <= i) {
                        double value = m[i][j];
                        if (value > max) {
                            max = value;
                        }
                        ++j;
                    }
                    ++i;
                }
                factor *= Math.atan(max);
                ++n2;
            }
            return Math.pow(factor, 0.16666666666666666) * 2.0 / 3.141592653589793 * 0.2 + 1.0;
        }
        double[][][] arrarrd = maps;
        int n = arrarrd.length;
        int n3 = 0;
        while (n3 < n) {
            double[][] m = arrarrd[n3];
            double max = 0.0;
            for (Object a0 : agents) {
                for (Object b0 : agents) {
                    double value;
                    int b1;
                    int a1 = LoadBalancer.getInstance().getAgentId((String)((Object[])a0)[0]);
                    if (a1 < (b1 = LoadBalancer.getInstance().getAgentId((String)((Object[])b0)[0])) || (value = m[a1][b1]) <= max) continue;
                    max = value;
                }
            }
            factor *= Math.atan(max);
            ++n3;
        }
        return Math.pow(factor, 0.16666666666666666) * 2.0 / 3.141592653589793 * 0.25 + 1.0;
    }

    public static Neuro getInstance() {
        return INSTANCE;
    }

    public static Neuro[] values() {
        Neuro[] arrneuro = ENUM$VALUES;
        int n = arrneuro.length;
        Neuro[] arrneuro2 = new Neuro[n];
        System.arraycopy(arrneuro, 0, arrneuro2, 0, n);
        return arrneuro2;
    }

    public static Neuro valueOf(String string) {
        return (Neuro)((Object)Enum.valueOf(Neuro.class, string));
    }
}

