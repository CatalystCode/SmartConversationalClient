package com.microsoft.pct.smartconversationalclient.cache;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.microsoft.pct.smartconversationalclient.luis.LUISQueryResult;

/**
 * Created by abornst on 6/7/2016.
 */
public class PersistentQueriesCacheNoSyncUnitTest extends InstrumentationTestCase {

    private Context _context;
    private PersistentQueriesCacheNoSync _persistentCache;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _context = getInstrumentation().getTargetContext();
        _persistentCache = new PersistentQueriesCacheNoSync(_context, LUISQueryResult.class);
        _persistentCache.init();
    }

    public void testPutAndMatch() throws Throwable
    {
        LUISQueryResult mockResult = new LUISQueryResult();
        mockResult.setQuery("Go to the Kitchen");

        _persistentCache.put(mockResult.getQuery(),mockResult);
        LUISQueryResult cached = (LUISQueryResult) _persistentCache.matchExact("Go to the Kitchen");

        assertEquals(mockResult.getQuery(),cached.getQuery());
    }

}