package com.example.lasting.fragmentdemo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MyFragment1 extends Fragment{
    private Button button1;
    private TextView textView1;
    private String string;

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.my_fragment1,container,false);
        textView1=(TextView)view.findViewById(R.id.textView1);
        button1=(Button)view.findViewById(R.id.button1);
        button1.setText("获取传值");
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),getString().toString(),Toast.LENGTH_SHORT).show();
            }
        });
        textView1.setText("静态加载");
        return view;
    }
}
