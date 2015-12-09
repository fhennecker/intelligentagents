/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.AbstractTransportable;
import edu.umich.eecs.tac.props.QueryType;
import java.text.ParseException;
import java.util.Arrays;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

public class ReserveInfo
extends AbstractTransportable {
    private double[] promotedReserve = new double[QueryType.values().length];
    private double[] regularReserve = new double[QueryType.values().length];

    public final double getPromotedReserve(QueryType queryType) {
        return this.promotedReserve[queryType.ordinal()];
    }

    public final void setPromotedReserve(QueryType queryType, double promotedReserve) {
        this.lockCheck();
        this.promotedReserve[queryType.ordinal()] = promotedReserve;
    }

    public final double getRegularReserve(QueryType queryType) {
        return this.regularReserve[queryType.ordinal()];
    }

    public final void setRegularReserve(QueryType queryType, double regularReserve) {
        this.lockCheck();
        this.regularReserve[queryType.ordinal()] = regularReserve;
    }

    @Override
    protected final void readWithLock(TransportReader reader) throws ParseException {
        QueryType type;
        QueryType[] arrqueryType = QueryType.values();
        int n = arrqueryType.length;
        int n2 = 0;
        while (n2 < n) {
            type = arrqueryType[n2];
            this.promotedReserve[type.ordinal()] = reader.getAttributeAsDouble(String.format("promotedReserve[%s]", type.name()), 0.0);
            ++n2;
        }
        arrqueryType = QueryType.values();
        n = arrqueryType.length;
        n2 = 0;
        while (n2 < n) {
            type = arrqueryType[n2];
            this.regularReserve[type.ordinal()] = reader.getAttributeAsDouble(String.format("regularReserve[%s]", type.name()), 0.0);
            ++n2;
        }
    }

    @Override
    protected final void writeWithLock(TransportWriter writer) {
        QueryType type;
        QueryType[] arrqueryType = QueryType.values();
        int n = arrqueryType.length;
        int n2 = 0;
        while (n2 < n) {
            type = arrqueryType[n2];
            writer.attr(String.format("promotedReserve[%s]", type.name()), this.promotedReserve[type.ordinal()]);
            ++n2;
        }
        arrqueryType = QueryType.values();
        n = arrqueryType.length;
        n2 = 0;
        while (n2 < n) {
            type = arrqueryType[n2];
            writer.attr(String.format("regularReserve[%s]", type.name()), this.regularReserve[type.ordinal()]);
            ++n2;
        }
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ReserveInfo that = (ReserveInfo)o;
        if (!Arrays.equals(this.promotedReserve, that.promotedReserve)) {
            return false;
        }
        if (!Arrays.equals(this.regularReserve, that.regularReserve)) {
            return false;
        }
        return true;
    }

    public final int hashCode() {
        int result = 0;
        return result;
    }
}

