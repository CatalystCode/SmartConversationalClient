package com.microsoft.pct.smartconversationalclient.cache;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.microsoft.pct.smartconversationalclient.luis.LUISQueryResult;

/**
 * Created by abornst on 6/16/2016.
 */
public class PersistentQueriesCacheUnitTest extends InstrumentationTestCase {

    private Context _context;
    private PersistentQueriesCache _persistentCache;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _context = getInstrumentation().getTargetContext();
        _persistentCache = new PersistentQueriesCache(_context);
        _persistentCache.init();
    }

    @Override
    protected void tearDown() throws Exception {
        _persistentCache.clear();
    }

    public void testPutAndMatch() throws Throwable {
        LUISQueryResult mockResult = new LUISQueryResult();
        mockResult.setQuery("Go to the Kitchen");

        _persistentCache.put(mockResult.getQuery(),mockResult);
        LUISQueryResult cached = (LUISQueryResult) _persistentCache.matchExact("Go to the Kitchen");

        assertEquals(mockResult.getQuery(),cached.getQuery());
    }

}