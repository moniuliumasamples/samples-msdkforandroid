package com.example.standardapi;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Display Wifi information.
 * 
 * @author jpelletier
 * 
 */
public class WifiActivity extends Activity {

	private WifiManager wifi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi);

		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		TextView txtWifi = (TextView) findViewById(R.id.txtWifi);
		txtWifi.setText(getDescritpion());
	}

	/**
	 * @precondition wifi is defined
	 * @return The MAC address of this device.
	 */
	private String getMacAddress() {
		String outVal = wifi.getConnectionInfo().getMacAddress();
		return outVal;
	}

	/**
	 * @precondition wifi is defined
	 * @return The Rssi of this device.
	 */
	private int getRssi() {
		int outVal = wifi.getConnectionInfo().getRssi();
		return outVal;
	}

	/**
	 * @precondition wifi is defined
	 * @return The Wifi signal quality of this device.
	 */
	private int getSignalQuality() {
		int outVal = WifiManager.calculateSignalLevel(getRssi(), 32);
		return outVal;
	}

	/**
	 * @return String contains MAC address, Rssi, and Signal quality.
	 */
	private String getDescritpion() {
		String outVal = "MAC address : " + getMacAddress() + "\r\n" + "Rssi : "
				+ getRssi() + "\r\n" + "Signal quality : " + getSignalQuality()
				+ "\r\n";
		return outVal;
	}

}
