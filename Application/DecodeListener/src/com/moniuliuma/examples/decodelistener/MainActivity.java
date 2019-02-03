package com.moniuliuma.examples.decodelistener;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.moniuliuma.android.decode.BarcodeManager;
import com.moniuliuma.android.decode.DecodeResult;
import com.moniuliuma.android.decode.ReadListener;
import com.moniuliuma.android.decode.StartListener;
import com.moniuliuma.android.decode.StopListener;
import com.moniuliuma.android.decode.TimeoutListener;

public class MainActivity extends Activity implements StartListener, StopListener , TimeoutListener{

	BarcodeManager decoder = null;
	ReadListener listener = null;
	TextView mBarcodeText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mBarcodeText = (TextView) findViewById(R.id.editText1);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			this.finish();
			return true;
		default:
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
			if (decoder == null) {
				decoder = new BarcodeManager();
				listener = new ReadListener() {

					@Override
					public void onRead(DecodeResult decodeResult) {
						// TODO Auto-generated method stub
						mBarcodeText.setText(decodeResult.getText());
					}

				};
				decoder.addReadListener(listener);
				decoder.addStartListener(this);
				decoder.addStopListener(this);
				decoder.addTimeoutListener(this);
			}

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (decoder != null) {
			try {
				decoder.removeReadListener(listener);
				decoder.removeStartListener(this);
				decoder.removeStopListener(this);
				decoder.removeTimeoutListener(this);
				decoder = null;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

    @Override
    public void onScanTimeout() {
        // TODO Auto-generated method stub
        Toast.makeText(this, "decode timeout", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onScanStopped() {
        // TODO Auto-generated method stub
        Toast.makeText(this, "decode onScanStopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onScanStarted() {
        // TODO Auto-generated method stub
        Toast.makeText(this, "decode onScanStarted", Toast.LENGTH_SHORT).show();
    }
}
