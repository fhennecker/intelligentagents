/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.AdvertiserInfo;
import edu.umich.eecs.tac.props.Auction;
import edu.umich.eecs.tac.props.Pricing;
import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.Ranking;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.SlotInfo;
import edu.umich.eecs.tac.props.UserClickModel;
import edu.umich.eecs.tac.sim.RecentConversionsTracker;
import edu.umich.eecs.tac.user.User;
import edu.umich.eecs.tac.user.UserEventListener;
import edu.umich.eecs.tac.user.UserEventSupport;
import edu.umich.eecs.tac.user.UserUtils;
import edu.umich.eecs.tac.user.UserViewManager;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import tau.tac.adx.props.AdLink;

public class DefaultUserViewManager
implements UserViewManager {
    private Logger log = Logger.getLogger(DefaultUserViewManager.class.getName());
    private UserEventSupport eventSupport;
    private Map<String, AdvertiserInfo> advertiserInfo;
    private SlotInfo slotInfo;
    private RetailCatalog catalog;
    private Random random;
    private UserClickModel userClickModel;
    private RecentConversionsTracker recentConversionsTracker;

    public DefaultUserViewManager(RetailCatalog catalog, RecentConversionsTracker recentConversionsTracker, Map<String, AdvertiserInfo> advertiserInfo, SlotInfo slotInfo) {
        this(catalog, recentConversionsTracker, advertiserInfo, slotInfo, new Random());
    }

    public DefaultUserViewManager(RetailCatalog catalog, RecentConversionsTracker recentConversionsTracker, Map<String, AdvertiserInfo> advertiserInfo, SlotInfo slotInfo, Random random) {
        if (catalog == null) {
            throw new NullPointerException("Retail catalog cannot be null");
        }
        if (slotInfo == null) {
            throw new NullPointerException("Auction info cannot be null");
        }
        if (recentConversionsTracker == null) {
            throw new NullPointerException("Recent conversions tracker cannot be null");
        }
        if (advertiserInfo == null) {
            throw new NullPointerException("Advertiser information cannot be null");
        }
        if (random == null) {
            throw new NullPointerException("Random generator cannot be null");
        }
        this.catalog = catalog;
        this.random = random;
        this.recentConversionsTracker = recentConversionsTracker;
        this.advertiserInfo = advertiserInfo;
        this.slotInfo = slotInfo;
        this.eventSupport = new UserEventSupport();
    }

    @Override
    public void nextTimeUnit(int timeUnit) {
    }

    @Override
    public boolean processImpression(User user, Query query, Auction auction) {
        this.fireQueryIssued(query);
        boolean converted = false;
        boolean clicking = true;
        double continuationProbability = 0.0;
        int queryIndex = this.userClickModel.queryIndex(query);
        if (queryIndex < 0) {
            this.log.warning(String.format("Query: %s does not have a click model.", query));
        } else {
            continuationProbability = this.userClickModel.getContinuationProbability(queryIndex);
        }
        Ranking ranking = auction.getRanking();
        Pricing pricing = auction.getPricing();
        int i = 0;
        while (i < ranking.size()) {
            AdLink ad = ranking.get(i);
            boolean isPromoted = ranking.isPromoted(i);
            this.fireAdViewed(query, ad, i + 1, isPromoted);
            if (clicking) {
                AdvertiserInfo info = this.advertiserInfo.get(ad.getAdvertiser());
                double promotionEffect = ranking.isPromoted(i) ? this.slotInfo.getPromotedSlotBonus() : 0.0;
                double clickProbability = UserUtils.calculateClickProbability(user, ad.getAd(), info.getTargetEffect(), promotionEffect, UserUtils.findAdvertiserEffect(query, ad, this.userClickModel));
                if (this.random.nextDouble() <= clickProbability) {
                    this.fireAdClicked(query, ad, i + 1, pricing.getPrice(ad));
                    double conversionProbability = UserUtils.calculateConversionProbability(user, query, info, this.recentConversionsTracker.getRecentConversions(ad.getAdvertiser()));
                    if (user.isTransacting() && this.random.nextDouble() <= conversionProbability) {
                        double salesProfit = this.catalog.getSalesProfit(user.getProduct());
                        this.fireAdConverted(query, ad, i + 1, UserUtils.modifySalesProfitForManufacturerSpecialty(user, info.getManufacturerSpecialty(), info.getManufacturerBonus(), salesProfit));
                        converted = true;
                        clicking = false;
                    }
                }
            }
            if (this.random.nextDouble() > continuationProbability) {
                clicking = false;
            }
            ++i;
        }
        return converted;
    }

    @Override
    public boolean addUserEventListener(UserEventListener listener) {
        return this.eventSupport.addUserEventListener(listener);
    }

    @Override
    public boolean containsUserEventListener(UserEventListener listener) {
        return this.eventSupport.containsUserEventListener(listener);
    }

    @Override
    public boolean removeUserEventListener(UserEventListener listener) {
        return this.eventSupport.removeUserEventListener(listener);
    }

    private void fireQueryIssued(Query query) {
        this.eventSupport.fireQueryIssued(query);
    }

    private void fireAdViewed(Query query, AdLink ad, int slot, boolean isPromoted) {
        this.eventSupport.fireAdViewed(query, ad, slot, isPromoted);
    }

    private void fireAdClicked(Query query, AdLink ad, int slot, double cpc) {
        this.eventSupport.fireAdClicked(query, ad, slot, cpc);
    }

    private void fireAdConverted(Query query, AdLink ad, int slot, double salesProfit) {
        this.eventSupport.fireAdConverted(query, ad, slot, salesProfit);
    }

    @Override
    public UserClickModel getUserClickModel() {
        return this.userClickModel;
    }

    @Override
    public void setUserClickModel(UserClickModel userClickModel) {
        this.userClickModel = userClickModel;
    }
}

