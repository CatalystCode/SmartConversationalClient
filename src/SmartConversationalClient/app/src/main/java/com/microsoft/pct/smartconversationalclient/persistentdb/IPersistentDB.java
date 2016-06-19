package com.microsoft.pct.smartconversationalclient.persistentdb;

/**
 * Created by abornst on 5/29/2016.
 */

/*
 Interface for a PersistentDB that will persist data after application is closed.
 */
public interface IPersistentDB
{
    /*
    Inserts value into persistent db
    */
    public void put(String key, DBValue value) throws Exception;

    /*
    Retrieves value from persistent db
    */
    public DBValue getValue(String key) throws Exception;

    /*
    Removes object from persistent db
    */
    public void remove (String key) throws Exception;

    /*
    Clears all objects from persistent db
    */
    public void clear() throws Exception;

    /*
    Opens the DB from local context
    */
    public void open() throws Exception;

    /*
    Closes the persistent db
     */
    public void close() throws Exception;

    /*
    Returns Size of DB
     */
    public int getSize() throws Exception;

    /*
    Gets All Keys
     */
    public String[] getAllKeys() throws Exception;

}
