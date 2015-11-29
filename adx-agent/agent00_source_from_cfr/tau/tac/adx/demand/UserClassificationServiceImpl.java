/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.demand;

import edu.umich.eecs.tac.auction.AuctionUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import tau.tac.adx.AdxManager;
import tau.tac.adx.demand.UserClassificationService;
import tau.tac.adx.demand.UserClassificationServiceAdNetData;
import tau.tac.adx.sim.TACAdxSimulation;

public class UserClassificationServiceImpl
implements UserClassificationService {
    private static final double UCS_PROB = 0.9;
    private final Map<String, UserClassificationServiceAdNetData> advertisersData = new HashMap<String, UserClassificationServiceAdNetData>();
    private final Map<String, UserClassificationServiceAdNetData> tomorrowsAdvertisersData = new HashMap<String, UserClassificationServiceAdNetData>();

    @Override
    public void updateAdvertiserBid(String advertiser, double ucsBid, int day) {
        UserClassificationServiceAdNetData advData = this.tomorrowsAdvertisersData.get(advertiser);
        if (advData == null) {
            advData = new UserClassificationServiceAdNetData();
            advData.setAuctionResult(0.0, 1.0, 1);
            this.tomorrowsAdvertisersData.put(advertiser, advData);
        }
        advData.setBid(ucsBid, day);
    }

    @Override
    public UserClassificationServiceAdNetData getAdNetData(String advertiser) {
        return this.advertisersData.get(advertiser);
    }

    @Override
    public UserClassificationServiceAdNetData getTomorrowsAdNetData(String advertiser) {
        return this.tomorrowsAdvertisersData.get(advertiser);
    }

    @Override
    public void auction(int day, boolean broadcast) {
        this.advertisersData.clear();
        for (String advertiser : this.tomorrowsAdvertisersData.keySet()) {
            this.advertisersData.put(advertiser, this.tomorrowsAdvertisersData.get(advertiser).clone());
        }
        int advCount = this.tomorrowsAdvertisersData.size();
        if (advCount > 0) {
            String[] advNames = new String[advCount + 1];
            double[] bids = new double[advCount + 1];
            int[] indices = new int[advCount + 1];
            int i = 0;
            ArrayList<String> advNamesList = new ArrayList<String>(this.tomorrowsAdvertisersData.keySet());
            Collections.shuffle(advNamesList);
            for (String advName : advNamesList) {
                advNames[i] = new String(advName);
                bids[i] = this.tomorrowsAdvertisersData.get((Object)advName).bid;
                indices[i] = i++;
            }
            advNames[advCount] = "Zero";
            bids[advCount] = 0.0;
            indices[advCount] = advCount;
            AuctionUtils.hardSort(bids, indices);
            double ucsProb = 1.0;
            double levelPrice = 0.0;
            int j = 0;
            while (j < advCount) {
                String advertiser2 = advNames[indices[j]];
                UserClassificationServiceAdNetData advData = this.tomorrowsAdvertisersData.get(advertiser2);
                levelPrice = ucsProb * bids[indices[j + 1]];
                advData.setAuctionResult(levelPrice, ucsProb, day + 1);
                if (broadcast) {
                    AdxManager.getInstance().getSimulation().broadcastUCSWin(advertiser2, levelPrice);
                }
                ucsProb *= 0.9;
                ++j;
            }
        }
    }

    @Override
    public String logToString() {
        String ret = new String("");
        for (String adv : this.advertisersData.keySet()) {
            ret = String.valueOf(ret) + adv + this.advertisersData.get(adv).logToString();
        }
        return ret;
    }
}

