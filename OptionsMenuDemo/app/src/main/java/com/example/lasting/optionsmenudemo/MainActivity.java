package com.example.lasting.optionsmenudemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(1,1,1,"菜单1");
        menu.add(1,2,1,"菜单2");
        menu.add(1,3,1,"菜单3");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 1:{
                Intent intent=new Intent(MainActivity.this,SecondActivity.class);
                item.setIntent(intent);
                break;
            }
            case 2:{
                Toast.makeText(this,"菜单2",Toast.LENGTH_SHORT).show();
                break;
            }
            case 3:{
                Toast.makeText(this,"菜单3",Toast.LENGTH_SHORT).show();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
