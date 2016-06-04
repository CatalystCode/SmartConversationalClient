package com.microsoft.pct.smartconversationalclient.common;

/**
 * Created by abornst on 6/1/2016.
 */

/*Implementation of IQueryResult for testing purposes */
public class MockQueryResult implements IQueryResult {

    @com.fasterxml.jackson.annotation.JsonProperty("query")
    private String _query;

    public MockQueryResult() {
        _query = "some query";
    }

    public MockQueryResult(String _query) {
        this._query = _query;
    }

    @Override
    public String getQuery() {
        return _query;
    }

    @Override
    public boolean equals(Object object){
        MockQueryResult myObject = (MockQueryResult) object;
        if (myObject.getQuery().equals(_query)){
            return true;
        }

        return false;
    }
}
