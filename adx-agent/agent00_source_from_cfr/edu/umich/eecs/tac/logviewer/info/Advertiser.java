/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.logviewer.info;

import edu.umich.eecs.tac.logviewer.info.Actor;
import edu.umich.eecs.tac.props.BidBundle;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.SalesReport;
import java.awt.Color;

public class Advertiser
extends Actor {
    private int[] balance;
    private Color color;
    private String manufacturerSpecialty;
    private String componentSpecialty;
    private int distributionCapacity;
    private int distributionWindow;
    private BidBundle[] bidBundle;
    private QueryReport[] queryReport;
    private SalesReport[] salesReport;

    public Advertiser(int simulationIndex, String address, String name, int numberOfDays, Color color) {
        super(simulationIndex, address, name);
        this.balance = new int[numberOfDays];
        this.bidBundle = new BidBundle[numberOfDays];
        this.queryReport = new QueryReport[numberOfDays + 1];
        this.salesReport = new SalesReport[numberOfDays + 1];
        this.color = color;
    }

    public void setManufacturerSpecialty(String specialty) {
        this.manufacturerSpecialty = specialty;
    }

    public void setComponentSpecialty(String specialty) {
        this.componentSpecialty = specialty;
    }

    public void setAccountBalance(int day, double accountBalance) {
        if (day < 0) {
            return;
        }
        this.balance[day < this.balance.length ? day : this.balance.length - 1] = (int)accountBalance;
    }

    public void setDistributionCapacity(int distributionCapacity) {
        this.distributionCapacity = distributionCapacity;
    }

    public void setBidBundle(BidBundle bundle, int day) {
        this.bidBundle[day] = bundle;
    }

    public void setQueryReport(QueryReport report, int day) {
        this.queryReport[day] = report;
    }

    public void setSalesReport(SalesReport report, int day) {
        this.salesReport[day] = report;
    }

    public String getManufacturerSpecialty() {
        return this.manufacturerSpecialty;
    }

    public String getComponentSpecialty() {
        return this.componentSpecialty;
    }

    public int getDistributionCapacity() {
        return this.distributionCapacity;
    }

    public int getAccountBalance(int day) {
        return this.balance[day];
    }

    public int[] getAccountBalance() {
        return this.balance;
    }

    public BidBundle[] getBidBundles() {
        return this.bidBundle;
    }

    public QueryReport[] getQueryReports() {
        return this.queryReport;
    }

    public SalesReport[] getSalesReports() {
        return this.salesReport;
    }

    public BidBundle getBidBundle(int day) {
        return this.bidBundle[day];
    }

    public QueryReport getQueryReport(int day) {
        return this.queryReport[day];
    }

    public SalesReport getSalesReport(int day) {
        return this.salesReport[day];
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getDistributionWindow() {
        return this.distributionWindow;
    }

    public void setDistributionWindow(int distributionWindow) {
        this.distributionWindow = distributionWindow;
    }
}

