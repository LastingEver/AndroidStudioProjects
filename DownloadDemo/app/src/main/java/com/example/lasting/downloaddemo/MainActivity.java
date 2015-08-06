package com.example.lasting.downloaddemo;
/**
 * 步骤
 * 1.获得网络文件的长度
 * 2.在本地创建一个文件，设置其长度
 * 3.从数据库中获得上次下载的进度
 * 4.从上次下载位置开始下载数据，同时保存进度至数据库
 * 5.将下载进度回传Activity
 * 6.下载完成后删除下载信息
 */
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lasting.downloaddemo.entities.FileInfo;
import com.example.lasting.downloaddemo.services.DownloadService;

public class MainActivity extends Activity {
    private TextView textView1;
    private ProgressBar progressBar1;
    private Button button1;
    private Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView1 = (TextView) findViewById(R.id.textView1);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        progressBar1.setMax(100);
        final FileInfo fileInfo=new FileInfo(0,"http://dldir1.qq.com/qqfile/qq/QQ7.5/15445/QQ7.5.exe","QQ7.5.exe",0,0);
        textView1.setText("文件名:"+fileInfo.getFilename());
        //添加事件监听
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DownloadService.class);
                intent.setAction(DownloadService.ACTION_START);
                intent.putExtra("fileInfo", fileInfo);
                startService(intent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, DownloadService.class);
                intent.setAction(DownloadService.ACTION_STOP);
                intent.putExtra("fileInfo", fileInfo);
                startService(intent);
            }
        });
        //注册广播接收器
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(DownloadService.ACTION_UPDATE);
        registerReceiver(broadcastReceiver,intentFilter);
    }

    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    /**
     * 更新UI的广播接收器
     */

    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadService.ACTION_UPDATE.equals(intent.getAction())){
                int finished=intent.getIntExtra("finished",0);
                progressBar1.setProgress(finished);
            }
        }
    };
}
