/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.google.inject.AbstractModule
 *  com.google.inject.Singleton
 *  com.google.inject.binder.AnnotatedBindingBuilder
 *  com.google.inject.binder.ScopedBindingBuilder
 */
package tau.tac.adx.util;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.ScopedBindingBuilder;
import tau.tac.adx.ads.properties.generators.AdTypeGenerator;
import tau.tac.adx.ads.properties.generators.SimpleAdTypeGenerator;
import tau.tac.adx.auction.AuctionManager;
import tau.tac.adx.auction.SimpleAuctionManager;
import tau.tac.adx.auction.manager.AdxBidManager;
import tau.tac.adx.auction.manager.AdxBidManagerImpl;
import tau.tac.adx.auction.tracker.AdxBidTracker;
import tau.tac.adx.auction.tracker.AdxBidTrackerImpl;
import tau.tac.adx.auction.tracker.AdxSpendTracker;
import tau.tac.adx.auction.tracker.AdxSpendTrackerImpl;
import tau.tac.adx.devices.generators.DeviceGenerator;
import tau.tac.adx.devices.generators.SimpleDeviceGenerator;
import tau.tac.adx.publishers.generators.AdxPublisherGenerator;
import tau.tac.adx.publishers.generators.SimplePublisherGenerator;
import tau.tac.adx.sim.AdxAuctioneer;
import tau.tac.adx.sim.SimpleAdxAuctioneer;

public class AdxModule
extends AbstractModule {
    protected void configure() {
        this.bind((Class)AdxPublisherGenerator.class).to((Class)SimplePublisherGenerator.class);
        this.bind((Class)AdTypeGenerator.class).to((Class)SimpleAdTypeGenerator.class);
        this.bind((Class)DeviceGenerator.class).to((Class)SimpleDeviceGenerator.class);
        this.bind((Class)AdxAuctioneer.class).to((Class)SimpleAdxAuctioneer.class).in((Class)Singleton.class);
        this.bind((Class)AdxBidManager.class).to((Class)AdxBidManagerImpl.class).in((Class)Singleton.class);
        this.bind((Class)AdxSpendTracker.class).to((Class)AdxSpendTrackerImpl.class).in((Class)Singleton.class);
        this.bind((Class)AdxBidTracker.class).to((Class)AdxBidTrackerImpl.class).in((Class)Singleton.class);
        this.bind((Class)AuctionManager.class).to((Class)SimpleAuctionManager.class).in((Class)Singleton.class);
    }
}

