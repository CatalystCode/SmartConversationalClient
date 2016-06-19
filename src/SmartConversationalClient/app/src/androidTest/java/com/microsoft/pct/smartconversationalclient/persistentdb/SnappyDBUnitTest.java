package com.microsoft.pct.smartconversationalclient.persistentdb;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.microsoft.pct.smartconversationalclient.common.IQueryResult;
import com.microsoft.pct.smartconversationalclient.mocks.MockQueryResult;

/**
 * Created by abornst on 6/1/2016.
 */
public class SnappyDBUnitTest extends InstrumentationTestCase {

    private SnappyDB _snappyDB;
    private Context _context;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _context = getInstrumentation().getTargetContext();
        _snappyDB = new SnappyDB(_context);
        _snappyDB.open();
    }

    @Override
    protected void tearDown() throws Exception{
        _snappyDB.clear();
    }

    public void testClear() throws Exception{
        //ensure that there are items in the db
        _snappyDB.put("Key", new DBValue(new MockQueryResult()));
        _snappyDB.put("Key1", new DBValue(new MockQueryResult()));
        _snappyDB.put("Key2", new DBValue(new MockQueryResult()));

        //clear
        _snappyDB.clear();

        //confirm
        assertTrue(_snappyDB.getSize() == 0);
    }

    public void testSize() throws  Exception{
        //ensure that there are items in the db
        _snappyDB.put("Key", new DBValue(new MockQueryResult()));
        _snappyDB.put("Key1", new DBValue(new MockQueryResult()));
        _snappyDB.put("Key2", new DBValue(new MockQueryResult()));

        assertTrue(_snappyDB.getSize() == 3);
    }

    public void testPutAndGet() throws Exception {
        //put something in the db
        IQueryResult myIQueryResult = new MockQueryResult() ;
        _snappyDB.put("Key", new DBValue(myIQueryResult));

        //retrieve the object from the db

        IQueryResult someResult = (IQueryResult) _snappyDB.getValue("Key").getObject();
        assertTrue(myIQueryResult.equals(someResult));

    }

    public void testUpdate() throws Exception{
        //add object to db
        _snappyDB.put("Key", new DBValue(new MockQueryResult()));
        IQueryResult oldResult = (IQueryResult) _snappyDB.getValue("Key").getObject();

        //update object in db
        _snappyDB.put("Key", new DBValue(new MockQueryResult("new query")));
        IQueryResult newResult = (IQueryResult) _snappyDB.getValue("Key").getObject();

        //confirm update
        assertFalse(oldResult.equals(newResult));
        assertTrue(newResult.getQuery().equals("new query"));
    }

    //test remove function
    public void testRemove() throws Exception{
        boolean keyNotFound = false;

        //add object to db
        _snappyDB.put("Key", new DBValue(new MockQueryResult()));
        assertNotNull(_snappyDB.getValue("Key").getObject());

        //remove entry
        _snappyDB.remove("Key");

        //confirm remove
        try {
            _snappyDB.getValue("Key");
        }
        catch(Exception e){
            if (e.getMessage().contains("NotFound")) {
                keyNotFound = true;
            }
        }

        assertTrue(keyNotFound);
    }

    public void testOpen() throws Exception{
        //ensure that there are items in the db
        _snappyDB.put("Key", new DBValue(new MockQueryResult()));
        _snappyDB.put("Key1", new DBValue(new MockQueryResult()));
        _snappyDB.put("Key2", new DBValue(new MockQueryResult()));

        //measure and close
        int dbSizeBeforeClose = _snappyDB.getSize();
        _snappyDB.close();

        //open and measure
        _snappyDB.open();
        int dbSizeAfterOpen = _snappyDB.getSize();

        assertTrue(dbSizeBeforeClose == dbSizeAfterOpen);
    }

    public  void testGetAllKeys() throws Exception{
        //ensure that there are items in the db
        _snappyDB.put("Key", new DBValue(new MockQueryResult()));
        _snappyDB.put("Key1", new DBValue(new MockQueryResult()));
        _snappyDB.put("Key2", new DBValue(new MockQueryResult()));

        //get all keys
        String[] keys = _snappyDB.getAllKeys();

        //check length
        assertTrue(keys.length == _snappyDB.getSize());

        //check individual key values
        assertTrue(keys[0].equals("Key"));
        assertTrue(keys[1].equals("Key1"));
        assertTrue(keys[2].equals("Key2"));
    }

}
