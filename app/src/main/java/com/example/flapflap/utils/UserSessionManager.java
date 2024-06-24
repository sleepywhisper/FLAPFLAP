package com.example.flapflap.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSessionManager {
    // SharedPreferences 文件名
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    // 构造方法
    public UserSessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // 保存登录信息
    public void createLoginSession(String username, String userId) {
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_USER_ID, userId);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.commit();
    }

    // 检查登录状态
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // 获取用户名
    public String getUsername() {
        return pref.getString(KEY_USERNAME, null);
    }

    // 获取用户ID
    public String getUserId() {
        return pref.getString(KEY_USER_ID, null);
    }

    // 清除登录信息
    public void logoutUser() {
        editor.clear();
        editor.commit();
    }
}