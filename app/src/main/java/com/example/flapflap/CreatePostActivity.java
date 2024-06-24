package com.example.flapflap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flapflap.javabean.Post;
import com.example.flapflap.retrofit.ApiService;
import com.example.flapflap.retrofit.Constant;
import com.example.flapflap.utils.UserSessionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreatePostActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;

    private EditText postEditText,postTitleText;
    private int communityId;
    private String communityName;
    private UserSessionManager session;
    private int userId;
    private Retrofit retrofit;
    private ApiService apiService;
    private String[] imageUrls;
    private TextView community_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // 获取传递过来的社区 ID
        communityId = getIntent().getIntExtra("COMMUNITY_ID", -1);
        communityName = getIntent().getStringExtra("COMMUNITY_NAME");
        //获取用户ID
        retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL) // 替换为你的后端地址
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();

        apiService = retrofit.create(ApiService.class);
        session = new UserSessionManager(getApplicationContext());
        userId = Integer.parseInt(session.getUserId());

        // 初始化视图
        community_name = findViewById(R.id.community_name);
        postEditText = findViewById(R.id.edit_text_post);
        postTitleText = findViewById(R.id.edit_text_title);
        Button btnPublish = findViewById(R.id.btn_publish);
        ImageView insertPictureButton = findViewById(R.id.insert_picture_button);
        ImageView atFriendsButton = findViewById(R.id.at_friends_button);
        ImageView selectTopicButton = findViewById(R.id.select_topic_button);
        community_name.setText(communityName);
        // 设置返回按钮点击事件
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ImageButton searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到搜索页面
                Intent intent = new Intent(CreatePostActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        menu();
        // 设置发布按钮点击事件
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Post post = new Post();
                String postText = postEditText.getText().toString();
                String postTitle = postTitleText.toString();
                post.setCommunityId(communityId);
                post.setPoster(userId);
                if(postTitle != null && !postTitle.isEmpty())post.setTitle(postTitle);
                post.setContent(postText);
                addPost(post);
            }
        });

        // 设置插入图片按钮点击事件
        insertPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        // 设置艾特好友按钮点击事件
        atFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAtFriendsDialog();
            }
        });

        // 设置选择话题按钮点击事件
        selectTopicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectTopicDialog();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            // 处理选择的图片
            Uri selectedImage = data.getData();
            // 可以在这里把选中的图片插入到编辑框中
            //insertImageAtBottom(selectedImage);
        }
    }

    private void insertImageAtBottom(Uri imageUri) {
        try {
            // 获取图片的Bitmap
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            // 缩放图片以适应EditText
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);

            // 创建ImageSpan
            ImageSpan imageSpan = new ImageSpan(this, scaledBitmap);

            // 获取EditText的Spannable对象
            SpannableStringBuilder ssb = new SpannableStringBuilder(postEditText.getText());

            // 插入图片到EditText的末尾
            int start = ssb.length();
            ssb.insert(start, "\n\uFFFC"); // 插入占位符和换行符
            ssb.setSpan(imageSpan, start + 1, start + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // 设置Spannable对象到EditText
            postEditText.setText(ssb);
            postEditText.setSelection(ssb.length()); // 将光标移动到文本末尾
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openAtFriendsDialog() {
        // 打开一个对话框或新活动，让用户选择或输入要@的好友
    }

    private void openSelectTopicDialog() {
        // 打开一个对话框或新活动，让用户选择或输入话题
    }

    private void addPost(Post post) {
        String url = Constant.BASE_URL + "/server/post/addPost";

        OkHttpClient client = new OkHttpClient();

        // 创建请求体
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(post);

        // 创建请求体
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, json);
//        try {
//            requestBody.put("communityId", communityId);
//            requestBody.put("poster", userId);
//
//            requestBody.put("content", content);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        RequestBody body = RequestBody.create(mediaType, requestBody.toString());

        // 创建请求
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        // 发送请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                String res = "\"" + responseData + "\"";
                Log.e("rita", "resData: " + res);
                if ("\"true\"".equals(res)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CreatePostActivity.this, "帖子发布成功", Toast.LENGTH_SHORT).show();
                            // 帖子发布成功后的处理，如关闭页面或刷新帖子列表等
                            finish();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CreatePostActivity.this, "帖子发布失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CreatePostActivity.this, "网络错误，帖子发布失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void fetchUserIdAndInfo(String name) {
        retrofit2.Call<Integer> getUserCall = apiService.getUser(name);
        getUserCall.enqueue(new retrofit2.Callback<Integer>() {
            @Override
            public void onResponse(retrofit2.Call<Integer> call, retrofit2.Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userId = response.body();
                } else {
                    Log.e("Error", "Failed to get user ID");
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Integer> call, Throwable t) {
                Log.e("Error", "Network request failed", t);
            }
        });
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
                Intent intent = new Intent(CreatePostActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出菜单
                PopupMenu popupMenu = new PopupMenu(CreatePostActivity.this, menuButton);
                popupMenu.getMenuInflater().inflate(R.menu.nav_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.navigation_find) {
                            // 跳转到发现页面
                            Intent intent = new Intent(CreatePostActivity.this, DiscoverActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (itemId == R.id.navigation_community) {
                            // 跳转到社区页面
                            Intent intent = new Intent(CreatePostActivity.this, AllCommunityActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (itemId == R.id.navigation_me) {
                            // 跳转到我的页面
                            Intent intent = new Intent(CreatePostActivity.this, MeActivity.class);
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
