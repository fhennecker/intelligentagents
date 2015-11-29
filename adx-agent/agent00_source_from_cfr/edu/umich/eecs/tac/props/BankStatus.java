/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.AbstractTransportable;
import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

public class BankStatus
extends AbstractTransportable {
    private static final long serialVersionUID = -6576269032652384128L;
    private double balance;

    public BankStatus() {
        this.balance = 0.0;
    }

    public BankStatus(double b) {
        this.balance = b;
    }

    public final double getAccountBalance() {
        return this.balance;
    }

    public final void setAccountBalance(double b) {
        this.lockCheck();
        this.balance = b;
    }

    public final String toString() {
        return String.format("%s[%f]", this.getTransportName(), this.balance);
    }

    @Override
    protected final void readWithLock(TransportReader reader) throws ParseException {
        this.balance = reader.getAttributeAsDouble("balance", 0.0);
    }

    @Override
    protected final void writeWithLock(TransportWriter writer) {
        writer.attr("balance", this.balance);
    }
}

