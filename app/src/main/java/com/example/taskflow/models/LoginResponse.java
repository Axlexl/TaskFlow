package com.example.taskflow.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("token")
    private String token;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("username")
    private String username;

    public String getMessage() { return message; }
    public String getToken() { return token; }
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
}
