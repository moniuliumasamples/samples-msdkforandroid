
package com.moniuliuma.examples.decodesampleapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.moniuliuma.android.decode.BarcodeManager;
import com.moniuliuma.android.decode.ConfigException;
import com.moniuliuma.android.decode.DecodeResult;
import com.moniuliuma.android.decode.ReadListener;
import com.moniuliuma.android.decode.StartListener;
import com.moniuliuma.android.decode.StopListener;
import com.moniuliuma.android.decode.TimeoutListener;

public class MainActivity extends Activity implements ReadListener, StartListener, TimeoutListener,
        StopListener {
    public final static String TAG = "DecodeSampleAPI";

    private TextView mBarcodeText;
    private TextView mSymbologyText;
    private TextView mExtraDataText;

    private Button mSoftScan;

    private BarcodeManager decoder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBarcodeText = (TextView) findViewById(R.id.txtBarcode);
        mSymbologyText = (TextView) findViewById(R.id.txtSymb);
        mExtraDataText = (TextView) findViewById(R.id.extraData);

        mSoftScan = (Button) findViewById(R.id.btnScan);

        if (decoder == null) {
            decoder = new BarcodeManager();
        }

        mSoftScan.setOnTouchListener(new Button.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mSoftScan.setBackgroundResource(R.drawable.button_scan_pre);
                        if (decoder != null) {
                            decoder.startDecode(5000);
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        mSoftScan.setBackgroundResource(R.drawable.button_scan_nor);
                        if (decoder != null) {
                            decoder.stopDecode();
                        }
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            actionThisListen();
            if (decoder.isWedgeEnabled())
                decoder.enableWedge(false);

        } catch (ConfigException e) {
            e.printStackTrace();
            Log.e(TAG, "Unable to add listeners to decoder");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (decoder != null) {
            decoder.removeReadListener(this);
            decoder.removeTimeoutListener(this);
            decoder.removeStartListener(this);
            decoder.removeStopListener(this);
        }
    }

    @Override
    public void onScanTimeout() {
        showMessage("Scanning timed out");
        Log.d(TAG, "Scan timeout");
    }

    @Override
    public void onScanStarted() {
        mBarcodeText.setText("");
        mSymbologyText.setText("");
        mExtraDataText.setText("");
        showMessage("Scanner Start");
        Log.d(TAG, "Scan start");
    }

    @Override
    public void onRead(DecodeResult data) {
        byte[] bData = data.getData();
        String bDataHex = encodeHex(bData);
        String text = data.getText();
        String symb = data.getType().name();

        // Select data in text boxes
        mBarcodeText.setText(text);
        mSymbologyText.setText("Symbology = " + symb);
        String extraMessage = "DATA = " + encodeHex(data.getData()) + "\r\n";
        extraMessage += "LEN = " + data.getData().length + "\r\n";
        mExtraDataText.setText(extraMessage);

        showMessage("Scanner Read");
    }

    @Override
    public void onScanStopped() {
        Log.d(TAG, "Scan stopped");
        showMessage("Scanner Stop");
    }

    /**
     * Activated by R.id.action_this_listen. Add this object as all listeners to
     * decoder
     */
    private void actionThisListen() {
        decoder.addReadListener(this);
        decoder.addTimeoutListener(this);
        decoder.addStartListener(this);
        decoder.addStopListener(this);
        Log.v(TAG, "Now listening for scan events");
    }

    /**
     * Pattern '[ ' followed by all bytes as hexadecimal, spaces in-between each
     * byte. Closed with ']'
     * 
     * @param data Array of bytes to convert to hexadecimal Strings
     * @return String representation of data, as hexadecimal values
     */
    String encodeHex(byte[] data) {

        StringBuffer hexString = new StringBuffer();
        hexString.append('[');
        for (int i = 0; i < data.length; i++) {
            hexString.append(' ');
            String hex = Integer.toHexString(0xFF & data[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        hexString.append(']');
        return hexString.toString();
    }

    /**
     * Make toast.
     * 
     * @param msg
     */
    protected void showMessage(String msg) {
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();

    }
}
