/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.messages;

import tau.tac.adx.auction.AdxAuctionResult;
import tau.tac.adx.messages.AdxMessage;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.users.AdxUser;

public class AuctionMessage
implements AdxMessage {
    AdxAuctionResult auctionResult;
    AdxQuery query;
    AdxUser user;

    public AuctionMessage(AdxAuctionResult auctionResult, AdxQuery query, AdxUser user) {
        this.auctionResult = auctionResult;
        this.query = query;
        this.user = user;
    }

    public AdxAuctionResult getAuctionResult() {
        return this.auctionResult;
    }

    public AdxQuery getQuery() {
        return this.query;
    }

    public AdxUser getUser() {
        return this.user;
    }
}

