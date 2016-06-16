package com.microsoft.pct.smartconversationalclient;

import android.app.Application;
import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.toolbox.*;
import com.microsoft.pct.smartconversationalclient.cache.PersistentQueriesCache;
import com.microsoft.pct.smartconversationalclient.cache.PersistentSyncQueriesCache;
import com.microsoft.pct.smartconversationalclient.cache.QueriesCache;
import com.microsoft.pct.smartconversationalclient.cache.QueriesCacheMatch;
import com.microsoft.pct.smartconversationalclient.luis.*;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {

    static final String LOG_TAG = "MainActivity";
    static final double QUERIES_CACHE_MATCH_CONFIDENCE_THRESHOLD = 0.9;

    String LUIS_APP_ID;
    String LUIS_SUBSCRIPTION_ID;

    private SpeechRecognizer _speechRecognizer;
    private RecognitionListener _recognitionListener;
    private PersistentSyncQueriesCache _queriesCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            _queriesCache = new PersistentSyncQueriesCache(getApplicationContext(), LUISQueryResult.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        LUIS_APP_ID = this.getString(R.string.luisAppID);
        LUIS_SUBSCRIPTION_ID = this.getString(R.string.luisSubscriptionID);

        //init speech to text google recognizer
        _recognitionListener = new RecognitionListener() {
            //speech recognizer offline partial global
            private String _offlineResult;

            @Override
            public void onReadyForSpeech(Bundle params) {}

            @Override
            public void onBeginningOfSpeech() {
                _offlineResult = null;
            }

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() { }

            @Override
            public void onEvent(int eventType, Bundle params) {}

            @Override
            public void onError(int error) {
                //handle google offline bug see http://stackoverflow.com/questions/30654191/speechrecognizer-offline-error-no-match
                if (error == 7){
                    queryLuisAndShowResult(_offlineResult);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                //handle offline unstable state based on http://stackoverflow.com/questions/30654191/speechrecognizer-offline-error-no-match
                ArrayList<String> data = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                ArrayList<String> unstableData = partialResults.getStringArrayList("android.speech.extra.UNSTABLE_TEXT");
                _offlineResult = data.get(0) + unstableData.get(0);
            }

            public void onResults(Bundle results) {
                //extractIntent from closest recognized string
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                EditText control = (EditText)findViewById(R.id.editText);
                String query = matches.get(0);
                control.setText(query);
                queryLuisAndShowResult(query);
            }
        };

        _speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        _speechRecognizer.setRecognitionListener(_recognitionListener);
    }

    //On Stop doesnt always get called find a better event or method of syncing db
    @Override
    protected void onStop(){
        try {
            _queriesCache.syncMemoryWithDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }
    private void queryLuisAndShowResult(final String query) {
        new AsyncTask<String, Void, LUISQueryResult>() {
            @Override
            protected LUISQueryResult doInBackground( final String ... params ) {
                String queryText = params[0];

                // try to query the cache first:
                QueriesCacheMatch[] matchResults = new QueriesCacheMatch[0];
                try {
                    matchResults = _queriesCache.match(queryText);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (matchResults != null && matchResults.length > 0) {
                    if (matchResults[0].getMatchConfidence() > QUERIES_CACHE_MATCH_CONFIDENCE_THRESHOLD) {
                        // TODO: Add support for general queries when available
                        return (LUISQueryResult)matchResults[0].getQueryResult();
                    }
                }

                LUISClient client = new LUISClient(LUIS_APP_ID, LUIS_SUBSCRIPTION_ID, Volley.newRequestQueue(getApplicationContext()));
                try {
                    LUISQueryResult result = client.queryLUIS(queryText);
                    // add it to the cache
                    // TODO make this call async
                    _queriesCache.put(query, result);
                    return result;
                }
                catch (Throwable e) {
                    Log.e(LOG_TAG, e.getLocalizedMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute( final LUISQueryResult result ) {
                TextView control = (TextView) findViewById(R.id.resultText);
                if (result == null) {
                    control.setText("Error occured during request to LUIS");
                }

                String intent = result.getIntents()[0].getIntent();
                control.setText("Intent: " + intent);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, query);
    }

    public void textQuery(View view) {
        // get text from text field:
        EditText control = (EditText)findViewById(R.id.editText);
        String queryText = control.getText().toString();
        queryLuisAndShowResult(queryText);
    }

    void voiceQuery(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,true);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
        _speechRecognizer.startListening(intent);
    }
    
}
