package com.example.flapflap; // 根据你的实际包名修改

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flapflap.R;
import com.example.flapflap.Adapter.GameAdapter;
import com.example.flapflap.javabean.GameInfo;
import com.example.flapflap.retrofit.Constant;
import com.example.flapflap_front.model.Game;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DiscoverActivity extends AppCompatActivity {

    private List<GameInfo> gameList;
    private OkHttpClient client;
    private GameAdapter gameAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        menu();

        // 初始化游戏数据
        initializeGameData();

        // 设置 RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        gameAdapter = new GameAdapter(gameList);
        recyclerView.setAdapter(gameAdapter);

        // 处理返回按钮点击事件
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        ImageButton searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(v -> {
            // 跳转到搜索页面
            Intent intent = new Intent(DiscoverActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        // 初始化 OkHttpClient
        client = new OkHttpClient();

        // 发送请求到后端
        getAllGames();
    }

    private void initializeGameData() {
        // 初始化游戏数据
        gameList = new ArrayList<>();
    }

    private void getAllGames() {
        // 创建请求
        Request request = new Request.Builder()
                .url(Constant.BASE_URL+"/server/gameinfo/getAllGames")
                .get()
                .build();

        // 异步发送请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 请求失败处理
                runOnUiThread(() -> Toast.makeText(DiscoverActivity.this, "Failed to fetch games", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // 请求成功处理
                    final String responseData = response.body().string();
                    runOnUiThread(() -> {
                        // 在这里处理服务器响应
                        //Toast.makeText(DiscoverActivity.this, "Games fetched successfully", Toast.LENGTH_SHORT).show();
                        parseAndDisplayGames(responseData);
                    });
                } else {
                    // 服务器返回错误处理
                    runOnUiThread(() -> Toast.makeText(DiscoverActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show());
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

    private void menu() {
        ImageButton backButton = findViewById(R.id.back_button);

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
                Toast.makeText(DiscoverActivity.this, "已在搜索页面", Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出菜单
                PopupMenu popupMenu = new PopupMenu(DiscoverActivity.this, menuButton);
                popupMenu.getMenuInflater().inflate(R.menu.nav_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.navigation_find) {
                            // 跳转到发现页面
                            Intent intent = new Intent(DiscoverActivity.this, DiscoverActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (itemId == R.id.navigation_community) {
                            // 跳转到社区页面
                            Intent intent = new Intent(DiscoverActivity.this, AllCommunityActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (itemId == R.id.navigation_me) {
                            // 跳转到我的页面
                            Intent intent = new Intent(DiscoverActivity.this, MeActivity.class);
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
