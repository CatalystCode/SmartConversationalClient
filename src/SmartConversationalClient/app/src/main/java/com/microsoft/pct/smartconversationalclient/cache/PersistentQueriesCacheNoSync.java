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

public class PersistentQueriesCacheNoSync extends QueriesCache {

    private static final int DEFAULT_MAXIMUM_CACHE_SIZE = 1000;

    private int _maximumCacheSize;

    private IPersistentDB _exactQueriesCache;

    //The type of IQueryResult being stored in the cache needed for serialization purposes
    private Class<? extends IQueryResult> _objectType;

    public PersistentQueriesCacheNoSync(Context context, Class<? extends IQueryResult> objectType) {
        this(DEFAULT_MAXIMUM_CACHE_SIZE, context, objectType);
    }

    public PersistentQueriesCacheNoSync(int maximumCacheSize, Context context, Class<? extends IQueryResult> objectType) {
        if (maximumCacheSize <= 0) {
            throw new IllegalArgumentException("maximumCacheSize must have a positive value");
        }

        _maximumCacheSize = maximumCacheSize;
        _objectType = objectType;

        _exactQueriesCache = new SnappyDB(context);
    }

    @Override
    public void init() throws Exception{
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
    public synchronized void put (String query, IQueryResult queryResult) throws Exception {
        if (getSize() >= _maximumCacheSize) {
            // TODO: Reduce the getSize of the cache
            throw new IllegalStateException("Cache reached its maximal getSize");
        }

        String transformedKey = preProcessKey(query);
        _exactQueriesCache.put(transformedKey, queryResult);
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