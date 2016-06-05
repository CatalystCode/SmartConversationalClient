package com.microsoft.pct.smartconversationalclient.persistentdb;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.microsoft.pct.smartconversationalclient.common.IQueryResult;
import com.microsoft.pct.smartconversationalclient.common.MockQueryResult;

/**
 * Created by abornst on 6/1/2016.
 */
public class SnappyDBUnitTest extends InstrumentationTestCase {

    private SnappyDB _snappyDB;
    private Context _mContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _mContext = getInstrumentation().getTargetContext();
        _snappyDB = new SnappyDB(_mContext);
        _snappyDB.open();
    }

    @Override
    protected void tearDown() throws Exception{
        _snappyDB.clear();
    }

    public void testClear() throws Exception{
        //ensure that there are items in the db
        _snappyDB.put("Key",new MockQueryResult() );
        _snappyDB.put("Key1",new MockQueryResult());
        _snappyDB.put("Key2",new MockQueryResult());

        //clear
        _snappyDB.clear();

        //confirm
        assertTrue(_snappyDB.getSize()==0);
    }

    public void testSize() throws  Exception{
        //ensure that there are items in the db
        _snappyDB.put("Key",new MockQueryResult() );
        _snappyDB.put("Key1",new MockQueryResult());
        _snappyDB.put("Key2",new MockQueryResult());

        assertTrue(_snappyDB.getSize() == 3);
    }

    public void testPutAndGet() throws Exception {
        //put something in the db
        IQueryResult myIQueryResult = new MockQueryResult() ;
        _snappyDB.put("Key",myIQueryResult);

        //retrieve the object from the db
        IQueryResult someResult = _snappyDB.getObject("Key",MockQueryResult.class);
        assertTrue(myIQueryResult.equals(someResult));
    }

    public void testUpdate() throws Exception{
        //add object to db
        _snappyDB.put("Key",new MockQueryResult());
        IQueryResult oldResult = _snappyDB.getObject("Key",MockQueryResult.class);

        //update object in db
        _snappyDB.put("Key",new MockQueryResult("new query"));
        IQueryResult newResult = _snappyDB.getObject("Key",MockQueryResult.class);

        //confirm update
        assertFalse(oldResult.equals(newResult));
        assertTrue(newResult.getQuery().equals("new query"));
    }

    //test remove function
    public void testRemove() throws Exception{
        boolean keyNotFound = false;

        //add object to db
        _snappyDB.put("Key",new MockQueryResult());
        IQueryResult result = _snappyDB.getObject("Key",MockQueryResult.class);
        assertNotNull(result);

        //remove entry
        _snappyDB.remove("Key");

        //confirm remove
        try {
            _snappyDB.getObject("Key",MockQueryResult.class);
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
        _snappyDB.put("Key",new MockQueryResult() );
        _snappyDB.put("Key1",new MockQueryResult());
        _snappyDB.put("Key2",new MockQueryResult());

        //measure and close
        int dbSizeBeforeClose = _snappyDB.getSize();
        _snappyDB.close();

        //open and measure
        _snappyDB.open();
        int dbSizeAfterOpen = _snappyDB.getSize();

        assertTrue(dbSizeBeforeClose == dbSizeAfterOpen);
    }

}
