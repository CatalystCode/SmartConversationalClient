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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.toolbox.*;
import com.microsoft.pct.smartconversationalclient.luis.*;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecognitionListener {

    static final String LOG_TAG = "MainActivity";
    static final String LUIS_APP_ID = "";
    static final String LUIS_SUBSCRIPTION_ID = "";


    private SpeechRecognizer sr;

    //speech recognizer offline partial global
    private String oResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init speech to text google recognizer
        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(this);
    }

    private void queryLuisAndShowResult(final String query) {
        new AsyncTask<String, Void, LUISResult>() {
            @Override
            protected LUISResult doInBackground( final String ... params ) {
                String queryText = params[0];
                LUISClient client = new LUISClient(LUIS_APP_ID, LUIS_SUBSCRIPTION_ID, Volley.newRequestQueue(getApplicationContext()));
                try {
                    LUISResult result = client.queryLUIS(queryText);
                    return result;
                }
                catch (Throwable e) {
                    Log.e(LOG_TAG, e.getLocalizedMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute( final LUISResult result ) {
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
        String queryText =  control.getText().toString();
        queryLuisAndShowResult(queryText);
    }

    void voiceQuery(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,true);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
        sr.startListening(intent);

    }


    //Google SpeechRecognizer Overrides

    @Override
    public void onReadyForSpeech(Bundle params) {}

    @Override
    public void onBeginningOfSpeech() {
        oResult = null;
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
        //handle google offline bug
        if (error ==7){
            queryLuisAndShowResult(oResult);
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        //handle offline unstable state
        ArrayList<String> data = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        ArrayList<String> unstableData = partialResults.getStringArrayList("android.speech.extra.UNSTABLE_TEXT");
        oResult = data.get(0) + unstableData.get(0);
    }

    public void onResults(Bundle results) {
        //extractIntent from closest recognized string

        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        EditText control = (EditText)findViewById(R.id.editText);
        String query = matches.get(0);
        control.setText(query);

        queryLuisAndShowResult(query);
    }




}
