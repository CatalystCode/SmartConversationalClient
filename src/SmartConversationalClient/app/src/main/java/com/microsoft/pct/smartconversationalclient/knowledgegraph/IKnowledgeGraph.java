package com.microsoft.pct.smartconversationalclient.knowledgegraph;

/**
 * Created by abornst on 5/31/2016.
 */
public interface IKnowledgeGraph
{

    /*
      Query takes a query string and returns a formated knowledge response
    */
    public IKnowledgeQueryResult query(String query) throws Throwable;

}
