package com.example.lasting.notificationdemo;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity implements View.OnClickListener{
    private NotificationManager notificationManager;
    private int notificationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button1:{
                sendNotification();
                break;
            }
            case R.id.button2:{
                notificationManager.cancel(notificationId);
                break;
            }
        }
    }

    private void sendNotification(){
        Intent intent=new Intent(this,MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,0);
        Notification.Builder builder=new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setTicker("hello world");
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle("notification");
        builder.setContentText("this is a notification");
        builder.setContentIntent(pendingIntent);
//        builder.setDefaults(Notification.DEFAULT_SOUND);
//        builder.setDefaults(Notification.DEFAULT_LIGHTS);
//        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setDefaults(Notification.DEFAULT_ALL);
        Notification notification=builder.build();
        notificationManager.notify(notificationId,notification);
    }
}
