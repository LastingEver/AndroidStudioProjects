package com.example.lasting.broadcastreceiverdemo;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter intentFilter=new IntentFilter("broadcast1");
        BroadcastReceiver2 broadcastReceiver2=new BroadcastReceiver2();
        registerReceiver(broadcastReceiver2,intentFilter);
    }

    public void doClick(View view){
        switch (view.getId()){
            case R.id.button1:{
                Intent intent=new Intent();
                intent.putExtra("msg","这是一条普通广播");
                intent.setAction("broadcast1");
                sendBroadcast(intent);
                break;
            }
            case R.id.button2:{
                Intent intent2=new Intent();
                intent2.putExtra("msg","这是一条有序广播");
                intent2.setAction("broadcast1");
                sendOrderedBroadcast(intent2, null);
                break;
            }
            case R.id.button3:{
                Intent intent3=new Intent();
                intent3.putExtra("msg","这是一条异步广播");
                intent3.setAction("broadcast2");
                sendStickyBroadcast(intent3);
                IntentFilter intentFilter=new IntentFilter("broadcast2");
                BroadcastReceiver3 broadcastReceiver3=new BroadcastReceiver3();
                registerReceiver(broadcastReceiver3,intentFilter);
                break;
            }
        }
    }
}

