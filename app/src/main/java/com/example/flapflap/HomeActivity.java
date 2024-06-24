package com.example.flapflap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flapflap.Adapter.GameAdapter;
import com.example.flapflap.javabean.GameInfo;
import com.example.flapflap.retrofit.ApiService;
import com.example.flapflap.retrofit.Constant;
import com.example.flapflap.utils.BitmapUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private BottomNavigationView navView;
    private BitmapUtils bitmapUtils;
    private Retrofit retrofit;
    private ApiService apiService;
    private Banner banner;
    private Button button;

    private List<GameInfo> gameList;
    private OkHttpClient client;
    private HomeAdapter gameAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setHomeButtonEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);
        gameList = new ArrayList<>();
        banner = findViewById(R.id.banner);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        client = new OkHttpClient();
        gameAdapter = new HomeAdapter(gameList);
        recyclerView.setAdapter(gameAdapter);

        // 本地图片资源ID列表
        List<Integer> imageList = Arrays.asList(
                R.drawable.banner_1,
                R.drawable.banner_2,
                R.drawable.banner_3
        );

        // 设置 Banner
        banner.setAdapter(new BannerImageAdapter<Integer>(imageList) {
                    @Override
                    public void onBindView(BannerImageHolder holder, Integer data, int position, int size) {
                        // 使用 Glide 加载本地图片
                        Glide.with(holder.itemView)
                                .load(data)
                                .into(holder.imageView);
                    }
                })
                .addBannerLifecycleObserver(this) // 如果你希望在 Activity 或 Fragment 的生命周期内自动管理 banner
                .setIndicator(new CircleIndicator(this));


        ImageButton searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到搜索页面
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出菜单
                PopupMenu popupMenu = new PopupMenu(HomeActivity.this, menuButton);
                popupMenu.getMenuInflater().inflate(R.menu.nav_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.navigation_find) {
                            // 跳转到发现页面
                            Intent intent = new Intent(HomeActivity.this, DiscoverActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (itemId == R.id.navigation_community) {
                            // 跳转到社区页面
                            Intent intent = new Intent(HomeActivity.this, AllCommunityActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (itemId == R.id.navigation_me) {
                            // 跳转到我的页面
                            Intent intent = new Intent(HomeActivity.this, MeActivity.class);
                            startActivity(intent);
                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        Button moreButton = findViewById(R.id.more_button);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到发现页面
                Intent intent = new Intent(HomeActivity.this, DiscoverActivity.class);
                startActivity(intent);
            }
        });

        retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL) // 替换为你的后端地址
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();

        apiService = retrofit.create(ApiService.class);

        banner.setOnClickListener(this);
        getAllGames();
//        bitmapUtils = new BitmapUtils();
//        Drawable drawable = getResources().getDrawable(R.drawable.icon7);
//        byte[] bytes = bitmapUtils.drawableToByteArray(drawable);
//        String icon = Base64.encodeToString(bytes, Base64.NO_WRAP);
//        GameInfo gameInfo = new GameInfo(7, icon);
//        update(gameInfo);
    }

    private void update(GameInfo gameInfo) {
        Call<Boolean> call = apiService.update(gameInfo);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Boolean res = response.body();
                    Log.e("rita", "res" + res);
                    Toast.makeText(HomeActivity.this,"修改成功!",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomeActivity.this,"修改失败!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("Error", "Network request failed", t);
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.nav_menu,menu);  //格式(R.menu.你的menu名字,menu)
//        return super.onCreateOptionsMenu(menu);
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if(id == R.id.navigation_me){
//            Intent intent = new Intent(this, MeActivity.class);
//            startActivity(intent);
//        }else if(id == R.id.navigation_find){
//            Intent intent = new Intent(this, DiscoverActivity.class);
//            startActivity(intent);
//        }else if(id == R.id.navigation_community){
//            Intent intent = new Intent(this, CommunityActivity.class);
//            startActivity(intent);
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    class HomeAdapter extends GameAdapter {
        private List<GameInfo> gameList;

        public HomeAdapter(List<GameInfo> gameList) {
            super(gameList);
            this.gameList = gameList;  // 初始化gameList
        }

        @Override
        public int getItemCount() {
            // 返回限制后的数量，例如只显示前5项
            return Math.min(gameList.size(), 5);
        }
    }

    private void getAllGames() {
        // 创建请求
        Request request = new Request.Builder()
                .url(Constant.BASE_URL+"/server/gameinfo/getAllGames")
                .get()
                .build();

        // 异步发送请求
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                // 请求失败处理
                runOnUiThread(() -> Toast.makeText(HomeActivity.this, "Failed to fetch games", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    // 请求成功处理
                    final String responseData = response.body().string();
                    runOnUiThread(() -> {
                        // 在这里处理服务器响应
                        //Toast.makeText(HomeActivity.this, "Games fetched successfully", Toast.LENGTH_SHORT).show();
                        parseAndDisplayGames(responseData);
                    });
                } else {
                    // 服务器返回错误处理
                    runOnUiThread(() -> Toast.makeText(HomeActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void parseAndDisplayGames(String jsonData) {
        Gson gson = new Gson();
        Type gameListType = new TypeToken<List<GameInfo>>(){}.getType();
        List<GameInfo> games = gson.fromJson(jsonData, gameListType);

        // 更新游戏列表和通知适配器刷新数据
        gameList.clear();
        gameList.addAll(games);
        gameAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.banner){
            Intent intent = new Intent(this, GameDetails.class);
            intent.putExtra("key_int", 5);
            startActivity(intent);
        }
    }
}
