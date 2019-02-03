
package com.moniuliuma.examples.decodeintent;

import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

/**
 * Log and Toast the received action, categories, barcode data, and Symbology.
 * MainActivity.isReading will be set to false.
 */
public class IntentStartActivity extends Activity {
    private final static String TAG = "#IntentStartActivity#";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_start);

        Intent intent = getIntent();
        String action = intent.getAction();
        showMessage("Activity started with action: " + action);

        Set<String> category_all = intent.getCategories();
        StringBuilder category = new StringBuilder();
        for (String s : category_all) {
            category.append(s);
        }

        int type = intent.getIntExtra(MainActivity.EXTRA_TYPE, -1);
        String data = intent.getStringExtra(MainActivity.EXTRA_DATA);

        Log.d(TAG,
                "action: " + action + "\n" + "category: " + category.toString()
                        + "\n" + "type: " + type + "\n" + "data: " + data);

        showMessage("START_ACTIVITY: " + data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.intent_start, menu);
        return true;
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
