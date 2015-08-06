package com.example.lastingever.recorderdemo;

import android.media.AudioManager;
import android.media.MediaPlayer;

public class MediaManager {

    private static MediaPlayer mediaPlayer;

    private static boolean isPause;

    public static void playSound(String path, MediaPlayer.OnCompletionListener onCompletionListener) {
        if (mediaPlayer==null){
            mediaPlayer=new MediaPlayer();
        }else {
            mediaPlayer.reset();
        }

        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(onCompletionListener);
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void pause(){
        if (mediaPlayer!=null&&mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            isPause=true;
        }
    }

    public static void resume(){
        if (mediaPlayer!=null&&isPause){
            mediaPlayer.start();
            isPause=false;
        }
    }

    public static void release(){
        if (mediaPlayer!=null){
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }
}
