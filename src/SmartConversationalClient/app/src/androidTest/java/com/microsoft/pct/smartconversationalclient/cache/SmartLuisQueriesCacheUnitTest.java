package com.microsoft.pct.smartconversationalclient.cache;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.android.volley.toolbox.Volley;
import com.microsoft.pct.smartconversationalclient.R;
import com.microsoft.pct.smartconversationalclient.luis.LUISClient;
import com.microsoft.pct.smartconversationalclient.luis.LUISQueryResult;

/**
 * Created by abornst on 6/16/2016.
 */
public class SmartLuisQueriesCacheUnitTest extends InstrumentationTestCase {

    String LUIS_APP_ID;
    String LUIS_SUBSCRIPTION_ID;

    private Context _context;
    private SmartLuisQueriesCache _smartCache;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _context = getInstrumentation().getTargetContext();
        _smartCache = new SmartLuisQueriesCache(_context);
        _smartCache.init();

        LUIS_APP_ID = _context.getString(R.string.luisAppID);
        LUIS_SUBSCRIPTION_ID = _context.getString(R.string.luisSubscriptionID);
    }

    @Override
    protected void tearDown() throws Exception{
        _smartCache.clear();
    }

    public void testLuisQueryToRuleKey() throws Throwable {
        LUISClient client = new LUISClient(LUIS_APP_ID, LUIS_SUBSCRIPTION_ID, Volley.newRequestQueue(_context));
        LUISQueryResult result = client.queryLUIS("Go to the Kitchen");
        String ruleKey = _smartCache.luisQRToRuleKey(result);
        assertEquals(ruleKey, "go to the (.*)");
    }

    public void testPutAndMatch() throws Throwable
    {
        LUISClient client = new LUISClient(LUIS_APP_ID, LUIS_SUBSCRIPTION_ID, Volley.newRequestQueue(_context));
        LUISQueryResult result = client.queryLUIS("Go to the Kitchen");

        _smartCache.put(result.getQuery(), result);

        LUISQueryResult cached = (LUISQueryResult) _smartCache.matchExact("Go to the Kitchen");

        assertEquals(result.getQuery(),cached.getQuery());
    }

    public void testMatchGeneralQuery() throws Throwable {
        LUISClient client = new LUISClient(LUIS_APP_ID, LUIS_SUBSCRIPTION_ID, Volley.newRequestQueue(_context));
        LUISQueryResult result = client.queryLUIS("Go to the Kitchen");
        _smartCache.put(result.getQuery(), result);

        LUISQueryResult generalizedInCache = (LUISQueryResult) _smartCache.matchExact("Go to the den");

        assertEquals(generalizedInCache.getQuery(), "Go to the den");
        assertEquals(generalizedInCache.getIntents()[0].getIntent(), "Go to");
        assertEquals(generalizedInCache.getEntities()[0].getEntity(), "den");
    }

    public void testUnknownIntent() throws Throwable{
        assertNull(_smartCache.matchExact("Pick up the kitchen"));
    }

    public void testNoKnownEntities() throws Throwable{
        LUISClient client = new LUISClient(LUIS_APP_ID, LUIS_SUBSCRIPTION_ID, Volley.newRequestQueue(_context));
        LUISQueryResult result = client.queryLUIS("Go to the Kitchen");
        _smartCache.put(result.getQuery(), result);

        LUISQueryResult generalizedInCache = (LUISQueryResult) _smartCache.matchExact("Go to the basement");

        assertEquals(generalizedInCache.getIntents()[0].getIntent(), "Go to");
        assertTrue(generalizedInCache.getEntities().length <= 0);
    };

}