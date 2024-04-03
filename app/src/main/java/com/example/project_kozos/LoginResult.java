package com.example.project_kozos;

import com.google.gson.annotations.SerializedName;

public class LoginResult {
    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("errorMessage")
    private String errorMessage;

    public String getAccessToken() {
        return accessToken;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
