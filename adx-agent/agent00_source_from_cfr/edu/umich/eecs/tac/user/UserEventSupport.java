/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.user.UserEventListener;
import java.util.ArrayList;
import java.util.List;
import tau.tac.adx.props.AdLink;

public class UserEventSupport {
    private List<UserEventListener> listeners = new ArrayList<UserEventListener>();

    public boolean addUserEventListener(UserEventListener listener) {
        return this.listeners.add(listener);
    }

    public boolean containsUserEventListener(UserEventListener listener) {
        return this.listeners.contains(listener);
    }

    public boolean removeUserEventListener(UserEventListener listener) {
        return this.listeners.remove(listener);
    }

    public void fireQueryIssued(Query query) {
        for (UserEventListener listener : this.listeners) {
            listener.queryIssued(query);
        }
    }

    public void fireAdViewed(Query query, AdLink ad, int slot, boolean isPromoted) {
        for (UserEventListener listener : this.listeners) {
            listener.viewed(query, ad.getAd(), slot, ad.getAdvertiser(), isPromoted);
        }
    }

    public void fireAdClicked(Query query, AdLink ad, int slot, double cpc) {
        for (UserEventListener listener : this.listeners) {
            listener.clicked(query, ad.getAd(), slot, cpc, ad.getAdvertiser());
        }
    }

    public void fireAdConverted(Query query, AdLink ad, int slot, double salesProfit) {
        for (UserEventListener listener : this.listeners) {
            listener.converted(query, ad.getAd(), slot, salesProfit, ad.getAdvertiser());
        }
    }
}

