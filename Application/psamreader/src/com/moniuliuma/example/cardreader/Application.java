package com.moniuliuma.example.cardreader;


import com.moniuliuma.android.card.SmartCardReader;

public class Application extends android.app.Application {
	private SmartCardReader mSmartCardReader;
	@Override
	public void onCreate() {
		super.onCreate();
	}
	public SmartCardReader getSmartCardReader() {
		if(mSmartCardReader == null)
		mSmartCardReader = new SmartCardReader();
		return mSmartCardReader;
	}
}
