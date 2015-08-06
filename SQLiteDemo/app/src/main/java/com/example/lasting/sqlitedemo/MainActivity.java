package com.example.lasting.sqlitedemo;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SQLiteDatabase sqLiteDatabase = openOrCreateDatabase("user.db", MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("CREATE table IF NOT EXISTS user (_id integer primary key AUTOINCREMENT,name text NOT NULL,age integer NOT NULL,sex text NOT NULL)");
        sqLiteDatabase.execSQL("INSERT INTO user(name,age,sex)VALUES('张三',18,'男')");
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM user WHERE name='张三'", null);
        if (cursor!=null){
            while (cursor.moveToNext()){
                Log.i("info",""+cursor.getString(cursor.getColumnIndex("name")));
            }
            cursor.close();
        }
        sqLiteDatabase.close();
    }
}
