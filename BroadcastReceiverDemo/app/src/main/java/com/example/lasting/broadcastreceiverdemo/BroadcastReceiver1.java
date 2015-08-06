package com.example.lasting.broadcastreceiverdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class BroadcastReceiver1 extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String string=intent.getStringExtra("msg");
        Log.i("info","接收器1收到消息"+string);
        Bundle bundle=getResultExtras(true);
        String string2=bundle.getString("test");
        Log.i("info","得到的数据:"+string2);
    }
}