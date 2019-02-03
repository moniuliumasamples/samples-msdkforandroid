
package com.moniuliuma.example.cardreader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;


/**
 */
public abstract class BaseActivity extends AppCompatActivity {

    private final static String TAG = BaseActivity.class.getSimpleName();
    protected Application mApplication;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mApplication = (Application) getApplication();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    protected abstract void onDataReceived(final String buffer);
}
