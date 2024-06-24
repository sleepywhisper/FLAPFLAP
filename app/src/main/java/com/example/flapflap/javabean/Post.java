package com.example.flapflap.javabean;

import okhttp3.RequestBody;

public class Post {
    private int id;
    private int communityId;
    private int poster;
    private User user;
    private String title;
    private String content;
    private String[] imageUrls;
    private int likes;
    private String ptime;
    private int commentCount;

    // 无参构造方法
    public Post() {}

    // 带参数的构造方法
    public Post(int id, int communityId, int poster, User user, String title, String content, String[] imageUrls, int likes, String ptime, int commentCount) {
        this.id = id;
        this.communityId = communityId;
        this.poster = poster;
        this.user = user;
        this.title = title;
        this.content = content;
        this.imageUrls = imageUrls;
        this.likes = likes;
        this.ptime = ptime;
        this.commentCount = commentCount;
    }

    // Getter 和 Setter 方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCommunityId() {
        return communityId;
    }

    public void setCommunityId(int communityId) {
        this.communityId = communityId;
    }

    public int getPoster() {
        return poster;
    }

    public void setPoster(int poster) {
        this.poster = poster;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String[] imageUrls) {
        this.imageUrls = imageUrls;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getPtime() {
        return ptime;
    }

    public void setPtime(String ptime) {
        this.ptime = ptime;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}
