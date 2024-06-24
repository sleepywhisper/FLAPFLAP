package com.example.flapflap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.example.flapflap.javabean.User;
import com.example.flapflap.retrofit.ApiService;
import com.example.flapflap.retrofit.Constant;
import com.example.flapflap.utils.UserSessionManager;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.flapflap.databinding.ActivityMyDetailsBinding;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class myDetails extends AppCompatActivity implements View.OnClickListener {
    private ImageView avatar;
    private TextView nickname, id, gender, birth, sign;
    private View mygame, mysub, changeInfo;
    private Retrofit retrofit;
    private ApiService apiService;
    private Integer userId;
    private ImageButton backButton;
    private UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_details);
        session = new UserSessionManager(getApplicationContext());
        int userId = Integer.parseInt(session.getUserId());
        find();
        menu();

        backButton = findViewById(R.id.back_button);

        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                finish(); // 返回上一页
            });
        }

        setSettingItem(mygame, getResources().getDrawable(R.drawable.game),"我的游戏");
        setSettingItem(mysub, getResources().getDrawable(R.drawable.launch),"我的发布");
        setSettingItem(changeInfo, getResources().getDrawable(R.drawable.setting),"编辑个人资料");

        changeInfo.setOnClickListener(this);
        mysub.setOnClickListener(this);

        retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL) // 替换为你的后端地址
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();

        apiService = retrofit.create(ApiService.class);

        // 获取用户名
        fetchUserInfo(userId);
    }


    private void find() {
        avatar = findViewById(R.id.my_avatar);
        nickname = findViewById(R.id.my_name);
        id = findViewById(R.id.my_id);
        gender = findViewById(R.id.my_gender);
        birth = findViewById(R.id.my_birth);
        sign = findViewById(R.id.my_sign);
        mygame = findViewById(R.id.mygame);
        mysub = findViewById(R.id.mysub);
        changeInfo = findViewById(R.id.changeInfo);
    }

    private void fetchUserInfo(Integer userId) {
        Call<User> call = apiService.getInfo(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user1 = response.body();
                    Log.e("rita", "onCreate: " + user1.getAvatar());
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
                        gender.setText("性别:" + user1.getGender());
                        birth.setText("生日:" + user1.getBirth());
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

    public void setSettingItem(View includeView, Drawable drawable, String name){
        ImageView icon = includeView.findViewById(R.id.item_icon);
        icon.setImageDrawable(drawable);
        TextView iname = includeView.findViewById(R.id.item_name);
        iname.setText(name);
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
                Intent intent = new Intent(myDetails.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出菜单
                PopupMenu popupMenu = new PopupMenu(myDetails.this, menuButton);
                popupMenu.getMenuInflater().inflate(R.menu.nav_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.navigation_find) {
                            // 跳转到发现页面
                            Intent intent = new Intent(myDetails.this, DiscoverActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (itemId == R.id.navigation_community) {
                            // 跳转到社区页面
                            Intent intent = new Intent(myDetails.this, AllCommunityActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (itemId == R.id.navigation_me) {
                            // 跳转到我的页面
                            Intent intent = new Intent(myDetails.this, MeActivity.class);
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.changeInfo){
            Intent intent = new Intent(this, changeInfo.class);
            startActivity(intent);
        }else if(id == R.id.mysub){
            Intent intent = new Intent(this, PostActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        }
    }
}