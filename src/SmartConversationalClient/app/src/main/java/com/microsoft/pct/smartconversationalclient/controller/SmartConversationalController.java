package com.microsoft.pct.smartconversationalclient.controller;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.toolbox.Volley;
import com.microsoft.pct.smartconversationalclient.R;
import com.microsoft.pct.smartconversationalclient.cache.PersistentQueriesCache;
import com.microsoft.pct.smartconversationalclient.cache.QueriesCacheMatch;
import com.microsoft.pct.smartconversationalclient.common.IQueryResult;
import com.microsoft.pct.smartconversationalclient.luis.LUISClient;
import com.microsoft.pct.smartconversationalclient.luis.LUISQueryResult;

/**
 * Created by abornst on 6/28/2016.
 */
public class SmartConversationalController {
    static final double QUERIES_CACHE_MATCH_CONFIDENCE_THRESHOLD = 0.9;

    String LUIS_APP_ID;
    String LUIS_SUBSCRIPTION_ID;

    private Context _context;
    private PersistentQueriesCache _queriesCache;
    private LUISClient _luisClient;

    public SmartConversationalController(Context context) {
        _context = context;

        //Init Luis Client
        LUIS_APP_ID = context.getString(R.string.luisAppID);
        LUIS_SUBSCRIPTION_ID = context.getString(R.string.luisSubscriptionID);
        _luisClient = new LUISClient(LUIS_APP_ID, LUIS_SUBSCRIPTION_ID, Volley.newRequestQueue(context));

        //Init persistent cache
        _queriesCache = new PersistentQueriesCache(context);
        try {
            _queriesCache.init();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public IQueryResult query(final String query) throws Throwable {
        IQueryResult result = queryCache(query);
        if (result == null){
            result = queryAndCacheLUIS(query);
        }

        return result;
    }

    public void clearCache() throws Exception {
        _queriesCache.clear();
    }

    private IQueryResult queryCache(final String query) throws Exception {
        QueriesCacheMatch[] matchResults = new QueriesCacheMatch[0];
        matchResults = _queriesCache.match(query);
        if (matchResults != null && matchResults.length > 0) {
            if (matchResults[0].getMatchConfidence() >= QUERIES_CACHE_MATCH_CONFIDENCE_THRESHOLD) {
                return matchResults[0].getQueryResult();
            }
        }

        return null;
    }

    private IQueryResult queryAndCacheLUIS(final String query) throws Throwable {
        LUISQueryResult luisQueryResult = _luisClient.queryLUIS(query);

        if (luisQueryResult == null || luisQueryResult.getQueryIntents().length == 0) {
            return null;
        }

        // if  the result didn't arrive from the cache but from LUIS
        new AsyncTask<IQueryResult, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(IQueryResult... params) {
                try {
                    LUISQueryResult result = (LUISQueryResult) params[0];
                    _queriesCache.put(result.getQuery(), result);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, luisQueryResult);

        return luisQueryResult;
    }

}