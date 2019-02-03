package com.example.standardapi;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Reboot using standard API. Requires this to be a system app.
 * 
 * @author jpelletier
 * 
 */
public class PowerActivity extends Activity {
	private static final String[] adaptArray = { "cold", "warm", "clean" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.power);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, adaptArray);
		ListView listReboot = (ListView) findViewById(R.id.listReboot);
		listReboot.setAdapter(adapter);
		listReboot.setOnItemClickListener(new RebootListener());
	}

	/**
	 * pos is the index in adaptArray. Reboot with that reason.
	 * 
	 * @author jpelletier
	 * 
	 */
	private class RebootListener implements OnItemClickListener {
		// private static final String[] adaptArray = "cold", "warm", "clean"

		@Override
		public void onItemClick(AdapterView<?> arg0, View clicked, int pos,
				long arg3) {
			String reason = ((TextView) clicked).getText().toString();
			reboot(reason);
		}

	}

	/**
	 * Reboot for given reason.
	 * 
	 * @param reason
	 *            cold, warm, or clean
	 */
	private void reboot(String reason) {
		if (isSystem()) {
			if (reason != null && !reason.isEmpty()) {

				// This section requires System permissions
				android.os.PowerManager power = (android.os.PowerManager) PowerActivity.this
						.getSystemService(Context.POWER_SERVICE);
				power.reboot(reason);

			}
		}
	}

	/**
	 * @return True if this is a system app.
	 */
	private boolean isSystem() {
		ApplicationInfo appInf = getApplicationInfo();
		int flag = appInf.flags;
		boolean outVal = (flag & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM;
		return outVal;
	}
}
