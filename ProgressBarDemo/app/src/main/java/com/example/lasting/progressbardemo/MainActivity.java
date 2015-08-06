package com.example.lasting.progressbardemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {
    private TextView textView1;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private ProgressBar progressBar1;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView1 = (TextView) findViewById(R.id.textView1);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        button4=(Button)findViewById(R.id.button4);
        int firstProgress = progressBar1.getProgress();
        int secondProgress = progressBar1.getSecondaryProgress();
        int maxProgress = progressBar1.getMax();
        textView1.setText("第一进度百分比" + (int) (firstProgress / (float) maxProgress * 100) + "% 第二进度百分比" + (int) (secondProgress / (float) maxProgress * 100) + "%");
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1: {
                progressBar1.incrementProgressBy(10);
                progressBar1.incrementSecondaryProgressBy(10);
                break;
            }
            case R.id.button2: {
                progressBar1.incrementProgressBy(-10);
                progressBar1.incrementSecondaryProgressBy(-10);
                break;
            }
            case R.id.button3: {
                progressBar1.setProgress(50);
                progressBar1.setSecondaryProgress(80);
                break;
            }
            case R.id.button4:{
                progressDialog=new ProgressDialog(this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setTitle("hello world");
                progressDialog.setMessage("hello world");
                progressDialog.setIcon(R.mipmap.ic_launcher);
                progressDialog.setMax(100);
                progressDialog.incrementProgressBy(50);
                progressDialog.setIndeterminate(false);
                progressDialog.setButton(DialogInterface.BUTTON_POSITIVE,"确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this,"hello world",Toast.LENGTH_LONG).show();
                    }
                });
                progressDialog.setCancelable(true);
                progressDialog.show();
                break;
            }
        }
        textView1.setText("第一进度百分比" + (int) (progressBar1.getProgress() / (float) progressBar1.getMax() * 100) + "% 第二进度百分比" + (int) (progressBar1.getSecondaryProgress() / (float) progressBar1.getMax() * 100) + "%");
    }
}
