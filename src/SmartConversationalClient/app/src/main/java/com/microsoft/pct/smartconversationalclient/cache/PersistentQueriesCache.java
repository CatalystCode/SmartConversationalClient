package com.microsoft.pct.smartconversationalclient.cache;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.microsoft.pct.smartconversationalclient.common.IQueryResult;
import com.microsoft.pct.smartconversationalclient.persistentdb.IPersistentDB;
import com.microsoft.pct.smartconversationalclient.persistentdb.SnappyDB;

import java.util.Map;

/**
 * Created by abornst on 6/7/2016.
 */

public class PersistentQueriesCache implements IQueriesCache {

    private static final int DEFAULT_MAXIMUM_CACHE_SIZE = 1000;

    private int _maximumCacheSize;

    private IPersistentDB _exactQueriesCache;

    private Class<? extends IQueryResult> _objectType;

    public PersistentQueriesCache(Context context, Class<? extends IQueryResult> objectType) throws Exception {
        this(DEFAULT_MAXIMUM_CACHE_SIZE, context, objectType);
    }

    public PersistentQueriesCache(int maximumCacheSize, Context context, Class<? extends IQueryResult> objectType) throws Exception {
        if (maximumCacheSize <= 0) {
            throw new IllegalArgumentException("maximumCacheSize must have a positive value");
        }

        _maximumCacheSize = maximumCacheSize;
        _objectType = objectType;

        _exactQueriesCache = new SnappyDB(context);
        _exactQueriesCache.open();
    }

    @Override
    public synchronized IQueryResult matchExact(String query) throws Exception {
        if (query == null || query.isEmpty()) {
            throw new IllegalArgumentException("query parameter is null or an empty string");
        }

        String transformedKey = preProcessKey(query);
        return _exactQueriesCache.getObject(transformedKey,_objectType);
    }

    @Override
    public synchronized QueriesCacheMatch[] match(String query) throws Exception {
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
    public synchronized long getSize() throws Exception {
        return _exactQueriesCache.getSize();
    }

    @Override
    public synchronized Map<String, IQueryResult> getCacheItems() throws Exception {
        ArrayMap<String, IQueryResult> duplicate = new ArrayMap<String, IQueryResult>();
        String[] keys =  _exactQueriesCache.getAllKeys();

        for (String key : keys) {
            duplicate.put(key,_exactQueriesCache.getObject(key,_objectType));
        }

        return duplicate;
    }

    private static String preProcessKey(String key) {
        String transformed = key.toLowerCase();
        return transformed;
    }
}