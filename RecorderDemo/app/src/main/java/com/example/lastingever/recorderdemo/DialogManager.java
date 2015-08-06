package com.example.lastingever.recorderdemo;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DialogManager {
    private Dialog dialog;

    private ImageView imageView1;
    private ImageView imageView2;

    private TextView textView1;

    private Context context;

    public DialogManager(Context context) {
        this.context = context;
    }

    public void showRecordingDialog(){
        dialog=new Dialog(context,R.style.AudioDialogTheme);
        //把xml文件封装成View对象
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.dialog_recorder,null);
        dialog.setContentView(view);

        //注意是dialog.findViewById
        imageView1= (ImageView) dialog.findViewById(R.id.imageView1);
        imageView2= (ImageView) dialog.findViewById(R.id.imageView2);
        textView1=(TextView)dialog.findViewById(R.id.textView1);

        dialog.show();
    }

    public void recording(){
        if (dialog!=null&&dialog.isShowing()){
            imageView1.setVisibility(View.VISIBLE);
            imageView2.setVisibility(View.VISIBLE);
            textView1.setVisibility(View.VISIBLE);

            imageView1.setImageResource(R.drawable.recorder);
            textView1.setText("手指上划，取消发送");
        }
    }

    public void wantToCancel(){
        if (dialog!=null&&dialog.isShowing()){
            imageView1.setVisibility(View.VISIBLE);
            imageView2.setVisibility(View.GONE);
            textView1.setVisibility(View.VISIBLE);

            imageView1.setImageResource(R.drawable.cancel);
            textView1.setText("松开手指，取消发送");
        }
    }

    public void tooShort(){
        if (dialog!=null&&dialog.isShowing()){
            imageView1.setVisibility(View.VISIBLE);
            imageView2.setVisibility(View.GONE);
            textView1.setVisibility(View.VISIBLE);

            imageView1.setImageResource(R.drawable.voice_too_short);
            textView1.setText("录音时间过短");
        }
    }

    public void dismissDialog(){
        if (dialog!=null&&dialog.isShowing()){
            dialog.dismiss();
            dialog=null;
        }
    }

    public void updateVoiceLevel(int level){
        if (dialog!=null&&dialog.isShowing()){
            //音量分为1到7不等，为了避免case7个太冗余，用indentifier来解决
            int resourceId=context.getResources().getIdentifier("v"+level,"drawable",context.getPackageName());
            imageView2.setImageResource(resourceId);
        }
    }
}
