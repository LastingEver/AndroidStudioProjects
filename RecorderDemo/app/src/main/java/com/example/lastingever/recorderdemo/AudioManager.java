package com.example.lastingever.recorderdemo;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class AudioManager {
    private MediaRecorder mediaRecorder;
    private String directory;
    private String path;

    private static AudioManager instance;

    private boolean isPrepared=false;

    private AudioManager(String dir){
        directory=dir;
    }

    public String getCurrentPath() {
        return path;
    }

    //回调准备完毕
    public interface AudioStateListener{
        void wellPrepared();
    }

    public AudioStateListener audioStateListener;

    public void setOnAudioStateListener(AudioStateListener listener){
        audioStateListener=listener;
    }

    public static AudioManager getInstance(String dir){
        if (instance==null) {
            synchronized (AudioManager.class) {
                if (instance == null) {
                    instance = new AudioManager(dir);
                }
            }
        }
        return instance;
    }

    public void prepareAudio(){
        try {
            isPrepared=false;

            File dir=new File(directory);
            if (!dir.exists()){
                dir.mkdirs();
            }

            String fileName=generateFileName();
            File file=new File(dir,fileName);

            path=file.getAbsolutePath();

            mediaRecorder=new MediaRecorder();
            //设置输出文件
            mediaRecorder.setOutputFile(file.getAbsolutePath());
            //设置MediaRecorder
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置音频格式
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            //设置音频编码
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();
            //准备结束
            isPrepared=true;
            if (audioStateListener!=null){
                audioStateListener.wellPrepared();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateFileName() {
        return UUID.randomUUID().toString()+".amr";
    }

    public int getVoiceLevel(int maxLevel){
        if (isPrepared){
                try {
                    return maxLevel*mediaRecorder.getMaxAmplitude()/32768+1;
                } catch (Exception e) {
            }
        }
        return 1;
    }

    public void release(){
        try{
            mediaRecorder.stop();
            mediaRecorder.release();
        }catch (Exception e){
        }
        mediaRecorder=null;
    }

    public void cancel(){
        release();
        if (path!=null){
            File file=new File(path);
            file.delete();
            path=null;
        }
    }
}
