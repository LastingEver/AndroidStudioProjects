package com.example.lasting.sharedpreferencesdemo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
    private EditText editText1;
    private EditText editText2;
    private CheckBox checkBox1;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
        sharedPreferences = getSharedPreferences("mySharePreferences", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String name = sharedPreferences.getString("name", "");
        if (name == null) {
            checkBox1.setChecked(false);
        } else {
            checkBox1.setChecked(true);
            editText1.setText(name);
        }
    }

    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.button1: {
                String name = editText1.getText().toString().trim();
                String password = editText2.getText().toString().trim();
                if ("admin".equals(name) && "admin".equals(password)) {
                    if (checkBox1.isChecked()) {
                        editor.putString("name", name);
                        editor.commit();
                    } else {
                        editor.remove("name");
                        editor.commit();
                    }
                    Toast.makeText(this, "登陆成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "登陆失败", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.button2: {
                finish();
                break;
            }
        }
    }
}
