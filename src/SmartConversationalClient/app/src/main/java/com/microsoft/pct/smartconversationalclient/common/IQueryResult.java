package com.microsoft.pct.smartconversationalclient.common;

/*
A query result interface to be implemented by the different types of query results
 */
public interface IQueryResult {
    /*
    Gets the query that triggered this QueryResult
     */
    String getQuery();

    /*
    Get the entities that are associated with the query result
     */
    String[] getQueryEntities();

    /*
    Get the intents that are associated with the query results, sorted by their importance
     */
    String[] getQueryIntents();
}
