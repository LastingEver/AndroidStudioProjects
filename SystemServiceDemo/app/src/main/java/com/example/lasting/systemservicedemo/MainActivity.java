package com.example.lasting.systemservicedemo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.activity_main, null);
        setContentView(view);
    }

    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.button1: {
                if (isNetworkConnected(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, "当前网络已连接", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "当前网络未连接", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.button2: {
                WifiManager wifiManager = (WifiManager) MainActivity.this.getSystemService(WIFI_SERVICE);
                if (wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(false);
                    Toast.makeText(MainActivity.this, "WiFi未打开", Toast.LENGTH_SHORT).show();
                } else {
                    wifiManager.setWifiEnabled(true);
                    Toast.makeText(MainActivity.this, "WiFi已打开", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.button3: {
                AudioManager audioManager = (AudioManager) MainActivity.this.getSystemService(AUDIO_SERVICE);
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
                int volume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
                Toast.makeText(MainActivity.this, "系统最大音量:" + maxVolume + "\n" + "当前系统音量:" + volume, Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.button4: {
                ActivityManager activityManager = (ActivityManager) MainActivity.this.getSystemService(ACTIVITY_SERVICE);
                String packageName = activityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
                Toast.makeText(MainActivity.this, "当前运行的activity包名:" + packageName, Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (networkInfo != null) {
                return networkInfo.isAvailable();
            }
        }
        return false;
    }
}
