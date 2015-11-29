/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.logviewer.info;

import edu.umich.eecs.tac.logviewer.info.Advertiser;
import edu.umich.eecs.tac.logviewer.util.SimulationParser;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.SlotInfo;
import edu.umich.eecs.tac.props.UserPopulationState;
import java.util.Set;

public class GameInfo {
    private String server;
    private int numberOfDays;
    private int simulationID;
    private String simulationType;
    private int secondsPerDay;
    private double squashingParameter;
    private Advertiser[] advertisers;
    private Set<Query> querySpace;
    private RetailCatalog catalog;
    private SlotInfo slotInfo;
    private UserPopulationState[] ups;

    public GameInfo(SimulationParser sp) {
        this.simulationID = sp.getSimulationID();
        this.simulationType = sp.getSimulationType();
        this.numberOfDays = sp.getNumberOfDays();
        this.secondsPerDay = sp.getSecondsPerDay();
        this.squashingParameter = sp.getSquashingParameter();
        this.server = sp.getServer();
        this.advertisers = sp.getAdvertisers();
        this.querySpace = sp.getQuerySpace();
        this.catalog = sp.getRetailCatalog();
        this.slotInfo = sp.getSlotInfo();
        this.ups = sp.getUserPopulationState();
    }

    public String getServer() {
        return this.server;
    }

    public int getSimulationID() {
        return this.simulationID;
    }

    public String getSimulationType() {
        return this.simulationType;
    }

    public int getSecondsPerDay() {
        return this.secondsPerDay;
    }

    public int getNumberOfDays() {
        return this.numberOfDays;
    }

    public int getAdvertiserCount() {
        return this.advertisers.length;
    }

    public double getSquashingParameter() {
        return this.squashingParameter;
    }

    public Set<Query> getQuerySpace() {
        return this.querySpace;
    }

    public RetailCatalog getRetailCatalog() {
        return this.catalog;
    }

    public SlotInfo getSlotInfo() {
        return this.slotInfo;
    }

    public Advertiser[] getAdvertisers() {
        return this.advertisers;
    }

    public Advertiser getAdvertiser(int index) {
        return this.advertisers[index];
    }

    public int getAdvertiserIndex(Advertiser m) {
        int i = 0;
        int n = this.advertisers.length;
        while (i < n) {
            if (m == this.advertisers[i]) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public final UserPopulationState getUserPopulationOnDay(int day) {
        try {
            return this.ups[day];
        }
        catch (Exception e) {
            return null;
        }
    }

    public final UserPopulationState[] getUserPopulationState() {
        return this.ups;
    }

    private boolean isValidDay(int day) {
        return day >= 0 && day <= this.numberOfDays;
    }
}

