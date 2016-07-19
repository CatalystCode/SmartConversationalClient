package com.microsoft.pct.smartconversationalclient.knowledgegraph;

import com.microsoft.pct.smartconversationalclient.common.IQueryResult;

/**
 * Created by abornst on 6/28/2016.
 */
public interface IKnowledgeQueryResult extends IQueryResult {

    /*
    Return a string that represents the query result as spoken text.
     */
    public String getSpokenText() throws Exception;

    /*
    Returns the original query string.
    */
    public String getQuery();

}
