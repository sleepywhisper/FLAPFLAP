package com.example.flapflap;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flapflap.javabean.LoginUser;
import com.example.flapflap.javabean.User;
import com.example.flapflap.retrofit.Constant;
import com.example.flapflap.utils.MD5;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button login,register;
    private EditText name,password;
    private LoginUser loginUser;
    private MYsqliteopenhelper mYsqliteopenhelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        mYsqliteopenhelper = new MYsqliteopenhelper(this);
        find();

        login.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    private void find(){
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        name = findViewById(R.id.name);
        password = findViewById(R.id.password);
    }

    @Override
    public void onClick(View v) {

        if(name == null || name.getText().toString().isEmpty()){
            Toast.makeText(this,"请输入用户名",Toast.LENGTH_SHORT).show();
        }else if(password == null || password.getText().toString().isEmpty()){
            Toast.makeText(this,"请输入密码",Toast.LENGTH_SHORT).show();
        }else{
            String s = name.getText().toString();
            String s1 = password.getText().toString();
            int id = v.getId();

            if (id == R.id.login) {
                loginUser = new LoginUser(s,s1);
                login(loginUser);
            }else if (id == R.id.register) {
                Drawable drawable = getResources().getDrawable(R.drawable.default_avatar);
                byte[] bytes = mYsqliteopenhelper.drawableToByteArray(drawable);
                String defaultAvatar = Base64.encodeToString(bytes, Base64.NO_WRAP);
                User user = new User(s, s1,defaultAvatar);
                register(user);
            }

        }




    }

    public void register(User user) {
        Log.d("name",name.getText().toString());
        Log.d("password",password.getText().toString());

        mYsqliteopenhelper.register(name.getText().toString(),password.getText().toString());
        new Thread(new Runnable() {
            OkHttpClient client=new OkHttpClient();
            @Override
            public void run() {
                MediaType JSON = MediaType.parse("application/json;charset=utf-8");
                JSONObject jsonObject = new JSONObject();
                OkHttpClient httpClient = new OkHttpClient();
                try {
                    jsonObject.put("name",user.getName());
                    jsonObject.put("password",MD5.digest(user.getPassword()));
                    jsonObject.put("nickname",user.getNickname());
                    jsonObject.put("gender",user.getGender());
                    jsonObject.put("birth",user.getBirth());
                    jsonObject.put("avatar", user.getAvatar());
                    jsonObject.put("sign",user.getSign());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody requestBody = RequestBody.create(JSON, String.valueOf(jsonObject));
                String url = Constant.BASE_URL + "/server/user/addUser";
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {}

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String jsondata = response.body().string();
                            registerResponse(jsondata);
                            response.body().close();
                        } else {
                            runOnUiThread(() -> Toast.makeText(MainActivity.this, "注册失败！ " + response.message(), Toast.LENGTH_SHORT).show());
                        }
                    }
                });
            }
        }).start();
    }

    public void getUser(View view) {
        OkHttpClient httpClient = new OkHttpClient();

        String url = Constant.BASE_URL + "/server/user/getAllUserName";
        Request getRequest = new Request.Builder()
                .url(url)
                .get()
                .build();

        Call call = httpClient.newCall(getRequest);

        new Thread(() -> {
            try {
                //同步请求，要放到子线程执行
                Response response = call.execute();
                Log.i("whq+getAllUserName", "okHttpGet run: response:"+ response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    public void login(LoginUser loginUser) {

        new Thread(() -> {

            OkHttpClient client=new OkHttpClient();
            //Form表单格式的参数传递
            MediaType JSON = MediaType.parse("application/json;charset=utf-8");
            JSONObject jsonObject = new JSONObject();
            OkHttpClient httpClient = new OkHttpClient();
            try {
                jsonObject.put("name",loginUser.getName());
                jsonObject.put("password",loginUser.getPassword());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody requestBody = RequestBody.create(JSON, String.valueOf(jsonObject));
            String url = Constant.BASE_URL + "/server/user/login";
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {}

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String jsondata = response.body().string();
                        // 解析
                        loginResponse(jsondata);
                        response.body().close();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "登录失败: " + response.message(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }).start();
}


private void loginResponse(String jsondata) {
    if (jsondata != null) {
        try {
            JSONObject jsonObject = new JSONObject(jsondata);
            String data = jsonObject.getString("message");
            Log.e("rita", "jsonJXData: " + data);
            if ("成功".equals(data)) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                    mYsqliteopenhelper.register(name.getText().toString(),password.getText().toString());
                });
            } else if(data.equals("失败")){
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "用户名或密码不正确", Toast.LENGTH_SHORT).show());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

private void registerResponse(String jsondata) {
    Log.e("rita", "jsonJXData: " + jsondata);
    if (jsondata != null) {
            String res = "\"" + jsondata + "\"";
            Log.e("rita", "resData: " + res);
            if ("\"true\"".equals(res)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                        mYsqliteopenhelper.register(name.getText().toString(),password.getText().toString());
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "注册失败！", Toast.LENGTH_SHORT).show();
                    }
                });
            }

    }
}

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}