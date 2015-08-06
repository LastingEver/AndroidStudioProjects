package com.example.lasting.servicedemo;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyBindService extends Service {
    @Override
    public void onCreate() {
        Log.i("info", "onCreate");
        super.onCreate();
    }

    public class MyBinder extends Binder{
        public MyBindService getService(){
            return MyBindService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("info", "onBind");
        return new MyBinder();
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        Log.i("info", "unbindService");
        super.unbindService(conn);
    }

    @Override
    public void onDestroy() {
        Log.i("info", "onDestroy");
        super.onDestroy();
    }

    public void play(){
        Log.i("info","播放");
    }

    public void pause(){
        Log.i("info","暂停");
    }

    public void prev(){
        Log.i("info","上一首");
    }

    public void next(){
        Log.i("info","下一首");
    }
}
