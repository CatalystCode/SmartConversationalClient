package com.microsoft.pct.smartconversationalclient.cache.keymatch;

import android.test.InstrumentationTestCase;

import com.microsoft.pct.smartconversationalclient.mocks.MockQueryResult;

/**
 * Created by nabar on 6/16/2016.
 */
public class RegexCacheKeyMatcherTest extends InstrumentationTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
    }

    // basic test for the regex matching
    public void testAddAndMatch() throws Throwable {
        RegexCacheKeyMatcher matcher = new RegexCacheKeyMatcher();

        MockQueryResult mockResult = new MockQueryResult("go to the kitchen", new String[] {"the kitchen"},
                new String[] {"go to"});

        matcher.addKeyMatchData(mockResult);

        CacheKeyMatchResult[] matchResults = matcher.match("Go to the living room");

        assertEquals("Results array length should be 1", matchResults.length, 1);
        assertEquals("Match results entities length should be 1 ", matchResults[0].getEntities().length, 1);
        assertEquals("Match results entities should be \"the living room\"", matchResults[0].getEntities()[0], "the living room");
        assertEquals("Match results key match should be \"go to the kitchen\"", matchResults[0].getKeyMatch(), "go to the kitchen");
    }

}
