package com.microsoft.pct.smartconversationalclient;

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
    private PersistentQueriesCache _queriesCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LUIS_APP_ID = this.getString(R.string.luisAppID);
        LUIS_SUBSCRIPTION_ID = this.getString(R.string.luisSubscriptionID);

        //Init persistent cache
        _queriesCache = new PersistentQueriesCache(getApplicationContext());
        try {
            _queriesCache.init();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

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

    private void queryLuisAndShowResult(final String query) {
        new AsyncTask<String, Void, LUISQueryResult>() {
            @Override
            protected LUISQueryResult doInBackground( final String ... params ) {
                String queryText = params[0];

                // try to query the cache first:
                QueriesCacheMatch[] matchResults = new QueriesCacheMatch[0];
                try {
                    matchResults = _queriesCache.match(queryText);
                }
                catch (Exception e) {
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
                if (result == null || result.getIntents().length <= 0) {
                    control.setText("Error occured during request to LUIS");
                    return;
                }
                // Add to cache
                new AsyncTask<LUISQueryResult,Void,Boolean>(){

                    @Override
                    protected Boolean doInBackground(LUISQueryResult... params) {
                        try {
                            LUISQueryResult result = params[0];
                            _queriesCache.put(result.getQuery(), result);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                        return true;
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, result);
                
                String intent = result.getIntents()[0].getIntent();
                String displayText = "Intent: " + intent;
                if (result.getEntities().length > 0){
                    displayText += "\n\nEntities: ";
                    for (LUISEntity entity : result.getEntities()){
                        displayText += entity.getEntity() + ", ";
                    }
                    displayText = displayText.substring(0, displayText.length()-2);
                }
                control.setText(displayText);
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

    public void clear (View view) {
        try {
            _queriesCache.clear();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        TextView control = (TextView) findViewById(R.id.resultText);
        control.setText("Cache Cleared!");
    }
}