package com.microsoft.pct.smartconversationalclient.cache;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.microsoft.pct.smartconversationalclient.common.IQueryResult;
import com.microsoft.pct.smartconversationalclient.luis.LUISEntity;
import com.microsoft.pct.smartconversationalclient.luis.LUISQueryResult;
import com.microsoft.pct.smartconversationalclient.persistentdb.IPersistentDB;
import com.microsoft.pct.smartconversationalclient.persistentdb.SnappyDB;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by abornst on 6/16/2016.
 */
public class SmartLuisQueriesCache implements IQueriesCache {

    private static final int DEFAULT_MAXIMUM_CACHE_SIZE = 1000;

    //A list of all possible entites (TBD read from external source)
    private static final String[] ENTITES_LIST = {"Kitchen","Bathroom","Living Room","Bed Room","Den"};

    private int _maximumCacheSize;

    private IPersistentDB _exactQueriesCache;


    public SmartLuisQueriesCache(Context context) throws Exception {
        this(DEFAULT_MAXIMUM_CACHE_SIZE, context);
    }

    public SmartLuisQueriesCache(int maximumCacheSize, Context context) throws Exception {
        if (maximumCacheSize <= 0) {
            throw new IllegalArgumentException("maximumCacheSize must have a positive value");
        }

        _maximumCacheSize = maximumCacheSize;

        _exactQueriesCache = new SnappyDB(context);
        _exactQueriesCache.open();
    }

    @Override
    public IQueryResult matchExact(String query) throws Exception {
        LUISQueryResult luisQueryResult = new LUISQueryResult();
        ArrayList<LUISEntity> extractedEntites = new ArrayList<LUISEntity>();
        //itterate through the ruleKeys
        for (String rule : _exactQueriesCache.getAllKeys()){
            Pattern p = Pattern.compile(rule);
            Matcher m = p.matcher(query);
            //if a the query matches a known rule
            if (m.find()){
                //populate the LuisQueryResult with known entites from the query
                for (int groupIndex = 0; groupIndex < m.groupCount(); groupIndex++){
                    for (String entity : ENTITES_LIST){
                        if(m.group(groupIndex).contains(entity)){
                            LUISEntity extractedEntity = new LUISEntity();
                            extractedEntity.setEntity(entity);
                            extractedEntity.setScore(1.0);
                            extractedEntites.add(extractedEntity);
                        }
                    }
                }
                //configure and return extracted query result
                luisQueryResult.setQuery(query);
                luisQueryResult.setIntents(((LUISQueryResult) _exactQueriesCache.getObject(rule,LUISQueryResult.class)).getIntents());
                luisQueryResult.setEntities((LUISEntity[]) extractedEntites.toArray());
                return luisQueryResult;
            }
        }
        return null;
    }

    @Override
    public QueriesCacheMatch[] match(String query) throws Exception {
        // TODO: implement based on confidences
        throw new Exception("Not implemented");
    }

    @Override
    public void put(String query, IQueryResult queryResult) throws Exception {
        if (getSize() >= _maximumCacheSize) {
            // TODO: Reduce the getSize of the cache
            throw new IllegalStateException("Cache reached its maximal getSize");
        }

        String ruleKey = luisQRToRuleKey((LUISQueryResult)queryResult);
        _exactQueriesCache.put(ruleKey, queryResult);

    }

    @Override
    public void clearOldCacheItems(long cacheItemTTLMilliseconds) throws Exception {
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

        for (String key : _exactQueriesCache.getAllKeys()) {
            duplicate.put(key,_exactQueriesCache.getObject(key,LUISQueryResult.class));
        }

        return duplicate;
    }

    /*
     Takes a LUISQueryResult and returns a key rule with comprised of the query string with entity occurrences replaced with .*
    */
    public String luisQRToRuleKey(LUISQueryResult luisQueryResult){
        String ruleKey = luisQueryResult.getQuery();

        for (LUISEntity entity : luisQueryResult.getEntities()) {
            String entityValue = entity.getEntity();
            ruleKey = ruleKey.replace(entityValue, ".*");
        }

        return ruleKey.toLowerCase();
    }
}
