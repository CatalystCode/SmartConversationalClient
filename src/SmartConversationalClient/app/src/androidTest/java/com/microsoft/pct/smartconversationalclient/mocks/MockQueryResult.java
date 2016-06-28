package com.microsoft.pct.smartconversationalclient.mocks;

import com.microsoft.pct.smartconversationalclient.common.IQueryResult;

import java.util.Arrays;

/**
 * Created by abornst on 6/1/2016.
 */

/*Implementation of IQueryResult for testing purposes */
public class MockQueryResult implements IQueryResult {

    @com.fasterxml.jackson.annotation.JsonProperty("query")
    private String _query;

    @com.fasterxml.jackson.annotation.JsonProperty("intents")
    private String[] _intents;

    @com.fasterxml.jackson.annotation.JsonProperty("entities")
    private String[] _entities;


    public MockQueryResult() {
        _query = "some query";
        _entities = new String[] {"some entity"};
        _intents = new String[] {"some intent"};
    }

    public MockQueryResult(String query) {
        _query = query;
        _entities = new String[] {"some entity"};
        _intents = new String[] {"some intent"};
    }

    public MockQueryResult(String query, String[] entities, String[] intents) {
        _query = query;
        _entities = entities;
        _intents = intents;
    }

    @Override
    public String getQuery() {
        return _query;
    }

    @Override
    public String[] getQueryIntents() {
        return _intents;
    }

    @Override
    public String[] getQueryEntities() {
        return _entities;
    }

    public static boolean compareArrays(String[] arr1, String[] arr2) {
        String[] copy1 = Arrays.copyOf(arr1, arr1.length);
        String[] copy2 = Arrays.copyOf(arr2, arr2.length);
        Arrays.sort(copy1);
        Arrays.sort(copy2);
        return Arrays.equals(copy1, copy2);
    }

    @Override
    public boolean equals(Object object){
        MockQueryResult myObject = (MockQueryResult) object;
        if (!myObject.getQuery().equals(_query)){
            return false;
        }

        if (!compareArrays(_entities, myObject._entities)) {
            return false;
        }

        if (!compareArrays(_intents, myObject._intents)) {
            return false;
        }

        return true;
    }
}
