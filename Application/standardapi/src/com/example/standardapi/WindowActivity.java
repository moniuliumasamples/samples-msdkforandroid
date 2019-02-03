package com.example.standardapi;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Click an item, set the brightness.
 * 
 * @author jpelletier
 *
 */
public class WindowActivity extends Activity {
	private static final String[] adaptArray = { ".01", "0.25", "0.50", "0.75",
			"1" };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_window);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, adaptArray);
		ListView listBrightness = (ListView) findViewById(R.id.listBrightness);
		listBrightness.setAdapter(adapter);
		listBrightness.setOnItemClickListener(new BrightnessListener());
	}
	
	/**
	 * Click an item, set the brightness.
	 * 
	 * @author jpelletier
	 *
	 */
	private class BrightnessListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View item, int arg2,
				long arg3) {
			String text = ((TextView) item).getText().toString();
			Float floatVal = Float.parseFloat(text);
			
			WindowManager.LayoutParams lp = getWindow().getAttributes() ;
			lp.screenBrightness = floatVal ;
			
			getWindow().setAttributes(lp);
		}
		
	}
}
