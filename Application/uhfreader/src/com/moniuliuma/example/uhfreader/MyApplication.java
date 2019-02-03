package com.moniuliuma.example.uhfreader;

import com.moniuliuma.android.device.UHFReader;

public class MyApplication extends android.app.Application {
    private UHFReader mUHFReader = null;
    private String path;
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }
    @Override
    public void onTerminate() {
        // TODO Auto-generated method stub
        super.onTerminate();
    }
    public UHFReader getUHFReader() {
        if(mUHFReader == null) {
            mUHFReader = new UHFReader();
        }
        return mUHFReader;
    }
}
