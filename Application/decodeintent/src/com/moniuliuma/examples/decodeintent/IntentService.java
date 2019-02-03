package com.moniuliuma.examples.decodeintent;

import java.util.Set;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Log and Toast the received action, categories, barcode data, and Symbology.
 * MainActivity.isReading will be set to false.
 * 
 * 
 */
public class IntentService extends Service {
	private final static String TAG = "#IntentService#";

	public IntentService() {
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		String action = intent.getAction();

		showMessage("Service created with action: " + action);

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
		
		showMessage("START_SERVICE: " + data);

		stopSelf();
		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		String action = intent.getAction();
		showMessage("Service created with action: " + action);

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
		
		showMessage("START_SERVICE: " + data);

		return null;
	}

	private void showMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
}
