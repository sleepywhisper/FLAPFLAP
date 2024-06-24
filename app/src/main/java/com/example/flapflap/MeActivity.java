package com.example.flapflap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.flapflap.javabean.LoginUser;
import com.example.flapflap.javabean.User;
import com.example.flapflap.retrofit.ApiService;
import com.example.flapflap.retrofit.Constant;
import com.example.flapflap.retrofit.GetUserRequest;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MeActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView avatar;
    private TextView nickname, id, sign;
    TextView mydetails;
    private View mysub,myalarm;
    private Retrofit retrofit;
    private ApiService apiService;
    private MYsqliteopenhelper mYsqliteopenhelper;
    private ImageButton backButton;

    Integer userId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_me);

        // 初始化视图
        find();
        menu();

        setSettingItem(findViewById(R.id.mygame), getResources().getDrawable(R.drawable.game),"我的游戏");
        setSettingItem(findViewById(R.id.mydown), getResources().getDrawable(R.drawable.download),"下载管理");
        setSettingItem(mysub, getResources().getDrawable(R.drawable.launch),"我的发布");
        setSettingItem(myalarm, getResources().getDrawable(R.drawable.announce),"我的消息");

        mysub.setOnClickListener(this);
        myalarm.setOnClickListener(this);
        mydetails.setOnClickListener(this);

        mYsqliteopenhelper = new MYsqliteopenhelper(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL) // 替换为你的后端地址
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();

        apiService = retrofit.create(ApiService.class);

        // 获取用户名
        String name = mYsqliteopenhelper.getName();
        Log.d("rita", "onCreate: " + name);
        if (name != null && !name.isEmpty()) {
            fetchUserIdAndInfo(name);
        } else {
            Log.e("Error", "Username is null or empty");
        }

        nickname.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        if(id == R.id.mydetails){
            Intent intent = new Intent(this, myDetails.class);
            intent.putExtra("key_int",userId);
            startActivity(intent);
        }else if(id == R.id.mysub){
            Intent intent = new Intent(this, PostActivity.class);
            intent.putExtra("key_int", userId);
            startActivity(intent);
        }else if(id == R.id.myalarm){
            Intent intent = new Intent(this, MessageActivity.class);
            intent.putExtra("key_int", userId);
            startActivity(intent);
        }
    }

    private void fetchUserIdAndInfo(String name) {
        Call<Integer> getUserCall = apiService.getUser(name);
        getUserCall.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userId = response.body();
                    Log.d("User ID", "User ID: " + userId);
                    fetchUserInfo(userId);
                } else {
                    Log.e("Error", "Failed to get user ID");
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.e("Error", "Network request failed", t);
            }
        });
    }

    private void fetchUserInfo(Integer userId) {
        Call<User> call = apiService.getInfo(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user1 = response.body();
                    Log.e("rita", "onCreate: " + user1);
                    runOnUiThread(() -> {
                        if (user1.getAvatar() != null && !user1.getAvatar().isEmpty()) {
                            byte[] decodedString = Base64.decode(user1.getAvatar(), Base64.NO_WRAP);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            avatar.setImageBitmap(bitmap);
                        } else {
                            Log.e("Error", "Avatar is null or empty");
                        }
                        nickname.setText(user1.getNickname());
                        id.setText(String.valueOf("id:" + user1.getId()));
                        sign.setText(user1.getSign());
                    });
                } else {
                    Log.e("Error", "Failed to get user info");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("Error", "Network request failed", t);
            }
        });
    }

    public void setSettingItem(View includeView,Drawable drawable, String name){
        ImageView icon = includeView.findViewById(R.id.item_icon);
        icon.setImageDrawable(drawable);
        TextView iname = includeView.findViewById(R.id.item_name);
        iname.setText(name);
    }
    private void find() {
        avatar = findViewById(R.id.me_avatar);
        nickname = findViewById(R.id.me_name);
        id = findViewById(R.id.me_id);
        sign = findViewById(R.id.me_sign);
        mydetails = findViewById(R.id.mydetails);
        mysub = findViewById(R.id.mysub);
        myalarm = findViewById(R.id.myalarm);
    }

    private void menu() {
        backButton = findViewById(R.id.back_button);

        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                finish(); // 返回上一页
            });
        }
        ImageButton searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到搜索页面
                Intent intent = new Intent(MeActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出菜单
                PopupMenu popupMenu = new PopupMenu(MeActivity.this, menuButton);
                popupMenu.getMenuInflater().inflate(R.menu.nav_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.navigation_find) {
                            // 跳转到发现页面
                            Intent intent = new Intent(MeActivity.this, DiscoverActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (itemId == R.id.navigation_community) {
                            // 跳转到社区页面
                            Intent intent = new Intent(MeActivity.this, AllCommunityActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (itemId == R.id.navigation_me) {
                            // 跳转到我的页面
                            Intent intent = new Intent(MeActivity.this, MeActivity.class);
                            startActivity(intent);
                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

}