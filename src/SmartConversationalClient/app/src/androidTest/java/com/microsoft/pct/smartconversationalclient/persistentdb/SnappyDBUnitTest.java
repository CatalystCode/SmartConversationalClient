package com.microsoft.pct.smartconversationalclient.persistentdb;

import android.test.InstrumentationTestCase;

import com.microsoft.pct.smartconversationalclient.common.IQueryResult;
import com.microsoft.pct.smartconversationalclient.common.QueryResultTest;

/**
 * Created by abornst on 6/1/2016.
 */
public class SnappyDBUnitTest extends InstrumentationTestCase {

    private SnappyDB _snappyDB;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _snappyDB = new SnappyDB(getInstrumentation().getTargetContext());
        _snappyDB.open();
    }

    @Override
    protected void tearDown() throws Exception{
        _snappyDB.clear();
    }

    public void testClear() throws Exception{
        //ensure that there are items in the db
        _snappyDB.put("Key",new QueryResultTest() );
        _snappyDB.put("Key1",new QueryResultTest());
        _snappyDB.put("Key2",new QueryResultTest());

        //clear
        _snappyDB.clear();

        //confirm
        assertTrue(_snappyDB.getSize()==0);
    }

    public  void testSize() throws  Exception{
        //ensure that there are items in the db
        _snappyDB.put("Key",new QueryResultTest() );
        _snappyDB.put("Key1",new QueryResultTest());
        _snappyDB.put("Key2",new QueryResultTest());

        assertTrue(_snappyDB.getSize() == 3);
    }

    public void testPutAndGet() throws Exception {
        //put something in the db
        IQueryResult myIQueryResult = new QueryResultTest() ;
        _snappyDB.put("Key",myIQueryResult);

        //retrieve the object from the db
        IQueryResult someResult = _snappyDB.getObject("Key",QueryResultTest.class);
        assertTrue(myIQueryResult.equals(someResult));
    }

    public void testUpdate() throws Exception{
        //add object to db
        _snappyDB.put("Key",new QueryResultTest());
        IQueryResult oldResult = _snappyDB.getObject("Key",QueryResultTest.class);

        //update object in db
        _snappyDB.put("Key",new QueryResultTest("new query"));
        IQueryResult newResult = _snappyDB.getObject("Key",QueryResultTest.class);

        //confirm update
        assertFalse(oldResult.equals(newResult));
        assertTrue(newResult.getQuery().equals("new query"));
    }

    //test remove function
    public void testRemove() throws Exception{
        boolean keyNotFound= false;

        //add object to db
        _snappyDB.put("Key",new QueryResultTest());
        IQueryResult result = _snappyDB.getObject("Key",QueryResultTest.class);
        assertNotNull(result);

        _snappyDB.remove("Key");

        try{
            _snappyDB.getObject("Key",QueryResultTest.class);
        }
        catch (Exception e){
            if(e.getMessage().contains("NotFound")) {
                keyNotFound = true;
            }
        }
        assertTrue(keyNotFound);
    }

    public void testOpen() throws Exception{
        //ensure that there are items in the db
        _snappyDB.put("Key",new QueryResultTest() );
        _snappyDB.put("Key1",new QueryResultTest());
        _snappyDB.put("Key2",new QueryResultTest());

        int dbSizeBeforeClose = _snappyDB.getSize();
        _snappyDB.close();

        _snappyDB.open();
        int dbSizeAfterOpen = _snappyDB.getSize();

        assertTrue(dbSizeBeforeClose == dbSizeAfterOpen);
    }
}