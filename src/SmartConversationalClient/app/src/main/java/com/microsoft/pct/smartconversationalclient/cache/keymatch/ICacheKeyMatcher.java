package com.microsoft.pct.smartconversationalclient.cache.keymatch;

import com.microsoft.pct.smartconversationalclient.common.IQueryResult;

/**
 * An interface for matching cache keys in a more general way than exact matching
 */
public interface ICacheKeyMatcher {

    /*
    Match the given string and return and array of putative matches
     */
    CacheKeyMatchResult[] match(String strToMatch);

    /*
    Add the data of the given key and its entities to the matcher
     */
    void addKeyMatchData(IQueryResult queryResult);
}
