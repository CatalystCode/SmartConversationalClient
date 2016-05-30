package com.microsoft.pct.smartconversationalclient;

import android.test.ActivityTestCase;
import com.microsoft.pct.smartconversationalclient.common.IQueryResult;
import com.microsoft.pct.smartconversationalclient.persistentdb.SnappyDB;

import org.junit.Test;


/**
 * Created by abornst on 5/30/2016.
 */


public class SnappyDBUnitTest extends ActivityTestCase {

    private SnappyDB _snappyDB;
    public void setup() throws Exception {
      _snappyDB = new SnappyDB(getActivity().getApplicationContext());
    }

    // test insert and get
    @Test
    public void testPutAndGet() throws Exception{
        IQueryResult myIQueryResult = new IQueryResult() {
            @Override
            public String getQuery() {
                return "some query";
            }
        };
        _snappyDB.put("Key",myIQueryResult);
        assertEquals(myIQueryResult, _snappyDB.get("Key"));
    }

    @Test
    public void testUpdate() throws Exception{
        IQueryResult oldIQueryResult = _snappyDB.get("Key");
        IQueryResult myIQueryResult = new IQueryResult() {
            @Override
            public String getQuery() {
                return "new query";
            }
        };
        _snappyDB.put("Key",myIQueryResult );
        assertNotSame(oldIQueryResult, _snappyDB.get("Key"));
    }

    @Test
    //test remove function
    public void testRemove() throws Exception{
        _snappyDB.remove("Key");
        assertNull(_snappyDB.get("Key"));
    }

    @Test
    public void  testClear() throws Exception{
        IQueryResult myIQueryResult = new IQueryResult() {
            @Override
            public String getQuery() {
                return "new query";
            }
        };
        //populate
        _snappyDB.put("Key",myIQueryResult );
        _snappyDB.put("Key1",myIQueryResult );
        _snappyDB.put("Key2",myIQueryResult );
        //clear
        _snappyDB.clear();
        //confirm
        assertTrue((_snappyDB.get("Key")==null) &&(_snappyDB.get("Key1")==null) &&(_snappyDB.get("Key2")==null));

    }
}

