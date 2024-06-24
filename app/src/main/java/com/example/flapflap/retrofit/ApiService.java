package com.example.flapflap.retrofit;

import com.example.flapflap.javabean.GameInfo;
import com.example.flapflap.javabean.Post;
import com.example.flapflap.javabean.User;
import com.example.flapflap.javabean.Notification;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/server/user/getUser")
    Call<Integer> getUser(@Query("name")String name);

    @POST("/server/user/getInfo")
    Call<User> getInfo(@Query("id")Integer id);

    @GET("getNickname/{id}")
    Call<String> getNickname(@Query("id")Integer id);

    @GET("getAvatar/{id}")
    Call<byte[]> getAvatar(@Query("id")Integer id);

    @GET("getGender/{id}")
    Call<String> getGender(@Query("id")Integer id);


    @GET("getSign/{id}")
    Call<String> getSign(@Query("id")Integer id);

    @POST("/server/user/changeNickname")
    Call<Boolean> changeNickname(@Body User user);

    @POST("/server/user/changeGender")
    Call<Boolean> changeGender(@Body User user);

    @POST("/server/user/changeBirth")
    Call<Boolean> changeBirth(@Body User user);

    @POST("/server/user/changeSign")
    Call<Boolean> changeSign(@Body User user);

    @POST("/server/user/changeAvatar")
    Call<Boolean> changeAvatar(@Body User user);

    @POST("/server/user/changePassword")
    Call<Boolean> changePassword(@Query("userName")String userName,@Query("oldPassword") String oldPassword,@Query("newPassword") String newPassword);

    @POST("/server/gameinfo/update")
    Call<Boolean> update(@Body GameInfo gameInfo);

    @POST("/server/gameinfo/getInfo")
    Call<GameInfo> getGameInfo(@Query("id")Integer id);

    @POST("/server/post/searchByUser")
    Call<List<Post>> getMyPost(@Query("id")Integer id);

    @POST("/server/notification/user")
    Call<List<Notification>> getMyNotificatons(@Query("userId") Integer userId);

    @GET("/server/gameinfo/getAllGames")
    Call<List<GameInfo>> getAllGames();
}