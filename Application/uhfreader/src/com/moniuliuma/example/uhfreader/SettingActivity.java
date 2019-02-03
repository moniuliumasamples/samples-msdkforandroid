
package com.moniuliuma.example.uhfreader;

import com.moniuliuma.android.device.Constants;
import com.moniuliuma.android.device.UHFReader;

import java.io.Reader;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends Activity implements OnClickListener {
    private Button button_set_threshold;

    private Button button_set_power;

    private Button button_set_work_area;

    private Button buttonSetPath;

    private String portPath = "/dev/ttyMT1";

    private EditText editPortPath;

    // private Button button_set_freq;
    private Spinner spinnerPower;// RF功率

    private Spinner spinnerWorkArea;// /工作区域
    // private EditText editFrequency;//频率

    private Spinner spinnerIFampGain;

    private Spinner spinnerMixerGain;

    private EditText editThreshold; // œâµ÷ãÐÖµ

    private String[] powers;

    // private String[] sensitives = null;

    private String[] areas = null;

    private ArrayAdapter<String> adapterSensitive;

    private ArrayAdapter<String> adapterPower;

    private ArrayAdapter<String> adapterArea;

    private UHFReader reader;

    private String hardwareVersion;

    private int sensitive = 0;

    private int power = 0;

    private Constants.Region area = Constants.Region.UNKNOW;

    private int frequency = 0;

    private int mixer_gain;

    private int if_gain;

    private int threshold;

    private TextView textTips;
    MyApplication mApplication;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.setting_activity);
        super.onCreate(savedInstanceState);
        mApplication = (MyApplication)getApplication();
        reader = mApplication.getUHFReader();
        initView();
    }

    private void initView() {
        hardwareVersion = getSharedHardwareVersion();

        button_set_threshold = (Button) findViewById(R.id.button_set_threshold);
        button_set_power = (Button) findViewById(R.id.button_set_power);
        button_set_work_area = (Button) findViewById(R.id.button_set_work_area);
        // button_set_freq = (Button) findViewById(R.id.button_set_freq);

        buttonSetPath = (Button) findViewById(R.id.button_set_path);
        buttonSetPath.setOnClickListener(this);
        editPortPath = (EditText) findViewById(R.id.editText_port_path);
        portPath = getSharedPortPath();
        editPortPath.setText(portPath);
        editPortPath.setVisibility(View.GONE);

        textTips = (TextView) findViewById(R.id.textViewTips);

        byte[] modemPara = null;
        modemPara = reader.getModemParam();
        try {
            Thread.sleep(40);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (modemPara != null) {
            mixer_gain = modemPara[0];
            if_gain = modemPara[1];
            int threshold_H = (int) modemPara[2] & 0xFF;
            int threshold_L = (int) modemPara[3] & 0xFF;
            threshold = (threshold_H << 8) | threshold_L;
            Log.i("UhfReader", "mixer_gain:" + mixer_gain + " if_gain:" + if_gain + " threshold:"
                    + threshold);
        } else {
            Log.e("UhfReader", " get Modem Parameter Failed");
        }
        spinnerMixerGain = (Spinner) findViewById(R.id.SpinnerMixerGain);
        spinnerMixerGain.setSelection(mixer_gain);
        spinnerIFampGain = (Spinner) findViewById(R.id.SpinnerIFampGain);
        spinnerIFampGain.setSelection(if_gain);

        editThreshold = (EditText) findViewById(R.id.editText_threshold);
        editThreshold.setText("0x" + Integer.toHexString(threshold));
        spinnerPower = (Spinner) findViewById(R.id.spinner2);
        spinnerWorkArea = (Spinner) findViewById(R.id.spinner3);
        // editFrequency = (EditText) findViewById(R.id.edit4);
        // sensitives = getResources().getStringArray(R.array.arr_sensitivity);
        areas = getResources().getStringArray(R.array.arr_area);

        if (hardwareVersion != null) {
            if (hardwareVersion.contains("26dBm")) {
                powers = new String[] {
                        "26dBm", "25dBm", "24dBm", "23dBm", "22dBm", "21dBm", "20dBm", "19dBm",
                        "18dBm", "17dBm", "16dBm", "15dBm"
                };
            } else if (hardwareVersion.contains("20dBm")) {
                powers = new String[] {
                        "20dBm", "18.5dBm", "17dBm", "15.5dBm", "14.5dBm", "13dBm"
                };
            } else {
                powers = new String[] {
                        "26dBm", "25dBm", "24dBm", "23dBm", "22dBm", "21dBm", "20dBm", "19dBm",
                        "18dBm", "17dBm", "16dBm", "15dBm"
                };
            }
        } else {
            powers = new String[] {
                    "26dBm", "25dBm", "24dBm", "23dBm", "22dBm", "21dBm", "20dBm", "19dBm",
                    "18dBm", "17dBm", "16dBm", "15dBm"
            };
        }
        // adapterSensitive = new ArrayAdapter<String>(this,
        // android.R.layout.simple_spinner_dropdown_item, sensitives);
        adapterPower = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, powers);
        adapterArea = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
                areas);
        spinnerPower.setAdapter(adapterPower);
        spinnerWorkArea.setAdapter(adapterArea);
        button_set_threshold.setOnClickListener(this);
        button_set_power.setOnClickListener(this);
        button_set_work_area.setOnClickListener(this);
        // button_set_freq.setOnClickListener(this);
        Log.d("UhfReader", "selected getRadioRegion: " + reader.getRadioRegion().toByte());

        spinnerWorkArea.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
                // if(position == 5){
                // area = position + 1;
                // }else{
                // area = position ;
                // }
                Log.d("UhfReader", "selected getRadioRegion position: " + position);
                switch (position) {
                    case 0:
                        area = Constants.Region.CHN2;
                        textTips.setText(R.string.china2Freq);
                        break;
                    case 1:
                        area = Constants.Region.US;
                        textTips.setText(R.string.usaFreq);
                        break;
                    case 2:
                        area = Constants.Region.EUR;
                        textTips.setText(R.string.euFreq);
                        break;
                    case 3:
                        area = Constants.Region.CHN1;
                        textTips.setText(R.string.china1Freq);
                        break;
                    case 4:
                        area = Constants.Region.KOREA;
                        textTips.setText(R.string.koreaFreq);
                        break;

                    default:
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        spinnerWorkArea.setSelection(reader.getRadioRegion().toByte() -1);
        spinnerPower.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Log.d("UhfReader", "selected power: " + powers[position]);
                power = (int) (100 * Double.valueOf(powers[position].replace("dBm", "")));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    @Override
    public void onClick(View v) {
        Log.e("", "sensitive = " + sensitive + "; power =  " + power);
        switch (v.getId()) {
            case R.id.button_set_threshold:
                mixer_gain = spinnerMixerGain.getSelectedItemPosition();
                if_gain = spinnerIFampGain.getSelectedItemPosition();
                threshold = Integer.valueOf(editThreshold.getText().toString().replace("0x", ""),
                        16);
                reader.setModemParam(mixer_gain, if_gain, threshold);
                Log.i("Uhfreader", "set mixer_gain:" + mixer_gain + ",IF_amp_gain:" + if_gain
                        + ",theshold:" + threshold);
                Toast.makeText(getApplicationContext(), R.string.setSuccess, 0).show();
                break;
            case R.id.button_set_power:
                reader.setOutputPowerLevel(power);
                Toast.makeText(getApplicationContext(), R.string.setSuccess, 0).show();
                break;
            case R.id.button_set_work_area:
                reader.setRadioRegion(area);
                Toast.makeText(getApplicationContext(), R.string.setSuccess, 0).show();
                break;
            case R.id.button_set_path:

                break;
            default:
                break;
        }

    }

    private String getSharedHardwareVersion() {
        SharedPreferences sharedVersion = getSharedPreferences("versions", 0);
        String hardware_version = sharedVersion.getString("hardware_version", "M100 26dBm");
        return hardware_version;
    }

    private String getSharedPortPath() {
        SharedPreferences shared = getSharedPreferences("portPath", 0);
        return shared.getString("portPath", "/dev/ttyMT2");
    }

    private void saveSharedPortPath(String portPath) {
        SharedPreferences shared = getSharedPreferences("portPath", 0);
        Editor editor = shared.edit();
        editor.putString("portPath", portPath);
        editor.commit();
    }

}
