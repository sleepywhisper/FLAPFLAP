package com.example.flapflap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flapflap.Adapter.CommentAdapter;
import com.example.flapflap.javabean.Comment;
import com.example.flapflap.javabean.Reply;
import com.example.flapflap.javabean.User;
import com.example.flapflap.retrofit.ApiService;
import com.example.flapflap.retrofit.Constant;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostDetailActivity extends AppCompatActivity {
    private int communityId;
    private int postId;
    private int likes;
    private int reply_likes;
    private int commentCount;
    private CommentAdapter commentsAdapter;
    private List<Comment> commentList = new ArrayList<>();
    private TextView likesTextView;

    private ImageButton backButton;
    private TextView titleTextView;
    private ImageView userAvatarImageView;
    private TextView usernameTextView, postTimeTextView, postTitleTextView, postContentTextView;
    private ImageView postImageView;
    private ImageView likeButton, commentButton, reply_likeButton;
    private TextView likeCountTextView, commentCountTextView, reply_likeCount;
    private RecyclerView recyclerViewComments;
    private EditText commentEditText;
    private Button sendCommentButton;
    private Integer commenterId;
    private Retrofit retrofit;
    private ApiService apiService;
    private MYsqliteopenhelper mYsqliteopenhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // 获取传递的社区ID和帖子ID
        communityId = getIntent().getIntExtra("COMMUNITY_ID", -1);
        postId = getIntent().getIntExtra("POST_ID", -1);

        //获取用户ID
        retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL) // 替换为你的后端地址
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();

        apiService = retrofit.create(ApiService.class);
        mYsqliteopenhelper = new MYsqliteopenhelper(this);
        String name = mYsqliteopenhelper.getName();
        fetchUserIdAndInfo(name);

        getPostDetail(postId, communityId);

        // 设置返回按钮的点击事件
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 返回上一页
            }
        });

        ImageButton searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到搜索页面
                Intent intent = new Intent(PostDetailActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        likesTextView = findViewById(R.id.like_count);
        ImageView likeButton = findViewById(R.id.btn_like);
        // 示例：设置点赞按钮的点击事件
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点赞逻辑
                Toast.makeText(PostDetailActivity.this, "点赞成功", Toast.LENGTH_SHORT).show();
                // 更新点赞数
                likePost(postId);
            }
        });

        // 示例：设置评论按钮的点击事件
        commentEditText = findViewById(R.id.comment_edit_text);
        sendCommentButton = findViewById(R.id.send_comment_button);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 显示评论输入框
                commentEditText.requestFocus();
                String commentContent = commentEditText.getText().toString().trim();
                if (!commentContent.isEmpty()) {
                    addComment(postId, commenterId, commentContent); // 假设评论者的ID是1
                }
            }
        });

        sendCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 发送评论逻辑
                Toast.makeText(PostDetailActivity.this, "评论已发送", Toast.LENGTH_SHORT).show();
                // 清空输入框
                commentEditText.setText("");
                // 更新评论列表
            }
        });

        // 示例：设置评论列表的 RecyclerView
        recyclerViewComments = findViewById(R.id.comment_list);
        commentsAdapter = new CommentAdapter(commentList, this);
        recyclerViewComments.setAdapter(commentsAdapter);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));

        loadComments(postId);
    }

    private void getPostDetail(int postId, int communityId) {
        String url = Constant.BASE_URL +  "/server/post/postInfo?id=" + postId;

        // 构建请求体
        RequestBody requestBody = new FormBody.Builder()
                .add("id", String.valueOf(communityId)) // 添加社区ID到FormData
                .build();

        // 创建请求
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        // 发起请求
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // 处理错误
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    runOnUiThread(() -> {
                        // 解析并显示数据
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            displayPostDetail(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }


    private void loadComments(int postId) {
        String url = Constant.BASE_URL + "/server/comment/searchByPost?id=" + postId;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(PostDetailActivity.this, "获取评论失败", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        commentList.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            // 解析 user 对象
                            JSONObject userObject = jsonObject.getJSONObject("user");
                            User user = new User();
                            user.setAvatar(userObject.getString("avatar"));
                            user.setNickname(userObject.getString("nickname"));

                            Comment comment = new Comment();
                            comment.setUser(user);
                            comment.setCommenter(jsonObject.getInt("commenter"));
                            comment.setPostId(jsonObject.getInt("postId"));
                            comment.setTimestamp(jsonObject.getString("ctime"));
                            comment.setContent(jsonObject.getString("content"));
                            comment.setLikes(jsonObject.getInt("likes"));

                            JSONArray repliesArray = jsonObject.getJSONArray("incomments");
                            List<Reply> replies = new ArrayList<>();
                            for (int j = 0; j < repliesArray.length(); j++) {
                                JSONObject replyObject = repliesArray.getJSONObject(j);

                                // 解析 user 对象
                                JSONObject replyUserObject = replyObject.getJSONObject("user");
                                User replyUser = new User();
                                replyUser.setAvatar(replyUserObject.getString("avatar"));
                                replyUser.setNickname(replyUserObject.getString("nickname"));

                                Reply reply = new Reply();
                                reply.setUser(replyUser);
                                reply.setTimestamp(replyObject.getString("ctime"));
                                reply.setContent(replyObject.getString("content"));
                                reply.setLikes(replyObject.getInt("likes"));
                                replies.add(reply);
                            }
                            comment.setReplies(replies);

                            commentList.add(comment);
                        }
                        runOnUiThread(() -> commentsAdapter.notifyDataSetChanged());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void displayPostDetail(JSONObject postDetail) {
        // 解析 JSON 并显示数据
        ImageView userAvatarImageView = findViewById(R.id.user_avatar);
        TextView usernameTextView = findViewById(R.id.username);
        TextView postTimeTextView = findViewById(R.id.post_time);
        TextView titleTextView = findViewById(R.id.post_title);
        TextView contentTextView = findViewById(R.id.post_content);
        TextView likesTextView = findViewById(R.id.like_count);
        TextView commentCountTextView = findViewById(R.id.comment_count);
        LinearLayout imagesContainer = findViewById(R.id.imagesContainer);

        try {
            String userAvatarBase64 = postDetail.getString("userAvatar");
            String username = postDetail.getString("username");
            String postTime = postDetail.getString("postTime");
            String title = postDetail.getString("title");
            String content = postDetail.getString("content");
            likes = postDetail.getInt("likes");
            commentCount = postDetail.getInt("commentCount");
            JSONArray imagesArray = postDetail.getJSONArray("images");

            // Decode Base64 avatar
            byte[] decodedString = Base64.decode(userAvatarBase64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            userAvatarImageView.setImageBitmap(decodedByte);

            usernameTextView.setText(username);
            postTimeTextView.setText(postTime);
            titleTextView.setText(title);
            contentTextView.setText(content);
            likesTextView.setText(String.valueOf(likes));
            commentCountTextView.setText(String.valueOf(commentCount));

            // Load images
            imagesContainer.removeAllViews();
            for (int i = 0; i < imagesArray.length(); i++) {
                String imageUrl = imagesArray.getString(i);
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(8, 8, 8, 8);
                imageView.setLayoutParams(layoutParams);
                Picasso.get().load(imageUrl).into(imageView);
                imagesContainer.addView(imageView);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void likePost(int postId) {
        String url = Constant.BASE_URL + "/server/post/like";

        // 构建请求体
        RequestBody requestBody = new FormBody.Builder()
                .add("id", String.valueOf(postId))
                .build();

        // 创建请求
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        // 发起请求
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // 处理错误
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            boolean success = Boolean.parseBoolean(responseData.trim());
                            if (success) {
                                likes++;
                                likesTextView.setText(String.valueOf(likes));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }

    private void addComment(int postId, int commenterId, String content) {
        String url = Constant.BASE_URL + "/server/comment/addComment";

        // 构建JSON请求体
        JSONObject json = new JSONObject();
        try {
            json.put("postId", postId);
            json.put("commenter", commenterId);
            json.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 创建请求体
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"), json.toString());

        // 创建请求
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        // 发起请求
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // 处理错误
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    runOnUiThread(() -> {
                        if (Boolean.parseBoolean(responseData.trim())) {
                            // 评论成功，清空输入框内容
                            commentEditText.setText("");
                        }
                    });
                }
            }
        });
    }

    private void fetchUserIdAndInfo(String name) {
        retrofit2.Call<Integer> getUserCall = apiService.getUser(name);
        getUserCall.enqueue(new retrofit2.Callback<Integer>() {
            @Override
            public void onResponse(retrofit2.Call<Integer> call, retrofit2.Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    commenterId = response.body();
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
}