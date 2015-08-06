package com.example.lasting.servicedemo;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

public class MainActivity extends Activity {
    private Intent intent;
    private Intent intent2;
    MyBindService myBindService;
    ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            myBindService=((MyBindService.MyBinder)iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.button1: {
                intent = new Intent(MainActivity.this, MyStartService.class);
                startService(intent);
                break;
            }
            case R.id.button2: {
                stopService(intent);
                break;
            }
            case R.id.button3: {
                intent2 = new Intent(MainActivity.this, MyBindService.class);
                bindService(intent2, serviceConnection, Service.BIND_AUTO_CREATE);
                break;
            }
            case R.id.button4:{
                myBindService.play();
                break;
            }
            case R.id.button5:{
                myBindService.pause();
                break;
            }
            case R.id.button6:{
                myBindService.prev();
                break;
            }
            case R.id.button7:{
                myBindService.next();
                break;
            }
            case R.id.button8:{
                unbindService(serviceConnection);
                break;
            }
        }
    }
}
