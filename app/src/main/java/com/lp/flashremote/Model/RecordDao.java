package com.lp.flashremote.Model;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class RecordDao {
    private MyDbHelper myDbHelper;
    public RecordDao(Context context){
        myDbHelper=new MyDbHelper(context);
    }

    public long addData(String record){
        SQLiteDatabase sqLiteDatabase=myDbHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("record",record);

        long row=sqLiteDatabase.insert("SearchRecord",null,contentValues);
        sqLiteDatabase.close();
        return row;
    }

    /**
     * @param re
     * @return
     */
    public int deleteData(String re){
        SQLiteDatabase sqLiteDatabase=myDbHelper.getWritableDatabase();
        int deleteResult=sqLiteDatabase.delete("SearchRecord","record=?",new String[]{re});

        sqLiteDatabase.close();
        return deleteResult;
    }

    /**
     * 查询数据库
     * @return
     */
    public List<String> alterData(){
        List<String> data=new ArrayList<>();
        SQLiteDatabase sqLiteDatabase=myDbHelper.getWritableDatabase();
        Cursor cursor=sqLiteDatabase.query("searchRecord",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                data.add(cursor.getString(cursor.getColumnIndex("record")));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return data;
    }
}

