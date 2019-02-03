package com.moniuliuma.examples.decodeintent;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.moniuliuma.android.decode.BarcodeManager;
import com.moniuliuma.android.decode.DecodeIntent;
import com.moniuliuma.android.decode.configuration.IntentDeliveryMode;
import com.moniuliuma.android.decode.ConfigException;
import com.moniuliuma.android.decode.Property;

public class MainActivity extends Activity {
	public static final String ACTION_RECEIVER = "com.moniuliuma.examples.decode_action";//自己定义扫描隐试输出的Intent action 名称； 系統默認"com.moniuliuma.decodewedge.ACTION";
	public static final String CATEGORY_RECEIVER = "com.moniuliuma.examples.decode_category";//自己定义扫描隐试输出的Intent 类型名称；系統默認"com.moniuliuma.decodewedge.CATEGORY";
	public static final String EXTRA_DATA = DecodeIntent.EXTRA_BARCODE_DATA;
	public static final String EXTRA_TYPE = DecodeIntent.EXTRA_BARCODE_TYPE ;

	private static final String TAG = "#DecodeIntent#";

	private final static int REQUEST_BARCODE = 1;

	private BroadcastReceiver receiver = null;
	private IntentFilter filter = null;

	private ListView listIntentType;

	private BarcodeManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		try {
			// prepare configuration for decode wedge in intent mode
			manager = new BarcodeManager();
			if(!manager.isScannerOpen())
			manager.open();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "Cannot instantiate BarcodeManager");
			return;
		}

		// Set display items in the spinner
		String[] spinArray = { "Broadcast", "Start Activity", "Start Service", "Stop Decoding" };
		ArrayAdapter<Object> adapter = new ArrayAdapter<Object>(this,
				android.R.layout.simple_list_item_1, spinArray);
		listIntentType = (ListView) findViewById(R.id.listIntentType);
		listIntentType.setAdapter(adapter);
		listIntentType.setOnItemClickListener(new MyClickedItemListener());

	}

	@Override
	protected void onResume() {
		Log.i(this.getClass().getName(), "onResume");
		super.onResume();

		// register decode wedge intent receiver
		receiver = new DecodeWedgeIntentReceiver();
		filter = new IntentFilter();
		filter.addAction(ACTION_RECEIVER);//监听自己定义扫描隐试Intent
		filter.addCategory(CATEGORY_RECEIVER);
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onPause() {
		Log.i(this.getClass().getName(), "onPause");
		super.onPause();

		// unregister decode wedge intent receiver
		unregisterReceiver(receiver);
		receiver = null;
		filter = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Log.d(TAG, "Activity result received.");
		/*if (requestCode == REQUEST_BARCODE) {
			if (resultCode == RESULT_OK) {
				// The string that is decoded
				String barcode = intent.getStringExtra("SCAN_RESULT");
				// Symbology
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				Log.d(TAG, "barcode: " + barcode + ", format: " + format);

				showMessage("ZEBRA: " + barcode);
			}
		}*/
	}

	/**
	 * One click start decoding.
	 * 
	 */
	private void startDecode() {
		Intent myintent = new Intent();
		myintent.setAction(BarcodeManager.ACTION_BARCODE_CAPTURE);
		sendBroadcast(myintent);
		//上面是用广播的方式触发扫描；
		//或者直接调用触发扫描 manager.startDecode(3000);
		
		showMessage("Barcode capturing");
	}

	/**
	 * One click to stop decoding.
	 */
	private void stopDecode() {
		Intent myintent = new Intent();
		myintent.setAction(BarcodeManager.ACTION_BARCODE_STOP);
		sendBroadcast(myintent);
		
		//上面是用广播的方式触发停止扫描；
        //或者直接调用触发停止扫描 manager.stopDecode();
		
		showMessage("Stopping capture");
	}

	/**
	 * Receives action ACTION_RECEIVER
	 *接收扫描完成广播出来的数据
	 * 
	 */
	public class DecodeWedgeIntentReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent wedgeIntent) {

			String action = wedgeIntent.getAction();

			if (action.equals(ACTION_RECEIVER)) {

				String barcode = wedgeIntent.getStringExtra(EXTRA_DATA);//获取条码数据
				

				showMessage("BROADCAST:" + barcode);
				Log.d(TAG, "Decoding Broadcast Received");
			}
		}
	}

	/**
	 * 
	 */
	public class MyClickedItemListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {

			try {
				// get the current settings from the BarcodeManager
				// disable wedge through emulated keystrokes
				// enable wedge intent
			    manager.enableWedge(false);//禁用键盘自动输出扫描数据
			 // set wedge intent action and category
			    int[] id = new int[]{
			            Property.id.WEDGE_INTENT_ACTION_NAME,//自己定义扫描隐试输出的Intent action 名称參數索引
                        Property.id.WEDGE_INTENT_CATEGORY_NAME
                        };
                    String[] action = new String[]{
                            ACTION_RECEIVER,//自己定义扫描隐试输出的Intent action 名称 字符串值
                            CATEGORY_RECEIVER};
                    manager.setPropertyStrings(id, action);
			    

				switch (pos) {
				case 0: // Broadcast}
				{
				    int[] id_buffer = new int[]{
                            Property.id.WEDGE_INTENT_ENABLE,//启用扫描隐式输出 參數索引
                            Property.id.WEDGE_INTENT_DELIVERY_MODE //启用扫描隐式输出方式为：广播
                    };
                    int[] value=new int[]{
                            1,
                            IntentDeliveryMode.BROADCAST.toInt()
                    }; 
                    
                    // set wedge intent delivery through broadcast
                    manager.setPropertyInts(id_buffer, value);
                    
                    
                    startDecode();//触发扫描
				}
					
					break;
				case 1: // Start Activity
					// set wedge intent action and category
				    {
				        int[] id_buffer = new int[]{
	                            Property.id.WEDGE_INTENT_ENABLE,//启用扫描隐式输出 參數索引；若已经启用无需再设
	                            Property.id.WEDGE_INTENT_DELIVERY_MODE//启用扫描隐式输出方式參數索引
	                    };
	                    int[] value=new int[]{
	                            1,
	                            IntentDeliveryMode.START_ACTIVITY.toInt()//启用扫描隐式输出方式为：启动一个Activity
	                    }; 
	                    
	                    // set wedge intent delivery through broadcast
	                    manager.setPropertyInts(id_buffer, value);
				    }
					startDecode();
					break;
				case 2: // Start Service
					// set wedge intent action and category
				{
                    int[] id_buffer = new int[]{
                            Property.id.WEDGE_INTENT_ENABLE,
                            Property.id.WEDGE_INTENT_DELIVERY_MODE
                    };
                    int[] value=new int[]{
                            1,
                            IntentDeliveryMode.START_SERVICE.toInt()//启用扫描隐式输出方式为：启动一个Service
                    }; 
                    
                    // set wedge intent delivery through broadcast
                    manager.setPropertyInts(id_buffer, value);
                }
                startDecode();
					break;
				
				case 3: // Stop decoding
					stopDecode();
					break;
				default:
					break;
				} // apply the new settings to the BarcodeManager Service //
			} catch (ConfigException e) {
				// TODO Auto-generated catch block e.printStackTrace();
				Log.e(TAG, "Cannot store configuration");
			}
		}

	}

	/**
	 * Simple toast.
	 * 
	 * @param message
	 */
	private void showMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
}
