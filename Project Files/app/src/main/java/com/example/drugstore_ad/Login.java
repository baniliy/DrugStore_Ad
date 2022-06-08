package com.example.drugstore_ad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Login extends AppCompatActivity {
    private EditText et_account;        // 用户名输入框
    private EditText et_password;       // 密码输入框
    private EditText et_cpassword;      // 确认密码输入框
    private TextView tv_login_info;     // 提示信息（初次登录注册和登录提示）
    private Spinner spinner;            // 选择城市下拉选择框
    private Button bt_log;              // 登录按钮
    boolean fstatus;                    // 记录登录状态
    String name;                        // 保存获取到的用户名
    String password;                    // 保存获取到的登录密码
    String location;                    // 保存获取到的位置信息
    String temp1 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bt_log = findViewById(R.id.bt_log);
        spinner = findViewById(R.id.spinner);
        et_account = findViewById(R.id.et_account);
        et_password = findViewById(R.id.et_password);
        et_cpassword = findViewById(R.id.et_cpasswod);
        tv_login_info = findViewById(R.id.tv_login_info);
        /*👻判断存储文件是否存在(注册和登录判断)*/
        String file_path = getFilesDir().getAbsolutePath();
        file_path = file_path.replace("files","Shared_prefs");
        File file = new File(file_path+"/"+"ENROLL");
        fstatus = file.exists();
        if(!fstatus){           // 不存在用户文件
            bt_log.setText("注册");
            tv_login_info.setText("⚠️初次运行，请设置用户名、密码.");
        }
        /*👻提交按钮响应*/
        bt_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*忽略验证测试*/
//                Intent intent = new Intent();
//                intent.setClass(Login.this,MainActivity.class);
//                intent.putExtra("status","login");
//                intent.putExtra("location","beijing");
//                startActivity(intent);
                /*👻注册，创建账户*/
                if(!fstatus){
                    name = et_account.getText().toString().trim();
                    password = et_password.getText().toString().trim();
                    location = spinner.getSelectedItem().toString().trim();
                    /*👻判断用户名和密码的有效性*/
                    if(verify_count(name,password,et_cpassword.getText().toString().trim(),location)){
                        /*👻获取内部存储文件对应的输入流*/
                        try {
                            FileOutputStream fos = openFileOutput("ENROLL",MODE_PRIVATE);
                            String temp = name+","+password+","+location;
                            fos.write(temp.getBytes());
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast toast=Toast.makeText(getApplicationContext(), "👻注册成功！", Toast.LENGTH_SHORT);
                        toast.show();
                        /*👻创建完成重置输入框并修改按钮状态*/
                        et_account.setText("");
                        et_password.setText("");
                        bt_log.setText("登录");
                        tv_login_info.setText("🔐");
                        findViewById(R.id.layout_location).setVisibility(View.INVISIBLE);
                        findViewById(R.id.layout_pw).setVisibility(View.INVISIBLE);
                        fstatus = true;
                    }
                    else {
                        Toast toast=Toast.makeText(getApplicationContext(), "❌ 注册失败，请检查用户名或密码的有效性！", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                /*👻登录，判断用户信息*/
                else {
                    name = et_account.getText().toString();
                    password = et_password.getText().toString();
                    /*👻获取内部存储文件对应的输出流*/
                    try {
                        FileInputStream fos1 = openFileInput("ENROLL");
                        byte[] buffer = new byte[fos1.available()];
                        fos1.read(buffer);
                        temp1 = new String(buffer);
                        fos1.close();
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                    String[] split = temp1.split(",");
                    Toast toast;
                    /*👻登录成功*/
                    if((name.equals(split[0]))&
                            (password.equals(split[1]))){
                        toast = Toast.makeText(Login.this, "👻登录成功！", Toast.LENGTH_LONG);
                        Intent intent = new Intent();
                        intent.setClass(Login.this,MainActivity.class);
                        intent.putExtra("status","login");
                        intent.putExtra("location",split[2]);
                        startActivity(intent);
                    }
                    /*登录失败*/
                    else {
                        toast = Toast.makeText(Login.this, "❌ 登录失败，账号或密码有误！", Toast.LENGTH_LONG);
                    }
                    toast.show();
                }
            }
        });
    }
    /*👻验证账号和密码的有效性*/
    public boolean verify_count(String name, String password, String cpassword ,String location){
        if((name.length() < 1)|(password.length() < 6)|(cpassword.length()<6|location.length()<1)){
            return false;
        }
        return password.equals(cpassword);
    }
}