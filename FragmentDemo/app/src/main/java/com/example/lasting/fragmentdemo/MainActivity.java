package com.example.lasting.fragmentdemo;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Switch;

public class MainActivity extends Activity implements RadioGroup.OnCheckedChangeListener{
    private RadioGroup radioGroup1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        radioGroup1=(RadioGroup)findViewById(R.id.radioGroup1);
        radioGroup1.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch(i){
            case R.id.radioButton1:{
                Intent intent=new Intent(this,MainActivity1.class);
                startActivity(intent);
                break;
            }
            case R.id.radioButton2:{
                MyFragment2 myFragment2=new MyFragment2();
                FragmentManager fragmentManager=getFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.linearLayout,myFragment2);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            }
            case R.id.radioButton3:{
                Intent intent=new Intent(this,MainActivity3.class);
                startActivity(intent);
                break;
            }
            case R.id.radioButton4:{
                Intent intent=new Intent(this,MainActivity4.class);
                startActivity(intent);
                break;
            }
        }
    }
}
