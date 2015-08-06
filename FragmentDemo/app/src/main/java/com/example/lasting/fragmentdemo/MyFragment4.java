package com.example.lasting.fragmentdemo;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyFragment4 extends Fragment{
    private TextView textView1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.my_fragment2,container,false);
        textView1=(TextView)view.findViewById(R.id.textView1);
        textView1.setText("第二个Fragment");
        Log.i("tag","onCreateView()");
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i("tag","onAttach()");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("tag","onActivityCreate()");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("tag","onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("tag","onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("tag","onPause（）");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("tag","onStop（）");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("tag","onDestroyView（）");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("tag","onDestroy（）");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("tag","onDetach（）");
    }
}
