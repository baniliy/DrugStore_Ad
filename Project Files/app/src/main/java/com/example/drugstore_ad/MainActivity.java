package com.example.drugstore_ad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tv_temp;                       // 天气温度
    private TextView tv_cityWeather;                // 城市名及天气情况
    private TextView tv_air;                        // 空气质量
    private ImageView iv_add;                       // 添加和取消按钮
    private ImageView iv_search;                    // 文本输入搜索
    private ImageView iv_SS;                        // 搜索图标，注：暂无实际功能只为临时获得焦点
    private ImageView iv_weather;                   // 天气图标
    private ImageView iv_setting;                   // 天气设置按钮
    private EditText et_search;                     // 开始检索按钮
    private ListView listView;                      // 检索内容列表
    private LinearLayout layout_title;              // titleBar搜索时隐藏
    private String location;                        // 暂存登录时跳转传回的城市位置信息
    private Boolean SearchStatus = false;           // 记录此时是否为搜索状态，以便展示不同的布局变化
    private List<Integer> ID_List = new ArrayList<>();  // 记录从数据库中检索到的数据项编号，配合列表项控件点击响应
    private DrugDBOpenHelper drugDBOpenHelper =new DrugDBOpenHelper(this);      // 获取自定义创建的数据库对象
    private SQLiteDatabase db;                          // 数据库对象
    private Map<String, String> map;                    // 以键值对的形式存放天气信息
    private List<Map<String, String>> list;             // 存放城市天气信息(键值对)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*初始化配置控件*/
        init();
        /*取消系统自动获取焦点*/
        //et_search.clearFocus();
        /*从登录信息获取位置信息，并赋值到对应控件*/
        Intent it = getIntent();
        String Status = it.getStringExtra("status");
        if(Status.equals("back")){
            Log.e("Status","由manage返回");
//            search("");
        }
        if(Status.equals("login")){
            createDB();                  //创建数据库,并添加测试数据
            Log.e("Status","由Login返回");
        }
        location = it.getStringExtra("location");
        Log.e("city",location);
//        tv_location.setText("正阳");
//        tv_location.setText(location);
        /*天气控件配置*/
        try {
            weather_info();
        } catch (Exception e) {
            e.printStackTrace();
        }
        weather_set(location);
        /*控件监听设置*/
        iv_add.setOnClickListener(this);    //设置添加按钮的响应
        iv_search.setOnClickListener(this); //设置搜索按钮的响应
        iv_setting.setOnClickListener(this);//展示城市天气切换
        /*设置ListVew监听，以响应点击对应的Item事件*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this,"你单击的是第"+(position+1)+"条数据 "+
                        "ID:"+ID_List.get(position),Toast.LENGTH_SHORT).show();
                Intent intent0 = new Intent(MainActivity.this,Manage.class);
                intent0.putExtra("location",location);
                intent0.putExtra("NO",ID_List.get(position));     // 传递点击的ItemID
                intent0.putExtra("option","update");        // 由此跳转启动manage的修改模式
                startActivity(intent0);
            }
        });
        /*对文本输入控件设置焦点监听，当用户点击布时局变化相应控件隐藏显示*/
        et_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {                             // 此处为得到焦点时的处理内容
                    layout_title.setVisibility(View.GONE);  // 当用户需要输入关键字以搜索数据时，隐藏页面上方的TitleBar
                    iv_add.setImageResource(R.drawable.cancel);
                    et_search.setHint("请输入关键词");
                    SearchStatus = true;
                    Log.e("搜索栏状态","et_search已获得焦点");
                } else {                                    // 此处为失去焦点时的处理内容
                    Log.e("搜索栏状态","et_search已失去焦点");
                    et_search.setHint("点击开始搜索");
                //      search(et_search.getText().toString().trim());
                }
            }
        });
        iv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] cit = {"北京","上海","广州","杭州"};
                for (int i = 0; i < cit.length; i++) {
                    if(cit[i].equals(location)){
                        location = cit[(i+1)%4];
                        weather_set(location);
                        break;
                    }
                }
            }
        });
    }
    /*初始化控件对象*/
    private void init(){
        iv_SS = findViewById(R.id.iv_SS);
        iv_add = findViewById(R.id.iv_add);
        iv_search = findViewById(R.id.iv_search);
        iv_weather = findViewById(R.id.iv_weather);
        iv_setting = findViewById(R.id.iv_setting);
        tv_air = findViewById(R.id.tv_air);
        tv_temp = findViewById(R.id.tv_Temp);
        tv_cityWeather = findViewById(R.id.tv_cityWeather);
        listView = findViewById(R.id.lv);
        et_search = findViewById(R.id.et_search);
        layout_title = findViewById(R.id.layout_title);
    }
    /*天气展示*/
    private void weather_set(String location){
        String temp, weather, name, pm, wind;
        String[] city = new String[]{"北京","上海","广州","杭州"};
        int i;
        for (i = 0; i < city.length; i++) {
            if(city[i].equals(location)){
                break;
            }
        }
        Map<String, String> cityMap = list.get(i);
        temp = cityMap.get("temp");
        weather = cityMap.get("weather");
        name = cityMap.get("name");
        pm = cityMap.get("pm");
        wind = cityMap.get("wind");
        tv_cityWeather.setText(name+"  "+weather);
        tv_temp.setText(temp);
        tv_air.setText(pm+"  "+wind+"风");
        /*匹配对应的天气图标*/
        switch (weather){
            case "晴天":
                iv_weather.setImageResource(R.drawable.sun);
                break;
            case "大雨":
                iv_weather.setImageResource(R.drawable.rain);
                break;
            case "多云":
                iv_weather.setImageResource(R.drawable.cloud);
                break;
        }
    }
    /*调用天气服务对象获取天气数据*/
    private void weather_info() throws Exception {
        //读取weather_raw.xml文件
        InputStream is = this.getResources().openRawResource(R.raw.weather_raw);
        //把每个城市的天气信息集合存到weatherInfos中
        List<WeatherInfo> weatherInfos = WeatherService.getInfosFromXML(is);
        //循环读取weatherInfos中的每一条数据
        list = new ArrayList<Map<String, String>>();
        for (WeatherInfo info : weatherInfos) {
            map = new HashMap<String, String>();
            map.put("temp", info.getTemp());
            map.put("weather", info.getWeather());
            map.put("name", info.getName());
            map.put("pm", info.getPm());
            map.put("wind", info.getWind());
            list.add(map);
        }
    }
    /*创建数据表info并添加测试数据*/
    private void createDB() {
        db = drugDBOpenHelper.getWritableDatabase();
        /*以下为测试数据*/
        String[] namel ={"Amoxicillin","Ganmaoling",
                "Liuwei Dihuang","Cefaclor"};
        String[] kindl ={"Rx","OTC","OTC","Rx"};
        String[] numl ={"20","32","13","25"};
        String[] pricel ={"21.50","32.40","16.80","17.60"};
        /*创建数据库并向info表中添加3条数据*/
        for(int i =0;i<4;i++){
            ContentValues values = new ContentValues();        //创建ContentValues对象
            values.put("name", namel[i]);             //将数据添加到ContentValues对象
            values.put("kind", kindl[i]);
            values.put("price", pricel[i]);
            values.put("num", numl[i]);
            db.insert("info", null, values);
        }
        db.close();
        Toast.makeText(this, "测试数据已添加", Toast.LENGTH_SHORT).show();
    }
   /*强制隐藏系统软键盘*/
    public static void hideInputManager(Context context, View view){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view !=null && imm != null){
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_add:           // 由主页面进入查询修改界面
                if(!SearchStatus){
                    Intent intent = new Intent(this,Manage.class);
                    intent.putExtra("location",location);
                    intent.putExtra("option","add");                 // 由此跳转启动manage的添加修改模式
                    startActivity(intent);
                }
                else {
                    listclaer();  // 回到主页面，titlebar可见，listview清除
                    layout_title.setVisibility(View.VISIBLE);
                    iv_add.setImageResource(R.drawable.addd);
                    SearchStatus=false;
                    hideInputManager(this,getCurrentFocus());
                    iv_SS.requestFocus();
                }
                break;
            case R.id.iv_search:        // 获取关键词检索数据或无关键词显示所有数据
                Log.e("YQM","搜索按钮已响应");
                search(et_search.getText().toString().trim());
                break;
        }
    }
    /*查询数据*/
    public void search(String keywords){
        ID_List.clear();
        List<DrugInfo> list = new ArrayList<>();
        db = drugDBOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from info where name like ? or kind like ?",
                new String[]{"%"+keywords+"%","%"+keywords+"%"});
        if (cursor.getCount() < 1) {
            Toast.makeText(MainActivity.this, "没有数据", Toast.LENGTH_SHORT).show();
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
                Log.e("YQM","cursor.getCount();--"+cursor.getCount()+_id+"name-"+name+"  "+kind+"  "+num+"  "+price);
                drugInfo.setId(_id);
                drugInfo.setName(name);
                drugInfo.setKind(kind);
                drugInfo.setPrice(price);
                drugInfo.setNum(num);
                list.add(drugInfo);
                ID_List.add(_id);
            }
            DrugAdapter adapter = new DrugAdapter(MainActivity.this,list);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        cursor.close();
        db.close();
        Log.i("ID_List",ID_List.toString());
    }
    private void listclaer(){
        List<DrugInfo> list = new ArrayList<>();
        DrugAdapter adapter = new DrugAdapter(MainActivity.this,list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
