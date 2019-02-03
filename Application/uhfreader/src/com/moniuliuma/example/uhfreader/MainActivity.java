package com.moniuliuma.example.uhfreader;

import com.moniuliuma.android.device.UHFReader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener ,OnItemClickListener{

	private static final String LOGTAG = "UhfReader";
	MyApplication mApplication;
	private Button buttonClear;
	private Button buttonConnect;
	private Button buttonStart;
	private TextView textVersion ;
	private ListView listViewData;
	private ArrayList<EPC> listEPC;
	private ArrayList<Map<String, Object>> listMap;
	private boolean runFlag = true;
	private boolean startFlag = false;
	private boolean connectFlag = false;
	private UHFReader reader ; //超高频读写器 
	private String hardwareVersion;
	
	 private SoundPool soundpool = null;
	 private int soundid;
	private int powerValue = 2600;
	boolean enable = false;
	private Handler mMainHandler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Window window = getWindow();
	    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setOverflowShowingAlways();
		setContentView(R.layout.main);
		initView();
		mApplication = (MyApplication)getApplication();
        mMainHandler = new Handler(Looper.getMainLooper());
		soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100); // MODE_RINGTONE
        soundid = soundpool.load(this,R.raw.msg, 1);
		//获取用户设置功率,并设置
		SharedPreferences shared = getSharedPreferences("power", 0);
		powerValue = shared.getInt("value", 2600);
		Log.d(LOGTAG, "stored power value" + powerValue);
		
		/*************************/
		mWorkHandlerThread = new WorkHandlerThread("CKCARD");
        mWorkHandlerThread.startThread();
        reader = mApplication.getUHFReader();
	}
	
	private void initView(){
		buttonStart = (Button) findViewById(R.id.button_start);
		buttonConnect = (Button) findViewById(R.id.button_connect);
		buttonClear = (Button) findViewById(R.id.button_clear);
		listViewData = (ListView) findViewById(R.id.listView_data);
		textVersion = (TextView) findViewById(R.id.textView_version);
		buttonStart.setOnClickListener(this);
		buttonConnect.setOnClickListener(this);
		buttonClear.setOnClickListener(this);
		setButtonClickable(buttonStart, false);
		listEPC = new ArrayList<EPC>();
		listViewData.setOnItemClickListener(this);
		
	}
	@Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if(reader.open()){
					setButtonClickable(buttonConnect, true);
                }else {
					textVersion.setText("serialport init fail");
					setButtonClickable(buttonClear, false);
					setButtonClickable(buttonStart, false);
					setButtonClickable(buttonConnect, false);
                }
            }
        });
        if(startFlag) {
            Message m = Message.obtain(mWorkHandler, WorkHandler.MESSAGE_CARD_READ);
            mWorkHandler.sendMessage(m);
        }
    }
	
	@Override
	protected void onPause() {
		//startFlag = false;
	    Message m = Message.obtain(mWorkHandler, WorkHandler.MESSAGE_STOP_WORK);
        mWorkHandler.sendMessage(m);
		super.onPause();
	}
	
	private class WorkHandlerThread extends HandlerThread {
        private Looper myLooper;

        public WorkHandlerThread(String name) {
            super(name);
            start();
            myLooper = this.getLooper();
            Log.d(LOGTAG, "HandlerThread------------>"
                    + Thread.currentThread().getId() +  " pid " + Binder.getCallingPid() + " Uid " + Binder.getCallingUid());
        }

        public void startThread() {
            mWorkHandler = new WorkHandler(myLooper);
        }
    }
	 private WorkHandler mWorkHandler;
	 private WorkHandlerThread mWorkHandlerThread;
    private class WorkHandler extends Handler {
        public static final int MESSAGE_CARD_READ = 1;
        public static final int MESSAGE_STOP_WORK = 2;
        public static final int MESSAGE_CARD_DATA_OK = 3;

    private final Object cardReadEvent = new Object();
    private boolean cardReadNotified = false;
    private CardNotifyThread CardNotify = new CardNotifyThread();
    public WorkHandler(Looper loop) {
        super(loop);
        //CardNotify.start();
    }
    private class CardNotifyThread extends Thread {
        private List<byte[]> epcList;
        private List<Integer> rssiList;
        @Override
        public void run() {
            int ret = 0;
            while (cardReadNotified) {
                epcList = reader.inventoryRealTime(); //ÊµÊ±ÅÌŽæ
                rssiList = reader.getRssiList();
                if(epcList != null && !epcList.isEmpty()){
                    //²¥·ÅÌáÊŸÒô
                    int i = 0;
                    for(byte[] epc:epcList){
                        int rssi = rssiList.get(i);
                        i++;
                        if (epc != null)
                        {
                            String epcStr = Tools.Bytes2HexString(epc, epc.length);
                            addToList(listEPC, epcStr);
                        }
                    }
                }
                epcList = null ;
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void handleMessage(Message msg) {
        android.util.Log.d(LOGTAG, "handleMessage Sub Thread ID------------>"
                + Thread.currentThread().getId() +  " pid " + Binder.getCallingPid() + " Uid " + Binder.getCallingUid() + "--msg----------->" + msg.what);
        switch (msg.what) {
        case MESSAGE_CARD_READ:
            cardFeedback();
            break;
        case MESSAGE_STOP_WORK:
            cardReadNotified = false;
            synchronized (cardReadEvent) {
                if(CardNotify != null) {
                    CardNotify.interrupt();
                    CardNotify = null;
                }
            }
            break;
        case MESSAGE_CARD_DATA_OK:
            cardReadNotified = false;
            synchronized (cardReadEvent) {
                if(CardNotify != null) {
                    CardNotify.interrupt();
                    CardNotify = null;
                }
            }
            Bundle bundle = (Bundle) msg.obj;
            int result = msg.arg1;
            
            break;
        }
    }

        private void cardFeedback() {
            synchronized (cardReadEvent) {
                //cardReadEvent.notify();
                if(CardNotify == null) {
                    CardNotify = new CardNotifyThread();
                } else {
                    if(cardReadNotified || CardNotify.isAlive()) {
                        Log.d(LOGTAG, "CardNotify is working " + cardReadNotified);
                        return;
                    }
                }
                if(CardNotify != null) {
                    cardReadNotified = true;
                    CardNotify.start();
                }
            }
        }
        public synchronized boolean isSearchCard() {
            return cardReadNotified;
        }
    }
	/**
	 * 盘存线程
	 * @author Administrator
	 *
	 */
	class InventoryThread extends Thread{
		private List<byte[]> epcList;
		private List<Integer> rssiList;

		@Override
		public void run() {
			super.run();
			while(runFlag){
				if(startFlag){
//					reader.stopInventoryMulti()
					epcList = reader.inventoryRealTime(); //实时盘存
					rssiList = reader.getRssiList();
					if(epcList != null && !epcList.isEmpty()){
						int i = 0;
						for(byte[] epc:epcList){
							int rssi = rssiList.get(i);
							i++;
							if (epc != null)
							{
								String epcStr = Tools.Bytes2HexString(epc, epc.length);
								addToList(listEPC, epcStr);
							}
						}
					}
					epcList = null ;
					try {
						Thread.sleep(40);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	//将读取的EPC添加到LISTVIEW
		private void addToList(final List<EPC> list, final String epc){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
				    //Util.play(1, 0);
				    soundpool.play(soundid, 1, 1, 0, 0, 1);
					//第一次读入数据
					if(list.isEmpty()){
						EPC epcTag = new EPC();
						epcTag.setEpc(epc);
						epcTag.setCount(1);
						list.add(epcTag);
					}else{
						for(int i = 0; i < list.size(); i++){
							EPC mEPC = list.get(i);
							//list中有此EPC
							if(epc.equals(mEPC.getEpc())){
							mEPC.setCount(mEPC.getCount() + 1);
							list.set(i, mEPC);
							break;
						}else if(i == (list.size() - 1)){
							//list中没有此epc
							EPC newEPC = new EPC();
							newEPC.setEpc(epc);
							newEPC.setCount(1);
							list.add(newEPC);
							}
						}
					}
					//将数据添加到ListView
					listMap = new ArrayList<Map<String,Object>>();
					int idcount = 1;
					for(EPC epcdata:list){
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("ID", idcount);
						map.put("EPC", epcdata.getEpc());
						map.put("COUNT", epcdata.getCount());
						idcount++;
						listMap.add(map);
					}
					listViewData.setAdapter(new SimpleAdapter(MainActivity.this,
							listMap, R.layout.listview_item, 
							new String[]{"ID", "EPC", "COUNT"}, 
							new int[]{R.id.textView_id, R.id.textView_epc, R.id.textView_count}));
				}
			});
		}

	//设置按钮是否可用
	private void setButtonClickable(Button button, boolean flag){
		button.setClickable(flag);
		if(flag){
			button.setTextColor(Color.BLACK);
		}else{
			button.setTextColor(Color.GRAY);
		}
	}
	public void onBackPressed() {
		//super.onBackPressed();
		runFlag = false;
		if(reader != null){
			Log.d(LOGTAG, "onBackPressed------------>");
			reader.close();
		}
		super.onBackPressed();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		runFlag = false;
		if(reader != null){
			reader.close();
		}
	}
	/**
	 * 清空listview
	 */
	private void clearData(){
		listEPC.removeAll(listEPC);
		listViewData.setAdapter(null);
	}
	
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        if (keyCode == KeyEvent.KEYCODE_BACK)  
        {  
    		runFlag = false;
    		Message m = Message.obtain(mWorkHandler, WorkHandler.MESSAGE_STOP_WORK);
            mWorkHandler.sendMessage(m);
    		if(reader != null){
    			reader.close();
    		}
        }
        return super.onKeyDown(keyCode, event);
    }  

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.button_start:
			if(!startFlag){
				startFlag = true ;
				Message m = Message.obtain(mWorkHandler, WorkHandler.MESSAGE_CARD_READ);
		        mWorkHandler.sendMessage(m);
				buttonStart.setText(R.string.stop_inventory);
			}else{
			    Message m = Message.obtain(mWorkHandler, WorkHandler.MESSAGE_STOP_WORK);
		        mWorkHandler.sendMessage(m);
				startFlag = false;
				buttonStart.setText(R.string.inventory);
			}
			break;
		case R.id.button_connect:
			byte[] mac = reader.getManufacturer();
			if(mac != null && mac.length > 0){
//				reader.setWorkArea(3);///设置成欧标
				//Util.play(1, 0);

				hardwareVersion = new String(mac);
				Log.i(LOGTAG,"hardware mac: " + hardwareVersion);
				textVersion.append("MAC:"+hardwareVersion);
			}
			byte[] versionBytes = reader.getHardwareVersion();
			if(versionBytes != null && versionBytes.length > 0){
//				reader.setWorkArea(3);///设置成欧标
				//Util.play(1, 0);
			    
			    soundpool.play(soundid, 1, 1, 0, 0, 1);
				hardwareVersion = new String(versionBytes);
				textVersion.append(" HW:"+hardwareVersion);
				Log.i(LOGTAG,"hardware version: " + hardwareVersion);
				saveSharedVersion("hardware_version", hardwareVersion);
				setButtonClickable(buttonConnect, false);
				setButtonClickable(buttonStart, true);
			}
			byte[] firmware = reader.getFirmware();
			if(firmware != null && firmware.length > 0){
//				reader.setWorkArea(3);///设置成欧标
				//Util.play(1, 0);

				hardwareVersion = new String(firmware);
				Log.i(LOGTAG,"hardware firmware: " + hardwareVersion);
				textVersion.append(" FW:"+hardwareVersion);
			}
			setButtonClickable(buttonConnect, false);
			setButtonClickable(buttonStart, true);
			boolean result = reader.setOutputPowerLevel(powerValue);
			Log.d(LOGTAG,"set power to " + powerValue + reader.getOutputPowerLevel());
			break;
			
		case R.id.button_clear:
			clearData();
			break;
		default:
			break;
		}
	}
	
	private void saveSharedVersion(String versionType, String version) {
		SharedPreferences shared = getSharedPreferences("versions", 0);
		Editor editor = shared.edit();
		editor.putString(versionType, version);
		editor.commit();
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		TextView epcTextview = (TextView) view.findViewById(R.id.textView_epc);
		String epc = epcTextview.getText().toString();
		//选择EPC
//		reader.selectEPC(Tools.HexString2Bytes(epc));
		
		Toast.makeText(getApplicationContext(), epc, Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(this, MoreHandleActivity.class);
		intent.putExtra("epc", epc);
		startActivity(intent);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Intent intent = new Intent(this, SettingActivity.class);
		startActivity(intent);
//		Intent intent = new Intent(this, SettingPower.class);
//		startActivity(intent);
		return super.onMenuItemSelected(featureId, item);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
	}
	
	@Override
  	public boolean onMenuOpened(int featureId, Menu menu) {
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
				}
			}
		}
	return super.onMenuOpened(featureId, menu);
	}
	
	/**
	 * 在actionbar上显示菜单按钮
	 */
	private void setOverflowShowingAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, false);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
}
