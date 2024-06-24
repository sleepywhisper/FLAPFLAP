package com.example.flapflap.javabean;

import com.youth.banner.util.BannerLifecycleObserver;

import java.sql.Blob;

public class User {
    private int id;
    private String name;
    private String password;
    private String nickname;
    private String gender;
    private String birth;
    private String avatar;

    private String sign;

    public User() {

    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
        this.nickname = name;
        this.gender="UNKNOWN";
        this.birth = "1999-01-01";
        this.avatar = null;
        this.sign = "这个人什么都没有留下~";
    }

    public String getSign() {
        return sign;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public User(String name, String password, String avatar) {
        this.name = name;
        this.password = password;
        this.nickname = name;
        this.gender="UNKNOWN";
        this.birth = "1999-01-01";
        this.avatar = avatar;
        this.sign = "这个人什么都没有留下~";
    }

    public User(int id, String name, String password, String nickname, String gender, String birth, String avatar, String sign) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.nickname = nickname;
        this.gender = gender;
        this.birth = birth;
        this.avatar = avatar;
        this.sign = sign;
    }
}
