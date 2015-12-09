/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.auction;

import com.botbox.util.ArrayUtils;
import edu.umich.eecs.tac.auction.SpendTracker;
import edu.umich.eecs.tac.props.Query;
import java.util.Arrays;

public class SpendTrackerImpl
implements SpendTracker {
    private String[] advertisers;
    private int advertisersCount;
    private QueryBudget[] queryBudget;

    public SpendTrackerImpl() {
        this(0);
    }

    public SpendTrackerImpl(int advertisersCount) {
        this.advertisersCount = advertisersCount;
        this.advertisers = new String[advertisersCount];
        this.queryBudget = new QueryBudget[advertisersCount];
    }

    @Override
    public void addAdvertiser(String advertiser) {
        int index = ArrayUtils.indexOf(this.advertisers, 0, this.advertisersCount, advertiser);
        if (index < 0) {
            this.doAddAdvertiser(advertiser);
        }
    }

    private synchronized int doAddAdvertiser(String advertiser) {
        if (this.advertisersCount == this.advertisers.length) {
            int newSize = this.advertisersCount + 8;
            this.advertisers = (String[])ArrayUtils.setSize(this.advertisers, newSize);
            this.queryBudget = (QueryBudget[])ArrayUtils.setSize(this.queryBudget, newSize);
        }
        this.advertisers[this.advertisersCount] = advertiser;
        return this.advertisersCount++;
    }

    @Override
    public void addCost(String advertiser, Query query, double cost) {
        int index = ArrayUtils.indexOf(this.advertisers, 0, this.advertisersCount, advertiser);
        if (index < 0) {
            index = this.doAddAdvertiser(advertiser);
        }
        if (this.queryBudget[index] == null) {
            this.queryBudget[index] = new QueryBudget(0);
        }
        this.queryBudget[index].addCost(query, cost);
    }

    @Override
    public double getDailyCost(String advertiser) {
        int index = ArrayUtils.indexOf(this.advertisers, 0, this.advertisersCount, advertiser);
        if (index < 0) {
            return 0.0;
        }
        if (this.queryBudget[index] == null) {
            this.queryBudget[index] = new QueryBudget(0);
        }
        return this.queryBudget[index].getTotalCost();
    }

    @Override
    public double getDailyCost(String advertiser, Query query) {
        int index = ArrayUtils.indexOf(this.advertisers, 0, this.advertisersCount, advertiser);
        if (index < 0) {
            return 0.0;
        }
        if (this.queryBudget[index] == null) {
            this.queryBudget[index] = new QueryBudget(0);
        }
        return this.queryBudget[index].getCost(query);
    }

    @Override
    public void reset() {
        QueryBudget[] arrqueryBudget = this.queryBudget;
        int n = arrqueryBudget.length;
        int n2 = 0;
        while (n2 < n) {
            QueryBudget budget = arrqueryBudget[n2];
            if (budget != null) {
                budget.reset();
            }
            ++n2;
        }
    }

    @Override
    public int size() {
        return this.advertisersCount;
    }

    private static class QueryBudget {
        private Query[] queries;
        private double[] cost;
        private int queryCount;
        private double totalCost;

        public QueryBudget(int queryCount) {
            this.queries = new Query[queryCount];
            this.cost = new double[queryCount];
            this.queryCount = queryCount;
        }

        private synchronized int doAddQuery(Query query) {
            if (this.queryCount == this.queries.length) {
                int newSize = this.queryCount + 8;
                this.queries = (Query[])ArrayUtils.setSize(this.queries, newSize);
                this.cost = ArrayUtils.setSize(this.cost, newSize);
            }
            this.queries[this.queryCount] = query;
            return this.queryCount++;
        }

        protected void addCost(Query query, double cost) {
            int index = ArrayUtils.indexOf(this.queries, 0, this.queryCount, query);
            if (index < 0) {
                index = this.doAddQuery(query);
            }
            double[] arrd = this.cost;
            int n = index;
            arrd[n] = arrd[n] + cost;
            this.totalCost += cost;
        }

        protected double getCost(Query query) {
            int index = ArrayUtils.indexOf(this.queries, 0, this.queryCount, query);
            if (index < 0) {
                return 0.0;
            }
            return this.cost[index];
        }

        protected double getTotalCost() {
            return this.totalCost;
        }

        public void reset() {
            Arrays.fill(this.cost, 0.0);
            this.totalCost = 0.0;
        }
    }

}
