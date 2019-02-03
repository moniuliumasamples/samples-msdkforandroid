
package com.moniuliuma.example.cardreader.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class AppUtil {
    public final static DateFormat mDateTimeFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public static Calendar getStr2Calendar(String date) {
        Calendar startDate = Calendar.getInstance();
        try {
            startDate.setTime(mDateTimeFormat.parse(date));
        } catch (ParseException e) {
        }
        return startDate;
    }

    public static String getDateTime2Str(Calendar date) {
        return mDateTimeFormat.format(date.getTime());
    }

    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        java.lang.reflect.Field field = null;
        int x = 0;
        int statusBarHeight = 0;
        try
        {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
            return statusBarHeight;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    public static void hideIME(Context context) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            View view = ((Activity) context).getCurrentFocus();
            if (view != null) {
                IBinder mTokenBinder = view.getApplicationWindowToken();
                if (mTokenBinder != null) {
                    imm.hideSoftInputFromWindow(mTokenBinder, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
    }

    public static String hexToStringGBK(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
        try {
            s = new String(baKeyword, "GBK");// UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
            return "";
        }
        return s;
    }

    public static String gbkStrToHex(String str) {
        try {
            byte[] b = str.getBytes("GBK");
            String data = "";
            for (int i = 0; i < b.length; i++) {
                String strTmp = Integer.toHexString(b[i]);
                if (strTmp.length() > 2)
                    strTmp = strTmp.substring(strTmp.length() - 2);
                data = data + strTmp;
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String hexString = "0123456789ABCDEF";

    public static String toString2Hex(String str) {
        // 根据默认编码获取字节数组
        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        // 将字节数组中每个字节拆解成2位16进制整数
        for (int i = 0; i < bytes.length; i++) {
            sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }

//    public static String toHex2String(String bytes) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream(
//                bytes.length() / 2);
//        // 将每2位16进制整数组装成一个字节
//        for (int i = 0; i < bytes.length(); i += 2)
//            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
//                    .indexOf(bytes.charAt(i + 1))));
//        return new String(baos.toByteArray());
//    }
    
    public static String toHex2String(String s){
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++){
            try{
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e){
                e.printStackTrace();
            }
        }try{
            s = new String(baKeyword, "utf-8");// UTF-16le:Not
        } catch (Exception e1){
            e1.printStackTrace();
        }
        return s;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        try {
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                        + Character.digit(s.charAt(i + 1), 16));
            }
        } catch (Exception e) {
            // Log.d("debug", "Argument(s) for hexStringToByteArray(String s)"
            // + "was not a hex string");
        }
        return data;
    }

    public static boolean isHexAnd16Byte(String hexString, Context context) {
        if (hexString.matches("[0-9A-Fa-f]+") == false) {
            // Error, not hex.
            Toast.makeText(context,
             "Error: Data must be in hexadecimal(0-9 and A-F)",
             Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static String bytesToHexString(byte[] src, int offset, int length) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = offset; i < length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String fillStrLen(String data, int size) {
        if (data != null && data.length() < size) {
            String newData = data;
            int count = size - data.length();
            for (int i = 0; i < count; i++) {
                newData = "0" + newData;
            }
            return newData;
        }
        return data;
    }

    public static String checkSum(byte[] arr) {
        String data = "";
        int sum = 0;
        if (arr != null) {
            int size = arr.length;
            for (int i = 0; i < size; i++) {
                sum = sum + arr[i] & 0xFF;
            }
            sum = (~sum) + 1 & 0xFF;
            if (sum > 0xf0)
                sum -= 16;
        }
        data = Integer.toHexString(sum);
        if (data.length() < 2) {
            data = "0" + data;
        }
        return data;
    }
    
    public static boolean isCanUseSim(Context context) { 
        try { 
            TelephonyManager mgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE); 
            return TelephonyManager.SIM_STATE_READY == mgr 
                    .getSimState(); 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
        return false; 
    } 
}
