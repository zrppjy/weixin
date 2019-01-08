package com.example.zy.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Registered extends AppCompatActivity implements View.OnClickListener{
private EditText et_name,et_username,et_password;
    private Button btn_register;
    final OkHttpClient client = new OkHttpClient();
    private Gson gson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        et_name= (EditText) findViewById(R.id.et_name);
        et_username= (EditText) findViewById(R.id.et_username);
        et_password= (EditText) findViewById(R.id.et_password);
        btn_register= (Button) findViewById(R.id.btn2_register);
        btn_register.setOnClickListener(this);
    }
    private Handler handler= new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String ReturnMessage = (String) msg.obj;
                gson = new Gson();
                UserInfo.UserRegister userRegister = gson.fromJson(ReturnMessage, UserInfo.UserRegister.class);

                if (userRegister.getStatus().equals("注册成功")) {
                    Toast.makeText(Registered.this, "注册成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Registered.this, Login.class);
                    startActivity(intent);
                }else if(userRegister.getStatus().equals(userRegister.getStatus())){
                    Toast.makeText(Registered.this, "注册失败"+userRegister.getStatus(), Toast.LENGTH_SHORT).show();
                    return;
                }


            }


        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn2_register:
                Register();
            break;
        }
    }
    private void Register() {
        final String name = et_name.getText().toString().trim();
        final String username = et_username.getText().toString().trim();
        final String password = et_password.getText().toString().trim();
        //验证是否为空
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(Registered.this, "请输入帐号", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(Registered.this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }else if(TextUtils.isEmpty(name)){
            Toast.makeText(Registered.this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }

        postRequest(username, password,name);

    }

    //用户名 密码 昵称
    private void postRequest(String username, String password, String name) {

        //建立请求表单，添加上传服务器的参数
        RequestBody formBody = new FormBody.Builder()
                .add("name",name)
                .add("user", username)
                .add("password", password)
                .build();
        //发起请求
        final Request request = new Request.Builder()
                .url("http://123.207.85.214/chat/register.php")
                .post(formBody)
                .build();
        //新建一个线程，用于得到服务器响应的参数
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    //回调
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        //将服务器响应的参数response.body().string())发送到hanlder中，并更新ui
                        handler.obtainMessage(1, response.body().string()).sendToTarget();

                    } else {
                        throw new IOException("Unexpected code:" + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

}
