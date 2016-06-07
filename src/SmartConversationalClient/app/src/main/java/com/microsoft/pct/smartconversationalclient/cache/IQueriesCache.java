package com.microsoft.pct.smartconversationalclient.cache;

import com.microsoft.pct.smartconversationalclient.common.IQueryResult;
import com.snappydb.SnappydbException;

import java.io.IOException;
import java.util.Map;

/**
 * An interface for a queries cache.
 */
public interface IQueriesCache {

    /*
    Performs an exact (but case insensitive) match for the query.
    Returns null if there is no match
     */
    IQueryResult matchExact(String query) throws Exception;

    /*
    Performs an inexact matching for the query. Returns an array of QueryMatch objects, ordered by
     the confidence score of the match.
     */
    QueriesCacheMatch[] match(String query) throws Exception;

    /*
    Adds the query and query result to the cache
     */
    void put(String query, IQueryResult queryResult) throws Exception;

    /*
    Clear old cache items with that exists longer than the specified time
     */
    void clearOldCacheItems(long cacheItemTTLMilliseconds) throws Exception;

    /*
    Returns the number of items in the cache
     */
    long getSize() throws Exception;

    /*
    Gets a shallow copy of the cache items
     */
    Map<String, IQueryResult> getCacheItems() throws Exception;
}
