package com.example.lasting.contextmenudemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showListView();
    }

    private void showListView() {
        ListView listView1=(ListView)findViewById(R.id.listView1);
        ArrayAdapter<String>arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,getData());
        listView1.setAdapter(arrayAdapter);
        this.registerForContextMenu(listView1);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("文件操作");
        menu.setHeaderIcon(R.mipmap.ic_launcher);
//        menu.add(1,1,1,"复制");
//        menu.add(1,2,1,"粘贴");
//        menu.add(1,3,1,"剪切");
//        menu.add(1,4,1,"重命名");
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu_main,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item1:{
                Toast.makeText(this,"复制",Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.item2:{
                Toast.makeText(this,"粘贴",Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.item3:{
                Toast.makeText(this,"剪切",Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.item4:{
                Toast.makeText(this,"重命名",Toast.LENGTH_SHORT).show();
                break;
            }
        }
        return super.onContextItemSelected(item);
    }

    private ArrayList<String>getData(){
        ArrayList<String>arrayList=new ArrayList<String>();
        for (int i=0;i<5;i++){
            arrayList.add("文件"+(i+1));
        }
        return arrayList;
    }
}
