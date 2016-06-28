package com.microsoft.pct.smartconversationalclient.cache;

import com.microsoft.pct.smartconversationalclient.cache.keymatch.CacheKeyMatchResult;
import com.microsoft.pct.smartconversationalclient.cache.keymatch.CacheQueryResult;
import com.microsoft.pct.smartconversationalclient.cache.keymatch.ICacheKeyMatcher;
import com.microsoft.pct.smartconversationalclient.cache.keymatch.RegexCacheKeyMatcher;
import com.microsoft.pct.smartconversationalclient.common.IQueryResult;
import com.microsoft.pct.smartconversationalclient.luis.LUISQueryResult;

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
    protected ICacheKeyMatcher _cacheKeyMatcher;

    public QueriesCache() {
        this(DEFAULT_MAXIMUM_CACHE_SIZE);
    }

    public QueriesCache(int maximumCacheSize) {
        if (maximumCacheSize <= 0) {
            throw new IllegalArgumentException("maximumCacheSize must have a positive value");
        }

        _maximumCacheSize = maximumCacheSize;
        _exactQueriesCache = new ArrayMap<String, IQueryResult>();
        _cacheKeyMatcher = new RegexCacheKeyMatcher();
    }

    @Override
    public void init() throws Exception {

    }

    @Override
    public synchronized IQueryResult matchExact(String query) throws Exception {
        if (query == null || query.isEmpty()) {
            throw new IllegalArgumentException("query parameter is null or an empty string");
        }

        String transformedKey = preProcessKey(query);
        return _exactQueriesCache.get(transformedKey);
    }

    @Override
    public synchronized QueriesCacheMatch[] match(String query) throws Exception {
        IQueryResult result = matchExact(query);

        if (result != null) {
            return new QueriesCacheMatch[] {
                new QueriesCacheMatch(result, 1.00)
            };
        }

        CacheKeyMatchResult[] results = _cacheKeyMatcher.match(query);

        if (results == null || results.length ==0) {
            return null;
        }

        QueriesCacheMatch[] matches = new QueriesCacheMatch[results.length];

        for (int i = 0; i < matches.length; i++) {
            IQueryResult cachedResult = matchExact(results[i].getKeyMatch());
            matches[i] = new QueriesCacheMatch(
                    new CacheQueryResult(query, results[i].getEntities(), cachedResult.getQueryIntents()), results[i].getMatchConfidence());
        }

        return matches;
    }

    @Override
    public synchronized void put(String query, IQueryResult queryResult) throws Exception {
        if (getSize() >= _maximumCacheSize) {
            // TODO: Reduce the getSize of the cache
            throw new IllegalStateException("Cache reached its maximal getSize");
        }

        String transformedKey = preProcessKey(query);
        _exactQueriesCache.put(transformedKey, queryResult);
        _cacheKeyMatcher.addKeyMatchData(queryResult);
    }

    @Override
    public synchronized  void clearOldCacheItems(long cacheItemTTLMilliseconds) throws Exception {
        // TODO: implement
        throw new Exception("Not implemented");
    }

    @Override
    public synchronized long getSize() throws Exception {
        return _exactQueriesCache.size();
    }

    @Override
    public synchronized Map<String, IQueryResult> getCacheItems() throws Exception {
        ArrayMap<String, IQueryResult> duplicate = new ArrayMap<String, IQueryResult>();
        duplicate.putAll(_exactQueriesCache);
        return duplicate;
    }

    private static String preProcessKey(String key) {
        String transformed = key.toLowerCase();
        return transformed;
    }
}
