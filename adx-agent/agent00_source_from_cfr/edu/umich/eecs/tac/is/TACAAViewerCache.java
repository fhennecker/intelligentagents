/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.is;

import edu.umich.eecs.tac.props.AdvertiserInfo;
import edu.umich.eecs.tac.props.PublisherInfo;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.SlotInfo;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.is.EventWriter;
import se.sics.tasim.is.common.ViewerCache;

public class TACAAViewerCache
extends ViewerCache {
    private static final Logger log = Logger.getLogger(TACAAViewerCache.class.getName());
    private RetailCatalog catalog;
    private SlotInfo slotInfo;
    private Map<Integer, AdvertiserInfo> advertiserInfos = new HashMap<Integer, AdvertiserInfo>();
    private Map<Integer, PublisherInfo> publisherInfoMap = new HashMap<Integer, PublisherInfo>();
    private int timeUnit;
    private static final int DU_AGENT = 0;
    private static final int DU_TYPE = 1;
    private static final int DU_VALUE = 2;
    private static final int DU_PARTS = 3;
    private int[] dataUpdatedConstants;
    private int dataUpdatedCount = 0;
    private int noCachedData = 0;
    private Hashtable cache = new Hashtable();
    public static final int MAX_CACHE = 60;

    private void addToCache(int agent, int type, int value) {
        String key = "" + agent + "_" + type;
        CacheEntry ce = (CacheEntry)this.cache.get(key);
        if (ce == null) {
            ce = new CacheEntry(null);
            ce.agent = agent;
            ce.type = type;
            ce.cachedData = new int[60];
            this.cache.put(key, ce);
        }
        ce.addCachedData(value);
    }

    @Override
    public void writeCache(EventWriter eventWriter) {
        Object[] keys;
        super.writeCache(eventWriter);
        if (this.catalog != null) {
            eventWriter.dataUpdated(0, this.catalog);
        }
        if (this.slotInfo != null) {
            eventWriter.dataUpdated(0, this.slotInfo);
        }
        for (Integer agent2 : this.publisherInfoMap.keySet()) {
            eventWriter.dataUpdated((int)agent2, 306, this.publisherInfoMap.get(agent2));
        }
        for (Integer agent2 : this.advertiserInfos.keySet()) {
            eventWriter.dataUpdated((int)agent2, 307, this.advertiserInfos.get(agent2));
        }
        if (this.timeUnit > 0) {
            eventWriter.nextTimeUnit(this.timeUnit);
        }
        if (this.dataUpdatedCount > 0) {
            int i = 0;
            int n = this.dataUpdatedCount * 3;
            while (i < n) {
                eventWriter.dataUpdated(this.dataUpdatedConstants[i + 0], this.dataUpdatedConstants[i + 1], this.dataUpdatedConstants[i + 2]);
                i += 3;
            }
        }
        if ((keys = this.cache.keySet().toArray()) != null) {
            int i = 0;
            int n = keys.length;
            while (i < n) {
                CacheEntry ce = (CacheEntry)this.cache.get(keys[i]);
                if (ce != null) {
                    eventWriter.intCache(ce.agent, ce.type, ce.getCache());
                }
                ++i;
            }
        }
    }

    @Override
    public void nextTimeUnit(int timeUnit) {
        this.timeUnit = timeUnit;
    }

    @Override
    public void dataUpdated(int agent, int type, int value) {
        super.dataUpdated(agent, type, value);
        if (type == 200 || type == 200 || type == 201 || type == 202 || type == 203 || type == 204) {
            this.addToCache(agent, type, value);
        }
    }

    @Override
    public void dataUpdated(int agent, int type, long value) {
        super.dataUpdated(agent, type, value);
        if ((type & 100) != 0) {
            this.addToCache(agent, type, (int)value);
        }
    }

    @Override
    public void dataUpdated(int type, Transportable value) {
        super.dataUpdated(type, value);
        Class valueType = value.getClass();
        if (valueType == RetailCatalog.class) {
            this.catalog = (RetailCatalog)value;
        } else if (valueType == SlotInfo.class) {
            this.slotInfo = (SlotInfo)value;
        }
    }

    @Override
    public void dataUpdated(int agent, int type, Transportable content) {
        super.dataUpdated(agent, type, content);
        if (type == 307 && content.getClass() == AdvertiserInfo.class) {
            this.advertiserInfos.put(agent, (AdvertiserInfo)content);
        } else if (type == 306 && content.getClass() == PublisherInfo.class) {
            this.publisherInfoMap.put(agent, (PublisherInfo)content);
        }
    }

    private static class CacheEntry {
        int agent;
        int type;
        int[] cachedData;
        int pos;
        int len;

        private CacheEntry() {
        }

        public void addCachedData(int value) {
            this.cachedData[this.pos] = value;
            this.pos = (this.pos + 1) % 60;
            if (this.len < 60) {
                ++this.len;
            }
        }

        public int[] getCache() {
            int[] tmp = new int[this.len];
            int start = (this.pos - this.len + 60) % 60;
            int i = 0;
            int n = this.len;
            while (i < n) {
                tmp[i] = this.cachedData[(start + i) % 60];
                ++i;
            }
            return tmp;
        }

        /* synthetic */ CacheEntry(CacheEntry cacheEntry) {
            CacheEntry cacheEntry2;
            cacheEntry2();
        }
    }

}

