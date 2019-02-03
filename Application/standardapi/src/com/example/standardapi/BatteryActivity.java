package com.example.standardapi;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Display Battery information.
 * 
 * @author jpelletier
 *
 */
public class BatteryActivity extends Activity {

	// Battery
	private int level;
	private int scale;
	private int level_percentage;

	// AC
	private int external_power_source;
	private boolean acPower;

	// Non-specification battery levels
	private int health;
	private int icon_small;
	private boolean present;
	private int status;
	private String technology;
	private int temperature;
	private int voltage;

	private ImageView imgBattery;
	private TextView txtBatterySpec;
	private TextView txtBatteryOther;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_battery);

		imgBattery = (ImageView) findViewById(R.id.imgBattery);
		txtBatterySpec = (TextView) findViewById(R.id.txtBatterySpec);
		txtBatteryOther = (TextView) findViewById(R.id.txtBatteryOther);

		getBatteryStatus();

		setBatterySpec();
		setBatteryOther();
	}

	/**
	 * Set all values needed from Intent.ACTION_BATTERY_CHANGED
	 */
	private void getBatteryStatus() {

		// http://developer.android.com/training/monitoring-device-state/battery-monitoring.html
		Intent intent = this.registerReceiver(null, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));

		// this is where we deal with the data sent from the battery.
		// Battery levels
		level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
		scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
		level_percentage = getBatteryLevel(level, scale);

		// AC status
		external_power_source = intent.getIntExtra(
				BatteryManager.EXTRA_PLUGGED, 0);
		acPower = (external_power_source == BatteryManager.BATTERY_PLUGGED_AC);

		// Non-specification battery status
		health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
		icon_small = intent.getIntExtra(BatteryManager.EXTRA_ICON_SMALL, 0);
		imgBattery.setImageResource(icon_small);
		present = intent.getExtras().getBoolean(BatteryManager.EXTRA_PRESENT);
		status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
		technology = intent.getExtras().getString(
				BatteryManager.EXTRA_TECHNOLOGY);
		temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
		voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
	}

	/**
	 * The battery level as described in the standard porting guide.
	 * 
	 * @return level * 100 / scale
	 */
	public static int getBatteryLevel(int level, int scale) {
		int outVal = (int) (level * 100 / (float) scale);
		return outVal;
	}

	/**
	 * Sets txtBatterySpec with level_percentage, acPower, and cradle statues.
	 * 
	 * @precondition Call getBatteryStatus first
	 */
	private void setBatterySpec() {
		String text = "battery level: " + level_percentage
				+ "\r\nAC Power Status: " + acPower + "\r\nIn Cradle Status: "
				+ getInCradleStatus() + "\r\n";
		txtBatterySpec.setText(text);

	}

	// Cradle, these values are not changed by getBatteryStatus
	private int dockState;
	private boolean inCradle;

	/**
	 * 
	 * @precondition Call getBatteryStatus first.
	 * 
	 * @return True if device is docked.
	 */
	public boolean getInCradleStatus() {
		// http://developer.android.com/training/monitoring-device-state/docking-monitoring.html
		Intent dockStatus = this.registerReceiver(null, new IntentFilter(
				Intent.ACTION_DOCK_EVENT));
		if (dockStatus != null) {
			dockState = dockStatus.getIntExtra(Intent.EXTRA_DOCK_STATE, -1);
			inCradle = (dockState != Intent.EXTRA_DOCK_STATE_UNDOCKED);
			return inCradle;
		}
		inCradle = false;
		return inCradle;
	}

	/**
	 * Fill txtBattery other with health, preset, status, technology,
	 * temperature, and voltage.
	 * 
	 * @precondition Call getBatteryStatus first.
	 */
	private void setBatteryOther() {
		String text = "Battery...\r\n" + "Health : " + health + "\r\n"
				+ "Present : " + present + "\r\n" + "Status : " + status
				+ "\r\n" + "Technology : " + technology + "\r\n"
				+ "Temperature : " + temperature + "\r\n" + "Voltage : "
				+ voltage;
		txtBatteryOther.setText(text);
	}
}
