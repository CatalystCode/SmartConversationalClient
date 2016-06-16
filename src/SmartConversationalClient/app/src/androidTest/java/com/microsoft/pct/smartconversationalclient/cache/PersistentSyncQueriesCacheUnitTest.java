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
public class PersistentSyncQueriesCacheUnitTest  extends InstrumentationTestCase {

    String LUIS_APP_ID;
    String LUIS_SUBSCRIPTION_ID;

    private Context _mContext;
    private  PersistentSyncQueriesCache _pqc;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _mContext = getInstrumentation().getTargetContext();
        _pqc = new PersistentSyncQueriesCache(_mContext, LUISQueryResult.class);

        LUIS_APP_ID = _mContext.getString(R.string.luisAppID);
        LUIS_SUBSCRIPTION_ID = _mContext.getString(R.string.luisSubscriptionID);
    }

    @Override
    protected void tearDown() throws Exception{
        _pqc.clear();
    }

    public void testPutandMatch() throws Throwable
    {
        LUISClient client = new LUISClient(LUIS_APP_ID, LUIS_SUBSCRIPTION_ID, Volley.newRequestQueue(_mContext));
        LUISQueryResult result = client.queryLUIS("Go to the Kitchen");

        _pqc.put(result.getQuery(),result);

        LUISQueryResult cached = (LUISQueryResult) _pqc.matchExact("Go to the Kitchen");

        assertEquals(result.getQuery(),cached.getQuery());
    }

}