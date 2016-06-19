package com.microsoft.pct.smartconversationalclient.mocks;

import com.microsoft.pct.smartconversationalclient.common.IQueryResult;

/**
 * Created by abornst on 6/1/2016.
 */

/*Implementation of IQueryResult for testing purposes */
public class QueryResultTest implements IQueryResult {

    @com.fasterxml.jackson.annotation.JsonProperty("query")
    private String _query;

    public QueryResultTest() {
        _query = "some query";
    }

    public QueryResultTest(String _query) {
        this._query = _query;
    }

    @Override
    public String getQuery() {
        return _query;
    }

    @Override
    public boolean equals (Object object){
        QueryResultTest myObject = (QueryResultTest) object;
        if (myObject.getQuery().equals(_query)){
            return true;
        }
        return false;

    }
}
