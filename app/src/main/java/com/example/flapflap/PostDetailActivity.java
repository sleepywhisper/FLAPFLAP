package com.example.flapflap;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flapflap.Adapter.CommentAdapter;
import com.example.flapflap.javabean.Comment;
import com.example.flapflap.javabean.Post;
import com.example.flapflap.javabean.Incomment;
import com.example.flapflap.javabean.User;
import com.example.flapflap.retrofit.Constant;
import com.example.flapflap.utils.UserSessionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
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

public class PostDetailActivity extends AppCompatActivity {
    private int communityId;
    private int postId;
    private int likes;
    private int reply_likes;
    private int commentCount;
    private CommentAdapter commentsAdapter;
    private List<Comment> commentList = new ArrayList<>();
    private TextView likesTextView;

    private RecyclerView recyclerViewComments;
    private EditText commentEditText;
    private Button sendCommentButton;
    private ImageButton delete;
    UserSessionManager session;
    Integer userId,poster;

    public PostDetailActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // 获取传递的社区ID和帖子ID
        communityId = getIntent().getIntExtra("COMMUNITY_ID", -1);
        postId = getIntent().getIntExtra("POST_ID", -1);
        session = new UserSessionManager(getApplicationContext());
        userId = Integer.parseInt(session.getUserId());
        delete = findViewById(R.id.delete);
        getPostDetail(postId, communityId);

        // 设置返回按钮的点击事件
        ImageView backButton = findViewById(R.id.btn_back);
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


        delete.setOnClickListener(view -> {
            final AlertDialog.Builder normalDialog =
                    new AlertDialog.Builder(PostDetailActivity.this);
            normalDialog.setMessage("确定要删除帖子吗?");
            normalDialog.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deletePost(postId);
                        }
                    });
            normalDialog.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //...To-do
                        }
                    });
            // 显示
            normalDialog.show();
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

        ImageView commentButton = findViewById(R.id.btn_comment);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 显示评论输入框
                commentEditText.requestFocus();
                String commentContent = commentEditText.getText().toString().trim();
                if (!commentContent.isEmpty()) {
                    addComment(postId, userId, commentContent); // 假设评论者的ID是1
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
        recyclerViewComments.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        loadComments(postId);
    }

    private void deletePost(int postId) {
        String url = Constant.BASE_URL + "/server/post/deletePost";

        // 构建请求体
        RequestBody requestBody = new FormBody.Builder()
                .add("id", String.valueOf(postId)) // 添加社区ID到FormData
                .build();

        // 创建请求
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // 处理错误
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                String res = "\"" + responseData + "\"";
                Log.e("rita", "resData: " + res);
                if ("\"true\"".equals(res)) {
                    runOnUiThread(() -> {
                        Toast.makeText(PostDetailActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(PostDetailActivity.this, "删除失败！", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void getPostDetail(int postId, int communityId) {
        String url = Constant.BASE_URL + "/server/post/postInfo";

        // 构建请求体
        RequestBody requestBody = new FormBody.Builder()
                .add("id", String.valueOf(postId)) // 添加社区ID到FormData
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
                        Gson gson = new GsonBuilder().create();
                        Post post = gson.fromJson(responseData, Post.class);
                        displayPostDetail(post);
                    });
                }
            }
        });
    }

    private void loadComments(int postId) {
        String url = Constant.BASE_URL + "/server/comment/searchByPost";
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("id", String.valueOf(postId))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
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
                    Gson gson = new GsonBuilder().create();
                    Type commentListType = new TypeToken<List<Comment>>() {}.getType();
                    List<Comment> comments = gson.fromJson(responseData, commentListType);
                    commentList.clear();
                    for (int i = 0; i < comments.size(); i++) {
//                            JSONObject jsonObject = jsonArray.getJSONObject(i);
//
//                            // 解析 user 对象
//                            JSONObject userObject = jsonObject.getJSONObject("user");
//                            User user = new User();
//                            user.setAvatar(userObject.getString("avatar"));
//                            user.setNickname(userObject.getString("nickname"));
//
//                            Comment comment = new Comment();
//                            comment.setUser(user);
//                            comment.setCommenter(jsonObject.getInt("commenter"));
//                            comment.setPostId(jsonObject.getInt("postId"));
//                            comment.setTimestamp(jsonObject.getString("ctime"));
//                            comment.setContent(jsonObject.getString("content"));
//                            comment.setLikes(jsonObject.getInt("likes"));
//
//                            JSONArray repliesArray = jsonObject.getJSONArray("incomments");
//                            List<Incomment> replies = new ArrayList<>();
//                            for (int j = 0; j < repliesArray.length(); j++) {
//                                JSONObject replyObject = repliesArray.getJSONObject(j);
//
//                                // 解析 user 对象
//                                JSONObject replyUserObject = replyObject.getJSONObject("user");
//                                User replyUser = new User();
//                                replyUser.setAvatar(replyUserObject.getString("avatar"));
//                                replyUser.setNickname(replyUserObject.getString("nickname"));
//
//                                Incomment reply = new Incomment();
//                                reply.setUser(replyUser);
//                                reply.setTimestamp(replyObject.getString("ctime"));
//                                reply.setContent(replyObject.getString("content"));
//                                reply.setLikes(replyObject.getInt("likes"));
//                                replies.add(reply);
//                            }
//                            comment.setReplies(replies);

                        commentList.add(comments.get(i));
                    }
                    runOnUiThread(() -> commentsAdapter.notifyDataSetChanged());
                }
            }
        });
    }

    private void displayPostDetail(Post post) {
        // 解析 JSON 并显示数据
        ImageView userAvatarImageView = findViewById(R.id.user_avatar);
        TextView usernameTextView = findViewById(R.id.username);
        TextView postTimeTextView = findViewById(R.id.post_time);
        TextView titleTextView = findViewById(R.id.post_title);
        TextView contentTextView = findViewById(R.id.post_content);
        TextView likesTextView = findViewById(R.id.like_count);
        TextView commentCountTextView = findViewById(R.id.comment_count);
        LinearLayout imagesContainer = findViewById(R.id.imagesContainer);

        if(post.getPoster() == userId){
            delete.setVisibility(View.VISIBLE);
        }else{
            delete.setVisibility(View.GONE);
        }
        String userAvatarBase64 = post.getUser().getAvatar();
        String username = post.getUser().getNickname();
        String postTime = post.getPtime();
        String title = post.getTitle();
        String content = post.getContent();
        likes = post.getLikes();
        commentCount = post.getCommentCount();
        String[] imagesArray = post.getImageUrls();

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
        if(imagesArray != null){
            imagesContainer.removeAllViews();
            for (int i = 0; i < imagesArray.length; i++) {
                String imageUrl = imagesArray[i];
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(8, 8, 8, 8);
                imageView.setLayoutParams(layoutParams);
                Picasso.get().load(imageUrl).into(imageView);
                imagesContainer.addView(imageView);
            }
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
        String url = Constant.BASE_URL+ "/server/comment/addComment";

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
}