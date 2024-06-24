package com.example.flapflap.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flapflap.R;
import com.example.flapflap.javabean.Reply;
import com.example.flapflap.javabean.User;

import java.util.List;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder> {
    private List<Reply> replyList;
    private Context context;

    public ReplyAdapter(List<Reply> replyList, Context context) {
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
        Reply reply = replyList.get(position);
        User user = reply.getUser();

        holder.replyUsernameTextView.setText(user.getNickname());
        holder.replyTimestampTextView.setText(reply.getTimestamp());
        holder.replyContentTextView.setText(reply.getContent());
        holder.replyLikesTextView.setText(String.valueOf(reply.getLikes()));

        // 加载用户头像
        Glide.with(context)
                .load(user.getAvatar())
                .placeholder(R.drawable.insert_picture_icon)
                .error(R.drawable.insert_picture_icon)
                .circleCrop()
                .into(holder.replyAvatarImageView);
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

