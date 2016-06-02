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
    }

    public void testFail() throws Exception {
        assertTrue("fail",false);
    }

    public void testTrue() throws Exception {
        assertTrue("ran",true);
    }

    public void  testClear() throws Exception{
        //ensure that there are items in the db populate
        _snappyDB.put("Key",new QueryResultTest() );
        _snappyDB.put("Key1",new QueryResultTest());
        _snappyDB.put("Key2",new QueryResultTest());

        //clear
        _snappyDB.clear();

        //confirm
        assertTrue( _snappyDB.getSize()==0);
    }

    public  void testSize() throws  Exception{
        _snappyDB.clear();

        //populate
        _snappyDB.put("Key",new QueryResultTest() );
        _snappyDB.put("Key1",new QueryResultTest());
        _snappyDB.put("Key2",new QueryResultTest());

        assertTrue(_snappyDB.getSize() == 3);
    }

    public void testPutAndGet() throws Exception{
        _snappyDB.clear();

        IQueryResult myIQueryResult = new QueryResultTest() ;
        _snappyDB.put("Key",myIQueryResult);

        IQueryResult someResult = _snappyDB.getObject("Key",QueryResultTest.class);
        assertTrue(myIQueryResult.equals(someResult));
    }

    public void testUpdate() throws Exception{
        _snappyDB.clear();

        _snappyDB.put("Key",new QueryResultTest());
        IQueryResult oldResult =  _snappyDB.getObject("Key",QueryResultTest.class);

        _snappyDB.put("Key",new QueryResultTest("new query"));
        IQueryResult newResult = _snappyDB.getObject("Key",QueryResultTest.class);

        assertFalse(oldResult.equals(newResult));
    }

    //test remove function
    public void testRemove() throws Exception{
        boolean keyNotFound= false;

        _snappyDB.clear();

        _snappyDB.put("Key",new QueryResultTest());
        IQueryResult result =  _snappyDB.getObject("Key",QueryResultTest.class);

        IQueryResult getResult = _snappyDB.getObject("Key",QueryResultTest.class);

        _snappyDB.remove("Key");

        try{
            _snappyDB.getObject("Key",QueryResultTest.class);
        }
        catch (Exception e){
            String keyNotFoundError = "Failed to get a String: NotFound: ";
            if(e.getMessage().equals(keyNotFoundError)) {
                keyNotFound = true;
            }
        }
        assertTrue(keyNotFound);
    }

    public void testOpen() throws Exception{
        _snappyDB.clear();

        //populate
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