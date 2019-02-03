package com.moniuliuma.example.nfcreader;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Bundle;
import android.app.PendingIntent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
//import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
//import android.nfc.tech.NfcF;
import android.widget.TextView;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    NfcAdapter nfcAdapter;
    TextView mTextMessage;
    BottomNavigationView navigation;
    IntentFilter[] intentFiltersArray;
    String[][] techListsArray;
    PendingIntent pendingIntent;
    private LinearLayout mLinearLayout;
    private LinearLayout mLinearLayout2;
    private LinearLayout mLinearLayout3;
    private ScrollView mScrollView;
    private Button done;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mLinearLayout2.setVisibility(View.GONE);
                    mLinearLayout3.setVisibility(View.GONE);
                    mLinearLayout.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_dashboard:
                    mLinearLayout.setVisibility(View.GONE);
                    mLinearLayout2.setVisibility(View.VISIBLE);
                    mLinearLayout3.setVisibility(View.GONE);
                    return true;
                /*case R.id.navigation_notifications:
                    mLinearLayout.setVisibility(View.GONE);
                    mLinearLayout2.setVisibility(View.GONE);
                    mLinearLayout3.setVisibility(View.VISIBLE);
                    return true;*/
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mLinearLayout = (LinearLayout)findViewById(R.id.page_1);
        mLinearLayout2 = (LinearLayout)findViewById(R.id.page2);
        mLinearLayout3 = (LinearLayout)findViewById(R.id.page3);
        //get the NFC adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Log.d("MainActivity","Equipment do not support NFC");
            finish();
            return;
        }
        if (!nfcAdapter.isEnabled()) {
            Log.d("MainActivity","Please enabled NFC from the settings");
            finish();
            return;
        }

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter ndef1 = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter ndef2 = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter ndef3 = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);

        try {
            ndef2.addDataType("*/*");    /* Handles all MIME based dispatches.
                                           You should specify only the ones that you need. */
        }
        catch (MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }

        intentFiltersArray = new IntentFilter[] {ndef1, ndef2, ndef3, };

        //techListsArray = new String[][] { new String[] { NfcF.class.getName() }, new String[] {NfcA.class.getName()} };

        techListsArray = new String[][] {};
        initListener();
    }

    private void showInfo(String msg){
        //mScrollView.setVisibility(View.VISIBLE);
        //mLinearLayout2.setVisibility(View.VISIBLE);
        //promt.setText(msg);
    }
    @Override
    protected void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);

    }

    @Override
    public void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    public void onNewIntent(Intent intent) {
        //Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        //do something with tagFromIntent
        mTextMessage.setText("");
        processIntent(intent);
    }


    //字符序列转换为16进制字符串
    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length <= 0) {
            return null;
        }

        System.out.println("CCW " + src.length);
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            //System.out.println(buffer);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }

    private String getHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    private void processIntent(Intent intent)
    {
        //取出封装在intent中的TAG
        String metaInfo = "Android support for tag\n";
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        metaInfo += " TAG ID: " +  getHex(tagFromIntent.getId())+ "\n";
        for (String tech : tagFromIntent.getTechList())
        {
            metaInfo += " " + tech + "\n";
        }
        metaInfo += "\n";

        metaInfo += tagFromIntent.toString() + "\n\n";

        mifareclassic = MifareClassic.get(tagFromIntent);
        if (mifareclassic != null)
        {
            try
            {
                metaInfo += "Mifare Classic\n";
                mifareclassic.connect();//en
                int mfType = mifareclassic.getType();//TAG type
                switch(mfType) {
                    case MifareClassic.TYPE_CLASSIC:
                        metaInfo += "TYPE: TYPE_CLASSIC\n";
                        break;
                    case MifareClassic.TYPE_PLUS:
                        metaInfo += "TYPE: TYPE_PLUS\n";
                        break;
                    case MifareClassic.TYPE_PRO:
                        metaInfo += "TYPE: TYPE_PRO\n";
                        break;
                    case MifareClassic.TYPE_UNKNOWN:
                        metaInfo += "TYPE: TYPE_UNKNOWN\n";
                        break;
                }
                int mSize = mifareclassic.getSize();//SIZE_MINI  320byte SIZE_1K 1024byte   SIZE_2K 2048 byte     SIZE_4K 4096byte
                metaInfo += "Size:  "+ mSize+ "K \n";
                int sectorCount = mifareclassic.getSectorCount();

                int bIndex;
                int bCount;



                for (int j = 0; j < sectorCount; j++)
                {
                    metaInfo += "Sector " + j;
                    //Authenticate a sector with key A.

                    if (mifareclassic.authenticateSectorWithKeyA(j, MifareClassic.KEY_DEFAULT) == true)
                    {
                        byte[] data;
                        metaInfo += " OK\n";

                        bCount = mifareclassic.getBlockCountInSector(j);//return 4 or 16;for SIZE_MINI SIZE_1K SIZE_2K a Sector and SIZE_4K first 32 sector each with 4 blocks
                        //SIZE_4K the last 8 sectors contain 16 blocks.
                        bIndex = mifareclassic.sectorToBlock(j);//Return the first block of a given sector.

                        for (int x = 0; x < bCount; ++x)
                        {
                            data = mifareclassic.readBlock(bIndex);
                            metaInfo += bytesToHexString(data) + "\n";
                            ++bIndex;
                        }
                    }
                    else
                    {
                        if(mifareclassic.authenticateSectorWithKeyA(j, MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY) == true)
                            metaInfo += "Key: " + bytesToHexString(MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY) + "\n";

                        if(mifareclassic.authenticateSectorWithKeyA(j, MifareClassic.KEY_NFC_FORUM) == true)
                            metaInfo += "Key: " + bytesToHexString(MifareClassic.KEY_NFC_FORUM) + "\n";

                        metaInfo += " Failed to read\n";
                    }

                    //write into a 1st block of sector 15
                    if (j == 15)
                    {
                        //metaInfo += "This is sector 15, performing special write to 1st block";
                        //bIndex = mifareclassic.sectorToBlock(j);
						/*
						byte[] cw = {(byte) 0,(byte) 1,(byte) 2,(byte) 3,
								(byte) 4,(byte) 5,(byte) 6,(byte) 7,
								(byte) 8,(byte) 9,(byte) 10,(byte) 11,
								(byte) 12,(byte) 13,(byte) 14,(byte) 15};

						mifareclassic.writeBlock(bIndex, cw);
						*/

						/*
						mifareclassic.increment(bIndex, 2);
						mifareclassic.transfer(bIndex);
						*/
                    }
                }
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        /*The MIFARE Ultralight C consists of a 192 byte EEPROM. The first 4 pages
        * are for OTP, manufacturer data, and locking bits. The next 36 pages are the
        * user read/write area. The next 4 pages are additional locking bits, counters
        * and authentication configuration and are readable. The final 4 pages are for
        * the authentication key and are not readable. For more information see the
        * NXP data sheet MF0ICU2.*/
        MifareUltralight mifareultralight = MifareUltralight.get(tagFromIntent);
        if (mifareultralight != null)
        {
            int pages;
            int loop;
            byte data[];
            metaInfo += "Mifare Ultralight\n";
            metaInfo += "Max transceive length is " + mifareultralight.getMaxTransceiveLength() + "\n";
            metaInfo += "PAGE_SIZE constant is " + MifareUltralight.PAGE_SIZE + "\n";

            switch (mifareultralight.getType())
            {

                case MifareUltralight.TYPE_ULTRALIGHT:
                    metaInfo += "MifareUltralight type orignal\n";
                    pages = 16;
                    break;

                case MifareUltralight.TYPE_ULTRALIGHT_C:
                    metaInfo += "MifareUltralight type C\n";
                    pages = 48;
                    break;

                case MifareUltralight.TYPE_UNKNOWN:
                    metaInfo += "MifareUltralight type unknown\n";
                    pages = 0;
                    break;

                default:
                    metaInfo += "Unknown MifareUltalight.getType() value " + mifareultralight.getType() + "\n";
                    pages = 0;
                    break;
            }

            try
            {
                mifareultralight.connect();

                if (mifareultralight.isConnected())
                {
                    for (loop = 0; loop < pages / 4; ++loop)//The MIFARE Ultralight protocol always reads 4 pages at a time, to
                    // reduce the number of commands required to read an entire tag.
                    {
                        data = mifareultralight.readPages(loop * 4);//Read 4 pages (16 bytes)
                        metaInfo += loop + ":" + bytesToHexString(data) + "\n";
                    }
                }

                mifareultralight.close();
            }
            catch (Exception e)
            {
                metaInfo += "Error in reading the tag";
            }
        }

        NfcA nfca = NfcA.get(tagFromIntent);//ISO 14443-3A
        if (nfca != null)
        {
            //byte data[];
            metaInfo += "\nNfcA\n";
            //data = ;
            metaInfo += "ATQA:" + bytesToHexString(nfca.getAtqa()) + "\n";
            metaInfo += "SAK:" + nfca.getSak() + "\n";
            metaInfo += "Max transceive length is " + nfca.getMaxTransceiveLength() + "\n";
            //NfcA 发送命令byte[] data: 第一个字节是命令，//具体操作需知道卡的协议
        	/*byte[] data =new byte[]{(byte)0xa0, (byte)0x03, (byte)0x41, (byte)0x31, (byte)0x38, (byte)0x31, (byte)0x30};
        	try {
        	    nfca.connect();
                nfca.transceive(data);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }*/
        }
        NfcB nfcb = NfcB.get(tagFromIntent);//ISO 14443-3B
        if (nfcb != null)
        {
            //byte data[];
            metaInfo += "\nNfcB\n";
            //data = ;
            metaInfo += "ApplicationData:" + bytesToHexString(nfcb.getApplicationData()) + "\n";
            metaInfo += "ProtocolInfo:" + bytesToHexString(nfcb.getProtocolInfo()) + "\n";
            metaInfo += "Max transceive length is " + nfcb.getMaxTransceiveLength() + "\n";
            //NfcB 发送命令byte[] data: 第一个字节是命令，//具体操作需知道卡的协议
            //nfcb.connect();nfcb.transceive(data);
        }

        IsoDep isoDep = IsoDep.get(tagFromIntent);//ISO 14443-4
        if (isoDep != null)
        {
            //byte data[];
            metaInfo += "\nIsoDep\n";
            //data = ;
            metaInfo += "HistoricalBytes:" + bytesToHexString(isoDep.getHistoricalBytes()) + "\n";
            metaInfo += "HiLayerResponse:" + bytesToHexString(isoDep.getHiLayerResponse()) + "\n";
            metaInfo += "Max transceive length is " + isoDep.getMaxTransceiveLength() + "\n";
            //IsoDep 发送命令byte[] data: 第一个字节是命令，//具体操作需知道卡的协议
            //isoDep.connect();isoDep.transceive(data);
        }

        NfcV nfcv = NfcV.get(tagFromIntent);//ISO 15693
        if (nfcv != null)
        {
            //byte data[];
            metaInfo += "\nNfcV\n";
            //data = ;
            metaInfo += "ResponseFlags:" + nfcv.getResponseFlags()+ "\n";
            metaInfo += "DsfId:" + nfcv.getDsfId() + "\n";
            metaInfo += "Max transceive length is " + nfcv.getMaxTransceiveLength() + "\n";
            //NfcV 发送命令byte[] data: 第一个字节是命令，//具体操作需知道卡的协议
            //nfcv.transceive(data);
        }

        NfcF nfcf = NfcF.get(tagFromIntent);//6319-4
        if (nfcf != null)
        {
            //byte data[];
            metaInfo += "\nNfcF\n";
            //data = ;
            metaInfo += "SystemCode:" + bytesToHexString(nfcf.getSystemCode()) + "\n";
            metaInfo += "Manufacturer:" + bytesToHexString(nfcf.getManufacturer()) + "\n";
            metaInfo += "Max transceive length is " + nfcf.getMaxTransceiveLength() + "\n";
            //NfcF 发送命令byte[] data: 第一个字节是命令，//具体操作需知道卡的协议
            //nfcf.transceive(data);
        }

        mLinearLayout.setVisibility(View.GONE);
        //showInfo(metaInfo);
        navigation.setSelectedItemId(R.id.navigation_dashboard);
        mTextMessage.setText(metaInfo);
    }

    protected EditText carCodeEditText;
    protected EditText typeEditText;
    protected EditText keyEdittext;
    protected EditText blockIdEditText;
    protected EditText contentEditText;
    protected Button hintButton;
    protected Button mHintButton;
    protected MifareClassic mifareclassic;
    protected LinearLayout linearLayout;
    protected LinearLayout mifareclassicLinearLayout;
    protected Button readButton;
    protected Button wriButton;
    protected Button modifyButton;
    protected boolean checkBlock() {

        if ("".equals(blockIdEditText.getText().toString().trim())) {
            blockIdEditText.setText("");
            blockIdEditText.setHintTextColor(Color.RED);
            return false;
        } else {
            // int
            // block=Integer.parseInt(blockIdEditText.getText().toString().trim());
            // if((block-1)/3==0){
            // blockIdEditText.setText("");
            // blockIdEditText.setHint("该块为秘钥块！");
            // blockIdEditText.setHintTextColor(Color.RED);
            // return false;
            // }
            return true;

        }

    }
    protected void setHintToContentEd(String msg) {
        contentEditText.setText("");
        contentEditText.setHint(msg);
        contentEditText.setHintTextColor(Color.RED);
    }

    private void initListener() {
        linearLayout = (LinearLayout) findViewById(R.id.m1_le);
        mifareclassicLinearLayout = (LinearLayout) findViewById(R.id.mifareclassic);
        readButton = (Button) findViewById(R.id.button_read);
        wriButton = (Button) findViewById(R.id.button_write);
        modifyButton = (Button) findViewById(R.id.button_modify);
        blockIdEditText = (EditText) findViewById(R.id.edittext_block_id);
        keyEdittext = (EditText) findViewById(R.id.edittext_key);
        contentEditText = (EditText) findViewById(R.id.edittext_content);
        mHintButton = (Button) findViewById(R.id.button_hint);
        readButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBlock()) {
                    MifareClassicCard mifareClassicCard = new MifareClassicCard(
                            mifareclassic);
                    int block = Integer.parseInt(blockIdEditText.getText()
                            .toString().trim());
                    String content = mifareClassicCard.readCarCode(block,
                            keyEdittext.getText().toString().trim());
                    if ("秘钥错误".equals(content) || "读取失败".equals(content))
                        setHintToContentEd(content);
                    else
                        contentEditText.setText(content);
                }

            }
        });
        wriButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkBlock()) {
                    MifareClassicCard mifareClassicCard = new MifareClassicCard(
                            mifareclassic);
                    int block = Integer.parseInt(blockIdEditText.getText()
                            .toString().trim());
                    String content = contentEditText.getText().toString()
                            .trim();
                    String result = mifareClassicCard.wirteCarCode(content,
                            block, keyEdittext.getText().toString().trim());
                    if ("".equals(result))
                        setHintToContentEd("写入成功");
                    else
                        setHintToContentEd(result);

                }

            }
        });

        modifyButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkBlock()) {
                    MifareClassicCard mifareClassicCard = new MifareClassicCard(
                            mifareclassic);
                    int block = Integer.parseInt(blockIdEditText.getText()
                            .toString().trim());
                    String content = contentEditText.getText().toString()
                            .trim();
                    String key = keyEdittext.getText().toString().trim();
                    String result = mifareClassicCard.modifyPassword(block,
                            content, key);
                    if ("".equals(result))
                        setHintToContentEd("修改成功");
                    else
                        setHintToContentEd(result);
                }

            }
        });
    }
}
