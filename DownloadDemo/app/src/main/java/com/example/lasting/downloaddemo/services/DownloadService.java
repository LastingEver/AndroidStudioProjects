package com.example.lasting.downloaddemo.services;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.lasting.downloaddemo.entities.FileInfo;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends Service {

    public static final String DOWNLOAD_PATH= Environment.getExternalStorageDirectory().getAbsolutePath()+"/downloads/";
    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_UPDATE = "ACTION_UPDATE";
    public static final int MESSAGE_INIT=0;
    private DownloadTask downloadTask=null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_START.equals(intent.getAction())) {
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            Log.i("info","start:"+fileInfo.toString());
            //启动初始化了的线程
            new InitThread(fileInfo).start();
        }else if (ACTION_STOP.equals(intent.getAction())){
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            Log.i("info","stop:"+fileInfo.toString());
            if (downloadTask!=null){
                downloadTask.isPause=true;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_INIT:{
                    FileInfo fileInfo=(FileInfo)msg.obj;
                    Log.i("info","init:"+fileInfo);
                    //启动下载任务
                    downloadTask=new DownloadTask(DownloadService.this,fileInfo);
                    downloadTask.download();
                    break;
                }
            }
        }
    };
    /**
     * 初始化子线程
     */
    class InitThread extends Thread{
        private FileInfo fileInfo=null;
        public InitThread(FileInfo fileInfo){
            this.fileInfo=fileInfo;
        }

        public void run() {
            //连接网络文件
            HttpURLConnection conn=null;
            RandomAccessFile randomAccessFile=null;
            try {
                URL url = new URL(fileInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                int length = -1;
                if (conn.getResponseCode() == HttpStatus.SC_OK) {
                    //获得文件长度
                    length = conn.getContentLength();
                }
                if (length <= 0) {
                    return;
                }
                //判断下载路径是否存在
                File dir = new File(DOWNLOAD_PATH);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                // 在本地创建文件
                File file = new File(dir, fileInfo.getFilename());
                randomAccessFile = new RandomAccessFile(file, "rwd");//r可读，w可写，d可删
                //设置文件长度
                randomAccessFile.setLength(length);
                fileInfo.setLength(length);
                handler.obtainMessage(MESSAGE_INIT, fileInfo).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
                try {
                    randomAccessFile.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
