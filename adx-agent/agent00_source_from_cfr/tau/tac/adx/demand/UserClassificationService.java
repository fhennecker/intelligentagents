/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.demand;

import tau.tac.adx.demand.UserClassificationServiceAdNetData;

public interface UserClassificationService {
    public void updateAdvertiserBid(String var1, double var2, int var4);

    public UserClassificationServiceAdNetData getAdNetData(String var1);

    public void auction(int var1, boolean var2);

    public String logToString();

    public UserClassificationServiceAdNetData getTomorrowsAdNetData(String var1);
}

