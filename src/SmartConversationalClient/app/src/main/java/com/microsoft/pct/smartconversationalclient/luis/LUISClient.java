package com.microsoft.pct.smartconversationalclient.luis;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by nadavbar on 5/23/16.
 */
public class LUISClient {
    static final String LUIS_QUERY_URL_FORMAT = "https://api.projectoxford.ai/luis/v1/application?id=%s&subscription-key=%s&q=%s";

    private String _luisAppId;
    private String _luisSubscriptionId;

    private RequestQueue _queue;

    public LUISClient(String luisAppId, String luisSubscriptionId, RequestQueue queue) {
        _luisAppId = luisAppId;
        _luisSubscriptionId = luisSubscriptionId;
        // Instantiate the cache
        _queue = queue;
    }

    public LUISQueryResult queryLUIS(String query) throws Throwable {
        String jsonResult = queryLUISEndPoint(query);
        return parseLUISResult(jsonResult);
    }

    private String createLUISQueryURL(String query) throws UnsupportedEncodingException {
        // TODO: on android use Android.Uri?
        String encoded = URLEncoder.encode(query, "UTF-8");
        return String.format(LUIS_QUERY_URL_FORMAT, _luisAppId, _luisSubscriptionId, encoded);
    }

    private String queryLUISEndPoint(String query) throws Throwable {
        String url = createLUISQueryURL(query);
        final String[] responseString = new String[1];
        final VolleyError[] requestError = new VolleyError[1];
        final CountDownLatch startSignal = new CountDownLatch(1);

        StringRequest request = new StringRequest
                (Request.Method.GET, url, new Response.Listener<String>() {

                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new android.support.v4.util.ArrayMap<String, String>();
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }

                    @Override
                    public void onResponse(String response) {
                        responseString[0] = response;
                        startSignal.countDown();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        requestError[0] = error;
                        startSignal.countDown();
                    }
                });

        // Add the request to the RequestQueue.
        _queue.add(request);
        startSignal.await();

        if (requestError[0] != null) {
            throw requestError[0].getCause();
        }

        return responseString[0];
    }

    private LUISQueryResult parseLUISResult(String result) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        LUISQueryResult value = mapper.readValue(result, LUISQueryResult.class);

        value.sortByIntent();
        return value;
    }
}
