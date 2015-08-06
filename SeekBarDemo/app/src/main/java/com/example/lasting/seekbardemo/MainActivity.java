package com.example.lasting.seekbardemo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener{
    private SeekBar seekBar1;
    private TextView textView1;
    private TextView textView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekBar1=(SeekBar)findViewById(R.id.seekBar1);
        textView1=(TextView)findViewById(R.id.textView1);
        textView2=(TextView)findViewById(R.id.textView2);
        seekBar1.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        textView1.setText("正在拖动");
        textView2.setText("当前进度："+i);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        textView1.setText("开始拖动");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        textView1.setText("停止拖动");
    }
}
