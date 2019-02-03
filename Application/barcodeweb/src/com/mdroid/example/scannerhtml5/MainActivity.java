
package com.mdroid.example.scannerhtml5;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.webkit.GeolocationPermissions;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.moniuliuma.android.decode.BarcodeManager;
import com.moniuliuma.android.decode.ConfigException;
import com.moniuliuma.android.decode.DecodeResult;
import com.moniuliuma.android.decode.ReadListener;
import com.moniuliuma.android.decode.StartListener;
import com.moniuliuma.android.decode.StopListener;
import com.moniuliuma.android.decode.TimeoutListener;
import com.moniuliuma.android.decode.configuration.ScannerProperties;

import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import android.webkit.JavascriptInterface;
import com.google.gson.Gson;

/*
 * Example of webkit application able to start decoding and able to access to decoder
 * configuartion from a local or remote html page
 * this example uses a local html page contained by the apk itself under the /assets directory
 */
public class MainActivity extends Activity implements ReadListener, StartListener, StopListener,
        TimeoutListener {

    private final static String LOG_TAG = "BarcodeScanner";

    BarcodeManager decoder = null;

    ReadListener listener = null;

    WebView mWebView;

    private static String default_url = "file:///android_asset/www/index.html";

    private final MainActivity app = this;

    Settings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        if (decoder == null) {
            decoder = new BarcodeManager();
            decoder.open();
        }

        mWebView = (WebView) findViewById(R.id.webView1);

        /*
         * This method stops the application to launch the browser with an
         * intent when we load an URL
         */
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (!url.matches("file://" + getPageUrl("index"))) {
                    webSettings.setOpen(false);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                app.onPageComplete();
            }
        });

        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        mWebView.setLongClickable(false);
        // mWebView.setOnTouchListener(new View.OnTouchListener() {
        // @Override
        // public boolean onTouch(View v, MotionEvent event) {
        // return (event.getAction() == MotionEvent.ACTION_MOVE);
        // }
        // });

        mWebView.clearCache(true);
        WebSettings settings = mWebView.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDatabasePath("/data/data/" + mWebView.getContext().getPackageName()
                + "/databases/");
        settings.setSupportZoom(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        /*
         * This method allows us to capture the JS alert event and run our own
         * code, in this case showing a native Alert dialog
         */
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     final android.webkit.JsResult result) {
                new AlertDialog.Builder(app).setTitle("Javascript Dialog").setMessage(message)
                        .setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }

                        }).setCancelable(false).create().show();
                return true;
            };

            @Override
            public void onExceededDatabaseQuota(String url, String databaseIdentifier,
                                                long currentQuota, long estimatedSize, long totalUsedQuota,
                                                WebStorage.QuotaUpdater quotaUpdater) {
                quotaUpdater.updateQuota(262140); // 256K
            };

            /*
             * Always grant geo-location permission
             */
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,
                                                           GeolocationPermissions.Callback callback) {
                Log.d("GeoLoc", "Permission for: " + origin);
                callback.invoke(origin, true, false);
            }
        });

        webSettings = new Settings(this);

        // Enable Java Script bridge to BarcodeManager class

        mWebView.addJavascriptInterface(new MyBarcodeManager(decoder), "BarcodeManager");
        mWebView.addJavascriptInterface(webSettings, "MainSettings");
        // mWebView.addJavascriptInterface(new BarGen(), "BarGen");
        // Enable Java Script bridge to configuration exposes by the
        // ScannerProperties class
        mWebView.addJavascriptInterface(new ScannerPropJScriptBridge(decoder), "ScannerProperties");
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        // Copy audio files from asset to internal storage
        ArrayList<String> files = new ArrayList<String>();
        files.add("audio/scan-new.ogg");
        files.add("audio/scan-new.mp3");

        copyToInternalStorage(files);

        File page = new File(getPageUrl("index"));
        if (page.exists()) {
            mWebView.loadUrl("file://" + page.getAbsolutePath());
        } else {
            // mWebView.loadData("Please store the web page in: "+page.getAbsolutePath(),
            // "text/plain", null);
            Log.d(LOG_TAG, "Loading default page. " + page.getAbsolutePath() + " not found");
            mWebView.loadUrl(default_url);
        }

        // mWebView.setInitialScale(100);
        settings.setGeolocationDatabasePath("/data/data/" + mWebView.getContext().getPackageName()
                + "/geo/");
        settings.setGeolocationEnabled(true);
    }

    protected void onPageComplete() {
        mWebView.setInitialScale(100);
    }
    public class MyBarcodeManager {
        BarcodeManager manager = null;
        public MyBarcodeManager(BarcodeManager decoder) {
            manager = decoder;
        }

        /*
         * Returns the configuration in JSON format
         */
        @JavascriptInterface
        public void startDecode(int timeout) {
            manager.startDecode(timeout);
        }

        @JavascriptInterface
        public void stopDecode() {
            manager.stopDecode();
        }
    }
    /*
     * Exposes the barcode scanner configuration in JSON format
     */
    public class ScannerPropJScriptBridge {
        BarcodeManager manager = null;
        public ScannerPropJScriptBridge(BarcodeManager decoder) {
            manager = decoder;
        }

        /*
         * Returns the configuration in JSON format
         */
        @JavascriptInterface
        public String edit() {
            String jsonString = "";
            ScannerProperties config = ScannerProperties.edit(manager);

            Gson gson = new Gson();
            jsonString = gson.toJson(config);

            Log.d(this.getClass().getName(), jsonString);
            return jsonString;
        }

        /*
         * Accepts the configuration in JSON format and stores into the barcode
         * scanner subsystem
         */
        @JavascriptInterface
        public void store(String jsonString, boolean persist) {
            Gson gson = new Gson();

            ScannerProperties config = gson.fromJson(jsonString, ScannerProperties.class);
            try {
                config.store(manager, persist);
            } catch (ConfigException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_about:
                new DialogFragment() {
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("HTML file location").setMessage(getWwwRoot());
                        return builder.create();
                    };
                }.show(getFragmentManager(), "about");
                break;

            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            decoder.addReadListener(this);
            decoder.addStartListener(this);
            decoder.addStopListener(this);
            decoder.addTimeoutListener(this);
            if (decoder.isWedgeEnabled()) {
                decoder.enableWedge(false);
            }
        } catch (ConfigException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (decoder != null) {
            decoder.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (webSettings.getOpen()) {
                evalJs(mWebView, "settingsPane.classList.remove('open');");
                webSettings.setOpen(false);
                return true;
            }
            if (mWebView.canGoBack()) {
                File page = new File(getPageUrl("index"));
                String home;
                if (page.exists()) {
                    home = "file://" + page.getAbsolutePath();
                } else {
                    Log.d(LOG_TAG, "Loading default page. " + page.getAbsolutePath() + " not found");
                    home = default_url;
                }

                WebBackForwardList stack = mWebView.copyBackForwardList();
                if (stack.getCurrentItem().getUrl() == home)
                    return false;

                mWebView.loadUrl(home);
                mWebView.clearHistory();
                return true;
            }
        }
        // If it wasn't the Back key or there's no web page history, bubble up
        // to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onScanStarted() {
        Log.d(LOG_TAG, "onStart");
        callJsFunction(mWebView, "onStart");
    }

    @Override
    public void onScanTimeout() {
        Log.d(LOG_TAG, "onTimeout");
        callJsFunction(mWebView, "onTimeout");
    }

    @Override
    public void onScanStopped() {
        Log.d(LOG_TAG, "onStop");
        callJsFunction(mWebView, "onStop");
    }

    @Override
    public void onRead(DecodeResult decodeResult) {
        Log.d(LOG_TAG, "onRead" + decodeResult.getText());
        callJsFunction(mWebView, "onRead", decodeResult.getText(), decodeResult.getType().name(),
                "]?0"); // This field should be removed, it's related to the AIM
        // identifier
    }

    private void callJsFunction(WebView w, String name, String... args) {
        StringBuilder js = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            // [BugFix] QC#79: Escape single quotes in scanned data otherwise it
            // messes up when evaluating JS code
            js.append("'" + args[i].replaceAll("'", "\\\\'") + "'");
            if (i != args.length - 1)
                js.append(",");
        }
        js.insert(0, name + "(");
        js.append(")");
        evalJs(w, js.toString());
    }

    private void evalJs(WebView w, String js) {
        w.loadUrl("javascript:" + js);
    }

    private void copyToInternalStorage(ArrayList<String> destFiles) {
        File destFile;
        for (int i = 0; i < destFiles.size(); i++) {

            destFile = new File(getFilesDir().getAbsolutePath() + File.separator + destFiles.get(i));

            if (!destFile.exists()) {
                Log.d(LOG_TAG, "Creating: " + destFile.getAbsolutePath());
                destFile.mkdirs();

                try {
                    CopyFromAssetsToStorage("www/" + destFiles.get(i), destFiles.get(i));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(LOG_TAG, "File exists: " + destFile.getAbsolutePath());
            }
        }
    }

    private void CopyFromAssetsToStorage(String src, String dst) throws IOException {
        InputStream is = getAssets().open(src);
        OutputStream os = new FileOutputStream(dst);

        copyStream(is, os);

        os.flush();
        os.close();
        is.close();
    }

    private void copyStream(InputStream Input, OutputStream Output) throws IOException {
        byte[] buffer = new byte[5120];
        int length = Input.read(buffer);
        while (length > 0) {
            Output.write(buffer, 0, length);
            length = Input.read(buffer);
        }
    }

    public String getPageUrl(String page) {
        return getWwwRoot() + page + ".html";
    }

    public String getWwwRoot() {
        return Environment.getExternalStorageDirectory().toString() + "/html5demo/";
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public class Settings {
        boolean open = false;

        MediaPlayer mediaPlayer;

        public Settings(Context c) {
            Uri notification = RingtoneManager.getActualDefaultRingtoneUri(c,
                    RingtoneManager.TYPE_NOTIFICATION);
            // Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
            // notification);
            // r.play();
            // mediaPlayer = MediaPlayer.create(c,
            // Uri.parse("file:///sdcard/html5demo/scannew.ogg"));
        }
        @JavascriptInterface
        public String getSoundUri() {
            return "file:///system/media/audio/notifications/Argon.ogg";
        }
        @JavascriptInterface
        public String getModel() {
            return Build.MODEL;
        }
        @JavascriptInterface
        public String getDevice() {
            return Build.DEVICE;
        }
        @JavascriptInterface
        public void play() {
            mediaPlayer.start();
        }
        @JavascriptInterface
        public String getRoot() {
            return getWwwRoot();
        }
        @JavascriptInterface
        public void setOpen(boolean o) {
            open = o;
            Log.d(LOG_TAG, "Settings pane open" + o);
        }
        @JavascriptInterface
        public boolean getOpen() {
            return open;
        }
    }
}
