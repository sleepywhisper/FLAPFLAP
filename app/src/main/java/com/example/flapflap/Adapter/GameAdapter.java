package com.example.flapflap.Adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flapflap.GameDetails;
import com.example.flapflap.R;
import com.example.flapflap.javabean.GameInfo;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    private final List<GameInfo> gameList;

    public GameAdapter(List<GameInfo> gameList) {
        this.gameList = gameList;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        GameInfo game = gameList.get(position);
        byte[] decodedString = Base64.decode(game.getIcon(), Base64.NO_WRAP);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.gameIcon.setImageBitmap(bitmap);
        holder.gameName.setText(game.getGameName());
        holder.gameDescription.setText(game.getProfile());

        // 设置点击事件监听器
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, GameDetails.class);
            intent.putExtra("gameId", game.getId()); // 将选中的游戏对象传递到详情页面
            context.startActivity(intent);
        });

        holder.downloadButton.setOnClickListener(v -> {
            // 处理下载按钮点击事件
            Context context = v.getContext();
            Uri webpage = Uri.parse(game.getdLink());
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    static class GameViewHolder extends RecyclerView.ViewHolder {
        ImageView gameIcon;
        TextView gameName;
        TextView gameDescription;
        Button downloadButton;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            gameIcon = itemView.findViewById(R.id.game_icon);
            gameName = itemView.findViewById(R.id.game_name);
            gameDescription = itemView.findViewById(R.id.game_description);
            downloadButton = itemView.findViewById(R.id.download_button);
        }
    }
}

