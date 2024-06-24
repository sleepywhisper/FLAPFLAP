package com.example.flapflap.retrofit;

public class GetUserRequest {
    private String name;

    public GetUserRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}