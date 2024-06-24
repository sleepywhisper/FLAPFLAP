package com.example.flapflap.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flapflap.PostDetailActivity;
import com.example.flapflap.R;
import com.example.flapflap.UserDetails;
import com.example.flapflap.javabean.Post;
import com.example.flapflap.myDetails;
import com.example.flapflap.utils.UserSessionManager;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;
    private Context context;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        String userAvatarBase64 = post.getUser().getAvatar();
        if (userAvatarBase64 != null && !userAvatarBase64.isEmpty()) {
            byte[] decodedString = Base64.decode(userAvatarBase64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.userAvatarImageView.setImageBitmap(decodedByte);
        } else {
            holder.userAvatarImageView.setImageResource(R.drawable.insert_picture_icon); // 设置默认头像
        }

        // 清空之前添加的图片
        holder.postImagesContainer.removeAllViews();

        // 处理帖子图片
        String[] postImagesBase64 = post.getImageUrls();
        if (postImagesBase64 != null && postImagesBase64.length > 0) {
            for (String imageBase64 : postImagesBase64) {
                if (imageBase64 != null && !imageBase64.isEmpty()) {
                    byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    ImageView imageView = new ImageView(context);
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    imageView.setImageBitmap(decodedByte);
                    holder.postImagesContainer.addView(imageView);
                }
            }
        }

        holder.userNameTextView.setText(post.getUser().getNickname());
        holder.postTimeTextView.setText(post.getPtime());
        holder.postTitleTextView.setText(post.getTitle());
        holder.postContentTextView.setText(post.getContent());
        holder.likesCountTextView.setText(String.valueOf(post.getLikes()));
        holder.commentsCountTextView.setText(String.valueOf(post.getCommentCount()));

        holder.userAvatarImageView.setOnClickListener(view -> {
            // 点击事件处理
            int userId = post.getPoster();
            UserSessionManager session = new UserSessionManager(context.getApplicationContext());
            // 跳转到帖子详情页面
            if(userId == Integer.parseInt(session.getUserId())){
                Intent intent = new Intent(view.getContext(), myDetails.class);
                view.getContext().startActivity(intent);
            } else {
                Intent intent = new Intent(view.getContext(), UserDetails.class);
                intent.putExtra("USER_ID", userId);
                view.getContext().startActivity(intent);
            }

        });
        holder.itemView.setOnClickListener(view -> {
            // 点击事件处理
            int communityId = post.getCommunityId();
            int postId = post.getId();

            // 跳转到帖子详情页面
            Intent intent = new Intent(view.getContext(), PostDetailActivity.class);
            intent.putExtra("COMMUNITY_ID", communityId);
            intent.putExtra("POST_ID", postId);
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatarImageView;
        TextView userNameTextView;
        TextView postTimeTextView;
        TextView postTitleTextView;
        TextView postContentTextView;
        ImageView likeButton;
        TextView likesCountTextView;
        ImageView commentButton;
        TextView commentsCountTextView;
        LinearLayout postImagesContainer;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatarImageView = itemView.findViewById(R.id.userAvatarImageView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            postTimeTextView = itemView.findViewById(R.id.postTimeTextView);
            postTitleTextView = itemView.findViewById(R.id.postTitleTextView);
            postContentTextView = itemView.findViewById(R.id.postContentTextView);
            likeButton = itemView.findViewById(R.id.likeButton);
            likesCountTextView = itemView.findViewById(R.id.likesCountTextView);
            commentButton = itemView.findViewById(R.id.commentButton);
            commentsCountTextView = itemView.findViewById(R.id.commentsCountTextView);
            postImagesContainer = itemView.findViewById(R.id.postImagesContainer);
        }
    }
}


