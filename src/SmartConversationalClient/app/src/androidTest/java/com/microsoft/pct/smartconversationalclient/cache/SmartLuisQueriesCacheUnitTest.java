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

    private Context _mContext;
    private SmartLuisQueriesCache _slqc;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _mContext = getInstrumentation().getTargetContext();
        _slqc = new SmartLuisQueriesCache(_mContext);

        LUIS_APP_ID = _mContext.getString(R.string.luisAppID);
        LUIS_SUBSCRIPTION_ID = _mContext.getString(R.string.luisSubscriptionID);
    }

    @Override
    protected void tearDown() throws Exception{
        _slqc.clear();
    }

    public void testLuisQuerytoRuleKey() throws Throwable {
        LUISClient client = new LUISClient(LUIS_APP_ID, LUIS_SUBSCRIPTION_ID, Volley.newRequestQueue(_mContext));
        LUISQueryResult result = client.queryLUIS("Go to the Kitchen");
        String ruleKey = _slqc.luisQRToRuleKey(result);
        assertEquals(ruleKey, "go to the .*");
    }

    public void testPutandMatch() throws Throwable
    {
        LUISClient client = new LUISClient(LUIS_APP_ID, LUIS_SUBSCRIPTION_ID, Volley.newRequestQueue(_mContext));
        LUISQueryResult result = client.queryLUIS("Go to the Kitchen");

        _slqc.put(result.getQuery(), result);

        LUISQueryResult cached = (LUISQueryResult) _slqc.matchExact("Go to the Kitchen");

        assertEquals(result.getQuery(),cached.getQuery());
    }

    public void testMatchGeneralQuery() throws Throwable {
        LUISQueryResult mockQueryResult = new LUISQueryResult();
        mockQueryResult.setQuery("Go to the den");

        LUISQueryResult generalizedInCache = (LUISQueryResult) _slqc.matchExact("Go to the den");

        assertEquals(mockQueryResult.getQuery(),generalizedInCache.getQuery());
        assertEquals(generalizedInCache.getIntents()[0].getIntent(), "Go to");
        assertEquals(generalizedInCache.getEntities()[0].getEntity(), "den");
    }

    public void testUnknownIntent() throws Throwable{
        assertNull(_slqc.matchExact("Pick up the kitchen"));
    }

    public void testNoKnownEntites() throws Throwable{
        LUISQueryResult generalizedInCache = (LUISQueryResult) _slqc.matchExact("Go to the basement");

        assertEquals(generalizedInCache.getIntents()[0].getIntent(), "Go to");
        assertTrue(generalizedInCache.getEntities().length <= 0);
    };

}