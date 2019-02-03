/**
 * 
 */
package com.example.standardapi;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

/**
 * @author jpelletier
 * 
 */
public class SoundActivity extends Activity {
	private static final int LENGTH = 10;
	private static final String[] SOUNDS = { "VIBRATE", "Beep", "Buzz" };
	private static int[] loopVals = null;
	private static String[] loops;
	private static float[] speedVals = null;
	private static String[] speeds;
	private static long[] vibrateLengths = null;
	private static String[] vibrateStrings;

	private int mSoundIndex = 0;

	private Spinner spinLoop;
	private Spinner spinSpeed;
	private Spinner spinVibLength;
	private ListView listSound;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sound);

		SoundManager.getInstance();
		SoundManager.initSounds(getBaseContext());
		SoundManager.loadSounds();

		initArr();

		spinLoop = (Spinner) findViewById(R.id.spinLoop);
		spinSpeed = (Spinner) findViewById(R.id.spinSpeed);
		spinVibLength = (Spinner) findViewById(R.id.spinVibLength);
		listSound = (ListView) findViewById(R.id.listSound);

		ArrayAdapter<String> loopAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, loops);
		ArrayAdapter<String> speedAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, speeds);
		ArrayAdapter<String> lengthAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, vibrateStrings);
		ArrayAdapter<String> soundAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, SOUNDS);

		spinLoop.setAdapter(loopAdapter);
		spinSpeed.setAdapter(speedAdapter);
		spinVibLength.setAdapter(lengthAdapter);
		listSound.setAdapter(soundAdapter);

		listSound.setOnItemClickListener(new SoundListener());
	}

	private void initArr() {
		if (loopVals == null) {
			loopVals = new int[LENGTH];
			loops = new String[loopVals.length];
			speedVals = new float[LENGTH];
			speeds = new String[speedVals.length];
			vibrateLengths = new long[LENGTH];
			vibrateStrings = new String[vibrateLengths.length];

			int n = 1;
			for (int i = 0 ; i < loopVals.length; i++ ) {
				loopVals[i] = n;
				loops[i] = "Loop : " + loopVals[i];
				n *= 2;
			}

			float dec = 0.25f;
			for (int i = 0 ; i < speedVals.length; i++ ) {
				speedVals[i] = dec;
				speeds[i] = "Speed : " + speedVals[i] ;
				dec *= 2.0f;
			}

			int nn = 256;
			for (int i = 0 ; i < speedVals.length; i++ ) {
				vibrateLengths[i] = nn;
				vibrateStrings[i] = "Vibrate length : " + vibrateLengths[i];
				nn *= 2;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sound, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_stop_sound:
			if (mSoundIndex != 0) {
				// Note, this does not terminate looping a playback.
				SoundManager.stopSound(mSoundIndex);
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		SoundManager.cleanup();
		super.onDestroy();
	}

	private class SoundListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			mSoundIndex = pos ;
			if (pos == 0) {
				int spinPos = spinVibLength.getSelectedItemPosition();
				long timeLong = vibrateLengths[spinPos];
				SoundManager.playVibrator(SoundActivity.this, timeLong);
			}
			else {
				int loop = loopVals [ spinLoop.getSelectedItemPosition()];
				float speed = speedVals [ spinSpeed.getSelectedItemPosition()];
				SoundManager.playSound(mSoundIndex, loop, speed);
			}
		}

	}
}
