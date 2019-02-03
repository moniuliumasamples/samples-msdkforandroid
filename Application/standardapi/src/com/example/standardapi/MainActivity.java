package com.example.standardapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Start an activity when you click on it.
 * 
 * @author jpelletier
 * 
 */
public class MainActivity extends Activity {
	private static final String[] ACTIVITIES = { "android.os.PowerManager",
			"android.os.BatteryManager", "SYSTEM",
			"android.view.WindowManager", "android.net.wifi.WifiManager",
			"Wireless Modules", "Sounds and Vibration" };
	private static final Class<?>[] ACT_CLASSES = { PowerActivity.class,
			BatteryActivity.class, SystemInfo.class, WindowActivity.class,
			WifiActivity.class, WirelessEnableActivity.class,
			SoundActivity.class };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, ACTIVITIES);
		ListView listActivities = (ListView) findViewById(R.id.listActivities);
		listActivities.setAdapter(adapter);
		listActivities.setOnItemClickListener(new ActivityListener());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * pos is the index in ACT_CLASSES. Start that activity.
	 * 
	 * @author jpelletier
	 * 
	 */
	private class ActivityListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			Intent intent = new Intent(MainActivity.this, ACT_CLASSES[pos]);
			startActivity(intent);
		}

	}
}
