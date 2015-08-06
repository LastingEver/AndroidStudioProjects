package com.example.lasting.gridviewdemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.Inflater;

public class MainActivity extends Activity {
    private GridView gridView1;
    private List<Map<String, Object>> dataList;
    private SimpleAdapter simpleAdapter;
    private int[] icon = {R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher};
    private String[] iconName = {"图标01", "图标02", "图标03", "图标04", "图标05", "图标06", "图标07", "图标08", "图标09", "图标10", "图标11", "图标12"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView1 = (GridView) findViewById(R.id.gridView1);
        dataList=new ArrayList<Map<String, Object>>();
        simpleAdapter = new SimpleAdapter(this, getData(), R.layout.item_layout, new String[]{"pic", "text"}, new int[]{R.id.imageView1, R.id.textView1});
        gridView1.setAdapter(simpleAdapter);
        gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LayoutInflater layoutInflater=LayoutInflater.from(MainActivity.this);
                View toastView=layoutInflater.inflate(R.layout.item_layout,null);
                Toast toast=new Toast(MainActivity.this);
                toast.setView(toastView);
                toast.show();
//                Toast toast=Toast.makeText(MainActivity.this,"我是"+iconName[i],Toast.LENGTH_LONG);
//                LinearLayout toastLayout=(LinearLayout)toast.getView();
//                ImageView imageView=new ImageView(MainActivity.this);
//                imageView.setImageResource(R.mipmap.ic_launcher);
//                toastLayout.addView(imageView);
//                toast.show();
            }
        });
    }

    private List<Map<String, Object>> getData() {
        for (int i=0;i<icon.length;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("pic", icon[i]);
            map.put("text", iconName[i]);
            dataList.add(map);
        }
        return dataList;
    }
}
