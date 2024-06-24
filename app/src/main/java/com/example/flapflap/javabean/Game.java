package com.example.flapflap_front.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Game implements Parcelable {
    private int id;
    private int icon;
    private String name;
    private String version;
    private String fileSize;
    private String type;
    private int downloadCount;
    private String description;
    private List<String> imageUrls;

    public Game(int id, int icon, String name, String version, String fileSize,
                String type, int downloadCount, String description, List<String> imageUrls) {
        this.id = id;
        this.icon = icon;
        this.name = name;
        this.version = version;
        this.fileSize = fileSize;
        this.type = type;
        this.downloadCount = downloadCount;
        this.description = description;
        this.imageUrls = imageUrls;
    }

    public int getId() { return id; }
    public int getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getFileSize() {
        return fileSize;
    }

    public String getType() {
        return type;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    // Parcelable 接口方法实现
    protected Game(Parcel in) {
        icon = in.readInt();
        name = in.readString();
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(icon);
        dest.writeString(name);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Game> CREATOR = new Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };
}
