package com.example.flapflap.javabean;

public class Community {
    private int id;
    private String gameName;
    private String icon;

    private Integer postCount;
    public Community(int id, String gameName, String icon) {
        this.id = id;
        this.gameName = gameName;
        this.icon = icon;
    }

    public Integer getPostCount() {
        return postCount;
    }

    public int getId() {
        return id;
    }

    public String getGameName() {
        return gameName;
    }

    public String getIcon() {
        return icon;
    }
}
