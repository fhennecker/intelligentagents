/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.UserPopulationState;
import edu.umich.eecs.tac.user.DistributionBroadcaster;
import edu.umich.eecs.tac.user.QueryState;
import edu.umich.eecs.tac.user.UserManager;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.is.EventWriter;

public class DefaultDistributionBroadcaster
implements DistributionBroadcaster {
    private UserManager userManager;
    private static /* synthetic */ int[] $SWITCH_TABLE$edu$umich$eecs$tac$user$QueryState;

    public DefaultDistributionBroadcaster(UserManager userManager) {
        if (userManager == null) {
            throw new NullPointerException("user manager cannot be null");
        }
        this.userManager = userManager;
    }

    @Override
    public void broadcastUserDistribution(int usersIndex, EventWriter eventWriter) {
        int[] distribution = this.userManager.getStateDistribution();
        QueryState[] states = QueryState.values();
        int i = 0;
        while (i < distribution.length) {
            switch (DefaultDistributionBroadcaster.$SWITCH_TABLE$edu$umich$eecs$tac$user$QueryState()[states[i].ordinal()]) {
                case 1: {
                    eventWriter.dataUpdated(usersIndex, 200, distribution[i]);
                    break;
                }
                case 2: {
                    eventWriter.dataUpdated(usersIndex, 201, distribution[i]);
                    break;
                }
                case 3: {
                    eventWriter.dataUpdated(usersIndex, 202, distribution[i]);
                    break;
                }
                case 4: {
                    eventWriter.dataUpdated(usersIndex, 203, distribution[i]);
                    break;
                }
                case 5: {
                    eventWriter.dataUpdated(usersIndex, 204, distribution[i]);
                    break;
                }
                case 6: {
                    eventWriter.dataUpdated(usersIndex, 205, distribution[i]);
                    break;
                }
            }
            ++i;
        }
        UserPopulationState ups = new UserPopulationState();
        for (Product product : this.userManager.getRetailCatalog()) {
            ups.setDistribution(product, this.userManager.getStateDistribution(product));
        }
        ups.lock();
        eventWriter.dataUpdated(usersIndex, 0, ups);
    }

    static /* synthetic */ int[] $SWITCH_TABLE$edu$umich$eecs$tac$user$QueryState() {
        int[] arrn;
        int[] arrn2 = $SWITCH_TABLE$edu$umich$eecs$tac$user$QueryState;
        if (arrn2 != null) {
            return arrn2;
        }
        arrn = new int[QueryState.values().length];
        try {
            arrn[QueryState.FOCUS_LEVEL_ONE.ordinal()] = 4;
        }
        catch (NoSuchFieldError v1) {}
        try {
            arrn[QueryState.FOCUS_LEVEL_TWO.ordinal()] = 5;
        }
        catch (NoSuchFieldError v2) {}
        try {
            arrn[QueryState.FOCUS_LEVEL_ZERO.ordinal()] = 3;
        }
        catch (NoSuchFieldError v3) {}
        try {
            arrn[QueryState.INFORMATIONAL_SEARCH.ordinal()] = 2;
        }
        catch (NoSuchFieldError v4) {}
        try {
            arrn[QueryState.NON_SEARCHING.ordinal()] = 1;
        }
        catch (NoSuchFieldError v5) {}
        try {
            arrn[QueryState.TRANSACTED.ordinal()] = 6;
        }
        catch (NoSuchFieldError v6) {}
        $SWITCH_TABLE$edu$umich$eecs$tac$user$QueryState = arrn;
        return $SWITCH_TABLE$edu$umich$eecs$tac$user$QueryState;
    }
}

