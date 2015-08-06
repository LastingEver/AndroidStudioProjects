package com.example.lasting.fragmentdemo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyFragment2 extends Fragment{
    private TextView textView1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.my_fragment2,container,false);
        textView1=(TextView)view.findViewById(R.id.textView1);
        textView1.setText("动态加载");
        return view;
    }
}
