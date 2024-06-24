package com.example.flapflap.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flapflap.PostDetailActivity;
import com.example.flapflap.R;
import com.example.flapflap.javabean.Notification;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Notification> notificationList; // 确保这是正确定义的

    public MessageAdapter(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.textMessage.setText(notification.getMessage());
        holder.textTime.setText(notification.getNtime());

        holder.itemView.setOnClickListener(view -> {
            // 点击事件处理
            int postId = notification.getPostId();
            // 跳转到帖子详情页面
            Intent intent = new Intent(view.getContext(), PostDetailActivity.class);
            intent.putExtra("POST_ID", postId);
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        TextView textTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.text_message);
            textTime = itemView.findViewById(R.id.text_time);
        }
    }
}
