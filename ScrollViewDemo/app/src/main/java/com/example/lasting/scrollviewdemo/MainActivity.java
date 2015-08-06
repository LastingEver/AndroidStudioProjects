package com.example.lasting.scrollviewdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener{
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private TextView textView1;
    private ScrollView scrollView1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1=(Button)findViewById(R.id.button1);
        button2=(Button)findViewById(R.id.button2);
        button3=(Button)findViewById(R.id.button3);
        button4=(Button)findViewById(R.id.button4);
        textView1=(TextView)findViewById(R.id.textView1);
        scrollView1=(ScrollView)findViewById(R.id.scrollView1);
        textView1.setText(getResources().getString(R.string.badminton));
        scrollView1.setVerticalScrollBarEnabled(false);
        scrollView1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_UP:{
                        break;
                    }
                    case MotionEvent.ACTION_MOVE:{
                        if (scrollView1.getChildAt(0).getMeasuredHeight()<=scrollView1.getHeight()+scrollView1.getScrollY()){
                            Toast.makeText(MainActivity.this,"底部",Toast.LENGTH_SHORT).show();
                            textView1.append(getResources().getString(R.string.badminton));
                        }
                        break;
                    }
                    case MotionEvent.ACTION_DOWN:{
                        break;
                    }
                }
                return false;
            }
        });
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button1:{
                scrollView1.scrollBy(0, -60);
                break;
            }
            case R.id.button2:{
                scrollView1.scrollBy(0, 60);
                break;
            }
            case R.id.button3:{
                scrollView1.scrollTo(0,0);
                break;
            }
            case R.id.button4:{
                scrollView1.scrollTo(0,scrollView1.getMeasuredHeight());
            }
        }
    }
}
