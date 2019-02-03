package com.example.standardapi;

import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * View state, and enable/disable Bluetooth, Wifi, GPS, and WWAN
 * 
 * @author jpelletier
 * 
 */
public class WirelessEnableActivity extends Activity implements
		LocationListener {
	private static final String[] adaptArray = { "Bluetooth", "Wifi", "GPS",
			"WWAN" };

	private static final String CLICK_BELOW = "Click below to enable/disable a wireless module.";

	private static final int REQUEST_GPS = 1;

	private BluetoothAdapter blue;
	private WifiManager wifi;
	private LocationManager loc;

	private TextView txtWireless;

	private Handler guiHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wireless_enable);

		blue = BluetoothAdapter.getDefaultAdapter();
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		loc = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, adaptArray);
		ListView listWirelessEnable = (ListView) findViewById(R.id.listWirelessEnable);
		listWirelessEnable.setAdapter(adapter);
		listWirelessEnable.setOnItemClickListener(new WirelessEnableListener());

		txtWireless = (TextView) findViewById(R.id.txtWireless);

		guiHandler = new Handler(new UpdateGUI());

		guiHandler.sendEmptyMessage(0);
	}

	/**
	 * Set the text in txtWireless with getDescription.
	 */
	private void load() {
		// Allow for changes to take affect. }

		guiHandler.sendEmptyMessageDelayed(0, 3000);
	}

	/**
	 * Enable or disable bluetooth.
	 * 
	 * @precondition blue is defined
	 * @param enable
	 */
	private void enableBluetooth(boolean enable) {
		if (isSystem()) {
			if (enable) {
				// The following requires this to be a system app if (enable) {
				blue.enable();
			} else {
				blue.disable();
			}
		}
	}

	/**
	 * @return True if this is a system app.
	 */
	private boolean isSystem() {
		ApplicationInfo appInf = getApplicationInfo();
		boolean outVal = (appInf.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM;
		return outVal;
	}

	/**
	 * Enable or disable Wifi.
	 * 
	 * @precondition wifi is defined
	 * @param enable
	 */
	private void enableWifi(boolean enable) {
		wifi.setWifiEnabled(enable);
	}

	/**
	 * Enable or disable GPS.
	 * 
	 * @precondition loc is defined
	 * @param enable
	 */
	private void enableGPS(boolean enable) {
		if (enable) {
			loc.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 10,
					this);
		} else {
			loc.removeUpdates(this);
		}
	}

	/**
	 * Enable or disable WWAN.
	 * 
	 * @param enable
	 */
	private void enableWWAN(boolean enable) {
		String sEnable = enable ? "0" : "1";

		Settings.System.putString(getContentResolver(),
				Settings.System.AIRPLANE_MODE_RADIOS, "cell");
		Settings.System.putString(getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, sEnable);

		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		sendBroadcast(intent);
	}

	/**
	 * When an item is clicked, enable or disable that module, whichever is the
	 * opposite of the current state.
	 * 
	 * @author jpelletier
	 * 
	 */
	private class WirelessEnableListener implements OnItemClickListener {
		// String[] adaptArray = { "Bluetooth", "Wifi", "GPS", "WWAN" };

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int pos,
				long arg3) {
			boolean enable;

			switch (pos) {
			case 0:
				if (blue != null) {
					enable = !blue.isEnabled();
					enableBluetooth(enable);
					load();
				}
				break;
			case 1:
				if (wifi != null) {
					enable = !wifi.isWifiEnabled();
					enableWifi(enable);
					load();
				}
				break;
			case 2:
				// GUI and location listener are updated on activity result.
				startActivityForResult(new Intent(
						Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_GPS);
				break;
			case 3:
				enable = !isWWANEnabled();
				enableWWAN(enable);
				load();
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQUEST_GPS:
			boolean enable = isProviderEnabled();
			enableGPS(enable);
			load();
			break;
		}
	}

	/**
	 * @precondition loc is defined
	 * @return True if any GPS provider is enabled.
	 */
	private boolean isProviderEnabled() {
		boolean outVal = false;

		List<String> providers = loc.getAllProviders();
		for (String s : providers) {
			if (s.contains("gps")) {
				outVal = loc.isProviderEnabled(s);
				if (outVal)
					return outVal;
			}
		}

		return outVal;
	}

	/**
	 * @return True if airplane mode is not turned on.
	 */
	private boolean isWWANEnabled() {
		String current = Settings.System.getString(getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON);
		boolean outVal = current.contains("0");
		return outVal;
	}

	@Override
	public void onLocationChanged(Location arg0) {
		showAndLog("GPS Location Changed");
	}

	@Override
	public void onProviderDisabled(String arg0) {
		showAndLog("GPS Provider Disabled");
	}

	@Override
	public void onProviderEnabled(String arg0) {
		showAndLog("GPS Provider Enabled");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle arg2) {
		showAndLog("GPS status changed. Provider : " + provider + ", status : "
				+ status);
	}

	/**
	 * Toast, and log to debug.
	 * 
	 * @param s
	 *            message
	 */
	private void showAndLog(String s) {
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
		Log.d("#WirelessEnableActivity#", s);
	}

	/**
	 * @return String detailing which of bluetooth, wifi, GPS, and WWAN are
	 *         enabled.
	 */
	private String getDescription() {
		StringBuilder outVal = new StringBuilder();
		if (blue != null) {
			outVal.append("Bluetooth : " + blue.isEnabled() + "\r\n");
		}
		if (wifi != null) {
			outVal.append("Wifi : " + wifi.isWifiEnabled() + "\r\n");
		}
		outVal.append("GPS : " + isProviderEnabled() + "\r\n" + "WWAAN : "
				+ isWWANEnabled() + "\r\n" + CLICK_BELOW);
		return outVal.toString();
	}

	/**
	 * Any message will set txtWireless with getDescription.
	 * 
	 * @author jpelletier
	 * 
	 */
	public class UpdateGUI implements Handler.Callback {

		@Override
		public boolean handleMessage(Message msg) {
			String text = getDescription();
			txtWireless.setText(text);
			return false;
		}

	}
}
