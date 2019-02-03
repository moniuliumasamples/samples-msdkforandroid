package com.example.standardapi;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

/**
 * Display system information
 * 
 * @author jpelletier
 * 
 */
public class SystemInfo extends Activity {

	PowerManager power;
	PackageManager pkg;
	Vibrator vibrator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);

		power = (PowerManager) getSystemService(Context.POWER_SERVICE);
		pkg = getPackageManager();
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		TextView text = (TextView) findViewById(R.id.txtInfo);
		text.setText(getDescription());
		text.setMovementMethod(new ScrollingMovementMethod());
		toFile("SYSTEM_INFO.xml", getDescription());
	}

	/**
	 * @return Locale language
	 */
	public String getLanguage() {
		return Locale.getDefault().getLanguage();
	}

	/**
	 * android.os.Build
	 */
	public String getModelName() {
		return android.os.Build.MODEL;
	}

	/**
	 * android.os.Build
	 */
	public String getBrandName() {
		return android.os.Build.BRAND;
	}

	/**
	 * android.os.Build
	 */
	public String getDeviceName() {
		return android.os.Build.DEVICE;
	}

	/**
	 * android.os.Build
	 */
	public String getProductName() {
		return android.os.Build.PRODUCT;
	}

	/**
	 * android.os.Build
	 */
	public String getManufacturer() {
		return android.os.Build.MANUFACTURER;
	}

	/**
	 * android.os.Build
	 */
	public String getSerialNumber() {
		return android.os.Build.SERIAL;
	}

	/**
	 * android.content.pm.PackageManager
	 */
	public FeatureInfo[] getHardwareInfo() {
		return pkg.getSystemAvailableFeatures();
	}

	/**
	 * android.content.pm.PackageManager
	 */
	public boolean hasBluetooth() {
		return pkg.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
	}

	/**
	 * android.content.pm.PackageManager
	 */
	public boolean hasCamera() {
		return pkg.hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}

	/**
	 * android.content.pm.PackageManager
	 */
	public boolean hasGPS() {
		return pkg.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
	}

	/**
	 * android.content.pm.PackageManager
	 */
	public boolean hasWWAN() {
		return pkg.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
	}

	/**
	 * android.os.Build
	 */
	public boolean hasVibrator() {
		if (android.os.Build.VERSION.SDK_INT <= 10) {
			return true;
		}
		// the following compiles only from android 3.0
		return vibrator.hasVibrator();
	}

	/**
	 * android.content.pm.PackageManager
	 */
	public boolean hasGravity() {
		return pkg
				.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
	}

	/**
	 * android.os.Build
	 */
	public String getFirmwareVersion() {
		return android.os.Build.VERSION.INCREMENTAL;
	}

	/**
	 * android.os.Build
	 */
	public String getBootloaderVersion() {
		return android.os.Build.BOOTLOADER;
	}

	/**
	 * @return xml String describing system information.
	 */
	public String getDescription() {

		String output = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +

		"<SYSTEM_INFO>\n" +

		"<LANGUAGE>" + getLanguage() + "</LANGUAGE>\n" +

		"<SERIAL_NUMBER>" + getSerialNumber() + "</SERIAL_NUMBER>\n" +

		"<FEATURE_BLUETOOTH>" + hasBluetooth() + "</FEATURE_BLUETOOTH>\n"
				+ "<FEATURE_CAMERA>" + hasCamera() + "</FEATURE_CAMERA>\n"
				+ "<FEATURE_LOCATION_GPS>" + hasGPS()
				+ "</FEATURE_LOCATION_GPS>\n" + "<FEATURE_TELEPHONY>"
				+ hasWWAN() + "</FEATURE_TELEPHONY>\n" + "<HAS_VIBRATOR>"
				+ hasVibrator() + "</HAS_VIBRATOR>\n"
				+ "<FEATURE_SENSOR_ACCELEROMETER>" + hasGravity()
				+ "</FEATURE_SENSOR_ACCELEROMETER>\n" +

				"<MODEL>" + getModelName() + "</MODEL>\n" + "<BRAND>"
				+ getBrandName() + "</BRAND>\n" + "<DEVICE>" + getDeviceName()
				+ "</DEVICE>\n" + "<PRODUCT>" + getProductName()
				+ "</PRODUCT>\n" + "<MANUFACTURER>" + getManufacturer()
				+ "</MANUFACTURER>\n" + "<VERSION_RELEASE>"
				+ android.os.Build.VERSION.RELEASE + "</VERSION_RELEASE>\n"
				+ "<VERSION_INCREMENTAL>"
				+ android.os.Build.VERSION.INCREMENTAL
				+ "</VERSION_INCREMENTAL>\n" + "<VERSION_SDK_INT>"
				+ android.os.Build.VERSION.SDK_INT + "</VERSION_SDK_INT>\n"
				+ "<VERSION_INCREMENTAL>" + getFirmwareVersion()
				+ "</VERSION_INCREMENTAL>\n" +

				"<BOOTLOADER>" + getBootloaderVersion() + "</BOOTLOADER>\n" +

				"</SYSTEM_INFO>" + "\r\n";
		return output;
	}

	/**
	 * @param name
	 *            file to write to
	 * @param xmlString
	 *            xml string to wright to file
	 */
	void toFile(String name, String xmlString) {
		FileOutputStream fos;
		try {
			fos = openFileOutput(name, Context.MODE_PRIVATE);
			fos.write(xmlString.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
