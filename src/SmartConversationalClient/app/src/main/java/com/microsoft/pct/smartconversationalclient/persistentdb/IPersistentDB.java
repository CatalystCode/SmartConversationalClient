package com.microsoft.pct.smartconversationalclient.persistentdb;

import com.microsoft.pct.smartconversationalclient.common.IQueryResult;

/**
 * Created by abornst on 5/29/2016.
 */
public interface IPersistentDB
{
    /*Inserts object into persistent db*/
    public void put(String key, IQueryResult value);

    /*Retrieves object from persistent db*/
    public IQueryResult get(String key);

    /*Removes object from persistent db*/
    public void remove (String key);

    /*Cleats all objects from persistent db*/
    public void clear();
}
