package com.microsoft.pct.smartconversationalclient.persistentdb;

import com.microsoft.pct.smartconversationalclient.common.IQueryResult;

/**
 * Created by abornst on 5/29/2016.
 */
public interface IPersistentDB
{
    /*
    Inserts object into persistent db
    */
    public void put(String key, IQueryResult value) throws Exception;

    /*
    Retrieves object from persistent db
    */
    public IQueryResult get(String key) throws Exception;

    /*
    Removes object from persistent db
    */
    public void remove (String key) throws Exception;

    /*
    Clears all objects from persistent db
    */
    public void clear() throws Exception;

    /*
    Returns Size of DB
     */
    public int getSize() throws Exception;
}
