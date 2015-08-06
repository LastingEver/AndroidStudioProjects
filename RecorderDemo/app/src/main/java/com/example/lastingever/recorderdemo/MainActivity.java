package com.example.lastingever.recorderdemo;
/**
 * AudioRecorderButton//录音按钮
 *  State:(状态)STATE_NORMAL(正常,即未录音状态),STATE_RECORDING(即录音状态),STATE_WANT_TO_CANCEL(准备退出录音状态)
 * DialogManager//录音时的弹出框
 *  Style(风格):RECORDING(正在录音),WANT_TO_CANCEL(准备放弃录音),TOO_SHORT(录音时长过短，录音无效)
 * AudioManager//主要操作
 *  prepare();->end prepare->showDialog//录音的准备工作，完了以后调弹出框
 *  cancel();//用于取消录音的方法
 *  release();->callbackToActivity//释放命令，回馈给activity
 *  getVoiceLevel();//获取音量
 *
 *  class AudioRecorderButton{
 *      onTouchEvent{//点击事件
 *          case DOWN(按下按钮):
 *              changeButtonState(STATE_RECORDING);//转换录音模式至录音状态
 *              longClick->AudioManager.prepare()->end prepare->DialogManager.showDialog(RECORDING)//结束准备并调用DialogManager的showDialog方法显示正在录音的弹出框
 *          case MOVE(移动手指):
 *              if(wantCancel(x,y)){//x,y为手指坐标
 *                  DialogManager.showDialog(WANT_TO_CANCEL);
 *                  changeButtonState(STATE_WANT_TO_CANCEL);
 *              }else{
 *                  DialogManager.showDialog(RECORDING);
 *                  changeButtonState(STATE_WANT_TO_CANCEL);
 *              }
 *          case UP(放开手指):
 *              if(STATE_WANT_TO_CANCEL==currentState){
 *                  AudioManager.cancel();
 *              }
 *              if(STATE_RECORDING==currentState){
 *                  AudioManager.release();
 *                  callbackToActivity(url,time);//url是录音文件的完整路径，time为录音时长
 *              }
 *      }
 *  }
 *
 * 先做好布局，准备好各函数并留空
 * 状态判断和按钮显示效果，弹出框的实现，录音的实现,录音文件保存并显示在列表中
 *
 */

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private ListView listView1;
    private ArrayAdapter<Recorder> arrayAdapter;
    private List<Recorder> list=new ArrayList<Recorder>();

    private AudioRecorderButton audioRecorderButton1;

    private View view1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView1= (ListView) findViewById(R.id.listView1);
        audioRecorderButton1= (AudioRecorderButton) findViewById(R.id.button1);
        audioRecorderButton1.setOnAudioFinishedListener(new AudioRecorderButton.AudioFinishedListener() {
            @Override
            public void onFinished(float secends, String path) {
                Recorder recorder=new Recorder(secends,path);
                list.add(recorder);
                arrayAdapter.notifyDataSetChanged();
                listView1.setSelection(list.size()-1);
            }
        });

        arrayAdapter=new RecorderAdapter(this,list);
        listView1.setAdapter(arrayAdapter);

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //前一个文件播放时，点第二个文件时停止第一个的播放
                if (view1!=null){
                    view1.setBackgroundResource(R.drawable.adj);
                    view1=null;
                }
                //播放动画
                view1=view.findViewById(R.id.view1);
                view1.setBackgroundResource(R.drawable.play_recorder);
                AnimationDrawable animationDrawable= (AnimationDrawable) view1.getBackground();
                animationDrawable.start();
                //播放音频
                MediaManager.playSound(list.get(position).path, new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        view1.setBackgroundResource(R.drawable.adj);
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaManager.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
    }

    class Recorder{
        float time;
        String path;

        public Recorder(float time, String path) {
            this.time = time;
            this.path = path;
        }

        public float getTime() {
            return time;
        }

        public void setTime(float time) {
            this.time = time;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}