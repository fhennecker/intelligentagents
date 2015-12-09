/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.users;

import java.util.ArrayList;
import java.util.List;
import tau.tac.adx.auction.AdxAuctionResult;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.users.AdxUser;
import tau.tac.adx.users.AdxUserEventListener;

public class AdxUserEventSupport {
    private final List<AdxUserEventListener> listeners = new ArrayList<AdxUserEventListener>();

    public boolean addUserEventListener(AdxUserEventListener listener) {
        return this.listeners.add(listener);
    }

    public boolean containsUserEventListener(AdxUserEventListener listener) {
        return this.listeners.contains(listener);
    }

    public boolean removeUserEventListener(AdxUserEventListener listener) {
        return this.listeners.remove(listener);
    }

    public void fireQueryIssued(AdxQuery query) {
        for (AdxUserEventListener listener : this.listeners) {
            listener.queryIssued(query);
        }
    }

    public void fireAuctionPerformed(AdxAuctionResult auctionResult, AdxQuery query, AdxUser user) {
        for (AdxUserEventListener listener : this.listeners) {
            listener.auctionPerformed(auctionResult, query, user);
        }
    }
}

