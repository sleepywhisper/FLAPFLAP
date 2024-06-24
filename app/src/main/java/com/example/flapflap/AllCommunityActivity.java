package com.example.flapflap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flapflap.Adapter.CommunityAdapter;
import com.example.flapflap.javabean.Community;
import com.example.flapflap.retrofit.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AllCommunityActivity extends AppCompatActivity {

    private static final String TAG = AllCommunityActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private CommunityAdapter adapter;
    private List<Community> communityList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_community);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        communityList = new ArrayList<>();
        adapter = new CommunityAdapter(this, communityList);
        recyclerView.setAdapter(adapter);

        fetchCommunityData();

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        ImageButton searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(v -> {
            // 跳转到搜索页面
            Intent intent = new Intent(AllCommunityActivity.this, SearchActivity.class);
            startActivity(intent);
        });
    }

    private void fetchCommunityData() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Constant.BASE_URL+"/server/community/getAllCommunities")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Failed to fetch community data: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d(TAG, "Response: " + responseData);

                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            String gameName = jsonObject.getString("gameName");
                            String icon = jsonObject.getString("icon");

                            Community community = new Community(id, gameName, icon);
                            communityList.add(community);
                        }

                        runOnUiThread(() -> adapter.notifyDataSetChanged());

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                    }

                } else {
                    Log.e(TAG, "Failed to fetch community data: " + response.code());
                }
            }
        });
    }
}
