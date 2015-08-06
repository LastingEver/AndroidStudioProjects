package com.example.lasting.broadcastreceiverdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class BroadcastReceiver2 extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String string=intent.getStringExtra("msg");
        Log.i("info", "接收器2收到消息" + string);
//        abortBroadcast();
        Bundle bundle=new Bundle();
        bundle.putString("test","测试数据");
        setResultExtras(bundle);
    }
}