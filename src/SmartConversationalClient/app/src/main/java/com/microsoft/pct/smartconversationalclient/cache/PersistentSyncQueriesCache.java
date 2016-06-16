package com.microsoft.pct.smartconversationalclient.cache;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.pct.smartconversationalclient.common.IQueryResult;
import com.microsoft.pct.smartconversationalclient.luis.LUISQueryResult;
import com.microsoft.pct.smartconversationalclient.persistentdb.SnappyDB;
import com.snappydb.SnappydbException;

import java.util.Map;

/**
 * Created by abornst on 6/16/2016.
 */
public class PersistentSyncQueriesCache implements IQueriesCache {

    private static final int DEFAULT_MAXIMUM_CACHE_SIZE = 1000;

    private int _maximumCacheSize;

    private Map<String, IQueryResult> _exactQueriesCache;

    private SnappyDB _exactQueriesDB;

    //The type of IQueryResult being stored in the cache needed for serialization purposes
    private Class<? extends IQueryResult> _objectType;

    public PersistentSyncQueriesCache(Context context, Class<? extends IQueryResult> objectType) throws Exception {
        this(DEFAULT_MAXIMUM_CACHE_SIZE, context, objectType);
    }

    public PersistentSyncQueriesCache(int maximumCacheSize, Context context, Class<? extends IQueryResult> objectType) throws Exception {
        if (maximumCacheSize <= 0) {
            throw new IllegalArgumentException("maximumCacheSize must have a positive value");
        }

        _maximumCacheSize = maximumCacheSize;
        _objectType = objectType;
        _exactQueriesCache = new ArrayMap<String, IQueryResult>();

        _exactQueriesDB = new SnappyDB(context);
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
    public synchronized QueriesCacheMatch[] match(String query)throws Exception {
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
        String transformedKey = preProcessKey(query);
        if (getSize() >= _maximumCacheSize) {
            _exactQueriesDB.put(transformedKey, queryResult);
        }
        else {
            _exactQueriesCache.put(transformedKey, queryResult);
        }
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

    /*
    Dumps in memory cache back into db on disk
    */
    private void syncMemoryWithDB() throws Exception {
        for (String key : _exactQueriesCache.keySet()){
            _exactQueriesDB.put(key, _exactQueriesCache.get(key));
        }
    }
}
