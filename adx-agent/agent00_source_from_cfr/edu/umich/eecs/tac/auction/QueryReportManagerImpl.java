/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.auction;

import com.botbox.util.ArrayUtils;
import edu.umich.eecs.tac.auction.QueryReportManager;
import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.sim.QueryReportSender;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

public class QueryReportManagerImpl
implements QueryReportManager {
    protected Logger log = Logger.getLogger(QueryReportManagerImpl.class.getName());
    private String[] advertisers;
    private int advertisersCount;
    private QueryReport[] queryReports;
    private Map<Query, QueryClass> querySampler;
    private Query lastQuery;
    private boolean substitute = false;
    int randomNumber = -1;
    private QueryReportSender queryReportSender;

    public QueryReportManagerImpl(QueryReportSender queryReportSender, int advertisersCount) {
        this.queryReportSender = queryReportSender;
        this.advertisers = new String[advertisersCount];
        this.queryReports = new QueryReport[advertisersCount];
        this.advertisersCount = advertisersCount;
        this.querySampler = new HashMap<Query, QueryClass>();
        this.lastQuery = new Query();
        this.log.info("QueryReportManager reset");
    }

    @Override
    public void addAdvertiser(String name) {
        int index = ArrayUtils.indexOf(this.advertisers, 0, this.advertisersCount, name);
        if (index < 0) {
            this.doAddAccount(name);
        }
    }

    private synchronized int doAddAccount(String name) {
        if (this.advertisersCount == this.advertisers.length) {
            int newSize = this.advertisersCount + 8;
            this.advertisers = (String[])ArrayUtils.setSize(this.advertisers, newSize);
            this.queryReports = (QueryReport[])ArrayUtils.setSize(this.queryReports, newSize);
        }
        this.advertisers[this.advertisersCount] = name;
        return this.advertisersCount++;
    }

    protected void addClicks(String name, Query query, int clicks, double cost) {
        int index = ArrayUtils.indexOf(this.advertisers, 0, this.advertisersCount, name);
        if (index < 0) {
            index = this.doAddAccount(name);
        }
        if (this.queryReports[index] == null) {
            this.queryReports[index] = new QueryReport();
        }
        this.queryReports[index].addClicks(query, clicks, cost);
    }

    protected void addImpressions(String name, Query query, int regular, int promoted, Ad ad, double positionSum) {
        int index = ArrayUtils.indexOf(this.advertisers, 0, this.advertisersCount, name);
        if (index < 0) {
            index = this.doAddAccount(name);
        }
        if (this.queryReports[index] == null) {
            this.queryReports[index] = new QueryReport();
        }
        this.queryReports[index].addImpressions(query, regular, promoted, ad, positionSum);
    }

    @Override
    public void sendQueryReportToAll() {
        int i = 0;
        while (i < this.advertisersCount) {
            if (this.queryReports[i] == null) {
                this.queryReports[i] = new QueryReport();
            }
            ++i;
        }
        int advertiserIndex = 0;
        while (advertiserIndex < this.advertisersCount) {
            QueryReport baseReport = this.queryReports[advertiserIndex];
            String baseAdvertiser = this.advertisers[advertiserIndex];
            int index = 0;
            while (index < baseReport.size()) {
                Query query = baseReport.getQuery(index);
                double position = baseReport.getPosition(index);
                double sampleAvgPosition = -1.0;
                Ad ad = baseReport.getAd(index);
                sampleAvgPosition = this.querySampler.get(query) == null ? Double.NaN : (this.querySampler.get(query).getadMap().get(baseAdvertiser) == null ? Double.NaN : this.querySampler.get(query).getadMap().get(baseAdvertiser).average());
                int otherIndex = 0;
                while (otherIndex < this.advertisersCount) {
                    this.queryReports[otherIndex].setAdAndPosition(query, baseAdvertiser, ad, sampleAvgPosition);
                    ++otherIndex;
                }
                ++index;
            }
            ++advertiserIndex;
        }
        i = 0;
        while (i < this.advertisersCount) {
            QueryReport report = this.queryReports[i];
            this.queryReports[i] = null;
            this.queryReportSender.sendQueryReport(this.advertisers[i], report);
            int impressions = 0;
            int clicks = 0;
            int index = 0;
            while (index < report.size()) {
                impressions += report.getImpressions(index);
                clicks += report.getClicks(index);
                ++index;
            }
            this.queryReportSender.broadcastImpressions(this.advertisers[i], impressions);
            this.queryReportSender.broadcastClicks(this.advertisers[i], clicks);
            ++i;
        }
        this.querySampler = new HashMap<Query, QueryClass>();
    }

    protected void sampleQuery(Query newQuery, int slot, String advertiser) {
        int samplecount = 10;
        if (this.querySampler.containsKey(newQuery)) {
            QueryClass i = this.querySampler.get(newQuery);
            int count = i.getQueryClassCount();
            if (!this.lastQuery.equals(newQuery) || this.lastQuery.equals(newQuery) && slot == 1) {
                i.setQueryClassCount(++count);
                if (count > samplecount) {
                    this.randomNumber = new Random().nextInt(count) + 1;
                    if (this.randomNumber <= samplecount) {
                        this.substitute = true;
                        Iterator<String> iter = i.getadMap().keySet().iterator();
                        while (iter.hasNext()) {
                            i.getadMap().get(iter.next()).getPositions().remove(this.randomNumber);
                        }
                    } else {
                        this.substitute = false;
                    }
                }
            }
            if (count <= samplecount) {
                i.addtoadMap(advertiser, count, new Integer(slot));
                this.querySampler.put(newQuery, i);
            } else if (this.substitute) {
                i.addtoadMap(advertiser, this.randomNumber, new Integer(slot));
                this.querySampler.put(newQuery, i);
            }
        } else {
            QueryClass i = new QueryClass();
            i.setQueryClassCount(1);
            i.addtoadMap(advertiser, 1, new Integer(slot));
            this.querySampler.put(newQuery, i);
        }
        this.lastQuery = newQuery;
    }

    @Override
    public int size() {
        return this.advertisersCount;
    }

    @Override
    public void queryIssued(Query query) {
    }

    @Override
    public void viewed(Query query, Ad ad, int slot, String advertiser, boolean isPromoted) {
        this.sampleQuery(query, slot, advertiser);
        if (isPromoted) {
            this.addImpressions(advertiser, query, 0, 1, ad, slot);
        } else {
            this.addImpressions(advertiser, query, 1, 0, ad, slot);
        }
    }

    @Override
    public void clicked(Query query, Ad ad, int slot, double cpc, String advertiser) {
        this.addClicks(advertiser, query, 1, cpc);
    }

    @Override
    public void converted(Query query, Ad ad, int slot, double salesProfit, String advertiser) {
    }

    private class QueryClass {
        private int queryClassCount;
        private Map<String, MapQueryClassCounterToPositions> adMap;

        public QueryClass() {
            this.queryClassCount = 0;
            this.adMap = new HashMap<String, MapQueryClassCounterToPositions>();
        }

        public int getQueryClassCount() {
            return this.queryClassCount;
        }

        public Map<String, MapQueryClassCounterToPositions> getadMap() {
            return this.adMap;
        }

        public void setQueryClassCount(int count) {
            this.queryClassCount = count;
        }

        public void addtoadMap(String advertiserName, Integer count, Integer slot) {
            if (!this.adMap.containsKey(advertiserName)) {
                this.adMap.put(advertiserName, new MapQueryClassCounterToPositions(this, count, slot));
            } else {
                this.adMap.get(advertiserName).adPosition(count, slot);
            }
        }

        private class MapQueryClassCounterToPositions {
            private Map<Integer, Integer> positions;
            final /* synthetic */ QueryClass this$1;

            public MapQueryClassCounterToPositions(QueryClass queryClass) {
                this.this$1 = queryClass;
                this.positions = new HashMap<Integer, Integer>();
            }

            public MapQueryClassCounterToPositions(QueryClass queryClass, Integer queryClassCount, Integer slot) {
                this.this$1 = queryClass;
                this.positions = new HashMap<Integer, Integer>();
                this.positions.put(queryClassCount, slot);
            }

            public void adPosition(Integer queryClassCount, Integer slot) {
                this.positions.put(queryClassCount, slot);
            }

            public Map<Integer, Integer> getPositions() {
                return this.positions;
            }

            public double average() {
                double sum = 0.0;
                Iterator<Integer> it = this.positions.values().iterator();
                while (it.hasNext()) {
                    sum += (double)it.next().intValue();
                }
                return sum / (double)this.positions.size();
            }
        }

    }

}

