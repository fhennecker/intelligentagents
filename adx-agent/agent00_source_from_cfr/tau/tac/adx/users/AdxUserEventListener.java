/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.users;

import tau.tac.adx.auction.AdxAuctionResult;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.users.AdxUser;

public interface AdxUserEventListener {
    public void queryIssued(AdxQuery var1);

    public void auctionPerformed(AdxAuctionResult var1, AdxQuery var2, AdxUser var3);
}

