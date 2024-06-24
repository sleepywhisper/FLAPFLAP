package com.example.flapflap.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flapflap.R;
import com.example.flapflap.javabean.Comment;
import com.example.flapflap.javabean.User;
import com.example.flapflap.retrofit.Constant;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;
    private Context context;

    private Comment comment;

    public CommentAdapter(List<Comment> commentList, Context context) {
        this.commentList = commentList;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        comment = commentList.get(position);
        User user = comment.getUser();

        holder.usernameTextView.setText(user.getNickname());
        holder.timestampTextView.setText(formatTimestamp(comment.getTimestamp()));
        holder.contentTextView.setText(comment.getContent());
        holder.likesTextView.setText(String.valueOf(comment.getLikes()));
        holder.replyCountTextView.setText(String.valueOf(comment.getReplies().size()));

        // 加载用户头像
        Glide.with(context)
                .load(user.getAvatar())
                .placeholder(R.drawable.insert_picture_icon)
                .error(R.drawable.insert_picture_icon)
                .circleCrop()
                .into(holder.avatarImageView);

        // 处理楼中楼回复
        ReplyAdapter replyAdapter = new ReplyAdapter(comment.getReplies(), context);
        holder.repliesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.repliesRecyclerView.setAdapter(replyAdapter);

        holder.likeCommentButton.setOnClickListener(v -> {
            int postId = comment.getPostId(); // 示例值
            int commenter = comment.getCommenter(); // 示例值
            String content = comment.getContent(); // 示例值

            likeComment(postId, commenter, content, holder);
        });
    }

    private void likeComment(int postId, int commenter, String content, CommentViewHolder holder) {
        String url = Constant.BASE_URL +  "/server/comment/like?id=" + postId;

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        String json = "{\"postId\":" + postId + ",\"commenter\":" + commenter + ",\"content\":\"" + content + "\"}";
        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(holder.itemView.getContext(), "点赞失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                        comment.setLikes(comment.getLikes() + 1);
                        holder.likesTextView.setText(String.valueOf(comment.getLikes()));

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    private String formatTimestamp(String timestamp) {
        // TODO: 格式化时间戳为合适的格式
        return timestamp;
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImageView;
        TextView usernameTextView;
        TextView timestampTextView;
        TextView contentTextView;
        TextView likesTextView;
        TextView replyCountTextView;
        RecyclerView repliesRecyclerView;
        ImageView likeCommentButton;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.comm_user_avatar);
            usernameTextView = itemView.findViewById(R.id.comm_user_name);
            timestampTextView = itemView.findViewById(R.id.comm_time);
            contentTextView = itemView.findViewById(R.id.comm_content);
            likesTextView = itemView.findViewById(R.id.comm_likes);
            replyCountTextView = itemView.findViewById(R.id.reply_count);
            repliesRecyclerView = itemView.findViewById(R.id.replyRecyclerView);
            likeCommentButton = itemView.findViewById(R.id.comm_likeButton);
        }
    }
}

