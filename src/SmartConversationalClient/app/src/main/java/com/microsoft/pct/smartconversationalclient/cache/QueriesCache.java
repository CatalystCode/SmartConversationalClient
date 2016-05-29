package com.microsoft.pct.smartconversationalclient.cache;

import com.microsoft.pct.smartconversationalclient.common.IQueryResult;

import java.util.Map;

/**
 * Created by nadavbar on 5/29/16.
 */
public class QueriesCache implements IQueriesCache {

    private static final int DEFAULT_MAXIMUM_CACHE_SIZE = 1000;

    private int _maximumCacheSize;

    

    public QueriesCache() {
        _maximumCacheSize = DEFAULT_MAXIMUM_CACHE_SIZE;
    }

    public QueriesCache(int maximumCacheSize) {
        if (maximumCacheSize <= 0) {
            throw new IllegalArgumentException("maximumCacheSize must have a positive value");
        }

        _maximumCacheSize = maximumCacheSize;
    }

    @Override
    public IQueryResult matchExact(String query) {
        return null;
    }

    @Override
    public QueriesCacheMatch[] match(String query) {
        return new QueriesCacheMatch[0];
    }

    @Override
    public void put(String query, IQueryResult queryResult) {

    }

    @Override
    public void clearOldCacheItems(long cacheItemTTLMilliseconds) {

    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public Map<String, IQueryResult> getCacheItems() {
        return null;
    }
}
