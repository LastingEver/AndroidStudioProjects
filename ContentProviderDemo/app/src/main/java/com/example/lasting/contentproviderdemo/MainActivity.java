package com.example.lasting.contentproviderdemo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ContentResolver contentResolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        Uri uri = contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, contentValues);
        Long rawContentId = ContentUris.parseId(uri);
        contentValues.clear();
        contentValues.put(ContactsContract.CommonDataKinds.StructuredName.RAW_CONTACT_ID, rawContentId);
        contentValues.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, "张三");
        contentValues.put(ContactsContract.CommonDataKinds.StructuredName.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        uri = contentResolver.insert(ContactsContract.Data.CONTENT_URI, contentValues);
        contentValues.clear();
        contentValues.put(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID, rawContentId);
        contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, "12345678901");
        contentValues.put(ContactsContract.CommonDataKinds.Phone.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        uri = contentResolver.insert(ContactsContract.Data.CONTENT_URI, contentValues);
        Cursor cursor=contentResolver.query(ContactsContract.Contacts.CONTENT_URI,new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME},null,null,null);
        if (cursor!=null){
            while (cursor.moveToNext()){
                int id=cursor.getColumnIndex("_ID");
                String name=cursor.getString(cursor.getColumnIndex("DISPLAY_NAME"));
                Log.i("info",id+"");
                Log.i("info",name);
                Cursor cursor1=contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER}, ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+id,null,null,null);
                if (cursor1!=null){
                    while (cursor1.moveToNext()){
                        int type=cursor1.getInt(cursor1.getColumnIndex("Phone.TYPE"));
                        if (type== ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE){
                            Log.i("info",cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        }
                    }
                    cursor1.close();
                }
            }
            cursor.close();
        }
    }
}
