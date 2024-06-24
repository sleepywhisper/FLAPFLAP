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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flapflap.R;
import com.example.flapflap.UserDetails;
import com.example.flapflap.javabean.Incomment;
import com.example.flapflap.javabean.User;

import java.util.List;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder> {
    private List<Incomment> replyList;
    private Context context;

    public ReplyAdapter(List<Incomment> replyList, Context context) {
        this.replyList = replyList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reply_comment, parent, false);
        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        Incomment reply = replyList.get(position);
        User user = reply.getUser();

        holder.replyUsernameTextView.setText(user.getNickname());
        holder.replyTimestampTextView.setText(reply.getCtime());
        holder.replyContentTextView.setText(reply.getContent());
        holder.replyLikesTextView.setText(String.valueOf(reply.getLikes()));

        byte[] decodedString = Base64.decode(user.getAvatar(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        // 加载用户头像
        Glide.with(context)
                .load(decodedByte)
                .placeholder(R.drawable.insert_picture_icon)
                .error(R.drawable.insert_picture_icon)
                .circleCrop()
                .into(holder.replyAvatarImageView);

        holder.replyAvatarImageView.setOnClickListener(v -> {
            int userId = reply.getCommenter(); // 示例值

            Intent intent = new Intent(v.getContext(), UserDetails.class);
            intent.putExtra("USER_ID", userId);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return replyList.size();
    }

    static class ReplyViewHolder extends RecyclerView.ViewHolder {
        ImageView replyAvatarImageView;
        TextView replyUsernameTextView;
        TextView replyContentTextView;
        TextView replyTimestampTextView;
        TextView replyLikesTextView;

        ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            replyAvatarImageView = itemView.findViewById(R.id.rep_comm_user_avatar);
            replyUsernameTextView = itemView.findViewById(R.id.rep_comm_user_name);
            replyContentTextView = itemView.findViewById(R.id.rep_comm_content);
            replyTimestampTextView = itemView.findViewById(R.id.rep_comm_time);
            replyLikesTextView = itemView.findViewById(R.id.rep_comm_likes);
        }
    }
}

