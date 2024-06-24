package com.example.flapflap;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flapflap.Adapter.PostAdapter;
import com.example.flapflap.javabean.Post;
import com.example.flapflap.retrofit.Constant;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommunityActivity extends AppCompatActivity {

    private ImageButton backButton;
    private Button btnAll, btnLatestPosts, btnLatestComments;
    private TextView communityNameTextView;
    private ImageView communityIconImageView;
    private List<Post> postList;
    private FloatingActionButton fabCreatePost;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        backButton = findViewById(R.id.back_button);
        btnAll = findViewById(R.id.btn_all);
        btnLatestPosts = findViewById(R.id.btn_latest_posts);
        btnLatestComments = findViewById(R.id.btn_latest_comments);
        communityNameTextView = findViewById(R.id.community_name);
        communityIconImageView = findViewById(R.id.community_icon);
        fabCreatePost = findViewById(R.id.fab_create_post);

        recyclerView = findViewById(R.id.post_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this, postList);
        recyclerView.setAdapter(postAdapter);

        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish(); // 返回上一页
                }
            });
        }

        TextView switchButton = findViewById(R.id.switch_btn);
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommunityActivity.this, AllCommunityActivity.class);
                startActivity(intent);
            }
        });

        ImageButton searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到搜索页面
                Intent intent = new Intent(CommunityActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        fabCreatePost.setOnClickListener(new View.OnClickListener() {
            int communityId = getIntent().getIntExtra("COMMUNITY_ID", -1);
            @Override
            public void onClick(View v) {
                // 跳转到发布帖子编辑页面
                Intent intent = new Intent(CommunityActivity.this, CreatePostActivity.class);
                intent.putExtra("COMMUNITY_ID", communityId);
                startActivity(intent);
            }
        });

        // 示例：点击事件示例
        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CommunityActivity.this, "显示全部帖子", Toast.LENGTH_SHORT).show();
                // 更新帖子列表显示全部帖子
            }
        });

        btnLatestPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CommunityActivity.this, "显示最新发帖", Toast.LENGTH_SHORT).show();
                // 更新帖子列表显示最新发帖
            }
        });

        btnLatestComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CommunityActivity.this, "显示最新评论", Toast.LENGTH_SHORT).show();
                // 更新帖子列表显示最新评论
            }
        });

        // 获取从上一个活动传递过来的社区 ID
        int communityId = getIntent().getIntExtra("COMMUNITY_ID", -1);
        if (communityId != -1) {
            fetchCommunityInfo(communityId);
            fetchPostList(communityId);
        }
    }

    private void fetchCommunityInfo(int communityId) {
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("id", String.valueOf(communityId))
                .build();

        Request request = new Request.Builder()
                .url(Constant.BASE_URL + "/server/community/getInfo")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Failed to fetch community info: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d(TAG, "Response: " + responseData);

                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        String gameName = jsonObject.getString("gameName");
                        String iconUrl = jsonObject.getString("icon");

                        // 在 UI 线程更新社区名称和图标
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                communityNameTextView.setText(gameName);
                                byte[] decodedString = Base64.decode(iconUrl, Base64.NO_WRAP);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                Glide.with(CommunityActivity.this)
                                        .load(bitmap)
                                        .into(communityIconImageView);
                            }
                        });

                        // 根据需要处理其他信息，比如帖子列表的加载等

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                    }

                } else {
                    Log.e(TAG, "Failed to fetch community info: " + response.code());
                }
            }
        });
    }

    public void fetchPostList(int communityId) {
        OkHttpClient client = new OkHttpClient();

        // 构建请求URL
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.BASE_URL + "/server/post/searchByCommunity").newBuilder();
        urlBuilder.addQueryParameter("id", String.valueOf(communityId)); // 传递社区ID作为查询参数
        String url = urlBuilder.build().toString();

        // 构建请求
        Request request = new Request.Builder()
                .url(url)
                .build();

        // 发起异步请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        List<Post> posts = parsePosts(responseBody); // 解析响应体中的 JSON 数据
                        if (posts != null) {
                            // 更新UI（在UI线程操作）
                            runOnUiThread(() -> {
                                postList.addAll(posts);
                                postAdapter.notifyDataSetChanged();
                            });
                        }
                    } else {
                        // 处理请求失败的情况
                        Log.e(TAG, "Request failed with status code: " + response.code());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception occurred during onResponse: ", e);
                } finally {
                    response.close(); // 确保关闭响应体以避免资源泄漏
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // 处理网络请求失败的情况
                Log.e(TAG, "Network request failed: ", e);
            }
        });
    }

    private List<Post> parsePosts(String responseBody) {
        Gson gson = new Gson();
        Type postListType = new TypeToken<List<Post>>(){}.getType();
        return gson.fromJson(responseBody, postListType);
    }
}


