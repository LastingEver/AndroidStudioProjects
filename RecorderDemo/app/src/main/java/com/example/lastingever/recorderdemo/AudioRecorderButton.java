package com.example.lastingever.recorderdemo;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class AudioRecorderButton extends Button implements AudioManager.AudioStateListener{

    private static final int STATE_NORMAL=1;
    private static final int STATE_RECORDING=2;
    private static final int STATE_WANT_TO_CANCEL=3;

    private static final int DISTANCE_Y_CANCEL=50;

    private static int currentState=STATE_NORMAL;

    private boolean isRecording=false;

    private DialogManager dialogManager;

    private AudioManager audioManager;

    private float time;
    //是否触发long click
    private boolean ready=false;
    public AudioRecorderButton(Context context) {
        this(context, null);
    }

    public AudioRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        dialogManager=new DialogManager(getContext());

        String dir= Environment.getExternalStorageDirectory()+"/recorder";
        audioManager=AudioManager.getInstance(dir);
        audioManager.setOnAudioStateListener(this);

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ready=true;
                audioManager.prepareAudio();
                return false;
            }
        });
    }

    public interface AudioFinishedListener{
        void onFinished(float secends,String path);
    }

    private AudioFinishedListener audioFinishedListener;

    public void setOnAudioFinishedListener(AudioFinishedListener audioFinishedListener){
        this.audioFinishedListener=audioFinishedListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action=event.getAction();
        int x= (int) event.getX();
        int y= (int) event.getY();

        switch (action){
            case MotionEvent.ACTION_DOWN:{
                changeState(STATE_RECORDING);
                break;
            }

            case MotionEvent.ACTION_MOVE:{
                if (isRecording){
                    if (wantToCancel(x,y)){
                        changeState(STATE_WANT_TO_CANCEL);
                    }else {
                        changeState(STATE_RECORDING);
                    }
                    break;
                }
            }

            case MotionEvent.ACTION_UP:{
                if (!ready){
                    reset();
                    return super.onTouchEvent(event);
                }
                if (!isRecording||time<0.6f){
                    dialogManager.tooShort();
                    audioManager.cancel();
                    handler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS,1300);
                } else if (currentState==STATE_RECORDING){
                    dialogManager.dismissDialog();
                    audioManager.release();
                    if (audioFinishedListener!=null){
                        audioFinishedListener.onFinished(time,audioManager.getCurrentPath());
                    }
                }else if (currentState==STATE_WANT_TO_CANCEL){
                    dialogManager.dismissDialog();
                    audioManager.cancel();
                }
                reset();
                break;
            }
        }
        return super.onTouchEvent(event);
    }

    private void reset() {
        if (isRecording){
            isRecording=false;
            time=0;
            changeState(STATE_NORMAL);
        }
    }

    private boolean wantToCancel(int x, int y) {
        if (x<0||x>getWidth()){
            return true;
        }
        if(y<-DISTANCE_Y_CANCEL||y>getHeight()+DISTANCE_Y_CANCEL){
            return true;
        }
        return false;
    }

    private void changeState(int state) {
        if (currentState!=state){
            currentState=state;
            switch (state){
                case STATE_NORMAL:{
                    setBackgroundResource(R.drawable.button_recorder_normal);
                    setText(R.string.recorder_normal);
                    break;
                }

                case STATE_RECORDING:{
                    setBackgroundResource(R.drawable.button_recorder_recording);
                    setText(R.string.recorder_recording);
                    if (isRecording){
                        dialogManager.recording();
                    }
                    break;
                }

                case STATE_WANT_TO_CANCEL:{
                    setBackgroundResource(R.drawable.button_recorder_recording);
                    setText(R.string.recorder_want_to_cancel);
                    dialogManager.wantToCancel();
                    break;
                }
            }
        }
    }

    //获取音量大小的runnable
    private Runnable getVoiceLevelRunnable=new Runnable() {
        @Override
        public void run() {
            while (isRecording){
                try {
                    Thread.sleep(100);
                    time+=0.1f;
                    handler.sendEmptyMessage(MSG_VOICE_CHANGE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private static final int MSG_AUDIO_PREPARED=1;
    private static final int MSG_VOICE_CHANGE=2;
    private static final int MSG_DIALOG_DISMISS=3;

    private static final int MAX_VOICE_LEVEL=7;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_AUDIO_PREPARED:{
                    //显示在end prepare以后
                    dialogManager.showRecordingDialog();
                    isRecording = true;
                    new Thread(getVoiceLevelRunnable).start();
                    break;
                }

                case MSG_VOICE_CHANGE:{
                    dialogManager.updateVoiceLevel(audioManager.getVoiceLevel(MAX_VOICE_LEVEL));
                    break;
                }

                case MSG_DIALOG_DISMISS:{
                    dialogManager.dismissDialog();
                    break;
                }
            }
        }
    };

    @Override
    public void wellPrepared() {
        handler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }
}
