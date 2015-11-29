/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.sim;

import com.botbox.util.ArrayUtils;
import edu.umich.eecs.tac.props.BankStatus;
import edu.umich.eecs.tac.sim.BankStatusSender;
import java.util.logging.Logger;
import se.sics.tasim.is.SimulationInfo;

public class Bank {
    private BankStatusSender bankStatusSender;
    private String[] accountNames;
    private double[] accountAmounts;
    private int accountNumber;
    private SimulationInfo simulationInfo;

    public Bank(BankStatusSender bankStatusSender, SimulationInfo simulationInfo, int accountNumber) {
        this.bankStatusSender = bankStatusSender;
        this.accountNames = new String[accountNumber];
        this.accountAmounts = new double[accountNumber];
        this.simulationInfo = simulationInfo;
    }

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
            this.accountAmounts = ArrayUtils.setSize(this.accountAmounts, newSize);
        }
        this.accountNames[this.accountNumber] = name;
        this.accountAmounts[this.accountNumber] = 0.0;
        return this.accountNumber++;
    }

    public double getAccountStatus(String name) {
        int index = ArrayUtils.indexOf(this.accountNames, 0, this.accountNumber, name);
        return index >= 0 ? this.accountAmounts[index] : 0.0;
    }

    public double deposit(String name, double amount) {
        int index = ArrayUtils.indexOf(this.accountNames, 0, this.accountNumber, name);
        if (index < 0) {
            index = this.doAddAccount(name);
        }
        double[] arrd = this.accountAmounts;
        int n = index;
        arrd[n] = arrd[n] + amount;
        return this.accountAmounts[index];
    }

    public double withdraw(String name, double amount) {
        return this.deposit(name, - amount);
    }

    public void sendBankStatusToAll() {
        int i = 0;
        while (i < this.accountNumber) {
            BankStatus status = new BankStatus();
            status.setAccountBalance(this.accountAmounts[i]);
            this.bankStatusSender.sendBankStatus(this.accountNames[i], status);
            ++i;
        }
    }

    protected void finalize() throws Throwable {
        Logger.global.info("BANK FOR SIMULATION " + this.simulationInfo.getSimulationID() + " IS BEING GARBAGED");
        super.finalize();
    }
}

