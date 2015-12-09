/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.sim;

import com.botbox.util.ArrayUtils;
import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.AdvertiserInfo;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.SalesReport;
import edu.umich.eecs.tac.sim.AgentRepository;
import edu.umich.eecs.tac.sim.SalesAnalyst;
import edu.umich.eecs.tac.sim.SalesReportSender;
import java.util.Arrays;
import java.util.Map;

public class DefaultSalesAnalyst
implements SalesAnalyst {
    private final AgentRepository agentRepository;
    private final SalesReportSender salesReportSender;
    private String[] accountNames;
    private int[][] accountConversions;
    private SalesReport[] salesReports;
    private int accountNumber;

    public DefaultSalesAnalyst(AgentRepository agentRepository, SalesReportSender salesReportSender, int accountNumber) {
        if (agentRepository == null) {
            throw new NullPointerException("Agent repository cannot be null");
        }
        this.agentRepository = agentRepository;
        if (salesReportSender == null) {
            throw new NullPointerException("Sales report sender cannot be null");
        }
        this.salesReportSender = salesReportSender;
        this.accountNames = new String[accountNumber];
        this.accountConversions = new int[accountNumber][];
        this.salesReports = new SalesReport[accountNumber];
    }

    @Override
    public void addAccount(String name) {
        int index = ArrayUtils.indexOf(this.accountNames, 0, this.accountNumber, name);
        if (index < 0) {
            this.doAddAccount(name);
        }
    }

    private synchronized int doAddAccount(String name) {
        if (this.accountNumber == this.accountNames.length) {
            int newSize = this.accountNumber + 8;
            this.accountNames = (String[])ArrayUtils.setSize(this.accountNames, newSize);
            this.accountConversions = (int[][])ArrayUtils.setSize((Object[])this.accountConversions, newSize);
            this.salesReports = (SalesReport[])ArrayUtils.setSize(this.salesReports, newSize);
        }
        this.accountNames[this.accountNumber] = name;
        this.accountConversions[this.accountNumber] = new int[this.getAdvertiserInfo().get(name).getDistributionWindow()];
        return this.accountNumber++;
    }

    @Override
    public double getRecentConversions(String name) {
        int index = ArrayUtils.indexOf(this.accountNames, 0, this.accountNumber, name);
        return index >= 0 ? this.sum(this.accountConversions[index]) : 0;
    }

    private int sum(int[] array) {
        int sum = 0;
        if (array != null) {
            int[] arrn = array;
            int n = arrn.length;
            int n2 = 0;
            while (n2 < n) {
                int value = arrn[n2];
                sum += value;
                ++n2;
            }
        }
        return sum;
    }

    protected int addConversions(String name, Query query, int conversions, double amount) {
        int queryIndex;
        int index = ArrayUtils.indexOf(this.accountNames, 0, this.accountNumber, name);
        if (index < 0) {
            index = this.doAddAccount(name);
        }
        if (this.accountConversions[index] == null) {
            AdvertiserInfo advertiserInfo = this.getAdvertiserInfo().get(name);
            this.accountConversions[index] = new int[advertiserInfo.getDistributionWindow()];
            int defaultConversions = advertiserInfo.getDistributionCapacity() / advertiserInfo.getDistributionWindow();
            Arrays.fill(this.accountConversions[index], defaultConversions);
        }
        int[] arrn = this.accountConversions[index];
        arrn[0] = arrn[0] + conversions;
        if (this.salesReports[index] == null) {
            this.salesReports[index] = new SalesReport();
        }
        if ((queryIndex = this.salesReports[index].indexForEntry(query)) < 0) {
            queryIndex = this.salesReports[index].addQuery(query);
        }
        this.salesReports[index].addConversions(queryIndex, conversions);
        this.salesReports[index].addRevenue(queryIndex, amount);
        return this.accountConversions[index][0];
    }

    @Override
    public void sendSalesReportToAll() {
        int i = 0;
        while (i < this.accountNumber) {
            SalesReport report = this.salesReports[i];
            if (report == null) {
                report = new SalesReport();
            } else {
                this.salesReports[i] = null;
            }
            this.salesReportSender.sendSalesReport(this.accountNames[i], report);
            this.salesReportSender.broadcastConversions(this.accountNames[i], this.accountConversions[i][0]);
            ++i;
        }
        this.updateConversionQueue();
    }

    private void updateConversionQueue() {
        int i = 0;
        while (i < this.accountConversions.length) {
            int j = this.accountConversions[i].length - 2;
            while (j >= 0) {
                this.accountConversions[i][j + 1] = this.accountConversions[i][j];
                --j;
            }
            this.accountConversions[i][0] = 0;
            ++i;
        }
    }

    @Override
    public void queryIssued(Query query) {
    }

    @Override
    public void viewed(Query query, Ad ad, int slot, String advertiser, boolean isPromoted) {
    }

    @Override
    public void clicked(Query query, Ad ad, int slot, double cpc, String advertiser) {
    }

    @Override
    public void converted(Query query, Ad ad, int slot, double salesProfit, String advertiser) {
        this.addConversions(advertiser, query, 1, salesProfit);
    }

    protected Map<String, AdvertiserInfo> getAdvertiserInfo() {
        return this.agentRepository.getAdvertiserInfo();
    }

    public int size() {
        return this.accountNumber;
    }
}

