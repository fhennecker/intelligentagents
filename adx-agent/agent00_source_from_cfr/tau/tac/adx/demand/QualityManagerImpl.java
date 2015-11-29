/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.demand;

import java.util.HashMap;
import java.util.Set;
import tau.tac.adx.AdxManager;
import tau.tac.adx.demand.QualityManager;
import tau.tac.adx.sim.TACAdxSimulation;

public class QualityManagerImpl
implements QualityManager {
    private static final double MU = 0.6;
    private final HashMap<String, Double> advertisersScores = new HashMap();

    @Override
    public void addAdvertiser(String advertiser) {
        this.advertisersScores.put(advertiser, 1.0);
    }

    @Override
    public double updateQualityScore(String advertiser, Double score) {
        Double newScore = 0.4 * this.getQualityScore(advertiser) + 0.6 * score;
        this.advertisersScores.put(advertiser, newScore);
        AdxManager.getInstance().getSimulation().broadcastAdNetworkQualityRating(advertiser, newScore);
        return newScore;
    }

    @Override
    public double getQualityScore(String advertiser) {
        return this.advertisersScores.get(advertiser);
    }

    @Override
    public String logToString() {
        String ret = new String("Quality Retings ");
        for (String adv : this.advertisersScores.keySet()) {
            ret = String.valueOf(ret) + adv + ": " + this.advertisersScores.get(adv);
        }
        return ret;
    }
}

