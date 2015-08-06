package com.example.lasting.fragmentdemo;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity3 extends Activity {
    private boolean flag=true;
    private Button button1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        init();
        button1=(Button)findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager=getFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                if (flag){
                    MyFragment4 myFragment4=new MyFragment4();
                    fragmentTransaction.replace(R.id.linearLayout,myFragment4);
                    flag=false;
                } else{
                    MyFragment3 myFragment3=new MyFragment3();
                    fragmentTransaction.replace(R.id.linearLayout,myFragment3);
                    flag=true;
                }
                fragmentTransaction.commit();
            }
        });

    }

    private void init() {
        FragmentManager fragmentManager=getFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        MyFragment3 myFragment3=new MyFragment3();
        fragmentTransaction.add(R.id.linearLayout,myFragment3);
        fragmentTransaction.commit();
    }
}
