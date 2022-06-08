package com.example.drugstore_ad;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DrugDBOpenHelper extends SQLiteOpenHelper {
    //构造方法，调用该方法创建一个drug.db数据库
    public DrugDBOpenHelper(Context context) {
        super(context, "drug.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建该数据库的同时新建一个info表，表中有_id,name,kind,num,price这五个字段
        db.execSQL("create table info (" +
                "_id integer primary key autoincrement, " +
                "name varchar(20)," +
                "kind varchar(20)," +
                "num integer," +
                "price integer)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
