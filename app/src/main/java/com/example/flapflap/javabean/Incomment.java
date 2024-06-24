package com.example.flapflap.javabean;

import java.util.Date;

public class Incomment {
    private Integer id;

    private Integer commentId;

    private Integer commenter;

    private User user;

    private String content;

    private Integer likes;

    private String ctime;



    public Incomment() {

    }

    public Incomment(Integer id, Integer commentId, Integer commenter, User user, String content, Integer likes, String ctime) {
        this.id = id;
        this.commentId = commentId;
        this.commenter = commenter;
        this.user = user;
        this.content = content;
        this.likes = likes;
        this.ctime = ctime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public Integer getCommenter() {
        return commenter;
    }

    public void setCommenter(Integer commenter) {
        this.commenter = commenter;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }
}

