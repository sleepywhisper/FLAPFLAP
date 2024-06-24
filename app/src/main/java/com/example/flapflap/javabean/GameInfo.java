package com.example.flapflap.javabean;

import org.json.JSONObject;

public class GameInfo {
    private Integer id;

    private String gameName;

    private String icon;

    private String profile;

    private String dLink;

    private String fileSize;

    private String version;

    private String imageUrls;

    public String getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String imageUrls) {
        this.imageUrls = imageUrls;
    }

    public GameInfo() {
    }

    public GameInfo(Integer id, String icon) {
        this.id = id;
        this.icon = icon;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getdLink() {
        return dLink;
    }

    public void setdLink(String dLink) {
        this.dLink = dLink;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
