package com.microsoft.pct.smartconversationalclient.cache;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.microsoft.pct.smartconversationalclient.common.IQueryResult;
import com.microsoft.pct.smartconversationalclient.persistentdb.SnappyDB;

import java.util.Map;

/**
 * Created by abornst on 6/16/2016.
 */
public class PersistentQueriesCache extends QueriesCache {

    private static final int DEFAULT_MAXIMUM_CACHE_SIZE = 1000;

    private int _maximumCacheSize;

    private Map<String, IQueryResult> _exactQueriesCache;

    private SnappyDB _exactQueriesDB;

    //The type of IQueryResult being stored in the cache needed for serialization purposes
    private Class<? extends IQueryResult> _objectType;

    public PersistentQueriesCache(Context context, Class<? extends IQueryResult> objectType) {
        this(DEFAULT_MAXIMUM_CACHE_SIZE, context, objectType);
    }

    public PersistentQueriesCache(int maximumCacheSize, Context context, Class<? extends IQueryResult> objectType) {
        if (maximumCacheSize <= 0) {
            throw new IllegalArgumentException("maximumCacheSize must have a positive value");
        }

        _maximumCacheSize = maximumCacheSize;
        _objectType = objectType;
        _exactQueriesCache = new ArrayMap<String, IQueryResult>();
        _exactQueriesDB = new SnappyDB(context);
    }


    @Override
    public void init() throws Exception {
        _exactQueriesDB.open();
        loadDBIntoMemory();
    }

    @Override
    public synchronized IQueryResult matchExact(String query) throws Exception{
        if (query == null || query.isEmpty()) {
            throw new IllegalArgumentException("query parameter is null or an empty string");
        }

        String transformedKey = preProcessKey(query);

        IQueryResult result = _exactQueriesCache.get(transformedKey);

        //if not in memory check disk
        if (result == null){
            result = _exactQueriesDB.getObject(transformedKey, _objectType);
        }

        return result;
    }

    @Override
    public synchronized void put (String query, IQueryResult queryResult) throws Exception {
        String transformedKey = preProcessKey(query);
        if (getSize() <= _maximumCacheSize) {
            _exactQueriesCache.put(transformedKey, queryResult);
        }

        _exactQueriesDB.put(transformedKey, queryResult);
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

    /*
    Loads first maximumCacheSize keys and values from disk into memory
    ToDo Provide Weighted Index for retrieving values
    */
    private void loadDBIntoMemory() throws Exception {
        String[] keys =  _exactQueriesDB.getNKeys(_maximumCacheSize);
        for (String key : keys) {
            _exactQueriesCache.put(key,_exactQueriesDB.getObject(key,_objectType));
        }
    }

    /*Clears in memory and disk database*/
    public void clear() throws Exception {
        _exactQueriesCache.clear();
        _exactQueriesDB.clear();
    }
}
