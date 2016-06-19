package com.microsoft.pct.smartconversationalclient.cache;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.microsoft.pct.smartconversationalclient.luis.LUISQueryResult;

/**
 * Created by abornst on 6/7/2016.
 */
public class PersistentQueriesCacheNoSyncUnitTest extends InstrumentationTestCase {

    private Context _context;
    private PersistentQueriesCacheNoSync __persistentCache;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _context = getInstrumentation().getTargetContext();
        __persistentCache = new PersistentQueriesCacheNoSync(_context, LUISQueryResult.class);
        __persistentCache.init();
    }

    public void testPutAndMatch() throws Throwable
    {
        LUISQueryResult mockResult = new LUISQueryResult();
        mockResult.setQuery("Go to the Kitchen");

        __persistentCache.put(mockResult.getQuery(),mockResult);
        LUISQueryResult cached = (LUISQueryResult) __persistentCache.matchExact("Go to the Kitchen");

        assertEquals(mockResult.getQuery(),cached.getQuery());
    }

}