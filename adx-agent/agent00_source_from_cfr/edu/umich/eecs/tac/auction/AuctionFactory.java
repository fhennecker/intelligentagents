/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.auction.BidManager;
import edu.umich.eecs.tac.props.Auction;
import edu.umich.eecs.tac.props.PublisherInfo;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.ReserveInfo;
import edu.umich.eecs.tac.props.SlotInfo;
import edu.umich.eecs.tac.util.config.ConfigProxy;

public interface AuctionFactory {
    public Auction runAuction(Query var1);

    public BidManager getBidManager();

    public PublisherInfo getPublisherInfo();

    public void setPublisherInfo(PublisherInfo var1);

    public void setBidManager(BidManager var1);

    public SlotInfo getSlotInfo();

    public void setSlotInfo(SlotInfo var1);

    public ReserveInfo getReserveInfo();

    public void setReserveInfo(ReserveInfo var1);

    public void configure(ConfigProxy var1);
}

