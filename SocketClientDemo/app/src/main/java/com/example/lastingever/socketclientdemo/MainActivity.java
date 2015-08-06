package com.example.lastingever.socketclientdemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    EditText editText1;
    EditText editText2;
    TextView textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText1= (EditText) findViewById(R.id.editText1);
        editText2= (EditText) findViewById(R.id.editText2);
        textView1= (TextView) findViewById(R.id.textView1);

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });
    }

    //----------------

    Socket socket=null;
    BufferedReader bufferedReader=null;
    BufferedWriter bufferedWriter=null;

    public void connect(){
        AsyncTask<Void,String,Void> read=new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    socket=new Socket(editText1.getText().toString(),54321);
                    bufferedWriter=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    publishProgress("success");
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this,"连接失败",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                try {
                    String line=null;
                    while ((line=bufferedReader.readLine())!=null){
                        publishProgress(line);
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this,"连接失败",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                if (values[0].equals("success")){
                    Toast.makeText(MainActivity.this,"连接成功",Toast.LENGTH_SHORT).show();
                }
                textView1.append("别人说："+values[0]+"\n");
                textView1.append(values[0]);
                super.onProgressUpdate(values);
            }
        };

        read.execute();
    }

    public void send(){
        try {
            textView1.append("我说:"+editText2.getText().toString()+"\n");
            bufferedWriter.write(editText2.getText().toString()+"\n");
            bufferedWriter.flush();
            editText2.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
