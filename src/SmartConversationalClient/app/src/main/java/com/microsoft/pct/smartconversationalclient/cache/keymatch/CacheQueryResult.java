package com.microsoft.pct.smartconversationalclient.cache.keymatch;

import com.microsoft.pct.smartconversationalclient.common.IQueryResult;

/**
 * Created by nadavbar on 6/19/16.
 */
public class CacheQueryResult implements IQueryResult {

    private String _query;
    private String[] _intents;
    private String[] _entities;

    public CacheQueryResult(String query, String[] intents, String[] entities) {
        _query = query;
        _intents = intents;
        _entities = entities;
    }

    public String getQuery() {
        return _query;
    }

    public String[] getQueryEntities() {
        return _entities;
    }

    public String[] getQueryIntents() {
        return _intents;
    }
}
