package com.microsoft.pct.smartconversationalclient.cache;

import com.microsoft.pct.smartconversationalclient.common.IQueryResult;

import java.lang.reflect.Array;
import java.util.Map;
import android.support.v4.util.ArrayMap;

/**
 * Created by nadavbar on 5/29/16.
 */
public class QueriesCache implements IQueriesCache {

    private static final int DEFAULT_MAXIMUM_CACHE_SIZE = 1000;

    private int _maximumCacheSize;

    private Map<String, IQueryResult> _exactQueriesCache;

    public QueriesCache() {
        this(DEFAULT_MAXIMUM_CACHE_SIZE);
    }

    public QueriesCache(int maximumCacheSize) {
        if (maximumCacheSize <= 0) {
            throw new IllegalArgumentException("maximumCacheSize must have a positive value");
        }

        _maximumCacheSize = maximumCacheSize;
        _exactQueriesCache = new ArrayMap<String, IQueryResult>();
    }

    @Override
    public synchronized IQueryResult matchExact(String query) {
        if (query == null || query.isEmpty()) {
            throw new IllegalArgumentException("query parameter is null or an empty string");
        }

        String transormedKey = preProcessKey(query);
        return _exactQueriesCache.get(transormedKey);
    }

    @Override
    public synchronized QueriesCacheMatch[] match(String query) {
        IQueryResult result = matchExact(query);

        if (result == null) {
            return null;
        }

        return new QueriesCacheMatch[] {
            new QueriesCacheMatch(result, 1.00)
        };
    }

    @Override
    public synchronized void put (String query, IQueryResult queryResult) throws Exception {
        if (getSize() >= _maximumCacheSize) {
            // TODO: Reduce the getSize of the cache
            throw new IllegalStateException("Cache reached its maximal getSize");
        }

        String transformedKey = preProcessKey(query);
        _exactQueriesCache.put(transformedKey, queryResult);
    }

    @Override
    public synchronized  void clearOldCacheItems(long cacheItemTTLMilliseconds) throws Exception {
        // TODO: implement
        throw new Exception("Not implemented");
    }

    @Override
    public synchronized long getSize() {
        return _exactQueriesCache.size();
    }

    @Override
    public synchronized Map<String, IQueryResult> getCacheItems() {
        ArrayMap<String, IQueryResult> duplicate = new ArrayMap<String, IQueryResult>();
        duplicate.putAll(_exactQueriesCache);
        return duplicate;
    }

    private static String preProcessKey(String key) {
        String transformed = key.toLowerCase();
        return transformed;
    }
}
