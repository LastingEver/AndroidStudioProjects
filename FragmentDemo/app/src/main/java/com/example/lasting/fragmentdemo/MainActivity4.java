package com.example.lasting.fragmentdemo;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity4 extends Activity implements MyFragment.MyListener{
    private Button button1;
    private EditText editText1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        button1=(Button)findViewById(R.id.button1);
        editText1=(EditText)findViewById(R.id.editText1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=editText1.getText().toString();
                MyFragment myFragment=new MyFragment();
                Bundle bundle=new Bundle();
                bundle.putString("text",text);
                myFragment.setArguments(bundle);
                FragmentManager fragmentManager=getFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.linearLayout,myFragment);
                fragmentTransaction.commit();
                Toast.makeText(MainActivity4.this,"发送信息"+text,Toast.LENGTH_SHORT).show();
            }
        });
        FragmentManager fragmentManager=getFragmentManager();
        Fragment findFragmentById=fragmentManager.findFragmentById(R.id.fragment1);
        MyFragment1 myFragment1=(MyFragment1)findFragmentById;
        myFragment1.setString("静态传值");
    }

    @Override
    public void thank(String code) {
        Toast.makeText(this,"已接收到:"+code+"不客气",Toast.LENGTH_SHORT).show();
    }
}
