package com.example.lasting.submenudemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        SubMenu subMenu1 = menu.addSubMenu("文件");
//        SubMenu subMenu2 = menu.addSubMenu("编辑");
//        subMenu1.add(1, 1, 1, "新建");
//        subMenu1.add(1, 2, 1, "打开");
//        subMenu1.add(1, 3, 1, "保存");
//        subMenu1.setHeaderTitle("文件操作");
//        subMenu1.setHeaderIcon(R.mipmap.ic_launcher);
//        subMenu2.add(2, 1, 1, "复制");
//        subMenu2.add(2, 2, 1, "粘贴");
//        subMenu2.add(2, 3, 1, "剪切");
//        subMenu2.add(2, 4, 1, "重命名");
//        subMenu2.setHeaderTitle("编辑操作");
//        subMenu2.setHeaderIcon(R.mipmap.ic_launcher);
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.group1item1: {
                Toast.makeText(this, "新建", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.group1item2: {
                Toast.makeText(this, "打开", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.group1item3: {
                Toast.makeText(this, "保存", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.group2item1: {
                Toast.makeText(this, "复制", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.group2item2: {
                Toast.makeText(this, "粘贴", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.group2item3: {
                Toast.makeText(this, "剪切", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.group2item4: {
                Toast.makeText(this, "重命名", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
