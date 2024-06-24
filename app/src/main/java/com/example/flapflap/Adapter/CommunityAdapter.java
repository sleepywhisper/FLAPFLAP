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
import com.example.flapflap.CommunityActivity;
import com.example.flapflap.R;
import com.example.flapflap.javabean.Community;

import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {

    private Context context;
    private List<Community> communityList;

    public CommunityAdapter(Context context, List<Community> communityList) {
        this.context = context;
        this.communityList = communityList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_community, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Community community = communityList.get(position);
        holder.bind(community);

        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommunityActivity.class);
            intent.putExtra("COMMUNITY_ID", community.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return communityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView gameNameTextView,idTextView;
        ImageView iconImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            idTextView = itemView.findViewById(R.id.community_id);
            gameNameTextView = itemView.findViewById(R.id.community_name);
            iconImageView = itemView.findViewById(R.id.community_icon);
        }

        public void bind(Community community) {
            idTextView.setText(String.valueOf(community.getId()));
            gameNameTextView.setText(community.getGameName());
            byte[] decodedString = Base64.decode(community.getIcon(), Base64.NO_WRAP);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            // 使用 Glide 加载图标，需要添加相应的依赖
            Glide.with(context)
                    .load(bitmap)
                    .into(iconImageView);
        }
    }
}
