/*
 * Decompiled with CFR 0_110.
 */
package adx.stats;

import adx.stats.CampaignSegmentTracker;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import tau.tac.adx.agents.CampaignData;
import tau.tac.adx.report.adn.MarketSegment;

public final class LoadBalancer
extends Enum<LoadBalancer> {
    public static final /* enum */ LoadBalancer INSTANCE = new LoadBalancer("INSTANCE", 0);
    private Map<MarketSegment, List<CampaignSegmentTracker>> bars = new HashMap<MarketSegment, List<CampaignSegmentTracker>>();
    private Map<String, Integer> agents = new HashMap<String, Integer>();
    private static final /* synthetic */ LoadBalancer[] ENUM$VALUES;

    static {
        ENUM$VALUES = new LoadBalancer[0];
    }

    private LoadBalancer(String string2, int n2) {
        super(string, n);
        this.bars.put(MarketSegment.MALE, new LinkedList());
        this.bars.put(MarketSegment.FEMALE, new LinkedList());
        this.bars.put(MarketSegment.YOUNG, new LinkedList());
        this.bars.put(MarketSegment.OLD, new LinkedList());
        this.bars.put(MarketSegment.LOW_INCOME, new LinkedList());
        this.bars.put(MarketSegment.HIGH_INCOME, new LinkedList());
    }

    public void initialize() {
        for (List<CampaignSegmentTracker> bar : this.bars.values()) {
            bar.clear();
        }
        this.agents.clear();
    }

    public Iterable<CampaignSegmentTracker> get(Set<MarketSegment> segments) {
        LinkedList<CampaignSegmentTracker> list = new LinkedList<CampaignSegmentTracker>();
        for (MarketSegment segment : segments) {
            list.addAll((Collection)this.bars.get((Object)segment));
        }
        return list;
    }

    public int getAgentId(String agent) {
        return this.agents.get(agent);
    }

    public void advanceDay() {
        for (Map.Entry<MarketSegment, List<CampaignSegmentTracker>> b : this.bars.entrySet()) {
            Iterator<CampaignSegmentTracker> i = b.getValue().iterator();
            while (i.hasNext()) {
                if (i.next().advanceDay() != 0) continue;
                i.remove();
            }
        }
    }

    public void put(CampaignData campaign, long budget, String agent) {
        HashSet<MarketSegment> targetSegment = new HashSet<MarketSegment>(campaign.getTargetSegment());
        if (!targetSegment.contains((Object)MarketSegment.MALE) && !targetSegment.contains((Object)MarketSegment.FEMALE)) {
            targetSegment.add(MarketSegment.MALE);
            targetSegment.add(MarketSegment.FEMALE);
        }
        if (!targetSegment.contains((Object)MarketSegment.YOUNG) && !targetSegment.contains((Object)MarketSegment.OLD)) {
            targetSegment.add(MarketSegment.YOUNG);
            targetSegment.add(MarketSegment.OLD);
        }
        if (!targetSegment.contains((Object)MarketSegment.LOW_INCOME) && !targetSegment.contains((Object)MarketSegment.HIGH_INCOME)) {
            targetSegment.add(MarketSegment.LOW_INCOME);
            targetSegment.add(MarketSegment.HIGH_INCOME);
        }
        for (MarketSegment segment : targetSegment) {
            this.bars.get((Object)segment).add(new CampaignSegmentTracker(campaign, segment, budget, agent));
        }
        if (!this.agents.containsKey(agent)) {
            this.agents.put(agent, this.agents.size());
        }
    }

    public static LoadBalancer getInstance() {
        return INSTANCE;
    }

    public static LoadBalancer[] values() {
        LoadBalancer[] arrloadBalancer = ENUM$VALUES;
        int n = arrloadBalancer.length;
        LoadBalancer[] arrloadBalancer2 = new LoadBalancer[n];
        System.arraycopy(arrloadBalancer, 0, arrloadBalancer2, 0, n);
        return arrloadBalancer2;
    }

    public static LoadBalancer valueOf(String string) {
        return (LoadBalancer)((Object)Enum.valueOf(LoadBalancer.class, string));
    }
}

