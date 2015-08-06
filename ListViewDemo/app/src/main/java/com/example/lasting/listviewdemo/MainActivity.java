package com.example.lasting.listviewdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {
    private ListView listView1;
    private List<Map<String, Object>> dataList;
    private SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataList = new ArrayList<Map<String, Object>>();
        listView1 = (ListView) findViewById(R.id.listView1);
        simpleAdapter = new SimpleAdapter(this, getData(), R.layout.item_layout, new String[]{"pic", "text"}, new int[]{R.id.imageView1, R.id.textView1});
        listView1.setAdapter(simpleAdapter);
        listView1.setOnItemClickListener(this);
        listView1.setOnScrollListener(this);
    }

    private List<Map<String, Object>> getData() {
        for (int i = 0; i < 20; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("pic", R.mipmap.ic_launcher);
            map.put("text", "羽毛球" + i);
            dataList.add(map);
        }
        return dataList;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String text = listView1.getItemAtPosition(i).toString();
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        switch (i) {
            case SCROLL_STATE_TOUCH_SCROLL:
                Toast.makeText(this, "touch scroll", Toast.LENGTH_LONG).show();
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("pic", R.mipmap.ic_launcher);
                map.put("text", "增加项");
                dataList.add(map);
                simpleAdapter.notifyDataSetChanged();
                break;
            case SCROLL_STATE_FLING:
                Toast.makeText(this, "continue scroll", Toast.LENGTH_LONG).show();
                break;
            case SCROLL_STATE_IDLE:
                Toast.makeText(this, "stop scroll", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }
}
