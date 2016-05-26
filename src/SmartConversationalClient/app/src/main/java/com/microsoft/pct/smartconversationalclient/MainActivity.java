package com.microsoft.pct.smartconversationalclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.toolbox.*;
import com.microsoft.pct.smartconversationalclient.luis.*;

public class MainActivity extends AppCompatActivity {

    static final String LOG_TAG = "MainActivity";
    static final String LUIS_APP_ID = "";
    static final String LUIS_SUBSCRIPTION_ID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void textQuery(View view) {
        // get text from text field:
        EditText control = (EditText)findViewById(R.id.editText);
        final String queryText =  control.getText().toString();

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
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, queryText);
    }
}
