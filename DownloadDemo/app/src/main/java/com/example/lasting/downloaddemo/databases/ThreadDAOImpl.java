package com.example.lasting.downloaddemo.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lasting.downloaddemo.entities.ThreadInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据访问接口的实现
 */
public class ThreadDAOImpl implements ThreadDAO {

    private DBHelper dbHelper = null;

    public ThreadDAOImpl(Context context) {
        dbHelper=new DBHelper(context);
    }

    @Override
    public void insertThread(ThreadInfo threadInfo) {
        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();
        sqLiteDatabase.execSQL("INSERT INTO thread_info (thread_id,url,start,end,finished) VALUES (?,?,?,?,?)",new Object[]{threadInfo.getId(),threadInfo.getUrl(),threadInfo.getStart(),threadInfo.getEnd(),threadInfo.getFinished()});//类比C语言中的printf函数，一个一个问号配对
        sqLiteDatabase.close();
    }

    @Override
    public void deleteThread(String url, int thread_id) {
        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM thread_info WHERE url=? and thread_id=?",new Object[]{url,thread_id});
        sqLiteDatabase.close();
    }

    @Override
    public void updateThread(String url, int thread_id, int finished) {
        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();
        sqLiteDatabase.execSQL("UPDATE thread_info SET finished=? WHERE url=? and thread_id=?",new Object[]{finished,url,thread_id});
        sqLiteDatabase.close();
    }

    @Override
    public List<ThreadInfo> getThreads(String url) {
        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();
        List<ThreadInfo>list=new ArrayList<ThreadInfo>();
        Cursor cursor =sqLiteDatabase.rawQuery("SELECT * FROM thread_info WHERE url=?", new String[]{url});
        while (cursor.moveToNext()){
            ThreadInfo threadInfo=new ThreadInfo();
            threadInfo.setId(cursor.getInt(cursor.getColumnIndex("thread_id")));
            threadInfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            threadInfo.setStart(cursor.getInt(cursor.getColumnIndex("start")));
            threadInfo.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
            threadInfo.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
            list.add(threadInfo);
        }
        cursor.close();
        sqLiteDatabase.close();
        return list;
    }

    @Override
    public boolean isExists(String url, int thread_id) {
        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();
        Cursor cursor =sqLiteDatabase.rawQuery("SELECT * FROM thread_info WHERE url=? and thread_id=?", new String[]{url, thread_id+""});
        boolean exist=cursor.moveToNext();
        cursor.close();
        sqLiteDatabase.close();
        return exist;
    }
}