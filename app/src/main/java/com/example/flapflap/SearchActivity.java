package com.example.flapflap; // 根据你的实际包名修改

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {

    private EditText searchEditText;
    private Button searchButton;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        menu();
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);

        // 设置搜索按钮点击事件
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchEditText.getText().toString();
                if (!query.isEmpty()) {
                    Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                    intent.putExtra("query", query); // 传递搜索查询到结果页面
                    startActivity(intent);
                } else {
                    searchEditText.setError("Please enter a game name");
                }
            }
        });
    }

    private void menu() {
        backButton = findViewById(R.id.backButton);

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
                Toast.makeText(SearchActivity.this, "已在搜索页面", Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出菜单
                PopupMenu popupMenu = new PopupMenu(SearchActivity.this, menuButton);
                popupMenu.getMenuInflater().inflate(R.menu.nav_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.navigation_find) {
                            // 跳转到发现页面
                            Intent intent = new Intent(SearchActivity.this, DiscoverActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (itemId == R.id.navigation_community) {
                            // 跳转到社区页面
                            Intent intent = new Intent(SearchActivity.this, AllCommunityActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (itemId == R.id.navigation_me) {
                            // 跳转到我的页面
                            Intent intent = new Intent(SearchActivity.this, MeActivity.class);
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

