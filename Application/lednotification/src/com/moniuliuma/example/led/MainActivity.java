package com.moniuliuma.example.led;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.moniuliuma.android.notification.NotificationLights;

public class MainActivity extends AppCompatActivity {
    NotificationLights mNotificationLights;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNotificationLights = new NotificationLights(this);
        Button turnonred = (Button) findViewById(R.id.button);
        turnonred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNotificationLights.indicatorLed(NotificationLights.LED_RED, 1);
            }
        });
        Button turnoffred = (Button) findViewById(R.id.button2);
        turnoffred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNotificationLights.indicatorLed(NotificationLights.LED_RED, 0);
            }
        });
        Button startBlink = (Button) findViewById(R.id.button3);
        startBlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNotificationLights.startBlink(NotificationLights.LED_GREEN, 500, 500,10);
            }
        });
        Button stopBlink = (Button) findViewById(R.id.button4);
        stopBlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNotificationLights.cancelBlink();
            }
        });
    }
}
