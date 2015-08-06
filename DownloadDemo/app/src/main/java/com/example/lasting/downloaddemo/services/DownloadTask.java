package com.example.lasting.downloaddemo.services;

import android.content.Context;
import android.content.Intent;

import com.example.lasting.downloaddemo.databases.ThreadDAO;
import com.example.lasting.downloaddemo.databases.ThreadDAOImpl;
import com.example.lasting.downloaddemo.entities.FileInfo;
import com.example.lasting.downloaddemo.entities.ThreadInfo;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * 下载任务类
 */
public class DownloadTask {
    private Context context=null;
    private FileInfo fileInfo=null;
    private ThreadDAO threadDAO=null;
    private int finished=0;
    public boolean isPause=false;


    public DownloadTask(Context context, FileInfo fileInfo) {
        this.context = context;
        this.fileInfo = fileInfo;
        threadDAO=new ThreadDAOImpl(context);
    }

    public void download(){
        //读取数据库的线程信息
        List<ThreadInfo>threadInfos=threadDAO.getThreads(fileInfo.getUrl());
        ThreadInfo threadInfo=null;
        if (threadInfos.size()==0){
            //初始化线程信息对象
            threadInfo=new ThreadInfo(0,fileInfo.getUrl(),0,fileInfo.getLength(),0);
        }else {
            threadInfo=threadInfos.get(0);//0号下载文件
        }
        //创建子线程下载
        new DownloadThread(threadInfo).start();
    }

    /**
     * 下载线程
     */

    class DownloadThread extends Thread{
        private ThreadInfo threadInfo=null;

        public DownloadThread(ThreadInfo threadInfo){
            this.threadInfo=threadInfo;
        }

        public void run(){
            //向数据库插入线程信息
            if (!threadDAO.isExists(threadInfo.getUrl(),threadInfo.getId())){
                threadDAO.insertThread(threadInfo);
            }
            HttpURLConnection conn=null;
            InputStream inputStream=null;
            RandomAccessFile randomAccessFile=null;
            try {
                URL url=new URL(threadInfo.getUrl());
                conn= (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                //设置下载位置
                int start=threadInfo.getStart()+threadInfo.getFinished();
                conn.setRequestProperty("Range","bytes="+start+"-"+threadInfo.getEnd());
                File file=new File(DownloadService.DOWNLOAD_PATH,fileInfo.getFilename());//之前都准备好了就等现在用了
                randomAccessFile=new RandomAccessFile(file,"rwd");
                randomAccessFile.seek(start);//设置文件写入位置
                Intent intent=new Intent(DownloadService.ACTION_UPDATE);
                finished+=threadInfo.getFinished();
                //开始下载
                if (conn.getResponseCode()== HttpStatus.SC_PARTIAL_CONTENT){
                    //读取数据
                    inputStream=conn.getInputStream();
                    byte[] buffer=new  byte[1024*4];
                    int len=-1;
                    long time=System.currentTimeMillis();
                    while ((len=inputStream.read(buffer))!=-1){
                        //写入文件
                        randomAccessFile.write(buffer, 0, len);
                        //把下载进度发送广播给Activity
                        finished+=len;
                        if (System.currentTimeMillis()-time>500){
                            time=System.currentTimeMillis();
                            //由于要弄成百分比，要乘100但又有可能爆掉，因为int型数值还是偏小，20多mb乘100就爆了，就会变成负数等乱码，所以会出现进度条乱闪之现象，在此用强制类型转换修正
                            intent.putExtra("finished",(int)((long)finished*100/fileInfo.getLength()));
                            context.sendBroadcast(intent);
                        }
                        //若下载暂停则保存下载进度
                        if (isPause){
                            threadDAO.updateThread(threadInfo.getUrl(), threadInfo.getId(), finished);
                            return;
                        }
                    }
                    //删除线程信息
                    threadDAO.deleteThread(threadInfo.getUrl(),threadInfo.getId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                conn.disconnect();
                try {
                    randomAccessFile.close();
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
