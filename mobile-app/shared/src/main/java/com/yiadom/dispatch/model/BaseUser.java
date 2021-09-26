package com.yiadom.dispatch.model;

import androidx.annotation.Nullable;

public class BaseUser {
    public String id;
    @Nullable
    public String phoneNumber;
    @Nullable
    public String deviceToken;
    @Nullable
    public String avatar;
    @Nullable
    public String email;

    public BaseUser(String id, @Nullable String phoneNumber, @Nullable String deviceToken, @Nullable String avatar, @Nullable String email) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.deviceToken = deviceToken;
        this.avatar = avatar;
        this.email = email;
    }
}
