
package com.moniuliuma.example.cardreader;

import java.util.Arrays;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.Toast;

import com.moniuliuma.android.card.APDUCmd;
import com.moniuliuma.android.card.ATR;
import com.moniuliuma.example.cardreader.util.AppUtil;
import com.moniuliuma.example.cardreader.util.General;

public class MainActivity extends BaseActivity {
    private final static String TAG=MainActivity.class.getSimpleName();
    private Application mApplication;
    private EditText mCmdEditText,mReptEditText;
    private Button mSendBtn;
    private Button mPowerOn;
    private Button mPowerOff;
    private Button mRandom;
    private Button mClear;
    CheckBox mCheckBox;
    private boolean isSending = false;
    private boolean isOn = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.main);
        initView();
        mApplication = (Application) getApplication();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mApplication.getSmartCardReader().open();
    }
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mApplication.getSmartCardReader().close();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        mCheckBox = (CheckBox) findViewById(R.id.checkBox1);
        mCmdEditText=(EditText) findViewById(R.id.cmd_content);
        mCmdEditText.setHint("hex(0-9, A-F, a-f).eg.008400000008");
        mReptEditText=(EditText) findViewById(R.id.rept_content);
        mSendBtn=(Button) findViewById(R.id.cmd_btn);
        mSendBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!isOn) return;

                String cmd=mCmdEditText.getText().toString().trim();
                if(cmd==null||cmd.equals("")){
                    Toast.makeText(MainActivity.this, R.string.cmd_not_null, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(isHexAnd16Byte(cmd) == false) {
                    return;
                }
                byte[] data = hexStringToByteArray(cmd);
                if(data.length < 6)
                    return;
                //CLA   INS   P1   P2   Lc   Data   le
                //00    A4    02   0C   02   01     01
                APDUCmd apduCmd = new APDUCmd();
                apduCmd.setCla(data[0]);
                apduCmd.setIns(data[1]);
                apduCmd.setP1(data[2]);
                apduCmd.setP2(data[3]);
                apduCmd.setLc(data[4]);
                apduCmd.setLe(data[data.length - 1]);

                Arrays.fill(apduCmd.getDataIn(), (byte) 0);
                if(data[4] > 0) {
                    int j = 0;
                    for(int i = 5;i < data.length - 1;i++)
                    {
                        apduCmd.getDataIn()[j++] = data[i];
                    }
                }
                //General.strcpy(apduCmd.getDataIn(), data);
                int ret=-100;
                ret = mApplication.getSmartCardReader().exchangeAPDU(apduCmd);
                byte[] dataOutTemp= new byte[1024];
                Arrays.fill(dataOutTemp, (byte)0);
                General.DatBcdToAsc(dataOutTemp, apduCmd.getDataOut(), apduCmd.getDataOutLen()*2);
                if(ret==0 && apduCmd.getSwa()==(byte)0x90 && apduCmd.getSwb()==(byte)0x00)
                {
                    onDataReceived(String.format("ret=%d, APDU successed swa=0x%2x swb=0x%2x dataOut=%s", ret, apduCmd.getSwa(),apduCmd.getSwb(),new String(dataOutTemp)));
                }
                else
                {
                    onDataReceived(String.format("ret=%d,APDU failed swa=0x%2x swb=0x%2x dataOut=%s", ret, apduCmd.getSwa(),apduCmd.getSwb(),new String(dataOutTemp)));
                }
            }
        });
        mPowerOn=(Button) findViewById(R.id.poweron);
        mPowerOn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ATR atr = mApplication.getSmartCardReader().activate(1);
                if(atr != null) {
                    String rsp = "";
                    byte[] buffer = atr.buffer;
                    if (buffer != null) {
                        rsp = AppUtil.bytesToHexString(buffer, 0, atr.nLength);
                        onDataReceived("OK:" + rsp);

                        isOn = true;
                    }
                }
            }
        });
        mPowerOff=(Button) findViewById(R.id.poweroff);
        mPowerOff.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                isOn = false;
                mApplication.getSmartCardReader().deactivate();
            }
        });
        mRandom=(Button) findViewById(R.id.random);
        mRandom.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isOn) return;
                APDUCmd cmd = new APDUCmd();
                cmd.setCla((byte) 0x00);
                cmd.setIns((byte) 0x84);
                cmd.setP1((byte) 0x00);
                cmd.setP2((byte) 0x00);
                cmd.setLc(0);
                cmd.setLe(8);
                Arrays.fill(cmd.getDataIn(), (byte) 0);
                int ret = mApplication.getSmartCardReader().exchangeAPDU(cmd);
                String rsp = "";
                byte[] dataOutTemp= new byte[1024];
                Arrays.fill(dataOutTemp, (byte)0);
                General.DatBcdToAsc(dataOutTemp, cmd.getDataOut(), cmd.getDataOutLen()*2);

                if(ret==0 && cmd.getSwa()==(byte)0x90 && cmd.getSwb()==(byte)0x00) {
                    onDataReceived(String.format("ret=%d, get Random successed swa=0x%2x swb=0x%2x dataOut=%s", ret, cmd.getSwa(),cmd.getSwb(),new String(dataOutTemp)));
                } else {
                    onDataReceived(String.format("ret=%d, get Random failed swa=0x%2x swb=0x%2x dataOut=%s", ret, cmd.getSwa(),cmd.getSwb(),new String(dataOutTemp)));
                }
				/*if (buffer != null) {
					if(mCheckBox.isChecked()) {
		           		rsp = AppUtil.bytesToHexString(buffer, 0, buffer.length);
						onDataReceived("OK:" + rsp);
					} else {
						onDataReceived("OK:" + AppUtil.bytesToHexString(buffer, 5, buffer.length-1-2));
					}
				}*/
                //progressSendAction();
            }
        });
        mClear=(Button) findViewById(R.id.clear);
        mClear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mReptEditText.setText("");
            }
        });
    }
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        try {
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                        + Character.digit(s.charAt(i+1), 16));
            }
        } catch (Exception e) {
            Log.d("debug", "Argument(s) for hexStringToByteArray(String s)"
                    + "was not a hex string");
        }
        return data;
    }
    /**
     * Check if a (hex) string is pure hex (0-9, A-F, a-f) and 16 byte
     * (32 chars) long. If not show an error Toast in the context.
     * @param hexString The string to check.
     * @return True if sting is hex an 16 Bytes long, False otherwise.
     */
    public boolean isHexAnd16Byte(String hexString) {
        if (hexString.matches("[0-9A-Fa-f]+") == false) {
            // Error, not hex.
            Toast.makeText(this, "Error: Data must be in hexadecimal(0-9 and A-F)",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        mApplication.getSmartCardReader().close();
        super.onBackPressed();
        System.exit(0);
    }

    @Override
    protected void onDataReceived(final String reStr) {
        runOnUiThread(new Runnable() {
            public void run() {
                if(reStr != null) {
                    mReptEditText.append("\n");
                    mReptEditText.append(reStr);
                    mReptEditText.append("\n");
                }

            }
        });
    }
    private final int MESSAGE_TIMEOUT = 0;
    class ProcessHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TIMEOUT:
                    mReptEditText.append(getString(R.string.wireless_send_fail));
                    mReptEditText.append("\n");
                    break;
                default:
                    break;
            }
        }
    }
}
