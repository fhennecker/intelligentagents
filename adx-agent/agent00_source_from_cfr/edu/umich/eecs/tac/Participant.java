/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac;

import edu.umich.eecs.tac.props.BankStatus;
import java.util.Comparator;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.logtool.ParticipantInfo;
import se.sics.tasim.props.StartInfo;

public class Participant {
    private ParticipantInfo info;
    private double totalResult;
    private double totalUCSCost;
    private double totalADXCost;
    private double totalRevenue;
    private long totalImpressions;
    private long totalClicks;
    private long totalConversions;
    private double qualityRating;
    private StartInfo startInfo;
    private int numberOfDays;
    private static Comparator<Participant> resultComparator;

    public double getQualityRating() {
        return this.qualityRating;
    }

    public void setQualityRating(double qualityRating) {
        this.qualityRating = qualityRating;
    }

    public Participant(ParticipantInfo info) {
        this.info = info;
    }

    public static Comparator getResultComparator() {
        if (resultComparator == null) {
            resultComparator = new Comparator<Participant>(){

                @Override
                public int compare(Participant o1, Participant o2) {
                    double r1 = o1.getResult();
                    double r2 = o2.getResult();
                    return - Double.compare(r1, r2);
                }

                @Override
                public boolean equals(Object obj) {
                    if (obj == this) {
                        return true;
                    }
                    return false;
                }
            };
        }
        return resultComparator;
    }

    public ParticipantInfo getInfo() {
        return this.info;
    }

    public StartInfo getStartInfo() {
        return this.startInfo;
    }

    public double getResult() {
        return this.totalResult;
    }

    public void setResult(double result) {
        this.totalResult = result;
    }

    public double getUCSCost() {
        return this.totalUCSCost;
    }

    public void setUCSCost(double Cost) {
        this.totalUCSCost = Cost;
    }

    public void addUCSCost(double Cost) {
        this.totalUCSCost += Cost;
    }

    public double getADXCost() {
        return this.totalADXCost;
    }

    public void setADXCost(double Cost) {
        this.totalADXCost = Cost;
    }

    public void addADXCost(double Cost) {
        this.totalADXCost += Cost;
    }

    public double getRevenue() {
        return this.totalRevenue;
    }

    public void setRevenue(double revenue) {
        this.totalRevenue = revenue;
    }

    public void addRevenue(double revenue) {
        this.totalRevenue += revenue;
    }

    public long getImpressions() {
        return this.totalImpressions;
    }

    public void setImpressions(long impressions) {
        this.totalImpressions = impressions;
    }

    public void addImpressions(long impressions) {
        this.totalImpressions += impressions;
    }

    public long getClicks() {
        return this.totalClicks;
    }

    public void setClicks(long clicks) {
        this.totalClicks = clicks;
    }

    public void addClicks(long clicks) {
        this.totalClicks += clicks;
    }

    public double getValuePerClick() {
        return this.getResult() / (double)this.getClicks();
    }

    public double getValuePerImpression() {
        return this.getResult() / (double)this.getImpressions();
    }

    public void messageReceived(int date, int sender, Transportable content) {
        if (!(content instanceof BankStatus) && content instanceof StartInfo) {
            this.startInfo = (StartInfo)content;
            this.numberOfDays = this.startInfo.getNumberOfDays();
        }
    }

    public void messageSent(int date, int receiver, Transportable content) {
    }

    public void messageSentToRole(int date, int role, Transportable content) {
    }

    public String toString() {
        return "Participant [info=" + this.info + ", totalResult=" + this.totalResult + ", totalUCSCost=" + this.totalUCSCost + ", totalADXCost=" + this.totalADXCost + ", totalRevenue=" + this.totalRevenue + ", totalImpressions=" + this.totalImpressions + ", totalClicks=" + this.totalClicks + ", totalConversions=" + this.totalConversions + ", qualityRating=" + this.qualityRating + ", startInfo=" + this.startInfo + ", numberOfDays=" + this.numberOfDays + "]";
    }

}

