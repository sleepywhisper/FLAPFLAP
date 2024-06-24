package com.example.flapflap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Printer;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.flapflap.javabean.GameInfo;
import com.example.flapflap.javabean.User;
import com.example.flapflap.retrofit.ApiService;
import com.example.flapflap.retrofit.Constant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GameDetails extends AppCompatActivity implements View.OnClickListener {
    private Banner banner;
    private Retrofit retrofit;
    private ApiService apiService;
    private ImageView icon;
    private TextView gameName, gameSize, gameVersion, gameProfile;
    private ExpandableTextView expandableTextView;
    private Button download,community;
    private ImageButton backButton;
    private String url;
    private int gameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_details);

        Intent intent = getIntent();
        gameId = intent.getIntExtra("gameId", 1);

        find();
        menu();

        banner = findViewById(R.id.gbanner);
        // 本地图片资源ID列表
//        List<Integer> imageList = Arrays.asList(
//                R.drawable.banner_1,
//                R.drawable.banner_2,
//                R.drawable.banner_3
//        );
//
//        // 设置 Banner
//        banner.setAdapter(new BannerImageAdapter<Integer>(imageList) {
//                    @Override
//                    public void onBindView(BannerImageHolder holder, Integer data, int position, int size) {
//                        // 使用 Glide 加载本地图片
//                        Glide.with(holder.itemView)
//                                .load(data)
//                                .into(holder.imageView);
//                    }
//                })
//                .addBannerLifecycleObserver(this) // 如果你希望在 Activity 或 Fragment 的生命周期内自动管理 banner
//                .setIndicator(new CircleIndicator(this));

        retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL) // 替换为你的后端地址
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();

        apiService = retrofit.create(ApiService.class);

        fetchGameInfo(gameId);

        download.setOnClickListener(this);
        community.setOnClickListener(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
                Intent intent = new Intent(GameDetails.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出菜单
                PopupMenu popupMenu = new PopupMenu(GameDetails.this, menuButton);
                popupMenu.getMenuInflater().inflate(R.menu.nav_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.navigation_find) {
                            // 跳转到发现页面
                            Intent intent = new Intent(GameDetails.this, DiscoverActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (itemId == R.id.navigation_community) {
                            // 跳转到社区页面
                            Intent intent = new Intent(GameDetails.this, AllCommunityActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (itemId == R.id.navigation_me) {
                            // 跳转到我的页面
                            Intent intent = new Intent(GameDetails.this, MeActivity.class);
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

    public void find() {
        gameName = findViewById(R.id.gameName);
        gameSize = findViewById(R.id.gameSize);
        gameVersion = findViewById(R.id.gameVersion);
        expandableTextView = findViewById(R.id.gameProfile);
        icon = findViewById(R.id.gameIcon);
        download = findViewById(R.id.game_download);
        community = findViewById(R.id.game_community);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.game_download){
            Uri webpage = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            startActivity(intent);
        } else if (id == R.id.game_community) {
            Intent intent = new Intent(GameDetails.this,CommunityActivity.class);
            intent.putExtra("COMMUNITY_ID",gameId);
            startActivity(intent);
        }
    }

    private void fetchGameInfo(Integer gameId) {
        Call<GameInfo> call = apiService.getGameInfo(gameId);
        call.enqueue(new Callback<GameInfo>() {
            @Override
            public void onResponse(Call<GameInfo> call, Response<GameInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GameInfo gameInfo = response.body();
                    Log.e("rita", "onCreate: " + gameInfo);
                    runOnUiThread(() -> {
                        if (gameInfo.getIcon() != null && !gameInfo.getIcon().isEmpty()) {
                            byte[] decodedString = Base64.decode(gameInfo.getIcon(), Base64.NO_WRAP);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            icon.setImageBitmap(bitmap);
                        } else {
                            Log.e("Error", "Icon is null or empty");
                        }
                        gameName.setText(gameInfo.getGameName());
                        gameSize.setText(gameInfo.getFileSize());
                        gameVersion.setText(gameInfo.getVersion());
                        expandableTextView.setText(gameInfo.getProfile());
                        url = gameInfo.getdLink();

                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<String>>() {}.getType();
                        List<String> imageUrls = gson.fromJson(gameInfo.getImageUrls(), listType);
                        Log.d("rita",imageUrls.toString());
                        banner.setAdapter(new BannerImageAdapter<String>(imageUrls) {
                                    @Override
                                    public void onBindView(BannerImageHolder holder, String data, int position, int size) {
                                        // 使用 Glide 加载本地图片
                                        Glide.with(holder.itemView)
                                                .load(data)
                                                .into(holder.imageView);
                                    }
                                })
                                .addBannerLifecycleObserver(GameDetails.this) // 如果你希望在 Activity 或 Fragment 的生命周期内自动管理 banner
                                .setIndicator(new CircleIndicator(GameDetails.this));
                    });
                } else {
                    Log.e("Error", "Failed to get user info");
                }
            }

            @Override
            public void onFailure(Call<GameInfo> call, Throwable t) {
                Log.e("Error", "Network request failed", t);
            }
        });
    }


}