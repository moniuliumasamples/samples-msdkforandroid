package com.moniuliuma.example.uhfreader;

import com.moniuliuma.android.device.UHFReader;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
/**
 * 设置功率，调节距离
 * @author Administrator
 *
 */
public class SettingPower extends Activity implements OnClickListener{

	private Button buttonMin;
	private Button buttonPlus;
	private Button buttonSet;
	private EditText editValues ;
	private int value = 26 ;//初始值为最大，2600为26dbm(value范围16dbm~26dbm)
	private UHFReader reader ;
	
	private Button buttonSetPath;
	private String portPath = "/dev/ttyMT1";
	private EditText editPortPath;
	MyApplication mApplication;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.setting_power);
		super.onCreate(savedInstanceState);
		initView();
		mApplication = (MyApplication)getApplication();
		reader = mApplication.getUHFReader();
	}
	
	private void initView(){
		buttonMin = (Button) findViewById(R.id.button_set_sensitivity);
		buttonPlus = (Button) findViewById(R.id.button_set_power);
		buttonSet = (Button) findViewById(R.id.button_set_work_area);
		editValues = (EditText) findViewById(R.id.editText_power);
		
		buttonSetPath = (Button) findViewById(R.id.button_set_path);
		buttonSetPath.setOnClickListener(this);
		editPortPath = (EditText) findViewById(R.id.editText_port_path);
		portPath = getSharedPortPath();
		editPortPath.setText(portPath);
		
		buttonMin.setOnClickListener(this);
		buttonPlus.setOnClickListener(this);
		buttonSet.setOnClickListener(this);
		value =  getSharedValue();
		editValues.setText("" +value);
		
	}

	//获取存储Value
	private int getSharedValue(){
		SharedPreferences shared = getSharedPreferences("power", 0);
		return shared.getInt("value", 26);
	}
	
	private String getSharedPortPath() {
		SharedPreferences shared = getSharedPreferences("portPath", 0);
		return shared.getString("portPath", "/dev/ttyMT1");
	}

	private void saveSharedPortPath(String portPath) {
		SharedPreferences shared = getSharedPreferences("portPath", 0);
		Editor editor = shared.edit();
		editor.putString("portPath", portPath);
		editor.commit();
	}

	//保存Value
	private void saveSharedValue(int value){
		SharedPreferences shared = getSharedPreferences("power", 0);
		Editor editor = shared.edit();
		editor.putInt("value", value);
		editor.commit();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_set_sensitivity://减
			if(value > 16){
				value = value - 1;
			}
			editValues.setText(value + "");
			break;
		case R.id.button_set_power://ŒÓ
			if(value < 26){
				value = value + 1;
			}
			editValues.setText(value + "");
			break;
		case R.id.button_set_work_area://ÉèÖÃ
			if(reader.setOutputPowerLevel(value)){
				saveSharedValue(value);
				Toast.makeText(getApplicationContext(), "设置成功", 0).show();
			}else{
				Toast.makeText(getApplicationContext(), "设置失败", 0).show();
			}
		    break;
		case R.id.button_set_path: //设置串口设备路径
			break;

		default:
			break;
		}
		
	}
	
}
