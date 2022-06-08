package com.example.drugstore_ad;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
/*数据添加修改页面*/
public class Manage extends AppCompatActivity implements
        View.OnClickListener{
    private Button btnInsert;
    private Button btnUpdate;
    private Button btnDelete;
    private EditText et_name;
    private EditText et_kind;
    private EditText et_num;
    private EditText et_price;
    private ImageView imageView;
    private TextView tv_mode;
    private String location;
    private String option;
    private int SelectID;
    private String name,kind,num,price;
    private DrugDBOpenHelper drugDBOpenHelper =new DrugDBOpenHelper(this);
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        initView();
        Log.i("DateBase","upData");
        Intent intent = getIntent();
        location = intent.getStringExtra("location");
        option = intent.getStringExtra("option");
        if(option.equals("update")){
            tv_mode.setText("修改   ");
            btnInsert.setVisibility(View.GONE);                           // 修改模式添加功能禁用
            SelectID = intent.getIntExtra("NO",-1);     // 添加模式获取ItemID
            query(SelectID);
        }
        if(option.equals("add")){
            tv_mode.setText("添加   ");
        }
    }
    private void initView() {
        btnInsert = findViewById(R.id.bt_insert);
        btnUpdate = findViewById(R.id.bt_update);
        btnDelete = findViewById(R.id.bt_delete);
        imageView = findViewById(R.id.iv_back);
        et_name = findViewById(R.id.et_Mname);
        et_kind = findViewById(R.id.et_Mkind);
        et_num = findViewById(R.id.et_Mnum);
        et_price = findViewById(R.id.et_Mprice);
        tv_mode = findViewById(R.id.tv_mode);
        btnInsert.setOnClickListener(this);     // 添加
        btnUpdate.setOnClickListener(this);     // 修改
        btnDelete.setOnClickListener(this);     // 删除
        imageView.setOnClickListener(this);     // 返回
    }

    @SuppressLint("SdCardPath")
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.bt_insert:
                //根据输入信息添加对应数据
                db = drugDBOpenHelper.getWritableDatabase();
                name = et_name.getText().toString();
                kind = et_kind.getText().toString();
                num = et_num.getText().toString();
                price = et_price.getText().toString();
                ContentValues values = new ContentValues();         //创建ContentValues对象
                values.put("name", name);                    //将数据添加到ContentValues对象
                values.put("kind",kind);
                values.put("num", num);
                values.put("price", price);
                db.insert("info", null, values);
                db.close();
                Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
                Log.i("数据库", "添加");
                break;
            case R.id.bt_delete:
                //根据所选ID删除对应数据,并重置所有输入框
                db.delete("info", "_id=?",
                        new String[]{String.valueOf(SelectID)}); // 根据ID删除数据库中对应数据
                db.close();
                et_name.setText("");            // 输入框重置
                et_kind.setText("");
                et_num.setText("");
                et_price.setText("");
                Toast.makeText(this, "信息已删除", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                Log.i("数据库", "删除");
                break;
            case R.id.bt_update:
                //根据所选ID更新对应数据的信息
                db = drugDBOpenHelper.getWritableDatabase();
                name = et_name.getText().toString();
                kind = et_kind.getText().toString();
                num = et_num.getText().toString();
                price = et_price.getText().toString();
                values = new ContentValues();                        //创建ContentValues对象
                values.put("name",name);
                values.put("kind",kind);
                values.put("num", num);
                values.put("price", price);
                db.update("info", values, "_id=?",
                        new String[]{String.valueOf(SelectID)}); // 根据ID更新数据库中对应数据
                db.close();
                Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
                Log.i("数据库", "更新");
                break;
            case R.id.iv_back:
                //返回主页面并更新ListView
                Log.i("back","--返回");
                Intent intent0 = new Intent(this,MainActivity.class);
                intent0.putExtra("status","back");
                intent0.putExtra("location",location);
                startActivity(intent0);
                break;
        }
    }
    /*根据Id查询*/
    public void query(int ID){
        List<DrugInfo> list = new ArrayList<>();
        db = drugDBOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from info where _id = ?",
                new String[]{String.valueOf(ID)});
        if (cursor.getCount() < 1) {
            Toast.makeText(Manage.this, "没有数据", Toast.LENGTH_SHORT).show();
        }
        else {
            while (cursor.moveToNext()) {
                DrugInfo drugInfo = new DrugInfo();
                int idIndex = cursor.getColumnIndex("_id");
                int nameIndex = cursor.getColumnIndex("name");
                int kindIndex = cursor.getColumnIndex("kind");
                int numIndex = cursor.getColumnIndex("num");
                int priceIndex = cursor.getColumnIndex("price");
                int _id = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);
                String kind = cursor.getString(kindIndex);
                String num = cursor.getString(numIndex);
                String price = cursor.getString(priceIndex);
                Log.e("YQM","查询;--"+cursor.getCount()+_id+"name-"+name+"  "+kind+"  "+num+"  "+price);
                et_name.setText(name);
                et_kind.setText(kind);
                et_num.setText(String.valueOf(num));
                et_price.setText(String.valueOf(price));
                drugInfo.setId(_id);
                drugInfo.setName(name);
                drugInfo.setKind(kind);
                drugInfo.setPrice(price);
                drugInfo.setNum(num);
                list.add(drugInfo);
                Log.i("manage-init","Done");
            }
        }
    }
}