package com.example.lasting.fragmentdemo;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MyFragment extends Fragment{
    private String code="谢谢!";
    MyListener myListener;
    public interface MyListener{
        public void thank(String code);
    }

    @Override
    public void onAttach(Activity activity) {
        myListener=(MyListener)activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.my_fragment2,container,false);
        TextView textView1=(TextView)view.findViewById(R.id.textView1);
        String text=getArguments().get("text").toString();
        textView1.setText(text);
        Toast.makeText(getActivity(),"接收到"+text,Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(),"发送感谢："+code,Toast.LENGTH_SHORT).show();
        myListener.thank(code);
        return view;
    }
}
