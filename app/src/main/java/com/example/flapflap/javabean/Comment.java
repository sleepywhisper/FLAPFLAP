package com.example.flapflap.javabean;

import java.util.List;

public class Comment {
    private String id;
    private int commenter;
    private int postId;
    private String content;
    private String timestamp;
    private int likes;
    private int replyCount;
    private User user;
    private List<Incomment> incomments;

    public Comment(String id, int commenter, int postId, String content, String timestamp, int likes, int replyCount, User user, List<Incomment> replies) {
        this.id = id;
        this.commenter = commenter;
        this.postId = postId;
        this.content = content;
        this.timestamp = timestamp;
        this.likes = likes;
        this.replyCount = replyCount;
        this.user = user;
        this.incomments = replies;
    }

    public Comment() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCommenter() {
        return commenter;
    }

    public void setCommenter(int commenter) {this.commenter = commenter; }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Incomment> getReplies() {
        return incomments;
    }

    public void setReplies(List<Incomment> replies) {
        this.incomments = replies;
    }
}

